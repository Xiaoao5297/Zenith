package net.minecraft.enchantment;

import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;

public abstract class Enchantment {

    public static final Enchantment[] field_77331_b = new Enchantment[256];
    public static final Enchantment[] field_92090_c;
    public static final Enchantment field_77332_c = new EnchantmentProtection(0, 10, 0);
    public static final Enchantment field_77329_d = new EnchantmentProtection(1, 5, 1);
    public static final Enchantment field_77330_e = new EnchantmentProtection(2, 5, 2);
    public static final Enchantment field_77327_f = new EnchantmentProtection(3, 2, 3);
    public static final Enchantment field_77328_g = new EnchantmentProtection(4, 5, 4);
    public static final Enchantment field_77340_h = new EnchantmentOxygen(5, 2);
    public static final Enchantment field_77341_i = new EnchantmentWaterWorker(6, 2);
    public static final Enchantment field_92091_k = new EnchantmentThorns(7, 1);
    public static final Enchantment field_77338_j = new EnchantmentDamage(16, 10, 0);
    public static final Enchantment field_77339_k = new EnchantmentDamage(17, 5, 1);
    public static final Enchantment field_77336_l = new EnchantmentDamage(18, 5, 2);
    public static final Enchantment field_77337_m = new EnchantmentKnockback(19, 5);
    public static final Enchantment field_77334_n = new EnchantmentFireAspect(20, 2);
    public static final Enchantment field_77335_o = new EnchantmentLootBonus(21, 2, EnumEnchantmentType.weapon);
    public static final Enchantment field_77349_p = new EnchantmentDigging(32, 10);
    public static final Enchantment field_77348_q = new EnchantmentUntouching(33, 1);
    public static final Enchantment field_77347_r = new EnchantmentDurability(34, 5);
    public static final Enchantment field_77346_s = new EnchantmentLootBonus(35, 2, EnumEnchantmentType.digger);
    public static final Enchantment field_77345_t = new EnchantmentArrowDamage(48, 10);
    public static final Enchantment field_77344_u = new EnchantmentArrowKnockback(49, 2);
    public static final Enchantment field_77343_v = new EnchantmentArrowFire(50, 2);
    public static final Enchantment field_77342_w = new EnchantmentArrowInfinite(51, 1);
    public static final Enchantment field_151370_z = new EnchantmentLootBonus(61, 2, EnumEnchantmentType.fishing_rod);
    public static final Enchantment field_151369_A = new EnchantmentFishingSpeed(62, 2, EnumEnchantmentType.fishing_rod);
    public final int field_77352_x;
    private final int field_77333_a;
    public EnumEnchantmentType field_77351_y;
    protected String field_77350_z;

    protected Enchantment(int i, int j, EnumEnchantmentType enumenchantmenttype) {
        this.field_77352_x = i;
        this.field_77333_a = j;
        this.field_77351_y = enumenchantmenttype;
        if (Enchantment.field_77331_b[i] != null) {
            throw new IllegalArgumentException("Duplicate enchantment id!");
        } else {
            Enchantment.field_77331_b[i] = this;
        }
    }

    public int func_77324_c() {
        return this.field_77333_a;
    }

    public int func_77319_d() {
        return 1;
    }

    public int func_77325_b() {
        return 1;
    }

    public int func_77321_a(int i) {
        return 1 + i * 10;
    }

    public int func_77317_b(int i) {
        return this.func_77321_a(i) + 5;
    }

    public int func_77318_a(int i, DamageSource damagesource) {
        return 0;
    }

    public float func_152376_a(int i, EnumCreatureAttribute enumcreatureattribute) {
        return 0.0F;
    }

    public boolean func_77326_a(Enchantment enchantment) {
        return this != enchantment;
    }

    public Enchantment func_77322_b(String s) {
        this.field_77350_z = s;
        return this;
    }

    public String func_77320_a() {
        return "enchantment." + this.field_77350_z;
    }

    public String func_77316_c(int i) {
        String s = StatCollector.func_74838_a(this.func_77320_a());

        return s + " " + StatCollector.func_74838_a("enchantment.level." + i);
    }

    public boolean func_92089_a(ItemStack itemstack) {
        return this.field_77351_y.func_77557_a(itemstack.func_77973_b());
    }

    public void func_151368_a(EntityLivingBase entitylivingbase, Entity entity, int i) {}

    public void func_151367_b(EntityLivingBase entitylivingbase, Entity entity, int i) {}

    static {
        ArrayList arraylist = new ArrayList();
        Enchantment[] aenchantment = Enchantment.field_77331_b;
        int i = aenchantment.length;

        for (int j = 0; j < i; ++j) {
            Enchantment enchantment = aenchantment[j];

            if (enchantment != null) {
                arraylist.add(enchantment);
            }
        }

        field_92090_c = (Enchantment[]) arraylist.toArray(new Enchantment[0]);
    }
}
