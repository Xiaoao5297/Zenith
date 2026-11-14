package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RecipesMapCloning implements IRecipe {

    public RecipesMapCloning() {}

    @Override
    public boolean func_77569_a(InventoryCrafting inventorycrafting, World world) {
        int i = 0;
        ItemStack itemstack = null;

        for (int j = 0; j < inventorycrafting.func_70302_i_(); ++j) {
            ItemStack itemstack1 = inventorycrafting.func_70301_a(j);

            if (itemstack1 != null) {
                if (itemstack1.func_77973_b() == Items.field_151098_aY) {
                    if (itemstack != null) {
                        return false;
                    }

                    itemstack = itemstack1;
                } else {
                    if (itemstack1.func_77973_b() != Items.field_151148_bJ) {
                        return false;
                    }

                    ++i;
                }
            }
        }

        return itemstack != null && i > 0;
    }

    @Override
    public ItemStack func_77572_b(InventoryCrafting inventorycrafting) {
        int i = 0;
        ItemStack itemstack = null;

        for (int j = 0; j < inventorycrafting.func_70302_i_(); ++j) {
            ItemStack itemstack1 = inventorycrafting.func_70301_a(j);

            if (itemstack1 != null) {
                if (itemstack1.func_77973_b() == Items.field_151098_aY) {
                    if (itemstack != null) {
                        return null;
                    }

                    itemstack = itemstack1;
                } else {
                    if (itemstack1.func_77973_b() != Items.field_151148_bJ) {
                        return null;
                    }

                    ++i;
                }
            }
        }

        if (itemstack != null && i >= 1) {
            ItemStack itemstack2 = new ItemStack(Items.field_151098_aY, i + 1, itemstack.func_77960_j());

            if (itemstack.func_82837_s()) {
                itemstack2.func_151001_c(itemstack.func_82833_r());
            }

            return itemstack2;
        } else {
            return null;
        }
    }

    @Override
    public int func_77570_a() {
        return 9;
    }

    @Override
    public ItemStack func_77571_b() {
        return null;
    }
}
