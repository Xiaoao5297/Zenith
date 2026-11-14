package net.minecraft.server;

import java.util.concurrent.Callable;

public class MinecraftServer$4 implements Callable {

    public MinecraftServer$4(MinecraftServer minecraftserver) {
        this.field_74274_a = minecraftserver;
    }

    public String call() {
        return this.field_74274_a.field_71304_b.field_76327_a ? this.field_74274_a.field_71304_b.func_76322_c() : "N/A (disabled)";
    }
}
