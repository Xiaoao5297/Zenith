package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonBase extends Block {

    private final boolean field_150082_a;

    public BlockPistonBase(boolean flag) {
        super(Material.field_76233_E);
        this.field_150082_a = flag;
        this.func_149672_a(BlockPistonBase.field_149780_i);
        this.func_149711_c(0.5F);
        this.func_149647_a(CreativeTabs.field_78028_d);
    }

    @Override
    public int func_149645_b() {
        return 16;
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        return false;
    }

    @Override
    public void func_149689_a(World world, int i, int j, int k, EntityLivingBase entitylivingbase, ItemStack itemstack) {
        int l = func_150071_a(world, i, j, k, entitylivingbase);

        world.func_72921_c(i, j, k, l, 2);
        if (!world.field_72995_K) {
            this.func_150078_e(world, i, j, k);
        }

    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        if (!world.field_72995_K) {
            this.func_150078_e(world, i, j, k);
        }

    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {
        if (!world.field_72995_K && world.func_147438_o(i, j, k) == null) {
            this.func_150078_e(world, i, j, k);
        }

    }

    private void func_150078_e(World world, int i, int j, int k) {
        int l = world.func_72805_g(i, j, k);
        int i1 = func_150076_b(l);

        if (i1 != 7) {
            boolean flag = this.func_150072_a(world, i, j, k, i1);

            if (flag && !func_150075_c(l)) {
                if (func_150077_h(world, i, j, k, i1)) {
                    world.func_147452_c(i, j, k, this, 0, i1);
                }
            } else if (!flag && func_150075_c(l)) {
                world.func_72921_c(i, j, k, i1, 2);
                world.func_147452_c(i, j, k, this, 1, i1);
            }

        }
    }

    private boolean func_150072_a(World world, int i, int j, int k, int l) {
        return l != 0 && world.func_94574_k(i, j - 1, k, 0) ? true : (l != 1 && world.func_94574_k(i, j + 1, k, 1) ? true : (l != 2 && world.func_94574_k(i, j, k - 1, 2) ? true : (l != 3 && world.func_94574_k(i, j, k + 1, 3) ? true : (l != 5 && world.func_94574_k(i + 1, j, k, 5) ? true : (l != 4 && world.func_94574_k(i - 1, j, k, 4) ? true : (world.func_94574_k(i, j, k, 0) ? true : (world.func_94574_k(i, j + 2, k, 1) ? true : (world.func_94574_k(i, j + 1, k - 1, 2) ? true : (world.func_94574_k(i, j + 1, k + 1, 3) ? true : (world.func_94574_k(i - 1, j + 1, k, 4) ? true : world.func_94574_k(i + 1, j + 1, k, 5)))))))))));
    }

    @Override
    public boolean func_149696_a(World world, int i, int j, int k, int l, int i1) {
        if (!world.field_72995_K) {
            boolean flag = this.func_150072_a(world, i, j, k, i1);

            if (flag && l == 1) {
                world.func_72921_c(i, j, k, i1 | 8, 2);
                return false;
            }

            if (!flag && l == 0) {
                return false;
            }
        }

        if (l == 0) {
            if (!this.func_150079_i(world, i, j, k, i1)) {
                return false;
            }

            world.func_72921_c(i, j, k, i1 | 8, 2);
            world.func_72908_a((double) i + 0.5D, (double) j + 0.5D, (double) k + 0.5D, "tile.piston.out", 0.5F, world.field_73012_v.nextFloat() * 0.25F + 0.6F);
        } else if (l == 1) {
            TileEntity tileentity = world.func_147438_o(i + Facing.field_71586_b[i1], j + Facing.field_71587_c[i1], k + Facing.field_71585_d[i1]);

            if (tileentity instanceof TileEntityPiston) {
                ((TileEntityPiston) tileentity).func_145866_f();
            }

            world.func_147465_d(i, j, k, Blocks.field_150326_M, i1, 3);
            world.func_147455_a(i, j, k, BlockPistonMoving.func_149962_a(this, i1, i1, false, true));
            if (this.field_150082_a) {
                int j1 = i + Facing.field_71586_b[i1] * 2;
                int k1 = j + Facing.field_71587_c[i1] * 2;
                int l1 = k + Facing.field_71585_d[i1] * 2;
                Block block = world.func_147439_a(j1, k1, l1);
                int i2 = world.func_72805_g(j1, k1, l1);
                boolean flag1 = false;

                if (block == Blocks.field_150326_M) {
                    TileEntity tileentity1 = world.func_147438_o(j1, k1, l1);

                    if (tileentity1 instanceof TileEntityPiston) {
                        TileEntityPiston tileentitypiston = (TileEntityPiston) tileentity1;

                        if (tileentitypiston.func_145864_c() == i1 && tileentitypiston.func_145868_b()) {
                            tileentitypiston.func_145866_f();
                            block = tileentitypiston.func_145861_a();
                            i2 = tileentitypiston.func_145832_p();
                            flag1 = true;
                        }
                    }
                }

                if (!flag1 && block.func_149688_o() != Material.field_151579_a && func_150080_a(block, world, j1, k1, l1, false) && (block.func_149656_h() == 0 || block == Blocks.field_150331_J || block == Blocks.field_150320_F)) {
                    i += Facing.field_71586_b[i1];
                    j += Facing.field_71587_c[i1];
                    k += Facing.field_71585_d[i1];
                    world.func_147465_d(i, j, k, Blocks.field_150326_M, i2, 3);
                    world.func_147455_a(i, j, k, BlockPistonMoving.func_149962_a(block, i2, i1, false, false));
                    world.func_147468_f(j1, k1, l1);
                } else if (!flag1) {
                    world.func_147468_f(i + Facing.field_71586_b[i1], j + Facing.field_71587_c[i1], k + Facing.field_71585_d[i1]);
                }
            } else {
                world.func_147468_f(i + Facing.field_71586_b[i1], j + Facing.field_71587_c[i1], k + Facing.field_71585_d[i1]);
            }

            world.func_72908_a((double) i + 0.5D, (double) j + 0.5D, (double) k + 0.5D, "tile.piston.in", 0.5F, world.field_73012_v.nextFloat() * 0.15F + 0.6F);
        }

        return true;
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        int l = iblockaccess.func_72805_g(i, j, k);

        if (func_150075_c(l)) {
            float f = 0.25F;

            switch (func_150076_b(l)) {
                case 0:
                    this.func_149676_a(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F);
                    break;
                case 1:
                    this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
                    break;
                case 2:
                    this.func_149676_a(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F);
                    break;
                case 3:
                    this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F);
                    break;
                case 4:
                    this.func_149676_a(0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                    break;
                case 5:
                    this.func_149676_a(0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F);
            }
        } else {
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }

    }

    @Override
    public void func_149683_g() {
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void func_149743_a(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, List list, Entity entity) {
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        this.func_149719_a(world, i, j, k);
        return super.func_149668_a(world, i, j, k);
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    public static int func_150076_b(int i) {
        return i & 7;
    }

    public static boolean func_150075_c(int i) {
        return (i & 8) != 0;
    }

    public static int func_150071_a(World world, int i, int j, int k, EntityLivingBase entitylivingbase) {
        if (MathHelper.func_76135_e((float) entitylivingbase.field_70165_t - (float) i) < 2.0F && MathHelper.func_76135_e((float) entitylivingbase.field_70161_v - (float) k) < 2.0F) {
            double d0 = entitylivingbase.field_70163_u + 1.82D - (double) entitylivingbase.field_70129_M;

            if (d0 - (double) j > 2.0D) {
                return 1;
            }

            if ((double) j - d0 > 0.0D) {
                return 0;
            }
        }

        int l = MathHelper.func_76128_c((double) (entitylivingbase.field_70177_z * 4.0F / 360.0F) + 0.5D) & 3;

        return l == 0 ? 2 : (l == 1 ? 5 : (l == 2 ? 3 : (l == 3 ? 4 : 0)));
    }

    private static boolean func_150080_a(Block block, World world, int i, int j, int k, boolean flag) {
        if (block == Blocks.field_150343_Z) {
            return false;
        } else {
            if (block != Blocks.field_150331_J && block != Blocks.field_150320_F) {
                if (block.func_149712_f(world, i, j, k) == -1.0F) {
                    return false;
                }

                if (block.func_149656_h() == 2) {
                    return false;
                }

                if (block.func_149656_h() == 1) {
                    if (!flag) {
                        return false;
                    }

                    return true;
                }
            } else if (func_150075_c(world.func_72805_g(i, j, k))) {
                return false;
            }

            return !(block instanceof ITileEntityProvider);
        }
    }

    private static boolean func_150077_h(World world, int i, int j, int k, int l) {
        int i1 = i + Facing.field_71586_b[l];
        int j1 = j + Facing.field_71587_c[l];
        int k1 = k + Facing.field_71585_d[l];
        int l1 = 0;

        while (true) {
            if (l1 < 13) {
                if (j1 <= 0 || j1 >= 255) {
                    return false;
                }

                Block block = world.func_147439_a(i1, j1, k1);

                if (block.func_149688_o() != Material.field_151579_a) {
                    if (!func_150080_a(block, world, i1, j1, k1, true)) {
                        return false;
                    }

                    if (block.func_149656_h() != 1) {
                        if (l1 == 12) {
                            return false;
                        }

                        i1 += Facing.field_71586_b[l];
                        j1 += Facing.field_71587_c[l];
                        k1 += Facing.field_71585_d[l];
                        ++l1;
                        continue;
                    }
                }
            }

            return true;
        }
    }

    private boolean func_150079_i(World world, int i, int j, int k, int l) {
        int i1 = i + Facing.field_71586_b[l];
        int j1 = j + Facing.field_71587_c[l];
        int k1 = k + Facing.field_71585_d[l];
        int l1 = 0;

        while (true) {
            if (l1 < 13) {
                if (j1 <= 0 || j1 >= 255) {
                    return false;
                }

                Block block = world.func_147439_a(i1, j1, k1);

                if (block.func_149688_o() != Material.field_151579_a) {
                    if (!func_150080_a(block, world, i1, j1, k1, true)) {
                        return false;
                    }

                    if (block.func_149656_h() != 1) {
                        if (l1 == 12) {
                            return false;
                        }

                        i1 += Facing.field_71586_b[l];
                        j1 += Facing.field_71587_c[l];
                        k1 += Facing.field_71585_d[l];
                        ++l1;
                        continue;
                    }

                    block.func_149697_b(world, i1, j1, k1, world.func_72805_g(i1, j1, k1), 0);
                    world.func_147468_f(i1, j1, k1);
                }
            }

            l1 = i1;
            int i2 = j1;
            int j2 = k1;
            int k2 = 0;

            int l2;
            int i3;
            int j3;
            Block[] ablock;

            for (ablock = new Block[13]; i1 != i || j1 != j || k1 != k; k1 = j3) {
                l2 = i1 - Facing.field_71586_b[l];
                i3 = j1 - Facing.field_71587_c[l];
                j3 = k1 - Facing.field_71585_d[l];
                Block block1 = world.func_147439_a(l2, i3, j3);
                int k3 = world.func_72805_g(l2, i3, j3);

                if (block1 == this && l2 == i && i3 == j && j3 == k) {
                    world.func_147465_d(i1, j1, k1, Blocks.field_150326_M, l | (this.field_150082_a ? 8 : 0), 4);
                    world.func_147455_a(i1, j1, k1, BlockPistonMoving.func_149962_a(Blocks.field_150332_K, l | (this.field_150082_a ? 8 : 0), l, true, false));
                } else {
                    world.func_147465_d(i1, j1, k1, Blocks.field_150326_M, k3, 4);
                    world.func_147455_a(i1, j1, k1, BlockPistonMoving.func_149962_a(block1, k3, l, true, false));
                }

                ablock[k2++] = block1;
                i1 = l2;
                j1 = i3;
            }

            i1 = l1;
            j1 = i2;
            k1 = j2;

            for (k2 = 0; i1 != i || j1 != j || k1 != k; k1 = j3) {
                l2 = i1 - Facing.field_71586_b[l];
                i3 = j1 - Facing.field_71587_c[l];
                j3 = k1 - Facing.field_71585_d[l];
                world.func_147459_d(l2, i3, j3, ablock[k2++]);
                i1 = l2;
                j1 = i3;
            }

            return true;
        }
    }
}
