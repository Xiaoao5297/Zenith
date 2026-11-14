package net.minecraft.block;

import net.minecraft.block.material.Material;

public class BlockBreakable extends Block {

    private boolean field_149996_a;
    private String field_149995_b;

    protected BlockBreakable(String s, Material material, boolean flag) {
        super(material);
        this.field_149996_a = flag;
        this.field_149995_b = s;
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }
}
