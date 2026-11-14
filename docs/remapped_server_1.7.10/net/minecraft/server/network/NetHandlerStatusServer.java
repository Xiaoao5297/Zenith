package net.minecraft.server.network;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.status.INetHandlerStatusServer;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;

public class NetHandlerStatusServer implements INetHandlerStatusServer {

    private final MinecraftServer field_147314_a;
    private final NetworkManager field_147313_b;

    public NetHandlerStatusServer(MinecraftServer minecraftserver, NetworkManager networkmanager) {
        this.field_147314_a = minecraftserver;
        this.field_147313_b = networkmanager;
    }

    @Override
    public void func_147231_a(IChatComponent ichatcomponent) {}

    @Override
    public void func_147232_a(EnumConnectionState enumconnectionstate, EnumConnectionState enumconnectionstate1) {
        if (enumconnectionstate1 != EnumConnectionState.STATUS) {
            throw new UnsupportedOperationException("Unexpected change in protocol to " + enumconnectionstate1);
        }
    }

    @Override
    public void func_147233_a() {}

    @Override
    public void func_147312_a(C00PacketServerQuery c00packetserverquery) {
        this.field_147313_b.func_150725_a(new S00PacketServerInfo(this.field_147314_a.func_147134_at()));
    }

    @Override
    public void func_147311_a(C01PacketPing c01packetping) {
        this.field_147313_b.func_150725_a(new S01PacketPong(c01packetping.func_149289_c()));
    }
}
