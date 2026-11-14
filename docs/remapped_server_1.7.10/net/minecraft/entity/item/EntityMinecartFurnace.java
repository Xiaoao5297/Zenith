package net.minecraft.entity.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityMinecartFurnace extends EntityMinecart {

    private int field_94110_c;
    public double field_94111_a;
    public double field_94109_b;

    public EntityMinecartFurnace(World world) {
        super(world);
    }

    public EntityMinecartFurnace(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    @Override
    public int func_94087_l() {
        return 2;
    }

    @Override
    protected void func_70088_a() {
        super.func_70088_a();
        this.field_70180_af.func_75682_a(16, new Byte((byte) 0));
    }

    @Override
    public void func_70071_h_() {
        super.func_70071_h_();
        if (this.field_94110_c > 0) {
            --this.field_94110_c;
        }

        if (this.field_94110_c <= 0) {
            this.field_94111_a = this.field_94109_b = 0.0D;
        }

        this.func_94107_f(this.field_94110_c > 0);
        if (this.func_94108_c() && this.field_70146_Z.nextInt(4) == 0) {
            this.field_70170_p.func_72869_a("largesmoke", this.field_70165_t, this.field_70163_u + 0.8D, this.field_70161_v, 0.0D, 0.0D, 0.0D);
        }

    }

    @Override
    public void func_94095_a(DamageSource damagesource) {
        super.func_94095_a(damagesource);
        if (!damagesource.func_94541_c()) {
            this.func_70099_a(new ItemStack(Blocks.field_150460_al, 1), 0.0F);
        }

    }

    @Override
    protected void func_145821_a(int i, int j, int k, double d0, double d1, Block block, int l) {
        super.func_145821_a(i, j, k, d0, d1, block, l);
        double d2 = this.field_94111_a * this.field_94111_a + this.field_94109_b * this.field_94109_b;

        if (d2 > 1.0E-4D && this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y > 0.001D) {
            d2 = (double) MathHelper.func_76133_a(d2);
            this.field_94111_a /= d2;
            this.field_94109_b /= d2;
            if (this.field_94111_a * this.field_70159_w + this.field_94109_b * this.field_70179_y < 0.0D) {
                this.field_94111_a = 0.0D;
                this.field_94109_b = 0.0D;
            } else {
                this.field_94111_a = this.field_70159_w;
                this.field_94109_b = this.field_70179_y;
            }
        }

    }

    @Override
    protected void func_94101_h() {
        double d0 = this.field_94111_a * this.field_94111_a + this.field_94109_b * this.field_94109_b;

        if (d0 > 1.0E-4D) {
            d0 = (double) MathHelper.func_76133_a(d0);
            this.field_94111_a /= d0;
            this.field_94109_b /= d0;
            double d1 = 0.05D;

            this.field_70159_w *= 0.800000011920929D;
            this.field_70181_x *= 0.0D;
            this.field_70179_y *= 0.800000011920929D;
            this.field_70159_w += this.field_94111_a * d1;
            this.field_70179_y += this.field_94109_b * d1;
        } else {
            this.field_70159_w *= 0.9800000190734863D;
            this.field_70181_x *= 0.0D;
            this.field_70179_y *= 0.9800000190734863D;
        }

        super.func_94101_h();
    }

    @Override
    public boolean func_130002_c(EntityPlayer entityplayer) {
        ItemStack itemstack = entityplayer.field_71071_by.func_70448_g();

        if (itemstack != null && itemstack.func_77973_b() == Items.field_151044_h) {
            if (!entityplayer.field_71075_bZ.field_75098_d && --itemstack.field_77994_a == 0) {
                entityplayer.field_71071_by.func_70299_a(entityplayer.field_71071_by.field_70461_c, (ItemStack) null);
            }

            this.field_94110_c += 3600;
        }

        this.field_94111_a = this.field_70165_t - entityplayer.field_70165_t;
        this.field_94109_b = this.field_70161_v - entityplayer.field_70161_v;
        return true;
    }

    @Override
    protected void func_70014_b(NBTTagCompound nbttagcompound) {
        super.func_70014_b(nbttagcompound);
        nbttagcompound.func_74780_a("PushX", this.field_94111_a);
        nbttagcompound.func_74780_a("PushZ", this.field_94109_b);
        nbttagcompound.func_74777_a("Fuel", (short) this.field_94110_c);
    }

    @Override
    protected void func_70037_a(NBTTagCompound nbttagcompound) {
        super.func_70037_a(nbttagcompound);
        this.field_94111_a = nbttagcompound.func_74769_h("PushX");
        this.field_94109_b = nbttagcompound.func_74769_h("PushZ");
        this.field_94110_c = nbttagcompound.func_74765_d("Fuel");
    }

    protected boolean func_94108_c() {
        return (this.field_70180_af.func_75683_a(16) & 1) != 0;
    }

    protected void func_94107_f(boolean flag) {
        if (flag) {
            this.field_70180_af.func_75692_b(16, (byte) (this.field_70180_af.func_75683_a(16) | 1));
        } else {
            this.field_70180_af.func_75692_b(16, (byte) (this.field_70180_af.func_75683_a(16) & -2));
        }

    }

    @Override
    public Block func_145817_o() {
        return Blocks.field_150470_am;
    }

    @Override
    public int func_94097_p() {
        return 2;
    }
}
