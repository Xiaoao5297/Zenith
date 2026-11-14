package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockEndPortalFrame extends Block {

    public BlockEndPortalFrame() {
        super(Material.field_151576_e);
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }

    @Override
    public int func_149645_b() {
        return 26;
    }

    @Override
    public void func_149683_g() {
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.8125F, 1.0F);
    }

    @Override
    public void func_149743_a(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, List list, Entity entity) {
        this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.8125F, 1.0F);
        super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
        int l = world.func_72805_g(i, j, k);

        if (func_150020_b(l)) {
            this.func_149676_a(0.3125F, 0.8125F, 0.3125F, 0.6875F, 1.0F, 0.6875F);
            super.func_149743_a(world, i, j, k, axisalignedbb, list, entity);
        }

        this.func_149683_g();
    }

    public static boolean func_150020_b(int i) {
        return (i & 4) != 0;
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return null;
    }

    @Override
    public void func_149689_a(World world, int i, int j, int k, EntityLivingBase entitylivingbase, ItemStack itemstack) {
        int l = ((MathHelper.func_76128_c((double) (entitylivingbase.field_70177_z * 4.0F / 360.0F) + 0.5D) & 3) + 2) % 4;

        world.func_72921_c(i, j, k, l, 2);
    }

    @Override
    public boolean func_149740_M() {
        return true;
    }

    @Override
    public int func_149736_g(World world, int i, int j, int k, int l) {
        int i1 = world.func_72805_g(i, j, k);

        return func_150020_b(i1) ? 15 : 0;
    }
}
