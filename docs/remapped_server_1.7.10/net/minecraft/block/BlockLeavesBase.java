package net.minecraft.block;

import net.minecraft.block.material.Material;

public class BlockLeavesBase extends Block {

    protected boolean field_150121_P;

    protected BlockLeavesBase(Material material, boolean flag) {
        super(material);
        this.field_150121_P = flag;
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }
}
