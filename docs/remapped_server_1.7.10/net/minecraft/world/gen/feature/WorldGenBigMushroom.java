package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenBigMushroom extends WorldGenerator {

    private int field_76523_a = -1;

    public WorldGenBigMushroom(int i) {
        super(true);
        this.field_76523_a = i;
    }

    public WorldGenBigMushroom() {
        super(false);
    }

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        int l = random.nextInt(2);

        if (this.field_76523_a >= 0) {
            l = this.field_76523_a;
        }

        int i1 = random.nextInt(3) + 4;
        boolean flag = true;

        if (j >= 1 && j + i1 + 1 < 256) {
            int j1;
            int k1;
            int l1;

            for (int i2 = j; i2 <= j + 1 + i1; ++i2) {
                j1 = 3;
                if (i2 <= j + 3) {
                    j1 = 0;
                }

                for (k1 = i - j1; k1 <= i + j1 && flag; ++k1) {
                    for (l1 = k - j1; l1 <= k + j1 && flag; ++l1) {
                        if (i2 >= 0 && i2 < 256) {
                            Block block = world.func_147439_a(k1, i2, l1);

                            if (block.func_149688_o() != Material.field_151579_a && block.func_149688_o() != Material.field_151584_j) {
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

                if (block1 != Blocks.field_150346_d && block1 != Blocks.field_150349_c && block1 != Blocks.field_150391_bh) {
                    return false;
                } else {
                    j1 = j + i1;
                    if (l == 1) {
                        j1 = j + i1 - 3;
                    }

                    for (k1 = j1; k1 <= j + i1; ++k1) {
                        l1 = 1;
                        if (k1 < j + i1) {
                            ++l1;
                        }

                        if (l == 0) {
                            l1 = 3;
                        }

                        for (int j2 = i - l1; j2 <= i + l1; ++j2) {
                            for (int k2 = k - l1; k2 <= k + l1; ++k2) {
                                int l2 = 5;

                                if (j2 == i - l1) {
                                    --l2;
                                }

                                if (j2 == i + l1) {
                                    ++l2;
                                }

                                if (k2 == k - l1) {
                                    l2 -= 3;
                                }

                                if (k2 == k + l1) {
                                    l2 += 3;
                                }

                                if (l == 0 || k1 < j + i1) {
                                    if ((j2 == i - l1 || j2 == i + l1) && (k2 == k - l1 || k2 == k + l1)) {
                                        continue;
                                    }

                                    if (j2 == i - (l1 - 1) && k2 == k - l1) {
                                        l2 = 1;
                                    }

                                    if (j2 == i - l1 && k2 == k - (l1 - 1)) {
                                        l2 = 1;
                                    }

                                    if (j2 == i + (l1 - 1) && k2 == k - l1) {
                                        l2 = 3;
                                    }

                                    if (j2 == i + l1 && k2 == k - (l1 - 1)) {
                                        l2 = 3;
                                    }

                                    if (j2 == i - (l1 - 1) && k2 == k + l1) {
                                        l2 = 7;
                                    }

                                    if (j2 == i - l1 && k2 == k + (l1 - 1)) {
                                        l2 = 7;
                                    }

                                    if (j2 == i + (l1 - 1) && k2 == k + l1) {
                                        l2 = 9;
                                    }

                                    if (j2 == i + l1 && k2 == k + (l1 - 1)) {
                                        l2 = 9;
                                    }
                                }

                                if (l2 == 5 && k1 < j + i1) {
                                    l2 = 0;
                                }

                                if ((l2 != 0 || j >= j + i1 - 1) && !world.func_147439_a(j2, k1, k2).func_149730_j()) {
                                    this.func_150516_a(world, j2, k1, k2, Block.func_149729_e(Block.func_149682_b(Blocks.field_150420_aW) + l), l2);
                                }
                            }
                        }
                    }

                    for (k1 = 0; k1 < i1; ++k1) {
                        Block block2 = world.func_147439_a(i, j + k1, k);

                        if (!block2.func_149730_j()) {
                            this.func_150516_a(world, i, j + k1, k, Block.func_149729_e(Block.func_149682_b(Blocks.field_150420_aW) + l), 10);
                        }
                    }

                    return true;
                }
            }
        } else {
            return false;
        }
    }
}
