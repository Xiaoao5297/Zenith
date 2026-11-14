package net.minecraft.util;

import java.util.Random;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;

public class WeightedRandomChestContent extends WeightedRandom$Item {

    private ItemStack field_76297_b;
    private int field_76295_d;
    private int field_76296_e;

    public WeightedRandomChestContent(Item item, int i, int j, int k, int l) {
        super(l);
        this.field_76297_b = new ItemStack(item, 1, i);
        this.field_76295_d = j;
        this.field_76296_e = k;
    }

    public WeightedRandomChestContent(ItemStack itemstack, int i, int j, int k) {
        super(k);
        this.field_76297_b = itemstack;
        this.field_76295_d = i;
        this.field_76296_e = j;
    }

    public static void func_76293_a(Random random, WeightedRandomChestContent[] aweightedrandomchestcontent, IInventory iinventory, int i) {
        for (int j = 0; j < i; ++j) {
            WeightedRandomChestContent weightedrandomchestcontent = (WeightedRandomChestContent) WeightedRandom.func_76274_a(random, aweightedrandomchestcontent);
            int k = weightedrandomchestcontent.field_76295_d + random.nextInt(weightedrandomchestcontent.field_76296_e - weightedrandomchestcontent.field_76295_d + 1);

            if (weightedrandomchestcontent.field_76297_b.func_77976_d() >= k) {
                ItemStack itemstack = weightedrandomchestcontent.field_76297_b.func_77946_l();

                itemstack.field_77994_a = k;
                iinventory.func_70299_a(random.nextInt(iinventory.func_70302_i_()), itemstack);
            } else {
                for (int l = 0; l < k; ++l) {
                    ItemStack itemstack1 = weightedrandomchestcontent.field_76297_b.func_77946_l();

                    itemstack1.field_77994_a = 1;
                    iinventory.func_70299_a(random.nextInt(iinventory.func_70302_i_()), itemstack1);
                }
            }
        }

    }

    public static void func_150706_a(Random random, WeightedRandomChestContent[] aweightedrandomchestcontent, TileEntityDispenser tileentitydispenser, int i) {
        for (int j = 0; j < i; ++j) {
            WeightedRandomChestContent weightedrandomchestcontent = (WeightedRandomChestContent) WeightedRandom.func_76274_a(random, aweightedrandomchestcontent);
            int k = weightedrandomchestcontent.field_76295_d + random.nextInt(weightedrandomchestcontent.field_76296_e - weightedrandomchestcontent.field_76295_d + 1);

            if (weightedrandomchestcontent.field_76297_b.func_77976_d() >= k) {
                ItemStack itemstack = weightedrandomchestcontent.field_76297_b.func_77946_l();

                itemstack.field_77994_a = k;
                tileentitydispenser.func_70299_a(random.nextInt(tileentitydispenser.func_70302_i_()), itemstack);
            } else {
                for (int l = 0; l < k; ++l) {
                    ItemStack itemstack1 = weightedrandomchestcontent.field_76297_b.func_77946_l();

                    itemstack1.field_77994_a = 1;
                    tileentitydispenser.func_70299_a(random.nextInt(tileentitydispenser.func_70302_i_()), itemstack1);
                }
            }
        }

    }

    public static WeightedRandomChestContent[] func_92080_a(WeightedRandomChestContent[] aweightedrandomchestcontent, WeightedRandomChestContent... aweightedrandomchestcontent1) {
        WeightedRandomChestContent[] aweightedrandomchestcontent2 = new WeightedRandomChestContent[aweightedrandomchestcontent.length + aweightedrandomchestcontent1.length];
        int i = 0;

        for (int j = 0; j < aweightedrandomchestcontent.length; ++j) {
            aweightedrandomchestcontent2[i++] = aweightedrandomchestcontent[j];
        }

        WeightedRandomChestContent[] aweightedrandomchestcontent3 = aweightedrandomchestcontent1;
        int k = aweightedrandomchestcontent1.length;

        for (int l = 0; l < k; ++l) {
            WeightedRandomChestContent weightedrandomchestcontent = aweightedrandomchestcontent3[l];

            aweightedrandomchestcontent2[i++] = weightedrandomchestcontent;
        }

        return aweightedrandomchestcontent2;
    }
}
