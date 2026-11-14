package net.minecraft.entity.projectile;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityPotion extends EntityThrowable {

    private ItemStack field_70197_d;

    public EntityPotion(World world) {
        super(world);
    }

    public EntityPotion(World world, EntityLivingBase entitylivingbase, int i) {
        this(world, entitylivingbase, new ItemStack(Items.field_151068_bn, 1, i));
    }

    public EntityPotion(World world, EntityLivingBase entitylivingbase, ItemStack itemstack) {
        super(world, entitylivingbase);
        this.field_70197_d = itemstack;
    }

    public EntityPotion(World world, double d0, double d1, double d2, ItemStack itemstack) {
        super(world, d0, d1, d2);
        this.field_70197_d = itemstack;
    }

    @Override
    protected float func_70185_h() {
        return 0.05F;
    }

    @Override
    protected float func_70182_d() {
        return 0.5F;
    }

    @Override
    protected float func_70183_g() {
        return -20.0F;
    }

    public void func_82340_a(int i) {
        if (this.field_70197_d == null) {
            this.field_70197_d = new ItemStack(Items.field_151068_bn, 1, 0);
        }

        this.field_70197_d.func_77964_b(i);
    }

    public int func_70196_i() {
        if (this.field_70197_d == null) {
            this.field_70197_d = new ItemStack(Items.field_151068_bn, 1, 0);
        }

        return this.field_70197_d.func_77960_j();
    }

    @Override
    protected void func_70184_a(MovingObjectPosition movingobjectposition) {
        if (!this.field_70170_p.field_72995_K) {
            List list = Items.field_151068_bn.func_77832_l(this.field_70197_d);

            if (list != null && !list.isEmpty()) {
                AxisAlignedBB axisalignedbb = this.field_70121_D.func_72314_b(4.0D, 2.0D, 4.0D);
                List list1 = this.field_70170_p.func_72872_a(EntityLivingBase.class, axisalignedbb);

                if (list1 != null && !list1.isEmpty()) {
                    Iterator iterator = list1.iterator();

                    while (iterator.hasNext()) {
                        EntityLivingBase entitylivingbase = (EntityLivingBase) iterator.next();
                        double d0 = this.func_70068_e(entitylivingbase);

                        if (d0 < 16.0D) {
                            double d1 = 1.0D - Math.sqrt(d0) / 4.0D;

                            if (entitylivingbase == movingobjectposition.field_72308_g) {
                                d1 = 1.0D;
                            }

                            Iterator iterator1 = list.iterator();

                            while (iterator1.hasNext()) {
                                PotionEffect potioneffect = (PotionEffect) iterator1.next();
                                int i = potioneffect.func_76456_a();

                                if (Potion.field_76425_a[i].func_76403_b()) {
                                    Potion.field_76425_a[i].func_76402_a(this.func_85052_h(), entitylivingbase, potioneffect.func_76458_c(), d1);
                                } else {
                                    int j = (int) (d1 * (double) potioneffect.func_76459_b() + 0.5D);

                                    if (j > 20) {
                                        entitylivingbase.func_70690_d(new PotionEffect(i, j, potioneffect.func_76458_c()));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            this.field_70170_p.func_72926_e(2002, (int) Math.round(this.field_70165_t), (int) Math.round(this.field_70163_u), (int) Math.round(this.field_70161_v), this.func_70196_i());
            this.func_70106_y();
        }

    }

    @Override
    public void func_70037_a(NBTTagCompound nbttagcompound) {
        super.func_70037_a(nbttagcompound);
        if (nbttagcompound.func_150297_b("Potion", 10)) {
            this.field_70197_d = ItemStack.func_77949_a(nbttagcompound.func_74775_l("Potion"));
        } else {
            this.func_82340_a(nbttagcompound.func_74762_e("potionValue"));
        }

        if (this.field_70197_d == null) {
            this.func_70106_y();
        }

    }

    @Override
    public void func_70014_b(NBTTagCompound nbttagcompound) {
        super.func_70014_b(nbttagcompound);
        if (this.field_70197_d != null) {
            nbttagcompound.func_74782_a("Potion", this.field_70197_d.func_77955_b(new NBTTagCompound()));
        }

    }
}
