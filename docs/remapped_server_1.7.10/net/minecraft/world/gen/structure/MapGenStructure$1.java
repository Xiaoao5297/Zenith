package net.minecraft.world.gen.structure;

import java.util.concurrent.Callable;

class MapGenStructure$1 implements Callable {

    MapGenStructure$1(MapGenStructure mapgenstructure, int i, int j) {
        this.field_85168_c = mapgenstructure;
        this.field_85169_a = i;
        this.field_85167_b = j;
    }

    public String call() {
        return this.field_85168_c.func_75047_a(this.field_85169_a, this.field_85167_b) ? "True" : "False";
    }
}
