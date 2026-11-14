package net.minecraft.network.login.client;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginServer;

public class C00PacketLoginStart extends Packet {

    private GameProfile field_149305_a;

    public C00PacketLoginStart() {}

    public C00PacketLoginStart(GameProfile gameprofile) {
        this.field_149305_a = gameprofile;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149305_a = new GameProfile((UUID) null, packetbuffer.func_150789_c(16));
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.func_150785_a(this.field_149305_a.getName());
    }

    public void func_148833_a(INetHandlerLoginServer inethandlerloginserver) {
        inethandlerloginserver.func_147316_a(this);
    }

    public GameProfile func_149304_c() {
        return this.field_149305_a;
    }
}
