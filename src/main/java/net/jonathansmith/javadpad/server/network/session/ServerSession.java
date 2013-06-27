/*
 * Copyright (C) 2013 Jon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.jonathansmith.javadpad.server.network.session;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.channel.Channel;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.database.DatabaseRecord;
import net.jonathansmith.javadpad.common.database.PluginRecord;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.database.RecordsTransform;
import net.jonathansmith.javadpad.common.database.records.Experiment;
import net.jonathansmith.javadpad.common.database.records.User;
import net.jonathansmith.javadpad.common.events.sessiondata.DataArriveEvent;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.packet.auth.EncryptedSessionKeyPacket;
import net.jonathansmith.javadpad.common.network.packet.auth.EncryptionKeyRequestPacket;
import net.jonathansmith.javadpad.common.network.packet.auth.EncryptionKeyResponsePacket;
import net.jonathansmith.javadpad.common.network.packet.auth.HandshakePacket;
import net.jonathansmith.javadpad.common.network.packet.database.DataPacket;
import net.jonathansmith.javadpad.common.network.packet.database.DataUpdatePacket;
import net.jonathansmith.javadpad.common.network.packet.plugins.PluginStatusPacket;
import net.jonathansmith.javadpad.common.network.packet.plugins.PluginTransferPacket;
import net.jonathansmith.javadpad.common.network.packet.session.DisconnectPacket;
import net.jonathansmith.javadpad.common.network.packet.session.SetSessionDataPacket;
import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.common.network.session.SessionData;
import net.jonathansmith.javadpad.common.plugins.PluginManager;
import net.jonathansmith.javadpad.common.security.SecurityHandler;
import net.jonathansmith.javadpad.common.util.database.RecordsList;
import net.jonathansmith.javadpad.server.Server;
import net.jonathansmith.javadpad.server.database.connection.DatabaseConnection;
import net.jonathansmith.javadpad.server.database.recordsaccess.experiment.ExperimentManager;
import net.jonathansmith.javadpad.server.database.recordsaccess.user.UserManager;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 *
 * @author Jon
 */
public final class ServerSession extends Session {
    
    private final AtomicBoolean shaLock = new AtomicBoolean(false);
    private final DatabaseConnection connection;
    
    private byte[] token;
    private String hash;
    private boolean disconnectExpected = false;
    private boolean pluginExpected = false;
    
    public ServerSession(Engine eng, Channel channel) {
        super(eng, channel);
        this.incoming = new IncomingServerNetworkThread(eng, this, this.getSessionID());
        this.outgoing = new OutgoingServerNetworkThread(eng, this, this.getSessionID());
        this.start();
        this.setServerKey(this.getSessionID());
        connection = ((Server) this.engine).getSessionConnection();
    }
    
    @Override
    public void handleHandshake(HandshakePacket p) {
        if (this.getState() != NetworkThreadState.EXCHANGING_HANDSHAKE) {
            this.engine.warn("Invalid handshake packet received. Discarding.");
            this.disconnect(true);
            return;
        }
        
        if (!this.engine.getVersion().contentEquals(p.version)) {
            this.engine.warn("Incompatible client/server versions. Client has: " + p.version + ", Server has: " + this.engine.getVersion());
            this.disconnect(true);
            return;
        }
        
        byte[] randomByte = new byte[4];
        p.random.nextBytes(randomByte);
        this.setVerifyToken(randomByte);
        
        AsymmetricCipherKeyPair keys = SecurityHandler.getInstance().getKeyPair();
        byte[] secret = SecurityHandler.getInstance().encodeKey(keys.getPublic());
        
        EncryptionKeyRequestPacket reply = new EncryptionKeyRequestPacket(this.engine, this, secret, randomByte);
        this.handleEncryptionKeyRequest(reply);
    }
    
    @Override
    public void handleEncryptionKeyRequest(EncryptionKeyRequestPacket p) {
        this.addPacketToSend(PacketPriority.CRITICAL, p);
        this.incrementState();
    }
    
    @Override
    public void handleEncryptionKeyResponse(EncryptionKeyResponsePacket p, boolean isReply) {
        if (this.getState() != NetworkThreadState.EXCHANGING_AUTHENTICATION) {
            this.engine.warn("Cannot respond to an authentication challenge, as either the handshake is not complete or we have not sent the token");
            this.disconnect(true);
            return;
        }
        
        AsymmetricCipherKeyPair pair = SecurityHandler.getInstance().getKeyPair();
        AsymmetricBlockCipher cipher = SecurityHandler.getInstance().getAsymmetricCipher();
        cipher.init(SecurityHandler.DECRYPT_MODE, pair.getPrivate());
        
        final byte[] initialVector = SecurityHandler.getInstance().processAll(cipher, p.keys);
        final byte[] validateToken = SecurityHandler.getInstance().processAll(cipher, p.token);
        final byte[] savedToken = this.getVerifyToken();
        
        if (validateToken.length != 4) {
            this.engine.warn("Invalid token from session");
            this.disconnect(true);
            return;
        }
        
        for (int i = 0; i < validateToken.length; i++) {
            if (validateToken[i] != savedToken[i]) {
                this.engine.warn("Invalid token from session");
                this.disconnect(true);
                return;
            }
        }
        
        byte[] publicKeyEncoded = SecurityHandler.getInstance().encodeKey(pair.getPublic());
        this.sha1Hash(new Object[] {initialVector, publicKeyEncoded});
        
        CipherParameters symmetricKey = new ParametersWithIV(new KeyParameter(initialVector), initialVector);
        
        CFBBlockCipher toClientCipher = SecurityHandler.getInstance().getSymmetricCipher();
        toClientCipher.init(SecurityHandler.ENCRYPT_MODE, symmetricKey);
        //CommonEncoder encoder = this.session.channel.getPipeline().get(CommonEncoder.class);
        //encoder.setEncryption(toClientCipher);
        
        CFBBlockCipher fromClientCipher = SecurityHandler.getInstance().getSymmetricCipher();
        fromClientCipher.init(SecurityHandler.DECRYPT_MODE, symmetricKey);
        //CommonDecoder decoder = this.session.channel.getPipeline().get(CommonDecoder.class);
        //decoder.setDecryption(fromClientCipher);
        
        EncryptionKeyResponsePacket reply = new EncryptionKeyResponsePacket(this.engine, this, new byte[1], new byte[1]);
        this.addPacketToSend(PacketPriority.CRITICAL, reply);
        this.incrementState();
        
        EncryptedSessionKeyPacket p2 = new EncryptedSessionKeyPacket(this.engine, this);
        this.handleSessionKey(p2);
    }
    
    @Override
    public void handleSessionKey(EncryptedSessionKeyPacket p) {
        this.lockAndSendPacket(PacketPriority.CRITICAL, p);
    }
    
    public byte[] getVerifyToken() {
        return this.token;
    }
    
    private void setVerifyToken(byte[] token) {
        this.token = token;
    }

    public void setSha1Hash(String sha1Hash) {
        this.hash = sha1Hash;
    }
    
    public String getSha1Hash() {
        return this.hash;
    }

    // Session data
    @Override
    public void addData(String key, SessionData dataType, RecordsList<Record> data) {
        if (this.addSessionData(key, dataType, data)) {
            this.fireChange(new DataArriveEvent(dataType));
        }
    }

    @Override
    public void updateData(String key, SessionData dataType, RecordsTransform data) {
        RecordsList<Record> oldData = this.checkoutSessionData(dataType);
        if (oldData == null) {
            return;
        }
        
        RecordsList<Record> dataUpdate = data.transform(oldData);
        if (this.addSessionData(this.getSessionID(), dataType, dataUpdate)) {
            RecordsTransform transform = RecordsTransform.getTransform(oldData, dataUpdate);
            LockedPacket p = new DataUpdatePacket(this.engine, this, dataType, transform);
            this.lockAndSendPacket(PacketPriority.MEDIUM, p);
        }
    }

    @Override
    public RecordsList<Record> checkoutData(SessionData dataType) {
        RecordsList<Record> oldData = this.checkoutSessionData(dataType);
        RecordsList<Record> dataUpdate = this.requestData(dataType);
        if (this.addSessionData(this.getSessionID(), dataType, dataUpdate)) {
            if (oldData != null) {
                RecordsTransform transform = RecordsTransform.getTransform(oldData, dataUpdate);
                if (transform != null) {
                    LockedPacket p = new DataUpdatePacket(this.engine, this, dataType, transform);
                    this.lockAndSendPacket(PacketPriority.MEDIUM, p);
                }
            }
            
            else {
                LockedPacket p = new DataPacket(this.engine, this, dataType, dataUpdate);
                this.lockAndSendPacket(PacketPriority.MEDIUM, p);
            }
        }
        
        return dataUpdate;
    }
    
    @Override
    public void setKeySessionData(String key, DatabaseRecord type, Record data) {
        if (!key.contentEquals(this.getSessionID())) {
            return;
        }
        
        switch (type) {
            case USER:
                if (!(data instanceof User)) {
                    return;
                }
                
                this.setUser((User) data);
                break;
                
            case EXPERIMENT:
                if (!(data instanceof Experiment)) {
                    return;
                }
                
                this.setExperiment((Experiment) data);
                break;
                
            case PLUGIN:
                if (!(data instanceof PluginRecord)) {
                    return;
                }
                
                PluginRecord plugin = (PluginRecord) data;
                PluginManager manager = this.engine.getPluginManager();
                PluginRecord local = manager.getLocalPluginRecord(plugin.getName());
                if (local == null) {
                    LockedPacket p = new SetSessionDataPacket(this.engine, this, DatabaseRecord.PLUGIN, null);
                    this.lockAndSendPacket(PacketPriority.MEDIUM, p);
                }
                
                else {
                    switch(manager.compareVersions(local, plugin)) {
                        // Local is newer
                        case -1:
                            LockedPacket p = new PluginStatusPacket(this.engine, this, -1);
                            this.lockAndSendPacket(PacketPriority.MEDIUM, p);
                            
                            this.setPlugin(local);
                            
                            String pluginPath = this.engine.getPluginManager().getPluginPath(local.getName());
                            LockedPacket p2 = new PluginTransferPacket(this.engine, this, local, pluginPath);
                            this.lockAndSendPacket(PacketPriority.MEDIUM, p2);
                            break;
                            
                        // Same
                        case 0:
                            LockedPacket p3 = new PluginStatusPacket(this.engine, this, 0);
                            this.lockAndSendPacket(PacketPriority.MEDIUM, p3);
                            
                            this.setPlugin(local);
                            break;
                            
                        // Client is newer
                        case 1:
                            LockedPacket p4 = new PluginStatusPacket(this.engine, this, 1);
                            this.lockAndSendPacket(PacketPriority.MEDIUM, p4);
                            
                            this.setPlugin(plugin);
                            break;
                            
                        default:
                    }
                }
        }
    }
    
    @Override
    public void setUser(User user) {
        super.setUser(user);
        LockedPacket p = new SetSessionDataPacket(this.engine, this, DatabaseRecord.USER, (Record) user);
        this.lockAndSendPacket(PacketPriority.HIGH, p);
    }
    
    @Override
    public void setExperiment(Experiment experiment) {
        super.setExperiment(experiment);
        LockedPacket p = new SetSessionDataPacket(this.engine, this, DatabaseRecord.EXPERIMENT, (Record) experiment);
        this.lockAndSendPacket(PacketPriority.HIGH, p);
    }
    
    public void setPlugin(PluginRecord plugin) {
        RecordsList<Record> plugins = new RecordsList<Record> ();
        plugins.add(plugin);
        this.addData(this.getSessionID(), SessionData.PLUGIN, plugins);
        LockedPacket p = new SetSessionDataPacket(this.engine, this, DatabaseRecord.PLUGIN, (Record) plugin);
        this.lockAndSendPacket(PacketPriority.LOW, p);
    }

    // Database
    public RecordsList<Record> requestData(SessionData dataType) {
        switch (dataType) {
            case ALL_USERS:
                return UserManager.getInstance().loadAll(this.connection);
                
            case USER_EXPERIMENTS:
                User user = this.getUser();
                if (user != null) {
                    Set<Experiment> experiments = user.getExperiments();
                    
                    if (!experiments.isEmpty()) {
                        RecordsList<Record> out = new RecordsList<Record> ();
                        for (Experiment exp : experiments) {
                            out.add(exp);
                        }
                        
                        return out;
                    }
                }
                
                return new RecordsList<Record> ();
                
            case ALL_PLUGINS:
                return this.engine.getPluginManager().getLocalPluginRecordList();
                
            default:
                return null;
        }
    }
    
    public void submitNewRecord(DatabaseRecord type, Record record) {
        switch (type) {
            case USER:
                if (!(record instanceof User)) {
                    return;
                }
                
                UserManager.getInstance().saveNew(this.connection, (User) record);
                this.setKeySessionData(this.getSessionID(), DatabaseRecord.USER, record);
                break;
                
            case EXPERIMENT:
                if (!(record instanceof Experiment)) {
                    return;
                }
                
                ExperimentManager.getInstance().saveNew(this.connection, (Experiment) record);
                this.setKeySessionData(this.getSessionID(), DatabaseRecord.EXPERIMENT, record);
                
                User user = this.getUser();
                user.addExperiment((Experiment) record);
                UserManager.getInstance().save(this.connection, user);
                break;
        }
    }
    
    // Runtime
    /*
     * Used to disconnect the client without affecting the server. Needs to be
     * available
     */
    @Override
    public void disconnect(boolean force) {
        if (!this.disconnectExpected) {
            LockedPacket p = new DisconnectPacket(this.engine, this, force);
            this.lockAndSendPacket(PacketPriority.CRITICAL, p);
        }
        
        this.incoming.shutdown(force);
        this.outgoing.shutdown(force);
        
        this.connection.closeConnection();
        if (this.channel.isConnected()) {
            this.channel.disconnect();
        }
    }
    
    public void setDisconnectExpected() {
        this.disconnectExpected = true;
    }
    
    public void sha1Hash(Object[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.reset();
            md.update(this.getSessionID().getBytes("ISO_8859_1"));
            
            for (Object o : input) {
                if (o instanceof String) {
                    md.update(((String) o).getBytes("ISO_8859_1"));
                }
                
                else if (o instanceof byte[]) {
                    md.update((byte[]) o);
                }
                
                else {
                    this.setHash(null);
                }
            }
            
            BigInteger bigInt = new BigInteger(md.digest());
            
            if (bigInt.compareTo(BigInteger.ZERO) < 0) {
                bigInt = bigInt.negate();
                this.setHash("-" + bigInt.toString(16));
            }
            
            else {
                this.setHash(bigInt.toString(16));
            }
        }
        
        catch (Exception ex) {
            this.engine.error("Error in sha1 digest", ex);
            this.setHash(null);
        }
    }
    
    private void setHash(String hash) {
        if (this.shaLock.compareAndSet(false, true)) {
            this.hash = hash;
        }
    }
}
