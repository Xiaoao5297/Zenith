package net.minecraft.block;

import net.minecraft.block.material.MapColor;

public class BlockSand extends BlockFalling {

    public static final String[] field_149838_a = new String[]{"default", "red"};

    public BlockSand() {}

    @Override
    public int func_149692_a(int i) {
        return i;
    }

    @Override
    public MapColor func_149728_f(int i) {
        return i == 1 ? MapColor.field_151664_l : MapColor.field_151658_d;
    }
}
