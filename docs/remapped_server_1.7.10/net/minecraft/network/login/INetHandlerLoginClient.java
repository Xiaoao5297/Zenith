package net.minecraft.network.login;

import net.minecraft.network.INetHandler;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;

public interface INetHandlerLoginClient extends INetHandler {

    void func_147389_a(S01PacketEncryptionRequest s01packetencryptionrequest);

    void func_147390_a(S02PacketLoginSuccess s02packetloginsuccess);

    void func_147388_a(S00PacketDisconnect s00packetdisconnect);
}
