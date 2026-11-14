package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;

public class BlockStainedGlassPane extends BlockPane {

    private static final IIcon[] field_150106_a = new IIcon[16];
    private static final IIcon[] field_150105_b = new IIcon[16];

    public BlockStainedGlassPane() {
        super("glass", "glass_pane_top", Material.field_151592_s, false);
        this.func_149647_a(CreativeTabs.field_78031_c);
    }

    @Override
    public int func_149692_a(int i) {
        return i;
    }
}
