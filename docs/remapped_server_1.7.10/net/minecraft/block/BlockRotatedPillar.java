package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class BlockRotatedPillar extends Block {

    protected BlockRotatedPillar(Material material) {
        super(material);
    }

    @Override
    public int func_149645_b() {
        return 31;
    }

    @Override
    public int func_149660_a(World world, int i, int j, int k, int l, float f, float f1, float f2, int i1) {
        int j1 = i1 & 3;
        byte b0 = 0;

        switch (l) {
            case 0:
            case 1:
                b0 = 0;
                break;
            case 2:
            case 3:
                b0 = 8;
                break;
            case 4:
            case 5:
                b0 = 4;
        }

        return j1 | b0;
    }

    @Override
    public int func_149692_a(int i) {
        return i & 3;
    }

    public int func_150162_k(int i) {
        return i & 3;
    }

    @Override
    protected ItemStack func_149644_j(int i) {
        return new ItemStack(Item.func_150898_a(this), 1, this.func_150162_k(i));
    }
}
