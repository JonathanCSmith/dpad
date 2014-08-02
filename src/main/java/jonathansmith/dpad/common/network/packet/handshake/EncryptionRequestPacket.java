package jonathansmith.dpad.common.network.packet.handshake;

import java.io.IOException;
import java.security.PublicKey;

import jonathansmith.dpad.common.crypto.CryptographyManager;
import jonathansmith.dpad.common.network.packet.Packet;
import jonathansmith.dpad.common.network.packet.PacketBuffer;
import jonathansmith.dpad.common.network.protocol.INetworkProtocol;

import jonathansmith.dpad.client.network.protocol.ClientLoginProtocol;

/**
 * Created by Jon on 08/04/14.
 * <p/>
 * Begins the encryption process between client and server
 */
public class EncryptionRequestPacket extends Packet {

    private PublicKey key;
    private byte[]    randomSignature;

    public EncryptionRequestPacket() {
    }

    public EncryptionRequestPacket(PublicKey aPublic, byte[] loginKey) {
        this.key = aPublic;
        this.randomSignature = loginKey;
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer) throws IOException {
        this.key = CryptographyManager.decodePublicKey(packetBuffer.readBlob());
        this.randomSignature = packetBuffer.readBlob();
    }

    @Override
    public void writePacketData(PacketBuffer packetBuffer) throws IOException {
        packetBuffer.writeBlob(this.key.getEncoded());
        packetBuffer.writeBlob(this.randomSignature);
    }

    @Override
    public void processPacket(INetworkProtocol networkProtocol) {
        ((ClientLoginProtocol) networkProtocol).handleEncryptionRequest(this.key, this.randomSignature);
    }

    @Override
    public String payloadToString() {
        return "Encryption request packet... payload masked";
    }
}
