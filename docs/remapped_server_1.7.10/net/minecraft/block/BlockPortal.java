package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPortal extends BlockBreakable {

    public static final int[][] field_150001_a = new int[][]{new int[0], {3, 1}, {2, 0}};

    public BlockPortal() {
        super("portal", Material.field_151567_E, false);
        this.func_149675_a(true);
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        super.func_149674_a(world, i, j, k, random);
        if (world.field_73011_w.func_76569_d() && world.func_82736_K().func_82766_b("doMobSpawning") && random.nextInt(2000) < world.field_73013_u.func_151525_a()) {
            int l;

            for (l = j; !World.func_147466_a(world, i, l, k) && l > 0; --l) {
                ;
            }

            if (l > 0 && !world.func_147439_a(i, l + 1, k).func_149721_r()) {
                Entity entity = ItemMonsterPlacer.func_77840_a(world, 57, (double) i + 0.5D, (double) l + 1.1D, (double) k + 0.5D);

                if (entity != null) {
                    entity.field_71088_bW = entity.func_82147_ab();
                }
            }
        }

    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        return null;
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        int l = func_149999_b(iblockaccess.func_72805_g(i, j, k));

        if (l == 0) {
            if (iblockaccess.func_147439_a(i - 1, j, k) != this && iblockaccess.func_147439_a(i + 1, j, k) != this) {
                l = 2;
            } else {
                l = 1;
            }

            if (iblockaccess instanceof World && !((World) iblockaccess).field_72995_K) {
                ((World) iblockaccess).func_72921_c(i, j, k, l, 2);
            }
        }

        float f = 0.125F;
        float f1 = 0.125F;

        if (l == 1) {
            f = 0.5F;
        }

        if (l == 2) {
            f1 = 0.5F;
        }

        this.func_149676_a(0.5F - f, 0.0F, 0.5F - f1, 0.5F + f, 1.0F, 0.5F + f1);
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    public boolean func_150000_e(World world, int i, int j, int k) {
        BlockPortal$Size blockportal$size = new BlockPortal$Size(world, i, j, k, 1);
        BlockPortal$Size blockportal$size1 = new BlockPortal$Size(world, i, j, k, 2);

        if (blockportal$size.func_150860_b() && BlockPortal$Size.access$000(blockportal$size) == 0) {
            blockportal$size.func_150859_c();
            return true;
        } else if (blockportal$size1.func_150860_b() && BlockPortal$Size.access$000(blockportal$size1) == 0) {
            blockportal$size1.func_150859_c();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        int l = func_149999_b(world.func_72805_g(i, j, k));
        BlockPortal$Size blockportal$size = new BlockPortal$Size(world, i, j, k, 1);
        BlockPortal$Size blockportal$size1 = new BlockPortal$Size(world, i, j, k, 2);

        if (l == 1 && (!blockportal$size.func_150860_b() || BlockPortal$Size.access$000(blockportal$size) < BlockPortal$Size.access$100(blockportal$size) * BlockPortal$Size.access$200(blockportal$size))) {
            world.func_147449_b(i, j, k, Blocks.field_150350_a);
        } else if (l == 2 && (!blockportal$size1.func_150860_b() || BlockPortal$Size.access$000(blockportal$size1) < BlockPortal$Size.access$100(blockportal$size1) * BlockPortal$Size.access$200(blockportal$size1))) {
            world.func_147449_b(i, j, k, Blocks.field_150350_a);
        } else if (l == 0 && !blockportal$size.func_150860_b() && !blockportal$size1.func_150860_b()) {
            world.func_147449_b(i, j, k, Blocks.field_150350_a);
        }

    }

    @Override
    public int func_149745_a(Random random) {
        return 0;
    }

    @Override
    public void func_149670_a(World world, int i, int j, int k, Entity entity) {
        if (entity.field_70154_o == null && entity.field_70153_n == null) {
            entity.func_70063_aa();
        }

    }

    public static int func_149999_b(int i) {
        return i & 3;
    }
}
