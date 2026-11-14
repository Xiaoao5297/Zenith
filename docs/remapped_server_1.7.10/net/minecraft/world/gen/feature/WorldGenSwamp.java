package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenSwamp extends WorldGenAbstractTree {

    public WorldGenSwamp() {
        super(false);
    }

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        int l;

        for (l = random.nextInt(4) + 5; world.func_147439_a(i, j - 1, k).func_149688_o() == Material.field_151586_h; --j) {
            ;
        }

        boolean flag = true;

        if (j >= 1 && j + l + 1 <= 256) {
            int i1;
            int j1;
            int k1;

            for (int l1 = j; l1 <= j + 1 + l; ++l1) {
                i1 = 1;
                if (l1 == j) {
                    i1 = 0;
                }

                if (l1 >= j + 1 + l - 2) {
                    i1 = 3;
                }

                for (j1 = i - i1; j1 <= i + i1 && flag; ++j1) {
                    for (k1 = k - i1; k1 <= k + i1 && flag; ++k1) {
                        if (l1 >= 0 && l1 < 256) {
                            Block block = world.func_147439_a(j1, l1, k1);

                            if (block.func_149688_o() != Material.field_151579_a && block.func_149688_o() != Material.field_151584_j) {
                                if (block != Blocks.field_150355_j && block != Blocks.field_150358_i) {
                                    flag = false;
                                } else if (l1 > j) {
                                    flag = false;
                                }
                            }
                        } else {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag) {
                return false;
            } else {
                Block block1 = world.func_147439_a(i, j - 1, k);

                if ((block1 == Blocks.field_150349_c || block1 == Blocks.field_150346_d) && j < 256 - l - 1) {
                    this.func_150515_a(world, i, j - 1, k, Blocks.field_150346_d);

                    int i2;
                    int j2;

                    for (i1 = j - 3 + l; i1 <= j + l; ++i1) {
                        j1 = i1 - (j + l);
                        k1 = 2 - j1 / 2;

                        for (j2 = i - k1; j2 <= i + k1; ++j2) {
                            i2 = j2 - i;

                            for (int k2 = k - k1; k2 <= k + k1; ++k2) {
                                int l2 = k2 - k;

                                if ((Math.abs(i2) != k1 || Math.abs(l2) != k1 || random.nextInt(2) != 0 && j1 != 0) && !world.func_147439_a(j2, i1, k2).func_149730_j()) {
                                    this.func_150515_a(world, j2, i1, k2, Blocks.field_150362_t);
                                }
                            }
                        }
                    }

                    for (i1 = 0; i1 < l; ++i1) {
                        Block block2 = world.func_147439_a(i, j + i1, k);

                        if (block2.func_149688_o() == Material.field_151579_a || block2.func_149688_o() == Material.field_151584_j || block2 == Blocks.field_150358_i || block2 == Blocks.field_150355_j) {
                            this.func_150515_a(world, i, j + i1, k, Blocks.field_150364_r);
                        }
                    }

                    for (i1 = j - 3 + l; i1 <= j + l; ++i1) {
                        j1 = i1 - (j + l);
                        k1 = 2 - j1 / 2;

                        for (j2 = i - k1; j2 <= i + k1; ++j2) {
                            for (i2 = k - k1; i2 <= k + k1; ++i2) {
                                if (world.func_147439_a(j2, i1, i2).func_149688_o() == Material.field_151584_j) {
                                    if (random.nextInt(4) == 0 && world.func_147439_a(j2 - 1, i1, i2).func_149688_o() == Material.field_151579_a) {
                                        this.func_76536_b(world, j2 - 1, i1, i2, 8);
                                    }

                                    if (random.nextInt(4) == 0 && world.func_147439_a(j2 + 1, i1, i2).func_149688_o() == Material.field_151579_a) {
                                        this.func_76536_b(world, j2 + 1, i1, i2, 2);
                                    }

                                    if (random.nextInt(4) == 0 && world.func_147439_a(j2, i1, i2 - 1).func_149688_o() == Material.field_151579_a) {
                                        this.func_76536_b(world, j2, i1, i2 - 1, 1);
                                    }

                                    if (random.nextInt(4) == 0 && world.func_147439_a(j2, i1, i2 + 1).func_149688_o() == Material.field_151579_a) {
                                        this.func_76536_b(world, j2, i1, i2 + 1, 4);
                                    }
                                }
                            }
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    private void func_76536_b(World world, int i, int j, int k, int l) {
        this.func_150516_a(world, i, j, k, Blocks.field_150395_bd, l);
        int i1 = 4;

        while (true) {
            --j;
            if (world.func_147439_a(i, j, k).func_149688_o() != Material.field_151579_a || i1 <= 0) {
                return;
            }

            this.func_150516_a(world, i, j, k, Blocks.field_150395_bd, l);
            --i1;
        }
    }
}
