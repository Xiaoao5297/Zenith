package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockGlass extends BlockBreakable {

    public BlockGlass(Material material, boolean flag) {
        super("glass", material, flag);
        this.func_149647_a(CreativeTabs.field_78030_b);
    }

    @Override
    public int func_149745_a(Random random) {
        return 0;
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    @Override
    protected boolean func_149700_E() {
        return true;
    }
}
