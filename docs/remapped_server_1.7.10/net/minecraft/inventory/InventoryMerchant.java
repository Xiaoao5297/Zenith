package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

public class InventoryMerchant implements IInventory {

    private final IMerchant field_70476_a;
    private ItemStack[] field_70474_b = new ItemStack[3];
    private final EntityPlayer field_70475_c;
    private MerchantRecipe field_70472_d;
    private int field_70473_e;

    public InventoryMerchant(EntityPlayer entityplayer, IMerchant imerchant) {
        this.field_70475_c = entityplayer;
        this.field_70476_a = imerchant;
    }

    @Override
    public int func_70302_i_() {
        return this.field_70474_b.length;
    }

    @Override
    public ItemStack func_70301_a(int i) {
        return this.field_70474_b[i];
    }

    @Override
    public ItemStack func_70298_a(int i, int j) {
        if (this.field_70474_b[i] != null) {
            ItemStack itemstack;

            if (i == 2) {
                itemstack = this.field_70474_b[i];
                this.field_70474_b[i] = null;
                return itemstack;
            } else if (this.field_70474_b[i].field_77994_a <= j) {
                itemstack = this.field_70474_b[i];
                this.field_70474_b[i] = null;
                if (this.func_70469_d(i)) {
                    this.func_70470_g();
                }

                return itemstack;
            } else {
                itemstack = this.field_70474_b[i].func_77979_a(j);
                if (this.field_70474_b[i].field_77994_a == 0) {
                    this.field_70474_b[i] = null;
                }

                if (this.func_70469_d(i)) {
                    this.func_70470_g();
                }

                return itemstack;
            }
        } else {
            return null;
        }
    }

    private boolean func_70469_d(int i) {
        return i == 0 || i == 1;
    }

    @Override
    public ItemStack func_70304_b(int i) {
        if (this.field_70474_b[i] != null) {
            ItemStack itemstack = this.field_70474_b[i];

            this.field_70474_b[i] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public void func_70299_a(int i, ItemStack itemstack) {
        this.field_70474_b[i] = itemstack;
        if (itemstack != null && itemstack.field_77994_a > this.func_70297_j_()) {
            itemstack.field_77994_a = this.func_70297_j_();
        }

        if (this.func_70469_d(i)) {
            this.func_70470_g();
        }

    }

    @Override
    public String func_145825_b() {
        return "mob.villager";
    }

    @Override
    public boolean func_145818_k_() {
        return false;
    }

    @Override
    public int func_70297_j_() {
        return 64;
    }

    @Override
    public boolean func_70300_a(EntityPlayer entityplayer) {
        return this.field_70476_a.func_70931_l_() == entityplayer;
    }

    @Override
    public void func_70295_k_() {}

    @Override
    public void func_70305_f() {}

    @Override
    public boolean func_94041_b(int i, ItemStack itemstack) {
        return true;
    }

    @Override
    public void func_70296_d() {
        this.func_70470_g();
    }

    public void func_70470_g() {
        this.field_70472_d = null;
        ItemStack itemstack = this.field_70474_b[0];
        ItemStack itemstack1 = this.field_70474_b[1];

        if (itemstack == null) {
            itemstack = itemstack1;
            itemstack1 = null;
        }

        if (itemstack == null) {
            this.func_70299_a(2, (ItemStack) null);
        } else {
            MerchantRecipeList merchantrecipelist = this.field_70476_a.func_70934_b(this.field_70475_c);

            if (merchantrecipelist != null) {
                MerchantRecipe merchantrecipe = merchantrecipelist.func_77203_a(itemstack, itemstack1, this.field_70473_e);

                if (merchantrecipe != null && !merchantrecipe.func_82784_g()) {
                    this.field_70472_d = merchantrecipe;
                    this.func_70299_a(2, merchantrecipe.func_77397_d().func_77946_l());
                } else if (itemstack1 != null) {
                    merchantrecipe = merchantrecipelist.func_77203_a(itemstack1, itemstack, this.field_70473_e);
                    if (merchantrecipe != null && !merchantrecipe.func_82784_g()) {
                        this.field_70472_d = merchantrecipe;
                        this.func_70299_a(2, merchantrecipe.func_77397_d().func_77946_l());
                    } else {
                        this.func_70299_a(2, (ItemStack) null);
                    }
                } else {
                    this.func_70299_a(2, (ItemStack) null);
                }
            }
        }

        this.field_70476_a.func_110297_a_(this.func_70301_a(2));
    }

    public MerchantRecipe func_70468_h() {
        return this.field_70472_d;
    }

    public void func_70471_c(int i) {
        this.field_70473_e = i;
        this.func_70470_g();
    }
}
