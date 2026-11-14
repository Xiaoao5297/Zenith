package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;

public class ContainerDispenser extends Container {

    private TileEntityDispenser field_75182_e;

    public ContainerDispenser(IInventory iinventory, TileEntityDispenser tileentitydispenser) {
        this.field_75182_e = tileentitydispenser;

        int i;
        int j;

        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 3; ++j) {
                this.func_75146_a(new Slot(tileentitydispenser, j + i * 3, 62 + j * 18, 17 + i * 18));
            }
        }

        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 9; ++j) {
                this.func_75146_a(new Slot(iinventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.func_75146_a(new Slot(iinventory, i, 8 + i * 18, 142));
        }

    }

    @Override
    public boolean func_75145_c(EntityPlayer entityplayer) {
        return this.field_75182_e.func_70300_a(entityplayer);
    }

    @Override
    public ItemStack func_82846_b(EntityPlayer entityplayer, int i) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.field_75151_b.get(i);

        if (slot != null && slot.func_75216_d()) {
            ItemStack itemstack1 = slot.func_75211_c();

            itemstack = itemstack1.func_77946_l();
            if (i < 9) {
                if (!this.func_75135_a(itemstack1, 9, 45, true)) {
                    return null;
                }
            } else if (!this.func_75135_a(itemstack1, 0, 9, false)) {
                return null;
            }

            if (itemstack1.field_77994_a == 0) {
                slot.func_75215_d((ItemStack) null);
            } else {
                slot.func_75218_e();
            }

            if (itemstack1.field_77994_a == itemstack.field_77994_a) {
                return null;
            }

            slot.func_82870_a(entityplayer, itemstack1);
        }

        return itemstack;
    }
}
