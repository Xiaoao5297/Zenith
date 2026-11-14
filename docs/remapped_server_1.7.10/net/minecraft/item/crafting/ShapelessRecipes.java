package net.minecraft.item.crafting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ShapelessRecipes implements IRecipe {

    private final ItemStack field_77580_a;
    private final List field_77579_b;

    public ShapelessRecipes(ItemStack itemstack, List list) {
        this.field_77580_a = itemstack;
        this.field_77579_b = list;
    }

    @Override
    public ItemStack func_77571_b() {
        return this.field_77580_a;
    }

    @Override
    public boolean func_77569_a(InventoryCrafting inventorycrafting, World world) {
        ArrayList arraylist = new ArrayList(this.field_77579_b);

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                ItemStack itemstack = inventorycrafting.func_70463_b(j, i);

                if (itemstack != null) {
                    boolean flag = false;
                    Iterator iterator = arraylist.iterator();

                    while (iterator.hasNext()) {
                        ItemStack itemstack1 = (ItemStack) iterator.next();

                        if (itemstack.func_77973_b() == itemstack1.func_77973_b() && (itemstack1.func_77960_j() == 32767 || itemstack.func_77960_j() == itemstack1.func_77960_j())) {
                            flag = true;
                            arraylist.remove(itemstack1);
                            break;
                        }
                    }

                    if (!flag) {
                        return false;
                    }
                }
            }
        }

        return arraylist.isEmpty();
    }

    @Override
    public ItemStack func_77572_b(InventoryCrafting inventorycrafting) {
        return this.field_77580_a.func_77946_l();
    }

    @Override
    public int func_77570_a() {
        return this.field_77579_b.size();
    }
}
