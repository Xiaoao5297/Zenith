package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRailDetector extends BlockRailBase {

    public BlockRailDetector() {
        super(true);
        this.func_149675_a(true);
    }

    @Override
    public int func_149738_a(World world) {
        return 20;
    }

    @Override
    public boolean func_149744_f() {
        return true;
    }

    @Override
    public void func_149670_a(World world, int i, int j, int k, Entity entity) {
        if (!world.field_72995_K) {
            int l = world.func_72805_g(i, j, k);

            if ((l & 8) == 0) {
                this.func_150054_a(world, i, j, k, l);
            }
        }
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        if (!world.field_72995_K) {
            int l = world.func_72805_g(i, j, k);

            if ((l & 8) != 0) {
                this.func_150054_a(world, i, j, k, l);
            }
        }
    }

    @Override
    public int func_149709_b(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return (iblockaccess.func_72805_g(i, j, k) & 8) != 0 ? 15 : 0;
    }

    @Override
    public int func_149748_c(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return (iblockaccess.func_72805_g(i, j, k) & 8) == 0 ? 0 : (l == 1 ? 15 : 0);
    }

    private void func_150054_a(World world, int i, int j, int k, int l) {
        boolean flag = (l & 8) != 0;
        boolean flag1 = false;
        float f = 0.125F;
        List list = world.func_72872_a(EntityMinecart.class, AxisAlignedBB.func_72330_a((double) ((float) i + f), (double) j, (double) ((float) k + f), (double) ((float) (i + 1) - f), (double) ((float) (j + 1) - f), (double) ((float) (k + 1) - f)));

        if (!list.isEmpty()) {
            flag1 = true;
        }

        if (flag1 && !flag) {
            world.func_72921_c(i, j, k, l | 8, 3);
            world.func_147459_d(i, j, k, this);
            world.func_147459_d(i, j - 1, k, this);
            world.func_147458_c(i, j, k, i, j, k);
        }

        if (!flag1 && flag) {
            world.func_72921_c(i, j, k, l & 7, 3);
            world.func_147459_d(i, j, k, this);
            world.func_147459_d(i, j - 1, k, this);
            world.func_147458_c(i, j, k, i, j, k);
        }

        if (flag1) {
            world.func_147464_a(i, j, k, this, this.func_149738_a(world));
        }

        world.func_147453_f(i, j, k, this);
    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {
        super.func_149726_b(world, i, j, k);
        this.func_150054_a(world, i, j, k, world.func_72805_g(i, j, k));
    }

    @Override
    public boolean func_149740_M() {
        return true;
    }

    @Override
    public int func_149736_g(World world, int i, int j, int k, int l) {
        if ((world.func_72805_g(i, j, k) & 8) > 0) {
            float f = 0.125F;
            List list = world.func_72872_a(EntityMinecartCommandBlock.class, AxisAlignedBB.func_72330_a((double) ((float) i + f), (double) j, (double) ((float) k + f), (double) ((float) (i + 1) - f), (double) ((float) (j + 1) - f), (double) ((float) (k + 1) - f)));

            if (list.size() > 0) {
                return ((EntityMinecartCommandBlock) list.get(0)).func_145822_e().func_145760_g();
            }

            List list1 = world.func_82733_a(EntityMinecart.class, AxisAlignedBB.func_72330_a((double) ((float) i + f), (double) j, (double) ((float) k + f), (double) ((float) (i + 1) - f), (double) ((float) (j + 1) - f), (double) ((float) (k + 1) - f)), IEntitySelector.field_96566_b);

            if (list1.size() > 0) {
                return Container.func_94526_b((IInventory) list1.get(0));
            }
        }

        return 0;
    }
}
