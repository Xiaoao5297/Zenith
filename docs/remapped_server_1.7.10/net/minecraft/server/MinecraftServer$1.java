package net.minecraft.server;

import net.minecraft.util.IProgressUpdate;

public class MinecraftServer$1 implements IProgressUpdate {

    private long field_96245_b;

    public MinecraftServer$1(MinecraftServer minecraftserver) {
        this.field_74267_a = minecraftserver;
        this.field_96245_b = MinecraftServer.func_130071_aq();
    }

    @Override
    public void func_73720_a(String s) {}

    @Override
    public void func_73718_a(int i) {
        if (MinecraftServer.func_130071_aq() - this.field_96245_b >= 1000L) {
            this.field_96245_b = MinecraftServer.func_130071_aq();
            MinecraftServer.access$000().info("Converting... " + i + "%");
        }

    }

    @Override
    public void func_73719_c(String s) {}
}
