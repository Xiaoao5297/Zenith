package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S2BPacketChangeGameState extends Packet {

    public static final String[] field_149142_a = new String[]{"tile.bed.notValid", null, null, "gameMode.changed"};
    private int field_149140_b;
    private float field_149141_c;

    public S2BPacketChangeGameState() {}

    public S2BPacketChangeGameState(int i, float f) {
        this.field_149140_b = i;
        this.field_149141_c = f;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149140_b = packetbuffer.readUnsignedByte();
        this.field_149141_c = packetbuffer.readFloat();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeByte(this.field_149140_b);
        packetbuffer.writeFloat(this.field_149141_c);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147252_a(this);
    }
}
