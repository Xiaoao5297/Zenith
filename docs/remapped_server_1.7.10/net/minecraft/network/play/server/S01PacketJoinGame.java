package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings$GameType;
import net.minecraft.world.WorldType;

public class S01PacketJoinGame extends Packet {

    private int field_149206_a;
    private boolean field_149204_b;
    private WorldSettings$GameType field_149205_c;
    private int field_149202_d;
    private EnumDifficulty field_149203_e;
    private int field_149200_f;
    private WorldType field_149201_g;

    public S01PacketJoinGame() {}

    public S01PacketJoinGame(int i, WorldSettings$GameType worldsettings$gametype, boolean flag, int j, EnumDifficulty enumdifficulty, int k, WorldType worldtype) {
        this.field_149206_a = i;
        this.field_149202_d = j;
        this.field_149203_e = enumdifficulty;
        this.field_149205_c = worldsettings$gametype;
        this.field_149200_f = k;
        this.field_149204_b = flag;
        this.field_149201_g = worldtype;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149206_a = packetbuffer.readInt();
        int i = packetbuffer.readUnsignedByte();

        this.field_149204_b = (i & 8) == 8;
        i &= -9;
        this.field_149205_c = WorldSettings$GameType.func_77146_a(i);
        this.field_149202_d = packetbuffer.readByte();
        this.field_149203_e = EnumDifficulty.func_151523_a(packetbuffer.readUnsignedByte());
        this.field_149200_f = packetbuffer.readUnsignedByte();
        this.field_149201_g = WorldType.func_77130_a(packetbuffer.func_150789_c(16));
        if (this.field_149201_g == null) {
            this.field_149201_g = WorldType.field_77137_b;
        }

    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149206_a);
        int i = this.field_149205_c.func_77148_a();

        if (this.field_149204_b) {
            i |= 8;
        }

        packetbuffer.writeByte(i);
        packetbuffer.writeByte(this.field_149202_d);
        packetbuffer.writeByte(this.field_149203_e.func_151525_a());
        packetbuffer.writeByte(this.field_149200_f);
        packetbuffer.func_150785_a(this.field_149201_g.func_77127_a());
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147282_a(this);
    }

    @Override
    public String func_148835_b() {
        return String.format("eid=%d, gameType=%d, hardcore=%b, dimension=%d, difficulty=%s, maxplayers=%d", this.field_149206_a, this.field_149205_c.func_77148_a(), this.field_149204_b, this.field_149202_d, this.field_149203_e, this.field_149200_f);
    }
}
