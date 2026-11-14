package net.minecraft.item.crafting;

import java.util.ArrayList;
import net.minecraft.block.BlockColored;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor$ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RecipesArmorDyes implements IRecipe {

    public RecipesArmorDyes() {}

    @Override
    public boolean func_77569_a(InventoryCrafting inventorycrafting, World world) {
        ItemStack itemstack = null;
        ArrayList arraylist = new ArrayList();

        for (int i = 0; i < inventorycrafting.func_70302_i_(); ++i) {
            ItemStack itemstack1 = inventorycrafting.func_70301_a(i);

            if (itemstack1 != null) {
                if (itemstack1.func_77973_b() instanceof ItemArmor) {
                    ItemArmor itemarmor = (ItemArmor) itemstack1.func_77973_b();

                    if (itemarmor.func_82812_d() != ItemArmor$ArmorMaterial.CLOTH || itemstack != null) {
                        return false;
                    }

                    itemstack = itemstack1;
                } else {
                    if (itemstack1.func_77973_b() != Items.field_151100_aR) {
                        return false;
                    }

                    arraylist.add(itemstack1);
                }
            }
        }

        return itemstack != null && !arraylist.isEmpty();
    }

    @Override
    public ItemStack func_77572_b(InventoryCrafting inventorycrafting) {
        ItemStack itemstack = null;
        int[] aint = new int[3];
        int i = 0;
        int j = 0;
        ItemArmor itemarmor = null;

        int k;
        int l;
        float f;
        float f1;
        int i1;

        for (k = 0; k < inventorycrafting.func_70302_i_(); ++k) {
            ItemStack itemstack1 = inventorycrafting.func_70301_a(k);

            if (itemstack1 != null) {
                if (itemstack1.func_77973_b() instanceof ItemArmor) {
                    itemarmor = (ItemArmor) itemstack1.func_77973_b();
                    if (itemarmor.func_82812_d() != ItemArmor$ArmorMaterial.CLOTH || itemstack != null) {
                        return null;
                    }

                    itemstack = itemstack1.func_77946_l();
                    itemstack.field_77994_a = 1;
                    if (itemarmor.func_82816_b_(itemstack1)) {
                        l = itemarmor.func_82814_b(itemstack);
                        f = (float) (l >> 16 & 255) / 255.0F;
                        f1 = (float) (l >> 8 & 255) / 255.0F;
                        float f2 = (float) (l & 255) / 255.0F;

                        i = (int) ((float) i + Math.max(f, Math.max(f1, f2)) * 255.0F);
                        aint[0] = (int) ((float) aint[0] + f * 255.0F);
                        aint[1] = (int) ((float) aint[1] + f1 * 255.0F);
                        aint[2] = (int) ((float) aint[2] + f2 * 255.0F);
                        ++j;
                    }
                } else {
                    if (itemstack1.func_77973_b() != Items.field_151100_aR) {
                        return null;
                    }

                    float[] afloat = EntitySheep.field_70898_d[BlockColored.func_150032_b(itemstack1.func_77960_j())];
                    int j1 = (int) (afloat[0] * 255.0F);
                    int k1 = (int) (afloat[1] * 255.0F);

                    i1 = (int) (afloat[2] * 255.0F);
                    i += Math.max(j1, Math.max(k1, i1));
                    aint[0] += j1;
                    aint[1] += k1;
                    aint[2] += i1;
                    ++j;
                }
            }
        }

        if (itemarmor == null) {
            return null;
        } else {
            k = aint[0] / j;
            int l1 = aint[1] / j;

            l = aint[2] / j;
            f = (float) i / (float) j;
            f1 = (float) Math.max(k, Math.max(l1, l));
            k = (int) ((float) k * f / f1);
            l1 = (int) ((float) l1 * f / f1);
            l = (int) ((float) l * f / f1);
            i1 = (k << 8) + l1;
            i1 = (i1 << 8) + l;
            itemarmor.func_82813_b(itemstack, i1);
            return itemstack;
        }
    }

    @Override
    public int func_77570_a() {
        return 10;
    }

    @Override
    public ItemStack func_77571_b() {
        return null;
    }
}
