package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class EnchantmentDamage extends Enchantment {

    private static final String[] field_77359_A = new String[]{"all", "undead", "arthropods"};
    private static final int[] field_77360_B = new int[]{1, 5, 5};
    private static final int[] field_77362_C = new int[]{11, 8, 8};
    private static final int[] field_77358_D = new int[]{20, 20, 20};
    public final int field_77361_a;

    public EnchantmentDamage(int i, int j, int k) {
        super(i, j, EnumEnchantmentType.weapon);
        this.field_77361_a = k;
    }

    @Override
    public int func_77321_a(int i) {
        return EnchantmentDamage.field_77360_B[this.field_77361_a] + (i - 1) * EnchantmentDamage.field_77362_C[this.field_77361_a];
    }

    @Override
    public int func_77317_b(int i) {
        return this.func_77321_a(i) + EnchantmentDamage.field_77358_D[this.field_77361_a];
    }

    @Override
    public int func_77325_b() {
        return 5;
    }

    @Override
    public float func_152376_a(int i, EnumCreatureAttribute enumcreatureattribute) {
        return this.field_77361_a == 0 ? (float) i * 1.25F : (this.field_77361_a == 1 && enumcreatureattribute == EnumCreatureAttribute.UNDEAD ? (float) i * 2.5F : (this.field_77361_a == 2 && enumcreatureattribute == EnumCreatureAttribute.ARTHROPOD ? (float) i * 2.5F : 0.0F));
    }

    @Override
    public String func_77320_a() {
        return "enchantment.damage." + EnchantmentDamage.field_77359_A[this.field_77361_a];
    }

    @Override
    public boolean func_77326_a(Enchantment enchantment) {
        return !(enchantment instanceof EnchantmentDamage);
    }

    @Override
    public boolean func_92089_a(ItemStack itemstack) {
        return itemstack.func_77973_b() instanceof ItemAxe ? true : super.func_92089_a(itemstack);
    }

    @Override
    public void func_151368_a(EntityLivingBase entitylivingbase, Entity entity, int i) {
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entitylivingbase1 = (EntityLivingBase) entity;

            if (this.field_77361_a == 2 && entitylivingbase1.func_70668_bt() == EnumCreatureAttribute.ARTHROPOD) {
                int j = 20 + entitylivingbase.func_70681_au().nextInt(10 * i);

                entitylivingbase1.func_70690_d(new PotionEffect(Potion.field_76421_d.field_76415_H, j, 3));
            }
        }

    }
}
