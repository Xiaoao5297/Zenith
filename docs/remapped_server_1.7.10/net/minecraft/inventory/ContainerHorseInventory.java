package net.minecraft.inventory;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerHorseInventory extends Container {

    private IInventory field_111243_a;
    private EntityHorse field_111242_f;

    public ContainerHorseInventory(IInventory iinventory, IInventory iinventory1, EntityHorse entityhorse) {
        this.field_111243_a = iinventory1;
        this.field_111242_f = entityhorse;
        byte b0 = 3;

        iinventory1.func_70295_k_();
        int i = (b0 - 4) * 18;

        this.func_75146_a(new ContainerHorseInventory$1(this, iinventory1, 0, 8, 18));
        this.func_75146_a(new ContainerHorseInventory$2(this, iinventory1, 1, 8, 36, entityhorse));
        int j;
        int k;

        if (entityhorse.func_110261_ca()) {
            for (j = 0; j < b0; ++j) {
                for (k = 0; k < 5; ++k) {
                    this.func_75146_a(new Slot(iinventory1, 2 + k + j * 5, 80 + k * 18, 18 + j * 18));
                }
            }
        }

        for (j = 0; j < 3; ++j) {
            for (k = 0; k < 9; ++k) {
                this.func_75146_a(new Slot(iinventory, k + j * 9 + 9, 8 + k * 18, 102 + j * 18 + i));
            }
        }

        for (j = 0; j < 9; ++j) {
            this.func_75146_a(new Slot(iinventory, j, 8 + j * 18, 160 + i));
        }

    }

    @Override
    public boolean func_75145_c(EntityPlayer entityplayer) {
        return this.field_111243_a.func_70300_a(entityplayer) && this.field_111242_f.func_70089_S() && this.field_111242_f.func_70032_d(entityplayer) < 8.0F;
    }

    @Override
    public ItemStack func_82846_b(EntityPlayer entityplayer, int i) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.field_75151_b.get(i);

        if (slot != null && slot.func_75216_d()) {
            ItemStack itemstack1 = slot.func_75211_c();

            itemstack = itemstack1.func_77946_l();
            if (i < this.field_111243_a.func_70302_i_()) {
                if (!this.func_75135_a(itemstack1, this.field_111243_a.func_70302_i_(), this.field_75151_b.size(), true)) {
                    return null;
                }
            } else if (this.func_75139_a(1).func_75214_a(itemstack1) && !this.func_75139_a(1).func_75216_d()) {
                if (!this.func_75135_a(itemstack1, 1, 2, false)) {
                    return null;
                }
            } else if (this.func_75139_a(0).func_75214_a(itemstack1)) {
                if (!this.func_75135_a(itemstack1, 0, 1, false)) {
                    return null;
                }
            } else if (this.field_111243_a.func_70302_i_() <= 2 || !this.func_75135_a(itemstack1, 2, this.field_111243_a.func_70302_i_(), false)) {
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
        this.field_111243_a.func_70305_f();
    }
}
