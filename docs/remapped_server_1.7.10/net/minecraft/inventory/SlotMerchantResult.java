package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

public class SlotMerchantResult extends Slot {

    private final InventoryMerchant field_75233_a;
    private EntityPlayer field_75232_b;
    private int field_75231_g;
    private final IMerchant field_75234_h;

    public SlotMerchantResult(EntityPlayer entityplayer, IMerchant imerchant, InventoryMerchant inventorymerchant, int i, int j, int k) {
        super(inventorymerchant, i, j, k);
        this.field_75232_b = entityplayer;
        this.field_75234_h = imerchant;
        this.field_75233_a = inventorymerchant;
    }

    @Override
    public boolean func_75214_a(ItemStack itemstack) {
        return false;
    }

    @Override
    public ItemStack func_75209_a(int i) {
        if (this.func_75216_d()) {
            this.field_75231_g += Math.min(i, this.func_75211_c().field_77994_a);
        }

        return super.func_75209_a(i);
    }

    @Override
    protected void func_75210_a(ItemStack itemstack, int i) {
        this.field_75231_g += i;
        this.func_75208_c(itemstack);
    }

    @Override
    protected void func_75208_c(ItemStack itemstack) {
        itemstack.func_77980_a(this.field_75232_b.field_70170_p, this.field_75232_b, this.field_75231_g);
        this.field_75231_g = 0;
    }

    @Override
    public void func_82870_a(EntityPlayer entityplayer, ItemStack itemstack) {
        this.func_75208_c(itemstack);
        MerchantRecipe merchantrecipe = this.field_75233_a.func_70468_h();

        if (merchantrecipe != null) {
            ItemStack itemstack1 = this.field_75233_a.func_70301_a(0);
            ItemStack itemstack2 = this.field_75233_a.func_70301_a(1);

            if (this.func_75230_a(merchantrecipe, itemstack1, itemstack2) || this.func_75230_a(merchantrecipe, itemstack2, itemstack1)) {
                this.field_75234_h.func_70933_a(merchantrecipe);
                if (itemstack1 != null && itemstack1.field_77994_a <= 0) {
                    itemstack1 = null;
                }

                if (itemstack2 != null && itemstack2.field_77994_a <= 0) {
                    itemstack2 = null;
                }

                this.field_75233_a.func_70299_a(0, itemstack1);
                this.field_75233_a.func_70299_a(1, itemstack2);
            }
        }

    }

    private boolean func_75230_a(MerchantRecipe merchantrecipe, ItemStack itemstack, ItemStack itemstack1) {
        ItemStack itemstack2 = merchantrecipe.func_77394_a();
        ItemStack itemstack3 = merchantrecipe.func_77396_b();

        if (itemstack != null && itemstack.func_77973_b() == itemstack2.func_77973_b()) {
            if (itemstack3 != null && itemstack1 != null && itemstack3.func_77973_b() == itemstack1.func_77973_b()) {
                itemstack.field_77994_a -= itemstack2.field_77994_a;
                itemstack1.field_77994_a -= itemstack3.field_77994_a;
                return true;
            }

            if (itemstack3 == null && itemstack1 == null) {
                itemstack.field_77994_a -= itemstack2.field_77994_a;
                return true;
            }
        }

        return false;
    }
}
