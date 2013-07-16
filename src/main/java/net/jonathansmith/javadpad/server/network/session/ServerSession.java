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

import java.math.BigInteger;

import java.security.MessageDigest;

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
import net.jonathansmith.javadpad.common.database.records.LoaderPluginRecord;
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
import net.jonathansmith.javadpad.common.network.packet.plugins.PluginTransferPacket;
import net.jonathansmith.javadpad.common.network.packet.plugins.PluginUploadRequestPacket;
import net.jonathansmith.javadpad.common.network.packet.session.DisconnectPacket;
import net.jonathansmith.javadpad.common.network.packet.session.SetSessionDataPacket;
import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.common.network.session.SessionData;
import net.jonathansmith.javadpad.common.plugins.PluginManagerHandler;
import net.jonathansmith.javadpad.common.security.SecurityHandler;
import net.jonathansmith.javadpad.common.util.database.RecordsList;
import net.jonathansmith.javadpad.server.Server;
import net.jonathansmith.javadpad.server.database.connection.DatabaseConnection;
import net.jonathansmith.javadpad.server.database.recordaccess.GenericManager;
import net.jonathansmith.javadpad.server.database.recordaccess.QueryType;
import net.jonathansmith.javadpad.server.database.recordaccess.loaderplugin.LoaderPluginManager;
import net.jonathansmith.javadpad.server.database.recordaccess.user.UserManager;

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
        this.connection = ((Server) this.engine).getSessionConnection();
        
        super.setSessionData(this.getSessionID(), SessionData.ALL_LOADER_PLUGINS, this.engine.getPluginManager().getLoaderPluginRecordList());
        super.setSessionData(this.getSessionID(), SessionData.ALL_ANALYSER_PLUGINS, this.engine.getPluginManager().getAnalyserPluginRecordList());
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
    
    private void sha1Hash(Object[] input) {
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
    
//    
//    public void setPlugin(PluginRecord plugin) {
//        Dataset data = this.getCurrentData();
//        if (data != null) {
//            data.setPluginInfo(plugin);
//            
//            if (data instanceof LoaderDataset) {
//                LoaderDataManager.getInstance().save(this.connection, (LoaderDataset) data);
//            }
//            
//            else {
//                // TODO:
//            }
//        }
//        
//        LockedPacket p = new SetSessionDataPacket(this.engine, this, DatabaseRecord.CURRENT_DATASET, (Record) data);
//        this.lockAndSendPacket(PacketPriority.MEDIUM, p);
//    }
    
    /**
     * Used to disconnect the client without affecting the server. Needs to be
     * available
     * 
     * @param force 
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
    
    public void handleUploadPluginRequest(boolean toServer, boolean sessionSet, PluginRecord plugin) {
        if (!toServer) {
            String pluginPath = this.engine.getPluginManager().getPluginPath(plugin.getName());
            LockedPacket p2 = new PluginTransferPacket(this.engine, this, plugin, pluginPath);
            this.lockAndSendPacket(PacketPriority.MEDIUM, p2);
            return;
        }
        
        
        PluginManagerHandler manager = this.engine.getPluginManager();
        PluginRecord local = manager.getPluginRecord(plugin.getName());
        PluginRecord decision;
        
        if (local == null) {
            LockedPacket p = new PluginUploadRequestPacket(this.engine, this, (byte) 1, null, true);
            this.lockAndSendPacket(PacketPriority.MEDIUM, p);

            decision = plugin;

            if (plugin instanceof LoaderPluginRecord) {
                LoaderPluginManager.getInstance().saveNew(this.connection, (LoaderPluginRecord) plugin);
            }

            else {
                // TODO:
            }
        }
        
        else {
            byte status = manager.compareVersions(local, plugin);
            switch (status) {
                case -1:
                    LockedPacket p = new PluginUploadRequestPacket(this.engine, this, (byte) -1, null, true);
                    this.lockAndSendPacket(PacketPriority.MEDIUM, p);

                    decision = local;

                    String pluginPath = this.engine.getPluginManager().getPluginPath(local.getName());
                    LockedPacket p2 = new PluginTransferPacket(this.engine, this, local, pluginPath);
                    this.lockAndSendPacket(PacketPriority.MEDIUM, p2);
                    break;

                case 0:
                    LockedPacket p3 = new PluginUploadRequestPacket(this.engine, this, (byte) 0, null, true);
                    this.lockAndSendPacket(PacketPriority.MEDIUM, p3);

                    decision = local;
                    break;

                case 1:
                    LockedPacket p4 = new PluginUploadRequestPacket(this.engine, this, (byte) 1, null, true);
                    this.lockAndSendPacket(PacketPriority.MEDIUM, p4);

                    decision = plugin;

                    if (plugin instanceof LoaderPluginRecord) {
                        LoaderPluginRecord current = LoaderPluginManager.getInstance().findPluginByName(this.connection, plugin.getName());
                        plugin.setUUID(current.getUUID());
                        LoaderPluginManager.getInstance().save(this.connection, (LoaderPluginRecord) plugin);
                    }

                    else {
                        // TODO:
                    }
                    break;

                default:
                    return;
            }
        }
        
        if (sessionSet) {
            RecordsList<Record> list = new RecordsList<Record> ();
            list.add(decision);
            this.setSessionData(this.getSessionID(), SessionData.LOADER_PLUGIN, list);
        }
    }
    
    /**
     * Sets the session data if sever key is correct, automatically updates the
     * client
     * 
     * @param soureKey
     * @param dataType
     * @param data
     * @return whether the data set was successful
     */
    @Override
    public boolean setSessionData(String soureKey, SessionData dataType, RecordsList<Record> data) {
        if (super.setSessionData(soureKey, dataType, data)) {
            this.fireChange(new DataArriveEvent(dataType)); // TODO: Fix events
            LockedPacket p = new SetSessionDataPacket(this.engine, this, dataType, data, false);
            this.lockAndSendPacket(PacketPriority.MEDIUM, p);
            return true;
        }
        
        return false;
    }
    
    /**
     * Updates the session data, client should only update if from server
     * 
     * @param sourceKey
     * @param data 
     */
    @Override
    public void updateSessionData(String sourceKey, SessionData dataType, RecordsTransform data) {
        RecordsList<Record> oldData = this.checkoutSessionData(sourceKey, dataType);
        if (oldData == null) {
            return;
        }
        
        RecordsList<Record> dataUpdate = data.transform(oldData);
        if (this.setSessionData(this.getSessionID(), dataType, dataUpdate)) {
            // TODO: fire update session data
        
            RecordsTransform transform = RecordsTransform.getTransform(oldData, dataUpdate);
            LockedPacket p = new DataUpdatePacket(this.engine, this, dataType, transform);
            this.lockAndSendPacket(PacketPriority.MEDIUM, p);
        }
    }
    
    /**
     * Checks out session specific data, this will poll the database and be
     * preferred, so all pending updates must be submitted before checking out
     * again
     * 
     * @param sourceKey
     * @param dataType
     * @return list of records found, may be empty! 
     */
    @Override
    public RecordsList<Record> checkoutSessionData(String sourceKey, SessionData dataType) {
        if (dataType.getRecordType() == null) {
            RecordsList<Record> returnData = super.checkoutSessionData(sourceKey, dataType);
            LockedPacket p = new DataPacket(this.engine, this, dataType, returnData);
            this.lockAndSendPacket(PacketPriority.MEDIUM, p);
            return returnData;
        }
        
        RecordsList<Record> oldData = super.checkoutSessionData(sourceKey, dataType);
        RecordsList<Record> dataUpdate = this.checkoutDatabaseRecords(dataType);
        if (dataUpdate != null && this.setSessionData(this.getSessionID(), dataType, dataUpdate)) {
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
    
    /**
     * Softly checks out any session data from the data map, the key ensures the
     * correct side is querying this and no packets will be sent
     * 
     * @param dataType
     * @return 
     */
    public RecordsList<Record> softlyCheckoutSessionData(SessionData dataType) {
        if (dataType.getRecordType() == null) {
            return super.checkoutSessionData(this.getSessionID(), dataType);
        }
        
        else {
            RecordsList<Record> dbData = this.checkoutDatabaseRecords(dataType);
            if (this.setSessionData(this.getSessionID(), dataType, dbData)) {
                return dbData;
            }
            
            else {
                return super.checkoutSessionData(this.getSessionID(), dataType);
            }
        }
    }
    
    /**
     * Used to add an new record to the database
     * 
     * @param record 
     */
    public void addDatabaseRecord(String key, DatabaseRecord recordType, Record record) {
        if (!this.isServerKey(key) || record == null) {
            return;
        }
        
        GenericManager manager = recordType.getManager();
        manager.saveNew(this.connection, record);
        RecordsList<Record> list = new RecordsList<Record> ();
        list.add(record);
        this.setSessionData(this.getSessionID(), SessionData.getSessionDataFromDatabaseRecordAndQuery(recordType, QueryType.SINGLE), list);
        this.updateParent(record);
    }
    
    /**
     * Used to update an existing record from the database
     * 
     * @param record 
     */
    protected void updateDatabaseRecord(Record record) {
        if (record == null) {
            return;
        }
        
        GenericManager manager = record.getType().getManager();
        manager.save(this.connection, record);
        RecordsList<Record> list = new RecordsList<Record> ();
        list.add(record);
        this.setSessionData(this.getSessionID(), SessionData.getSessionDataFromDatabaseRecordAndQuery(record.getType(), QueryType.SINGLE), list);
        this.updateParent(record);
    }
    
    private void updateParent(Record record) {
        RecordsList<Record> dataList = this.getSessionFocusData();
        if (dataList == null || dataList.isEmpty()) {
            return;
        }
        
        Record data = dataList.getFirst();
        DatabaseRecord type = data.getType();
        if (type != null) {
            data.addToChildren(record);
            GenericManager parentManager = type.getManager();
            parentManager.save(this.connection, data);
        }
    }
    
    /**
     * Used to pull a set of records from the database based on a provided query
     * 
     * @param dataType
     * @return a list of results that may be null if none were found
     */
    private RecordsList<Record> checkoutDatabaseRecords(SessionData dataType) {
        if (dataType.getRecordType() == null) {
            return null;
        }
        
        if (dataType.getQueryType() == QueryType.SINGLE) {
            RecordsList<Record> list = super.checkoutSessionData(this.getSessionID(), dataType);
            if (list == null || list.isEmpty()) {
                return null;
            }
            
            Record value = this.checkoutDatabaseRecord(dataType.getRecordType(), list.getFirst().getUUID());
            RecordsList<Record> out = new RecordsList<Record> ();
            out.add(value);
            return out;
        }
        
        switch (dataType) {
            case ALL_USERS:
                return UserManager.getInstance().loadAll(this.connection);
                
            case USER_EXPERIMENTS:
                RecordsList<Record> list = this.softlyCheckoutSessionData(SessionData.getSessionDataFromDatabaseRecordAndQuery(DatabaseRecord.USER, QueryType.SINGLE));
                if (list == null || list.isEmpty()) {
                    return null;
                }
                
                Record record = list.getFirst();
                if (!(record instanceof User)) {
                    return null;
                }
                
                User user = (User) record;
                Set<Experiment> experiments = user.getExperiments();

                if (!experiments.isEmpty()) {
                    RecordsList<Record> out = new RecordsList<Record> ();
                    for (Experiment exp : experiments) {
                        out.add(exp);
                    }

                    return out;
                }
                
                return new RecordsList<Record> ();
                
            case ALL_LOADER_PLUGINS:
                return this.engine.getPluginManager().getLoaderPluginRecordList();
                
            default:
                return null;
        }
    }
    
    /**
     * Used to pull a record from the database based on the given key attribute
     * 
     * @param recordType
     * @return a record of type requested, may be null if no record was found
     */
    private Record checkoutDatabaseRecord(DatabaseRecord recordType, String attribute) {
        GenericManager manager = recordType.getManager();
        Record out = manager.findByID(this.connection, attribute);
        return out;
    }
}