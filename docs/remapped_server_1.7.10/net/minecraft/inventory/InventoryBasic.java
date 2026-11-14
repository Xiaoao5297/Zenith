package net.minecraft.inventory;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryBasic implements IInventory {

    private String field_70483_a;
    private int field_70481_b;
    private ItemStack[] field_70482_c;
    private List field_70480_d;
    private boolean field_94051_e;

    public InventoryBasic(String s, boolean flag, int i) {
        this.field_70483_a = s;
        this.field_94051_e = flag;
        this.field_70481_b = i;
        this.field_70482_c = new ItemStack[i];
    }

    public void func_110134_a(IInvBasic iinvbasic) {
        if (this.field_70480_d == null) {
            this.field_70480_d = new ArrayList();
        }

        this.field_70480_d.add(iinvbasic);
    }

    public void func_110132_b(IInvBasic iinvbasic) {
        this.field_70480_d.remove(iinvbasic);
    }

    @Override
    public ItemStack func_70301_a(int i) {
        return i >= 0 && i < this.field_70482_c.length ? this.field_70482_c[i] : null;
    }

    @Override
    public ItemStack func_70298_a(int i, int j) {
        if (this.field_70482_c[i] != null) {
            ItemStack itemstack;

            if (this.field_70482_c[i].field_77994_a <= j) {
                itemstack = this.field_70482_c[i];
                this.field_70482_c[i] = null;
                this.func_70296_d();
                return itemstack;
            } else {
                itemstack = this.field_70482_c[i].func_77979_a(j);
                if (this.field_70482_c[i].field_77994_a == 0) {
                    this.field_70482_c[i] = null;
                }

                this.func_70296_d();
                return itemstack;
            }
        } else {
            return null;
        }
    }

    @Override
    public ItemStack func_70304_b(int i) {
        if (this.field_70482_c[i] != null) {
            ItemStack itemstack = this.field_70482_c[i];

            this.field_70482_c[i] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public void func_70299_a(int i, ItemStack itemstack) {
        this.field_70482_c[i] = itemstack;
        if (itemstack != null && itemstack.field_77994_a > this.func_70297_j_()) {
            itemstack.field_77994_a = this.func_70297_j_();
        }

        this.func_70296_d();
    }

    @Override
    public int func_70302_i_() {
        return this.field_70481_b;
    }

    @Override
    public String func_145825_b() {
        return this.field_70483_a;
    }

    @Override
    public boolean func_145818_k_() {
        return this.field_94051_e;
    }

    public void func_110133_a(String s) {
        this.field_94051_e = true;
        this.field_70483_a = s;
    }

    @Override
    public int func_70297_j_() {
        return 64;
    }

    @Override
    public void func_70296_d() {
        if (this.field_70480_d != null) {
            for (int i = 0; i < this.field_70480_d.size(); ++i) {
                ((IInvBasic) this.field_70480_d.get(i)).func_76316_a(this);
            }
        }

    }

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
