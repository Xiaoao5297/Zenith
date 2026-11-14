package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerMerchant extends Container {

    private IMerchant field_75178_e;
    private InventoryMerchant field_75176_f;
    private final World field_75177_g;

    public ContainerMerchant(InventoryPlayer inventoryplayer, IMerchant imerchant, World world) {
        this.field_75178_e = imerchant;
        this.field_75177_g = world;
        this.field_75176_f = new InventoryMerchant(inventoryplayer.field_70458_d, imerchant);
        this.func_75146_a(new Slot(this.field_75176_f, 0, 36, 53));
        this.func_75146_a(new Slot(this.field_75176_f, 1, 62, 53));
        this.func_75146_a(new SlotMerchantResult(inventoryplayer.field_70458_d, imerchant, this.field_75176_f, 2, 120, 53));

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

    public InventoryMerchant func_75174_d() {
        return this.field_75176_f;
    }

    @Override
    public void func_75132_a(ICrafting icrafting) {
        super.func_75132_a(icrafting);
    }

    @Override
    public void func_75142_b() {
        super.func_75142_b();
    }

    @Override
    public void func_75130_a(IInventory iinventory) {
        this.field_75176_f.func_70470_g();
        super.func_75130_a(iinventory);
    }

    public void func_75175_c(int i) {
        this.field_75176_f.func_70471_c(i);
    }

    @Override
    public boolean func_75145_c(EntityPlayer entityplayer) {
        return this.field_75178_e.func_70931_l_() == entityplayer;
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
            } else if (i != 0 && i != 1) {
                if (i >= 3 && i < 30) {
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

    @Override
    public void func_75134_a(EntityPlayer entityplayer) {
        super.func_75134_a(entityplayer);
        this.field_75178_e.func_70932_a_((EntityPlayer) null);
        super.func_75134_a(entityplayer);
        if (!this.field_75177_g.field_72995_K) {
            ItemStack itemstack = this.field_75176_f.func_70304_b(0);

            if (itemstack != null) {
                entityplayer.func_71019_a(itemstack, false);
            }

            itemstack = this.field_75176_f.func_70304_b(1);
            if (itemstack != null) {
                entityplayer.func_71019_a(itemstack, false);
            }

        }
    }
}
