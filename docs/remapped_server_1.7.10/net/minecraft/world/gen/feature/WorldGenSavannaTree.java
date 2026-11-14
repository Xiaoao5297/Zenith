package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class WorldGenSavannaTree extends WorldGenAbstractTree {

    public WorldGenSavannaTree(boolean flag) {
        super(flag);
    }

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        int l = random.nextInt(3) + random.nextInt(3) + 5;
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
                    i1 = 2;
                }

                for (j1 = i - i1; j1 <= i + i1 && flag; ++j1) {
                    for (k1 = k - i1; k1 <= k + i1 && flag; ++k1) {
                        if (l1 >= 0 && l1 < 256) {
                            Block block = world.func_147439_a(j1, l1, k1);

                            if (!this.func_150523_a(block)) {
                                flag = false;
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
                    i1 = random.nextInt(4);
                    j1 = l - random.nextInt(4) - 1;
                    k1 = 3 - random.nextInt(3);
                    int i2 = i;
                    int j2 = k;
                    int k2 = 0;

                    int l2;
                    int i3;

                    for (i3 = 0; i3 < l; ++i3) {
                        l2 = j + i3;
                        if (i3 >= j1 && k1 > 0) {
                            i2 += Direction.field_71583_a[i1];
                            j2 += Direction.field_71581_b[i1];
                            --k1;
                        }

                        Block block2 = world.func_147439_a(i2, l2, j2);

                        if (block2.func_149688_o() == Material.field_151579_a || block2.func_149688_o() == Material.field_151584_j) {
                            this.func_150516_a(world, i2, l2, j2, Blocks.field_150363_s, 0);
                            k2 = l2;
                        }
                    }

                    for (i3 = -1; i3 <= 1; ++i3) {
                        for (l2 = -1; l2 <= 1; ++l2) {
                            this.func_150525_a(world, i2 + i3, k2 + 1, j2 + l2);
                        }
                    }

                    this.func_150525_a(world, i2 + 2, k2 + 1, j2);
                    this.func_150525_a(world, i2 - 2, k2 + 1, j2);
                    this.func_150525_a(world, i2, k2 + 1, j2 + 2);
                    this.func_150525_a(world, i2, k2 + 1, j2 - 2);

                    for (i3 = -3; i3 <= 3; ++i3) {
                        for (l2 = -3; l2 <= 3; ++l2) {
                            if (Math.abs(i3) != 3 || Math.abs(l2) != 3) {
                                this.func_150525_a(world, i2 + i3, k2, j2 + l2);
                            }
                        }
                    }

                    i2 = i;
                    j2 = k;
                    i3 = random.nextInt(4);
                    if (i3 != i1) {
                        l2 = j1 - random.nextInt(2) - 1;
                        int j3 = 1 + random.nextInt(3);

                        k2 = 0;

                        int k3;
                        int l3;

                        for (l3 = l2; l3 < l && j3 > 0; --j3) {
                            if (l3 >= 1) {
                                k3 = j + l3;
                                i2 += Direction.field_71583_a[i3];
                                j2 += Direction.field_71581_b[i3];
                                Block block3 = world.func_147439_a(i2, k3, j2);

                                if (block3.func_149688_o() == Material.field_151579_a || block3.func_149688_o() == Material.field_151584_j) {
                                    this.func_150516_a(world, i2, k3, j2, Blocks.field_150363_s, 0);
                                    k2 = k3;
                                }
                            }

                            ++l3;
                        }

                        if (k2 > 0) {
                            for (l3 = -1; l3 <= 1; ++l3) {
                                for (k3 = -1; k3 <= 1; ++k3) {
                                    this.func_150525_a(world, i2 + l3, k2 + 1, j2 + k3);
                                }
                            }

                            for (l3 = -2; l3 <= 2; ++l3) {
                                for (k3 = -2; k3 <= 2; ++k3) {
                                    if (Math.abs(l3) != 2 || Math.abs(k3) != 2) {
                                        this.func_150525_a(world, i2 + l3, k2, j2 + k3);
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

    private void func_150525_a(World world, int i, int j, int k) {
        Block block = world.func_147439_a(i, j, k);

        if (block.func_149688_o() == Material.field_151579_a || block.func_149688_o() == Material.field_151584_j) {
            this.func_150516_a(world, i, j, k, Blocks.field_150361_u, 0);
        }

    }
}
