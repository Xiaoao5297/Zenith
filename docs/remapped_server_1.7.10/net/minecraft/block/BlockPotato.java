package net.minecraft.block;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockPotato extends BlockCrops {

    public BlockPotato() {}

    @Override
    protected Item func_149866_i() {
        return Items.field_151174_bG;
    }

    @Override
    protected Item func_149865_P() {
        return Items.field_151174_bG;
    }

    @Override
    public void func_149690_a(World world, int i, int j, int k, int l, float f, int i1) {
        super.func_149690_a(world, i, j, k, l, f, i1);
        if (!world.field_72995_K) {
            if (l >= 7 && world.field_73012_v.nextInt(50) == 0) {
                this.func_149642_a(world, i, j, k, new ItemStack(Items.field_151170_bI));
            }

        }
    }
}
