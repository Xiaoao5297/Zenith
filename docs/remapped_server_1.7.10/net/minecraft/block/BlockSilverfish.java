package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class BlockSilverfish extends Block {

    public static final String[] field_150198_a = new String[]{"stone", "cobble", "brick", "mossybrick", "crackedbrick", "chiseledbrick"};

    public BlockSilverfish() {
        super(Material.field_151571_B);
        this.func_149711_c(0.0F);
        this.func_149647_a(CreativeTabs.field_78031_c);
    }

    @Override
    public void func_149664_b(World world, int i, int j, int k, int l) {
        if (!world.field_72995_K) {
            EntitySilverfish entitysilverfish = new EntitySilverfish(world);

            entitysilverfish.func_70012_b((double) i + 0.5D, (double) j, (double) k + 0.5D, 0.0F, 0.0F);
            world.func_72838_d(entitysilverfish);
            entitysilverfish.func_70656_aK();
        }

        super.func_149664_b(world, i, j, k, l);
    }

    @Override
    public int func_149745_a(Random random) {
        return 0;
    }

    public static boolean func_150196_a(Block block) {
        return block == Blocks.field_150348_b || block == Blocks.field_150347_e || block == Blocks.field_150417_aV;
    }

    public static int func_150195_a(Block block, int i) {
        if (i == 0) {
            if (block == Blocks.field_150347_e) {
                return 1;
            }

            if (block == Blocks.field_150417_aV) {
                return 2;
            }
        } else if (block == Blocks.field_150417_aV) {
            switch (i) {
                case 1:
                    return 3;
                case 2:
                    return 4;
                case 3:
                    return 5;
            }
        }

        return 0;
    }

    public static ImmutablePair func_150197_b(int i) {
        switch (i) {
            case 1:
                return new ImmutablePair<>(Blocks.field_150347_e, 0);
            case 2:
                return new ImmutablePair<>(Blocks.field_150417_aV, 0);
            case 3:
                return new ImmutablePair<>(Blocks.field_150417_aV, 1);
            case 4:
                return new ImmutablePair<>(Blocks.field_150417_aV, 2);
            case 5:
                return new ImmutablePair<>(Blocks.field_150417_aV, 3);
            default:
                return new ImmutablePair<>(Blocks.field_150348_b, 0);
        }
    }

    @Override
    protected ItemStack func_149644_j(int i) {
        switch (i) {
            case 1:
                return new ItemStack(Blocks.field_150347_e);
            case 2:
                return new ItemStack(Blocks.field_150417_aV);
            case 3:
                return new ItemStack(Blocks.field_150417_aV, 1, 1);
            case 4:
                return new ItemStack(Blocks.field_150417_aV, 1, 2);
            case 5:
                return new ItemStack(Blocks.field_150417_aV, 1, 3);
            default:
                return new ItemStack(Blocks.field_150348_b);
        }
    }

    @Override
    public void func_149690_a(World world, int i, int j, int k, int l, float f, int i1) {
        if (!world.field_72995_K) {
            EntitySilverfish entitysilverfish = new EntitySilverfish(world);

            entitysilverfish.func_70012_b((double) i + 0.5D, (double) j, (double) k + 0.5D, 0.0F, 0.0F);
            world.func_72838_d(entitysilverfish);
            entitysilverfish.func_70656_aK();
        }

    }

    @Override
    public int func_149643_k(World world, int i, int j, int k) {
        return world.func_72805_g(i, j, k);
    }
}
