package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class S21PacketChunkData extends Packet {

    private int field_149284_a;
    private int field_149282_b;
    private int field_149283_c;
    private int field_149280_d;
    private byte[] field_149281_e;
    private byte[] field_149278_f;
    private boolean field_149279_g;
    private int field_149285_h;
    private static byte[] field_149286_i = new byte[196864];

    public S21PacketChunkData() {}

    public S21PacketChunkData(Chunk chunk, boolean flag, int i) {
        this.field_149284_a = chunk.field_76635_g;
        this.field_149282_b = chunk.field_76647_h;
        this.field_149279_g = flag;
        S21PacketChunkData$Extracted s21packetchunkdata$extracted = func_149269_a(chunk, flag, i);
        Deflater deflater = new Deflater(-1);

        this.field_149280_d = s21packetchunkdata$extracted.field_150281_c;
        this.field_149283_c = s21packetchunkdata$extracted.field_150280_b;

        try {
            this.field_149278_f = s21packetchunkdata$extracted.field_150282_a;
            deflater.setInput(s21packetchunkdata$extracted.field_150282_a, 0, s21packetchunkdata$extracted.field_150282_a.length);
            deflater.finish();
            this.field_149281_e = new byte[s21packetchunkdata$extracted.field_150282_a.length];
            this.field_149285_h = deflater.deflate(this.field_149281_e);
        } finally {
            deflater.end();
        }

    }

    public static int func_149275_c() {
        return 196864;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149284_a = packetbuffer.readInt();
        this.field_149282_b = packetbuffer.readInt();
        this.field_149279_g = packetbuffer.readBoolean();
        this.field_149283_c = packetbuffer.readShort();
        this.field_149280_d = packetbuffer.readShort();
        this.field_149285_h = packetbuffer.readInt();
        if (S21PacketChunkData.field_149286_i.length < this.field_149285_h) {
            S21PacketChunkData.field_149286_i = new byte[this.field_149285_h];
        }

        packetbuffer.readBytes(S21PacketChunkData.field_149286_i, 0, this.field_149285_h);
        int i = 0;

        int j;

        for (j = 0; j < 16; ++j) {
            i += this.field_149283_c >> j & 1;
        }

        j = 12288 * i;
        if (this.field_149279_g) {
            j += 256;
        }

        this.field_149278_f = new byte[j];
        Inflater inflater = new Inflater();

        inflater.setInput(S21PacketChunkData.field_149286_i, 0, this.field_149285_h);

        try {
            inflater.inflate(this.field_149278_f);
        } catch (DataFormatException dataformatexception) {
            throw new IOException("Bad compressed data format");
        } finally {
            inflater.end();
        }

    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149284_a);
        packetbuffer.writeInt(this.field_149282_b);
        packetbuffer.writeBoolean(this.field_149279_g);
        packetbuffer.writeShort((short) (this.field_149283_c & '\uffff'));
        packetbuffer.writeShort((short) (this.field_149280_d & '\uffff'));
        packetbuffer.writeInt(this.field_149285_h);
        packetbuffer.writeBytes(this.field_149281_e, 0, this.field_149285_h);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147263_a(this);
    }

    @Override
    public String func_148835_b() {
        return String.format("x=%d, z=%d, full=%b, sects=%d, add=%d, size=%d", this.field_149284_a, this.field_149282_b, this.field_149279_g, this.field_149283_c, this.field_149280_d, this.field_149285_h);
    }

    public static S21PacketChunkData$Extracted func_149269_a(Chunk chunk, boolean flag, int i) {
        int j = 0;
        ExtendedBlockStorage[] aextendedblockstorage = chunk.func_76587_i();
        int k = 0;
        S21PacketChunkData$Extracted s21packetchunkdata$extracted = new S21PacketChunkData$Extracted();
        byte[] abyte = S21PacketChunkData.field_149286_i;

        if (flag) {
            chunk.field_76642_o = true;
        }

        int l;

        for (l = 0; l < aextendedblockstorage.length; ++l) {
            if (aextendedblockstorage[l] != null && (!flag || !aextendedblockstorage[l].func_76663_a()) && (i & 1 << l) != 0) {
                s21packetchunkdata$extracted.field_150280_b |= 1 << l;
                if (aextendedblockstorage[l].func_76660_i() != null) {
                    s21packetchunkdata$extracted.field_150281_c |= 1 << l;
                    ++k;
                }
            }
        }

        for (l = 0; l < aextendedblockstorage.length; ++l) {
            if (aextendedblockstorage[l] != null && (!flag || !aextendedblockstorage[l].func_76663_a()) && (i & 1 << l) != 0) {
                byte[] abyte1 = aextendedblockstorage[l].func_76658_g();

                System.arraycopy(abyte1, 0, abyte, j, abyte1.length);
                j += abyte1.length;
            }
        }

        NibbleArray nibblearray;

        for (l = 0; l < aextendedblockstorage.length; ++l) {
            if (aextendedblockstorage[l] != null && (!flag || !aextendedblockstorage[l].func_76663_a()) && (i & 1 << l) != 0) {
                nibblearray = aextendedblockstorage[l].func_76669_j();
                System.arraycopy(nibblearray.field_76585_a, 0, abyte, j, nibblearray.field_76585_a.length);
                j += nibblearray.field_76585_a.length;
            }
        }

        for (l = 0; l < aextendedblockstorage.length; ++l) {
            if (aextendedblockstorage[l] != null && (!flag || !aextendedblockstorage[l].func_76663_a()) && (i & 1 << l) != 0) {
                nibblearray = aextendedblockstorage[l].func_76661_k();
                System.arraycopy(nibblearray.field_76585_a, 0, abyte, j, nibblearray.field_76585_a.length);
                j += nibblearray.field_76585_a.length;
            }
        }

        if (!chunk.field_76637_e.field_73011_w.field_76576_e) {
            for (l = 0; l < aextendedblockstorage.length; ++l) {
                if (aextendedblockstorage[l] != null && (!flag || !aextendedblockstorage[l].func_76663_a()) && (i & 1 << l) != 0) {
                    nibblearray = aextendedblockstorage[l].func_76671_l();
                    System.arraycopy(nibblearray.field_76585_a, 0, abyte, j, nibblearray.field_76585_a.length);
                    j += nibblearray.field_76585_a.length;
                }
            }
        }

        if (k > 0) {
            for (l = 0; l < aextendedblockstorage.length; ++l) {
                if (aextendedblockstorage[l] != null && (!flag || !aextendedblockstorage[l].func_76663_a()) && aextendedblockstorage[l].func_76660_i() != null && (i & 1 << l) != 0) {
                    nibblearray = aextendedblockstorage[l].func_76660_i();
                    System.arraycopy(nibblearray.field_76585_a, 0, abyte, j, nibblearray.field_76585_a.length);
                    j += nibblearray.field_76585_a.length;
                }
            }
        }

        if (flag) {
            byte[] abyte2 = chunk.func_76605_m();

            System.arraycopy(abyte2, 0, abyte, j, abyte2.length);
            j += abyte2.length;
        }

        s21packetchunkdata$extracted.field_150282_a = new byte[j];
        System.arraycopy(abyte, 0, s21packetchunkdata$extracted.field_150282_a, 0, j);
        return s21packetchunkdata$extracted;
    }
}
