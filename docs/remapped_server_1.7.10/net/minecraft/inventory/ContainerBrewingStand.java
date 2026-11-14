package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBrewingStand;

public class ContainerBrewingStand extends Container {

    private TileEntityBrewingStand field_75188_e;
    private final Slot field_75186_f;
    private int field_75187_g;

    public ContainerBrewingStand(InventoryPlayer inventoryplayer, TileEntityBrewingStand tileentitybrewingstand) {
        this.field_75188_e = tileentitybrewingstand;
        this.func_75146_a(new ContainerBrewingStand$Potion(inventoryplayer.field_70458_d, tileentitybrewingstand, 0, 56, 46));
        this.func_75146_a(new ContainerBrewingStand$Potion(inventoryplayer.field_70458_d, tileentitybrewingstand, 1, 79, 53));
        this.func_75146_a(new ContainerBrewingStand$Potion(inventoryplayer.field_70458_d, tileentitybrewingstand, 2, 102, 46));
        this.field_75186_f = this.func_75146_a(new ContainerBrewingStand$Ingredient(this, tileentitybrewingstand, 3, 79, 17));

        int i;

        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.func_75146_a(new Slot(inventoryplayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.func_75146_a(new Slot(inventoryplayer, i, 8 + i * 18, 142));
        }

    }

    @Override
    public void func_75132_a(ICrafting icrafting) {
        super.func_75132_a(icrafting);
        icrafting.func_71112_a(this, 0, this.field_75188_e.func_145935_i());
    }

    @Override
    public void func_75142_b() {
        super.func_75142_b();

        for (int i = 0; i < this.field_75149_d.size(); ++i) {
            ICrafting icrafting = (ICrafting) this.field_75149_d.get(i);

            if (this.field_75187_g != this.field_75188_e.func_145935_i()) {
                icrafting.func_71112_a(this, 0, this.field_75188_e.func_145935_i());
            }
        }

        this.field_75187_g = this.field_75188_e.func_145935_i();
    }

    @Override
    public boolean func_75145_c(EntityPlayer entityplayer) {
        return this.field_75188_e.func_70300_a(entityplayer);
    }

    @Override
    public ItemStack func_82846_b(EntityPlayer entityplayer, int i) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.field_75151_b.get(i);

        if (slot != null && slot.func_75216_d()) {
            ItemStack itemstack1 = slot.func_75211_c();

            itemstack = itemstack1.func_77946_l();
            if ((i < 0 || i > 2) && i != 3) {
                if (!this.field_75186_f.func_75216_d() && this.field_75186_f.func_75214_a(itemstack1)) {
                    if (!this.func_75135_a(itemstack1, 3, 4, false)) {
                        return null;
                    }
                } else if (ContainerBrewingStand$Potion.func_75243_a_(itemstack)) {
                    if (!this.func_75135_a(itemstack1, 0, 3, false)) {
                        return null;
                    }
                } else if (i >= 4 && i < 31) {
                    if (!this.func_75135_a(itemstack1, 31, 40, false)) {
                        return null;
                    }
                } else if (i >= 31 && i < 40) {
                    if (!this.func_75135_a(itemstack1, 4, 31, false)) {
                        return null;
                    }
                } else if (!this.func_75135_a(itemstack1, 4, 40, false)) {
                    return null;
                }
            } else {
                if (!this.func_75135_a(itemstack1, 4, 40, true)) {
                    return null;
                }

                slot.func_75220_a(itemstack1, itemstack);
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
