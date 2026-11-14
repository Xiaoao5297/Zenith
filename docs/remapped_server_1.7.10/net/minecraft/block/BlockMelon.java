package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockMelon extends Block {

    protected BlockMelon() {
        super(Material.field_151572_C);
        this.func_149647_a(CreativeTabs.field_78030_b);
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Items.field_151127_ba;
    }

    @Override
    public int func_149745_a(Random random) {
        return 3 + random.nextInt(5);
    }

    @Override
    public int func_149679_a(int i, Random random) {
        int j = this.func_149745_a(random) + random.nextInt(1 + i);

        if (j > 9) {
            j = 9;
        }

        return j;
    }
}
