package net.minecraft.network.play.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class S22PacketMultiBlockChange extends Packet {

    private static final Logger field_148927_a = LogManager.getLogger();
    private ChunkCoordIntPair field_148925_b;
    private byte[] field_148926_c;
    private int field_148924_d;

    public S22PacketMultiBlockChange() {}

    public S22PacketMultiBlockChange(int i, short[] ashort, Chunk chunk) {
        this.field_148925_b = new ChunkCoordIntPair(chunk.field_76635_g, chunk.field_76647_h);
        this.field_148924_d = i;
        int j = 4 * i;

        try {
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(j);
            DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);

            for (int k = 0; k < i; ++k) {
                int l = ashort[k] >> 12 & 15;
                int i1 = ashort[k] >> 8 & 15;
                int j1 = ashort[k] & 255;

                dataoutputstream.writeShort(ashort[k]);
                dataoutputstream.writeShort((short) ((Block.func_149682_b(chunk.func_150810_a(l, j1, i1)) & 4095) << 4 | chunk.func_76628_c(l, j1, i1) & 15));
            }

            this.field_148926_c = bytearrayoutputstream.toByteArray();
            if (this.field_148926_c.length != j) {
                throw new RuntimeException("Expected length " + j + " doesn't match received length " + this.field_148926_c.length);
            }
        } catch (IOException ioexception) {
            S22PacketMultiBlockChange.field_148927_a.error("Couldn't create bulk block update packet", (Throwable) ioexception);
            this.field_148926_c = null;
        }

    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_148925_b = new ChunkCoordIntPair(packetbuffer.readInt(), packetbuffer.readInt());
        this.field_148924_d = packetbuffer.readShort() & '\uffff';
        int i = packetbuffer.readInt();

        if (i > 0) {
            this.field_148926_c = new byte[i];
            packetbuffer.readBytes(this.field_148926_c);
        }

    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_148925_b.field_77276_a);
        packetbuffer.writeInt(this.field_148925_b.field_77275_b);
        packetbuffer.writeShort((short) this.field_148924_d);
        if (this.field_148926_c != null) {
            packetbuffer.writeInt(this.field_148926_c.length);
            packetbuffer.writeBytes(this.field_148926_c);
        } else {
            packetbuffer.writeInt(0);
        }

    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147287_a(this);
    }

    @Override
    public String func_148835_b() {
        return String.format("xc=%d, zc=%d, count=%d", this.field_148925_b.field_77276_a, this.field_148925_b.field_77275_b, this.field_148924_d);
    }
}
