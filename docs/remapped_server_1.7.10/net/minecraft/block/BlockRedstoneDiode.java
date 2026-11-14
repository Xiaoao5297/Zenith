package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockRedstoneDiode extends BlockDirectional {

    protected final boolean field_149914_a;

    protected BlockRedstoneDiode(boolean flag) {
        super(Material.field_151594_q);
        this.field_149914_a = flag;
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        return !World.func_147466_a(world, i, j - 1, k) ? false : super.func_149742_c(world, i, j, k);
    }

    @Override
    public boolean func_149718_j(World world, int i, int j, int k) {
        return !World.func_147466_a(world, i, j - 1, k) ? false : super.func_149718_j(world, i, j, k);
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        int l = world.func_72805_g(i, j, k);

        if (!this.func_149910_g(world, i, j, k, l)) {
            boolean flag = this.func_149900_a(world, i, j, k, l);

            if (this.field_149914_a && !flag) {
                world.func_147465_d(i, j, k, this.func_149898_i(), l, 2);
            } else if (!this.field_149914_a) {
                world.func_147465_d(i, j, k, this.func_149906_e(), l, 2);
                if (!flag) {
                    world.func_147454_a(i, j, k, this.func_149906_e(), this.func_149899_k(l), -1);
                }
            }
        }

    }

    @Override
    public int func_149645_b() {
        return 36;
    }

    protected boolean func_149905_c(int i) {
        return this.field_149914_a;
    }

    @Override
    public int func_149748_c(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return this.func_149709_b(iblockaccess, i, j, k, l);
    }

    @Override
    public int func_149709_b(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        int i1 = iblockaccess.func_72805_g(i, j, k);

        if (!this.func_149905_c(i1)) {
            return 0;
        } else {
            int j1 = func_149895_l(i1);

            return j1 == 0 && l == 3 ? this.func_149904_f(iblockaccess, i, j, k, i1) : (j1 == 1 && l == 4 ? this.func_149904_f(iblockaccess, i, j, k, i1) : (j1 == 2 && l == 2 ? this.func_149904_f(iblockaccess, i, j, k, i1) : (j1 == 3 && l == 5 ? this.func_149904_f(iblockaccess, i, j, k, i1) : 0)));
        }
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        if (!this.func_149718_j(world, i, j, k)) {
            this.func_149697_b(world, i, j, k, world.func_72805_g(i, j, k), 0);
            world.func_147468_f(i, j, k);
            world.func_147459_d(i + 1, j, k, this);
            world.func_147459_d(i - 1, j, k, this);
            world.func_147459_d(i, j, k + 1, this);
            world.func_147459_d(i, j, k - 1, this);
            world.func_147459_d(i, j - 1, k, this);
            world.func_147459_d(i, j + 1, k, this);
        } else {
            this.func_149897_b(world, i, j, k, block);
        }
    }

    protected void func_149897_b(World world, int i, int j, int k, Block block) {
        int l = world.func_72805_g(i, j, k);

        if (!this.func_149910_g(world, i, j, k, l)) {
            boolean flag = this.func_149900_a(world, i, j, k, l);

            if ((this.field_149914_a && !flag || !this.field_149914_a && flag) && !world.func_147477_a(i, j, k, this)) {
                byte b0 = -1;

                if (this.func_149912_i(world, i, j, k, l)) {
                    b0 = -3;
                } else if (this.field_149914_a) {
                    b0 = -2;
                }

                world.func_147454_a(i, j, k, this, this.func_149901_b(l), b0);
            }
        }

    }

    public boolean func_149910_g(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return false;
    }

    protected boolean func_149900_a(World world, int i, int j, int k, int l) {
        return this.func_149903_h(world, i, j, k, l) > 0;
    }

    protected int func_149903_h(World world, int i, int j, int k, int l) {
        int i1 = func_149895_l(l);
        int j1 = i + Direction.field_71583_a[i1];
        int k1 = k + Direction.field_71581_b[i1];
        int l1 = world.func_72878_l(j1, j, k1, Direction.field_71582_c[i1]);

        return l1 >= 15 ? l1 : Math.max(l1, world.func_147439_a(j1, j, k1) == Blocks.field_150488_af ? world.func_72805_g(j1, j, k1) : 0);
    }

    protected int func_149902_h(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        int i1 = func_149895_l(l);

        switch (i1) {
            case 0:
            case 2:
                return Math.max(this.func_149913_i(iblockaccess, i - 1, j, k, 4), this.func_149913_i(iblockaccess, i + 1, j, k, 5));
            case 1:
            case 3:
                return Math.max(this.func_149913_i(iblockaccess, i, j, k + 1, 3), this.func_149913_i(iblockaccess, i, j, k - 1, 2));
            default:
                return 0;
        }
    }

    protected int func_149913_i(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        Block block = iblockaccess.func_147439_a(i, j, k);

        return this.func_149908_a(block) ? (block == Blocks.field_150488_af ? iblockaccess.func_72805_g(i, j, k) : iblockaccess.func_72879_k(i, j, k, l)) : 0;
    }

    @Override
    public boolean func_149744_f() {
        return true;
    }

    @Override
    public void func_149689_a(World world, int i, int j, int k, EntityLivingBase entitylivingbase, ItemStack itemstack) {
        int l = ((MathHelper.func_76128_c((double) (entitylivingbase.field_70177_z * 4.0F / 360.0F) + 0.5D) & 3) + 2) % 4;

        world.func_72921_c(i, j, k, l, 3);
        boolean flag = this.func_149900_a(world, i, j, k, l);

        if (flag) {
            world.func_147464_a(i, j, k, this, 1);
        }

    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {
        this.func_149911_e(world, i, j, k);
    }

    protected void func_149911_e(World world, int i, int j, int k) {
        int l = func_149895_l(world.func_72805_g(i, j, k));

        if (l == 1) {
            world.func_147460_e(i + 1, j, k, this);
            world.func_147441_b(i + 1, j, k, this, 4);
        }

        if (l == 3) {
            world.func_147460_e(i - 1, j, k, this);
            world.func_147441_b(i - 1, j, k, this, 5);
        }

        if (l == 2) {
            world.func_147460_e(i, j, k + 1, this);
            world.func_147441_b(i, j, k + 1, this, 2);
        }

        if (l == 0) {
            world.func_147460_e(i, j, k - 1, this);
            world.func_147441_b(i, j, k - 1, this, 3);
        }

    }

    @Override
    public void func_149664_b(World world, int i, int j, int k, int l) {
        if (this.field_149914_a) {
            world.func_147459_d(i + 1, j, k, this);
            world.func_147459_d(i - 1, j, k, this);
            world.func_147459_d(i, j, k + 1, this);
            world.func_147459_d(i, j, k - 1, this);
            world.func_147459_d(i, j - 1, k, this);
            world.func_147459_d(i, j + 1, k, this);
        }

        super.func_149664_b(world, i, j, k, l);
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }

    protected boolean func_149908_a(Block block) {
        return block.func_149744_f();
    }

    protected int func_149904_f(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return 15;
    }

    public static boolean func_149909_d(Block block) {
        return Blocks.field_150413_aR.func_149907_e(block) || Blocks.field_150441_bU.func_149907_e(block);
    }

    public boolean func_149907_e(Block block) {
        return block == this.func_149906_e() || block == this.func_149898_i();
    }

    public boolean func_149912_i(World world, int i, int j, int k, int l) {
        int i1 = func_149895_l(l);

        if (func_149909_d(world.func_147439_a(i - Direction.field_71583_a[i1], j, k - Direction.field_71581_b[i1]))) {
            int j1 = world.func_72805_g(i - Direction.field_71583_a[i1], j, k - Direction.field_71581_b[i1]);
            int k1 = func_149895_l(j1);

            return k1 != i1;
        } else {
            return false;
        }
    }

    protected int func_149899_k(int i) {
        return this.func_149901_b(i);
    }

    protected abstract int func_149901_b(int i);

    protected abstract BlockRedstoneDiode func_149906_e();

    protected abstract BlockRedstoneDiode func_149898_i();

    @Override
    public boolean func_149667_c(Block block) {
        return this.func_149907_e(block);
    }
}
