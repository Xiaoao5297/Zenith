package net.minecraft.block;

import java.util.Random;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenForest;
import net.minecraft.world.gen.feature.WorldGenMegaJungle;
import net.minecraft.world.gen.feature.WorldGenMegaPineTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BlockSapling extends BlockBush implements IGrowable {

    public static final String[] field_149882_a = new String[]{"oak", "spruce", "birch", "jungle", "acacia", "roofed_oak"};
    private static final IIcon[] field_149881_b = new IIcon[BlockSapling.field_149882_a.length];

    protected BlockSapling() {
        float f = 0.4F;

        this.func_149676_a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
        this.func_149647_a(CreativeTabs.field_78031_c);
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        if (!world.field_72995_K) {
            super.func_149674_a(world, i, j, k, random);
            if (world.func_72957_l(i, j + 1, k) >= 9 && random.nextInt(7) == 0) {
                this.func_149879_c(world, i, j, k, random);
            }

        }
    }

    public void func_149879_c(World world, int i, int j, int k, Random random) {
        int l = world.func_72805_g(i, j, k);

        if ((l & 8) == 0) {
            world.func_72921_c(i, j, k, l | 8, 4);
        } else {
            this.func_149878_d(world, i, j, k, random);
        }

    }

    public void func_149878_d(World world, int i, int j, int k, Random random) {
        int l = world.func_72805_g(i, j, k) & 7;
        Object object = random.nextInt(10) == 0 ? new WorldGenBigTree(true) : new WorldGenTrees(true);
        int i1 = 0;
        int j1 = 0;
        boolean flag = false;

        switch (l) {
            case 0:
            default:
                break;
            case 1:
                label78:
                for (i1 = 0; i1 >= -1; --i1) {
                    for (j1 = 0; j1 >= -1; --j1) {
                        if (this.func_149880_a(world, i + i1, j, k + j1, 1) && this.func_149880_a(world, i + i1 + 1, j, k + j1, 1) && this.func_149880_a(world, i + i1, j, k + j1 + 1, 1) && this.func_149880_a(world, i + i1 + 1, j, k + j1 + 1, 1)) {
                            object = new WorldGenMegaPineTree(false, random.nextBoolean());
                            flag = true;
                            break label78;
                        }
                    }
                }

                if (!flag) {
                    j1 = 0;
                    i1 = 0;
                    object = new WorldGenTaiga2(true);
                }
                break;
            case 2:
                object = new WorldGenForest(true, false);
                break;
            case 3:
                label93:
                for (i1 = 0; i1 >= -1; --i1) {
                    for (j1 = 0; j1 >= -1; --j1) {
                        if (this.func_149880_a(world, i + i1, j, k + j1, 3) && this.func_149880_a(world, i + i1 + 1, j, k + j1, 3) && this.func_149880_a(world, i + i1, j, k + j1 + 1, 3) && this.func_149880_a(world, i + i1 + 1, j, k + j1 + 1, 3)) {
                            object = new WorldGenMegaJungle(true, 10, 20, 3, 3);
                            flag = true;
                            break label93;
                        }
                    }
                }

                if (!flag) {
                    j1 = 0;
                    i1 = 0;
                    object = new WorldGenTrees(true, 4 + random.nextInt(7), 3, 3, false);
                }
                break;
            case 4:
                object = new WorldGenSavannaTree(true);
                break;
            case 5:
                label108:
                for (i1 = 0; i1 >= -1; --i1) {
                    for (j1 = 0; j1 >= -1; --j1) {
                        if (this.func_149880_a(world, i + i1, j, k + j1, 5) && this.func_149880_a(world, i + i1 + 1, j, k + j1, 5) && this.func_149880_a(world, i + i1, j, k + j1 + 1, 5) && this.func_149880_a(world, i + i1 + 1, j, k + j1 + 1, 5)) {
                            object = new WorldGenCanopyTree(true);
                            flag = true;
                            break label108;
                        }
                    }
                }

                if (!flag) {
                    return;
                }
        }

        Block block = Blocks.field_150350_a;

        if (flag) {
            world.func_147465_d(i + i1, j, k + j1, block, 0, 4);
            world.func_147465_d(i + i1 + 1, j, k + j1, block, 0, 4);
            world.func_147465_d(i + i1, j, k + j1 + 1, block, 0, 4);
            world.func_147465_d(i + i1 + 1, j, k + j1 + 1, block, 0, 4);
        } else {
            world.func_147465_d(i, j, k, block, 0, 4);
        }

        if (!((WorldGenerator) object).func_76484_a(world, random, i + i1, j, k + j1)) {
            if (flag) {
                world.func_147465_d(i + i1, j, k + j1, this, l, 4);
                world.func_147465_d(i + i1 + 1, j, k + j1, this, l, 4);
                world.func_147465_d(i + i1, j, k + j1 + 1, this, l, 4);
                world.func_147465_d(i + i1 + 1, j, k + j1 + 1, this, l, 4);
            } else {
                world.func_147465_d(i, j, k, this, l, 4);
            }
        }

    }

    public boolean func_149880_a(World world, int i, int j, int k, int l) {
        return world.func_147439_a(i, j, k) == this && (world.func_72805_g(i, j, k) & 7) == l;
    }

    @Override
    public int func_149692_a(int i) {
        return MathHelper.func_76125_a(i & 7, 0, 5);
    }

    @Override
    public boolean func_149851_a(World world, int i, int j, int k, boolean flag) {
        return true;
    }

    @Override
    public boolean func_149852_a(World world, Random random, int i, int j, int k) {
        return (double) world.field_73012_v.nextFloat() < 0.45D;
    }

    @Override
    public void func_149853_b(World world, Random random, int i, int j, int k) {
        this.func_149879_c(world, i, j, k, random);
    }
}
