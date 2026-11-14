package net.minecraft.network.play.server;

import java.util.List;
import net.minecraft.entity.DataWatcher;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S1CPacketEntityMetadata extends Packet {

    private int field_149379_a;
    private List field_149378_b;

    public S1CPacketEntityMetadata() {}

    public S1CPacketEntityMetadata(int i, DataWatcher datawatcher, boolean flag) {
        this.field_149379_a = i;
        if (flag) {
            this.field_149378_b = datawatcher.func_75685_c();
        } else {
            this.field_149378_b = datawatcher.func_75688_b();
        }

    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149379_a = packetbuffer.readInt();
        this.field_149378_b = DataWatcher.func_151508_b(packetbuffer);
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149379_a);
        DataWatcher.func_151507_a(this.field_149378_b, packetbuffer);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147284_a(this);
    }
}
