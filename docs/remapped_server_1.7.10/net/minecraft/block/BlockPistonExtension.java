package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonExtension extends Block {

    public BlockPistonExtension() {
        super(Material.field_76233_E);
        this.func_149672_a(BlockPistonExtension.field_149780_i);
        this.func_149711_c(0.5F);
    }

    @Override
    public void func_149681_a(World world, int i, int j, int k, int l, EntityPlayer entityplayer) {
        if (entityplayer.field_71075_bZ.field_75098_d) {
            int i1 = func_150085_b(l);
            Block block = world.func_147439_a(i - Facing.field_71586_b[i1], j - Facing.field_71587_c[i1], k - Facing.field_71585_d[i1]);

            if (block == Blocks.field_150331_J || block == Blocks.field_150320_F) {
                world.func_147468_f(i - Facing.field_71586_b[i1], j - Facing.field_71587_c[i1], k - Facing.field_71585_d[i1]);
            }
        }

        super.func_149681_a(world, i, j, k, l, entityplayer);
    }

    @Override
    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {
        super.func_149749_a(world, i, j, k, block, l);
        int i1 = Facing.field_71588_a[func_150085_b(l)];

        i += Facing.field_71586_b[i1];
        j += Facing.field_71587_c[i1];
        k += Facing.field_71585_d[i1];
        Block block1 = world.func_147439_a(i, j, k);

        if (block1 == Blocks.field_150331_J || block1 == Blocks.field_150320_F) {
            l = world.func_72805_g(i, j, k);
            if (BlockPistonBase.func_150075_c(l)) {
                block1.func_149697_b(world, i, j, k, l, 0);
                world.func_147468_f(i, j, k);
            }
        }

    }

    @Override
    public int func_149645_b() {
        return 17;
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
    public boolean func_149742_c(World world, int i, int j, int k) {
        return false;
    }

    @Override
    public boolean func_149707_d(World world, int i, int j, int k, int l) {
        return false;
    }

    @Override
    public int func_149745_a(Random random) {
        return 0;
    }

    @Override
    public void func_149743_a(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, List list, Entity entity) {
        int l = world.func_72805_g(i, j, k);
        float f = 0.25F;
        float f1 = 0.375F;
        float f2 = 0.625F;
        float f3 = 0.25F;
        float f4 = 0.75F;

        switch (func_150085_b(l)) {
            case 0:
                this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
                super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
                this.func_149676_a(0.375F, 0.25F, 0.375F, 0.625F, 1.0F, 0.625F);
                super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
                break;
            case 1:
                this.func_149676_a(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
                super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
                this.func_149676_a(0.375F, 0.0F, 0.375F, 0.625F, 0.75F, 0.625F);
                super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
                break;
            case 2:
                this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
                super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
                this.func_149676_a(0.25F, 0.375F, 0.25F, 0.75F, 0.625F, 1.0F);
                super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
                break;
            case 3:
                this.func_149676_a(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
                super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
                this.func_149676_a(0.25F, 0.375F, 0.0F, 0.75F, 0.625F, 0.75F);
                super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
                break;
            case 4:
                this.func_149676_a(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
                super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
                this.func_149676_a(0.375F, 0.25F, 0.25F, 0.625F, 0.75F, 1.0F);
                super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
                break;
            case 5:
                this.func_149676_a(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
                this.func_149676_a(0.0F, 0.375F, 0.25F, 0.75F, 0.625F, 0.75F);
                super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
        }

        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        int l = iblockaccess.func_72805_g(i, j, k);
        float f = 0.25F;

        switch (func_150085_b(l)) {
            case 0:
                this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
                break;
            case 1:
                this.func_149676_a(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
                break;
            case 2:
                this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
                break;
            case 3:
                this.func_149676_a(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
                break;
            case 4:
                this.func_149676_a(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
                break;
            case 5:
                this.func_149676_a(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }

    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        int l = func_150085_b(world.func_72805_g(i, j, k));
        Block block1 = world.func_147439_a(i - Facing.field_71586_b[l], j - Facing.field_71587_c[l], k - Facing.field_71585_d[l]);

        if (block1 != Blocks.field_150331_J && block1 != Blocks.field_150320_F) {
            world.func_147468_f(i, j, k);
        } else {
            block1.func_149695_a(world, i - Facing.field_71586_b[l], j - Facing.field_71587_c[l], k - Facing.field_71585_d[l], block);
        }

    }

    public static int func_150085_b(int i) {
        return MathHelper.func_76125_a(i & 7, 0, Facing.field_71586_b.length - 1);
    }
}
