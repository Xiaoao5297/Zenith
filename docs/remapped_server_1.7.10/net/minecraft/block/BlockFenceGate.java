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

public class BlockFenceGate extends BlockDirectional {

    public BlockFenceGate() {
        super(Material.field_151575_d);
        this.func_149647_a(CreativeTabs.field_78028_d);
    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        return !world.func_147439_a(i, j - 1, k).func_149688_o().func_76220_a() ? false : super.func_149742_c(world, i, j, k);
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        int l = world.func_72805_g(i, j, k);

        return func_149896_b(l) ? null : (l != 2 && l != 0 ? AxisAlignedBB.func_72330_a((double) ((float) i + 0.375F), (double) j, (double) k, (double) ((float) i + 0.625F), (double) ((float) j + 1.5F), (double) (k + 1)) : AxisAlignedBB.func_72330_a((double) i, (double) j, (double) ((float) k + 0.375F), (double) (i + 1), (double) ((float) j + 1.5F), (double) ((float) k + 0.625F)));
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        int l = func_149895_l(iblockaccess.func_72805_g(i, j, k));

        if (l != 2 && l != 0) {
            this.func_149676_a(0.375F, 0.0F, 0.0F, 0.625F, 1.0F, 1.0F);
        } else {
            this.func_149676_a(0.0F, 0.0F, 0.375F, 1.0F, 1.0F, 0.625F);
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
    public boolean func_149655_b(IBlockAccess iblockaccess, int i, int j, int k) {
        return func_149896_b(iblockaccess.func_72805_g(i, j, k));
    }

    @Override
    public int func_149645_b() {
        return 21;
    }

    @Override
    public void func_149689_a(World world, int i, int j, int k, EntityLivingBase entitylivingbase, ItemStack itemstack) {
        int l = (MathHelper.func_76128_c((double) (entitylivingbase.field_70177_z * 4.0F / 360.0F) + 0.5D) & 3) % 4;

        world.func_72921_c(i, j, k, l, 2);
    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        int i1 = world.func_72805_g(i, j, k);

        if (func_149896_b(i1)) {
            world.func_72921_c(i, j, k, i1 & -5, 2);
        } else {
            int j1 = (MathHelper.func_76128_c((double) (entityplayer.field_70177_z * 4.0F / 360.0F) + 0.5D) & 3) % 4;
            int k1 = func_149895_l(i1);

            if (k1 == (j1 + 2) % 4) {
                i1 = j1;
            }

            world.func_72921_c(i, j, k, i1 | 4, 2);
        }

        world.func_72889_a(entityplayer, 1003, i, j, k, 0);
        return true;
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        if (!world.field_72995_K) {
            int l = world.func_72805_g(i, j, k);
            boolean flag = world.func_72864_z(i, j, k);

            if (flag || block.func_149744_f()) {
                if (flag && !func_149896_b(l)) {
                    world.func_72921_c(i, j, k, l | 4, 2);
                    world.func_72889_a((EntityPlayer) null, 1003, i, j, k, 0);
                } else if (!flag && func_149896_b(l)) {
                    world.func_72921_c(i, j, k, l & -5, 2);
                    world.func_72889_a((EntityPlayer) null, 1003, i, j, k, 0);
                }
            }

        }
    }

    public static boolean func_149896_b(int i) {
        return (i & 4) != 0;
    }
}
