package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.ScoreObjective;

public class S3DPacketDisplayScoreboard extends Packet {

    private int field_149374_a;
    private String field_149373_b;

    public S3DPacketDisplayScoreboard() {}

    public S3DPacketDisplayScoreboard(int i, ScoreObjective scoreobjective) {
        this.field_149374_a = i;
        if (scoreobjective == null) {
            this.field_149373_b = "";
        } else {
            this.field_149373_b = scoreobjective.func_96679_b();
        }

    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149374_a = packetbuffer.readByte();
        this.field_149373_b = packetbuffer.func_150789_c(16);
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeByte(this.field_149374_a);
        packetbuffer.func_150785_a(this.field_149373_b);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147254_a(this);
    }
}
