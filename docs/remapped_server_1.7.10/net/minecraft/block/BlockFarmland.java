package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockFarmland extends Block {

    protected BlockFarmland() {
        super(Material.field_151578_c);
        this.func_149675_a(true);
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
        this.func_149713_g(255);
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        return AxisAlignedBB.func_72330_a((double) (i + 0), (double) (j + 0), (double) (k + 0), (double) (i + 1), (double) (j + 1), (double) (k + 1));
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
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        if (!this.func_149821_m(world, i, j, k) && !world.func_72951_B(i, j + 1, k)) {
            int l = world.func_72805_g(i, j, k);

            if (l > 0) {
                world.func_72921_c(i, j, k, l - 1, 2);
            } else if (!this.func_149822_e(world, i, j, k)) {
                world.func_147449_b(i, j, k, Blocks.field_150346_d);
            }
        } else {
            world.func_72921_c(i, j, k, 7, 2);
        }

    }

    @Override
    public void func_149746_a(World world, int i, int j, int k, Entity entity, float f) {
        if (!world.field_72995_K && world.field_73012_v.nextFloat() < f - 0.5F) {
            if (!(entity instanceof EntityPlayer) && !world.func_82736_K().func_82766_b("mobGriefing")) {
                return;
            }

            world.func_147449_b(i, j, k, Blocks.field_150346_d);
        }

    }

    private boolean func_149822_e(World world, int i, int j, int k) {
        byte b0 = 0;

        for (int l = i - b0; l <= i + b0; ++l) {
            for (int i1 = k - b0; i1 <= k + b0; ++i1) {
                Block block = world.func_147439_a(l, j + 1, i1);

                if (block == Blocks.field_150464_aj || block == Blocks.field_150394_bc || block == Blocks.field_150393_bb || block == Blocks.field_150469_bN || block == Blocks.field_150459_bM) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean func_149821_m(World world, int i, int j, int k) {
        for (int l = i - 4; l <= i + 4; ++l) {
            for (int i1 = j; i1 <= j + 1; ++i1) {
                for (int j1 = k - 4; j1 <= k + 4; ++j1) {
                    if (world.func_147439_a(l, i1, j1).func_149688_o() == Material.field_151586_h) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        super.func_149695_a(world, i, j, k, block);
        Material material = world.func_147439_a(i, j + 1, k).func_149688_o();

        if (material.func_76220_a()) {
            world.func_147449_b(i, j, k, Blocks.field_150346_d);
        }

    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Blocks.field_150346_d.func_149650_a(0, random, j);
    }
}
