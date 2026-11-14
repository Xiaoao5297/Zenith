package net.minecraft.item;

import net.minecraft.block.Block;

public class ItemBlockWithMetadata extends ItemBlock {

    private Block field_150950_b;

    public ItemBlockWithMetadata(Block block, Block block1) {
        super(block);
        this.field_150950_b = block1;
        this.func_77656_e(0);
        this.func_77627_a(true);
    }

    @Override
    public int func_77647_b(int i) {
        return i;
    }
}
