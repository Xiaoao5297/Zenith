package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTrapDoor extends Block {

    protected BlockTrapDoor(Material material) {
        super(material);
        float f = 0.5F;
        float f1 = 1.0F;

        this.func_149676_a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
        this.func_149647_a(CreativeTabs.field_78028_d);
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
    public boolean func_149655_b(IBlockAccess iblockaccess, int i, int j, int k) {
        return !func_150118_d(iblockaccess.func_72805_g(i, j, k));
    }

    @Override
    public int func_149645_b() {
        return 0;
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        this.func_149719_a(world, i, j, k);
        return super.func_149668_a(world, i, j, k);
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        this.func_150117_b(iblockaccess.func_72805_g(i, j, k));
    }

    @Override
    public void func_149683_g() {
        float f = 0.1875F;

        this.func_149676_a(0.0F, 0.5F - f / 2.0F, 0.0F, 1.0F, 0.5F + f / 2.0F, 1.0F);
    }

    public void func_150117_b(int i) {
        float f = 0.1875F;

        if ((i & 8) != 0) {
            this.func_149676_a(0.0F, 1.0F - f, 0.0F, 1.0F, 1.0F, 1.0F);
        } else {
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
        }

        if (func_150118_d(i)) {
            if ((i & 3) == 0) {
                this.func_149676_a(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
            }

            if ((i & 3) == 1) {
                this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
            }

            if ((i & 3) == 2) {
                this.func_149676_a(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }

            if ((i & 3) == 3) {
                this.func_149676_a(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
            }
        }

    }

    @Override
    public void func_149699_a(World world, int i, int j, int k, EntityPlayer entityplayer) {}

    @Override
    public boolean func_149727_a(World world, int i, int j, int k, EntityPlayer entityplayer, int l, float f, float f1, float f2) {
        if (this.field_149764_J == Material.field_151573_f) {
            return true;
        } else {
            int i1 = world.func_72805_g(i, j, k);

            world.func_72921_c(i, j, k, i1 ^ 4, 2);
            world.func_72889_a(entityplayer, 1003, i, j, k, 0);
            return true;
        }
    }

    public void func_150120_a(World world, int i, int j, int k, boolean flag) {
        int l = world.func_72805_g(i, j, k);
        boolean flag1 = (l & 4) > 0;

        if (flag1 != flag) {
            world.func_72921_c(i, j, k, l ^ 4, 2);
            world.func_72889_a((EntityPlayer) null, 1003, i, j, k, 0);
        }
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        if (!world.field_72995_K) {
            int l = world.func_72805_g(i, j, k);
            int i1 = i;
            int j1 = k;

            if ((l & 3) == 0) {
                j1 = k + 1;
            }

            if ((l & 3) == 1) {
                --j1;
            }

            if ((l & 3) == 2) {
                i1 = i + 1;
            }

            if ((l & 3) == 3) {
                --i1;
            }

            if (!func_150119_a(world.func_147439_a(i1, j, j1))) {
                world.func_147468_f(i, j, k);
                this.func_149697_b(world, i, j, k, l, 0);
            }

            boolean flag = world.func_72864_z(i, j, k);

            if (flag || block.func_149744_f()) {
                this.func_150120_a(world, i, j, k, flag);
            }

        }
    }

    @Override
    public MovingObjectPosition func_149731_a(World world, int i, int j, int k, Vec3 vec3, Vec3 vec31) {
        this.func_149719_a(world, i, j, k);
        return super.func_149731_a(world, i, j, k, vec3, vec31);
    }

    @Override
    public int func_149660_a(World world, int i, int j, int k, int l, float f, float f1, float f2, int i1) {
        int j1 = 0;

        if (l == 2) {
            j1 = 0;
        }

        if (l == 3) {
            j1 = 1;
        }

        if (l == 4) {
            j1 = 2;
        }

        if (l == 5) {
            j1 = 3;
        }

        if (l != 1 && l != 0 && f1 > 0.5F) {
            j1 |= 8;
        }

        return j1;
    }

    @Override
    public boolean func_149707_d(World world, int i, int j, int k, int l) {
        if (l == 0) {
            return false;
        } else if (l == 1) {
            return false;
        } else {
            if (l == 2) {
                ++k;
            }

            if (l == 3) {
                --k;
            }

            if (l == 4) {
                ++i;
            }

            if (l == 5) {
                --i;
            }

            return func_150119_a(world.func_147439_a(i, j, k));
        }
    }

    public static boolean func_150118_d(int i) {
        return (i & 4) != 0;
    }

    private static boolean func_150119_a(Block block) {
        return block.field_149764_J.func_76218_k() && block.func_149686_d() || block == Blocks.field_150426_aN || block instanceof BlockSlab || block instanceof BlockStairs;
    }
}
