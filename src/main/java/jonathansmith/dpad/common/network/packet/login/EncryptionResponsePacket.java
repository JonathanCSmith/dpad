package jonathansmith.dpad.common.network.packet.login;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import jonathansmith.dpad.common.crypto.CryptographyManager;
import jonathansmith.dpad.common.network.packet.Packet;
import jonathansmith.dpad.common.network.packet.PacketBuffer;
import jonathansmith.dpad.common.network.protocol.NetworkProtocol;

import jonathansmith.dpad.server.network.protocol.ServerLoginNetworkProtocol;

/**
 * Created by Jon on 08/04/14.
 * <p/>
 * Handles the client's response to an encryption request
 */
public class EncryptionResponsePacket extends Packet {

    private byte[] encodedSecretKey;
    private byte[] encodedRandomSignature;

    public EncryptionResponsePacket(SecretKey secretKey, PublicKey key, byte[] randomSignature) {
        this.encodedSecretKey = CryptographyManager.encryptData(key, secretKey.getEncoded());
        this.encodedRandomSignature = CryptographyManager.encryptData(key, randomSignature);
    }

    public SecretKey decodeSecretKey(PrivateKey key) {
        return CryptographyManager.decryptSharedKey(key, this.encodedSecretKey);
    }

    public byte[] decodeRandomSignature(PrivateKey key) {
        return key == null ? this.encodedRandomSignature : CryptographyManager.decryptData(key, this.encodedRandomSignature);
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer) throws IOException {
        this.encodedSecretKey = packetBuffer.readBlob();
        this.encodedRandomSignature = packetBuffer.readBlob();
    }

    @Override
    public void writePacketData(PacketBuffer packetBuffer) throws IOException {
        packetBuffer.writeBlob(this.encodedSecretKey);
        packetBuffer.writeBlob(this.encodedRandomSignature);
    }

    @Override
    public void processPacket(NetworkProtocol networkProtocol) {
        ((ServerLoginNetworkProtocol) networkProtocol).handleEncryptionResponse(this);
    }

    @Override
    public String payloadToString() {
        return "Encryption response packet ... payload masked";
    }
}
