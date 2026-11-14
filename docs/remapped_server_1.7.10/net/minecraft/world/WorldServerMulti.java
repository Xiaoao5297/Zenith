package net.minecraft.world;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.ISaveHandler;

public class WorldServerMulti extends WorldServer {

    public WorldServerMulti(MinecraftServer minecraftserver, ISaveHandler isavehandler, String s, int i, WorldSettings worldsettings, WorldServer worldserver, Profiler profiler) {
        super(minecraftserver, isavehandler, s, i, worldsettings, profiler);
        this.field_72988_C = worldserver.field_72988_C;
        this.field_96442_D = worldserver.func_96441_U();
        this.field_72986_A = new DerivedWorldInfo(worldserver.func_72912_H());
    }

    @Override
    protected void func_73042_a() {}
}
