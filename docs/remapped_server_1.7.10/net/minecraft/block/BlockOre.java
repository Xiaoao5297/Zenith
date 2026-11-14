package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockOre extends Block {

    public BlockOre() {
        super(Material.field_151576_e);
        this.func_149647_a(CreativeTabs.field_78030_b);
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return this == Blocks.field_150365_q ? Items.field_151044_h : (this == Blocks.field_150482_ag ? Items.field_151045_i : (this == Blocks.field_150369_x ? Items.field_151100_aR : (this == Blocks.field_150412_bA ? Items.field_151166_bC : (this == Blocks.field_150449_bY ? Items.field_151128_bU : Item.func_150898_a(this)))));
    }

    @Override
    public int func_149745_a(Random random) {
        return this == Blocks.field_150369_x ? 4 + random.nextInt(5) : 1;
    }

    @Override
    public int func_149679_a(int i, Random random) {
        if (i > 0 && Item.func_150898_a(this) != this.func_149650_a(0, random, i)) {
            int j = random.nextInt(i + 2) - 1;

            if (j < 0) {
                j = 0;
            }

            return this.func_149745_a(random) * (j + 1);
        } else {
            return this.func_149745_a(random);
        }
    }

    @Override
    public void func_149690_a(World world, int i, int j, int k, int l, float f, int i1) {
        super.func_149690_a(world, i, j, k, l, f, i1);
        if (this.func_149650_a(l, world.field_73012_v, i1) != Item.func_150898_a(this)) {
            int j1 = 0;

            if (this == Blocks.field_150365_q) {
                j1 = MathHelper.func_76136_a(world.field_73012_v, 0, 2);
            } else if (this == Blocks.field_150482_ag) {
                j1 = MathHelper.func_76136_a(world.field_73012_v, 3, 7);
            } else if (this == Blocks.field_150412_bA) {
                j1 = MathHelper.func_76136_a(world.field_73012_v, 3, 7);
            } else if (this == Blocks.field_150369_x) {
                j1 = MathHelper.func_76136_a(world.field_73012_v, 2, 5);
            } else if (this == Blocks.field_150449_bY) {
                j1 = MathHelper.func_76136_a(world.field_73012_v, 2, 5);
            }

            this.func_149657_c(world, i, j, k, j1);
        }

    }

    @Override
    public int func_149692_a(int i) {
        return this == Blocks.field_150369_x ? 4 : 0;
    }
}
