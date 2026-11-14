package net.minecraft.world.storage;

import java.util.concurrent.Callable;

class WorldInfo$7 implements Callable {

    WorldInfo$7(WorldInfo worldinfo) {
        this.field_85113_a = worldinfo;
    }

    public String call() {
        String s = "Unknown?";

        try {
            switch (WorldInfo.access$900(this.field_85113_a)) {
                case 19132:
                    s = "McRegion";
                    break;
                case 19133:
                    s = "Anvil";
            }
        } catch (Throwable throwable) {
            ;
        }

        return String.format("0x%05X - %s", WorldInfo.access$900(this.field_85113_a), s);
    }
}
