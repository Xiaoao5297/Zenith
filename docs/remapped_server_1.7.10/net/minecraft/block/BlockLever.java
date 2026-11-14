package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLever extends Block {

    protected BlockLever() {
        super(Material.field_151594_q);
        this.func_149647_a(CreativeTabs.field_78028_d);
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
        return 12;
    }

    @Override
    public boolean func_149707_d(World world, int i, int j, int k, int l) {
        return l == 0 && world.func_147439_a(i, j + 1, k).func_149721_r() ? true : (l == 1 && World.func_147466_a(world, i, j - 1, k) ? true : (l == 2 && world.func_147439_a(i, j, k + 1).func_149721_r() ? true : (l == 3 && world.func_147439_a(i, j, k - 1).func_149721_r() ? true : (l == 4 && world.func_147439_a(i + 1, j, k).func_149721_r() ? true : l == 5 && world.func_147439_a(i - 1, j, k).func_149721_r()))));
    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        return world.func_147439_a(i - 1, j, k).func_149721_r() ? true : (world.func_147439_a(i + 1, j, k).func_149721_r() ? true : (world.func_147439_a(i, j, k - 1).func_149721_r() ? true : (world.func_147439_a(i, j, k + 1).func_149721_r() ? true : (World.func_147466_a(world, i, j - 1, k) ? true : world.func_147439_a(i, j + 1, k).func_149721_r()))));
    }

    @Override
    public int func_149660_a(World world, int i, int j, int k, int l, float f, float f1, float f2, int i1) {
        int j1 = i1 & 8;
        int k1 = i1 & 7;
        byte b0 = -1;

        if (l == 0 && world.func_147439_a(i, j + 1, k).func_149721_r()) {
            b0 = 0;
        }

        if (l == 1 && World.func_147466_a(world, i, j - 1, k)) {
            b0 = 5;
        }

        if (l == 2 && world.func_147439_a(i, j, k + 1).func_149721_r()) {
            b0 = 4;
        }

        if (l == 3 && world.func_147439_a(i, j, k - 1).func_149721_r()) {
            b0 = 3;
        }

        if (l == 4 && world.func_147439_a(i + 1, j, k).func_149721_r()) {
            b0 = 2;
        }

        if (l == 5 && world.func_147439_a(i - 1, j, k).func_149721_r()) {
            b0 = 1;
        }

        return b0 + j1;
    }

    @Override
    public void func_149689_a(World world, int i, int j, int k, EntityLivingBase entitylivingbase, ItemStack itemstack) {
        int l = world.func_72805_g(i, j, k);
        int i1 = l & 7;
        int j1 = l & 8;

        if (i1 == func_149819_b(1)) {
            if ((MathHelper.func_76128_c((double) (entitylivingbase.field_70177_z * 4.0F / 360.0F) + 0.5D) & 1) == 0) {
                world.func_72921_c(i, j, k, 5 | j1, 2);
            } else {
                world.func_72921_c(i, j, k, 6 | j1, 2);
            }
        } else if (i1 == func_149819_b(0)) {
            if ((MathHelper.func_76128_c((double) (entitylivingbase.field_70177_z * 4.0F / 360.0F) + 0.5D) & 1) == 0) {
                world.func_72921_c(i, j, k, 7 | j1, 2);
            } else {
                world.func_72921_c(i, j, k, 0 | j1, 2);
            }
        }

    }

    public static int func_149819_b(int i) {
        switch (i) {
            case 0:
                return 0;
            case 1:
                return 5;
            case 2:
                return 4;
            case 3:
                return 3;
            case 4:
                return 2;
            case 5:
                return 1;
            default:
                return -1;
        }
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        if (this.func_149820_e(world, i, j, k)) {
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

            if (!World.func_147466_a(world, i, j - 1, k) && l == 5) {
                flag = true;
            }

            if (!World.func_147466_a(world, i, j - 1, k) && l == 6) {
                flag = true;
            }

            if (!world.func_147439_a(i, j + 1, k).func_149721_r() && l == 0) {
                flag = true;
            }

            if (!world.func_147439_a(i, j + 1, k).func_149721_r() && l == 7) {
                flag = true;
            }

            if (flag) {
                this.func_149697_b(world, i, j, k, world.func_72805_g(i, j, k), 0);
                world.func_147468_f(i, j, k);
            }
        }

    }

    private boolean func_149820_e(World world, int i, int j, int k) {
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
        int l = iblockaccess.func_72805_g(i, j, k) & 7;
        float f = 0.1875F;

        if (l == 1) {
            this.func_149676_a(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
        } else if (l == 2) {
            this.func_149676_a(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
        } else if (l == 3) {
            this.func_149676_a(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
        } else if (l == 4) {
            this.func_149676_a(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
        } else if (l != 5 && l != 6) {
            if (l == 0 || l == 7) {
                f = 0.25F;
                this.func_149676_a(0.5F - f, 0.4F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
            }
        } else {
            f = 0.25F;
            this.func_149676_a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.6F, 0.5F + f);
        }

    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        if (world.field_72995_K) {
            return true;
        } else {
            int i1 = world.func_72805_g(i, j, k);
            int j1 = i1 & 7;
            int k1 = 8 - (i1 & 8);

            world.func_72921_c(i, j, k, j1 + k1, 3);
            world.func_72908_a((double) i + 0.5D, (double) j + 0.5D, (double) k + 0.5D, "random.click", 0.3F, k1 > 0 ? 0.6F : 0.5F);
            world.func_147459_d(i, j, k, this);
            if (j1 == 1) {
                world.func_147459_d(i - 1, j, k, this);
            } else if (j1 == 2) {
                world.func_147459_d(i + 1, j, k, this);
            } else if (j1 == 3) {
                world.func_147459_d(i, j, k - 1, this);
            } else if (j1 == 4) {
                world.func_147459_d(i, j, k + 1, this);
            } else if (j1 != 5 && j1 != 6) {
                if (j1 == 0 || j1 == 7) {
                    world.func_147459_d(i, j + 1, k, this);
                }
            } else {
                world.func_147459_d(i, j - 1, k, this);
            }

            return true;
        }
    }

    @Override
    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {
        if ((l & 8) > 0) {
            world.func_147459_d(i, j, k, this);
            int i1 = l & 7;

            if (i1 == 1) {
                world.func_147459_d(i - 1, j, k, this);
            } else if (i1 == 2) {
                world.func_147459_d(i + 1, j, k, this);
            } else if (i1 == 3) {
                world.func_147459_d(i, j, k - 1, this);
            } else if (i1 == 4) {
                world.func_147459_d(i, j, k + 1, this);
            } else if (i1 != 5 && i1 != 6) {
                if (i1 == 0 || i1 == 7) {
                    world.func_147459_d(i, j + 1, k, this);
                }
            } else {
                world.func_147459_d(i, j - 1, k, this);
            }
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

            return j1 == 0 && l == 0 ? 15 : (j1 == 7 && l == 0 ? 15 : (j1 == 6 && l == 1 ? 15 : (j1 == 5 && l == 1 ? 15 : (j1 == 4 && l == 2 ? 15 : (j1 == 3 && l == 3 ? 15 : (j1 == 2 && l == 4 ? 15 : (j1 == 1 && l == 5 ? 15 : 0)))))));
        }
    }

    @Override
    public boolean func_149744_f() {
        return true;
    }
}
