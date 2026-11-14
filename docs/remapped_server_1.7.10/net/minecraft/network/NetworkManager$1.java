package net.minecraft.network;

import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.GenericFutureListener;

class NetworkManager$1 implements Runnable {

    NetworkManager$1(NetworkManager networkmanager, EnumConnectionState enumconnectionstate, EnumConnectionState enumconnectionstate1, Packet packet, GenericFutureListener[] agenericfuturelistener) {
        this.field_150714_e = networkmanager;
        this.field_150717_a = enumconnectionstate;
        this.field_150715_b = enumconnectionstate1;
        this.field_150716_c = packet;
        this.field_150713_d = agenericfuturelistener;
    }

    public void run() {
        if (this.field_150717_a != this.field_150715_b) {
            this.field_150714_e.func_150723_a(this.field_150717_a);
        }

        NetworkManager.access$000(this.field_150714_e).writeAndFlush(this.field_150716_c).addListeners(this.field_150713_d).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }
}
