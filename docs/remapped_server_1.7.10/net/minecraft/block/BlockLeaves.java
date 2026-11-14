package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public abstract class BlockLeaves extends BlockLeavesBase {

    int[] field_150128_a;
    protected IIcon[][] field_150129_M = new IIcon[2][];

    public BlockLeaves() {
        super(Material.field_151584_j, false);
        this.func_149675_a(true);
        this.func_149647_a(CreativeTabs.field_78031_c);
        this.func_149711_c(0.2F);
        this.func_149713_g(1);
        this.func_149672_a(BlockLeaves.field_149779_h);
    }

    @Override
    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {
        byte b0 = 1;
        int i1 = b0 + 1;

        if (world.func_72904_c(i - i1, j - i1, k - i1, i + i1, j + i1, k + i1)) {
            for (int j1 = -b0; j1 <= b0; ++j1) {
                for (int k1 = -b0; k1 <= b0; ++k1) {
                    for (int l1 = -b0; l1 <= b0; ++l1) {
                        if (world.func_147439_a(i + j1, j + k1, k + l1).func_149688_o() == Material.field_151584_j) {
                            int i2 = world.func_72805_g(i + j1, j + k1, k + l1);

                            world.func_72921_c(i + j1, j + k1, k + l1, i2 | 8, 4);
                        }
                    }
                }
            }
        }

    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        if (!world.field_72995_K) {
            int l = world.func_72805_g(i, j, k);

            if ((l & 8) != 0 && (l & 4) == 0) {
                byte b0 = 4;
                int i1 = b0 + 1;
                byte b1 = 32;
                int j1 = b1 * b1;
                int k1 = b1 / 2;

                if (this.field_150128_a == null) {
                    this.field_150128_a = new int[b1 * b1 * b1];
                }

                int l1;

                if (world.func_72904_c(i - i1, j - i1, k - i1, i + i1, j + i1, k + i1)) {
                    int i2;
                    int j2;

                    for (l1 = -b0; l1 <= b0; ++l1) {
                        for (j2 = -b0; j2 <= b0; ++j2) {
                            for (i2 = -b0; i2 <= b0; ++i2) {
                                Block block = world.func_147439_a(i + l1, j + j2, k + i2);

                                if (block != Blocks.field_150364_r && block != Blocks.field_150363_s) {
                                    if (block.func_149688_o() == Material.field_151584_j) {
                                        this.field_150128_a[(l1 + k1) * j1 + (j2 + k1) * b1 + i2 + k1] = -2;
                                    } else {
                                        this.field_150128_a[(l1 + k1) * j1 + (j2 + k1) * b1 + i2 + k1] = -1;
                                    }
                                } else {
                                    this.field_150128_a[(l1 + k1) * j1 + (j2 + k1) * b1 + i2 + k1] = 0;
                                }
                            }
                        }
                    }

                    for (l1 = 1; l1 <= 4; ++l1) {
                        for (j2 = -b0; j2 <= b0; ++j2) {
                            for (i2 = -b0; i2 <= b0; ++i2) {
                                for (int k2 = -b0; k2 <= b0; ++k2) {
                                    if (this.field_150128_a[(j2 + k1) * j1 + (i2 + k1) * b1 + k2 + k1] == l1 - 1) {
                                        if (this.field_150128_a[(j2 + k1 - 1) * j1 + (i2 + k1) * b1 + k2 + k1] == -2) {
                                            this.field_150128_a[(j2 + k1 - 1) * j1 + (i2 + k1) * b1 + k2 + k1] = l1;
                                        }

                                        if (this.field_150128_a[(j2 + k1 + 1) * j1 + (i2 + k1) * b1 + k2 + k1] == -2) {
                                            this.field_150128_a[(j2 + k1 + 1) * j1 + (i2 + k1) * b1 + k2 + k1] = l1;
                                        }

                                        if (this.field_150128_a[(j2 + k1) * j1 + (i2 + k1 - 1) * b1 + k2 + k1] == -2) {
                                            this.field_150128_a[(j2 + k1) * j1 + (i2 + k1 - 1) * b1 + k2 + k1] = l1;
                                        }

                                        if (this.field_150128_a[(j2 + k1) * j1 + (i2 + k1 + 1) * b1 + k2 + k1] == -2) {
                                            this.field_150128_a[(j2 + k1) * j1 + (i2 + k1 + 1) * b1 + k2 + k1] = l1;
                                        }

                                        if (this.field_150128_a[(j2 + k1) * j1 + (i2 + k1) * b1 + (k2 + k1 - 1)] == -2) {
                                            this.field_150128_a[(j2 + k1) * j1 + (i2 + k1) * b1 + (k2 + k1 - 1)] = l1;
                                        }

                                        if (this.field_150128_a[(j2 + k1) * j1 + (i2 + k1) * b1 + k2 + k1 + 1] == -2) {
                                            this.field_150128_a[(j2 + k1) * j1 + (i2 + k1) * b1 + k2 + k1 + 1] = l1;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                l1 = this.field_150128_a[k1 * j1 + k1 * b1 + k1];
                if (l1 >= 0) {
                    world.func_72921_c(i, j, k, l & -9, 4);
                } else {
                    this.func_150126_e(world, i, j, k);
                }
            }

        }
    }

    private void func_150126_e(World world, int i, int j, int k) {
        this.func_149697_b(world, i, j, k, world.func_72805_g(i, j, k), 0);
        world.func_147468_f(i, j, k);
    }

    @Override
    public int func_149745_a(Random random) {
        return random.nextInt(20) == 0 ? 1 : 0;
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Item.func_150898_a(Blocks.field_150345_g);
    }

    @Override
    public void func_149690_a(World world, int i, int j, int k, int l, float f, int i1) {
        if (!world.field_72995_K) {
            int j1 = this.func_150123_b(l);

            if (i1 > 0) {
                j1 -= 2 << i1;
                if (j1 < 10) {
                    j1 = 10;
                }
            }

            if (world.field_73012_v.nextInt(j1) == 0) {
                Item item = this.func_149650_a(l, world.field_73012_v, i1);

                this.func_149642_a(world, i, j, k, new ItemStack(item, 1, this.func_149692_a(l)));
            }

            j1 = 200;
            if (i1 > 0) {
                j1 -= 10 << i1;
                if (j1 < 40) {
                    j1 = 40;
                }
            }

            this.func_150124_c(world, i, j, k, l, j1);
        }

    }

    protected void func_150124_c(World world, int i, int j, int k, int l, int i1) {}

    protected int func_150123_b(int i) {
        return 20;
    }

    @Override
    public void func_149636_a(World world, EntityPlayer entityplayer, int i, int j, int k, int l) {
        if (!world.field_72995_K && entityplayer.func_71045_bC() != null && entityplayer.func_71045_bC().func_77973_b() == Items.field_151097_aZ) {
            entityplayer.func_71064_a(StatList.field_75934_C[Block.func_149682_b(this)], 1);
            this.func_149642_a(world, i, j, k, new ItemStack(Item.func_150898_a(this), 1, l & 3));
        } else {
            super.func_149636_a(world, entityplayer, i, j, k, l);
        }

    }

    @Override
    public int func_149692_a(int i) {
        return i & 3;
    }

    @Override
    public boolean func_149662_c() {
        return !this.field_150121_P;
    }

    @Override
    protected ItemStack func_149644_j(int i) {
        return new ItemStack(Item.func_150898_a(this), 1, i & 3);
    }

    public abstract String[] func_150125_e();
}
