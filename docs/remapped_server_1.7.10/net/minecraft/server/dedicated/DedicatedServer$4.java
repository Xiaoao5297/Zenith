package net.minecraft.server.dedicated;

import java.util.concurrent.Callable;

class DedicatedServer$4 implements Callable {

    DedicatedServer$4(DedicatedServer dedicatedserver) {
        this.field_85171_a = dedicatedserver;
    }

    public String call() {
        return "Dedicated Server (map_server.txt)";
    }
}
