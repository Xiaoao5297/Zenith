package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLadder extends Block {

    protected BlockLadder() {
        super(Material.field_151594_q);
        this.func_149647_a(CreativeTabs.field_78031_c);
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        this.func_149719_a(world, i, j, k);
        return super.func_149668_a(world, i, j, k);
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        this.func_149797_b(iblockaccess.func_72805_g(i, j, k));
    }

    public void func_149797_b(int i) {
        float f = 0.125F;

        if (i == 2) {
            this.func_149676_a(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        }

        if (i == 3) {
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        }

        if (i == 4) {
            this.func_149676_a(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }

        if (i == 5) {
            this.func_149676_a(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        }

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
        return 8;
    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        return world.func_147439_a(i - 1, j, k).func_149721_r() ? true : (world.func_147439_a(i + 1, j, k).func_149721_r() ? true : (world.func_147439_a(i, j, k - 1).func_149721_r() ? true : world.func_147439_a(i, j, k + 1).func_149721_r()));
    }

    @Override
    public int func_149660_a(World world, int i, int j, int k, int l, float f, float f1, float f2, int i1) {
        int j1 = i1;

        if ((i1 == 0 || l == 2) && world.func_147439_a(i, j, k + 1).func_149721_r()) {
            j1 = 2;
        }

        if ((j1 == 0 || l == 3) && world.func_147439_a(i, j, k - 1).func_149721_r()) {
            j1 = 3;
        }

        if ((j1 == 0 || l == 4) && world.func_147439_a(i + 1, j, k).func_149721_r()) {
            j1 = 4;
        }

        if ((j1 == 0 || l == 5) && world.func_147439_a(i - 1, j, k).func_149721_r()) {
            j1 = 5;
        }

        return j1;
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        int l = world.func_72805_g(i, j, k);
        boolean flag = false;

        if (l == 2 && world.func_147439_a(i, j, k + 1).func_149721_r()) {
            flag = true;
        }

        if (l == 3 && world.func_147439_a(i, j, k - 1).func_149721_r()) {
            flag = true;
        }

        if (l == 4 && world.func_147439_a(i + 1, j, k).func_149721_r()) {
            flag = true;
        }

        if (l == 5 && world.func_147439_a(i - 1, j, k).func_149721_r()) {
            flag = true;
        }

        if (!flag) {
            this.func_149697_b(world, i, j, k, l, 0);
            world.func_147468_f(i, j, k);
        }

        super.func_149695_a(world, i, j, k, block);
    }

    @Override
    public int func_149745_a(Random random) {
        return 1;
    }
}
