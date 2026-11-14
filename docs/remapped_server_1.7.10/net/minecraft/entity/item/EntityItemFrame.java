package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class EntityItemFrame extends EntityHanging {

    private float field_82337_e = 1.0F;

    public EntityItemFrame(World world) {
        super(world);
    }

    public EntityItemFrame(World world, int i, int j, int k, int l) {
        super(world, i, j, k, l);
        this.func_82328_a(l);
    }

    @Override
    protected void func_70088_a() {
        this.func_70096_w().func_82709_a(2, 5);
        this.func_70096_w().func_75682_a(3, (byte) 0);
    }

    @Override
    public boolean func_70097_a(DamageSource damagesource, float f) {
        if (this.func_85032_ar()) {
            return false;
        } else if (this.func_82335_i() != null) {
            if (!this.field_70170_p.field_72995_K) {
                this.func_146065_b(damagesource.func_76346_g(), false);
                this.func_82334_a((ItemStack) null);
            }

            return true;
        } else {
            return super.func_70097_a(damagesource, f);
        }
    }

    @Override
    public int func_82329_d() {
        return 9;
    }

    @Override
    public int func_82330_g() {
        return 9;
    }

    @Override
    public void func_110128_b(Entity entity) {
        this.func_146065_b(entity, true);
    }

    public void func_146065_b(Entity entity, boolean flag) {
        ItemStack itemstack = this.func_82335_i();

        if (entity instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entity;

            if (entityplayer.field_71075_bZ.field_75098_d) {
                this.func_110131_b(itemstack);
                return;
            }
        }

        if (flag) {
            this.func_70099_a(new ItemStack(Items.field_151160_bD), 0.0F);
        }

        if (itemstack != null && this.field_70146_Z.nextFloat() < this.field_82337_e) {
            itemstack = itemstack.func_77946_l();
            this.func_110131_b(itemstack);
            this.func_70099_a(itemstack, 0.0F);
        }

    }

    private void func_110131_b(ItemStack itemstack) {
        if (itemstack != null) {
            if (itemstack.func_77973_b() == Items.field_151098_aY) {
                MapData mapdata = ((ItemMap) itemstack.func_77973_b()).func_77873_a(itemstack, this.field_70170_p);

                mapdata.field_76203_h.remove("frame-" + this.func_145782_y());
            }

            itemstack.func_82842_a((EntityItemFrame) null);
        }
    }

    public ItemStack func_82335_i() {
        return this.func_70096_w().func_82710_f(2);
    }

    public void func_82334_a(ItemStack itemstack) {
        if (itemstack != null) {
            itemstack = itemstack.func_77946_l();
            itemstack.field_77994_a = 1;
            itemstack.func_82842_a(this);
        }

        this.func_70096_w().func_75692_b(2, itemstack);
        this.func_70096_w().func_82708_h(2);
    }

    public int func_82333_j() {
        return this.func_70096_w().func_75683_a(3);
    }

    public void func_82336_g(int i) {
        this.func_70096_w().func_75692_b(3, (byte) (i % 4));
    }

    @Override
    public void func_70014_b(NBTTagCompound nbttagcompound) {
        if (this.func_82335_i() != null) {
            nbttagcompound.func_74782_a("Item", this.func_82335_i().func_77955_b(new NBTTagCompound()));
            nbttagcompound.func_74774_a("ItemRotation", (byte) this.func_82333_j());
            nbttagcompound.func_74776_a("ItemDropChance", this.field_82337_e);
        }

        super.func_70014_b(nbttagcompound);
    }

    @Override
    public void func_70037_a(NBTTagCompound nbttagcompound) {
        NBTTagCompound nbttagcompound1 = nbttagcompound.func_74775_l("Item");

        if (nbttagcompound1 != null && !nbttagcompound1.func_82582_d()) {
            this.func_82334_a(ItemStack.func_77949_a(nbttagcompound1));
            this.func_82336_g(nbttagcompound.func_74771_c("ItemRotation"));
            if (nbttagcompound.func_150297_b("ItemDropChance", 99)) {
                this.field_82337_e = nbttagcompound.func_74760_g("ItemDropChance");
            }
        }

        super.func_70037_a(nbttagcompound);
    }

    @Override
    public boolean func_130002_c(EntityPlayer entityplayer) {
        if (this.func_82335_i() == null) {
            ItemStack itemstack = entityplayer.func_70694_bm();

            if (itemstack != null && !this.field_70170_p.field_72995_K) {
                this.func_82334_a(itemstack);
                if (!entityplayer.field_71075_bZ.field_75098_d && --itemstack.field_77994_a <= 0) {
                    entityplayer.field_71071_by.func_70299_a(entityplayer.field_71071_by.field_70461_c, (ItemStack) null);
                }
            }
        } else if (!this.field_70170_p.field_72995_K) {
            this.func_82336_g(this.func_82333_j() + 1);
        }

        return true;
    }
}
