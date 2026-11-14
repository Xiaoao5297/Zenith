package net.minecraft.network;

import com.google.common.collect.BiMap;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Packet {

    private static final Logger field_148841_a = LogManager.getLogger();

    public Packet() {}

    public static Packet func_148839_a(BiMap bimap, int i) {
        try {
            Class oclass = (Class) bimap.get(i);

            return oclass == null ? null : (Packet) oclass.newInstance();
        } catch (Exception exception) {
            Packet.field_148841_a.error("Couldn't create packet " + i, (Throwable) exception);
            return null;
        }
    }

    public static void func_148838_a(ByteBuf bytebuf, byte[] abyte) {
        bytebuf.writeShort(abyte.length);
        bytebuf.writeBytes(abyte);
    }

    public static byte[] func_148834_a(ByteBuf bytebuf) {
        short short0 = bytebuf.readShort();

        if (short0 < 0) {
            throw new IOException("Key was smaller than nothing!  Weird key!");
        } else {
            byte[] abyte = new byte[short0];

            bytebuf.readBytes(abyte);
            return abyte;
        }
    }

    public abstract void func_148837_a(PacketBuffer packetbuffer);

    public abstract void func_148840_b(PacketBuffer packetbuffer);

    public abstract void func_148833_a(INetHandler inethandler);

    public boolean func_148836_a() {
        return false;
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }

    public String func_148835_b() {
        return "";
    }
}
