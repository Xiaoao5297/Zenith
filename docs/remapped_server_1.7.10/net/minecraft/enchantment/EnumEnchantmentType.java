package net.minecraft.enchantment;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public enum EnumEnchantmentType {

    all, armor, armor_feet, armor_legs, armor_torso, armor_head, weapon, digger, fishing_rod, breakable, bow;

    private EnumEnchantmentType() {}

    public boolean func_77557_a(Item item) {
        if (this == EnumEnchantmentType.all) {
            return true;
        } else if (this == EnumEnchantmentType.breakable && item.func_77645_m()) {
            return true;
        } else if (item instanceof ItemArmor) {
            if (this == EnumEnchantmentType.armor) {
                return true;
            } else {
                ItemArmor itemarmor = (ItemArmor) item;

                return itemarmor.field_77881_a == 0 ? this == EnumEnchantmentType.armor_head : (itemarmor.field_77881_a == 2 ? this == EnumEnchantmentType.armor_legs : (itemarmor.field_77881_a == 1 ? this == EnumEnchantmentType.armor_torso : (itemarmor.field_77881_a == 3 ? this == EnumEnchantmentType.armor_feet : false)));
            }
        } else {
            return item instanceof ItemSword ? this == EnumEnchantmentType.weapon : (item instanceof ItemTool ? this == EnumEnchantmentType.digger : (item instanceof ItemBow ? this == EnumEnchantmentType.bow : (item instanceof ItemFishingRod ? this == EnumEnchantmentType.fishing_rod : false)));
        }
    }
}
