package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerChest extends Container {

    private IInventory field_75155_e;
    private int field_75154_f;

    public ContainerChest(IInventory iinventory, IInventory iinventory1) {
        this.field_75155_e = iinventory1;
        this.field_75154_f = iinventory1.func_70302_i_() / 9;
        iinventory1.func_70295_k_();
        int i = (this.field_75154_f - 4) * 18;

        int j;
        int k;

        for (j = 0; j < this.field_75154_f; ++j) {
            for (k = 0; k < 9; ++k) {
                this.func_75146_a(new Slot(iinventory1, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        for (j = 0; j < 3; ++j) {
            for (k = 0; k < 9; ++k) {
                this.func_75146_a(new Slot(iinventory, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
            }
        }

        for (j = 0; j < 9; ++j) {
            this.func_75146_a(new Slot(iinventory, j, 8 + j * 18, 161 + i));
        }

    }

    @Override
    public boolean func_75145_c(EntityPlayer entityplayer) {
        return this.field_75155_e.func_70300_a(entityplayer);
    }

    @Override
    public ItemStack func_82846_b(EntityPlayer entityplayer, int i) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.field_75151_b.get(i);

        if (slot != null && slot.func_75216_d()) {
            ItemStack itemstack1 = slot.func_75211_c();

            itemstack = itemstack1.func_77946_l();
            if (i < this.field_75154_f * 9) {
                if (!this.func_75135_a(itemstack1, this.field_75154_f * 9, this.field_75151_b.size(), true)) {
                    return null;
                }
            } else if (!this.func_75135_a(itemstack1, 0, this.field_75154_f * 9, false)) {
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
        this.field_75155_e.func_70305_f();
    }

    public IInventory func_85151_d() {
        return this.field_75155_e;
    }
}
