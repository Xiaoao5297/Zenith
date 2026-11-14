package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

public class MapGenBase {

    protected int field_75040_a = 8;
    protected Random field_75038_b = new Random();
    protected World field_75039_c;

    public MapGenBase() {}

    public void func_151539_a(IChunkProvider ichunkprovider, World world, int i, int j, Block[] ablock) {
        int k = this.field_75040_a;

        this.field_75039_c = world;
        this.field_75038_b.setSeed(world.func_72905_C());
        long l = this.field_75038_b.nextLong();
        long i1 = this.field_75038_b.nextLong();

        for (int j1 = i - k; j1 <= i + k; ++j1) {
            for (int k1 = j - k; k1 <= j + k; ++k1) {
                long l1 = (long) j1 * l;
                long i2 = (long) k1 * i1;

                this.field_75038_b.setSeed(l1 ^ i2 ^ world.func_72905_C());
                this.func_151538_a(world, j1, k1, i, j, ablock);
            }
        }

    }

    protected void func_151538_a(World world, int i, int j, int k, int l, Block[] ablock) {}
}
