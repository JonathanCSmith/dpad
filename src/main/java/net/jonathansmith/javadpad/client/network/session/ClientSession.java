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
import net.jonathansmith.javadpad.common.events.sessiondata.DataArriveEvent;
import net.jonathansmith.javadpad.common.network.packet.LockedPacket;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.packet.auth.EncryptedSessionKeyPacket;
import net.jonathansmith.javadpad.common.network.packet.auth.EncryptionKeyRequestPacket;
import net.jonathansmith.javadpad.common.network.packet.auth.EncryptionKeyResponsePacket;
import net.jonathansmith.javadpad.common.network.packet.auth.HandshakePacket;
import net.jonathansmith.javadpad.common.network.packet.database.DataRequestPacket;
import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.common.network.session.SessionData;
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
            this.disconnect(true);
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
            this.disconnect(true);
        }
    }
    
    @Override
    public void handleEncryptionKeyResponse(EncryptionKeyResponsePacket p, boolean isReply) {
        if (isReply) {
            if (this.getState() != NetworkThreadState.EXCHANGING_AUTHENTICATION) {
                this.engine.error("Cannot respond to an authentication challenge, as either the handshake is not complete or we have not received the token");
                this.disconnect(true);
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
    public void disconnect(boolean force) {
        this.incoming.shutdown(force);
        this.outgoing.shutdown(force);
        
        while (this.incoming.isRunning() || this.outgoing.isRunning()) {
            try {
                Thread.sleep(100);
            }
            
            catch (InterruptedException ex) {
                this.engine.error("Shutdown of network threads was interrupted, channel may have exited early", ex);
            }
        }
        
        if (this.channel.isConnected()) {
            this.channel.disconnect();
        }
    }
    
    /**
     * Sets the session data if server key is correct, ensures client doesnt
     * update itself
     * 
     * @param soureKey
     * @param dataType
     * @param data
     * @return whether the data set was successful
     */
    @Override
    public boolean setSessionData(String soureKey, SessionData dataType, RecordsList<Record> data) {
        if (super.setSessionData(soureKey, dataType, data)) {
            this.sessionDataTimestamp.put(dataType, System.currentTimeMillis());
            this.fireChange(new DataArriveEvent(dataType));
            return true;
        }
        
        return false;
    }
    
    /**
     * Updates the session data if data is present, otherwise does nothing
     * Only accepts server key
     * 
     * @param sourceKey
     * @param dataType
     * @param data 
     */
    @Override
    public void updateSessionData(String sourceKey, SessionData dataType, RecordsTransform data) {
        RecordsList<Record> currentData = this.softlyCheckoutSessionData(dataType);
        if (currentData == null) {
            return;
        }
        
        RecordsList<Record> dataUpdate = data.transform(currentData);
        if (this.setSessionData(sourceKey, dataType, dataUpdate)) {
            this.sessionDataTimestamp.put(dataType, System.currentTimeMillis());
            // TODO: fire update session data
        }
    }
    
    /**
     * Checks out session data, if the current data is outdated or not present
     * it will request the data from the server
     * 
     * @param sourceKey
     * @param dataType
     * @return 
     */
    @Override
    public RecordsList<Record> checkoutSessionData(String sourceKey, SessionData dataType) {
        if (this.sessionDataTimestamp.containsKey(dataType)) {
            if (System.currentTimeMillis() - this.sessionDataTimestamp.get(dataType) < 300000) {
                return super.checkoutSessionData(sourceKey, dataType);
            }
        }
        
        LockedPacket p = new DataRequestPacket(this.engine, this, dataType);
        this.lockAndSendPacket(PacketPriority.HIGH, p);
        return null;
    }
    
    /**
     * Softly checks out any session data from the data map, the key ensures the
     * correct side is querying this and no packets will be sent
     * 
     * @param dataType
     * @return 
     */
    public RecordsList<Record> softlyCheckoutSessionData(SessionData dataType) {
        return super.checkoutSessionData(this.getSessionID(), dataType);
    }
}