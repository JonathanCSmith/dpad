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
package net.jonathansmith.javadpad.aaaarewrite.common.network.packet.auth;


import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.util.PublicKeyFactory;

import net.jonathansmith.javadpad.aaaarewrite.common.network.message.PacketMessage;
import net.jonathansmith.javadpad.aaaarewrite.common.network.packet.Packet;
import net.jonathansmith.javadpad.aaaarewrite.common.network.packet.PacketPriority;
import net.jonathansmith.javadpad.aaaarewrite.common.network.protocol.CommonEncoder;
import net.jonathansmith.javadpad.aaaarewrite.common.network.session.Session;
import net.jonathansmith.javadpad.aaaarewrite.common.security.SecurityHandler;
import net.jonathansmith.javadpad.aaaarewrite.common.thread.Engine;

/**
 *
 * @author Jon
 */
class EncryptionKeyRequestPacket extends Packet {

    private byte[] keys;
    private byte[] token;
    
    public EncryptionKeyRequestPacket(Engine engine, Session session, byte[] keys, byte[] token) {
        super(engine, session);
        this.keys = keys;
        this.token = token;
        this.forceUnencrypted(); // Asymmetric only for this packet as we havent established everything
    }

    @Override
    public int getNumberOfPayloads() {
        return 2;
    }

    @Override
    public int[] getPayloadSizes() {
        return new int[] {keys.length, token.length};
    }

    @Override
    public byte[] writePayload(int payloadNumber) {
        switch (payloadNumber) {
            case 0:
                return this.keys;
                
            case 1:
                return this.token;
                
            default:
                System.out.println("Encode failure, payloads are being read wrong");
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
                System.out.println("Decode failure, payloads are being read wrong");
                return;
        }
    }

    @Override
    public void handleClientSide() {
        final byte[] sharedKey = SecurityHandler.getInstance().getSymetricKey();
        
        AsymmetricBlockCipher cipher = SecurityHandler.getInstance().getAsymmetricCipher();
        try {
            AsymmetricKeyParameter publicKey = PublicKeyFactory.createKey(this.keys);
            cipher.init(SecurityHandler.ENCRYPT_MODE, publicKey);
            
            byte[] encodedSecret = cipher.processBlock(sharedKey, 0, 16);
            byte[] encodedToken = cipher.processBlock(this.token, 0, 4);
            
            CipherParameters symmetricKey = new ParametersWithIV(new KeyParameter(sharedKey), sharedKey);
            
            CFBBlockCipher toServerCipher = SecurityHandler.getInstance().getSymmetricCipher();
            toServerCipher.init(SecurityHandler.ENCRYPT_MODE, symmetricKey);
            
            CommonEncoder encoder = this.session.channel.getPipeline().get(CommonEncoder.class);
            encoder.setEncryption(toServerCipher);
            
            Packet p = new EncryptionKeyResponsePacket(this.engine, this.session, encodedSecret, encodedToken);
            this.session.sendPacketMessage(new PacketMessage(p, PacketPriority.CRITICAL));
            
        } catch (Exception ex) {
            System.out.println("Error when responding to encryption key request");
            ex.printStackTrace();
        }
    }

    @Override
    public void handleServerSide() {}
}
