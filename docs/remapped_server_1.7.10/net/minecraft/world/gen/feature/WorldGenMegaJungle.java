package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMegaJungle extends WorldGenHugeTrees {

    public WorldGenMegaJungle(boolean flag, int i, int j, int k, int l) {
        super(flag, i, j, k, l);
    }

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        int l = this.func_150533_a(random);

        if (!this.func_150537_a(world, random, i, j, k, l)) {
            return false;
        } else {
            this.func_150543_c(world, i, k, j + l, 2, random);

            for (int i1 = j + l - 2 - random.nextInt(4); i1 > j + l / 2; i1 -= 2 + random.nextInt(4)) {
                float f = random.nextFloat() * 3.1415927F * 2.0F;
                int j1 = i + (int) (0.5F + MathHelper.func_76134_b(f) * 4.0F);
                int k1 = k + (int) (0.5F + MathHelper.func_76126_a(f) * 4.0F);

                int l1;

                for (l1 = 0; l1 < 5; ++l1) {
                    j1 = i + (int) (1.5F + MathHelper.func_76134_b(f) * (float) l1);
                    k1 = k + (int) (1.5F + MathHelper.func_76126_a(f) * (float) l1);
                    this.func_150516_a(world, j1, i1 - 3 + l1 / 2, k1, Blocks.field_150364_r, this.field_76520_b);
                }

                l1 = 1 + random.nextInt(2);
                int i2 = i1;

                for (int j2 = i1 - l1; j2 <= i2; ++j2) {
                    int k2 = j2 - i2;

                    this.func_150534_b(world, j1, j2, k1, 1 - k2, random);
                }
            }

            for (int l2 = 0; l2 < l; ++l2) {
                Block block = world.func_147439_a(i, j + l2, k);

                if (block.func_149688_o() == Material.field_151579_a || block.func_149688_o() == Material.field_151584_j) {
                    this.func_150516_a(world, i, j + l2, k, Blocks.field_150364_r, this.field_76520_b);
                    if (l2 > 0) {
                        if (random.nextInt(3) > 0 && world.func_147437_c(i - 1, j + l2, k)) {
                            this.func_150516_a(world, i - 1, j + l2, k, Blocks.field_150395_bd, 8);
                        }

                        if (random.nextInt(3) > 0 && world.func_147437_c(i, j + l2, k - 1)) {
                            this.func_150516_a(world, i, j + l2, k - 1, Blocks.field_150395_bd, 1);
                        }
                    }
                }

                if (l2 < l - 1) {
                    block = world.func_147439_a(i + 1, j + l2, k);
                    if (block.func_149688_o() == Material.field_151579_a || block.func_149688_o() == Material.field_151584_j) {
                        this.func_150516_a(world, i + 1, j + l2, k, Blocks.field_150364_r, this.field_76520_b);
                        if (l2 > 0) {
                            if (random.nextInt(3) > 0 && world.func_147437_c(i + 2, j + l2, k)) {
                                this.func_150516_a(world, i + 2, j + l2, k, Blocks.field_150395_bd, 2);
                            }

                            if (random.nextInt(3) > 0 && world.func_147437_c(i + 1, j + l2, k - 1)) {
                                this.func_150516_a(world, i + 1, j + l2, k - 1, Blocks.field_150395_bd, 1);
                            }
                        }
                    }

                    block = world.func_147439_a(i + 1, j + l2, k + 1);
                    if (block.func_149688_o() == Material.field_151579_a || block.func_149688_o() == Material.field_151584_j) {
                        this.func_150516_a(world, i + 1, j + l2, k + 1, Blocks.field_150364_r, this.field_76520_b);
                        if (l2 > 0) {
                            if (random.nextInt(3) > 0 && world.func_147437_c(i + 2, j + l2, k + 1)) {
                                this.func_150516_a(world, i + 2, j + l2, k + 1, Blocks.field_150395_bd, 2);
                            }

                            if (random.nextInt(3) > 0 && world.func_147437_c(i + 1, j + l2, k + 2)) {
                                this.func_150516_a(world, i + 1, j + l2, k + 2, Blocks.field_150395_bd, 4);
                            }
                        }
                    }

                    block = world.func_147439_a(i, j + l2, k + 1);
                    if (block.func_149688_o() == Material.field_151579_a || block.func_149688_o() == Material.field_151584_j) {
                        this.func_150516_a(world, i, j + l2, k + 1, Blocks.field_150364_r, this.field_76520_b);
                        if (l2 > 0) {
                            if (random.nextInt(3) > 0 && world.func_147437_c(i - 1, j + l2, k + 1)) {
                                this.func_150516_a(world, i - 1, j + l2, k + 1, Blocks.field_150395_bd, 8);
                            }

                            if (random.nextInt(3) > 0 && world.func_147437_c(i, j + l2, k + 2)) {
                                this.func_150516_a(world, i, j + l2, k + 2, Blocks.field_150395_bd, 4);
                            }
                        }
                    }
                }
            }

            return true;
        }
    }

    private void func_150543_c(World world, int i, int j, int k, int l, Random random) {
        byte b0 = 2;

        for (int i1 = k - b0; i1 <= k; ++i1) {
            int j1 = i1 - k;

            this.func_150535_a(world, i, i1, j, l + 1 - j1, random);
        }

    }
}
