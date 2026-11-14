package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryLargeChest implements IInventory {

    private String field_70479_a;
    private IInventory field_70477_b;
    private IInventory field_70478_c;

    public InventoryLargeChest(String s, IInventory iinventory, IInventory iinventory1) {
        this.field_70479_a = s;
        if (iinventory == null) {
            iinventory = iinventory1;
        }

        if (iinventory1 == null) {
            iinventory1 = iinventory;
        }

        this.field_70477_b = iinventory;
        this.field_70478_c = iinventory1;
    }

    @Override
    public int func_70302_i_() {
        return this.field_70477_b.func_70302_i_() + this.field_70478_c.func_70302_i_();
    }

    public boolean func_90010_a(IInventory iinventory) {
        return this.field_70477_b == iinventory || this.field_70478_c == iinventory;
    }

    @Override
    public String func_145825_b() {
        return this.field_70477_b.func_145818_k_() ? this.field_70477_b.func_145825_b() : (this.field_70478_c.func_145818_k_() ? this.field_70478_c.func_145825_b() : this.field_70479_a);
    }

    @Override
    public boolean func_145818_k_() {
        return this.field_70477_b.func_145818_k_() || this.field_70478_c.func_145818_k_();
    }

    @Override
    public ItemStack func_70301_a(int i) {
        return i >= this.field_70477_b.func_70302_i_() ? this.field_70478_c.func_70301_a(i - this.field_70477_b.func_70302_i_()) : this.field_70477_b.func_70301_a(i);
    }

    @Override
    public ItemStack func_70298_a(int i, int j) {
        return i >= this.field_70477_b.func_70302_i_() ? this.field_70478_c.func_70298_a(i - this.field_70477_b.func_70302_i_(), j) : this.field_70477_b.func_70298_a(i, j);
    }

    @Override
    public ItemStack func_70304_b(int i) {
        return i >= this.field_70477_b.func_70302_i_() ? this.field_70478_c.func_70304_b(i - this.field_70477_b.func_70302_i_()) : this.field_70477_b.func_70304_b(i);
    }

    @Override
    public void func_70299_a(int i, ItemStack itemstack) {
        if (i >= this.field_70477_b.func_70302_i_()) {
            this.field_70478_c.func_70299_a(i - this.field_70477_b.func_70302_i_(), itemstack);
        } else {
            this.field_70477_b.func_70299_a(i, itemstack);
        }

    }

    @Override
    public int func_70297_j_() {
        return this.field_70477_b.func_70297_j_();
    }

    @Override
    public void func_70296_d() {
        this.field_70477_b.func_70296_d();
        this.field_70478_c.func_70296_d();
    }

    @Override
    public boolean func_70300_a(EntityPlayer entityplayer) {
        return this.field_70477_b.func_70300_a(entityplayer) && this.field_70478_c.func_70300_a(entityplayer);
    }

    @Override
    public void func_70295_k_() {
        this.field_70477_b.func_70295_k_();
        this.field_70478_c.func_70295_k_();
    }

    @Override
    public void func_70305_f() {
        this.field_70477_b.func_70305_f();
        this.field_70478_c.func_70305_f();
    }

    @Override
    public boolean func_94041_b(int i, ItemStack itemstack) {
        return true;
    }
}
