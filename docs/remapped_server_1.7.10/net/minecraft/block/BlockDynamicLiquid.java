package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BlockDynamicLiquid extends BlockLiquid {

    int field_149815_a;
    boolean[] field_149814_b = new boolean[4];
    int[] field_149816_M = new int[4];

    protected BlockDynamicLiquid(Material material) {
        super(material);
    }

    private void func_149811_n(World world, int i, int j, int k) {
        int l = world.func_72805_g(i, j, k);

        world.func_147465_d(i, j, k, Block.func_149729_e(Block.func_149682_b(this) + 1), l, 2);
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        int l = this.func_149804_e(world, i, j, k);
        byte b0 = 1;

        if (this.field_149764_J == Material.field_151587_i && !world.field_73011_w.field_76575_d) {
            b0 = 2;
        }

        boolean flag = true;
        int i1 = this.func_149738_a(world);
        int j1;

        if (l > 0) {
            int k1 = -100;

            this.field_149815_a = 0;
            k1 = this.func_149810_a(world, i - 1, j, k, k1);
            k1 = this.func_149810_a(world, i + 1, j, k, k1);
            k1 = this.func_149810_a(world, i, j, k - 1, k1);
            k1 = this.func_149810_a(world, i, j, k + 1, k1);
            j1 = k1 + b0;
            if (j1 >= 8 || k1 < 0) {
                j1 = -1;
            }

            if (this.func_149804_e(world, i, j + 1, k) >= 0) {
                int l1 = this.func_149804_e(world, i, j + 1, k);

                if (l1 >= 8) {
                    j1 = l1;
                } else {
                    j1 = l1 + 8;
                }
            }

            if (this.field_149815_a >= 2 && this.field_149764_J == Material.field_151586_h) {
                if (world.func_147439_a(i, j - 1, k).func_149688_o().func_76220_a()) {
                    j1 = 0;
                } else if (world.func_147439_a(i, j - 1, k).func_149688_o() == this.field_149764_J && world.func_72805_g(i, j - 1, k) == 0) {
                    j1 = 0;
                }
            }

            if (this.field_149764_J == Material.field_151587_i && l < 8 && j1 < 8 && j1 > l && random.nextInt(4) != 0) {
                i1 *= 4;
            }

            if (j1 == l) {
                if (flag) {
                    this.func_149811_n(world, i, j, k);
                }
            } else {
                l = j1;
                if (j1 < 0) {
                    world.func_147468_f(i, j, k);
                } else {
                    world.func_72921_c(i, j, k, j1, 2);
                    world.func_147464_a(i, j, k, this, i1);
                    world.func_147459_d(i, j, k, this);
                }
            }
        } else {
            this.func_149811_n(world, i, j, k);
        }

        if (this.func_149809_q(world, i, j - 1, k)) {
            if (this.field_149764_J == Material.field_151587_i && world.func_147439_a(i, j - 1, k).func_149688_o() == Material.field_151586_h) {
                world.func_147449_b(i, j - 1, k, Blocks.field_150348_b);
                this.func_149799_m(world, i, j - 1, k);
                return;
            }

            if (l >= 8) {
                this.func_149813_h(world, i, j - 1, k, l);
            } else {
                this.func_149813_h(world, i, j - 1, k, l + 8);
            }
        } else if (l >= 0 && (l == 0 || this.func_149807_p(world, i, j - 1, k))) {
            boolean[] aboolean = this.func_149808_o(world, i, j, k);

            j1 = l + b0;
            if (l >= 8) {
                j1 = 1;
            }

            if (j1 >= 8) {
                return;
            }

            if (aboolean[0]) {
                this.func_149813_h(world, i - 1, j, k, j1);
            }

            if (aboolean[1]) {
                this.func_149813_h(world, i + 1, j, k, j1);
            }

            if (aboolean[2]) {
                this.func_149813_h(world, i, j, k - 1, j1);
            }

            if (aboolean[3]) {
                this.func_149813_h(world, i, j, k + 1, j1);
            }
        }

    }

    private void func_149813_h(World world, int i, int j, int k, int l) {
        if (this.func_149809_q(world, i, j, k)) {
            Block block = world.func_147439_a(i, j, k);

            if (this.field_149764_J == Material.field_151587_i) {
                this.func_149799_m(world, i, j, k);
            } else {
                block.func_149697_b(world, i, j, k, world.func_72805_g(i, j, k), 0);
            }

            world.func_147465_d(i, j, k, this, l, 3);
        }

    }

    private int func_149812_c(World world, int i, int j, int k, int l, int i1) {
        int j1 = 1000;

        for (int k1 = 0; k1 < 4; ++k1) {
            if ((k1 != 0 || i1 != 1) && (k1 != 1 || i1 != 0) && (k1 != 2 || i1 != 3) && (k1 != 3 || i1 != 2)) {
                int l1 = i;
                int i2 = k;

                if (k1 == 0) {
                    l1 = i - 1;
                }

                if (k1 == 1) {
                    ++l1;
                }

                if (k1 == 2) {
                    i2 = k - 1;
                }

                if (k1 == 3) {
                    ++i2;
                }

                if (!this.func_149807_p(world, l1, j, i2) && (world.func_147439_a(l1, j, i2).func_149688_o() != this.field_149764_J || world.func_72805_g(l1, j, i2) != 0)) {
                    if (!this.func_149807_p(world, l1, j - 1, i2)) {
                        return l;
                    }

                    if (l < 4) {
                        int j2 = this.func_149812_c(world, l1, j, i2, l + 1, k1);

                        if (j2 < j1) {
                            j1 = j2;
                        }
                    }
                }
            }
        }

        return j1;
    }

    private boolean[] func_149808_o(World world, int i, int j, int k) {
        int l;
        int i1;

        for (l = 0; l < 4; ++l) {
            this.field_149816_M[l] = 1000;
            i1 = i;
            int j1 = k;

            if (l == 0) {
                i1 = i - 1;
            }

            if (l == 1) {
                ++i1;
            }

            if (l == 2) {
                j1 = k - 1;
            }

            if (l == 3) {
                ++j1;
            }

            if (!this.func_149807_p(world, i1, j, j1) && (world.func_147439_a(i1, j, j1).func_149688_o() != this.field_149764_J || world.func_72805_g(i1, j, j1) != 0)) {
                if (this.func_149807_p(world, i1, j - 1, j1)) {
                    this.field_149816_M[l] = this.func_149812_c(world, i1, j, j1, 1, l);
                } else {
                    this.field_149816_M[l] = 0;
                }
            }
        }

        l = this.field_149816_M[0];

        for (i1 = 1; i1 < 4; ++i1) {
            if (this.field_149816_M[i1] < l) {
                l = this.field_149816_M[i1];
            }
        }

        for (i1 = 0; i1 < 4; ++i1) {
            this.field_149814_b[i1] = this.field_149816_M[i1] == l;
        }

        return this.field_149814_b;
    }

    private boolean func_149807_p(World world, int i, int j, int k) {
        Block block = world.func_147439_a(i, j, k);

        return block != Blocks.field_150466_ao && block != Blocks.field_150454_av && block != Blocks.field_150472_an && block != Blocks.field_150468_ap && block != Blocks.field_150436_aH ? (block.field_149764_J == Material.field_151567_E ? true : block.field_149764_J.func_76230_c()) : true;
    }

    protected int func_149810_a(World world, int i, int j, int k, int l) {
        int i1 = this.func_149804_e(world, i, j, k);

        if (i1 < 0) {
            return l;
        } else {
            if (i1 == 0) {
                ++this.field_149815_a;
            }

            if (i1 >= 8) {
                i1 = 0;
            }

            return l >= 0 && i1 >= l ? l : i1;
        }
    }

    private boolean func_149809_q(World world, int i, int j, int k) {
        Material material = world.func_147439_a(i, j, k).func_149688_o();

        return material == this.field_149764_J ? false : (material == Material.field_151587_i ? false : !this.func_149807_p(world, i, j, k));
    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {
        super.func_149726_b(world, i, j, k);
        if (world.func_147439_a(i, j, k) == this) {
            world.func_147464_a(i, j, k, this, this.func_149738_a(world));
        }

    }

    @Override
    public boolean func_149698_L() {
        return true;
    }
}
