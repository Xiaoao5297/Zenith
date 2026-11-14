package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockLilyPad extends BlockBush {

    protected BlockLilyPad() {
        float f = 0.5F;
        float f1 = 0.015625F;

        this.func_149676_a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
        this.func_149647_a(CreativeTabs.field_78031_c);
    }

    @Override
    public int func_149645_b() {
        return 23;
    }

    @Override
    public void func_149743_a(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, List list, Entity entity) {
        if (entity == null || !(entity instanceof EntityBoat)) {
            super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
        }

    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        return AxisAlignedBB.func_72330_a((double) i + this.field_149759_B, (double) j + this.field_149760_C, (double) k + this.field_149754_D, (double) i + this.field_149755_E, (double) j + this.field_149756_F, (double) k + this.field_149757_G);
    }

    @Override
    protected boolean func_149854_a(Block block) {
        return block == Blocks.field_150355_j;
    }

    @Override
    public boolean func_149718_j(World world, int i, int j, int k) {
        return j >= 0 && j < 256 ? world.func_147439_a(i, j - 1, k).func_149688_o() == Material.field_151586_h && world.func_72805_g(i, j - 1, k) == 0 : false;
    }
}
