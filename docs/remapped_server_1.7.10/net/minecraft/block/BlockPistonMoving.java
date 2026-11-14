package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonMoving extends BlockContainer {

    public BlockPistonMoving() {
        super(Material.field_76233_E);
        this.func_149711_c(-1.0F);
    }

    @Override
    public TileEntity func_149915_a(World world, int i) {
        return null;
    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {}

    @Override
    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {
        TileEntity tileentity = world.func_147438_o(i, j, k);

        if (tileentity instanceof TileEntityPiston) {
            ((TileEntityPiston) tileentity).func_145866_f();
        } else {
            super.func_149749_a(world, i, j, k, block, l);
        }

    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        return false;
    }

    @Override
    public boolean func_149707_d(World world, int i, int j, int k, int l) {
        return false;
    }

    @Override
    public int func_149645_b() {
        return -1;
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
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        if (!world.field_72995_K && world.func_147438_o(i, j, k) == null) {
            world.func_147468_f(i, j, k);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return null;
    }

    @Override
    public void func_149690_a(World world, int i, int j, int k, int l, float f, int i1) {
        if (!world.field_72995_K) {
            TileEntityPiston tileentitypiston = this.func_149963_e(world, i, j, k);

            if (tileentitypiston != null) {
                tileentitypiston.func_145861_a().func_149697_b(world, i, j, k, tileentitypiston.func_145832_p(), 0);
            }
        }
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        if (!world.field_72995_K) {
            world.func_147438_o(i, j, k);
        }

    }

    public static TileEntity func_149962_a(Block block, int i, int j, boolean flag, boolean flag1) {
        return new TileEntityPiston(block, i, j, flag, flag1);
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        TileEntityPiston tileentitypiston = this.func_149963_e(world, i, j, k);

        if (tileentitypiston == null) {
            return null;
        } else {
            float f = tileentitypiston.func_145860_a(0.0F);

            if (tileentitypiston.func_145868_b()) {
                f = 1.0F - f;
            }

            return this.func_149964_a(world, i, j, k, tileentitypiston.func_145861_a(), f, tileentitypiston.func_145864_c());
        }
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        TileEntityPiston tileentitypiston = this.func_149963_e(iblockaccess, i, j, k);

        if (tileentitypiston != null) {
            Block block = tileentitypiston.func_145861_a();

            if (block == this || block.func_149688_o() == Material.field_151579_a) {
                return;
            }

            block.func_149719_a(iblockaccess, i, j, k);
            float f = tileentitypiston.func_145860_a(0.0F);

            if (tileentitypiston.func_145868_b()) {
                f = 1.0F - f;
            }

            int l = tileentitypiston.func_145864_c();

            this.field_149759_B = block.func_149704_x() - (double) ((float) Facing.field_71586_b[l] * f);
            this.field_149760_C = block.func_149665_z() - (double) ((float) Facing.field_71587_c[l] * f);
            this.field_149754_D = block.func_149706_B() - (double) ((float) Facing.field_71585_d[l] * f);
            this.field_149755_E = block.func_149753_y() - (double) ((float) Facing.field_71586_b[l] * f);
            this.field_149756_F = block.func_149669_A() - (double) ((float) Facing.field_71587_c[l] * f);
            this.field_149757_G = block.func_149693_C() - (double) ((float) Facing.field_71585_d[l] * f);
        }

    }

    public AxisAlignedBB func_149964_a(World world, int i, int j, int k, Block block, float f, int l) {
        if (block != this && block.func_149688_o() != Material.field_151579_a) {
            AxisAlignedBB axisalignedbb = block.func_149668_a(world, i, j, k);

            if (axisalignedbb == null) {
                return null;
            } else {
                if (Facing.field_71586_b[l] < 0) {
                    axisalignedbb.field_72340_a -= (double) ((float) Facing.field_71586_b[l] * f);
                } else {
                    axisalignedbb.field_72336_d -= (double) ((float) Facing.field_71586_b[l] * f);
                }

                if (Facing.field_71587_c[l] < 0) {
                    axisalignedbb.field_72338_b -= (double) ((float) Facing.field_71587_c[l] * f);
                } else {
                    axisalignedbb.field_72337_e -= (double) ((float) Facing.field_71587_c[l] * f);
                }

                if (Facing.field_71585_d[l] < 0) {
                    axisalignedbb.field_72339_c -= (double) ((float) Facing.field_71585_d[l] * f);
                } else {
                    axisalignedbb.field_72334_f -= (double) ((float) Facing.field_71585_d[l] * f);
                }

                return axisalignedbb;
            }
        } else {
            return null;
        }
    }

    private TileEntityPiston func_149963_e(IBlockAccess iblockaccess, int i, int j, int k) {
        TileEntity tileentity = iblockaccess.func_147438_o(i, j, k);

        return tileentity instanceof TileEntityPiston ? (TileEntityPiston) tileentity : null;
    }
}
