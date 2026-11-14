package net.minecraft.block;

import net.minecraft.block.material.Material;

public abstract class BlockDirectional extends Block {

    protected BlockDirectional(Material material) {
        super(material);
    }

    public static int func_149895_l(int i) {
        return i & 3;
    }
}
