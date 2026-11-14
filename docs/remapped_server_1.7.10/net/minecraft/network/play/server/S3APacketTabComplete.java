package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import org.apache.commons.lang3.ArrayUtils;

public class S3APacketTabComplete extends Packet {

    private String[] field_149632_a;

    public S3APacketTabComplete() {}

    public S3APacketTabComplete(String[] astring) {
        this.field_149632_a = astring;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149632_a = new String[packetbuffer.func_150792_a()];

        for (int i = 0; i < this.field_149632_a.length; ++i) {
            this.field_149632_a[i] = packetbuffer.func_150789_c(32767);
        }

    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.func_150787_b(this.field_149632_a.length);
        String[] astring = this.field_149632_a;
        int i = astring.length;

        for (int j = 0; j < i; ++j) {
            String s = astring[j];

            packetbuffer.func_150785_a(s);
        }

    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147274_a(this);
    }

    @Override
    public String func_148835_b() {
        return String.format("candidates='%s'", ArrayUtils.toString(this.field_149632_a));
    }
}
