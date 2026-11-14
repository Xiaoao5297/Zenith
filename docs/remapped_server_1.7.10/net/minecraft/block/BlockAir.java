package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockAir extends Block {

    protected BlockAir() {
        super(Material.field_151579_a);
    }

    @Override
    public int func_149645_b() {
        return -1;
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        return null;
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }

    @Override
    public boolean func_149678_a(int i, boolean flag) {
        return false;
    }

    @Override
    public void func_149690_a(World world, int i, int j, int k, int l, float f, int i1) {}
}
