package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;

public class ItemCloth extends ItemBlock {

    public ItemCloth(Block block) {
        super(block);
        this.func_77656_e(0);
        this.func_77627_a(true);
    }

    @Override
    public int func_77647_b(int i) {
        return i;
    }

    @Override
    public String func_77667_c(ItemStack itemstack) {
        return super.func_77658_a() + "." + ItemDye.field_150923_a[BlockColored.func_150032_b(itemstack.func_77960_j())];
    }
}
