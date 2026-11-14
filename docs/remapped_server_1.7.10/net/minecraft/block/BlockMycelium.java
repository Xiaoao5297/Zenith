package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class BlockMycelium extends Block {

    protected BlockMycelium() {
        super(Material.field_151577_b);
        this.func_149675_a(true);
        this.func_149647_a(CreativeTabs.field_78030_b);
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        if (!world.field_72995_K) {
            if (world.func_72957_l(i, j + 1, k) < 4 && world.func_147439_a(i, j + 1, k).func_149717_k() > 2) {
                world.func_147449_b(i, j, k, Blocks.field_150346_d);
            } else if (world.func_72957_l(i, j + 1, k) >= 9) {
                for (int l = 0; l < 4; ++l) {
                    int i1 = i + random.nextInt(3) - 1;
                    int j1 = j + random.nextInt(5) - 3;
                    int k1 = k + random.nextInt(3) - 1;
                    Block block = world.func_147439_a(i1, j1 + 1, k1);

                    if (world.func_147439_a(i1, j1, k1) == Blocks.field_150346_d && world.func_72805_g(i1, j1, k1) == 0 && world.func_72957_l(i1, j1 + 1, k1) >= 4 && block.func_149717_k() <= 2) {
                        world.func_147449_b(i1, j1, k1, this);
                    }
                }
            }

        }
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Blocks.field_150346_d.func_149650_a(0, random, j);
    }
}
