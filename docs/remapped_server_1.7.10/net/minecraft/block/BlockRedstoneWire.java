package net.minecraft.block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneWire extends Block {

    private boolean field_150181_a = true;
    private Set field_150179_b = new HashSet();

    public BlockRedstoneWire() {
        super(Material.field_151594_q);
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
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
        return 5;
    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        return World.func_147466_a(world, i, j - 1, k) || world.func_147439_a(i, j - 1, k) == Blocks.field_150426_aN;
    }

    private void func_150177_e(World world, int i, int j, int k) {
        this.func_150175_a(world, i, j, k, i, j, k);
        ArrayList arraylist = new ArrayList(this.field_150179_b);

        this.field_150179_b.clear();

        for (int l = 0; l < arraylist.size(); ++l) {
            ChunkPosition chunkposition = (ChunkPosition) arraylist.get(l);

            world.func_147459_d(chunkposition.field_151329_a, chunkposition.field_151327_b, chunkposition.field_151328_c, this);
        }

    }

    private void func_150175_a(World world, int i, int j, int k, int l, int i1, int j1) {
        int k1 = world.func_72805_g(i, j, k);
        int l1 = 0;

        l1 = this.func_150178_a(world, l, i1, j1, l1);
        this.field_150181_a = false;
        int i2 = world.func_94572_D(i, j, k);

        this.field_150181_a = true;
        if (i2 > 0 && i2 > l1 - 1) {
            l1 = i2;
        }

        int j2 = 0;

        for (int k2 = 0; k2 < 4; ++k2) {
            int l2 = i;
            int i3 = k;

            if (k2 == 0) {
                l2 = i - 1;
            }

            if (k2 == 1) {
                ++l2;
            }

            if (k2 == 2) {
                i3 = k - 1;
            }

            if (k2 == 3) {
                ++i3;
            }

            if (l2 != l || i3 != j1) {
                j2 = this.func_150178_a(world, l2, j, i3, j2);
            }

            if (world.func_147439_a(l2, j, i3).func_149721_r() && !world.func_147439_a(i, j + 1, k).func_149721_r()) {
                if ((l2 != l || i3 != j1) && j >= i1) {
                    j2 = this.func_150178_a(world, l2, j + 1, i3, j2);
                }
            } else if (!world.func_147439_a(l2, j, i3).func_149721_r() && (l2 != l || i3 != j1) && j <= i1) {
                j2 = this.func_150178_a(world, l2, j - 1, i3, j2);
            }
        }

        if (j2 > l1) {
            l1 = j2 - 1;
        } else if (l1 > 0) {
            --l1;
        } else {
            l1 = 0;
        }

        if (i2 > l1 - 1) {
            l1 = i2;
        }

        if (k1 != l1) {
            world.func_72921_c(i, j, k, l1, 2);
            this.field_150179_b.add(new ChunkPosition(i, j, k));
            this.field_150179_b.add(new ChunkPosition(i - 1, j, k));
            this.field_150179_b.add(new ChunkPosition(i + 1, j, k));
            this.field_150179_b.add(new ChunkPosition(i, j - 1, k));
            this.field_150179_b.add(new ChunkPosition(i, j + 1, k));
            this.field_150179_b.add(new ChunkPosition(i, j, k - 1));
            this.field_150179_b.add(new ChunkPosition(i, j, k + 1));
        }

    }

    private void func_150172_m(World world, int i, int j, int k) {
        if (world.func_147439_a(i, j, k) == this) {
            world.func_147459_d(i, j, k, this);
            world.func_147459_d(i - 1, j, k, this);
            world.func_147459_d(i + 1, j, k, this);
            world.func_147459_d(i, j, k - 1, this);
            world.func_147459_d(i, j, k + 1, this);
            world.func_147459_d(i, j - 1, k, this);
            world.func_147459_d(i, j + 1, k, this);
        }
    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {
        super.func_149726_b(world, i, j, k);
        if (!world.field_72995_K) {
            this.func_150177_e(world, i, j, k);
            world.func_147459_d(i, j + 1, k, this);
            world.func_147459_d(i, j - 1, k, this);
            this.func_150172_m(world, i - 1, j, k);
            this.func_150172_m(world, i + 1, j, k);
            this.func_150172_m(world, i, j, k - 1);
            this.func_150172_m(world, i, j, k + 1);
            if (world.func_147439_a(i - 1, j, k).func_149721_r()) {
                this.func_150172_m(world, i - 1, j + 1, k);
            } else {
                this.func_150172_m(world, i - 1, j - 1, k);
            }

            if (world.func_147439_a(i + 1, j, k).func_149721_r()) {
                this.func_150172_m(world, i + 1, j + 1, k);
            } else {
                this.func_150172_m(world, i + 1, j - 1, k);
            }

            if (world.func_147439_a(i, j, k - 1).func_149721_r()) {
                this.func_150172_m(world, i, j + 1, k - 1);
            } else {
                this.func_150172_m(world, i, j - 1, k - 1);
            }

            if (world.func_147439_a(i, j, k + 1).func_149721_r()) {
                this.func_150172_m(world, i, j + 1, k + 1);
            } else {
                this.func_150172_m(world, i, j - 1, k + 1);
            }

        }
    }

    @Override
    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {
        super.func_149749_a(world, i, j, k, block, l);
        if (!world.field_72995_K) {
            world.func_147459_d(i, j + 1, k, this);
            world.func_147459_d(i, j - 1, k, this);
            world.func_147459_d(i + 1, j, k, this);
            world.func_147459_d(i - 1, j, k, this);
            world.func_147459_d(i, j, k + 1, this);
            world.func_147459_d(i, j, k - 1, this);
            this.func_150177_e(world, i, j, k);
            this.func_150172_m(world, i - 1, j, k);
            this.func_150172_m(world, i + 1, j, k);
            this.func_150172_m(world, i, j, k - 1);
            this.func_150172_m(world, i, j, k + 1);
            if (world.func_147439_a(i - 1, j, k).func_149721_r()) {
                this.func_150172_m(world, i - 1, j + 1, k);
            } else {
                this.func_150172_m(world, i - 1, j - 1, k);
            }

            if (world.func_147439_a(i + 1, j, k).func_149721_r()) {
                this.func_150172_m(world, i + 1, j + 1, k);
            } else {
                this.func_150172_m(world, i + 1, j - 1, k);
            }

            if (world.func_147439_a(i, j, k - 1).func_149721_r()) {
                this.func_150172_m(world, i, j + 1, k - 1);
            } else {
                this.func_150172_m(world, i, j - 1, k - 1);
            }

            if (world.func_147439_a(i, j, k + 1).func_149721_r()) {
                this.func_150172_m(world, i, j + 1, k + 1);
            } else {
                this.func_150172_m(world, i, j - 1, k + 1);
            }

        }
    }

    private int func_150178_a(World world, int i, int j, int k, int l) {
        if (world.func_147439_a(i, j, k) != this) {
            return l;
        } else {
            int i1 = world.func_72805_g(i, j, k);

            return i1 > l ? i1 : l;
        }
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        if (!world.field_72995_K) {
            boolean flag = this.func_149742_c(world, i, j, k);

            if (flag) {
                this.func_150177_e(world, i, j, k);
            } else {
                this.func_149697_b(world, i, j, k, 0, 0);
                world.func_147468_f(i, j, k);
            }

            super.func_149695_a(world, i, j, k, block);
        }
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Items.field_151137_ax;
    }

    @Override
    public int func_149748_c(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return !this.field_150181_a ? 0 : this.func_149709_b(iblockaccess, i, j, k, l);
    }

    @Override
    public int func_149709_b(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        if (!this.field_150181_a) {
            return 0;
        } else {
            int i1 = iblockaccess.func_72805_g(i, j, k);

            if (i1 == 0) {
                return 0;
            } else if (l == 1) {
                return i1;
            } else {
                boolean flag = func_150176_g(iblockaccess, i - 1, j, k, 1) || !iblockaccess.func_147439_a(i - 1, j, k).func_149721_r() && func_150176_g(iblockaccess, i - 1, j - 1, k, -1);
                boolean flag1 = func_150176_g(iblockaccess, i + 1, j, k, 3) || !iblockaccess.func_147439_a(i + 1, j, k).func_149721_r() && func_150176_g(iblockaccess, i + 1, j - 1, k, -1);
                boolean flag2 = func_150176_g(iblockaccess, i, j, k - 1, 2) || !iblockaccess.func_147439_a(i, j, k - 1).func_149721_r() && func_150176_g(iblockaccess, i, j - 1, k - 1, -1);
                boolean flag3 = func_150176_g(iblockaccess, i, j, k + 1, 0) || !iblockaccess.func_147439_a(i, j, k + 1).func_149721_r() && func_150176_g(iblockaccess, i, j - 1, k + 1, -1);

                if (!iblockaccess.func_147439_a(i, j + 1, k).func_149721_r()) {
                    if (iblockaccess.func_147439_a(i - 1, j, k).func_149721_r() && func_150176_g(iblockaccess, i - 1, j + 1, k, -1)) {
                        flag = true;
                    }

                    if (iblockaccess.func_147439_a(i + 1, j, k).func_149721_r() && func_150176_g(iblockaccess, i + 1, j + 1, k, -1)) {
                        flag1 = true;
                    }

                    if (iblockaccess.func_147439_a(i, j, k - 1).func_149721_r() && func_150176_g(iblockaccess, i, j + 1, k - 1, -1)) {
                        flag2 = true;
                    }

                    if (iblockaccess.func_147439_a(i, j, k + 1).func_149721_r() && func_150176_g(iblockaccess, i, j + 1, k + 1, -1)) {
                        flag3 = true;
                    }
                }

                return !flag2 && !flag1 && !flag && !flag3 && l >= 2 && l <= 5 ? i1 : (l == 2 && flag2 && !flag && !flag1 ? i1 : (l == 3 && flag3 && !flag && !flag1 ? i1 : (l == 4 && flag && !flag2 && !flag3 ? i1 : (l == 5 && flag1 && !flag2 && !flag3 ? i1 : 0))));
            }
        }
    }

    @Override
    public boolean func_149744_f() {
        return this.field_150181_a;
    }

    public static boolean func_150174_f(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        Block block = iblockaccess.func_147439_a(i, j, k);

        if (block == Blocks.field_150488_af) {
            return true;
        } else if (!Blocks.field_150413_aR.func_149907_e(block)) {
            return block.func_149744_f() && l != -1;
        } else {
            int i1 = iblockaccess.func_72805_g(i, j, k);

            return l == (i1 & 3) || l == Direction.field_71580_e[i1 & 3];
        }
    }

    public static boolean func_150176_g(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        if (func_150174_f(iblockaccess, i, j, k, l)) {
            return true;
        } else if (iblockaccess.func_147439_a(i, j, k) == Blocks.field_150416_aS) {
            int i1 = iblockaccess.func_72805_g(i, j, k);

            return l == (i1 & 3);
        } else {
            return false;
        }
    }
}
