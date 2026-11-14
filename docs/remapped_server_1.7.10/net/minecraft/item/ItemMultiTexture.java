package net.minecraft.item;

import net.minecraft.block.Block;

public class ItemMultiTexture extends ItemBlock {

    protected final Block field_150941_b;
    protected final String[] field_150942_c;

    public ItemMultiTexture(Block block, Block block1, String[] astring) {
        super(block);
        this.field_150941_b = block1;
        this.field_150942_c = astring;
        this.func_77656_e(0);
        this.func_77627_a(true);
    }

    @Override
    public int func_77647_b(int i) {
        return i;
    }

    @Override
    public String func_77667_c(ItemStack itemstack) {
        int i = itemstack.func_77960_j();

        if (i < 0 || i >= this.field_150942_c.length) {
            i = 0;
        }

        return super.func_77658_a() + "." + this.field_150942_c[i];
    }
}
