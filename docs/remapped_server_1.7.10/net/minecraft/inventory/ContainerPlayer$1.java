package net.minecraft.inventory;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

class ContainerPlayer$1 extends Slot {

    ContainerPlayer$1(ContainerPlayer containerplayer, IInventory iinventory, int i, int j, int k, int l) {
        super(iinventory, i, j, k);
        this.field_75235_b = containerplayer;
        this.field_75236_a = l;
    }

    @Override
    public int func_75219_a() {
        return 1;
    }

    @Override
    public boolean func_75214_a(ItemStack itemstack) {
        return itemstack == null ? false : (itemstack.func_77973_b() instanceof ItemArmor ? ((ItemArmor) itemstack.func_77973_b()).field_77881_a == this.field_75236_a : (itemstack.func_77973_b() != Item.func_150898_a(Blocks.field_150423_aK) && itemstack.func_77973_b() != Items.field_151144_bL ? false : this.field_75236_a == 0));
    }
}
