package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public abstract class WorldGenAbstractTree extends WorldGenerator {

    public WorldGenAbstractTree(boolean flag) {
        super(flag);
    }

    protected boolean func_150523_a(Block block) {
        return block.func_149688_o() == Material.field_151579_a || block.func_149688_o() == Material.field_151584_j || block == Blocks.field_150349_c || block == Blocks.field_150346_d || block == Blocks.field_150364_r || block == Blocks.field_150363_s || block == Blocks.field_150345_g || block == Blocks.field_150395_bd;
    }

    public void func_150524_b(World world, Random random, int i, int j, int k) {}
}
