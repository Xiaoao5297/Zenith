package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public class BlockHugeMushroom extends Block {

    private static final String[] field_149793_a = new String[]{"skin_brown", "skin_red"};
    private final int field_149792_b;

    public BlockHugeMushroom(Material material, int i) {
        super(material);
        this.field_149792_b = i;
    }

    @Override
    public int func_149745_a(Random random) {
        int i = random.nextInt(10) - 7;

        if (i < 0) {
            i = 0;
        }

        return i;
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Item.func_150899_d(Block.func_149682_b(Blocks.field_150338_P) + this.field_149792_b);
    }
}
