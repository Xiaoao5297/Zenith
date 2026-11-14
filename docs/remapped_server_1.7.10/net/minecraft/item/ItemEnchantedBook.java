package net.minecraft.item;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.WeightedRandomChestContent;

public class ItemEnchantedBook extends Item {

    public ItemEnchantedBook() {}

    @Override
    public boolean func_77616_k(ItemStack itemstack) {
        return false;
    }

    @Override
    public EnumRarity func_77613_e(ItemStack itemstack) {
        return this.func_92110_g(itemstack).func_74745_c() > 0 ? EnumRarity.uncommon : super.func_77613_e(itemstack);
    }

    public NBTTagList func_92110_g(ItemStack itemstack) {
        return itemstack.field_77990_d != null && itemstack.field_77990_d.func_150297_b("StoredEnchantments", 9) ? (NBTTagList) itemstack.field_77990_d.func_74781_a("StoredEnchantments") : new NBTTagList();
    }

    public void func_92115_a(ItemStack itemstack, EnchantmentData enchantmentdata) {
        NBTTagList nbttaglist = this.func_92110_g(itemstack);
        boolean flag = true;

        for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.func_150305_b(i);

            if (nbttagcompound.func_74765_d("id") == enchantmentdata.field_76302_b.field_77352_x) {
                if (nbttagcompound.func_74765_d("lvl") < enchantmentdata.field_76303_c) {
                    nbttagcompound.func_74777_a("lvl", (short) enchantmentdata.field_76303_c);
                }

                flag = false;
                break;
            }
        }

        if (flag) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            nbttagcompound1.func_74777_a("id", (short) enchantmentdata.field_76302_b.field_77352_x);
            nbttagcompound1.func_74777_a("lvl", (short) enchantmentdata.field_76303_c);
            nbttaglist.func_74742_a(nbttagcompound1);
        }

        if (!itemstack.func_77942_o()) {
            itemstack.func_77982_d(new NBTTagCompound());
        }

        itemstack.func_77978_p().func_74782_a("StoredEnchantments", nbttaglist);
    }

    public ItemStack func_92111_a(EnchantmentData enchantmentdata) {
        ItemStack itemstack = new ItemStack(this);

        this.func_92115_a(itemstack, enchantmentdata);
        return itemstack;
    }

    public WeightedRandomChestContent func_92114_b(Random random) {
        return this.func_92112_a(random, 1, 1, 1);
    }

    public WeightedRandomChestContent func_92112_a(Random random, int i, int j, int k) {
        ItemStack itemstack = new ItemStack(Items.field_151122_aG, 1, 0);

        EnchantmentHelper.func_77504_a(random, itemstack, 30);
        return new WeightedRandomChestContent(itemstack, i, j, k);
    }
}
