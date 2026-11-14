package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPane extends Block {

    private final String field_150100_a;
    private final boolean field_150099_b;
    private final String field_150101_M;

    protected BlockPane(String s, String s1, Material material, boolean flag) {
        super(material);
        this.field_150100_a = s1;
        this.field_150099_b = flag;
        this.field_150101_M = s;
        this.func_149647_a(CreativeTabs.field_78031_c);
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return !this.field_150099_b ? null : super.func_149650_a(i, random, j);
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
        return this.field_149764_J == Material.field_151592_s ? 41 : 18;
    }

    @Override
    public void func_149743_a(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, List list, Entity entity) {
        boolean flag = this.func_150098_a(world.func_147439_a(i, j, k - 1));
        boolean flag1 = this.func_150098_a(world.func_147439_a(i, j, k + 1));
        boolean flag2 = this.func_150098_a(world.func_147439_a(i - 1, j, k));
        boolean flag3 = this.func_150098_a(world.func_147439_a(i + 1, j, k));

        if ((!flag2 || !flag3) && (flag2 || flag3 || flag || flag1)) {
            if (flag2 && !flag3) {
                this.func_149676_a(0.0F, 0.0F, 0.4375F, 0.5F, 1.0F, 0.5625F);
                super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
            } else if (!flag2 && flag3) {
                this.func_149676_a(0.5F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
                super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
            }
        } else {
            this.func_149676_a(0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
            super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
        }

        if ((!flag || !flag1) && (flag2 || flag3 || flag || flag1)) {
            if (flag && !flag1) {
                this.func_149676_a(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 0.5F);
                super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
            } else if (!flag && flag1) {
                this.func_149676_a(0.4375F, 0.0F, 0.5F, 0.5625F, 1.0F, 1.0F);
                super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
            }
        } else {
            this.func_149676_a(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 1.0F);
            super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
        }

    }

    @Override
    public void func_149683_g() {
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        float f = 0.4375F;
        float f1 = 0.5625F;
        float f2 = 0.4375F;
        float f3 = 0.5625F;
        boolean flag = this.func_150098_a(iblockaccess.func_147439_a(i, j, k - 1));
        boolean flag1 = this.func_150098_a(iblockaccess.func_147439_a(i, j, k + 1));
        boolean flag2 = this.func_150098_a(iblockaccess.func_147439_a(i - 1, j, k));
        boolean flag3 = this.func_150098_a(iblockaccess.func_147439_a(i + 1, j, k));

        if ((!flag2 || !flag3) && (flag2 || flag3 || flag || flag1)) {
            if (flag2 && !flag3) {
                f = 0.0F;
            } else if (!flag2 && flag3) {
                f1 = 1.0F;
            }
        } else {
            f = 0.0F;
            f1 = 1.0F;
        }

        if ((!flag || !flag1) && (flag2 || flag3 || flag || flag1)) {
            if (flag && !flag1) {
                f2 = 0.0F;
            } else if (!flag && flag1) {
                f3 = 1.0F;
            }
        } else {
            f2 = 0.0F;
            f3 = 1.0F;
        }

        this.func_149676_a(f, 0.0F, f2, f1, 1.0F, f3);
    }

    public final boolean func_150098_a(Block block) {
        return block.func_149730_j() || block == this || block == Blocks.field_150359_w || block == Blocks.field_150399_cn || block == Blocks.field_150397_co || block instanceof BlockPane;
    }

    @Override
    protected boolean func_149700_E() {
        return true;
    }

    @Override
    protected ItemStack func_149644_j(int i) {
        return new ItemStack(Item.func_150898_a(this), 1, i);
    }
}
