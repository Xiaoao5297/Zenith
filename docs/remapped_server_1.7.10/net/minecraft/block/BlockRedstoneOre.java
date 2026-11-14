package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockRedstoneOre extends Block {

    private boolean field_150187_a;

    public BlockRedstoneOre(boolean flag) {
        super(Material.field_151576_e);
        if (flag) {
            this.func_149675_a(true);
        }

        this.field_150187_a = flag;
    }

    @Override
    public int func_149738_a(World world) {
        return 30;
    }

    @Override
    public void func_149699_a(World world, int i, int j, int k, EntityPlayer entityplayer) {
        this.func_150185_e(world, i, j, k);
        super.func_149699_a(world, i, j, k, entityplayer);
    }

    @Override
    public void func_149724_b(World world, int i, int j, int k, Entity entity) {
        this.func_150185_e(world, i, j, k);
        super.func_149724_b(world, i, j, k, entity);
    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        this.func_150185_e(world, i, j, k);
        return super.func_149727_a(world, i, j, k, entityplayer, l, f, f1, f2);
    }

    private void func_150185_e(World world, int i, int j, int k) {
        this.func_150186_m(world, i, j, k);
        if (this == Blocks.field_150450_ax) {
            world.func_147449_b(i, j, k, Blocks.field_150439_ay);
        }

    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        if (this == Blocks.field_150439_ay) {
            world.func_147449_b(i, j, k, Blocks.field_150450_ax);
        }

    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Items.field_151137_ax;
    }

    @Override
    public int func_149679_a(int i, Random random) {
        return this.func_149745_a(random) + random.nextInt(i + 1);
    }

    @Override
    public int func_149745_a(Random random) {
        return 4 + random.nextInt(2);
    }

    @Override
    public void func_149690_a(World world, int i, int j, int k, int l, float f, int i1) {
        super.func_149690_a(world, i, j, k, l, f, i1);
        if (this.func_149650_a(l, world.field_73012_v, i1) != Item.func_150898_a(this)) {
            int j1 = 1 + world.field_73012_v.nextInt(5);

            this.func_149657_c(world, i, j, k, j1);
        }

    }

    private void func_150186_m(World world, int i, int j, int k) {
        Random random = world.field_73012_v;
        double d0 = 0.0625D;

        for (int l = 0; l < 6; ++l) {
            double d1 = (double) ((float) i + random.nextFloat());
            double d2 = (double) ((float) j + random.nextFloat());
            double d3 = (double) ((float) k + random.nextFloat());

            if (l == 0 && !world.func_147439_a(i, j + 1, k).func_149662_c()) {
                d2 = (double) (j + 1) + d0;
            }

            if (l == 1 && !world.func_147439_a(i, j - 1, k).func_149662_c()) {
                d2 = (double) (j + 0) - d0;
            }

            if (l == 2 && !world.func_147439_a(i, j, k + 1).func_149662_c()) {
                d3 = (double) (k + 1) + d0;
            }

            if (l == 3 && !world.func_147439_a(i, j, k - 1).func_149662_c()) {
                d3 = (double) (k + 0) - d0;
            }

            if (l == 4 && !world.func_147439_a(i + 1, j, k).func_149662_c()) {
                d1 = (double) (i + 1) + d0;
            }

            if (l == 5 && !world.func_147439_a(i - 1, j, k).func_149662_c()) {
                d1 = (double) (i + 0) - d0;
            }

            if (d1 < (double) i || d1 > (double) (i + 1) || d2 < 0.0D || d2 > (double) (j + 1) || d3 < (double) k || d3 > (double) (k + 1)) {
                world.func_72869_a("reddust", d1, d2, d3, 0.0D, 0.0D, 0.0D);
            }
        }

    }

    @Override
    protected ItemStack func_149644_j(int i) {
        return new ItemStack(Blocks.field_150450_ax);
    }
}
