package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockBush extends Block {

    protected BlockBush(Material material) {
        super(material);
        this.func_149675_a(true);
        float f = 0.2F;

        this.func_149676_a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 3.0F, 0.5F + f);
        this.func_149647_a(CreativeTabs.field_78031_c);
    }

    protected BlockBush() {
        this(Material.field_151585_k);
    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        return super.func_149742_c(world, i, j, k) && this.func_149854_a(world.func_147439_a(i, j - 1, k));
    }

    protected boolean func_149854_a(Block block) {
        return block == Blocks.field_150349_c || block == Blocks.field_150346_d || block == Blocks.field_150458_ak;
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        super.func_149695_a(world, i, j, k, block);
        this.func_149855_e(world, i, j, k);
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        this.func_149855_e(world, i, j, k);
    }

    protected void func_149855_e(World world, int i, int j, int k) {
        if (!this.func_149718_j(world, i, j, k)) {
            this.func_149697_b(world, i, j, k, world.func_72805_g(i, j, k), 0);
            world.func_147465_d(i, j, k, func_149729_e(0), 0, 2);
        }

    }

    @Override
    public boolean func_149718_j(World world, int i, int j, int k) {
        return this.func_149854_a(world.func_147439_a(i, j - 1, k));
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        return null;
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    @Override
    public int func_149645_b() {
        return 1;
    }
}
