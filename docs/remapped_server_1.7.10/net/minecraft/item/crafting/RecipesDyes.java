package net.minecraft.item.crafting;

import net.minecraft.block.BlockColored;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipesDyes {

    public RecipesDyes() {}

    public void func_77607_a(CraftingManager craftingmanager) {
        int i;

        for (i = 0; i < 16; ++i) {
            craftingmanager.func_77596_b(new ItemStack(Blocks.field_150325_L, 1, BlockColored.func_150031_c(i)), new ItemStack(Items.field_151100_aR, 1, i), new ItemStack(Item.func_150898_a(Blocks.field_150325_L), 1, 0));
            craftingmanager.func_92103_a(new ItemStack(Blocks.field_150406_ce, 8, BlockColored.func_150031_c(i)), "###", "#X#", "###", '#', new ItemStack(Blocks.field_150405_ch), 'X', new ItemStack(Items.field_151100_aR, 1, i));
            craftingmanager.func_92103_a(new ItemStack(Blocks.field_150399_cn, 8, BlockColored.func_150031_c(i)), "###", "#X#", "###", '#', new ItemStack(Blocks.field_150359_w), 'X', new ItemStack(Items.field_151100_aR, 1, i));
            craftingmanager.func_92103_a(new ItemStack(Blocks.field_150397_co, 16, i), "###", "###", '#', new ItemStack(Blocks.field_150399_cn, 1, i));
        }

        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 1, 11), new ItemStack(Blocks.field_150327_N, 1, 0));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 1, 1), new ItemStack(Blocks.field_150328_O, 1, 0));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 3, 15), Items.field_151103_aS);
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 9), new ItemStack(Items.field_151100_aR, 1, 1), new ItemStack(Items.field_151100_aR, 1, 15));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 14), new ItemStack(Items.field_151100_aR, 1, 1), new ItemStack(Items.field_151100_aR, 1, 11));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 10), new ItemStack(Items.field_151100_aR, 1, 2), new ItemStack(Items.field_151100_aR, 1, 15));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 8), new ItemStack(Items.field_151100_aR, 1, 0), new ItemStack(Items.field_151100_aR, 1, 15));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 7), new ItemStack(Items.field_151100_aR, 1, 8), new ItemStack(Items.field_151100_aR, 1, 15));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 3, 7), new ItemStack(Items.field_151100_aR, 1, 0), new ItemStack(Items.field_151100_aR, 1, 15), new ItemStack(Items.field_151100_aR, 1, 15));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 12), new ItemStack(Items.field_151100_aR, 1, 4), new ItemStack(Items.field_151100_aR, 1, 15));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 6), new ItemStack(Items.field_151100_aR, 1, 4), new ItemStack(Items.field_151100_aR, 1, 2));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 5), new ItemStack(Items.field_151100_aR, 1, 4), new ItemStack(Items.field_151100_aR, 1, 1));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 13), new ItemStack(Items.field_151100_aR, 1, 5), new ItemStack(Items.field_151100_aR, 1, 9));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 3, 13), new ItemStack(Items.field_151100_aR, 1, 4), new ItemStack(Items.field_151100_aR, 1, 1), new ItemStack(Items.field_151100_aR, 1, 9));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 4, 13), new ItemStack(Items.field_151100_aR, 1, 4), new ItemStack(Items.field_151100_aR, 1, 1), new ItemStack(Items.field_151100_aR, 1, 1), new ItemStack(Items.field_151100_aR, 1, 15));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 1, 12), new ItemStack(Blocks.field_150328_O, 1, 1));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 1, 13), new ItemStack(Blocks.field_150328_O, 1, 2));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 1, 7), new ItemStack(Blocks.field_150328_O, 1, 3));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 1, 1), new ItemStack(Blocks.field_150328_O, 1, 4));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 1, 14), new ItemStack(Blocks.field_150328_O, 1, 5));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 1, 7), new ItemStack(Blocks.field_150328_O, 1, 6));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 1, 9), new ItemStack(Blocks.field_150328_O, 1, 7));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 1, 7), new ItemStack(Blocks.field_150328_O, 1, 8));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 11), new ItemStack(Blocks.field_150398_cm, 1, 0));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 13), new ItemStack(Blocks.field_150398_cm, 1, 1));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 1), new ItemStack(Blocks.field_150398_cm, 1, 4));
        craftingmanager.func_77596_b(new ItemStack(Items.field_151100_aR, 2, 9), new ItemStack(Blocks.field_150398_cm, 1, 5));

        for (i = 0; i < 16; ++i) {
            craftingmanager.func_92103_a(new ItemStack(Blocks.field_150404_cg, 3, i), "##", '#', new ItemStack(Blocks.field_150325_L, 1, i));
        }

    }
}
