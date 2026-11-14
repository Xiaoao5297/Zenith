package net.minecraft.item;

import net.minecraft.block.BlockLeaves;

public class ItemLeaves extends ItemBlock {

    private final BlockLeaves field_150940_b;

    public ItemLeaves(BlockLeaves blockleaves) {
        super(blockleaves);
        this.field_150940_b = blockleaves;
        this.func_77656_e(0);
        this.func_77627_a(true);
    }

    @Override
    public int func_77647_b(int i) {
        return i | 4;
    }

    @Override
    public String func_77667_c(ItemStack itemstack) {
        int i = itemstack.func_77960_j();

        if (i < 0 || i >= this.field_150940_b.func_150125_e().length) {
            i = 0;
        }

        return super.func_77658_a() + "." + this.field_150940_b.func_150125_e()[i];
    }
}
