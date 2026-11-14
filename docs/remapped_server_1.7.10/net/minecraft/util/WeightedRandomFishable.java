package net.minecraft.util;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public class WeightedRandomFishable extends WeightedRandom$Item {

    private final ItemStack field_150711_b;
    private float field_150712_c;
    private boolean field_150710_d;

    public WeightedRandomFishable(ItemStack itemstack, int i) {
        super(i);
        this.field_150711_b = itemstack;
    }

    public ItemStack func_150708_a(Random random) {
        ItemStack itemstack = this.field_150711_b.func_77946_l();

        if (this.field_150712_c > 0.0F) {
            int i = (int) (this.field_150712_c * (float) this.field_150711_b.func_77958_k());
            int j = itemstack.func_77958_k() - random.nextInt(random.nextInt(i) + 1);

            if (j > i) {
                j = i;
            }

            if (j < 1) {
                j = 1;
            }

            itemstack.func_77964_b(j);
        }

        if (this.field_150710_d) {
            EnchantmentHelper.func_77504_a(random, itemstack, 30);
        }

        return itemstack;
    }

    public WeightedRandomFishable func_150709_a(float f) {
        this.field_150712_c = f;
        return this;
    }

    public WeightedRandomFishable func_150707_a() {
        this.field_150710_d = true;
        return this;
    }
}
