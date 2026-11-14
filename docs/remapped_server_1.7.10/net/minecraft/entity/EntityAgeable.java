package net.minecraft.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class EntityAgeable extends EntityCreature {

    private float field_98056_d = -1.0F;
    private float field_98057_e;

    public EntityAgeable(World world) {
        super(world);
    }

    public abstract EntityAgeable func_90011_a(EntityAgeable entityageable);

    @Override
    public boolean func_70085_c(EntityPlayer entityplayer) {
        ItemStack itemstack = entityplayer.field_71071_by.func_70448_g();

        if (itemstack != null && itemstack.func_77973_b() == Items.field_151063_bx) {
            if (!this.field_70170_p.field_72995_K) {
                Class oclass = EntityList.func_90035_a(itemstack.func_77960_j());

                if (oclass != null && oclass.isAssignableFrom(this.getClass())) {
                    EntityAgeable entityageable = this.func_90011_a(this);

                    if (entityageable != null) {
                        entityageable.func_70873_a(-24000);
                        entityageable.func_70012_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, 0.0F, 0.0F);
                        this.field_70170_p.func_72838_d(entityageable);
                        if (itemstack.func_82837_s()) {
                            entityageable.func_94058_c(itemstack.func_82833_r());
                        }

                        if (!entityplayer.field_71075_bZ.field_75098_d) {
                            --itemstack.field_77994_a;
                            if (itemstack.field_77994_a <= 0) {
                                entityplayer.field_71071_by.func_70299_a(entityplayer.field_71071_by.field_70461_c, (ItemStack) null);
                            }
                        }
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        this.field_70180_af.func_75682_a(12, new Integer(0));
    }

    public int func_70874_b() {
        return this.field_70180_af.func_75679_c(12);
    }

    public void func_110195_a(int i) {
        int j = this.func_70874_b();

        j += i * 20;
        if (j > 0) {
            j = 0;
        }

        this.func_70873_a(j);
    }

    public void func_70873_a(int i) {
        this.field_70180_af.func_75692_b(12, i);
        this.func_98054_a(this.func_70631_g_());
    }

    @Override
    public void func_70014_b(NBTTagCompound nbttagcompound) {
        super.func_70014_b(nbttagcompound);
        nbttagcompound.func_74768_a("Age", this.func_70874_b());
    }

    @Override
    public void func_70037_a(NBTTagCompound nbttagcompound) {
        super.func_70037_a(nbttagcompound);
        this.func_70873_a(nbttagcompound.func_74762_e("Age"));
    }

    @Override
    public void func_70636_d() {
        super.func_70636_d();
        if (this.field_70170_p.field_72995_K) {
            this.func_98054_a(this.func_70631_g_());
        } else {
            int i = this.func_70874_b();

            if (i < 0) {
                ++i;
                this.func_70873_a(i);
            } else if (i > 0) {
                --i;
                this.func_70873_a(i);
            }
        }

    }

    @Override
    public boolean func_70631_g_() {
        return this.func_70874_b() < 0;
    }

    public void func_98054_a(boolean flag) {
        this.func_98055_j(flag ? 0.5F : 1.0F);
    }

    @Override
    protected final void func_70105_a(float f, float f1) {
        boolean flag = this.field_98056_d > 0.0F;

        this.field_98056_d = f;
        this.field_98057_e = f1;
        if (!flag) {
            this.func_98055_j(1.0F);
        }

    }

    protected final void func_98055_j(float f) {
        super.func_70105_a(this.field_98056_d * f, this.field_98057_e * f);
    }
}
