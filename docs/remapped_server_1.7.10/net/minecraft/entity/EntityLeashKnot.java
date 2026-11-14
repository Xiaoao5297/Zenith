package net.minecraft.entity;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityLeashKnot extends EntityHanging {

    public EntityLeashKnot(World world) {
        super(world);
    }

    public EntityLeashKnot(World world, int i, int j, int k) {
        super(world, i, j, k, 0);
        this.func_70107_b((double) i + 0.5D, (double) j + 0.5D, (double) k + 0.5D);
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
    }

    @Override
    public void func_82328_a(int i) {}

    @Override
    public int func_82329_d() {
        return 9;
    }

    @Override
    public int func_82330_g() {
        return 9;
    }

    @Override
    public void func_110128_b(Entity entity) {}

    @Override
    public boolean func_70039_c(NBTTagCompound nbttagcompound) {
        return false;
    }

    @Override
    public void func_70014_b(NBTTagCompound nbttagcompound) {}

    @Override
    public void func_70037_a(NBTTagCompound nbttagcompound) {}

    @Override
    public boolean func_130002_c(EntityPlayer entityplayer) {
        ItemStack itemstack = entityplayer.func_70694_bm();
        boolean flag = false;
        double d0;
        List list;
        Iterator iterator;
        EntityLiving entityliving;

        if (itemstack != null && itemstack.func_77973_b() == Items.field_151058_ca && !this.field_70170_p.field_72995_K) {
            d0 = 7.0D;
            list = this.field_70170_p.func_72872_a(EntityLiving.class, AxisAlignedBB.func_72330_a(this.field_70165_t - d0, this.field_70163_u - d0, this.field_70161_v - d0, this.field_70165_t + d0, this.field_70163_u + d0, this.field_70161_v + d0));
            if (list != null) {
                iterator = list.iterator();

                while (iterator.hasNext()) {
                    entityliving = (EntityLiving) iterator.next();
                    if (entityliving.func_110167_bD() && entityliving.func_110166_bE() == entityplayer) {
                        entityliving.func_110162_b(this, true);
                        flag = true;
                    }
                }
            }
        }

        if (!this.field_70170_p.field_72995_K && !flag) {
            this.func_70106_y();
            if (entityplayer.field_71075_bZ.field_75098_d) {
                d0 = 7.0D;
                list = this.field_70170_p.func_72872_a(EntityLiving.class, AxisAlignedBB.func_72330_a(this.field_70165_t - d0, this.field_70163_u - d0, this.field_70161_v - d0, this.field_70165_t + d0, this.field_70163_u + d0, this.field_70161_v + d0));
                if (list != null) {
                    iterator = list.iterator();

                    while (iterator.hasNext()) {
                        entityliving = (EntityLiving) iterator.next();
                        if (entityliving.func_110167_bD() && entityliving.func_110166_bE() == this) {
                            entityliving.func_110160_i(true, false);
                        }
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean func_70518_d() {
        return this.field_70170_p.func_147439_a(this.field_146063_b, this.field_146064_c, this.field_146062_d).func_149645_b() == 11;
    }

    public static EntityLeashKnot func_110129_a(World world, int i, int j, int k) {
        EntityLeashKnot entityleashknot = new EntityLeashKnot(world, i, j, k);

        entityleashknot.field_98038_p = true;
        world.func_72838_d(entityleashknot);
        return entityleashknot;
    }

    public static EntityLeashKnot func_110130_b(World world, int i, int j, int k) {
        List list = world.func_72872_a(EntityLeashKnot.class, AxisAlignedBB.func_72330_a((double) i - 1.0D, (double) j - 1.0D, (double) k - 1.0D, (double) i + 1.0D, (double) j + 1.0D, (double) k + 1.0D));

        if (list != null) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityLeashKnot entityleashknot = (EntityLeashKnot) iterator.next();

                if (entityleashknot.field_146063_b == i && entityleashknot.field_146064_c == j && entityleashknot.field_146062_d == k) {
                    return entityleashknot;
                }
            }
        }

        return null;
    }
}
