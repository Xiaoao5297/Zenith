package net.minecraft.block;

import java.util.Random;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockGravel extends BlockFalling {

    public BlockGravel() {}

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        if (j > 3) {
            j = 3;
        }

        return random.nextInt(10 - j * 3) == 0 ? Items.field_151145_ak : Item.func_150898_a(this);
    }
}
