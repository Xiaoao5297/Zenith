package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.ScoreObjective;

public class S3BPacketScoreboardObjective extends Packet {

    private String field_149343_a;
    private String field_149341_b;
    private int field_149342_c;

    public S3BPacketScoreboardObjective() {}

    public S3BPacketScoreboardObjective(ScoreObjective scoreobjective, int i) {
        this.field_149343_a = scoreobjective.func_96679_b();
        this.field_149341_b = scoreobjective.func_96678_d();
        this.field_149342_c = i;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149343_a = packetbuffer.func_150789_c(16);
        this.field_149341_b = packetbuffer.func_150789_c(32);
        this.field_149342_c = packetbuffer.readByte();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.func_150785_a(this.field_149343_a);
        packetbuffer.func_150785_a(this.field_149341_b);
        packetbuffer.writeByte(this.field_149342_c);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147291_a(this);
    }
}
