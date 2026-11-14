package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;

public class BlockGlowstone extends Block {

    public BlockGlowstone(Material material) {
        super(material);
        this.func_149647_a(CreativeTabs.field_78030_b);
    }

    @Override
    public int func_149679_a(int i, Random random) {
        return MathHelper.func_76125_a(this.func_149745_a(random) + random.nextInt(i + 1), 1, 4);
    }

    @Override
    public int func_149745_a(Random random) {
        return 2 + random.nextInt(3);
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Items.field_151114_aO;
    }

    @Override
    public MapColor func_149728_f(int i) {
        return MapColor.field_151658_d;
    }
}
