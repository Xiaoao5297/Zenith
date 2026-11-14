package net.minecraft.item.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ShapedRecipes implements IRecipe {

    private int field_77576_b;
    private int field_77577_c;
    private ItemStack[] field_77574_d;
    private ItemStack field_77575_e;
    private boolean field_92101_f;

    public ShapedRecipes(int i, int j, ItemStack[] aitemstack, ItemStack itemstack) {
        this.field_77576_b = i;
        this.field_77577_c = j;
        this.field_77574_d = aitemstack;
        this.field_77575_e = itemstack;
    }

    @Override
    public ItemStack func_77571_b() {
        return this.field_77575_e;
    }

    @Override
    public boolean func_77569_a(InventoryCrafting inventorycrafting, World world) {
        for (int i = 0; i <= 3 - this.field_77576_b; ++i) {
            for (int j = 0; j <= 3 - this.field_77577_c; ++j) {
                if (this.func_77573_a(inventorycrafting, i, j, true)) {
                    return true;
                }

                if (this.func_77573_a(inventorycrafting, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean func_77573_a(InventoryCrafting inventorycrafting, int i, int j, boolean flag) {
        for (int k = 0; k < 3; ++k) {
            for (int l = 0; l < 3; ++l) {
                int i1 = k - i;
                int j1 = l - j;
                ItemStack itemstack = null;

                if (i1 >= 0 && j1 >= 0 && i1 < this.field_77576_b && j1 < this.field_77577_c) {
                    if (flag) {
                        itemstack = this.field_77574_d[this.field_77576_b - i1 - 1 + j1 * this.field_77576_b];
                    } else {
                        itemstack = this.field_77574_d[i1 + j1 * this.field_77576_b];
                    }
                }

                ItemStack itemstack1 = inventorycrafting.func_70463_b(k, l);

                if (itemstack1 != null || itemstack != null) {
                    if (itemstack1 == null && itemstack != null || itemstack1 != null && itemstack == null) {
                        return false;
                    }

                    if (itemstack.func_77973_b() != itemstack1.func_77973_b()) {
                        return false;
                    }

                    if (itemstack.func_77960_j() != 32767 && itemstack.func_77960_j() != itemstack1.func_77960_j()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack func_77572_b(InventoryCrafting inventorycrafting) {
        ItemStack itemstack = this.func_77571_b().func_77946_l();

        if (this.field_92101_f) {
            for (int i = 0; i < inventorycrafting.func_70302_i_(); ++i) {
                ItemStack itemstack1 = inventorycrafting.func_70301_a(i);

                if (itemstack1 != null && itemstack1.func_77942_o()) {
                    itemstack.func_77982_d((NBTTagCompound) itemstack1.field_77990_d.func_74737_b());
                }
            }
        }

        return itemstack;
    }

    @Override
    public int func_77570_a() {
        return this.field_77576_b * this.field_77577_c;
    }

    public ShapedRecipes func_92100_c() {
        this.field_92101_f = true;
        return this;
    }
}
