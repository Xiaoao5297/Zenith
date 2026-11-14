package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDaylightDetector;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDaylightDetector extends BlockContainer {

    private IIcon[] field_149958_a = new IIcon[2];

    public BlockDaylightDetector() {
        super(Material.field_151575_d);
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
        this.func_149647_a(CreativeTabs.field_78028_d);
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
    }

    @Override
    public int func_149709_b(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return iblockaccess.func_72805_g(i, j, k);
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {}

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {}

    @Override
    public void func_149726_b(World world, int i, int j, int k) {}

    public void func_149957_e(World world, int i, int j, int k) {
        if (!world.field_73011_w.field_76576_e) {
            int l = world.func_72805_g(i, j, k);
            int i1 = world.func_72972_b(EnumSkyBlock.Sky, i, j, k) - world.field_73008_k;
            float f = world.func_72929_e(1.0F);

            if (f < 3.1415927F) {
                f += (0.0F - f) * 0.2F;
            } else {
                f += (6.2831855F - f) * 0.2F;
            }

            i1 = Math.round((float) i1 * MathHelper.func_76134_b(f));
            if (i1 < 0) {
                i1 = 0;
            }

            if (i1 > 15) {
                i1 = 15;
            }

            if (l != i1) {
                world.func_72921_c(i, j, k, i1, 3);
            }

        }
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
    public boolean func_149744_f() {
        return true;
    }

    @Override
    public TileEntity func_149915_a(World world, int i) {
        return new TileEntityDaylightDetector();
    }
}
