package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class ContainerPlayer extends Container {

    public InventoryCrafting field_75181_e = new InventoryCrafting(this, 2, 2);
    public IInventory field_75179_f = new InventoryCraftResult();
    public boolean field_75180_g;
    private final EntityPlayer field_82862_h;

    public ContainerPlayer(InventoryPlayer inventoryplayer, boolean flag, EntityPlayer entityplayer) {
        this.field_75180_g = flag;
        this.field_82862_h = entityplayer;
        this.func_75146_a(new SlotCrafting(inventoryplayer.field_70458_d, this.field_75181_e, this.field_75179_f, 0, 144, 36));

        int i;
        int j;

        for (i = 0; i < 2; ++i) {
            for (j = 0; j < 2; ++j) {
                this.func_75146_a(new Slot(this.field_75181_e, j + i * 2, 88 + j * 18, 26 + i * 18));
            }
        }

        for (i = 0; i < 4; ++i) {
            this.func_75146_a(new ContainerPlayer$1(this, inventoryplayer, inventoryplayer.func_70302_i_() - 1 - i, 8, 8 + i * 18, i));
        }

        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 9; ++j) {
                this.func_75146_a(new Slot(inventoryplayer, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.func_75146_a(new Slot(inventoryplayer, i, 8 + i * 18, 142));
        }

        this.func_75130_a(this.field_75181_e);
    }

    @Override
    public void func_75130_a(IInventory iinventory) {
        this.field_75179_f.func_70299_a(0, CraftingManager.func_77594_a().func_82787_a(this.field_75181_e, this.field_82862_h.field_70170_p));
    }

    @Override
    public void func_75134_a(EntityPlayer entityplayer) {
        super.func_75134_a(entityplayer);

        for (int i = 0; i < 4; ++i) {
            ItemStack itemstack = this.field_75181_e.func_70304_b(i);

            if (itemstack != null) {
                entityplayer.func_71019_a(itemstack, false);
            }
        }

        this.field_75179_f.func_70299_a(0, (ItemStack) null);
    }

    @Override
    public boolean func_75145_c(EntityPlayer entityplayer) {
        return true;
    }

    @Override
    public ItemStack func_82846_b(EntityPlayer entityplayer, int i) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.field_75151_b.get(i);

        if (slot != null && slot.func_75216_d()) {
            ItemStack itemstack1 = slot.func_75211_c();

            itemstack = itemstack1.func_77946_l();
            if (i == 0) {
                if (!this.func_75135_a(itemstack1, 9, 45, true)) {
                    return null;
                }

                slot.func_75220_a(itemstack1, itemstack);
            } else if (i >= 1 && i < 5) {
                if (!this.func_75135_a(itemstack1, 9, 45, false)) {
                    return null;
                }
            } else if (i >= 5 && i < 9) {
                if (!this.func_75135_a(itemstack1, 9, 45, false)) {
                    return null;
                }
            } else if (itemstack.func_77973_b() instanceof ItemArmor && !((Slot) this.field_75151_b.get(5 + ((ItemArmor) itemstack.func_77973_b()).field_77881_a)).func_75216_d()) {
                int j = 5 + ((ItemArmor) itemstack.func_77973_b()).field_77881_a;

                if (!this.func_75135_a(itemstack1, j, j + 1, false)) {
                    return null;
                }
            } else if (i >= 9 && i < 36) {
                if (!this.func_75135_a(itemstack1, 36, 45, false)) {
                    return null;
                }
            } else if (i >= 36 && i < 45) {
                if (!this.func_75135_a(itemstack1, 9, 36, false)) {
                    return null;
                }
            } else if (!this.func_75135_a(itemstack1, 9, 45, false)) {
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

    @Override
    public boolean func_94530_a(ItemStack itemstack, Slot slot) {
        return slot.field_75224_c != this.field_75179_f && super.func_94530_a(itemstack, slot);
    }
}
