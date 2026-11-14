package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BlockStaticLiquid extends BlockLiquid {

    protected BlockStaticLiquid(Material material) {
        super(material);
        this.func_149675_a(false);
        if (material == Material.field_151587_i) {
            this.func_149675_a(true);
        }

    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        super.func_149695_a(world, i, j, k, block);
        if (world.func_147439_a(i, j, k) == this) {
            this.func_149818_n(world, i, j, k);
        }

    }

    private void func_149818_n(World world, int i, int j, int k) {
        int l = world.func_72805_g(i, j, k);

        world.func_147465_d(i, j, k, Block.func_149729_e(Block.func_149682_b(this) - 1), l, 2);
        world.func_147464_a(i, j, k, Block.func_149729_e(Block.func_149682_b(this) - 1), this.func_149738_a(world));
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        if (this.field_149764_J == Material.field_151587_i) {
            int l = random.nextInt(3);
            int i1 = 0;

            while (true) {
                if (i1 >= l) {
                    if (l == 0) {
                        i1 = i;
                        int j1 = k;

                        for (int k1 = 0; k1 < 3; ++k1) {
                            i = i1 + random.nextInt(3) - 1;
                            k = j1 + random.nextInt(3) - 1;
                            if (world.func_147437_c(i, j + 1, k) && this.func_149817_o(world, i, j, k)) {
                                world.func_147449_b(i, j + 1, k, Blocks.field_150480_ab);
                            }
                        }
                    }
                    break;
                }

                i += random.nextInt(3) - 1;
                ++j;
                k += random.nextInt(3) - 1;
                Block block = world.func_147439_a(i, j, k);

                if (block.field_149764_J == Material.field_151579_a) {
                    if (this.func_149817_o(world, i - 1, j, k) || this.func_149817_o(world, i + 1, j, k) || this.func_149817_o(world, i, j, k - 1) || this.func_149817_o(world, i, j, k + 1) || this.func_149817_o(world, i, j - 1, k) || this.func_149817_o(world, i, j + 1, k)) {
                        world.func_147449_b(i, j, k, Blocks.field_150480_ab);
                        return;
                    }
                } else if (block.field_149764_J.func_76230_c()) {
                    return;
                }

                ++i1;
            }
        }

    }

    private boolean func_149817_o(World world, int i, int j, int k) {
        return world.func_147439_a(i, j, k).func_149688_o().func_76217_h();
    }
}
