package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class WorldGenLakes extends WorldGenerator {

    private Block field_150556_a;

    public WorldGenLakes(Block block) {
        this.field_150556_a = block;
    }

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        i -= 8;

        for (k -= 8; j > 5 && world.func_147437_c(i, j, k); --j) {
            ;
        }

        if (j <= 4) {
            return false;
        } else {
            j -= 4;
            boolean[] aboolean = new boolean[2048];
            int l = random.nextInt(4) + 4;

            int i1;

            for (i1 = 0; i1 < l; ++i1) {
                double d0 = random.nextDouble() * 6.0D + 3.0D;
                double d1 = random.nextDouble() * 4.0D + 2.0D;
                double d2 = random.nextDouble() * 6.0D + 3.0D;
                double d3 = random.nextDouble() * (16.0D - d0 - 2.0D) + 1.0D + d0 / 2.0D;
                double d4 = random.nextDouble() * (8.0D - d1 - 4.0D) + 2.0D + d1 / 2.0D;
                double d5 = random.nextDouble() * (16.0D - d2 - 2.0D) + 1.0D + d2 / 2.0D;

                for (int j1 = 1; j1 < 15; ++j1) {
                    for (int k1 = 1; k1 < 15; ++k1) {
                        for (int l1 = 1; l1 < 7; ++l1) {
                            double d6 = ((double) j1 - d3) / (d0 / 2.0D);
                            double d7 = ((double) l1 - d4) / (d1 / 2.0D);
                            double d8 = ((double) k1 - d5) / (d2 / 2.0D);
                            double d9 = d6 * d6 + d7 * d7 + d8 * d8;

                            if (d9 < 1.0D) {
                                aboolean[(j1 * 16 + k1) * 8 + l1] = true;
                            }
                        }
                    }
                }
            }

            boolean flag;
            int i2;
            int j2;

            for (i1 = 0; i1 < 16; ++i1) {
                for (j2 = 0; j2 < 16; ++j2) {
                    for (i2 = 0; i2 < 8; ++i2) {
                        flag = !aboolean[(i1 * 16 + j2) * 8 + i2] && (i1 < 15 && aboolean[((i1 + 1) * 16 + j2) * 8 + i2] || i1 > 0 && aboolean[((i1 - 1) * 16 + j2) * 8 + i2] || j2 < 15 && aboolean[(i1 * 16 + j2 + 1) * 8 + i2] || j2 > 0 && aboolean[(i1 * 16 + (j2 - 1)) * 8 + i2] || i2 < 7 && aboolean[(i1 * 16 + j2) * 8 + i2 + 1] || i2 > 0 && aboolean[(i1 * 16 + j2) * 8 + (i2 - 1)]);
                        if (flag) {
                            Material material = world.func_147439_a(i + i1, j + i2, k + j2).func_149688_o();

                            if (i2 >= 4 && material.func_76224_d()) {
                                return false;
                            }

                            if (i2 < 4 && !material.func_76220_a() && world.func_147439_a(i + i1, j + i2, k + j2) != this.field_150556_a) {
                                return false;
                            }
                        }
                    }
                }
            }

            for (i1 = 0; i1 < 16; ++i1) {
                for (j2 = 0; j2 < 16; ++j2) {
                    for (i2 = 0; i2 < 8; ++i2) {
                        if (aboolean[(i1 * 16 + j2) * 8 + i2]) {
                            world.func_147465_d(i + i1, j + i2, k + j2, i2 >= 4 ? Blocks.field_150350_a : this.field_150556_a, 0, 2);
                        }
                    }
                }
            }

            for (i1 = 0; i1 < 16; ++i1) {
                for (j2 = 0; j2 < 16; ++j2) {
                    for (i2 = 4; i2 < 8; ++i2) {
                        if (aboolean[(i1 * 16 + j2) * 8 + i2] && world.func_147439_a(i + i1, j + i2 - 1, k + j2) == Blocks.field_150346_d && world.func_72972_b(EnumSkyBlock.Sky, i + i1, j + i2, k + j2) > 0) {
                            BiomeGenBase biomegenbase = world.func_72807_a(i + i1, k + j2);

                            if (biomegenbase.field_76752_A == Blocks.field_150391_bh) {
                                world.func_147465_d(i + i1, j + i2 - 1, k + j2, Blocks.field_150391_bh, 0, 2);
                            } else {
                                world.func_147465_d(i + i1, j + i2 - 1, k + j2, Blocks.field_150349_c, 0, 2);
                            }
                        }
                    }
                }
            }

            if (this.field_150556_a.func_149688_o() == Material.field_151587_i) {
                for (i1 = 0; i1 < 16; ++i1) {
                    for (j2 = 0; j2 < 16; ++j2) {
                        for (i2 = 0; i2 < 8; ++i2) {
                            flag = !aboolean[(i1 * 16 + j2) * 8 + i2] && (i1 < 15 && aboolean[((i1 + 1) * 16 + j2) * 8 + i2] || i1 > 0 && aboolean[((i1 - 1) * 16 + j2) * 8 + i2] || j2 < 15 && aboolean[(i1 * 16 + j2 + 1) * 8 + i2] || j2 > 0 && aboolean[(i1 * 16 + (j2 - 1)) * 8 + i2] || i2 < 7 && aboolean[(i1 * 16 + j2) * 8 + i2 + 1] || i2 > 0 && aboolean[(i1 * 16 + j2) * 8 + (i2 - 1)]);
                            if (flag && (i2 < 4 || random.nextInt(2) != 0) && world.func_147439_a(i + i1, j + i2, k + j2).func_149688_o().func_76220_a()) {
                                world.func_147465_d(i + i1, j + i2, k + j2, Blocks.field_150348_b, 0, 2);
                            }
                        }
                    }
                }
            }

            if (this.field_150556_a.func_149688_o() == Material.field_151586_h) {
                for (i1 = 0; i1 < 16; ++i1) {
                    for (j2 = 0; j2 < 16; ++j2) {
                        byte b0 = 4;

                        if (world.func_72884_u(i + i1, j + b0, k + j2)) {
                            world.func_147465_d(i + i1, j + b0, k + j2, Blocks.field_150432_aD, 0, 2);
                        }
                    }
                }
            }

            return true;
        }
    }
}
