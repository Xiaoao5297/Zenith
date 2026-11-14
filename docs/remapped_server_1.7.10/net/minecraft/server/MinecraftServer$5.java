package net.minecraft.server;

import java.util.concurrent.Callable;

public class MinecraftServer$5 implements Callable {

    public MinecraftServer$5(MinecraftServer minecraftserver) {
        this.field_74272_a = minecraftserver;
    }

    public String call() {
        byte b0 = 0;
        int i = 56 * b0;
        int j = i / 1024 / 1024;
        byte b1 = 0;
        int k = 56 * b1;
        int l = k / 1024 / 1024;

        return b0 + " (" + i + " bytes; " + j + " MB) allocated, " + b1 + " (" + k + " bytes; " + l + " MB) used";
    }
}
