package net.minecraft.server.network;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class NetHandlerHandshakeTCP implements INetHandlerHandshakeServer {

    private final MinecraftServer field_147387_a;
    private final NetworkManager field_147386_b;

    public NetHandlerHandshakeTCP(MinecraftServer minecraftserver, NetworkManager networkmanager) {
        this.field_147387_a = minecraftserver;
        this.field_147386_b = networkmanager;
    }

    @Override
    public void func_147383_a(C00Handshake c00handshake) {
        switch (c00handshake.func_149594_c()) {
            case LOGIN:
                this.field_147386_b.func_150723_a(EnumConnectionState.LOGIN);
                ChatComponentText chatcomponenttext;

                if (c00handshake.func_149595_d() > 5) {
                    chatcomponenttext = new ChatComponentText("Outdated server! I'm still on 1.7.10");
                    this.field_147386_b.func_150725_a(new S00PacketDisconnect(chatcomponenttext));
                    this.field_147386_b.func_150718_a(chatcomponenttext);
                } else if (c00handshake.func_149595_d() < 5) {
                    chatcomponenttext = new ChatComponentText("Outdated client! Please use 1.7.10");
                    this.field_147386_b.func_150725_a(new S00PacketDisconnect(chatcomponenttext));
                    this.field_147386_b.func_150718_a(chatcomponenttext);
                } else {
                    this.field_147386_b.func_150719_a(new NetHandlerLoginServer(this.field_147387_a, this.field_147386_b));
                }
                break;
            case STATUS:
                this.field_147386_b.func_150723_a(EnumConnectionState.STATUS);
                this.field_147386_b.func_150719_a(new NetHandlerStatusServer(this.field_147387_a, this.field_147386_b));
                break;
            default:
                throw new UnsupportedOperationException("Invalid intention " + c00handshake.func_149594_c());
        }

    }

    @Override
    public void func_147231_a(IChatComponent ichatcomponent) {}

    @Override
    public void func_147232_a(EnumConnectionState enumconnectionstate, EnumConnectionState enumconnectionstate1) {
        if (enumconnectionstate1 != EnumConnectionState.LOGIN && enumconnectionstate1 != EnumConnectionState.STATUS) {
            throw new UnsupportedOperationException("Invalid state " + enumconnectionstate1);
        }
    }

    @Override
    public void func_147233_a() {}
}
