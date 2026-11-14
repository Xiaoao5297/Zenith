package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockGrass extends Block implements IGrowable {

    private static final Logger field_149992_a = LogManager.getLogger();

    protected BlockGrass() {
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
                        world.func_147449_b(i1, j1, k1, Blocks.field_150349_c);
                    }
                }
            }

        }
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Blocks.field_150346_d.func_149650_a(0, random, j);
    }

    @Override
    public boolean func_149851_a(World world, int i, int j, int k, boolean flag) {
        return true;
    }

    @Override
    public boolean func_149852_a(World world, Random random, int i, int j, int k) {
        return true;
    }

    @Override
    public void func_149853_b(World world, Random random, int i, int j, int k) {
        int l = 0;

        while (l < 128) {
            int i1 = i;
            int j1 = j + 1;
            int k1 = k;
            int l1 = 0;

            while (true) {
                if (l1 < l / 16) {
                    i1 += random.nextInt(3) - 1;
                    j1 += (random.nextInt(3) - 1) * random.nextInt(3) / 2;
                    k1 += random.nextInt(3) - 1;
                    if (world.func_147439_a(i1, j1 - 1, k1) == Blocks.field_150349_c && !world.func_147439_a(i1, j1, k1).func_149721_r()) {
                        ++l1;
                        continue;
                    }
                } else if (world.func_147439_a(i1, j1, k1).field_149764_J == Material.field_151579_a) {
                    if (random.nextInt(8) != 0) {
                        if (Blocks.field_150329_H.func_149718_j(world, i1, j1, k1)) {
                            world.func_147465_d(i1, j1, k1, Blocks.field_150329_H, 1, 3);
                        }
                    } else {
                        String s = world.func_72807_a(i1, k1).func_150572_a(random, i1, j1, k1);

                        BlockGrass.field_149992_a.debug("Flower in " + world.func_72807_a(i1, k1).field_76791_y + ": " + s);
                        BlockFlower blockflower = BlockFlower.func_149857_e(s);

                        if (blockflower != null && blockflower.func_149718_j(world, i1, j1, k1)) {
                            int i2 = BlockFlower.func_149856_f(s);

                            world.func_147465_d(i1, j1, k1, blockflower, i2, 3);
                        }
                    }
                }

                ++l;
                break;
            }
        }

    }
}
