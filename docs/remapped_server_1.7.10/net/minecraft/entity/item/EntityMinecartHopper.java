package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityMinecartHopper extends EntityMinecartContainer implements IHopper {

    private boolean field_96113_a = true;
    private int field_98044_b = -1;

    public EntityMinecartHopper(World world) {
        super(world);
    }

    public EntityMinecartHopper(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    @Override
    public int func_94087_l() {
        return 5;
    }

    @Override
    public Block func_145817_o() {
        return Blocks.field_150438_bZ;
    }

    @Override
    public int func_94085_r() {
        return 1;
    }

    @Override
    public int func_70302_i_() {
        return 5;
    }

    @Override
    public boolean func_130002_c(EntityPlayer entityplayer) {
        if (!this.field_70170_p.field_72995_K) {
            entityplayer.func_96125_a(this);
        }

        return true;
    }

    @Override
    public void func_96095_a(int i, int j, int k, boolean flag) {
        boolean flag1 = !flag;

        if (flag1 != this.func_96111_ay()) {
            this.func_96110_f(flag1);
        }

    }

    public boolean func_96111_ay() {
        return this.field_96113_a;
    }

    public void func_96110_f(boolean flag) {
        this.field_96113_a = flag;
    }

    @Override
    public World func_145831_w() {
        return this.field_70170_p;
    }

    @Override
    public double func_96107_aA() {
        return this.field_70165_t;
    }

    @Override
    public double func_96109_aB() {
        return this.field_70163_u;
    }

    @Override
    public double func_96108_aC() {
        return this.field_70161_v;
    }

    @Override
    public void func_70071_h_() {
        super.func_70071_h_();
        if (!this.field_70170_p.field_72995_K && this.func_70089_S() && this.func_96111_ay()) {
            --this.field_98044_b;
            if (!this.func_98043_aE()) {
                this.func_98042_n(0);
                if (this.func_96112_aD()) {
                    this.func_98042_n(4);
                    this.func_70296_d();
                }
            }
        }

    }

    public boolean func_96112_aD() {
        if (TileEntityHopper.func_145891_a(this)) {
            return true;
        } else {
            List list = this.field_70170_p.func_82733_a(EntityItem.class, this.field_70121_D.func_72314_b(0.25D, 0.0D, 0.25D), IEntitySelector.field_94557_a);

            if (list.size() > 0) {
                TileEntityHopper.func_145898_a(this, (EntityItem) list.get(0));
            }

            return false;
        }
    }

    @Override
    public void func_94095_a(DamageSource damagesource) {
        super.func_94095_a(damagesource);
        this.func_145778_a(Item.func_150898_a(Blocks.field_150438_bZ), 1, 0.0F);
    }

    @Override
    protected void func_70014_b(NBTTagCompound nbttagcompound) {
        super.func_70014_b(nbttagcompound);
        nbttagcompound.func_74768_a("TransferCooldown", this.field_98044_b);
    }

    @Override
    protected void func_70037_a(NBTTagCompound nbttagcompound) {
        super.func_70037_a(nbttagcompound);
        this.field_98044_b = nbttagcompound.func_74762_e("TransferCooldown");
    }

    public void func_98042_n(int i) {
        this.field_98044_b = i;
    }

    public boolean func_98043_aE() {
        return this.field_98044_b > 0;
    }
}
