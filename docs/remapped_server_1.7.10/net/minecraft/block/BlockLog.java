package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public abstract class BlockLog extends BlockRotatedPillar {

    public BlockLog() {
        super(Material.field_151575_d);
        this.func_149647_a(CreativeTabs.field_78030_b);
        this.func_149711_c(2.0F);
        this.func_149672_a(BlockLog.field_149766_f);
    }

    public static int func_150165_c(int i) {
        return i & 3;
    }

    @Override
    public int func_149745_a(Random random) {
        return 1;
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Item.func_150898_a(this);
    }

    @Override
    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {
        byte b0 = 4;
        int i1 = b0 + 1;

        if (world.func_72904_c(i - i1, j - i1, k - i1, i + i1, j + i1, k + i1)) {
            for (int j1 = -b0; j1 <= b0; ++j1) {
                for (int k1 = -b0; k1 <= b0; ++k1) {
                    for (int l1 = -b0; l1 <= b0; ++l1) {
                        if (world.func_147439_a(i + j1, j + k1, k + l1).func_149688_o() == Material.field_151584_j) {
                            int i2 = world.func_72805_g(i + j1, j + k1, k + l1);

                            if ((i2 & 8) == 0) {
                                world.func_72921_c(i + j1, j + k1, k + l1, i2 | 8, 4);
                            }
                        }
                    }
                }
            }
        }

    }
}
