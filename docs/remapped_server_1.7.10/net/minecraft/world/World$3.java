package net.minecraft.world;

import java.util.concurrent.Callable;

class World$3 implements Callable {

    World$3(World world) {
        this.field_77440_a = world;
    }

    public String call() {
        return this.field_77440_a.field_73010_i.size() + " total; " + this.field_77440_a.field_73010_i.toString();
    }
}
