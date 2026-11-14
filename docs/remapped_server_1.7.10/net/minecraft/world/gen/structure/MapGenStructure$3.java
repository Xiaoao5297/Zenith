package net.minecraft.world.gen.structure;

import java.util.concurrent.Callable;

class MapGenStructure$3 implements Callable {

    MapGenStructure$3(MapGenStructure mapgenstructure) {
        this.field_85161_a = mapgenstructure;
    }

    public String call() {
        return this.field_85161_a.getClass().getCanonicalName();
    }
}
