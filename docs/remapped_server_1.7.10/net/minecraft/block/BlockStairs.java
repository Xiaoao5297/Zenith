package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStairs extends Block {

    private static final int[][] field_150150_a = new int[][]{{2, 6}, {3, 7}, {2, 3}, {6, 7}, {0, 4}, {1, 5}, {0, 1}, {4, 5}};
    private final Block field_150149_b;
    private final int field_150151_M;
    private boolean field_150152_N;
    private int field_150153_O;

    protected BlockStairs(Block block, int i) {
        super(block.field_149764_J);
        this.field_150149_b = block;
        this.field_150151_M = i;
        this.func_149711_c(block.field_149782_v);
        this.func_149752_b(block.field_149781_w / 3.0F);
        this.func_149672_a(block.field_149762_H);
        this.func_149713_g(255);
        this.func_149647_a(CreativeTabs.field_78030_b);
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        if (this.field_150152_N) {
            this.func_149676_a(0.5F * (float) (this.field_150153_O % 2), 0.5F * (float) (this.field_150153_O / 2 % 2), 0.5F * (float) (this.field_150153_O / 4 % 2), 0.5F + 0.5F * (float) (this.field_150153_O % 2), 0.5F + 0.5F * (float) (this.field_150153_O / 2 % 2), 0.5F + 0.5F * (float) (this.field_150153_O / 4 % 2));
        } else {
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }

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
        return 10;
    }

    public void func_150147_e(IBlockAccess iblockaccess, int i, int j, int k) {
        int l = iblockaccess.func_72805_g(i, j, k);

        if ((l & 4) != 0) {
            this.func_149676_a(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else {
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }

    }

    public static boolean func_150148_a(Block block) {
        return block instanceof BlockStairs;
    }

    private boolean func_150146_f(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        Block block = iblockaccess.func_147439_a(i, j, k);

        return func_150148_a(block) && iblockaccess.func_72805_g(i, j, k) == l;
    }

    public boolean func_150145_f(IBlockAccess iblockaccess, int i, int j, int k) {
        int l = iblockaccess.func_72805_g(i, j, k);
        int i1 = l & 3;
        float f = 0.5F;
        float f1 = 1.0F;

        if ((l & 4) != 0) {
            f = 0.0F;
            f1 = 0.5F;
        }

        float f2 = 0.0F;
        float f3 = 1.0F;
        float f4 = 0.0F;
        float f5 = 0.5F;
        boolean flag = true;
        Block block;
        int j1;
        int k1;

        if (i1 == 0) {
            f2 = 0.5F;
            f5 = 1.0F;
            block = iblockaccess.func_147439_a(i + 1, j, k);
            j1 = iblockaccess.func_72805_g(i + 1, j, k);
            if (func_150148_a(block) && (l & 4) == (j1 & 4)) {
                k1 = j1 & 3;
                if (k1 == 3 && !this.func_150146_f(iblockaccess, i, j, k + 1, l)) {
                    f5 = 0.5F;
                    flag = false;
                } else if (k1 == 2 && !this.func_150146_f(iblockaccess, i, j, k - 1, l)) {
                    f4 = 0.5F;
                    flag = false;
                }
            }
        } else if (i1 == 1) {
            f3 = 0.5F;
            f5 = 1.0F;
            block = iblockaccess.func_147439_a(i - 1, j, k);
            j1 = iblockaccess.func_72805_g(i - 1, j, k);
            if (func_150148_a(block) && (l & 4) == (j1 & 4)) {
                k1 = j1 & 3;
                if (k1 == 3 && !this.func_150146_f(iblockaccess, i, j, k + 1, l)) {
                    f5 = 0.5F;
                    flag = false;
                } else if (k1 == 2 && !this.func_150146_f(iblockaccess, i, j, k - 1, l)) {
                    f4 = 0.5F;
                    flag = false;
                }
            }
        } else if (i1 == 2) {
            f4 = 0.5F;
            f5 = 1.0F;
            block = iblockaccess.func_147439_a(i, j, k + 1);
            j1 = iblockaccess.func_72805_g(i, j, k + 1);
            if (func_150148_a(block) && (l & 4) == (j1 & 4)) {
                k1 = j1 & 3;
                if (k1 == 1 && !this.func_150146_f(iblockaccess, i + 1, j, k, l)) {
                    f3 = 0.5F;
                    flag = false;
                } else if (k1 == 0 && !this.func_150146_f(iblockaccess, i - 1, j, k, l)) {
                    f2 = 0.5F;
                    flag = false;
                }
            }
        } else if (i1 == 3) {
            block = iblockaccess.func_147439_a(i, j, k - 1);
            j1 = iblockaccess.func_72805_g(i, j, k - 1);
            if (func_150148_a(block) && (l & 4) == (j1 & 4)) {
                k1 = j1 & 3;
                if (k1 == 1 && !this.func_150146_f(iblockaccess, i + 1, j, k, l)) {
                    f3 = 0.5F;
                    flag = false;
                } else if (k1 == 0 && !this.func_150146_f(iblockaccess, i - 1, j, k, l)) {
                    f2 = 0.5F;
                    flag = false;
                }
            }
        }

        this.func_149676_a(f2, f, f4, f3, f1, f5);
        return flag;
    }

    public boolean func_150144_g(IBlockAccess iblockaccess, int i, int j, int k) {
        int l = iblockaccess.func_72805_g(i, j, k);
        int i1 = l & 3;
        float f = 0.5F;
        float f1 = 1.0F;

        if ((l & 4) != 0) {
            f = 0.0F;
            f1 = 0.5F;
        }

        float f2 = 0.0F;
        float f3 = 0.5F;
        float f4 = 0.5F;
        float f5 = 1.0F;
        boolean flag = false;
        Block block;
        int j1;
        int k1;

        if (i1 == 0) {
            block = iblockaccess.func_147439_a(i - 1, j, k);
            j1 = iblockaccess.func_72805_g(i - 1, j, k);
            if (func_150148_a(block) && (l & 4) == (j1 & 4)) {
                k1 = j1 & 3;
                if (k1 == 3 && !this.func_150146_f(iblockaccess, i, j, k - 1, l)) {
                    f4 = 0.0F;
                    f5 = 0.5F;
                    flag = true;
                } else if (k1 == 2 && !this.func_150146_f(iblockaccess, i, j, k + 1, l)) {
                    f4 = 0.5F;
                    f5 = 1.0F;
                    flag = true;
                }
            }
        } else if (i1 == 1) {
            block = iblockaccess.func_147439_a(i + 1, j, k);
            j1 = iblockaccess.func_72805_g(i + 1, j, k);
            if (func_150148_a(block) && (l & 4) == (j1 & 4)) {
                f2 = 0.5F;
                f3 = 1.0F;
                k1 = j1 & 3;
                if (k1 == 3 && !this.func_150146_f(iblockaccess, i, j, k - 1, l)) {
                    f4 = 0.0F;
                    f5 = 0.5F;
                    flag = true;
                } else if (k1 == 2 && !this.func_150146_f(iblockaccess, i, j, k + 1, l)) {
                    f4 = 0.5F;
                    f5 = 1.0F;
                    flag = true;
                }
            }
        } else if (i1 == 2) {
            block = iblockaccess.func_147439_a(i, j, k - 1);
            j1 = iblockaccess.func_72805_g(i, j, k - 1);
            if (func_150148_a(block) && (l & 4) == (j1 & 4)) {
                f4 = 0.0F;
                f5 = 0.5F;
                k1 = j1 & 3;
                if (k1 == 1 && !this.func_150146_f(iblockaccess, i - 1, j, k, l)) {
                    flag = true;
                } else if (k1 == 0 && !this.func_150146_f(iblockaccess, i + 1, j, k, l)) {
                    f2 = 0.5F;
                    f3 = 1.0F;
                    flag = true;
                }
            }
        } else if (i1 == 3) {
            block = iblockaccess.func_147439_a(i, j, k + 1);
            j1 = iblockaccess.func_72805_g(i, j, k + 1);
            if (func_150148_a(block) && (l & 4) == (j1 & 4)) {
                k1 = j1 & 3;
                if (k1 == 1 && !this.func_150146_f(iblockaccess, i - 1, j, k, l)) {
                    flag = true;
                } else if (k1 == 0 && !this.func_150146_f(iblockaccess, i + 1, j, k, l)) {
                    f2 = 0.5F;
                    f3 = 1.0F;
                    flag = true;
                }
            }
        }

        if (flag) {
            this.func_149676_a(f2, f, f4, f3, f1, f5);
        }

        return flag;
    }

    @Override
    public void func_149743_a(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, List list, Entity entity) {
        this.func_150147_e(world, i, j, k);
        super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
        boolean flag = this.func_150145_f(world, i, j, k);

        super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
        if (flag && this.func_150144_g(world, i, j, k)) {
            super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
        }

        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void func_149699_a(World world, int i, int j, int k, EntityPlayer entityplayer) {
        this.field_150149_b.func_149699_a(world, i, j, k, entityplayer);
    }

    @Override
    public void func_149664_b(World world, int i, int j, int k, int l) {
        this.field_150149_b.func_149664_b(world, i, j, k, l);
    }

    @Override
    public float func_149638_a(Entity entity) {
        return this.field_150149_b.func_149638_a(entity);
    }

    @Override
    public int func_149738_a(World world) {
        return this.field_150149_b.func_149738_a(world);
    }

    @Override
    public void func_149640_a(World world, int i, int j, int k, Entity entity, Vec3 vec3) {
        this.field_150149_b.func_149640_a(world, i, j, k, entity, vec3);
    }

    @Override
    public boolean func_149703_v() {
        return this.field_150149_b.func_149703_v();
    }

    @Override
    public boolean func_149678_a(int i, boolean flag) {
        return this.field_150149_b.func_149678_a(i, flag);
    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        return this.field_150149_b.func_149742_c(world, i, j, k);
    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {
        this.func_149695_a(world, i, j, k, Blocks.field_150350_a);
        this.field_150149_b.func_149726_b(world, i, j, k);
    }

    @Override
    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {
        this.field_150149_b.func_149749_a(world, i, j, k, block, l);
    }

    @Override
    public void func_149724_b(World world, int i, int j, int k, Entity entity) {
        this.field_150149_b.func_149724_b(world, i, j, k, entity);
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        this.field_150149_b.func_149674_a(world, i, j, k, random);
    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        return this.field_150149_b.func_149727_a(world, i, j, k, entityplayer, 0, 0.0F, 0.0F, 0.0F);
    }

    @Override
    public void func_149723_a(World world, int i, int j, int k, Explosion explosion) {
        this.field_150149_b.func_149723_a(world, i, j, k, explosion);
    }

    @Override
    public MapColor func_149728_f(int i) {
        return this.field_150149_b.func_149728_f(this.field_150151_M);
    }

    @Override
    public void func_149689_a(World world, int i, int j, int k, EntityLivingBase entitylivingbase, ItemStack itemstack) {
        int l = MathHelper.func_76128_c((double) (entitylivingbase.field_70177_z * 4.0F / 360.0F) + 0.5D) & 3;
        int i1 = world.func_72805_g(i, j, k) & 4;

        if (l == 0) {
            world.func_72921_c(i, j, k, 2 | i1, 2);
        }

        if (l == 1) {
            world.func_72921_c(i, j, k, 1 | i1, 2);
        }

        if (l == 2) {
            world.func_72921_c(i, j, k, 3 | i1, 2);
        }

        if (l == 3) {
            world.func_72921_c(i, j, k, 0 | i1, 2);
        }

    }

    @Override
    public int func_149660_a(World world, int i, int j, int k, int l, float f, float f1, float f2, int i1) {
        return l != 0 && (l == 1 || (double) f1 <= 0.5D) ? i1 : i1 | 4;
    }

    @Override
    public MovingObjectPosition func_149731_a(World world, int i, int j, int k, Vec3 vec3, Vec3 vec31) {
        MovingObjectPosition[] amovingobjectposition = new MovingObjectPosition[8];
        int l = world.func_72805_g(i, j, k);
        int i1 = l & 3;
        boolean flag = (l & 4) == 4;
        int[] aint = BlockStairs.field_150150_a[i1 + (flag ? 4 : 0)];

        this.field_150152_N = true;

        int j1;
        int k1;
        int l1;

        for (int i2 = 0; i2 < 8; ++i2) {
            this.field_150153_O = i2;
            int[] aint1 = aint;

            j1 = aint.length;

            for (l1 = 0; l1 < j1; ++l1) {
                k1 = aint1[l1];
                if (k1 == i2) {
                    ;
                }
            }

            amovingobjectposition[i2] = super.func_149731_a(world, i, j, k, vec3, vec31);
        }

        int[] aint2 = aint;
        int j2 = aint.length;

        for (j1 = 0; j1 < j2; ++j1) {
            l1 = aint2[j1];
            amovingobjectposition[l1] = null;
        }

        MovingObjectPosition movingobjectposition = null;
        double d0 = 0.0D;
        MovingObjectPosition[] amovingobjectposition1 = amovingobjectposition;

        k1 = amovingobjectposition.length;

        for (int k2 = 0; k2 < k1; ++k2) {
            MovingObjectPosition movingobjectposition1 = amovingobjectposition1[k2];

            if (movingobjectposition1 != null) {
                double d1 = movingobjectposition1.field_72307_f.func_72436_e(vec31);

                if (d1 > d0) {
                    movingobjectposition = movingobjectposition1;
                    d0 = d1;
                }
            }
        }

        return movingobjectposition;
    }
}
