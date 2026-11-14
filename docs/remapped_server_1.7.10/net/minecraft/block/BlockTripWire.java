package net.minecraft.block;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTripWire extends Block {

    public BlockTripWire() {
        super(Material.field_151594_q);
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.15625F, 1.0F);
        this.func_149675_a(true);
    }

    @Override
    public int func_149738_a(World world) {
        return 10;
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        return null;
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
        return 30;
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Items.field_151007_F;
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        int l = world.func_72805_g(i, j, k);
        boolean flag = (l & 2) == 2;
        boolean flag1 = !World.func_147466_a(world, i, j - 1, k);

        if (flag != flag1) {
            this.func_149697_b(world, i, j, k, l, 0);
            world.func_147468_f(i, j, k);
        }

    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        int l = iblockaccess.func_72805_g(i, j, k);
        boolean flag = (l & 4) == 4;
        boolean flag1 = (l & 2) == 2;

        if (!flag1) {
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.09375F, 1.0F);
        } else if (!flag) {
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        } else {
            this.func_149676_a(0.0F, 0.0625F, 0.0F, 1.0F, 0.15625F, 1.0F);
        }

    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {
        int l = World.func_147466_a(world, i, j - 1, k) ? 0 : 2;

        world.func_72921_c(i, j, k, l, 3);
        this.func_150138_a(world, i, j, k, l);
    }

    @Override
    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {
        this.func_150138_a(world, i, j, k, l | 1);
    }

    @Override
    public void func_149681_a(World world, int i, int j, int k, int l, EntityPlayer entityplayer) {
        if (!world.field_72995_K) {
            if (entityplayer.func_71045_bC() != null && entityplayer.func_71045_bC().func_77973_b() == Items.field_151097_aZ) {
                world.func_72921_c(i, j, k, l | 8, 4);
            }

        }
    }

    private void func_150138_a(World world, int i, int j, int k, int l) {
        int i1 = 0;

        while (i1 < 2) {
            int j1 = 1;

            while (true) {
                if (j1 < 42) {
                    int k1 = i + Direction.field_71583_a[i1] * j1;
                    int l1 = k + Direction.field_71581_b[i1] * j1;
                    Block block = world.func_147439_a(k1, j, l1);

                    if (block == Blocks.field_150479_bC) {
                        int i2 = world.func_72805_g(k1, j, l1) & 3;

                        if (i2 == Direction.field_71580_e[i1]) {
                            Blocks.field_150479_bC.func_150136_a(world, k1, j, l1, false, world.func_72805_g(k1, j, l1), true, j1, l);
                        }
                    } else if (block == Blocks.field_150473_bD) {
                        ++j1;
                        continue;
                    }
                }

                ++i1;
                break;
            }
        }

    }

    @Override
    public void func_149670_a(World world, int i, int j, int k, Entity entity) {
        if (!world.field_72995_K) {
            if ((world.func_72805_g(i, j, k) & 1) != 1) {
                this.func_150140_e(world, i, j, k);
            }
        }
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        if (!world.field_72995_K) {
            if ((world.func_72805_g(i, j, k) & 1) == 1) {
                this.func_150140_e(world, i, j, k);
            }
        }
    }

    private void func_150140_e(World world, int i, int j, int k) {
        int l = world.func_72805_g(i, j, k);
        boolean flag = (l & 1) == 1;
        boolean flag1 = false;
        List list = world.func_72839_b((Entity) null, AxisAlignedBB.func_72330_a((double) i + this.field_149759_B, (double) j + this.field_149760_C, (double) k + this.field_149754_D, (double) i + this.field_149755_E, (double) j + this.field_149756_F, (double) k + this.field_149757_G));

        if (!list.isEmpty()) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                if (!entity.func_145773_az()) {
                    flag1 = true;
                    break;
                }
            }
        }

        if (flag1 && !flag) {
            l |= 1;
        }

        if (!flag1 && flag) {
            l &= -2;
        }

        if (flag1 != flag) {
            world.func_72921_c(i, j, k, l, 3);
            this.func_150138_a(world, i, j, k, l);
        }

        if (flag1) {
            world.func_147464_a(i, j, k, this, this.func_149738_a(world));
        }

    }
}
