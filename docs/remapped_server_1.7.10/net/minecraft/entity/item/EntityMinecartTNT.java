package net.minecraft.entity.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityMinecartTNT extends EntityMinecart {

    private int field_94106_a = -1;

    public EntityMinecartTNT(World world) {
        super(world);
    }

    public EntityMinecartTNT(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    @Override
    public int func_94087_l() {
        return 3;
    }

    @Override
    public Block func_145817_o() {
        return Blocks.field_150335_W;
    }

    @Override
    public void func_70071_h_() {
        super.func_70071_h_();
        if (this.field_94106_a > 0) {
            --this.field_94106_a;
            this.field_70170_p.func_72869_a("smoke", this.field_70165_t, this.field_70163_u + 0.5D, this.field_70161_v, 0.0D, 0.0D, 0.0D);
        } else if (this.field_94106_a == 0) {
            this.func_94103_c(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
        }

        if (this.field_70123_F) {
            double d0 = this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y;

            if (d0 >= 0.009999999776482582D) {
                this.func_94103_c(d0);
            }
        }

    }

    @Override
    public void func_94095_a(DamageSource damagesource) {
        super.func_94095_a(damagesource);
        double d0 = this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y;

        if (!damagesource.func_94541_c()) {
            this.func_70099_a(new ItemStack(Blocks.field_150335_W, 1), 0.0F);
        }

        if (damagesource.func_76347_k() || damagesource.func_94541_c() || d0 >= 0.009999999776482582D) {
            this.func_94103_c(d0);
        }

    }

    protected void func_94103_c(double d0) {
        if (!this.field_70170_p.field_72995_K) {
            double d1 = Math.sqrt(d0);

            if (d1 > 5.0D) {
                d1 = 5.0D;
            }

            this.field_70170_p.func_72876_a(this, this.field_70165_t, this.field_70163_u, this.field_70161_v, (float) (4.0D + this.field_70146_Z.nextDouble() * 1.5D * d1), true);
            this.func_70106_y();
        }

    }

    @Override
    protected void func_70069_a(float f) {
        if (f >= 3.0F) {
            float f1 = f / 10.0F;

            this.func_94103_c((double) (f1 * f1));
        }

        super.func_70069_a(f);
    }

    @Override
    public void func_96095_a(int i, int j, int k, boolean flag) {
        if (flag && this.field_94106_a < 0) {
            this.func_94105_c();
        }

    }

    public void func_94105_c() {
        this.field_94106_a = 80;
        if (!this.field_70170_p.field_72995_K) {
            this.field_70170_p.func_72960_a(this, (byte) 10);
            this.field_70170_p.func_72956_a(this, "game.tnt.primed", 1.0F, 1.0F);
        }

    }

    public boolean func_96096_ay() {
        return this.field_94106_a > -1;
    }

    @Override
    public float func_145772_a(Explosion explosion, World world, int i, int j, int k, Block block) {
        return this.func_96096_ay() && (BlockRailBase.func_150051_a(block) || BlockRailBase.func_150049_b_(world, i, j + 1, k)) ? 0.0F : super.func_145772_a(explosion, world, i, j, k, block);
    }

    @Override
    public boolean func_145774_a(Explosion explosion, World world, int i, int j, int k, Block block, float f) {
        return this.func_96096_ay() && (BlockRailBase.func_150051_a(block) || BlockRailBase.func_150049_b_(world, i, j + 1, k)) ? false : super.func_145774_a(explosion, world, i, j, k, block, f);
    }

    @Override
    protected void func_70037_a(NBTTagCompound nbttagcompound) {
        super.func_70037_a(nbttagcompound);
        if (nbttagcompound.func_150297_b("TNTFuse", 99)) {
            this.field_94106_a = nbttagcompound.func_74762_e("TNTFuse");
        }

    }

    @Override
    protected void func_70014_b(NBTTagCompound nbttagcompound) {
        super.func_70014_b(nbttagcompound);
        nbttagcompound.func_74768_a("TNTFuse", this.field_94106_a);
    }
}
