package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class WorldGenTrees extends WorldGenAbstractTree {

    private final int field_76533_a;
    private final boolean field_76531_b;
    private final int field_76532_c;
    private final int field_76530_d;

    public WorldGenTrees(boolean flag) {
        this(flag, 4, 0, 0, false);
    }

    public WorldGenTrees(boolean flag, int i, int j, int k, boolean flag1) {
        super(flag);
        this.field_76533_a = i;
        this.field_76532_c = j;
        this.field_76530_d = k;
        this.field_76531_b = flag1;
    }

    @Override
    public boolean func_76484_a(World world, Random random, int i, int j, int k) {
        int l = random.nextInt(3) + this.field_76533_a;
        boolean flag = true;

        if (j >= 1 && j + l + 1 <= 256) {
            byte b0;
            Block block;
            int i1;

            for (int j1 = j; j1 <= j + 1 + l; ++j1) {
                b0 = 1;
                if (j1 == j) {
                    b0 = 0;
                }

                if (j1 >= j + 1 + l - 2) {
                    b0 = 2;
                }

                for (int k1 = i - b0; k1 <= i + b0 && flag; ++k1) {
                    for (i1 = k - b0; i1 <= k + b0 && flag; ++i1) {
                        if (j1 >= 0 && j1 < 256) {
                            block = world.func_147439_a(k1, j1, i1);
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

                if ((block1 == Blocks.field_150349_c || block1 == Blocks.field_150346_d || block1 == Blocks.field_150458_ak) && j < 256 - l - 1) {
                    this.func_150515_a(world, i, j - 1, k, Blocks.field_150346_d);
                    b0 = 3;
                    byte b1 = 0;

                    int l1;
                    int i2;
                    int j2;
                    int k2;

                    for (i1 = j - b0 + l; i1 <= j + l; ++i1) {
                        l1 = i1 - (j + l);
                        i2 = b1 + 1 - l1 / 2;

                        for (k2 = i - i2; k2 <= i + i2; ++k2) {
                            j2 = k2 - i;

                            for (int l2 = k - i2; l2 <= k + i2; ++l2) {
                                int i3 = l2 - k;

                                if (Math.abs(j2) != i2 || Math.abs(i3) != i2 || random.nextInt(2) != 0 && l1 != 0) {
                                    Block block2 = world.func_147439_a(k2, i1, l2);

                                    if (block2.func_149688_o() == Material.field_151579_a || block2.func_149688_o() == Material.field_151584_j) {
                                        this.func_150516_a(world, k2, i1, l2, Blocks.field_150362_t, this.field_76530_d);
                                    }
                                }
                            }
                        }
                    }

                    for (i1 = 0; i1 < l; ++i1) {
                        block = world.func_147439_a(i, j + i1, k);
                        if (block.func_149688_o() == Material.field_151579_a || block.func_149688_o() == Material.field_151584_j) {
                            this.func_150516_a(world, i, j + i1, k, Blocks.field_150364_r, this.field_76532_c);
                            if (this.field_76531_b && i1 > 0) {
                                if (random.nextInt(3) > 0 && world.func_147437_c(i - 1, j + i1, k)) {
                                    this.func_150516_a(world, i - 1, j + i1, k, Blocks.field_150395_bd, 8);
                                }

                                if (random.nextInt(3) > 0 && world.func_147437_c(i + 1, j + i1, k)) {
                                    this.func_150516_a(world, i + 1, j + i1, k, Blocks.field_150395_bd, 2);
                                }

                                if (random.nextInt(3) > 0 && world.func_147437_c(i, j + i1, k - 1)) {
                                    this.func_150516_a(world, i, j + i1, k - 1, Blocks.field_150395_bd, 1);
                                }

                                if (random.nextInt(3) > 0 && world.func_147437_c(i, j + i1, k + 1)) {
                                    this.func_150516_a(world, i, j + i1, k + 1, Blocks.field_150395_bd, 4);
                                }
                            }
                        }
                    }

                    if (this.field_76531_b) {
                        for (i1 = j - 3 + l; i1 <= j + l; ++i1) {
                            l1 = i1 - (j + l);
                            i2 = 2 - l1 / 2;

                            for (k2 = i - i2; k2 <= i + i2; ++k2) {
                                for (j2 = k - i2; j2 <= k + i2; ++j2) {
                                    if (world.func_147439_a(k2, i1, j2).func_149688_o() == Material.field_151584_j) {
                                        if (random.nextInt(4) == 0 && world.func_147439_a(k2 - 1, i1, j2).func_149688_o() == Material.field_151579_a) {
                                            this.func_76529_b(world, k2 - 1, i1, j2, 8);
                                        }

                                        if (random.nextInt(4) == 0 && world.func_147439_a(k2 + 1, i1, j2).func_149688_o() == Material.field_151579_a) {
                                            this.func_76529_b(world, k2 + 1, i1, j2, 2);
                                        }

                                        if (random.nextInt(4) == 0 && world.func_147439_a(k2, i1, j2 - 1).func_149688_o() == Material.field_151579_a) {
                                            this.func_76529_b(world, k2, i1, j2 - 1, 1);
                                        }

                                        if (random.nextInt(4) == 0 && world.func_147439_a(k2, i1, j2 + 1).func_149688_o() == Material.field_151579_a) {
                                            this.func_76529_b(world, k2, i1, j2 + 1, 4);
                                        }
                                    }
                                }
                            }
                        }

                        if (random.nextInt(5) == 0 && l > 5) {
                            for (i1 = 0; i1 < 2; ++i1) {
                                for (l1 = 0; l1 < 4; ++l1) {
                                    if (random.nextInt(4 - i1) == 0) {
                                        i2 = random.nextInt(3);
                                        this.func_150516_a(world, i + Direction.field_71583_a[Direction.field_71580_e[l1]], j + l - 5 + i1, k + Direction.field_71581_b[Direction.field_71580_e[l1]], Blocks.field_150375_by, i2 << 2 | l1);
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

    private void func_76529_b(World world, int i, int j, int k, int l) {
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
