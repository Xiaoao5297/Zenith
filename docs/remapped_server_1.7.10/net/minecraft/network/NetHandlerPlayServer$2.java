package net.minecraft.network;

import java.util.concurrent.Callable;

class NetHandlerPlayServer$2 implements Callable {

    NetHandlerPlayServer$2(NetHandlerPlayServer nethandlerplayserver, Packet packet) {
        this.field_151286_b = nethandlerplayserver;
        this.field_151287_a = packet;
    }

    public String call() {
        return this.field_151287_a.getClass().getCanonicalName();
    }
}
