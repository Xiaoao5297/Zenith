package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryCrafting implements IInventory {

    private ItemStack[] field_70466_a;
    private int field_70464_b;
    private Container field_70465_c;

    public InventoryCrafting(Container container, int i, int j) {
        int k = i * j;

        this.field_70466_a = new ItemStack[k];
        this.field_70465_c = container;
        this.field_70464_b = i;
    }

    @Override
    public int func_70302_i_() {
        return this.field_70466_a.length;
    }

    @Override
    public ItemStack func_70301_a(int i) {
        return i >= this.func_70302_i_() ? null : this.field_70466_a[i];
    }

    public ItemStack func_70463_b(int i, int j) {
        if (i >= 0 && i < this.field_70464_b) {
            int k = i + j * this.field_70464_b;

            return this.func_70301_a(k);
        } else {
            return null;
        }
    }

    @Override
    public String func_145825_b() {
        return "container.crafting";
    }

    @Override
    public boolean func_145818_k_() {
        return false;
    }

    @Override
    public ItemStack func_70304_b(int i) {
        if (this.field_70466_a[i] != null) {
            ItemStack itemstack = this.field_70466_a[i];

            this.field_70466_a[i] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public ItemStack func_70298_a(int i, int j) {
        if (this.field_70466_a[i] != null) {
            ItemStack itemstack;

            if (this.field_70466_a[i].field_77994_a <= j) {
                itemstack = this.field_70466_a[i];
                this.field_70466_a[i] = null;
                this.field_70465_c.func_75130_a(this);
                return itemstack;
            } else {
                itemstack = this.field_70466_a[i].func_77979_a(j);
                if (this.field_70466_a[i].field_77994_a == 0) {
                    this.field_70466_a[i] = null;
                }

                this.field_70465_c.func_75130_a(this);
                return itemstack;
            }
        } else {
            return null;
        }
    }

    @Override
    public void func_70299_a(int i, ItemStack itemstack) {
        this.field_70466_a[i] = itemstack;
        this.field_70465_c.func_75130_a(this);
    }

    @Override
    public int func_70297_j_() {
        return 64;
    }

    @Override
    public void func_70296_d() {}

    @Override
    public boolean func_70300_a(EntityPlayer entityplayer) {
        return true;
    }

    @Override
    public void func_70295_k_() {}

    @Override
    public void func_70305_f() {}

    @Override
    public boolean func_94041_b(int i, ItemStack itemstack) {
        return true;
    }
}
