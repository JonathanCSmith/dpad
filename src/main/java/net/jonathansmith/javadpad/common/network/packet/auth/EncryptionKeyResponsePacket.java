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
package net.jonathansmith.javadpad.common.network.packet.auth;


import java.util.concurrent.atomic.AtomicBoolean;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import net.jonathansmith.javadpad.common.Engine;
import net.jonathansmith.javadpad.common.network.packet.Packet;
import net.jonathansmith.javadpad.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.common.network.protocol.CommonDecoder;
import net.jonathansmith.javadpad.common.network.protocol.CommonEncoder;
import net.jonathansmith.javadpad.common.network.session.Session;
import net.jonathansmith.javadpad.common.network.session.Session.NetworkThreadState;
import net.jonathansmith.javadpad.common.security.SecurityHandler;
import net.jonathansmith.javadpad.server.network.session.ServerSession;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 *
 * @author Jon
 */
public class EncryptionKeyResponsePacket extends Packet {
    
    private static final AtomicBoolean lock = new AtomicBoolean(false);
    
    private static int id;
    
    private byte[] keys;
    private byte[] token;
    
    public EncryptionKeyResponsePacket() {
        super();
    }

    public EncryptionKeyResponsePacket(Engine engine, Session session, byte[] keys, byte[] token) {
        super(engine, session);
        this.keys = keys;
        this.token = token;
        this.forceUnencrypted();
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int newID) {
        if (lock.compareAndSet(false, true)) {
            id = newID;
        }
    }

    @Override
    public int getNumberOfPayloads() {
        return 2;
    }

    @Override
    public int getPayloadSize(int payloadNumber) {
        switch (payloadNumber) {
            case 0:
                return keys.length;
                
            case 1:
                return token.length;
                
            default:
                return 0;
        }
    }

    @Override
    public byte[] writePayload(int payloadNumber, int providedSize) {
        switch (payloadNumber) {
            case 0:
                return this.keys;
                
            case 1:
                return this.token;
                
            default:
                this.engine.warn("Encoding error, invalid payload number!");
                return null;
        }
    }

    @Override
    public void parsePayload(int payloadNumber, byte[] bytes) {
        switch (payloadNumber) {
            case 0:
                this.keys = bytes;
                return;
                
            case 1:
                this.token = bytes;
                return;
                
            default:
                this.engine.warn("Decoding error, invalid payload number!");
                return;
        }
    }

    @Override
    public void handleClientSide() {
        if (this.session.getState() != NetworkThreadState.EXCHANGING_AUTHENTICATION) {
            this.engine.error("Cannot respond to an authentication challenge, as either the handshake is not complete or we have not received the token");
            this.session.disconnect();
            this.session.dispose();
            return;
        }
        
        final byte[] sharedSecret = SecurityHandler.getInstance().getSymetricKey();
        CipherParameters symmetricKey = new ParametersWithIV(new KeyParameter(sharedSecret), sharedSecret);
        CFBBlockCipher fromServerCipher = SecurityHandler.getInstance().getSymmetricCipher();
        fromServerCipher.init(SecurityHandler.DECRYPT_MODE, symmetricKey);
        CommonDecoder decoder = this.session.channel.getPipeline().get(CommonDecoder.class);
        decoder.setDecryption(fromServerCipher);
    }

    @Override
    public void handleServerSide() {
        if (this.session.getState() != NetworkThreadState.EXCHANGING_AUTHENTICATION) {
            this.engine.warn("Cannot respond to an authentication challenge, as either the handshake is not complete or we have not sent the token");
            this.session.disconnect();
            this.session.dispose();
            return;
        }
        
        AsymmetricCipherKeyPair pair = SecurityHandler.getInstance().getKeyPair();
        AsymmetricBlockCipher cipher = SecurityHandler.getInstance().getAsymmetricCipher();
        cipher.init(SecurityHandler.DECRYPT_MODE, pair.getPrivate());
        
        final byte[] initialVector = SecurityHandler.getInstance().processAll(cipher, this.keys);
        final byte[] validateToken = SecurityHandler.getInstance().processAll(cipher, this.token);
        final byte[] savedToken = ((ServerSession) this.session).getVerifyToken();
        
        if (validateToken.length != 4) {
            this.engine.warn("Invalid token from session: " + this.session.getSessionID());
            this.session.disconnect();
            return;
        }
        
        for (int i = 0; i < validateToken.length; i++) {
            if (validateToken[i] != savedToken[i]) {
                this.engine.warn("Invalid token from session: " + this.session.getSessionID());
                this.session.disconnect();
                return;
            }
        }
        
        byte[] publicKeyEncoded = SecurityHandler.getInstance().encodeKey(pair.getPublic());
        String sha1Hash = this.sha1Hash(new Object[] {this.session.getSessionID(), initialVector, publicKeyEncoded});
        ((ServerSession) this.session).setSha1Hash(sha1Hash);
        
        CipherParameters symmetricKey = new ParametersWithIV(new KeyParameter(initialVector), initialVector);
        
        CFBBlockCipher toClientCipher = SecurityHandler.getInstance().getSymmetricCipher();
        toClientCipher.init(SecurityHandler.ENCRYPT_MODE, symmetricKey);
        CommonEncoder encoder = this.session.channel.getPipeline().get(CommonEncoder.class);
        encoder.setEncryption(toClientCipher);
        
        CFBBlockCipher fromClientCipher = SecurityHandler.getInstance().getSymmetricCipher();
        fromClientCipher.init(SecurityHandler.DECRYPT_MODE, symmetricKey);
        CommonDecoder decoder = this.session.channel.getPipeline().get(CommonDecoder.class);
        decoder.setDecryption(fromClientCipher);
        
        Packet p = new EncryptionKeyResponsePacket(this.engine, this.session, new byte[0], new byte[0]);
        this.session.addPacketToSend(PacketPriority.CRITICAL, p);
        this.session.incrementState();
        
        // TODO: Delay or not delay
        ((ServerSession) this.session).buildAndSendEncryptedSessionKeyPacket();
    }
    
    @Override
    public String toString() {
        return "Encryption key response packet";
    }
    
    private String sha1Hash(Object[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.reset();
            
            for (Object o : input) {
                if (o instanceof String) {
                    md.update(((String) o).getBytes("ISO_8859_1"));
                }
                
                else if (o instanceof byte[]) {
                    md.update((byte[]) o);
                }
                
                else {
                    return null;
                }
            }
            
            BigInteger bigInt = new BigInteger(md.digest());
            
            if (bigInt.compareTo(BigInteger.ZERO) < 0) {
                bigInt = bigInt.negate();
                return "-" + bigInt.toString(16);
            }
            
            else {
                return bigInt.toString(16);
            }
        }
        
        catch (Exception ex) {
            this.engine.error("Error in sha1 digest", ex);
            return null;
        }
    }
}
