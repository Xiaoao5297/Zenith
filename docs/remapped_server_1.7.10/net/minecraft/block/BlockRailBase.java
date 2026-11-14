package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockRailBase extends Block {

    protected final boolean field_150053_a;

    public static final boolean func_150049_b_(World world, int i, int j, int k) {
        return func_150051_a(world.func_147439_a(i, j, k));
    }

    public static final boolean func_150051_a(Block block) {
        return block == Blocks.field_150448_aq || block == Blocks.field_150318_D || block == Blocks.field_150319_E || block == Blocks.field_150408_cc;
    }

    protected BlockRailBase(boolean flag) {
        super(Material.field_151594_q);
        this.field_150053_a = flag;
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
        this.func_149647_a(CreativeTabs.field_78029_e);
    }

    public boolean func_150050_e() {
        return this.field_150053_a;
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
    public MovingObjectPosition func_149731_a(World world, int i, int j, int k, Vec3 vec3, Vec3 vec31) {
        this.func_149719_a(world, i, j, k);
        return super.func_149731_a(world, i, j, k, vec3, vec31);
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        int l = iblockaccess.func_72805_g(i, j, k);

        if (l >= 2 && l <= 5) {
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
        } else {
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
        }

    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    @Override
    public int func_149645_b() {
        return 9;
    }

    @Override
    public int func_149745_a(Random random) {
        return 1;
    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        return World.func_147466_a(world, i, j - 1, k);
    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {
        if (!world.field_72995_K) {
            this.func_150052_a(world, i, j, k, true);
            if (this.field_150053_a) {
                this.func_149695_a(world, i, j, k, this);
            }
        }

    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        if (!world.field_72995_K) {
            int l = world.func_72805_g(i, j, k);
            int i1 = l;

            if (this.field_150053_a) {
                i1 = l & 7;
            }

            boolean flag = false;

            if (!World.func_147466_a(world, i, j - 1, k)) {
                flag = true;
            }

            if (i1 == 2 && !World.func_147466_a(world, i + 1, j, k)) {
                flag = true;
            }

            if (i1 == 3 && !World.func_147466_a(world, i - 1, j, k)) {
                flag = true;
            }

            if (i1 == 4 && !World.func_147466_a(world, i, j, k - 1)) {
                flag = true;
            }

            if (i1 == 5 && !World.func_147466_a(world, i, j, k + 1)) {
                flag = true;
            }

            if (flag) {
                this.func_149697_b(world, i, j, k, world.func_72805_g(i, j, k), 0);
                world.func_147468_f(i, j, k);
            } else {
                this.func_150048_a(world, i, j, k, l, i1, block);
            }

        }
    }

    protected void func_150048_a(World world, int i, int j, int k, int l, int i1, Block block) {}

    protected void func_150052_a(World world, int i, int j, int k, boolean flag) {
        if (!world.field_72995_K) {
            (new BlockRailBase$Rail(this, world, i, j, k)).func_150655_a(world.func_72864_z(i, j, k), flag);
        }
    }

    @Override
    public int func_149656_h() {
        return 0;
    }

    @Override
    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {
        int i1 = l;

        if (this.field_150053_a) {
            i1 = l & 7;
        }

        super.func_149749_a(world, i, j, k, block, l);
        if (i1 == 2 || i1 == 3 || i1 == 4 || i1 == 5) {
            world.func_147459_d(i, j + 1, k, block);
        }

        if (this.field_150053_a) {
            world.func_147459_d(i, j, k, block);
            world.func_147459_d(i, j - 1, k, block);
        }

    }
}
