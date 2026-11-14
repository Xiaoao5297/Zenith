package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings$GameType;
import net.minecraft.world.WorldType;

public class S07PacketRespawn extends Packet {

    private int field_149088_a;
    private EnumDifficulty field_149086_b;
    private WorldSettings$GameType field_149087_c;
    private WorldType field_149085_d;

    public S07PacketRespawn() {}

    public S07PacketRespawn(int i, EnumDifficulty enumdifficulty, WorldType worldtype, WorldSettings$GameType worldsettings$gametype) {
        this.field_149088_a = i;
        this.field_149086_b = enumdifficulty;
        this.field_149087_c = worldsettings$gametype;
        this.field_149085_d = worldtype;
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147280_a(this);
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149088_a = packetbuffer.readInt();
        this.field_149086_b = EnumDifficulty.func_151523_a(packetbuffer.readUnsignedByte());
        this.field_149087_c = WorldSettings$GameType.func_77146_a(packetbuffer.readUnsignedByte());
        this.field_149085_d = WorldType.func_77130_a(packetbuffer.func_150789_c(16));
        if (this.field_149085_d == null) {
            this.field_149085_d = WorldType.field_77137_b;
        }

    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149088_a);
        packetbuffer.writeByte(this.field_149086_b.func_151525_a());
        packetbuffer.writeByte(this.field_149087_c.func_77148_a());
        packetbuffer.func_150785_a(this.field_149085_d.func_77127_a());
    }
}
