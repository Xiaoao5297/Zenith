package net.minecraft.world.storage;

import java.util.concurrent.Callable;

class WorldInfo$6 implements Callable {

    WorldInfo$6(WorldInfo worldinfo) {
        this.field_85115_a = worldinfo;
    }

    public String call() {
        return String.valueOf(WorldInfo.access$800(this.field_85115_a));
    }
}
