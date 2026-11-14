package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockVine extends Block {

    public BlockVine() {
        super(Material.field_151582_l);
        this.func_149675_a(true);
        this.func_149647_a(CreativeTabs.field_78031_c);
    }

    @Override
    public void func_149683_g() {
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public int func_149645_b() {
        return 20;
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
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        float f = 0.0625F;
        int l = iblockaccess.func_72805_g(i, j, k);
        float f1 = 1.0F;
        float f2 = 1.0F;
        float f3 = 1.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        boolean flag = l > 0;

        if ((l & 2) != 0) {
            f4 = Math.max(f4, 0.0625F);
            f1 = 0.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            f3 = 0.0F;
            f6 = 1.0F;
            flag = true;
        }

        if ((l & 8) != 0) {
            f1 = Math.min(f1, 0.9375F);
            f4 = 1.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            f3 = 0.0F;
            f6 = 1.0F;
            flag = true;
        }

        if ((l & 4) != 0) {
            f6 = Math.max(f6, 0.0625F);
            f3 = 0.0F;
            f1 = 0.0F;
            f4 = 1.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            flag = true;
        }

        if ((l & 1) != 0) {
            f3 = Math.min(f3, 0.9375F);
            f6 = 1.0F;
            f1 = 0.0F;
            f4 = 1.0F;
            f2 = 0.0F;
            f5 = 1.0F;
            flag = true;
        }

        if (!flag && this.func_150093_a(iblockaccess.func_147439_a(i, j + 1, k))) {
            f2 = Math.min(f2, 0.9375F);
            f5 = 1.0F;
            f1 = 0.0F;
            f4 = 1.0F;
            f3 = 0.0F;
            f6 = 1.0F;
        }

        this.func_149676_a(f1, f2, f3, f4, f5, f6);
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        return null;
    }

    @Override
    public boolean func_149707_d(World world, int i, int j, int k, int l) {
        switch (l) {
            case 1:
                return this.func_150093_a(world.func_147439_a(i, j + 1, k));
            case 2:
                return this.func_150093_a(world.func_147439_a(i, j, k + 1));
            case 3:
                return this.func_150093_a(world.func_147439_a(i, j, k - 1));
            case 4:
                return this.func_150093_a(world.func_147439_a(i + 1, j, k));
            case 5:
                return this.func_150093_a(world.func_147439_a(i - 1, j, k));
            default:
                return false;
        }
    }

    private boolean func_150093_a(Block block) {
        return block.func_149686_d() && block.field_149764_J.func_76230_c();
    }

    private boolean func_150094_e(World world, int i, int j, int k) {
        int l = world.func_72805_g(i, j, k);
        int i1 = l;

        if (l > 0) {
            for (int j1 = 0; j1 <= 3; ++j1) {
                int k1 = 1 << j1;

                if ((l & k1) != 0 && !this.func_150093_a(world.func_147439_a(i + Direction.field_71583_a[j1], j, k + Direction.field_71581_b[j1])) && (world.func_147439_a(i, j + 1, k) != this || (world.func_72805_g(i, j + 1, k) & k1) == 0)) {
                    i1 &= ~k1;
                }
            }
        }

        if (i1 == 0 && !this.func_150093_a(world.func_147439_a(i, j + 1, k))) {
            return false;
        } else {
            if (i1 != l) {
                world.func_72921_c(i, j, k, i1, 2);
            }

            return true;
        }
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        if (!world.field_72995_K && !this.func_150094_e(world, i, j, k)) {
            this.func_149697_b(world, i, j, k, world.func_72805_g(i, j, k), 0);
            world.func_147468_f(i, j, k);
        }

    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        if (!world.field_72995_K && world.field_73012_v.nextInt(4) == 0) {
            byte b0 = 4;
            int l = 5;
            boolean flag = false;

            int i1;
            int j1;
            int k1;

            label134:
            for (i1 = i - b0; i1 <= i + b0; ++i1) {
                for (k1 = k - b0; k1 <= k + b0; ++k1) {
                    for (j1 = j - 1; j1 <= j + 1; ++j1) {
                        if (world.func_147439_a(i1, j1, k1) == this) {
                            --l;
                            if (l <= 0) {
                                flag = true;
                                break label134;
                            }
                        }
                    }
                }
            }

            i1 = world.func_72805_g(i, j, k);
            k1 = world.field_73012_v.nextInt(6);
            j1 = Direction.field_71579_d[k1];
            int l1;

            if (k1 == 1 && j < 255 && world.func_147437_c(i, j + 1, k)) {
                if (flag) {
                    return;
                }

                int i2 = world.field_73012_v.nextInt(16) & i1;

                if (i2 > 0) {
                    for (l1 = 0; l1 <= 3; ++l1) {
                        if (!this.func_150093_a(world.func_147439_a(i + Direction.field_71583_a[l1], j + 1, k + Direction.field_71581_b[l1]))) {
                            i2 &= ~(1 << l1);
                        }
                    }

                    if (i2 > 0) {
                        world.func_147465_d(i, j + 1, k, this, i2, 2);
                    }
                }
            } else {
                Block block;
                int j2;

                if (k1 >= 2 && k1 <= 5 && (i1 & 1 << j1) == 0) {
                    if (flag) {
                        return;
                    }

                    block = world.func_147439_a(i + Direction.field_71583_a[j1], j, k + Direction.field_71581_b[j1]);
                    if (block.field_149764_J == Material.field_151579_a) {
                        l1 = j1 + 1 & 3;
                        j2 = j1 + 3 & 3;
                        if ((i1 & 1 << l1) != 0 && this.func_150093_a(world.func_147439_a(i + Direction.field_71583_a[j1] + Direction.field_71583_a[l1], j, k + Direction.field_71581_b[j1] + Direction.field_71581_b[l1]))) {
                            world.func_147465_d(i + Direction.field_71583_a[j1], j, k + Direction.field_71581_b[j1], this, 1 << l1, 2);
                        } else if ((i1 & 1 << j2) != 0 && this.func_150093_a(world.func_147439_a(i + Direction.field_71583_a[j1] + Direction.field_71583_a[j2], j, k + Direction.field_71581_b[j1] + Direction.field_71581_b[j2]))) {
                            world.func_147465_d(i + Direction.field_71583_a[j1], j, k + Direction.field_71581_b[j1], this, 1 << j2, 2);
                        } else if ((i1 & 1 << l1) != 0 && world.func_147437_c(i + Direction.field_71583_a[j1] + Direction.field_71583_a[l1], j, k + Direction.field_71581_b[j1] + Direction.field_71581_b[l1]) && this.func_150093_a(world.func_147439_a(i + Direction.field_71583_a[l1], j, k + Direction.field_71581_b[l1]))) {
                            world.func_147465_d(i + Direction.field_71583_a[j1] + Direction.field_71583_a[l1], j, k + Direction.field_71581_b[j1] + Direction.field_71581_b[l1], this, 1 << (j1 + 2 & 3), 2);
                        } else if ((i1 & 1 << j2) != 0 && world.func_147437_c(i + Direction.field_71583_a[j1] + Direction.field_71583_a[j2], j, k + Direction.field_71581_b[j1] + Direction.field_71581_b[j2]) && this.func_150093_a(world.func_147439_a(i + Direction.field_71583_a[j2], j, k + Direction.field_71581_b[j2]))) {
                            world.func_147465_d(i + Direction.field_71583_a[j1] + Direction.field_71583_a[j2], j, k + Direction.field_71581_b[j1] + Direction.field_71581_b[j2], this, 1 << (j1 + 2 & 3), 2);
                        } else if (this.func_150093_a(world.func_147439_a(i + Direction.field_71583_a[j1], j + 1, k + Direction.field_71581_b[j1]))) {
                            world.func_147465_d(i + Direction.field_71583_a[j1], j, k + Direction.field_71581_b[j1], this, 0, 2);
                        }
                    } else if (block.field_149764_J.func_76218_k() && block.func_149686_d()) {
                        world.func_72921_c(i, j, k, i1 | 1 << j1, 2);
                    }
                } else if (j > 1) {
                    block = world.func_147439_a(i, j - 1, k);
                    if (block.field_149764_J == Material.field_151579_a) {
                        l1 = world.field_73012_v.nextInt(16) & i1;
                        if (l1 > 0) {
                            world.func_147465_d(i, j - 1, k, this, l1, 2);
                        }
                    } else if (block == this) {
                        l1 = world.field_73012_v.nextInt(16) & i1;
                        j2 = world.func_72805_g(i, j - 1, k);
                        if (j2 != (j2 | l1)) {
                            world.func_72921_c(i, j - 1, k, j2 | l1, 2);
                        }
                    }
                }
            }
        }

    }

    @Override
    public int func_149660_a(World world, int i, int j, int k, int l, float f, float f1, float f2, int i1) {
        byte b0 = 0;

        switch (l) {
            case 2:
                b0 = 1;
                break;
            case 3:
                b0 = 4;
                break;
            case 4:
                b0 = 8;
                break;
            case 5:
                b0 = 2;
        }

        return b0 != 0 ? b0 : i1;
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return null;
    }

    @Override
    public int func_149745_a(Random random) {
        return 0;
    }

    @Override
    public void func_149636_a(World world, EntityPlayer entityplayer, int i, int j, int k, int l) {
        if (!world.field_72995_K && entityplayer.func_71045_bC() != null && entityplayer.func_71045_bC().func_77973_b() == Items.field_151097_aZ) {
            entityplayer.func_71064_a(StatList.field_75934_C[Block.func_149682_b(this)], 1);
            this.func_149642_a(world, i, j, k, new ItemStack(Blocks.field_150395_bd, 1, 0));
        } else {
            super.func_149636_a(world, entityplayer, i, j, k, l);
        }

    }
}
