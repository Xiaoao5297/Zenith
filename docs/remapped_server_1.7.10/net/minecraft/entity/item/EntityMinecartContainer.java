package net.minecraft.entity.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class EntityMinecartContainer extends EntityMinecart implements IInventory {

    private ItemStack[] field_94113_a = new ItemStack[36];
    private boolean field_94112_b = true;

    public EntityMinecartContainer(World world) {
        super(world);
    }

    public EntityMinecartContainer(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    @Override
    public void func_94095_a(DamageSource damagesource) {
        super.func_94095_a(damagesource);

        for (int i = 0; i < this.func_70302_i_(); ++i) {
            ItemStack itemstack = this.func_70301_a(i);

            if (itemstack != null) {
                float f = this.field_70146_Z.nextFloat() * 0.8F + 0.1F;
                float f1 = this.field_70146_Z.nextFloat() * 0.8F + 0.1F;
                float f2 = this.field_70146_Z.nextFloat() * 0.8F + 0.1F;

                while (itemstack.field_77994_a > 0) {
                    int j = this.field_70146_Z.nextInt(21) + 10;

                    if (j > itemstack.field_77994_a) {
                        j = itemstack.field_77994_a;
                    }

                    itemstack.field_77994_a -= j;
                    EntityItem entityitem = new EntityItem(this.field_70170_p, this.field_70165_t + (double) f, this.field_70163_u + (double) f1, this.field_70161_v + (double) f2, new ItemStack(itemstack.func_77973_b(), j, itemstack.func_77960_j()));
                    float f3 = 0.05F;

                    entityitem.field_70159_w = (double) ((float) this.field_70146_Z.nextGaussian() * f3);
                    entityitem.field_70181_x = (double) ((float) this.field_70146_Z.nextGaussian() * f3 + 0.2F);
                    entityitem.field_70179_y = (double) ((float) this.field_70146_Z.nextGaussian() * f3);
                    this.field_70170_p.func_72838_d(entityitem);
                }
            }
        }

    }

    @Override
    public ItemStack func_70301_a(int i) {
        return this.field_94113_a[i];
    }

    @Override
    public ItemStack func_70298_a(int i, int j) {
        if (this.field_94113_a[i] != null) {
            ItemStack itemstack;

            if (this.field_94113_a[i].field_77994_a <= j) {
                itemstack = this.field_94113_a[i];
                this.field_94113_a[i] = null;
                return itemstack;
            } else {
                itemstack = this.field_94113_a[i].func_77979_a(j);
                if (this.field_94113_a[i].field_77994_a == 0) {
                    this.field_94113_a[i] = null;
                }

                return itemstack;
            }
        } else {
            return null;
        }
    }

    @Override
    public ItemStack func_70304_b(int i) {
        if (this.field_94113_a[i] != null) {
            ItemStack itemstack = this.field_94113_a[i];

            this.field_94113_a[i] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public void func_70299_a(int i, ItemStack itemstack) {
        this.field_94113_a[i] = itemstack;
        if (itemstack != null && itemstack.field_77994_a > this.func_70297_j_()) {
            itemstack.field_77994_a = this.func_70297_j_();
        }

    }

    @Override
    public void func_70296_d() {}

    @Override
    public boolean func_70300_a(EntityPlayer entityplayer) {
        return this.field_70128_L ? false : entityplayer.func_70068_e(this) <= 64.0D;
    }

    @Override
    public void func_70295_k_() {}

    @Override
    public void func_70305_f() {}

    @Override
    public boolean func_94041_b(int i, ItemStack itemstack) {
        return true;
    }

    @Override
    public String func_145825_b() {
        return this.func_145818_k_() ? this.func_95999_t() : "container.minecart";
    }

    @Override
    public int func_70297_j_() {
        return 64;
    }

    @Override
    public void func_71027_c(int i) {
        this.field_94112_b = false;
        super.func_71027_c(i);
    }

    @Override
    public void func_70106_y() {
        if (this.field_94112_b) {
            for (int i = 0; i < this.func_70302_i_(); ++i) {
                ItemStack itemstack = this.func_70301_a(i);

                if (itemstack != null) {
                    float f = this.field_70146_Z.nextFloat() * 0.8F + 0.1F;
                    float f1 = this.field_70146_Z.nextFloat() * 0.8F + 0.1F;
                    float f2 = this.field_70146_Z.nextFloat() * 0.8F + 0.1F;

                    while (itemstack.field_77994_a > 0) {
                        int j = this.field_70146_Z.nextInt(21) + 10;

                        if (j > itemstack.field_77994_a) {
                            j = itemstack.field_77994_a;
                        }

                        itemstack.field_77994_a -= j;
                        EntityItem entityitem = new EntityItem(this.field_70170_p, this.field_70165_t + (double) f, this.field_70163_u + (double) f1, this.field_70161_v + (double) f2, new ItemStack(itemstack.func_77973_b(), j, itemstack.func_77960_j()));

                        if (itemstack.func_77942_o()) {
                            entityitem.func_92059_d().func_77982_d((NBTTagCompound) itemstack.func_77978_p().func_74737_b());
                        }

                        float f3 = 0.05F;

                        entityitem.field_70159_w = (double) ((float) this.field_70146_Z.nextGaussian() * f3);
                        entityitem.field_70181_x = (double) ((float) this.field_70146_Z.nextGaussian() * f3 + 0.2F);
                        entityitem.field_70179_y = (double) ((float) this.field_70146_Z.nextGaussian() * f3);
                        this.field_70170_p.func_72838_d(entityitem);
                    }
                }
            }
        }

        super.func_70106_y();
    }

    @Override
    protected void func_70014_b(NBTTagCompound nbttagcompound) {
        super.func_70014_b(nbttagcompound);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.field_94113_a.length; ++i) {
            if (this.field_94113_a[i] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                nbttagcompound1.func_74774_a("Slot", (byte) i);
                this.field_94113_a[i].func_77955_b(nbttagcompound1);
                nbttaglist.func_74742_a(nbttagcompound1);
            }
        }

        nbttagcompound.func_74782_a("Items", nbttaglist);
    }

    @Override
    protected void func_70037_a(NBTTagCompound nbttagcompound) {
        super.func_70037_a(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.func_150295_c("Items", 10);

        this.field_94113_a = new ItemStack[this.func_70302_i_()];

        for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.func_150305_b(i);
            int j = nbttagcompound1.func_74771_c("Slot") & 255;

            if (j >= 0 && j < this.field_94113_a.length) {
                this.field_94113_a[j] = ItemStack.func_77949_a(nbttagcompound1);
            }
        }

    }

    @Override
    public boolean func_130002_c(EntityPlayer entityplayer) {
        if (!this.field_70170_p.field_72995_K) {
            entityplayer.func_71007_a(this);
        }

        return true;
    }

    @Override
    protected void func_94101_h() {
        int i = 15 - Container.func_94526_b(this);
        float f = 0.98F + (float) i * 0.001F;

        this.field_70159_w *= (double) f;
        this.field_70181_x *= 0.0D;
        this.field_70179_y *= (double) f;
    }
}
