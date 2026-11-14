package net.minecraft.enchantment;

public class EnchantmentFishingSpeed extends Enchantment {

    protected EnchantmentFishingSpeed(int i, int j, EnumEnchantmentType enumenchantmenttype) {
        super(i, j, enumenchantmenttype);
        this.func_77322_b("fishingSpeed");
    }

    @Override
    public int func_77321_a(int i) {
        return 15 + (i - 1) * 9;
    }

    @Override
    public int func_77317_b(int i) {
        return super.func_77321_a(i) + 50;
    }

    @Override
    public int func_77325_b() {
        return 3;
    }
}
