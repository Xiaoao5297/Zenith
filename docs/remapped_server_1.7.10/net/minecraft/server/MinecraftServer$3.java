package net.minecraft.server;

public class MinecraftServer$3 extends Thread {

    public MinecraftServer$3(MinecraftServer minecraftserver, String s) {
        super(s);
        this.field_73716_a = minecraftserver;
    }

    public void run() {
        this.field_73716_a.run();
    }
}
