package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockEndPortal extends BlockContainer {

    public static boolean field_149948_a;

    protected BlockEndPortal(Material material) {
        super(material);
        this.func_149715_a(1.0F);
    }

    @Override
    public TileEntity func_149915_a(World world, int i) {
        return new TileEntityEndPortal();
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        float f = 0.0625F;

        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
    }

    @Override
    public void func_149743_a(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, List list, Entity entity) {}

    @Override
    public boolean func_149662_c() {
        return false;
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    @Override
    public int func_149745_a(Random random) {
        return 0;
    }

    @Override
    public void func_149670_a(World world, int i, int j, int k, Entity entity) {
        if (entity.field_70154_o == null && entity.field_70153_n == null && !world.field_72995_K) {
            entity.func_71027_c(1);
        }

    }

    @Override
    public int func_149645_b() {
        return -1;
    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {
        if (!BlockEndPortal.field_149948_a) {
            if (world.field_73011_w.field_76574_g != 0) {
                world.func_147468_f(i, j, k);
            }

        }
    }

    @Override
    public MapColor func_149728_f(int i) {
        return MapColor.field_151654_J;
    }
}
