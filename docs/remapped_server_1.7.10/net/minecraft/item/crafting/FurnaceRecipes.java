package net.minecraft.item.crafting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood$FishType;
import net.minecraft.item.ItemStack;

public class FurnaceRecipes {

    private static final FurnaceRecipes field_77606_a = new FurnaceRecipes();
    private Map field_77604_b = new HashMap();
    private Map field_77605_c = new HashMap();

    public static FurnaceRecipes func_77602_a() {
        return FurnaceRecipes.field_77606_a;
    }

    private FurnaceRecipes() {
        this.func_151393_a(Blocks.field_150366_p, new ItemStack(Items.field_151042_j), 0.7F);
        this.func_151393_a(Blocks.field_150352_o, new ItemStack(Items.field_151043_k), 1.0F);
        this.func_151393_a(Blocks.field_150482_ag, new ItemStack(Items.field_151045_i), 1.0F);
        this.func_151393_a(Blocks.field_150354_m, new ItemStack(Blocks.field_150359_w), 0.1F);
        this.func_151396_a(Items.field_151147_al, new ItemStack(Items.field_151157_am), 0.35F);
        this.func_151396_a(Items.field_151082_bd, new ItemStack(Items.field_151083_be), 0.35F);
        this.func_151396_a(Items.field_151076_bf, new ItemStack(Items.field_151077_bg), 0.35F);
        this.func_151393_a(Blocks.field_150347_e, new ItemStack(Blocks.field_150348_b), 0.1F);
        this.func_151396_a(Items.field_151119_aD, new ItemStack(Items.field_151118_aC), 0.3F);
        this.func_151393_a(Blocks.field_150435_aG, new ItemStack(Blocks.field_150405_ch), 0.35F);
        this.func_151393_a(Blocks.field_150434_aF, new ItemStack(Items.field_151100_aR, 1, 2), 0.2F);
        this.func_151393_a(Blocks.field_150364_r, new ItemStack(Items.field_151044_h, 1, 1), 0.15F);
        this.func_151393_a(Blocks.field_150363_s, new ItemStack(Items.field_151044_h, 1, 1), 0.15F);
        this.func_151393_a(Blocks.field_150412_bA, new ItemStack(Items.field_151166_bC), 1.0F);
        this.func_151396_a(Items.field_151174_bG, new ItemStack(Items.field_151168_bH), 0.35F);
        this.func_151393_a(Blocks.field_150424_aL, new ItemStack(Items.field_151130_bT), 0.1F);
        ItemFishFood$FishType[] aitemfishfood$fishtype = ItemFishFood$FishType.values();
        int i = aitemfishfood$fishtype.length;

        for (int j = 0; j < i; ++j) {
            ItemFishFood$FishType itemfishfood$fishtype = aitemfishfood$fishtype[j];

            if (itemfishfood$fishtype.func_150973_i()) {
                this.func_151394_a(new ItemStack(Items.field_151115_aP, 1, itemfishfood$fishtype.func_150976_a()), new ItemStack(Items.field_151101_aQ, 1, itemfishfood$fishtype.func_150976_a()), 0.35F);
            }
        }

        this.func_151393_a(Blocks.field_150365_q, new ItemStack(Items.field_151044_h), 0.1F);
        this.func_151393_a(Blocks.field_150450_ax, new ItemStack(Items.field_151137_ax), 0.7F);
        this.func_151393_a(Blocks.field_150369_x, new ItemStack(Items.field_151100_aR, 1, 4), 0.2F);
        this.func_151393_a(Blocks.field_150449_bY, new ItemStack(Items.field_151128_bU), 0.2F);
    }

    public void func_151393_a(Block block, ItemStack itemstack, float f) {
        this.func_151396_a(Item.func_150898_a(block), itemstack, f);
    }

    public void func_151396_a(Item item, ItemStack itemstack, float f) {
        this.func_151394_a(new ItemStack(item, 1, 32767), itemstack, f);
    }

    public void func_151394_a(ItemStack itemstack, ItemStack itemstack1, float f) {
        this.field_77604_b.put(itemstack, itemstack1);
        this.field_77605_c.put(itemstack1, f);
    }

    public ItemStack func_151395_a(ItemStack itemstack) {
        Iterator iterator = this.field_77604_b.entrySet().iterator();

        Entry entry;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            entry = (Entry) iterator.next();
        } while (!this.func_151397_a(itemstack, (ItemStack) entry.getKey()));

        return (ItemStack) entry.getValue();
    }

    private boolean func_151397_a(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack1.func_77973_b() == itemstack.func_77973_b() && (itemstack1.func_77960_j() == 32767 || itemstack1.func_77960_j() == itemstack.func_77960_j());
    }

    public Map func_77599_b() {
        return this.field_77604_b;
    }

    public float func_151398_b(ItemStack itemstack) {
        Iterator iterator = this.field_77605_c.entrySet().iterator();

        Entry entry;

        do {
            if (!iterator.hasNext()) {
                return 0.0F;
            }

            entry = (Entry) iterator.next();
        } while (!this.func_151397_a(itemstack, (ItemStack) entry.getKey()));

        return (Float) entry.getValue();
    }
}
