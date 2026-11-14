package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockSoulSand extends Block {

    public BlockSoulSand() {
        super(Material.field_151595_p);
        this.func_149647_a(CreativeTabs.field_78030_b);
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        float f = 0.125F;

        return AxisAlignedBB.func_72330_a((double) i, (double) j, (double) k, (double) (i + 1), (double) ((float) (j + 1) - f), (double) (k + 1));
    }

    @Override
    public void func_149670_a(World world, int i, int j, int k, Entity entity) {
        entity.field_70159_w *= 0.4D;
        entity.field_70179_y *= 0.4D;
    }
}
