package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.Score;

public class S3CPacketUpdateScore extends Packet {

    private String field_149329_a = "";
    private String field_149327_b = "";
    private int field_149328_c;
    private int field_149326_d;

    public S3CPacketUpdateScore() {}

    public S3CPacketUpdateScore(Score score, int i) {
        this.field_149329_a = score.func_96653_e();
        this.field_149327_b = score.func_96645_d().func_96679_b();
        this.field_149328_c = score.func_96652_c();
        this.field_149326_d = i;
    }

    public S3CPacketUpdateScore(String s) {
        this.field_149329_a = s;
        this.field_149327_b = "";
        this.field_149328_c = 0;
        this.field_149326_d = 1;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149329_a = packetbuffer.func_150789_c(16);
        this.field_149326_d = packetbuffer.readByte();
        if (this.field_149326_d != 1) {
            this.field_149327_b = packetbuffer.func_150789_c(16);
            this.field_149328_c = packetbuffer.readInt();
        }

    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.func_150785_a(this.field_149329_a);
        packetbuffer.writeByte(this.field_149326_d);
        if (this.field_149326_d != 1) {
            packetbuffer.func_150785_a(this.field_149327_b);
            packetbuffer.writeInt(this.field_149328_c);
        }

    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147250_a(this);
    }
}
