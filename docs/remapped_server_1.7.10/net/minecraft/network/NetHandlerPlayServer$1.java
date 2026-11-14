package net.minecraft.network;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.util.ChatComponentText;

class NetHandlerPlayServer$1 implements GenericFutureListener {

    NetHandlerPlayServer$1(NetHandlerPlayServer nethandlerplayserver, ChatComponentText chatcomponenttext) {
        this.field_151288_b = nethandlerplayserver;
        this.field_151289_a = chatcomponenttext;
    }

    @Override
    public void operationComplete(Future future) {
        this.field_151288_b.field_147371_a.func_150718_a(this.field_151289_a);
    }
}
