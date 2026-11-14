package net.minecraft.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneTorch extends BlockTorch {

    private boolean field_150113_a;
    private static Map field_150112_b = new HashMap();

    private boolean func_150111_a(World world, int i, int j, int k, boolean flag) {
        if (!BlockRedstoneTorch.field_150112_b.containsKey(world)) {
            BlockRedstoneTorch.field_150112_b.put(world, new ArrayList());
        }

        List list = (List) BlockRedstoneTorch.field_150112_b.get(world);

        if (flag) {
            list.add(new BlockRedstoneTorch$Toggle(i, j, k, world.func_82737_E()));
        }

        int l = 0;

        for (int i1 = 0; i1 < list.size(); ++i1) {
            BlockRedstoneTorch$Toggle blockredstonetorch$toggle = (BlockRedstoneTorch$Toggle) list.get(i1);

            if (blockredstonetorch$toggle.field_150847_a == i && blockredstonetorch$toggle.field_150845_b == j && blockredstonetorch$toggle.field_150846_c == k) {
                ++l;
                if (l >= 8) {
                    return true;
                }
            }
        }

        return false;
    }

    protected BlockRedstoneTorch(boolean flag) {
        this.field_150113_a = flag;
        this.func_149675_a(true);
        this.func_149647_a((CreativeTabs) null);
    }

    @Override
    public int func_149738_a(World world) {
        return 2;
    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {
        if (world.func_72805_g(i, j, k) == 0) {
            super.func_149726_b(world, i, j, k);
        }

        if (this.field_150113_a) {
            world.func_147459_d(i, j - 1, k, this);
            world.func_147459_d(i, j + 1, k, this);
            world.func_147459_d(i - 1, j, k, this);
            world.func_147459_d(i + 1, j, k, this);
            world.func_147459_d(i, j, k - 1, this);
            world.func_147459_d(i, j, k + 1, this);
        }

    }

    @Override
    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {
        if (this.field_150113_a) {
            world.func_147459_d(i, j - 1, k, this);
            world.func_147459_d(i, j + 1, k, this);
            world.func_147459_d(i - 1, j, k, this);
            world.func_147459_d(i + 1, j, k, this);
            world.func_147459_d(i, j, k - 1, this);
            world.func_147459_d(i, j, k + 1, this);
        }

    }

    @Override
    public int func_149709_b(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        if (!this.field_150113_a) {
            return 0;
        } else {
            int i1 = iblockaccess.func_72805_g(i, j, k);

            return i1 == 5 && l == 1 ? 0 : (i1 == 3 && l == 3 ? 0 : (i1 == 4 && l == 2 ? 0 : (i1 == 1 && l == 5 ? 0 : (i1 == 2 && l == 4 ? 0 : 15))));
        }
    }

    private boolean func_150110_m(World world, int i, int j, int k) {
        int l = world.func_72805_g(i, j, k);

        return l == 5 && world.func_94574_k(i, j - 1, k, 0) ? true : (l == 3 && world.func_94574_k(i, j, k - 1, 2) ? true : (l == 4 && world.func_94574_k(i, j, k + 1, 3) ? true : (l == 1 && world.func_94574_k(i - 1, j, k, 4) ? true : l == 2 && world.func_94574_k(i + 1, j, k, 5))));
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        boolean flag = this.func_150110_m(world, i, j, k);
        List list = (List) BlockRedstoneTorch.field_150112_b.get(world);

        while (list != null && !list.isEmpty() && world.func_82737_E() - ((BlockRedstoneTorch$Toggle) list.get(0)).field_150844_d > 60L) {
            list.remove(0);
        }

        if (this.field_150113_a) {
            if (flag) {
                world.func_147465_d(i, j, k, Blocks.field_150437_az, world.func_72805_g(i, j, k), 3);
                if (this.func_150111_a(world, i, j, k, true)) {
                    world.func_72908_a((double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), "random.fizz", 0.5F, 2.6F + (world.field_73012_v.nextFloat() - world.field_73012_v.nextFloat()) * 0.8F);

                    for (int l = 0; l < 5; ++l) {
                        double d0 = (double) i + random.nextDouble() * 0.6D + 0.2D;
                        double d1 = (double) j + random.nextDouble() * 0.6D + 0.2D;
                        double d2 = (double) k + random.nextDouble() * 0.6D + 0.2D;

                        world.func_72869_a("smoke", d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    }
                }
            }
        } else if (!flag && !this.func_150111_a(world, i, j, k, false)) {
            world.func_147465_d(i, j, k, Blocks.field_150429_aA, world.func_72805_g(i, j, k), 3);
        }

    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        if (!this.func_150108_b(world, i, j, k, block)) {
            boolean flag = this.func_150110_m(world, i, j, k);

            if (this.field_150113_a && flag || !this.field_150113_a && !flag) {
                world.func_147464_a(i, j, k, this, this.func_149738_a(world));
            }

        }
    }

    @Override
    public int func_149748_c(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return l == 0 ? this.func_149709_b(iblockaccess, i, j, k, l) : 0;
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Item.func_150898_a(Blocks.field_150429_aA);
    }

    @Override
    public boolean func_149744_f() {
        return true;
    }

    @Override
    public boolean func_149667_c(Block block) {
        return block == Blocks.field_150437_az || block == Blocks.field_150429_aA;
    }
}
