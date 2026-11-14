package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;

public class BlockStainedGlass extends BlockBreakable {

    private static final IIcon[] field_149998_a = new IIcon[16];

    public BlockStainedGlass(Material material) {
        super("glass", material, false);
        this.func_149647_a(CreativeTabs.field_78030_b);
    }

    @Override
    public int func_149692_a(int i) {
        return i;
    }

    @Override
    public int func_149745_a(Random random) {
        return 0;
    }

    @Override
    protected boolean func_149700_E() {
        return true;
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }
}
