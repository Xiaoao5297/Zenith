package net.minecraft.item.crafting;

import java.util.Comparator;

class CraftingManager$1 implements Comparator {

    CraftingManager$1(CraftingManager craftingmanager) {
        this.field_77582_a = craftingmanager;
    }

    public int compare(IRecipe irecipe, IRecipe irecipe1) {
        return irecipe instanceof ShapelessRecipes && irecipe1 instanceof ShapedRecipes ? 1 : (irecipe1 instanceof ShapelessRecipes && irecipe instanceof ShapedRecipes ? -1 : (irecipe1.func_77570_a() < irecipe.func_77570_a() ? -1 : (irecipe1.func_77570_a() > irecipe.func_77570_a() ? 1 : 0)));
    }
}
