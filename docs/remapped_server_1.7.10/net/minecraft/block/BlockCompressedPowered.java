package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.IBlockAccess;

public class BlockCompressedPowered extends BlockCompressed {

    public BlockCompressedPowered(MapColor mapcolor) {
        super(mapcolor);
        this.func_149647_a(CreativeTabs.field_78028_d);
    }

    @Override
    public boolean func_149744_f() {
        return true;
    }

    @Override
    public int func_149709_b(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return 15;
    }
}
