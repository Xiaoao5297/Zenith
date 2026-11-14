package net.minecraft.item.crafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class CraftingManager {

    private static final CraftingManager field_77598_a = new CraftingManager();
    private List field_77597_b = new ArrayList();

    public static final CraftingManager func_77594_a() {
        return CraftingManager.field_77598_a;
    }

    private CraftingManager() {
        (new RecipesTools()).func_77586_a(this);
        (new RecipesWeapons()).func_77583_a(this);
        (new RecipesIngots()).func_77590_a(this);
        (new RecipesFood()).func_77608_a(this);
        (new RecipesCrafting()).func_77589_a(this);
        (new RecipesArmor()).func_77609_a(this);
        (new RecipesDyes()).func_77607_a(this);
        this.field_77597_b.add(new RecipesArmorDyes());
        this.field_77597_b.add(new RecipeBookCloning());
        this.field_77597_b.add(new RecipesMapCloning());
        this.field_77597_b.add(new RecipesMapExtending());
        this.field_77597_b.add(new RecipeFireworks());
        this.func_92103_a(new ItemStack(Items.field_151121_aF, 3), "###", '#', Items.field_151120_aE);
        this.func_77596_b(new ItemStack(Items.field_151122_aG, 1), Items.field_151121_aF, Items.field_151121_aF, Items.field_151121_aF, Items.field_151116_aA);
        this.func_77596_b(new ItemStack(Items.field_151099_bA, 1), Items.field_151122_aG, new ItemStack(Items.field_151100_aR, 1, 0), Items.field_151008_G);
        this.func_92103_a(new ItemStack(Blocks.field_150422_aJ, 2), "###", "###", '#', Items.field_151055_y);
        this.func_92103_a(new ItemStack(Blocks.field_150463_bK, 6, 0), "###", "###", '#', Blocks.field_150347_e);
        this.func_92103_a(new ItemStack(Blocks.field_150463_bK, 6, 1), "###", "###", '#', Blocks.field_150341_Y);
        this.func_92103_a(new ItemStack(Blocks.field_150386_bk, 6), "###", "###", '#', Blocks.field_150385_bj);
        this.func_92103_a(new ItemStack(Blocks.field_150396_be, 1), "#W#", "#W#", '#', Items.field_151055_y, 'W', Blocks.field_150344_f);
        this.func_92103_a(new ItemStack(Blocks.field_150421_aI, 1), "###", "#X#", "###", '#', Blocks.field_150344_f, 'X', Items.field_151045_i);
        this.func_92103_a(new ItemStack(Items.field_151058_ca, 2), "~~ ", "~O ", "  ~", '~', Items.field_151007_F, 'O', Items.field_151123_aH);
        this.func_92103_a(new ItemStack(Blocks.field_150323_B, 1), "###", "#X#", "###", '#', Blocks.field_150344_f, 'X', Items.field_151137_ax);
        this.func_92103_a(new ItemStack(Blocks.field_150342_X, 1), "###", "XXX", "###", '#', Blocks.field_150344_f, 'X', Items.field_151122_aG);
        this.func_92103_a(new ItemStack(Blocks.field_150433_aE, 1), "##", "##", '#', Items.field_151126_ay);
        this.func_92103_a(new ItemStack(Blocks.field_150431_aC, 6), "###", '#', Blocks.field_150433_aE);
        this.func_92103_a(new ItemStack(Blocks.field_150435_aG, 1), "##", "##", '#', Items.field_151119_aD);
        this.func_92103_a(new ItemStack(Blocks.field_150336_V, 1), "##", "##", '#', Items.field_151118_aC);
        this.func_92103_a(new ItemStack(Blocks.field_150426_aN, 1), "##", "##", '#', Items.field_151114_aO);
        this.func_92103_a(new ItemStack(Blocks.field_150371_ca, 1), "##", "##", '#', Items.field_151128_bU);
        this.func_92103_a(new ItemStack(Blocks.field_150325_L, 1), "##", "##", '#', Items.field_151007_F);
        this.func_92103_a(new ItemStack(Blocks.field_150335_W, 1), "X#X", "#X#", "X#X", 'X', Items.field_151016_H, '#', Blocks.field_150354_m);
        this.func_92103_a(new ItemStack(Blocks.field_150333_U, 6, 3), "###", '#', Blocks.field_150347_e);
        this.func_92103_a(new ItemStack(Blocks.field_150333_U, 6, 0), "###", '#', Blocks.field_150348_b);
        this.func_92103_a(new ItemStack(Blocks.field_150333_U, 6, 1), "###", '#', Blocks.field_150322_A);
        this.func_92103_a(new ItemStack(Blocks.field_150333_U, 6, 4), "###", '#', Blocks.field_150336_V);
        this.func_92103_a(new ItemStack(Blocks.field_150333_U, 6, 5), "###", '#', Blocks.field_150417_aV);
        this.func_92103_a(new ItemStack(Blocks.field_150333_U, 6, 6), "###", '#', Blocks.field_150385_bj);
        this.func_92103_a(new ItemStack(Blocks.field_150333_U, 6, 7), "###", '#', Blocks.field_150371_ca);
        this.func_92103_a(new ItemStack(Blocks.field_150376_bx, 6, 0), "###", '#', new ItemStack(Blocks.field_150344_f, 1, 0));
        this.func_92103_a(new ItemStack(Blocks.field_150376_bx, 6, 2), "###", '#', new ItemStack(Blocks.field_150344_f, 1, 2));
        this.func_92103_a(new ItemStack(Blocks.field_150376_bx, 6, 1), "###", '#', new ItemStack(Blocks.field_150344_f, 1, 1));
        this.func_92103_a(new ItemStack(Blocks.field_150376_bx, 6, 3), "###", '#', new ItemStack(Blocks.field_150344_f, 1, 3));
        this.func_92103_a(new ItemStack(Blocks.field_150376_bx, 6, 4), "###", '#', new ItemStack(Blocks.field_150344_f, 1, 4));
        this.func_92103_a(new ItemStack(Blocks.field_150376_bx, 6, 5), "###", '#', new ItemStack(Blocks.field_150344_f, 1, 5));
        this.func_92103_a(new ItemStack(Blocks.field_150468_ap, 3), "# #", "###", "# #", '#', Items.field_151055_y);
        this.func_92103_a(new ItemStack(Items.field_151135_aq, 1), "##", "##", "##", '#', Blocks.field_150344_f);
        this.func_92103_a(new ItemStack(Blocks.field_150415_aT, 2), "###", "###", '#', Blocks.field_150344_f);
        this.func_92103_a(new ItemStack(Items.field_151139_aw, 1), "##", "##", "##", '#', Items.field_151042_j);
        this.func_92103_a(new ItemStack(Items.field_151155_ap, 3), "###", "###", " X ", '#', Blocks.field_150344_f, 'X', Items.field_151055_y);
        this.func_92103_a(new ItemStack(Items.field_151105_aU, 1), "AAA", "BEB", "CCC", 'A', Items.field_151117_aB, 'B', Items.field_151102_aT, 'C', Items.field_151015_O, 'E', Items.field_151110_aK);
        this.func_92103_a(new ItemStack(Items.field_151102_aT, 1), "#", '#', Items.field_151120_aE);
        this.func_92103_a(new ItemStack(Blocks.field_150344_f, 4, 0), "#", '#', new ItemStack(Blocks.field_150364_r, 1, 0));
        this.func_92103_a(new ItemStack(Blocks.field_150344_f, 4, 1), "#", '#', new ItemStack(Blocks.field_150364_r, 1, 1));
        this.func_92103_a(new ItemStack(Blocks.field_150344_f, 4, 2), "#", '#', new ItemStack(Blocks.field_150364_r, 1, 2));
        this.func_92103_a(new ItemStack(Blocks.field_150344_f, 4, 3), "#", '#', new ItemStack(Blocks.field_150364_r, 1, 3));
        this.func_92103_a(new ItemStack(Blocks.field_150344_f, 4, 4), "#", '#', new ItemStack(Blocks.field_150363_s, 1, 0));
        this.func_92103_a(new ItemStack(Blocks.field_150344_f, 4, 5), "#", '#', new ItemStack(Blocks.field_150363_s, 1, 1));
        this.func_92103_a(new ItemStack(Items.field_151055_y, 4), "#", "#", '#', Blocks.field_150344_f);
        this.func_92103_a(new ItemStack(Blocks.field_150478_aa, 4), "X", "#", 'X', Items.field_151044_h, '#', Items.field_151055_y);
        this.func_92103_a(new ItemStack(Blocks.field_150478_aa, 4), "X", "#", 'X', new ItemStack(Items.field_151044_h, 1, 1), '#', Items.field_151055_y);
        this.func_92103_a(new ItemStack(Items.field_151054_z, 4), "# #", " # ", '#', Blocks.field_150344_f);
        this.func_92103_a(new ItemStack(Items.field_151069_bo, 3), "# #", " # ", '#', Blocks.field_150359_w);
        this.func_92103_a(new ItemStack(Blocks.field_150448_aq, 16), "X X", "X#X", "X X", 'X', Items.field_151042_j, '#', Items.field_151055_y);
        this.func_92103_a(new ItemStack(Blocks.field_150318_D, 6), "X X", "X#X", "XRX", 'X', Items.field_151043_k, 'R', Items.field_151137_ax, '#', Items.field_151055_y);
        this.func_92103_a(new ItemStack(Blocks.field_150408_cc, 6), "XSX", "X#X", "XSX", 'X', Items.field_151042_j, '#', Blocks.field_150429_aA, 'S', Items.field_151055_y);
        this.func_92103_a(new ItemStack(Blocks.field_150319_E, 6), "X X", "X#X", "XRX", 'X', Items.field_151042_j, 'R', Items.field_151137_ax, '#', Blocks.field_150456_au);
        this.func_92103_a(new ItemStack(Items.field_151143_au, 1), "# #", "###", '#', Items.field_151042_j);
        this.func_92103_a(new ItemStack(Items.field_151066_bu, 1), "# #", "# #", "###", '#', Items.field_151042_j);
        this.func_92103_a(new ItemStack(Items.field_151067_bt, 1), " B ", "###", '#', Blocks.field_150347_e, 'B', Items.field_151072_bj);
        this.func_92103_a(new ItemStack(Blocks.field_150428_aP, 1), "A", "B", 'A', Blocks.field_150423_aK, 'B', Blocks.field_150478_aa);
        this.func_92103_a(new ItemStack(Items.field_151108_aI, 1), "A", "B", 'A', Blocks.field_150486_ae, 'B', Items.field_151143_au);
        this.func_92103_a(new ItemStack(Items.field_151109_aJ, 1), "A", "B", 'A', Blocks.field_150460_al, 'B', Items.field_151143_au);
        this.func_92103_a(new ItemStack(Items.field_151142_bV, 1), "A", "B", 'A', Blocks.field_150335_W, 'B', Items.field_151143_au);
        this.func_92103_a(new ItemStack(Items.field_151140_bW, 1), "A", "B", 'A', Blocks.field_150438_bZ, 'B', Items.field_151143_au);
        this.func_92103_a(new ItemStack(Items.field_151124_az, 1), "# #", "###", '#', Blocks.field_150344_f);
        this.func_92103_a(new ItemStack(Items.field_151133_ar, 1), "# #", " # ", '#', Items.field_151042_j);
        this.func_92103_a(new ItemStack(Items.field_151162_bE, 1), "# #", " # ", '#', Items.field_151118_aC);
        this.func_77596_b(new ItemStack(Items.field_151033_d, 1), new ItemStack(Items.field_151042_j, 1), new ItemStack(Items.field_151145_ak, 1));
        this.func_92103_a(new ItemStack(Items.field_151025_P, 1), "###", '#', Items.field_151015_O);
        this.func_92103_a(new ItemStack(Blocks.field_150476_ad, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.field_150344_f, 1, 0));
        this.func_92103_a(new ItemStack(Blocks.field_150487_bG, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.field_150344_f, 1, 2));
        this.func_92103_a(new ItemStack(Blocks.field_150485_bF, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.field_150344_f, 1, 1));
        this.func_92103_a(new ItemStack(Blocks.field_150481_bH, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.field_150344_f, 1, 3));
        this.func_92103_a(new ItemStack(Blocks.field_150400_ck, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.field_150344_f, 1, 4));
        this.func_92103_a(new ItemStack(Blocks.field_150401_cl, 4), "#  ", "## ", "###", '#', new ItemStack(Blocks.field_150344_f, 1, 5));
        this.func_92103_a(new ItemStack(Items.field_151112_aM, 1), "  #", " #X", "# X", '#', Items.field_151055_y, 'X', Items.field_151007_F);
        this.func_92103_a(new ItemStack(Items.field_151146_bM, 1), "# ", " X", '#', Items.field_151112_aM, 'X', Items.field_151172_bF).func_92100_c();
        this.func_92103_a(new ItemStack(Blocks.field_150446_ar, 4), "#  ", "## ", "###", '#', Blocks.field_150347_e);
        this.func_92103_a(new ItemStack(Blocks.field_150389_bf, 4), "#  ", "## ", "###", '#', Blocks.field_150336_V);
        this.func_92103_a(new ItemStack(Blocks.field_150390_bg, 4), "#  ", "## ", "###", '#', Blocks.field_150417_aV);
        this.func_92103_a(new ItemStack(Blocks.field_150387_bl, 4), "#  ", "## ", "###", '#', Blocks.field_150385_bj);
        this.func_92103_a(new ItemStack(Blocks.field_150372_bz, 4), "#  ", "## ", "###", '#', Blocks.field_150322_A);
        this.func_92103_a(new ItemStack(Blocks.field_150370_cb, 4), "#  ", "## ", "###", '#', Blocks.field_150371_ca);
        this.func_92103_a(new ItemStack(Items.field_151159_an, 1), "###", "#X#", "###", '#', Items.field_151055_y, 'X', Blocks.field_150325_L);
        this.func_92103_a(new ItemStack(Items.field_151160_bD, 1), "###", "#X#", "###", '#', Items.field_151055_y, 'X', Items.field_151116_aA);
        this.func_92103_a(new ItemStack(Items.field_151153_ao, 1, 0), "###", "#X#", "###", '#', Items.field_151043_k, 'X', Items.field_151034_e);
        this.func_92103_a(new ItemStack(Items.field_151153_ao, 1, 1), "###", "#X#", "###", '#', Blocks.field_150340_R, 'X', Items.field_151034_e);
        this.func_92103_a(new ItemStack(Items.field_151150_bK, 1, 0), "###", "#X#", "###", '#', Items.field_151074_bl, 'X', Items.field_151172_bF);
        this.func_92103_a(new ItemStack(Items.field_151060_bw, 1), "###", "#X#", "###", '#', Items.field_151074_bl, 'X', Items.field_151127_ba);
        this.func_92103_a(new ItemStack(Blocks.field_150442_at, 1), "X", "#", '#', Blocks.field_150347_e, 'X', Items.field_151055_y);
        this.func_92103_a(new ItemStack(Blocks.field_150479_bC, 2), "I", "S", "#", '#', Blocks.field_150344_f, 'S', Items.field_151055_y, 'I', Items.field_151042_j);
        this.func_92103_a(new ItemStack(Blocks.field_150429_aA, 1), "X", "#", '#', Items.field_151055_y, 'X', Items.field_151137_ax);
        this.func_92103_a(new ItemStack(Items.field_151107_aW, 1), "#X#", "III", '#', Blocks.field_150429_aA, 'X', Items.field_151137_ax, 'I', Blocks.field_150348_b);
        this.func_92103_a(new ItemStack(Items.field_151132_bS, 1), " # ", "#X#", "III", '#', Blocks.field_150429_aA, 'X', Items.field_151128_bU, 'I', Blocks.field_150348_b);
        this.func_92103_a(new ItemStack(Items.field_151113_aN, 1), " # ", "#X#", " # ", '#', Items.field_151043_k, 'X', Items.field_151137_ax);
        this.func_92103_a(new ItemStack(Items.field_151111_aL, 1), " # ", "#X#", " # ", '#', Items.field_151042_j, 'X', Items.field_151137_ax);
        this.func_92103_a(new ItemStack(Items.field_151148_bJ, 1), "###", "#X#", "###", '#', Items.field_151121_aF, 'X', Items.field_151111_aL);
        this.func_92103_a(new ItemStack(Blocks.field_150430_aB, 1), "#", '#', Blocks.field_150348_b);
        this.func_92103_a(new ItemStack(Blocks.field_150471_bO, 1), "#", '#', Blocks.field_150344_f);
        this.func_92103_a(new ItemStack(Blocks.field_150456_au, 1), "##", '#', Blocks.field_150348_b);
        this.func_92103_a(new ItemStack(Blocks.field_150452_aw, 1), "##", '#', Blocks.field_150344_f);
        this.func_92103_a(new ItemStack(Blocks.field_150443_bT, 1), "##", '#', Items.field_151042_j);
        this.func_92103_a(new ItemStack(Blocks.field_150445_bS, 1), "##", '#', Items.field_151043_k);
        this.func_92103_a(new ItemStack(Blocks.field_150367_z, 1), "###", "#X#", "#R#", '#', Blocks.field_150347_e, 'X', Items.field_151031_f, 'R', Items.field_151137_ax);
        this.func_92103_a(new ItemStack(Blocks.field_150409_cd, 1), "###", "# #", "#R#", '#', Blocks.field_150347_e, 'R', Items.field_151137_ax);
        this.func_92103_a(new ItemStack(Blocks.field_150331_J, 1), "TTT", "#X#", "#R#", '#', Blocks.field_150347_e, 'X', Items.field_151042_j, 'R', Items.field_151137_ax, 'T', Blocks.field_150344_f);
        this.func_92103_a(new ItemStack(Blocks.field_150320_F, 1), "S", "P", 'S', Items.field_151123_aH, 'P', Blocks.field_150331_J);
        this.func_92103_a(new ItemStack(Items.field_151104_aV, 1), "###", "XXX", '#', Blocks.field_150325_L, 'X', Blocks.field_150344_f);
        this.func_92103_a(new ItemStack(Blocks.field_150381_bn, 1), " B ", "D#D", "###", '#', Blocks.field_150343_Z, 'B', Items.field_151122_aG, 'D', Items.field_151045_i);
        this.func_92103_a(new ItemStack(Blocks.field_150467_bQ, 1), "III", " i ", "iii", 'I', Blocks.field_150339_S, 'i', Items.field_151042_j);
        this.func_77596_b(new ItemStack(Items.field_151061_bv, 1), Items.field_151079_bi, Items.field_151065_br);
        this.func_77596_b(new ItemStack(Items.field_151059_bz, 3), Items.field_151016_H, Items.field_151065_br, Items.field_151044_h);
        this.func_77596_b(new ItemStack(Items.field_151059_bz, 3), Items.field_151016_H, Items.field_151065_br, new ItemStack(Items.field_151044_h, 1, 1));
        this.func_92103_a(new ItemStack(Blocks.field_150453_bW), "GGG", "QQQ", "WWW", 'G', Blocks.field_150359_w, 'Q', Items.field_151128_bU, 'W', Blocks.field_150376_bx);
        this.func_92103_a(new ItemStack(Blocks.field_150438_bZ), "I I", "ICI", " I ", 'I', Items.field_151042_j, 'C', Blocks.field_150486_ae);
        Collections.sort(this.field_77597_b, new CraftingManager$1(this));
    }

    ShapedRecipes func_92103_a(ItemStack itemstack, Object... aobject) {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;

        if (aobject[i] instanceof String[]) {
            String[] astring = (String[]) ((String[]) aobject[i++]);

            for (int l = 0; l < astring.length; ++l) {
                String s1 = astring[l];

                ++k;
                j = s1.length();
                s = s + s1;
            }
        } else {
            while (aobject[i] instanceof String) {
                String s2 = (String) aobject[i++];

                ++k;
                j = s2.length();
                s = s + s2;
            }
        }

        HashMap hashmap;

        for (hashmap = new HashMap(); i < aobject.length; i += 2) {
            Character character = (Character) aobject[i];
            ItemStack itemstack1 = null;

            if (aobject[i + 1] instanceof Item) {
                itemstack1 = new ItemStack((Item) aobject[i + 1]);
            } else if (aobject[i + 1] instanceof Block) {
                itemstack1 = new ItemStack((Block) aobject[i + 1], 1, 32767);
            } else if (aobject[i + 1] instanceof ItemStack) {
                itemstack1 = (ItemStack) aobject[i + 1];
            }

            hashmap.put(character, itemstack1);
        }

        ItemStack[] aitemstack = new ItemStack[j * k];

        for (int i1 = 0; i1 < j * k; ++i1) {
            char c0 = s.charAt(i1);

            if (hashmap.containsKey(c0)) {
                aitemstack[i1] = ((ItemStack) hashmap.get(c0)).func_77946_l();
            } else {
                aitemstack[i1] = null;
            }
        }

        ShapedRecipes shapedrecipes = new ShapedRecipes(j, k, aitemstack, itemstack);

        this.field_77597_b.add(shapedrecipes);
        return shapedrecipes;
    }

    void func_77596_b(ItemStack itemstack, Object... aobject) {
        ArrayList arraylist = new ArrayList();
        Object[] aobject1 = aobject;
        int i = aobject.length;

        for (int j = 0; j < i; ++j) {
            Object object = aobject1[j];

            if (object instanceof ItemStack) {
                arraylist.add(((ItemStack) object).func_77946_l());
            } else if (object instanceof Item) {
                arraylist.add(new ItemStack((Item) object));
            } else {
                if (!(object instanceof Block)) {
                    throw new RuntimeException("Invalid shapeless recipy!");
                }

                arraylist.add(new ItemStack((Block) object));
            }
        }

        this.field_77597_b.add(new ShapelessRecipes(itemstack, arraylist));
    }

    public ItemStack func_82787_a(InventoryCrafting inventorycrafting, World world) {
        int i = 0;
        ItemStack itemstack = null;
        ItemStack itemstack1 = null;

        int j;

        for (j = 0; j < inventorycrafting.func_70302_i_(); ++j) {
            ItemStack itemstack2 = inventorycrafting.func_70301_a(j);

            if (itemstack2 != null) {
                if (i == 0) {
                    itemstack = itemstack2;
                }

                if (i == 1) {
                    itemstack1 = itemstack2;
                }

                ++i;
            }
        }

        if (i == 2 && itemstack.func_77973_b() == itemstack1.func_77973_b() && itemstack.field_77994_a == 1 && itemstack1.field_77994_a == 1 && itemstack.func_77973_b().func_77645_m()) {
            Item item = itemstack.func_77973_b();
            int k = item.func_77612_l() - itemstack.func_77952_i();
            int l = item.func_77612_l() - itemstack1.func_77952_i();
            int i1 = k + l + item.func_77612_l() * 5 / 100;
            int j1 = item.func_77612_l() - i1;

            if (j1 < 0) {
                j1 = 0;
            }

            return new ItemStack(itemstack.func_77973_b(), 1, j1);
        } else {
            for (j = 0; j < this.field_77597_b.size(); ++j) {
                IRecipe irecipe = (IRecipe) this.field_77597_b.get(j);

                if (irecipe.func_77569_a(inventorycrafting, world)) {
                    return irecipe.func_77572_b(inventorycrafting);
                }
            }

            return null;
        }
    }

    public List func_77592_b() {
        return this.field_77597_b;
    }
}
