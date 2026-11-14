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

public class BlockTallGrass extends BlockBush implements IGrowable {

    private static final String[] field_149871_a = new String[]{"deadbush", "tallgrass", "fern"};

    protected BlockTallGrass() {
        super(Material.field_151582_l);
        float f = 0.4F;

        this.func_149676_a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.8F, 0.5F + f);
    }

    @Override
    public boolean func_149718_j(World world, int i, int j, int k) {
        return this.func_149854_a(world.func_147439_a(i, j - 1, k));
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return random.nextInt(8) == 0 ? Items.field_151014_N : null;
    }

    @Override
    public int func_149679_a(int i, Random random) {
        return 1 + random.nextInt(i * 2 + 1);
    }

    @Override
    public void func_149636_a(World world, EntityPlayer entityplayer, int i, int j, int k, int l) {
        if (!world.field_72995_K && entityplayer.func_71045_bC() != null && entityplayer.func_71045_bC().func_77973_b() == Items.field_151097_aZ) {
            entityplayer.func_71064_a(StatList.field_75934_C[Block.func_149682_b(this)], 1);
            this.func_149642_a(world, i, j, k, new ItemStack(Blocks.field_150329_H, 1, l));
        } else {
            super.func_149636_a(world, entityplayer, i, j, k, l);
        }

    }

    @Override
    public int func_149643_k(World world, int i, int j, int k) {
        return world.func_72805_g(i, j, k);
    }

    @Override
    public boolean func_149851_a(World world, int i, int j, int k, boolean flag) {
        int l = world.func_72805_g(i, j, k);

        return l != 0;
    }

    @Override
    public boolean func_149852_a(World world, Random random, int i, int j, int k) {
        return true;
    }

    @Override
    public void func_149853_b(World world, Random random, int i, int j, int k) {
        int l = world.func_72805_g(i, j, k);
        byte b0 = 2;

        if (l == 2) {
            b0 = 3;
        }

        if (Blocks.field_150398_cm.func_149742_c(world, i, j, k)) {
            Blocks.field_150398_cm.func_149889_c(world, i, j, k, b0, 2);
        }

    }
}
