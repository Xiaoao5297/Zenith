package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S13PacketDestroyEntities extends Packet {

    private int[] field_149100_a;

    public S13PacketDestroyEntities() {}

    public S13PacketDestroyEntities(int... aint) {
        this.field_149100_a = aint;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149100_a = new int[packetbuffer.readByte()];

        for (int i = 0; i < this.field_149100_a.length; ++i) {
            this.field_149100_a[i] = packetbuffer.readInt();
        }

    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeByte(this.field_149100_a.length);

        for (int i = 0; i < this.field_149100_a.length; ++i) {
            packetbuffer.writeInt(this.field_149100_a[i]);
        }

    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147238_a(this);
    }

    @Override
    public String func_148835_b() {
        StringBuilder stringbuilder = new StringBuilder();

        for (int i = 0; i < this.field_149100_a.length; ++i) {
            if (i > 0) {
                stringbuilder.append(", ");
            }

            stringbuilder.append(this.field_149100_a[i]);
        }

        return String.format("entities=%d[%s]", this.field_149100_a.length, stringbuilder);
    }
}
