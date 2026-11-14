package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockSlab extends Block {

    protected final boolean field_150004_a;

    public BlockSlab(boolean flag, Material material) {
        super(material);
        this.field_150004_a = flag;
        if (flag) {
            this.field_149787_q = true;
        } else {
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }

        this.func_149713_g(255);
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        if (this.field_150004_a) {
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else {
            boolean flag = (iblockaccess.func_72805_g(i, j, k) & 8) != 0;

            if (flag) {
                this.func_149676_a(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else {
                this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
            }
        }

    }

    @Override
    public void func_149683_g() {
        if (this.field_150004_a) {
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else {
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }

    }

    @Override
    public void func_149743_a(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, List list, Entity entity) {
        this.func_149719_a(world, i, j, k);
        super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
    }

    @Override
    public boolean func_149662_c() {
        return this.field_150004_a;
    }

    @Override
    public int func_149660_a(World world, int i, int j, int k, int l, float f, float f1, float f2, int i1) {
        return this.field_150004_a ? i1 : (l != 0 && (l == 1 || (double) f1 <= 0.5D) ? i1 : i1 | 8);
    }

    @Override
    public int func_149745_a(Random random) {
        return this.field_150004_a ? 2 : 1;
    }

    @Override
    public int func_149692_a(int i) {
        return i & 7;
    }

    @Override
    public boolean func_149686_d() {
        return this.field_150004_a;
    }

    public abstract String func_150002_b(int i);

    @Override
    public int func_149643_k(World world, int i, int j, int k) {
        return super.func_149643_k(world, i, j, k) & 7;
    }
}
