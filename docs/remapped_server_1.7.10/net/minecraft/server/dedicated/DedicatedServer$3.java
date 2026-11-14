package net.minecraft.server.dedicated;

import java.util.concurrent.Callable;

class DedicatedServer$3 implements Callable {

    DedicatedServer$3(DedicatedServer dedicatedserver) {
        this.field_71743_a = dedicatedserver;
    }

    public String call() {
        String s = this.field_71743_a.getServerModName();

        return !s.equals("vanilla") ? "Definitely; Server brand changed to '" + s + "'" : "Unknown (can't tell)";
    }
}
