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
package net.jonathansmith.javadpad.client.network.session;

import java.util.EnumMap;
import java.util.Map;

import org.jboss.netty.channel.Channel;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.util.PublicKeyFactory;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.database.Record;
import net.jonathansmith.javadpad.common.database.RecordsTransform;
import net.jonathansmith.javadpad.common.database.SessionData;
import net.jonathansmith.javadpad.common.database.records.Experiment;
import net.jonathansmith.javadpad.common.database.records.User;
import net.jonathansmith.javadpad.common.events.sessiondata.DataArriveEvent;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.packet.auth.EncryptedSessionKeyPacket;
import net.jonathansmith.javadpad.common.network.packet.auth.EncryptionKeyRequestPacket;
import net.jonathansmith.javadpad.common.network.packet.auth.EncryptionKeyResponsePacket;
import net.jonathansmith.javadpad.common.network.packet.auth.HandshakePacket;
import net.jonathansmith.javadpad.common.network.packet.database.DataRequestPacket;
import net.jonathansmith.javadpad.common.network.session.DatabaseRecord;
import static net.jonathansmith.javadpad.common.network.session.DatabaseRecord.USER;
import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.common.security.SecurityHandler;
import net.jonathansmith.javadpad.common.util.database.RecordsList;

/**
 *
 * @author Jon
 */
public final class ClientSession extends Session {
    
    private final Map<SessionData, Long> sessionDataTimestamp = new EnumMap<SessionData, Long> (SessionData.class);
    
    public ClientSession(Engine eng, Channel c) {
        super(eng, c);
        this.incoming = new IncomingClientNetworkThread(eng, this, this.getSessionID());
        this.outgoing = new OutgoingClientNetworkThread(eng, this, this.getSessionID());
        this.start();
    }
    
    @Override
    public void handleHandshake(HandshakePacket p) {
        this.addPacketToSend(PacketPriority.CRITICAL, p);
        this.incrementState();
    }
    
    @Override
    public void handleEncryptionKeyRequest(EncryptionKeyRequestPacket p) {
        if (this.getState() != NetworkThreadState.EXCHANGING_AUTHENTICATION) {
            this.engine.error("Cannot respond to an authentication challenge, as either the handshake is not complete or we have not received the token");
            this.disconnect();
            this.dispose();
            return;
        }
        
        byte[] sharedKey = SecurityHandler.getInstance().getSymetricKey();
        AsymmetricBlockCipher cipher = SecurityHandler.getInstance().getAsymmetricCipher();
        
        try {
            AsymmetricKeyParameter publicKey = PublicKeyFactory.createKey(p.keys);
            cipher.init(SecurityHandler.ENCRYPT_MODE, publicKey);
            
            byte[] encodedSecret = cipher.processBlock(sharedKey, 0, 16);
            byte[] encodedToken = cipher.processBlock(p.token, 0, 4);
            
            CipherParameters symmetricKey = new ParametersWithIV(new KeyParameter(sharedKey), sharedKey);
            
            CFBBlockCipher toServerCipher = SecurityHandler.getInstance().getSymmetricCipher();
            toServerCipher.init(SecurityHandler.ENCRYPT_MODE, symmetricKey);
            
            //CommonEncoder encoder = this.session.channel.getPipeline().get(CommonEncoder.class);
            //encoder.setEncryption(toServerCipher);
            
            EncryptionKeyResponsePacket reply = new EncryptionKeyResponsePacket(this.engine, this, encodedSecret, encodedToken);
            this.handleEncryptionKeyResponse(reply, false);
            
        } catch (Exception ex) {
            this.engine.error("Error when responding to encryption key request", ex);
            this.disconnect();
            this.dispose();
        }
    }
    
    @Override
    public void handleEncryptionKeyResponse(EncryptionKeyResponsePacket p, boolean isReply) {
        if (isReply) {
            if (this.getState() != NetworkThreadState.EXCHANGING_AUTHENTICATION) {
                this.engine.error("Cannot respond to an authentication challenge, as either the handshake is not complete or we have not received the token");
                this.disconnect();
                this.dispose();
                return;
            }

            final byte[] sharedSecret = SecurityHandler.getInstance().getSymetricKey();
            CipherParameters symmetricKey = new ParametersWithIV(new KeyParameter(sharedSecret), sharedSecret);
            CFBBlockCipher fromServerCipher = SecurityHandler.getInstance().getSymmetricCipher();
            fromServerCipher.init(SecurityHandler.DECRYPT_MODE, symmetricKey);
            //CommonDecoder decoder = this.session.channel.getPipeline().get(CommonDecoder.class);
            //decoder.setDecryption(fromServerCipher);
        }
        
        else {
            this.addPacketToSend(PacketPriority.CRITICAL, p);
        }
    }
    
    @Override
    public void handleSessionKey(EncryptedSessionKeyPacket p) {
        this.setKey(p.getKey());
        this.incrementState();
    }
    
    public void setKey(String key) {
        this.setServerKey(key);
    }

    @Override
    public void addData(String key, SessionData dataType, RecordsList<Record> data) {
        if (this.addSessionData(key, dataType, data)) {
            this.sessionDataTimestamp.put(dataType, System.currentTimeMillis());
            this.fireChange(new DataArriveEvent(dataType));
        }
    }

    @Override
    public void updateData(String key, SessionData dataType, RecordsTransform data) {
        RecordsList<Record> currentData = this.checkoutSessionData(dataType);
        if (this.isServerKey(key)) {
            if (currentData != null) {
                currentData = data.transform(currentData);
                this.addSessionData(key, dataType, currentData);
                this.sessionDataTimestamp.put(dataType, System.currentTimeMillis());
            }
            
            else {
                Packet p = new DataRequestPacket(this.engine, this, dataType);
                this.addPacketToSend(PacketPriority.HIGH, p);
            }
        }
    }

    @Override
    public RecordsList<Record> checkoutData(SessionData dataType) {
        RecordsList<Record> data = this.checkoutSessionData(dataType);
        if (data == null || !this.sessionDataTimestamp.containsKey(dataType) || System.currentTimeMillis() - this.sessionDataTimestamp.get(dataType) > 300000) {
            Packet p = new DataRequestPacket(this.engine, this, dataType);
            this.addPacketToSend(PacketPriority.HIGH, p);
            return null;
        }
        
        else {
            return data;
        }
    }
    
    @Override
    public void setKeySessionData(String key, DatabaseRecord type, Record data) {
        if (!this.isServerKey(key)) {
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
        }
    }
    
    @Override
    public void shutdown(boolean force) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO:
    }

    @Override
    public void disconnect() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO
    }

    @Override
    public void dispose() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO
    }
}
