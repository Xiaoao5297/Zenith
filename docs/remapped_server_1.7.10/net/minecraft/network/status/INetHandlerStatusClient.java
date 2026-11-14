package net.minecraft.network.status;

import net.minecraft.network.INetHandler;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;

public interface INetHandlerStatusClient extends INetHandler {

    void func_147397_a(S00PacketServerInfo s00packetserverinfo);

    void func_147398_a(S01PacketPong s01packetpong);
}
