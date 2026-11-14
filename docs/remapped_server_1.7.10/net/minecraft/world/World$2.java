package net.minecraft.world;

import java.util.concurrent.Callable;
import net.minecraft.block.Block;

class World$2 implements Callable {

    World$2(World world, Block block) {
        this.field_77405_a = world;
        this.field_151300_a = block;
    }

    public String call() {
        try {
            return String.format("ID #%d (%s // %s)", Block.func_149682_b(this.field_151300_a), this.field_151300_a.func_149739_a(), this.field_151300_a.getClass().getCanonicalName());
        } catch (Throwable throwable) {
            return "ID #" + Block.func_149682_b(this.field_151300_a);
        }
    }
}
