package jonathansmith.dpad.common.network.packet;

import java.io.IOException;

import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Packet buffer class containing utility methods for writing packets to a byte buffer
 */
public class PacketBuffer {

    public static int getVarIntSize(int integer) {
        if ((integer & -128) == 0) {
            return 1;
        }

        else if ((integer & -16834) == 0) {
            return 2;
        }

        else if ((integer & -2097152) == 0) {
            return 3;
        }

        else if ((integer & -268435456) == 0) {
            return 4;
        }

        else {
            return 5;
        }
    }

    private final ByteBuf buffer;

    public PacketBuffer(ByteBuf byteBuf) {
        this.buffer = byteBuf;
    }

    public int readVarIntFromBuffer() {
        int i = 0;
        int j = 0;
        byte b;

        do {
            b = this.buffer.readByte();
            i |= (b & 127) << j++ * 7;

            if (j > 5) {
                throw new RuntimeException("The VarInt in the buffer was too big to decode");
            }
        } while ((b & 128) == 128);

        return i;
    }

    public void writeVarIntToBuffer(int integer) {
        while ((integer & -128) != 0) {
            this.buffer.writeByte(integer & 127 | 128);
            integer >>>= 7;
        }

        this.buffer.writeByte(integer);
    }

    public String readStringFromBuffer(int maxSize) throws IOException {
        int i = this.readVarIntFromBuffer();

        if (i > maxSize * 4) {
            throw new IOException("The received encoded string buffer length is longer than the maximum provided!");
        }

        else if (i < 0) {
            throw new IOException("Error reading the string from the buffer as its indicated length is less than zero!");
        }

        else {
            String s = new String(this.buffer.readBytes(i).array(), Charsets.UTF_8);

            if (s.length() > maxSize) {
                throw new IOException("The received string length was longer than the maximum provided!");
            }

            else {
                return s;
            }
        }
    }

    public void writeStringToBuffer(String s) throws IOException {
        byte[] bytes = s.getBytes(Charsets.UTF_8);

        if (bytes.length > 32767) {
            throw new IOException("The string provided: " + s + " was too long to encode!");
        }

        else {
            this.writeVarIntToBuffer(bytes.length);
            this.buffer.writeBytes(bytes);
        }
    }

    public int readUnsignedShort() {
        return this.buffer.readUnsignedShort();
    }

    public void writeShort(int s) {
        this.buffer.writeShort(s);
    }

    public int readableBytes() {
        return this.buffer.readableBytes();
    }

    public void ensureWriteable(int i) {
        this.buffer.ensureWritable(i);
    }

    public void writeBytes(ByteBuf source, int readerIndex, int length) {
        this.buffer.writeBytes(source, readerIndex, length);
    }
}
