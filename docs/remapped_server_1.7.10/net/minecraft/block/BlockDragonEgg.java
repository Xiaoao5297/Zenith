package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockDragonEgg extends Block {

    public BlockDragonEgg() {
        super(Material.field_151566_D);
        this.func_149676_a(0.0625F, 0.0F, 0.0625F, 0.9375F, 1.0F, 0.9375F);
    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {
        world.func_147464_a(i, j, k, this, this.func_149738_a(world));
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        world.func_147464_a(i, j, k, this, this.func_149738_a(world));
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        this.func_150018_e(world, i, j, k);
    }

    private void func_150018_e(World world, int i, int j, int k) {
        if (BlockFalling.func_149831_e(world, i, j - 1, k) && j >= 0) {
            byte b0 = 32;

            if (!BlockFalling.field_149832_M && world.func_72904_c(i - b0, j - b0, k - b0, i + b0, j + b0, k + b0)) {
                EntityFallingBlock entityfallingblock = new EntityFallingBlock(world, (double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), this);

                world.func_72838_d(entityfallingblock);
            } else {
                world.func_147468_f(i, j, k);

                while (BlockFalling.func_149831_e(world, i, j - 1, k) && j > 0) {
                    --j;
                }

                if (j > 0) {
                    world.func_147465_d(i, j, k, this, 0, 2);
                }
            }
        }

    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        this.func_150019_m(world, i, j, k);
        return true;
    }

    @Override
    public void func_149699_a(World world, int i, int j, int k, EntityPlayer entityplayer) {
        this.func_150019_m(world, i, j, k);
    }

    private void func_150019_m(World world, int i, int j, int k) {
        if (world.func_147439_a(i, j, k) == this) {
            for (int l = 0; l < 1000; ++l) {
                int i1 = i + world.field_73012_v.nextInt(16) - world.field_73012_v.nextInt(16);
                int j1 = j + world.field_73012_v.nextInt(8) - world.field_73012_v.nextInt(8);
                int k1 = k + world.field_73012_v.nextInt(16) - world.field_73012_v.nextInt(16);

                if (world.func_147439_a(i1, j1, k1).field_149764_J == Material.field_151579_a) {
                    if (!world.field_72995_K) {
                        world.func_147465_d(i1, j1, k1, this, world.func_72805_g(i, j, k), 2);
                        world.func_147468_f(i, j, k);
                    } else {
                        short short0 = 128;

                        for (int l1 = 0; l1 < short0; ++l1) {
                            double d0 = world.field_73012_v.nextDouble();
                            float f = (world.field_73012_v.nextFloat() - 0.5F) * 0.2F;
                            float f1 = (world.field_73012_v.nextFloat() - 0.5F) * 0.2F;
                            float f2 = (world.field_73012_v.nextFloat() - 0.5F) * 0.2F;
                            double d1 = (double) i1 + (double) (i - i1) * d0 + (world.field_73012_v.nextDouble() - 0.5D) * 1.0D + 0.5D;
                            double d2 = (double) j1 + (double) (j - j1) * d0 + world.field_73012_v.nextDouble() * 1.0D - 0.5D;
                            double d3 = (double) k1 + (double) (k - k1) * d0 + (world.field_73012_v.nextDouble() - 0.5D) * 1.0D + 0.5D;

                            world.func_72869_a("portal", d1, d2, d3, (double) f, (double) f1, (double) f2);
                        }
                    }

                    return;
                }
            }

        }
    }

    @Override
    public int func_149738_a(World world) {
        return 5;
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    @Override
    public int func_149645_b() {
        return 27;
    }
}
