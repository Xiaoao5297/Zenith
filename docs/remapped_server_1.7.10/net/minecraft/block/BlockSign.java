package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSign extends BlockContainer {

    private Class field_149968_a;
    private boolean field_149967_b;

    protected BlockSign(Class oclass, boolean flag) {
        super(Material.field_151575_d);
        this.field_149967_b = flag;
        this.field_149968_a = oclass;
        float f = 0.25F;
        float f1 = 1.0F;

        this.func_149676_a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        return null;
    }

    @Override
    public void func_149719_a(IBlockAccess iblockaccess, int i, int j, int k) {
        if (!this.field_149967_b) {
            int l = iblockaccess.func_72805_g(i, j, k);
            float f = 0.28125F;
            float f1 = 0.78125F;
            float f2 = 0.0F;
            float f3 = 1.0F;
            float f4 = 0.125F;

            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            if (l == 2) {
                this.func_149676_a(f2, f, 1.0F - f4, f3, f1, 1.0F);
            }

            if (l == 3) {
                this.func_149676_a(f2, f, 0.0F, f3, f1, f4);
            }

            if (l == 4) {
                this.func_149676_a(1.0F - f4, f, f2, 1.0F, f1, f3);
            }

            if (l == 5) {
                this.func_149676_a(0.0F, f, f2, f4, f1, f3);
            }

        }
    }

    @Override
    public int func_149645_b() {
        return -1;
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    @Override
    public boolean func_149655_b(IBlockAccess iblockaccess, int i, int j, int k) {
        return true;
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }

    @Override
    public TileEntity func_149915_a(World world, int i) {
        try {
            return (TileEntity) this.field_149968_a.newInstance();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Items.field_151155_ap;
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        boolean flag = false;

        if (this.field_149967_b) {
            if (!world.func_147439_a(i, j - 1, k).func_149688_o().func_76220_a()) {
                flag = true;
            }
        } else {
            int l = world.func_72805_g(i, j, k);

            flag = true;
            if (l == 2 && world.func_147439_a(i, j, k + 1).func_149688_o().func_76220_a()) {
                flag = false;
            }

            if (l == 3 && world.func_147439_a(i, j, k - 1).func_149688_o().func_76220_a()) {
                flag = false;
            }

            if (l == 4 && world.func_147439_a(i + 1, j, k).func_149688_o().func_76220_a()) {
                flag = false;
            }

            if (l == 5 && world.func_147439_a(i - 1, j, k).func_149688_o().func_76220_a()) {
                flag = false;
            }
        }

        if (flag) {
            this.func_149697_b(world, i, j, k, world.func_72805_g(i, j, k), 0);
            world.func_147468_f(i, j, k);
        }

        super.func_149695_a(world, i, j, k, block);
    }
}
