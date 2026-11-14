package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenLiquids extends WorldGenerator {

    private Block field_150521_a;

    public WorldGenLiquids(Block block) {
        this.field_150521_a = block;
    }

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        if (world.func_147439_a(i, j + 1, k) != Blocks.field_150348_b) {
            return false;
        } else if (world.func_147439_a(i, j - 1, k) != Blocks.field_150348_b) {
            return false;
        } else if (world.func_147439_a(i, j, k).func_149688_o() != Material.field_151579_a && world.func_147439_a(i, j, k) != Blocks.field_150348_b) {
            return false;
        } else {
            int l = 0;

            if (world.func_147439_a(i - 1, j, k) == Blocks.field_150348_b) {
                ++l;
            }

            if (world.func_147439_a(i + 1, j, k) == Blocks.field_150348_b) {
                ++l;
            }

            if (world.func_147439_a(i, j, k - 1) == Blocks.field_150348_b) {
                ++l;
            }

            if (world.func_147439_a(i, j, k + 1) == Blocks.field_150348_b) {
                ++l;
            }

            int i1 = 0;

            if (world.func_147437_c(i - 1, j, k)) {
                ++i1;
            }

            if (world.func_147437_c(i + 1, j, k)) {
                ++i1;
            }

            if (world.func_147437_c(i, j, k - 1)) {
                ++i1;
            }

            if (world.func_147437_c(i, j, k + 1)) {
                ++i1;
            }

            if (l == 3 && i1 == 1) {
                world.func_147465_d(i, j, k, this.field_150521_a, 0, 2);
                world.field_72999_e = true;
                this.field_150521_a.func_149674_a(world, i, j, k, random);
                world.field_72999_e = false;
            }

            return true;
        }
    }
}
