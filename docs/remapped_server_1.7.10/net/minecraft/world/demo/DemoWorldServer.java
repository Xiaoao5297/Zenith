package net.minecraft.world.demo;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings$GameType;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveHandler;

public class DemoWorldServer extends WorldServer {

    private static final long field_73072_L = (long) "North Carolina".hashCode();
    public static final WorldSettings field_73071_a = (new WorldSettings(DemoWorldServer.field_73072_L, WorldSettings$GameType.SURVIVAL, true, false, WorldType.field_77137_b)).func_77159_a();

    public DemoWorldServer(MinecraftServer minecraftserver, ISaveHandler isavehandler, String s, int i, Profiler profiler) {
        super(minecraftserver, isavehandler, s, i, DemoWorldServer.field_73071_a, profiler);
    }
}
