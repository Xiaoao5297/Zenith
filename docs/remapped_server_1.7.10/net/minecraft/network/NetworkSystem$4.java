package net.minecraft.network;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.util.ChatComponentText;

class NetworkSystem$4 implements GenericFutureListener {

    NetworkSystem$4(NetworkSystem networksystem, NetworkManager networkmanager, ChatComponentText chatcomponenttext) {
        this.field_151283_c = networksystem;
        this.field_151284_a = networkmanager;
        this.field_151282_b = chatcomponenttext;
    }

    @Override
    public void operationComplete(Future future) {
        this.field_151284_a.func_150718_a(this.field_151282_b);
    }
}
