package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockQuartz extends Block {

    public static final String[] field_150191_a = new String[]{"default", "chiseled", "lines"};
    private static final String[] field_150189_b = new String[]{"side", "chiseled", "lines", null, null};

    public BlockQuartz() {
        super(Material.field_151576_e);
        this.func_149647_a(CreativeTabs.field_78030_b);
    }

    @Override
    public int func_149660_a(World world, int i, int j, int k, int l, float f, float f1, float f2, int i1) {
        if (i1 == 2) {
            switch (l) {
                case 0:
                case 1:
                    i1 = 2;
                    break;
                case 2:
                case 3:
                    i1 = 4;
                    break;
                case 4:
                case 5:
                    i1 = 3;
            }
        }

        return i1;
    }

    @Override
    public int func_149692_a(int i) {
        return i != 3 && i != 4 ? i : 2;
    }

    @Override
    protected ItemStack func_149644_j(int i) {
        return i != 3 && i != 4 ? super.func_149644_j(i) : new ItemStack(Item.func_150898_a(this), 1, 2);
    }

    @Override
    public int func_149645_b() {
        return 39;
    }

    @Override
    public MapColor func_149728_f(int i) {
        return MapColor.field_151677_p;
    }
}
