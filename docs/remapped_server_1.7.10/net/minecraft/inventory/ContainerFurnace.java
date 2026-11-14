package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;

public class ContainerFurnace extends Container {

    private TileEntityFurnace field_75158_e;
    private int field_75156_f;
    private int field_75157_g;
    private int field_75159_h;

    public ContainerFurnace(InventoryPlayer inventoryplayer, TileEntityFurnace tileentityfurnace) {
        this.field_75158_e = tileentityfurnace;
        this.func_75146_a(new Slot(tileentityfurnace, 0, 56, 17));
        this.func_75146_a(new Slot(tileentityfurnace, 1, 56, 53));
        this.func_75146_a(new SlotFurnace(inventoryplayer.field_70458_d, tileentityfurnace, 2, 116, 35));

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
        icrafting.func_71112_a(this, 0, this.field_75158_e.field_145961_j);
        icrafting.func_71112_a(this, 1, this.field_75158_e.field_145956_a);
        icrafting.func_71112_a(this, 2, this.field_75158_e.field_145963_i);
    }

    @Override
    public void func_75142_b() {
        super.func_75142_b();

        for (int i = 0; i < this.field_75149_d.size(); ++i) {
            ICrafting icrafting = (ICrafting) this.field_75149_d.get(i);

            if (this.field_75156_f != this.field_75158_e.field_145961_j) {
                icrafting.func_71112_a(this, 0, this.field_75158_e.field_145961_j);
            }

            if (this.field_75157_g != this.field_75158_e.field_145956_a) {
                icrafting.func_71112_a(this, 1, this.field_75158_e.field_145956_a);
            }

            if (this.field_75159_h != this.field_75158_e.field_145963_i) {
                icrafting.func_71112_a(this, 2, this.field_75158_e.field_145963_i);
            }
        }

        this.field_75156_f = this.field_75158_e.field_145961_j;
        this.field_75157_g = this.field_75158_e.field_145956_a;
        this.field_75159_h = this.field_75158_e.field_145963_i;
    }

    @Override
    public boolean func_75145_c(EntityPlayer entityplayer) {
        return this.field_75158_e.func_70300_a(entityplayer);
    }

    @Override
    public ItemStack func_82846_b(EntityPlayer entityplayer, int i) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.field_75151_b.get(i);

        if (slot != null && slot.func_75216_d()) {
            ItemStack itemstack1 = slot.func_75211_c();

            itemstack = itemstack1.func_77946_l();
            if (i == 2) {
                if (!this.func_75135_a(itemstack1, 3, 39, true)) {
                    return null;
                }

                slot.func_75220_a(itemstack1, itemstack);
            } else if (i != 1 && i != 0) {
                if (FurnaceRecipes.func_77602_a().func_151395_a(itemstack1) != null) {
                    if (!this.func_75135_a(itemstack1, 0, 1, false)) {
                        return null;
                    }
                } else if (TileEntityFurnace.func_145954_b(itemstack1)) {
                    if (!this.func_75135_a(itemstack1, 1, 2, false)) {
                        return null;
                    }
                } else if (i >= 3 && i < 30) {
                    if (!this.func_75135_a(itemstack1, 30, 39, false)) {
                        return null;
                    }
                } else if (i >= 30 && i < 39 && !this.func_75135_a(itemstack1, 3, 30, false)) {
                    return null;
                }
            } else if (!this.func_75135_a(itemstack1, 3, 39, false)) {
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
