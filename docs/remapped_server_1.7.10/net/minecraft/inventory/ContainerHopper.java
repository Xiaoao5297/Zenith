package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerHopper extends Container {

    private final IInventory field_94538_a;

    public ContainerHopper(InventoryPlayer inventoryplayer, IInventory iinventory) {
        this.field_94538_a = iinventory;
        iinventory.func_70295_k_();
        byte b0 = 51;

        int i;

        for (i = 0; i < iinventory.func_70302_i_(); ++i) {
            this.func_75146_a(new Slot(iinventory, i, 44 + i * 18, 20));
        }

        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.func_75146_a(new Slot(inventoryplayer, j + i * 9 + 9, 8 + j * 18, i * 18 + b0));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.func_75146_a(new Slot(inventoryplayer, i, 8 + i * 18, 58 + b0));
        }

    }

    @Override
    public boolean func_75145_c(EntityPlayer entityplayer) {
        return this.field_94538_a.func_70300_a(entityplayer);
    }

    @Override
    public ItemStack func_82846_b(EntityPlayer entityplayer, int i) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.field_75151_b.get(i);

        if (slot != null && slot.func_75216_d()) {
            ItemStack itemstack1 = slot.func_75211_c();

            itemstack = itemstack1.func_77946_l();
            if (i < this.field_94538_a.func_70302_i_()) {
                if (!this.func_75135_a(itemstack1, this.field_94538_a.func_70302_i_(), this.field_75151_b.size(), true)) {
                    return null;
                }
            } else if (!this.func_75135_a(itemstack1, 0, this.field_94538_a.func_70302_i_(), false)) {
                return null;
            }

            if (itemstack1.field_77994_a == 0) {
                slot.func_75215_d((ItemStack) null);
            } else {
                slot.func_75218_e();
            }
        }

        return itemstack;
    }

    @Override
    public void func_75134_a(EntityPlayer entityplayer) {
        super.func_75134_a(entityplayer);
        this.field_94538_a.func_70305_f();
    }
}
