package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class EnchantmentThorns extends Enchantment {

    public EnchantmentThorns(int i, int j) {
        super(i, j, EnumEnchantmentType.armor_torso);
        this.func_77322_b("thorns");
    }

    @Override
    public int func_77321_a(int i) {
        return 10 + 20 * (i - 1);
    }

    @Override
    public int func_77317_b(int i) {
        return super.func_77321_a(i) + 50;
    }

    @Override
    public int func_77325_b() {
        return 3;
    }

    @Override
    public boolean func_92089_a(ItemStack itemstack) {
        return itemstack.func_77973_b() instanceof ItemArmor ? true : super.func_92089_a(itemstack);
    }

    @Override
    public void func_151367_b(EntityLivingBase entitylivingbase, Entity entity, int i) {
        Random random = entitylivingbase.func_70681_au();
        ItemStack itemstack = EnchantmentHelper.func_92099_a(Enchantment.field_92091_k, entitylivingbase);

        if (func_92094_a(i, random)) {
            entity.func_70097_a(DamageSource.func_92087_a(entitylivingbase), (float) func_92095_b(i, random));
            entity.func_85030_a("damage.thorns", 0.5F, 1.0F);
            if (itemstack != null) {
                itemstack.func_77972_a(3, entitylivingbase);
            }
        } else if (itemstack != null) {
            itemstack.func_77972_a(1, entitylivingbase);
        }

    }

    public static boolean func_92094_a(int i, Random random) {
        return i <= 0 ? false : random.nextFloat() < 0.15F * (float) i;
    }

    public static int func_92095_b(int i, Random random) {
        return i > 10 ? i - 10 : 1 + random.nextInt(4);
    }
}
