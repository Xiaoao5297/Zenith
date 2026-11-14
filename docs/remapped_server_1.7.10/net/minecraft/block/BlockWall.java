package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWall extends Block {

    public static final String[] field_150092_a = new String[]{"normal", "mossy"};

    public BlockWall(Block block) {
        super(block.field_149764_J);
        this.func_149711_c(block.field_149782_v);
        this.func_149752_b(block.field_149781_w / 3.0F);
        this.func_149672_a(block.field_149762_H);
        this.func_149647_a(CreativeTabs.field_78030_b);
    }

    @Override
    public int func_149645_b() {
        return 32;
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    @Override
    public boolean func_149655_b(IBlockAccess iblockaccess, int i, int j, int k) {
        return false;
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        boolean flag = this.func_150091_e(iblockaccess, i, j, k - 1);
        boolean flag1 = this.func_150091_e(iblockaccess, i, j, k + 1);
        boolean flag2 = this.func_150091_e(iblockaccess, i - 1, j, k);
        boolean flag3 = this.func_150091_e(iblockaccess, i + 1, j, k);
        float f = 0.25F;
        float f1 = 0.75F;
        float f2 = 0.25F;
        float f3 = 0.75F;
        float f4 = 1.0F;

        if (flag) {
            f2 = 0.0F;
        }

        if (flag1) {
            f3 = 1.0F;
        }

        if (flag2) {
            f = 0.0F;
        }

        if (flag3) {
            f1 = 1.0F;
        }

        if (flag && flag1 && !flag2 && !flag3) {
            f4 = 0.8125F;
            f = 0.3125F;
            f1 = 0.6875F;
        } else if (!flag && !flag1 && flag2 && flag3) {
            f4 = 0.8125F;
            f2 = 0.3125F;
            f3 = 0.6875F;
        }

        this.func_149676_a(f, 0.0F, f2, f1, f4, f3);
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        this.func_149719_a(world, i, j, k);
        this.field_149756_F = 1.5D;
        return super.func_149668_a(world, i, j, k);
    }

    public boolean func_150091_e(IBlockAccess iblockaccess, int i, int j, int k) {
        Block block = iblockaccess.func_147439_a(i, j, k);

        return block != this && block != Blocks.field_150396_be ? (block.field_149764_J.func_76218_k() && block.func_149686_d() ? block.field_149764_J != Material.field_151572_C : false) : true;
    }

    @Override
    public int func_149692_a(int i) {
        return i;
    }
}
