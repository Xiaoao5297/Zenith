package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockButton extends Block {

    private final boolean field_150047_a;

    protected BlockButton(boolean flag) {
        super(Material.field_151594_q);
        this.func_149675_a(true);
        this.func_149647_a(CreativeTabs.field_78028_d);
        this.field_150047_a = flag;
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        return null;
    }

    @Override
    public int func_149738_a(World world) {
        return this.field_150047_a ? 30 : 20;
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
    public boolean func_149707_d(World world, int i, int j, int k, int l) {
        return l == 2 && world.func_147439_a(i, j, k + 1).func_149721_r() ? true : (l == 3 && world.func_147439_a(i, j, k - 1).func_149721_r() ? true : (l == 4 && world.func_147439_a(i + 1, j, k).func_149721_r() ? true : l == 5 && world.func_147439_a(i - 1, j, k).func_149721_r()));
    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        return world.func_147439_a(i - 1, j, k).func_149721_r() ? true : (world.func_147439_a(i + 1, j, k).func_149721_r() ? true : (world.func_147439_a(i, j, k - 1).func_149721_r() ? true : world.func_147439_a(i, j, k + 1).func_149721_r()));
    }

    @Override
    public int func_149660_a(World world, int i, int j, int k, int l, float f, float f1, float f2, int i1) {
        int j1 = world.func_72805_g(i, j, k);
        int k1 = j1 & 8;

        j1 &= 7;
        if (l == 2 && world.func_147439_a(i, j, k + 1).func_149721_r()) {
            j1 = 4;
        } else if (l == 3 && world.func_147439_a(i, j, k - 1).func_149721_r()) {
            j1 = 3;
        } else if (l == 4 && world.func_147439_a(i + 1, j, k).func_149721_r()) {
            j1 = 2;
        } else if (l == 5 && world.func_147439_a(i - 1, j, k).func_149721_r()) {
            j1 = 1;
        } else {
            j1 = this.func_150045_e(world, i, j, k);
        }

        return j1 + k1;
    }

    private int func_150045_e(World world, int i, int j, int k) {
        return world.func_147439_a(i - 1, j, k).func_149721_r() ? 1 : (world.func_147439_a(i + 1, j, k).func_149721_r() ? 2 : (world.func_147439_a(i, j, k - 1).func_149721_r() ? 3 : (world.func_147439_a(i, j, k + 1).func_149721_r() ? 4 : 1)));
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        if (this.func_150044_m(world, i, j, k)) {
            int l = world.func_72805_g(i, j, k) & 7;
            boolean flag = false;

            if (!world.func_147439_a(i - 1, j, k).func_149721_r() && l == 1) {
                flag = true;
            }

            if (!world.func_147439_a(i + 1, j, k).func_149721_r() && l == 2) {
                flag = true;
            }

            if (!world.func_147439_a(i, j, k - 1).func_149721_r() && l == 3) {
                flag = true;
            }

            if (!world.func_147439_a(i, j, k + 1).func_149721_r() && l == 4) {
                flag = true;
            }

            if (flag) {
                this.func_149697_b(world, i, j, k, world.func_72805_g(i, j, k), 0);
                world.func_147468_f(i, j, k);
            }
        }

    }

    private boolean func_150044_m(World world, int i, int j, int k) {
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
        int l = iblockaccess.func_72805_g(i, j, k);

        this.func_150043_b(l);
    }

    private void func_150043_b(int i) {
        int j = i & 7;
        boolean flag = (i & 8) > 0;
        float f = 0.375F;
        float f1 = 0.625F;
        float f2 = 0.1875F;
        float f3 = 0.125F;

        if (flag) {
            f3 = 0.0625F;
        }

        if (j == 1) {
            this.func_149676_a(0.0F, f, 0.5F - f2, f3, f1, 0.5F + f2);
        } else if (j == 2) {
            this.func_149676_a(1.0F - f3, f, 0.5F - f2, 1.0F, f1, 0.5F + f2);
        } else if (j == 3) {
            this.func_149676_a(0.5F - f2, f, 0.0F, 0.5F + f2, f1, f3);
        } else if (j == 4) {
            this.func_149676_a(0.5F - f2, f, 1.0F - f3, 0.5F + f2, f1, 1.0F);
        }

    }

    @Override
    public void func_149699_a(World world, int i, int j, int k, EntityPlayer entityplayer) {}

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        int i1 = world.func_72805_g(i, j, k);
        int j1 = i1 & 7;
        int k1 = 8 - (i1 & 8);

        if (k1 == 0) {
            return true;
        } else {
            world.func_72921_c(i, j, k, j1 + k1, 3);
            world.func_147458_c(i, j, k, i, j, k);
            world.func_72908_a((double) i + 0.5D, (double) j + 0.5D, (double) k + 0.5D, "random.click", 0.3F, 0.6F);
            this.func_150042_a(world, i, j, k, j1);
            world.func_147464_a(i, j, k, this, this.func_149738_a(world));
            return true;
        }
    }

    @Override
    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {
        if ((l & 8) > 0) {
            int i1 = l & 7;

            this.func_150042_a(world, i, j, k, i1);
        }

        super.func_149749_a(world, i, j, k, block, l);
    }

    @Override
    public int func_149709_b(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return (iblockaccess.func_72805_g(i, j, k) & 8) > 0 ? 15 : 0;
    }

    @Override
    public int func_149748_c(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        int i1 = iblockaccess.func_72805_g(i, j, k);

        if ((i1 & 8) == 0) {
            return 0;
        } else {
            int j1 = i1 & 7;

            return j1 == 5 && l == 1 ? 15 : (j1 == 4 && l == 2 ? 15 : (j1 == 3 && l == 3 ? 15 : (j1 == 2 && l == 4 ? 15 : (j1 == 1 && l == 5 ? 15 : 0))));
        }
    }

    @Override
    public boolean func_149744_f() {
        return true;
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        if (!world.field_72995_K) {
            int l = world.func_72805_g(i, j, k);

            if ((l & 8) != 0) {
                if (this.field_150047_a) {
                    this.func_150046_n(world, i, j, k);
                } else {
                    world.func_72921_c(i, j, k, l & 7, 3);
                    int i1 = l & 7;

                    this.func_150042_a(world, i, j, k, i1);
                    world.func_72908_a((double) i + 0.5D, (double) j + 0.5D, (double) k + 0.5D, "random.click", 0.3F, 0.5F);
                    world.func_147458_c(i, j, k, i, j, k);
                }

            }
        }
    }

    @Override
    public void func_149683_g() {
        float f = 0.1875F;
        float f1 = 0.125F;
        float f2 = 0.125F;

        this.func_149676_a(0.5F - f, 0.5F - f1, 0.5F - f2, 0.5F + f, 0.5F + f1, 0.5F + f2);
    }

    @Override
    public void func_149670_a(World world, int i, int j, int k, Entity entity) {
        if (!world.field_72995_K) {
            if (this.field_150047_a) {
                if ((world.func_72805_g(i, j, k) & 8) == 0) {
                    this.func_150046_n(world, i, j, k);
                }
            }
        }
    }

    private void func_150046_n(World world, int i, int j, int k) {
        int l = world.func_72805_g(i, j, k);
        int i1 = l & 7;
        boolean flag = (l & 8) != 0;

        this.func_150043_b(l);
        List list = world.func_72872_a(EntityArrow.class, AxisAlignedBB.func_72330_a((double) i + this.field_149759_B, (double) j + this.field_149760_C, (double) k + this.field_149754_D, (double) i + this.field_149755_E, (double) j + this.field_149756_F, (double) k + this.field_149757_G));
        boolean flag1 = !list.isEmpty();

        if (flag1 && !flag) {
            world.func_72921_c(i, j, k, i1 | 8, 3);
            this.func_150042_a(world, i, j, k, i1);
            world.func_147458_c(i, j, k, i, j, k);
            world.func_72908_a((double) i + 0.5D, (double) j + 0.5D, (double) k + 0.5D, "random.click", 0.3F, 0.6F);
        }

        if (!flag1 && flag) {
            world.func_72921_c(i, j, k, i1, 3);
            this.func_150042_a(world, i, j, k, i1);
            world.func_147458_c(i, j, k, i, j, k);
            world.func_72908_a((double) i + 0.5D, (double) j + 0.5D, (double) k + 0.5D, "random.click", 0.3F, 0.5F);
        }

        if (flag1) {
            world.func_147464_a(i, j, k, this, this.func_149738_a(world));
        }

    }

    private void func_150042_a(World world, int i, int j, int k, int l) {
        world.func_147459_d(i, j, k, this);
        if (l == 1) {
            world.func_147459_d(i - 1, j, k, this);
        } else if (l == 2) {
            world.func_147459_d(i + 1, j, k, this);
        } else if (l == 3) {
            world.func_147459_d(i, j, k - 1, this);
        } else if (l == 4) {
            world.func_147459_d(i, j, k + 1, this);
        } else {
            world.func_147459_d(i, j - 1, k, this);
        }

    }
}
