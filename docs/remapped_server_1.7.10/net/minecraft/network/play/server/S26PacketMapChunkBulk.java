package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.chunk.Chunk;

public class S26PacketMapChunkBulk extends Packet {

    private int[] field_149266_a;
    private int[] field_149264_b;
    private int[] field_149265_c;
    private int[] field_149262_d;
    private byte[] field_149263_e;
    private byte[][] field_149260_f;
    private int field_149261_g;
    private boolean field_149267_h;
    private static byte[] field_149268_i = new byte[0];

    public S26PacketMapChunkBulk() {}

    public S26PacketMapChunkBulk(List list) {
        int i = list.size();

        this.field_149266_a = new int[i];
        this.field_149264_b = new int[i];
        this.field_149265_c = new int[i];
        this.field_149262_d = new int[i];
        this.field_149260_f = new byte[i][];
        this.field_149267_h = !list.isEmpty() && !((Chunk) list.get(0)).field_76637_e.field_73011_w.field_76576_e;
        int j = 0;

        for (int k = 0; k < i; ++k) {
            Chunk chunk = (Chunk) list.get(k);
            S21PacketChunkData$Extracted s21packetchunkdata$extracted = S21PacketChunkData.func_149269_a(chunk, true, 65535);

            if (S26PacketMapChunkBulk.field_149268_i.length < j + s21packetchunkdata$extracted.field_150282_a.length) {
                byte[] abyte = new byte[j + s21packetchunkdata$extracted.field_150282_a.length];

                System.arraycopy(S26PacketMapChunkBulk.field_149268_i, 0, abyte, 0, S26PacketMapChunkBulk.field_149268_i.length);
                S26PacketMapChunkBulk.field_149268_i = abyte;
            }

            System.arraycopy(s21packetchunkdata$extracted.field_150282_a, 0, S26PacketMapChunkBulk.field_149268_i, j, s21packetchunkdata$extracted.field_150282_a.length);
            j += s21packetchunkdata$extracted.field_150282_a.length;
            this.field_149266_a[k] = chunk.field_76635_g;
            this.field_149264_b[k] = chunk.field_76647_h;
            this.field_149265_c[k] = s21packetchunkdata$extracted.field_150280_b;
            this.field_149262_d[k] = s21packetchunkdata$extracted.field_150281_c;
            this.field_149260_f[k] = s21packetchunkdata$extracted.field_150282_a;
        }

        Deflater deflater = new Deflater(-1);

        try {
            deflater.setInput(S26PacketMapChunkBulk.field_149268_i, 0, j);
            deflater.finish();
            this.field_149263_e = new byte[j];
            this.field_149261_g = deflater.deflate(this.field_149263_e);
        } finally {
            deflater.end();
        }

    }

    public static int func_149258_c() {
        return 5;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        short short0 = packetbuffer.readShort();

        this.field_149261_g = packetbuffer.readInt();
        this.field_149267_h = packetbuffer.readBoolean();
        this.field_149266_a = new int[short0];
        this.field_149264_b = new int[short0];
        this.field_149265_c = new int[short0];
        this.field_149262_d = new int[short0];
        this.field_149260_f = new byte[short0][];
        if (S26PacketMapChunkBulk.field_149268_i.length < this.field_149261_g) {
            S26PacketMapChunkBulk.field_149268_i = new byte[this.field_149261_g];
        }

        packetbuffer.readBytes(S26PacketMapChunkBulk.field_149268_i, 0, this.field_149261_g);
        byte[] abyte = new byte[S21PacketChunkData.func_149275_c() * short0];
        Inflater inflater = new Inflater();

        inflater.setInput(S26PacketMapChunkBulk.field_149268_i, 0, this.field_149261_g);

        try {
            inflater.inflate(abyte);
        } catch (DataFormatException dataformatexception) {
            throw new IOException("Bad compressed data format");
        } finally {
            inflater.end();
        }

        int i = 0;

        for (int j = 0; j < short0; ++j) {
            this.field_149266_a[j] = packetbuffer.readInt();
            this.field_149264_b[j] = packetbuffer.readInt();
            this.field_149265_c[j] = packetbuffer.readShort();
            this.field_149262_d[j] = packetbuffer.readShort();
            int k = 0;
            int l = 0;

            int i1;

            for (i1 = 0; i1 < 16; ++i1) {
                k += this.field_149265_c[j] >> i1 & 1;
                l += this.field_149262_d[j] >> i1 & 1;
            }

            i1 = 2048 * 4 * k + 256;
            i1 += 2048 * l;
            if (this.field_149267_h) {
                i1 += 2048 * k;
            }

            this.field_149260_f[j] = new byte[i1];
            System.arraycopy(abyte, i, this.field_149260_f[j], 0, i1);
            i += i1;
        }

    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeShort(this.field_149266_a.length);
        packetbuffer.writeInt(this.field_149261_g);
        packetbuffer.writeBoolean(this.field_149267_h);
        packetbuffer.writeBytes(this.field_149263_e, 0, this.field_149261_g);

        for (int i = 0; i < this.field_149266_a.length; ++i) {
            packetbuffer.writeInt(this.field_149266_a[i]);
            packetbuffer.writeInt(this.field_149264_b[i]);
            packetbuffer.writeShort((short) (this.field_149265_c[i] & '\uffff'));
            packetbuffer.writeShort((short) (this.field_149262_d[i] & '\uffff'));
        }

    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147269_a(this);
    }

    @Override
    public String func_148835_b() {
        StringBuilder stringbuilder = new StringBuilder();

        for (int i = 0; i < this.field_149266_a.length; ++i) {
            if (i > 0) {
                stringbuilder.append(", ");
            }

            stringbuilder.append(String.format("{x=%d, z=%d, sections=%d, adds=%d, data=%d}", this.field_149266_a[i], this.field_149264_b[i], this.field_149265_c[i], this.field_149262_d[i], this.field_149260_f[i].length));
        }

        return String.format("size=%d, chunks=%d[%s]", this.field_149261_g, this.field_149266_a.length, stringbuilder);
    }
}
