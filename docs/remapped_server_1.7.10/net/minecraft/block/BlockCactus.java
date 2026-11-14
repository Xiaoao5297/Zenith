package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class BlockCactus extends Block {

    protected BlockCactus() {
        super(Material.field_151570_A);
        this.func_149675_a(true);
        this.func_149647_a(CreativeTabs.field_78031_c);
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        if (world.func_147437_c(i, j + 1, k)) {
            int l;

            for (l = 1; world.func_147439_a(i, j - l, k) == this; ++l) {
                ;
            }

            if (l < 3) {
                int i1 = world.func_72805_g(i, j, k);

                if (i1 == 15) {
                    world.func_147449_b(i, j + 1, k, this);
                    world.func_72921_c(i, j, k, 0, 4);
                    this.func_149695_a(world, i, j + 1, k, this);
                } else {
                    world.func_72921_c(i, j, k, i1 + 1, 4);
                }
            }
        }

    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        float f = 0.0625F;

        return AxisAlignedBB.func_72330_a((double) ((float) i + f), (double) j, (double) ((float) k + f), (double) ((float) (i + 1) - f), (double) ((float) (j + 1) - f), (double) ((float) (k + 1) - f));
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }

    @Override
    public int func_149645_b() {
        return 13;
    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        return !super.func_149742_c(world, i, j, k) ? false : this.func_149718_j(world, i, j, k);
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        if (!this.func_149718_j(world, i, j, k)) {
            world.func_147480_a(i, j, k, true);
        }

    }

    @Override
    public boolean func_149718_j(World world, int i, int j, int k) {
        if (world.func_147439_a(i - 1, j, k).func_149688_o().func_76220_a()) {
            return false;
        } else if (world.func_147439_a(i + 1, j, k).func_149688_o().func_76220_a()) {
            return false;
        } else if (world.func_147439_a(i, j, k - 1).func_149688_o().func_76220_a()) {
            return false;
        } else if (world.func_147439_a(i, j, k + 1).func_149688_o().func_76220_a()) {
            return false;
        } else {
            Block block = world.func_147439_a(i, j - 1, k);

            return block == Blocks.field_150434_aF || block == Blocks.field_150354_m;
        }
    }

    @Override
    public void func_149670_a(World world, int i, int j, int k, Entity entity) {
        entity.func_70097_a(DamageSource.field_76367_g, 1.0F);
    }
}
