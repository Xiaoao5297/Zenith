package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFireworkRocket extends Entity {

    private int field_92056_a;
    private int field_92055_b;

    public EntityFireworkRocket(World world) {
        super(world);
        this.func_70105_a(0.25F, 0.25F);
    }

    @Override
    protected void func_70088_a() {
        this.field_70180_af.func_82709_a(8, 5);
    }

    public EntityFireworkRocket(World world, double d0, double d1, double d2, ItemStack itemstack) {
        super(world);
        this.field_92056_a = 0;
        this.func_70105_a(0.25F, 0.25F);
        this.func_70107_b(d0, d1, d2);
        this.field_70129_M = 0.0F;
        int i = 1;

        if (itemstack != null && itemstack.func_77942_o()) {
            this.field_70180_af.func_75692_b(8, itemstack);
            NBTTagCompound nbttagcompound = itemstack.func_77978_p();
            NBTTagCompound nbttagcompound1 = nbttagcompound.func_74775_l("Fireworks");

            if (nbttagcompound1 != null) {
                i += nbttagcompound1.func_74771_c("Flight");
            }
        }

        this.field_70159_w = this.field_70146_Z.nextGaussian() * 0.001D;
        this.field_70179_y = this.field_70146_Z.nextGaussian() * 0.001D;
        this.field_70181_x = 0.05D;
        this.field_92055_b = 10 * i + this.field_70146_Z.nextInt(6) + this.field_70146_Z.nextInt(7);
    }

    @Override
    public void func_70071_h_() {
        this.field_70142_S = this.field_70165_t;
        this.field_70137_T = this.field_70163_u;
        this.field_70136_U = this.field_70161_v;
        super.func_70071_h_();
        this.field_70159_w *= 1.15D;
        this.field_70179_y *= 1.15D;
        this.field_70181_x += 0.04D;
        this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
        float f = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);

        this.field_70177_z = (float) (Math.atan2(this.field_70159_w, this.field_70179_y) * 180.0D / 3.1415927410125732D);

        for (this.field_70125_A = (float) (Math.atan2(this.field_70181_x, (double) f) * 180.0D / 3.1415927410125732D); this.field_70125_A - this.field_70127_C < -180.0F; this.field_70127_C -= 360.0F) {
            ;
        }

        while (this.field_70125_A - this.field_70127_C >= 180.0F) {
            this.field_70127_C += 360.0F;
        }

        while (this.field_70177_z - this.field_70126_B < -180.0F) {
            this.field_70126_B -= 360.0F;
        }

        while (this.field_70177_z - this.field_70126_B >= 180.0F) {
            this.field_70126_B += 360.0F;
        }

        this.field_70125_A = this.field_70127_C + (this.field_70125_A - this.field_70127_C) * 0.2F;
        this.field_70177_z = this.field_70126_B + (this.field_70177_z - this.field_70126_B) * 0.2F;
        if (this.field_92056_a == 0) {
            this.field_70170_p.func_72956_a(this, "fireworks.launch", 3.0F, 1.0F);
        }

        ++this.field_92056_a;
        if (this.field_70170_p.field_72995_K && this.field_92056_a % 2 < 2) {
            this.field_70170_p.func_72869_a("fireworksSpark", this.field_70165_t, this.field_70163_u - 0.3D, this.field_70161_v, this.field_70146_Z.nextGaussian() * 0.05D, -this.field_70181_x * 0.5D, this.field_70146_Z.nextGaussian() * 0.05D);
        }

        if (!this.field_70170_p.field_72995_K && this.field_92056_a > this.field_92055_b) {
            this.field_70170_p.func_72960_a(this, (byte) 17);
            this.func_70106_y();
        }

    }

    @Override
    public void func_70014_b(NBTTagCompound nbttagcompound) {
        nbttagcompound.func_74768_a("Life", this.field_92056_a);
        nbttagcompound.func_74768_a("LifeTime", this.field_92055_b);
        ItemStack itemstack = this.field_70180_af.func_82710_f(8);

        if (itemstack != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            itemstack.func_77955_b(nbttagcompound1);
            nbttagcompound.func_74782_a("FireworksItem", nbttagcompound1);
        }

    }

    @Override
    public void func_70037_a(NBTTagCompound nbttagcompound) {
        this.field_92056_a = nbttagcompound.func_74762_e("Life");
        this.field_92055_b = nbttagcompound.func_74762_e("LifeTime");
        NBTTagCompound nbttagcompound1 = nbttagcompound.func_74775_l("FireworksItem");

        if (nbttagcompound1 != null) {
            ItemStack itemstack = ItemStack.func_77949_a(nbttagcompound1);

            if (itemstack != null) {
                this.field_70180_af.func_75692_b(8, itemstack);
            }
        }

    }

    @Override
    public float func_70013_c(float f) {
        return super.func_70013_c(f);
    }

    @Override
    public boolean func_70075_an() {
        return false;
    }
}
