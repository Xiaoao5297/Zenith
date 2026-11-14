package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;

public class EnchantmentProtection extends Enchantment {

    private static final String[] field_77354_A = new String[]{"all", "fire", "fall", "explosion", "projectile"};
    private static final int[] field_77355_B = new int[]{1, 10, 5, 5, 3};
    private static final int[] field_77357_C = new int[]{11, 8, 6, 8, 6};
    private static final int[] field_77353_D = new int[]{20, 12, 10, 12, 15};
    public final int field_77356_a;

    public EnchantmentProtection(int i, int j, int k) {
        super(i, j, EnumEnchantmentType.armor);
        this.field_77356_a = k;
        if (k == 2) {
            this.field_77351_y = EnumEnchantmentType.armor_feet;
        }

    }

    @Override
    public int func_77321_a(int i) {
        return EnchantmentProtection.field_77355_B[this.field_77356_a] + (i - 1) * EnchantmentProtection.field_77357_C[this.field_77356_a];
    }

    @Override
    public int func_77317_b(int i) {
        return this.func_77321_a(i) + EnchantmentProtection.field_77353_D[this.field_77356_a];
    }

    @Override
    public int func_77325_b() {
        return 4;
    }

    @Override
    public int func_77318_a(int i, DamageSource damagesource) {
        if (damagesource.func_76357_e()) {
            return 0;
        } else {
            float f = (float) (6 + i * i) / 3.0F;

            return this.field_77356_a == 0 ? MathHelper.func_76141_d(f * 0.75F) : (this.field_77356_a == 1 && damagesource.func_76347_k() ? MathHelper.func_76141_d(f * 1.25F) : (this.field_77356_a == 2 && damagesource == DamageSource.field_76379_h ? MathHelper.func_76141_d(f * 2.5F) : (this.field_77356_a == 3 && damagesource.func_94541_c() ? MathHelper.func_76141_d(f * 1.5F) : (this.field_77356_a == 4 && damagesource.func_76352_a() ? MathHelper.func_76141_d(f * 1.5F) : 0))));
        }
    }

    @Override
    public String func_77320_a() {
        return "enchantment.protect." + EnchantmentProtection.field_77354_A[this.field_77356_a];
    }

    @Override
    public boolean func_77326_a(Enchantment enchantment) {
        if (enchantment instanceof EnchantmentProtection) {
            EnchantmentProtection enchantmentprotection = (EnchantmentProtection) enchantment;

            return enchantmentprotection.field_77356_a == this.field_77356_a ? false : this.field_77356_a == 2 || enchantmentprotection.field_77356_a == 2;
        } else {
            return super.func_77326_a(enchantment);
        }
    }

    public static int func_92093_a(Entity entity, int i) {
        int j = EnchantmentHelper.func_77511_a(Enchantment.field_77329_d.field_77352_x, entity.func_70035_c());

        if (j > 0) {
            i -= MathHelper.func_76141_d((float) i * (float) j * 0.15F);
        }

        return i;
    }

    public static double func_92092_a(Entity entity, double d0) {
        int i = EnchantmentHelper.func_77511_a(Enchantment.field_77327_f.field_77352_x, entity.func_70035_c());

        if (i > 0) {
            d0 -= (double) MathHelper.func_76128_c(d0 * (double) ((float) i * 0.15F));
        }

        return d0;
    }
}
