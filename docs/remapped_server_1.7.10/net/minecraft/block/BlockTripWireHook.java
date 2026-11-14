package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTripWireHook extends Block {

    public BlockTripWireHook() {
        super(Material.field_151594_q);
        this.func_149647_a(CreativeTabs.field_78028_d);
        this.func_149675_a(true);
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        return null;
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    @Override
    public int func_149645_b() {
        return 29;
    }

    @Override
    public int func_149738_a(World world) {
        return 10;
    }

    @Override
    public boolean func_149707_d(World world, int i, int j, int k, int l) {
        return l == 2 && world.func_147439_a(i, j, k + 1).func_149721_r() ? true : (l == 3 && world.func_147439_a(i, j, k - 1).func_149721_r() ? true : (l == 4 && world.func_147439_a(i + 1, j, k).func_149721_r() ? true : l == 5 && world.func_147439_a(i - 1, j, k).func_149721_r()));
    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        return world.func_147439_a(i - 1, j, k).func_149721_r() ? true : (world.func_147439_a(i + 1, j, k).func_149721_r() ? true : (world.func_147439_a(i, j, k - 1).func_149721_r() ? true : world.func_147439_a(i, j, k + 1).func_149721_r()));
    }

    @Override
    public int func_149660_a(World world, int i, int j, int k, int l, float f, float f1, float f2, int i1) {
        byte b0 = 0;

        if (l == 2 && world.func_147445_c(i, j, k + 1, true)) {
            b0 = 2;
        }

        if (l == 3 && world.func_147445_c(i, j, k - 1, true)) {
            b0 = 0;
        }

        if (l == 4 && world.func_147445_c(i + 1, j, k, true)) {
            b0 = 1;
        }

        if (l == 5 && world.func_147445_c(i - 1, j, k, true)) {
            b0 = 3;
        }

        return b0;
    }

    @Override
    public void func_149714_e(World world, int i, int j, int k, int l) {
        this.func_150136_a(world, i, j, k, false, l, false, -1, 0);
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        if (block != this) {
            if (this.func_150137_e(world, i, j, k)) {
                int l = world.func_72805_g(i, j, k);
                int i1 = l & 3;
                boolean flag = false;

                if (!world.func_147439_a(i - 1, j, k).func_149721_r() && i1 == 3) {
                    flag = true;
                }

                if (!world.func_147439_a(i + 1, j, k).func_149721_r() && i1 == 1) {
                    flag = true;
                }

                if (!world.func_147439_a(i, j, k - 1).func_149721_r() && i1 == 0) {
                    flag = true;
                }

                if (!world.func_147439_a(i, j, k + 1).func_149721_r() && i1 == 2) {
                    flag = true;
                }

                if (flag) {
                    this.func_149697_b(world, i, j, k, l, 0);
                    world.func_147468_f(i, j, k);
                }
            }

        }
    }

    public void func_150136_a(World world, int i, int j, int k, boolean flag, int l, boolean flag1, int i1, int j1) {
        int k1 = l & 3;
        boolean flag2 = (l & 4) == 4;
        boolean flag3 = (l & 8) == 8;
        boolean flag4 = !flag;
        boolean flag5 = false;
        boolean flag6 = !World.func_147466_a(world, i, j - 1, k);
        int l1 = Direction.field_71583_a[k1];
        int i2 = Direction.field_71581_b[k1];
        int j2 = 0;
        int[] aint = new int[42];

        int k2;
        int l2;
        int i3;
        int j3;

        for (l2 = 1; l2 < 42; ++l2) {
            k2 = i + l1 * l2;
            i3 = k + i2 * l2;
            Block block = world.func_147439_a(k2, j, i3);

            if (block == Blocks.field_150479_bC) {
                j3 = world.func_72805_g(k2, j, i3);
                if ((j3 & 3) == Direction.field_71580_e[k1]) {
                    j2 = l2;
                }
                break;
            }

            if (block != Blocks.field_150473_bD && l2 != i1) {
                aint[l2] = -1;
                flag4 = false;
            } else {
                j3 = l2 == i1 ? j1 : world.func_72805_g(k2, j, i3);
                boolean flag7 = (j3 & 8) != 8;
                boolean flag8 = (j3 & 1) == 1;
                boolean flag9 = (j3 & 2) == 2;

                flag4 &= flag9 == flag6;
                flag5 |= flag7 && flag8;
                aint[l2] = j3;
                if (l2 == i1) {
                    world.func_147464_a(i, j, k, this, this.func_149738_a(world));
                    flag4 &= flag7;
                }
            }
        }

        flag4 &= j2 > 1;
        flag5 &= flag4;
        l2 = (flag4 ? 4 : 0) | (flag5 ? 8 : 0);
        l = k1 | l2;
        int k3;

        if (j2 > 0) {
            k2 = i + l1 * j2;
            i3 = k + i2 * j2;
            k3 = Direction.field_71580_e[k1];
            world.func_72921_c(k2, j, i3, k3 | l2, 3);
            this.func_150134_a(world, k2, j, i3, k3);
            this.func_150135_a(world, k2, j, i3, flag4, flag5, flag2, flag3);
        }

        this.func_150135_a(world, i, j, k, flag4, flag5, flag2, flag3);
        if (!flag) {
            world.func_72921_c(i, j, k, l, 3);
            if (flag1) {
                this.func_150134_a(world, i, j, k, k1);
            }
        }

        if (flag2 != flag4) {
            for (k2 = 1; k2 < j2; ++k2) {
                i3 = i + l1 * k2;
                k3 = k + i2 * k2;
                j3 = aint[k2];
                if (j3 >= 0) {
                    if (flag4) {
                        j3 |= 4;
                    } else {
                        j3 &= -5;
                    }

                    world.func_72921_c(i3, j, k3, j3, 3);
                }
            }
        }

    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        this.func_150136_a(world, i, j, k, false, world.func_72805_g(i, j, k), true, -1, 0);
    }

    private void func_150135_a(World world, int i, int j, int k, boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        if (flag1 && !flag3) {
            world.func_72908_a((double) i + 0.5D, (double) j + 0.1D, (double) k + 0.5D, "random.click", 0.4F, 0.6F);
        } else if (!flag1 && flag3) {
            world.func_72908_a((double) i + 0.5D, (double) j + 0.1D, (double) k + 0.5D, "random.click", 0.4F, 0.5F);
        } else if (flag && !flag2) {
            world.func_72908_a((double) i + 0.5D, (double) j + 0.1D, (double) k + 0.5D, "random.click", 0.4F, 0.7F);
        } else if (!flag && flag2) {
            world.func_72908_a((double) i + 0.5D, (double) j + 0.1D, (double) k + 0.5D, "random.bowhit", 0.4F, 1.2F / (world.field_73012_v.nextFloat() * 0.2F + 0.9F));
        }

    }

    private void func_150134_a(World world, int i, int j, int k, int l) {
        world.func_147459_d(i, j, k, this);
        if (l == 3) {
            world.func_147459_d(i - 1, j, k, this);
        } else if (l == 1) {
            world.func_147459_d(i + 1, j, k, this);
        } else if (l == 0) {
            world.func_147459_d(i, j, k - 1, this);
        } else if (l == 2) {
            world.func_147459_d(i, j, k + 1, this);
        }

    }

    private boolean func_150137_e(World world, int i, int j, int k) {
        if (!this.func_149742_c(world, i, j, k)) {
            this.func_149697_b(world, i, j, k, world.func_72805_g(i, j, k), 0);
            world.func_147468_f(i, j, k);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        int l = iblockaccess.func_72805_g(i, j, k) & 3;
        float f = 0.1875F;

        if (l == 3) {
            this.func_149676_a(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
        } else if (l == 1) {
            this.func_149676_a(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
        } else if (l == 0) {
            this.func_149676_a(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
        } else if (l == 2) {
            this.func_149676_a(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
        }

    }

    @Override
    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {
        boolean flag = (l & 4) == 4;
        boolean flag1 = (l & 8) == 8;

        if (flag || flag1) {
            this.func_150136_a(world, i, j, k, true, l, false, -1, 0);
        }

        if (flag1) {
            world.func_147459_d(i, j, k, this);
            int i1 = l & 3;

            if (i1 == 3) {
                world.func_147459_d(i - 1, j, k, this);
            } else if (i1 == 1) {
                world.func_147459_d(i + 1, j, k, this);
            } else if (i1 == 0) {
                world.func_147459_d(i, j, k - 1, this);
            } else if (i1 == 2) {
                world.func_147459_d(i, j, k + 1, this);
            }
        }

        super.func_149749_a(world, i, j, k, block, l);
    }

    @Override
    public int func_149709_b(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return (iblockaccess.func_72805_g(i, j, k) & 8) == 8 ? 15 : 0;
    }

    @Override
    public int func_149748_c(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        int i1 = iblockaccess.func_72805_g(i, j, k);

        if ((i1 & 8) != 8) {
            return 0;
        } else {
            int j1 = i1 & 3;

            return j1 == 2 && l == 2 ? 15 : (j1 == 0 && l == 3 ? 15 : (j1 == 1 && l == 4 ? 15 : (j1 == 3 && l == 5 ? 15 : 0)));
        }
    }

    @Override
    public boolean func_149744_f() {
        return true;
    }
}
