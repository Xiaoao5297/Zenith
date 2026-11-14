package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IChatComponent$Serializer;

public class S02PacketChat extends Packet {

    private IChatComponent field_148919_a;
    private boolean field_148918_b;

    public S02PacketChat() {
        this.field_148918_b = true;
    }

    public S02PacketChat(IChatComponent ichatcomponent) {
        this(ichatcomponent, true);
    }

    public S02PacketChat(IChatComponent ichatcomponent, boolean flag) {
        this.field_148918_b = true;
        this.field_148919_a = ichatcomponent;
        this.field_148918_b = flag;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_148919_a = IChatComponent$Serializer.func_150699_a(packetbuffer.func_150789_c(32767));
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.func_150785_a(IChatComponent$Serializer.func_150696_a(this.field_148919_a));
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147251_a(this);
    }

    @Override
    public String func_148835_b() {
        return String.format("message='%s'", this.field_148919_a);
    }

    public boolean func_148916_d() {
        return this.field_148918_b;
    }
}
