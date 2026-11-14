package net.minecraft.network.login.server;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;

public class S02PacketLoginSuccess extends Packet {

    private GameProfile field_149602_a;

    public S02PacketLoginSuccess() {}

    public S02PacketLoginSuccess(GameProfile gameprofile) {
        this.field_149602_a = gameprofile;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        String s = packetbuffer.func_150789_c(36);
        String s1 = packetbuffer.func_150789_c(16);
        UUID uuid = UUID.fromString(s);

        this.field_149602_a = new GameProfile(uuid, s1);
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        UUID uuid = this.field_149602_a.getId();

        packetbuffer.func_150785_a(uuid == null ? "" : uuid.toString());
        packetbuffer.func_150785_a(this.field_149602_a.getName());
    }

    public void func_148833_a(INetHandlerLoginClient inethandlerloginclient) {
        inethandlerloginclient.func_147390_a(this);
    }

    @Override
    public boolean func_148836_a() {
        return true;
    }
}
