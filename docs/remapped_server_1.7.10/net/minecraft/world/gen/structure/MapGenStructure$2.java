package net.minecraft.world.gen.structure;

import java.util.concurrent.Callable;
import net.minecraft.world.ChunkCoordIntPair;

class MapGenStructure$2 implements Callable {

    MapGenStructure$2(MapGenStructure mapgenstructure, int i, int j) {
        this.field_85164_c = mapgenstructure;
        this.field_85165_a = i;
        this.field_85163_b = j;
    }

    public String call() {
        return String.valueOf(ChunkCoordIntPair.func_77272_a(this.field_85165_a, this.field_85163_b));
    }
}
