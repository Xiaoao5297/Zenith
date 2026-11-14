package net.minecraft.enchantment;

public class EnchantmentLootBonus extends Enchantment {

    protected EnchantmentLootBonus(int i, int j, EnumEnchantmentType enumenchantmenttype) {
        super(i, j, enumenchantmenttype);
        if (enumenchantmenttype == EnumEnchantmentType.digger) {
            this.func_77322_b("lootBonusDigger");
        } else if (enumenchantmenttype == EnumEnchantmentType.fishing_rod) {
            this.func_77322_b("lootBonusFishing");
        } else {
            this.func_77322_b("lootBonus");
        }

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

    @Override
    public boolean func_77326_a(Enchantment enchantment) {
        return super.func_77326_a(enchantment) && enchantment.field_77352_x != EnchantmentLootBonus.field_77348_q.field_77352_x;
    }
}
