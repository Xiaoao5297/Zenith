package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class BlockDeadBush extends BlockBush {

    protected BlockDeadBush() {
        super(Material.field_151582_l);
        float f = 0.4F;

        this.func_149676_a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.8F, 0.5F + f);
    }

    @Override
    protected boolean func_149854_a(Block block) {
        return block == Blocks.field_150354_m || block == Blocks.field_150405_ch || block == Blocks.field_150406_ce || block == Blocks.field_150346_d;
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return null;
    }

    @Override
    public void func_149636_a(World world, EntityPlayer entityplayer, int i, int j, int k, int l) {
        if (!world.field_72995_K && entityplayer.func_71045_bC() != null && entityplayer.func_71045_bC().func_77973_b() == Items.field_151097_aZ) {
            entityplayer.func_71064_a(StatList.field_75934_C[Block.func_149682_b(this)], 1);
            this.func_149642_a(world, i, j, k, new ItemStack(Blocks.field_150330_I, 1, l));
        } else {
            super.func_149636_a(world, entityplayer, i, j, k, l);
        }

    }
}
