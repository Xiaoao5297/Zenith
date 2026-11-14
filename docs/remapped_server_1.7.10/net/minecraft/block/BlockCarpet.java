package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCarpet extends Block {

    protected BlockCarpet() {
        super(Material.field_151593_r);
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
        this.func_149675_a(true);
        this.func_149647_a(CreativeTabs.field_78031_c);
        this.func_150089_b(0);
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        byte b0 = 0;
        float f = 0.0625F;

        return AxisAlignedBB.func_72330_a((double) i + this.field_149759_B, (double) j + this.field_149760_C, (double) k + this.field_149754_D, (double) i + this.field_149755_E, (double) ((float) j + (float) b0 * f), (double) k + this.field_149757_G);
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
    public void func_149683_g() {
        this.func_150089_b(0);
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        this.func_150089_b(iblockaccess.func_72805_g(i, j, k));
    }

    protected void func_150089_b(int i) {
        byte b0 = 0;
        float f = (float) (1 * (1 + b0)) / 16.0F;

        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        return super.func_149742_c(world, i, j, k) && this.func_149718_j(world, i, j, k);
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        this.func_150090_e(world, i, j, k);
    }

    private boolean func_150090_e(World world, int i, int j, int k) {
        if (!this.func_149718_j(world, i, j, k)) {
            this.func_149697_b(world, i, j, k, world.func_72805_g(i, j, k), 0);
            world.func_147468_f(i, j, k);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean func_149718_j(World world, int i, int j, int k) {
        return !world.func_147437_c(i, j - 1, k);
    }

    @Override
    public int func_149692_a(int i) {
        return i;
    }
}
