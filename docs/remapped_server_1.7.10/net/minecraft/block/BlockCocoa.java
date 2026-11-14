package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCocoa extends BlockDirectional implements IGrowable {

    public BlockCocoa() {
        super(Material.field_151585_k);
        this.func_149675_a(true);
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        if (!this.func_149718_j(world, i, j, k)) {
            this.func_149697_b(world, i, j, k, world.func_72805_g(i, j, k), 0);
            world.func_147465_d(i, j, k, func_149729_e(0), 0, 2);
        } else if (world.field_73012_v.nextInt(5) == 0) {
            int l = world.func_72805_g(i, j, k);
            int i1 = func_149987_c(l);

            if (i1 < 2) {
                ++i1;
                world.func_72921_c(i, j, k, i1 << 2 | func_149895_l(l), 2);
            }
        }

    }

    @Override
    public boolean func_149718_j(World world, int i, int j, int k) {
        int l = func_149895_l(world.func_72805_g(i, j, k));

        i += Direction.field_71583_a[l];
        k += Direction.field_71581_b[l];
        Block block = world.func_147439_a(i, j, k);

        return block == Blocks.field_150364_r && BlockLog.func_150165_c(world.func_72805_g(i, j, k)) == 3;
    }

    @Override
    public int func_149645_b() {
        return 28;
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        this.func_149719_a(world, i, j, k);
        return super.func_149668_a(world, i, j, k);
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        int l = iblockaccess.func_72805_g(i, j, k);
        int i1 = func_149895_l(l);
        int j1 = func_149987_c(l);
        int k1 = 4 + j1 * 2;
        int l1 = 5 + j1 * 2;
        float f = (float) k1 / 2.0F;

        switch (i1) {
            case 0:
                this.func_149676_a((8.0F - f) / 16.0F, (12.0F - (float) l1) / 16.0F, (15.0F - (float) k1) / 16.0F, (8.0F + f) / 16.0F, 0.75F, 0.9375F);
                break;
            case 1:
                this.func_149676_a(0.0625F, (12.0F - (float) l1) / 16.0F, (8.0F - f) / 16.0F, (1.0F + (float) k1) / 16.0F, 0.75F, (8.0F + f) / 16.0F);
                break;
            case 2:
                this.func_149676_a((8.0F - f) / 16.0F, (12.0F - (float) l1) / 16.0F, 0.0625F, (8.0F + f) / 16.0F, 0.75F, (1.0F + (float) k1) / 16.0F);
                break;
            case 3:
                this.func_149676_a((15.0F - (float) k1) / 16.0F, (12.0F - (float) l1) / 16.0F, (8.0F - f) / 16.0F, 0.9375F, 0.75F, (8.0F + f) / 16.0F);
        }

    }

    @Override
    public void func_149689_a(World world, int i, int j, int k, EntityLivingBase entitylivingbase, ItemStack itemstack) {
        int l = ((MathHelper.func_76128_c((double) (entitylivingbase.field_70177_z * 4.0F / 360.0F) + 0.5D) & 3) + 0) % 4;

        world.func_72921_c(i, j, k, l, 2);
    }

    @Override
    public int func_149660_a(World world, int i, int j, int k, int l, float f, float f1, float f2, int i1) {
        if (l == 1 || l == 0) {
            l = 2;
        }

        return Direction.field_71580_e[Direction.field_71579_d[l]];
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        if (!this.func_149718_j(world, i, j, k)) {
            this.func_149697_b(world, i, j, k, world.func_72805_g(i, j, k), 0);
            world.func_147465_d(i, j, k, func_149729_e(0), 0, 2);
        }

    }

    public static int func_149987_c(int i) {
        return (i & 12) >> 2;
    }

    @Override
    public void func_149690_a(World world, int i, int j, int k, int l, float f, int i1) {
        int j1 = func_149987_c(l);
        byte b0 = 1;

        if (j1 >= 2) {
            b0 = 3;
        }

        for (int k1 = 0; k1 < b0; ++k1) {
            this.func_149642_a(world, i, j, k, new ItemStack(Items.field_151100_aR, 1, 3));
        }

    }

    @Override
    public int func_149643_k(World world, int i, int j, int k) {
        return 3;
    }

    @Override
    public boolean func_149851_a(World world, int i, int j, int k, boolean flag) {
        int l = world.func_72805_g(i, j, k);
        int i1 = func_149987_c(l);

        return i1 < 2;
    }

    @Override
    public boolean func_149852_a(World world, Random random, int i, int j, int k) {
        return true;
    }

    @Override
    public void func_149853_b(World world, Random random, int i, int j, int k) {
        int l = world.func_72805_g(i, j, k);
        int i1 = BlockDirectional.func_149895_l(l);
        int j1 = func_149987_c(l);

        ++j1;
        world.func_72921_c(i, j, k, j1 << 2 | i1, 2);
    }
}
