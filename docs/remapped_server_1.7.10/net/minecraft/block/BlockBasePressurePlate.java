package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockBasePressurePlate extends Block {

    private String field_150067_a;

    protected BlockBasePressurePlate(String s, Material material) {
        super(material);
        this.field_150067_a = s;
        this.func_149647_a(CreativeTabs.field_78028_d);
        this.func_149675_a(true);
        this.func_150063_b(this.func_150066_d(15));
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        this.func_150063_b(iblockaccess.func_72805_g(i, j, k));
    }

    protected void func_150063_b(int i) {
        boolean flag = this.func_150060_c(i) > 0;
        float f = 0.0625F;

        if (flag) {
            this.func_149676_a(f, 0.0F, f, 1.0F - f, 0.03125F, 1.0F - f);
        } else {
            this.func_149676_a(f, 0.0F, f, 1.0F - f, 0.0625F, 1.0F - f);
        }

    }

    @Override
    public int func_149738_a(World world) {
        return 20;
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
    public boolean func_149655_b(IBlockAccess iblockaccess, int i, int j, int k) {
        return true;
    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        return World.func_147466_a(world, i, j - 1, k) || BlockFence.func_149825_a(world.func_147439_a(i, j - 1, k));
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        boolean flag = false;

        if (!World.func_147466_a(world, i, j - 1, k) && !BlockFence.func_149825_a(world.func_147439_a(i, j - 1, k))) {
            flag = true;
        }

        if (flag) {
            this.func_149697_b(world, i, j, k, world.func_72805_g(i, j, k), 0);
            world.func_147468_f(i, j, k);
        }

    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        if (!world.field_72995_K) {
            int l = this.func_150060_c(world.func_72805_g(i, j, k));

            if (l > 0) {
                this.func_150062_a(world, i, j, k, l);
            }

        }
    }

    @Override
    public void func_149670_a(World world, int i, int j, int k, Entity entity) {
        if (!world.field_72995_K) {
            int l = this.func_150060_c(world.func_72805_g(i, j, k));

            if (l == 0) {
                this.func_150062_a(world, i, j, k, l);
            }

        }
    }

    protected void func_150062_a(World world, int i, int j, int k, int l) {
        int i1 = this.func_150065_e(world, i, j, k);
        boolean flag = l > 0;
        boolean flag1 = i1 > 0;

        if (l != i1) {
            world.func_72921_c(i, j, k, this.func_150066_d(i1), 2);
            this.func_150064_a_(world, i, j, k);
            world.func_147458_c(i, j, k, i, j, k);
        }

        if (!flag1 && flag) {
            world.func_72908_a((double) i + 0.5D, (double) j + 0.1D, (double) k + 0.5D, "random.click", 0.3F, 0.5F);
        } else if (flag1 && !flag) {
            world.func_72908_a((double) i + 0.5D, (double) j + 0.1D, (double) k + 0.5D, "random.click", 0.3F, 0.6F);
        }

        if (flag1) {
            world.func_147464_a(i, j, k, this, this.func_149738_a(world));
        }

    }

    protected AxisAlignedBB func_150061_a(int i, int j, int k) {
        float f = 0.125F;

        return AxisAlignedBB.func_72330_a((double) ((float) i + f), (double) j, (double) ((float) k + f), (double) ((float) (i + 1) - f), (double) j + 0.25D, (double) ((float) (k + 1) - f));
    }

    @Override
    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {
        if (this.func_150060_c(l) > 0) {
            this.func_150064_a_(world, i, j, k);
        }

        super.func_149749_a(world, i, j, k, block, l);
    }

    protected void func_150064_a_(World world, int i, int j, int k) {
        world.func_147459_d(i, j, k, this);
        world.func_147459_d(i, j - 1, k, this);
    }

    @Override
    public int func_149709_b(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return this.func_150060_c(iblockaccess.func_72805_g(i, j, k));
    }

    @Override
    public int func_149748_c(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return l == 1 ? this.func_150060_c(iblockaccess.func_72805_g(i, j, k)) : 0;
    }

    @Override
    public boolean func_149744_f() {
        return true;
    }

    @Override
    public void func_149683_g() {
        float f = 0.5F;
        float f1 = 0.125F;
        float f2 = 0.5F;

        this.func_149676_a(0.5F - f, 0.5F - f1, 0.5F - f2, 0.5F + f, 0.5F + f1, 0.5F + f2);
    }

    @Override
    public int func_149656_h() {
        return 1;
    }

    protected abstract int func_150065_e(World world, int i, int j, int k);

    protected abstract int func_150060_c(int i);

    protected abstract int func_150066_d(int i);
}
