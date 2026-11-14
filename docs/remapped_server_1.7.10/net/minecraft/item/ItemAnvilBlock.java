package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;

public class ItemAnvilBlock extends ItemMultiTexture {

    public ItemAnvilBlock(Block block) {
        super(block, block, BlockAnvil.field_149834_a);
    }

    @Override
    public int func_77647_b(int i) {
        return i << 2;
    }
}
