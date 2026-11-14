package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemLead;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFence extends Block {

    private final String field_149827_a;

    public BlockFence(String s, Material material) {
        super(material);
        this.field_149827_a = s;
        this.func_149647_a(CreativeTabs.field_78031_c);
    }

    @Override
    public void func_149743_a(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, List list, Entity entity) {
        boolean flag = this.func_149826_e(world, i, j, k - 1);
        boolean flag1 = this.func_149826_e(world, i, j, k + 1);
        boolean flag2 = this.func_149826_e(world, i - 1, j, k);
        boolean flag3 = this.func_149826_e(world, i + 1, j, k);
        float f = 0.375F;
        float f1 = 0.625F;
        float f2 = 0.375F;
        float f3 = 0.625F;

        if (flag) {
            f2 = 0.0F;
        }

        if (flag1) {
            f3 = 1.0F;
        }

        if (flag || flag1) {
            this.func_149676_a(f, 0.0F, f2, f1, 1.5F, f3);
            super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
        }

        f2 = 0.375F;
        f3 = 0.625F;
        if (flag2) {
            f = 0.0F;
        }

        if (flag3) {
            f1 = 1.0F;
        }

        if (flag2 || flag3 || !flag && !flag1) {
            this.func_149676_a(f, 0.0F, f2, f1, 1.5F, f3);
            super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
        }

        if (flag) {
            f2 = 0.0F;
        }

        if (flag1) {
            f3 = 1.0F;
        }

        this.func_149676_a(f, 0.0F, f2, f1, 1.0F, f3);
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        boolean flag = this.func_149826_e(iblockaccess, i, j, k - 1);
        boolean flag1 = this.func_149826_e(iblockaccess, i, j, k + 1);
        boolean flag2 = this.func_149826_e(iblockaccess, i - 1, j, k);
        boolean flag3 = this.func_149826_e(iblockaccess, i + 1, j, k);
        float f = 0.375F;
        float f1 = 0.625F;
        float f2 = 0.375F;
        float f3 = 0.625F;

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

        this.func_149676_a(f, 0.0F, f2, f1, 1.0F, f3);
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
        return false;
    }

    @Override
    public int func_149645_b() {
        return 11;
    }

    public boolean func_149826_e(IBlockAccess iblockaccess, int i, int j, int k) {
        Block block = iblockaccess.func_147439_a(i, j, k);

        return block != this && block != Blocks.field_150396_be ? (block.field_149764_J.func_76218_k() && block.func_149686_d() ? block.field_149764_J != Material.field_151572_C : false) : true;
    }

    public static boolean func_149825_a(Block block) {
        return block == Blocks.field_150422_aJ || block == Blocks.field_150386_bk;
    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        return world.field_72995_K ? true : ItemLead.func_150909_a(entityplayer, world, i, j, k);
    }
}
