package net.minecraft.server;

import net.minecraft.server.dedicated.DedicatedServer;

public final class MinecraftServer$2 extends Thread {

    public MinecraftServer$2(String s, DedicatedServer dedicatedserver) {
        super(s);
        this.field_96244_a = dedicatedserver;
    }

    public void run() {
        this.field_96244_a.func_71260_j();
    }
}
