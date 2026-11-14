package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public abstract class WorldGenerator {

    private final boolean field_76488_a;

    public WorldGenerator() {
        this.field_76488_a = false;
    }

    public WorldGenerator(boolean flag) {
        this.field_76488_a = flag;
    }

    public abstract boolean func_76484_a(World world, Random random, int i, int j, int k);

    public void func_76487_a(double d0, double d1, double d2) {}

    protected void func_150515_a(World world, int i, int j, int k, Block block) {
        this.func_150516_a(world, i, j, k, block, 0);
    }

    protected void func_150516_a(World world, int i, int j, int k, Block block, int l) {
        if (this.field_76488_a) {
            world.func_147465_d(i, j, k, block, l, 3);
        } else {
            world.func_147465_d(i, j, k, block, l, 2);
        }

    }
}
