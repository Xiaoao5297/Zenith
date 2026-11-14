package net.minecraft.enchantment;

public class EnchantmentWaterWorker extends Enchantment {

    public EnchantmentWaterWorker(int i, int j) {
        super(i, j, EnumEnchantmentType.armor_head);
        this.func_77322_b("waterWorker");
    }

    @Override
    public int func_77321_a(int i) {
        return 1;
    }

    @Override
    public int func_77317_b(int i) {
        return this.func_77321_a(i) + 40;
    }

    @Override
    public int func_77325_b() {
        return 1;
    }
}
