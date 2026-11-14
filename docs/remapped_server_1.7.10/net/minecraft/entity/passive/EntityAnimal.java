package net.minecraft.entity.passive;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityAnimal extends EntityAgeable implements IAnimals {

    private int field_70881_d;
    private int field_70882_e;
    private EntityPlayer field_146084_br;

    public EntityAnimal(World world) {
        super(world);
    }

    @Override
    protected void func_70629_bd() {
        if (this.func_70874_b() != 0) {
            this.field_70881_d = 0;
        }

        super.func_70629_bd();
    }

    @Override
    public void func_70636_d() {
        super.func_70636_d();
        if (this.func_70874_b() != 0) {
            this.field_70881_d = 0;
        }

        if (this.field_70881_d > 0) {
            --this.field_70881_d;
            String s = "heart";

            if (this.field_70881_d % 10 == 0) {
                double d0 = this.field_70146_Z.nextGaussian() * 0.02D;
                double d1 = this.field_70146_Z.nextGaussian() * 0.02D;
                double d2 = this.field_70146_Z.nextGaussian() * 0.02D;

                this.field_70170_p.func_72869_a(s, this.field_70165_t + (double) (this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double) this.field_70130_N, this.field_70163_u + 0.5D + (double) (this.field_70146_Z.nextFloat() * this.field_70131_O), this.field_70161_v + (double) (this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double) this.field_70130_N, d0, d1, d2);
            }
        } else {
            this.field_70882_e = 0;
        }

    }

    @Override
    protected void func_70785_a(Entity entity, float f) {
        if (entity instanceof EntityPlayer) {
            if (f < 3.0F) {
                double d0 = entity.field_70165_t - this.field_70165_t;
                double d1 = entity.field_70161_v - this.field_70161_v;

                this.field_70177_z = (float) (Math.atan2(d1, d0) * 180.0D / 3.1415927410125732D) - 90.0F;
                this.field_70787_b = true;
            }

            EntityPlayer entityplayer = (EntityPlayer) entity;

            if (entityplayer.func_71045_bC() == null || !this.func_70877_b(entityplayer.func_71045_bC())) {
                this.field_70789_a = null;
            }
        } else if (entity instanceof EntityAnimal) {
            EntityAnimal entityanimal = (EntityAnimal) entity;

            if (this.func_70874_b() > 0 && entityanimal.func_70874_b() < 0) {
                if ((double) f < 2.5D) {
                    this.field_70787_b = true;
                }
            } else if (this.field_70881_d > 0 && entityanimal.field_70881_d > 0) {
                if (entityanimal.field_70789_a == null) {
                    entityanimal.field_70789_a = this;
                }

                if (entityanimal.field_70789_a == this && (double) f < 3.5D) {
                    ++entityanimal.field_70881_d;
                    ++this.field_70881_d;
                    ++this.field_70882_e;
                    if (this.field_70882_e % 4 == 0) {
                        this.field_70170_p.func_72869_a("heart", this.field_70165_t + (double) (this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double) this.field_70130_N, this.field_70163_u + 0.5D + (double) (this.field_70146_Z.nextFloat() * this.field_70131_O), this.field_70161_v + (double) (this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double) this.field_70130_N, 0.0D, 0.0D, 0.0D);
                    }

                    if (this.field_70882_e == 60) {
                        this.func_70876_c((EntityAnimal) entity);
                    }
                } else {
                    this.field_70882_e = 0;
                }
            } else {
                this.field_70882_e = 0;
                this.field_70789_a = null;
            }
        }

    }

    private void func_70876_c(EntityAnimal entityanimal) {
        EntityAgeable entityageable = this.func_90011_a(entityanimal);

        if (entityageable != null) {
            if (this.field_146084_br == null && entityanimal.func_146083_cb() != null) {
                this.field_146084_br = entityanimal.func_146083_cb();
            }

            if (this.field_146084_br != null) {
                this.field_146084_br.func_71029_a(StatList.field_151186_x);
                if (this instanceof EntityCow) {
                    this.field_146084_br.func_71029_a(AchievementList.field_150962_H);
                }
            }

            this.func_70873_a(6000);
            entityanimal.func_70873_a(6000);
            this.field_70881_d = 0;
            this.field_70882_e = 0;
            this.field_70789_a = null;
            entityanimal.field_70789_a = null;
            entityanimal.field_70882_e = 0;
            entityanimal.field_70881_d = 0;
            entityageable.func_70873_a(-24000);
            entityageable.func_70012_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);

            for (int i = 0; i < 7; ++i) {
                double d0 = this.field_70146_Z.nextGaussian() * 0.02D;
                double d1 = this.field_70146_Z.nextGaussian() * 0.02D;
                double d2 = this.field_70146_Z.nextGaussian() * 0.02D;

                this.field_70170_p.func_72869_a("heart", this.field_70165_t + (double) (this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double) this.field_70130_N, this.field_70163_u + 0.5D + (double) (this.field_70146_Z.nextFloat() * this.field_70131_O), this.field_70161_v + (double) (this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double) this.field_70130_N, d0, d1, d2);
            }

            this.field_70170_p.func_72838_d(entityageable);
        }

    }

    @Override
    public boolean func_70097_a(DamageSource damagesource, float f) {
        if (this.func_85032_ar()) {
            return false;
        } else {
            this.field_70788_c = 60;
            if (!this.func_70650_aV()) {
                IAttributeInstance iattributeinstance = this.func_110148_a(SharedMonsterAttributes.field_111263_d);

                if (iattributeinstance.func_111127_a(EntityAnimal.field_110179_h) == null) {
                    iattributeinstance.func_111121_a(EntityAnimal.field_110181_i);
                }
            }

            this.field_70789_a = null;
            this.field_70881_d = 0;
            return super.func_70097_a(damagesource, f);
        }
    }

    @Override
    public float func_70783_a(int i, int j, int k) {
        return this.field_70170_p.func_147439_a(i, j - 1, k) == Blocks.field_150349_c ? 10.0F : this.field_70170_p.func_72801_o(i, j, k) - 0.5F;
    }

    @Override
    public void func_70014_b(NBTTagCompound nbttagcompound) {
        super.func_70014_b(nbttagcompound);
        nbttagcompound.func_74768_a("InLove", this.field_70881_d);
    }

    @Override
    public void func_70037_a(NBTTagCompound nbttagcompound) {
        super.func_70037_a(nbttagcompound);
        this.field_70881_d = nbttagcompound.func_74762_e("InLove");
    }

    @Override
    protected Entity func_70782_k() {
        if (this.field_70788_c > 0) {
            return null;
        } else {
            float f = 8.0F;
            List list;
            EntityAnimal entityanimal;
            int i;

            if (this.field_70881_d > 0) {
                list = this.field_70170_p.func_72872_a(this.getClass(), this.field_70121_D.func_72314_b((double) f, (double) f, (double) f));

                for (i = 0; i < list.size(); ++i) {
                    entityanimal = (EntityAnimal) list.get(i);
                    if (entityanimal != this && entityanimal.field_70881_d > 0) {
                        return entityanimal;
                    }
                }
            } else if (this.func_70874_b() == 0) {
                list = this.field_70170_p.func_72872_a(EntityPlayer.class, this.field_70121_D.func_72314_b((double) f, (double) f, (double) f));

                for (i = 0; i < list.size(); ++i) {
                    EntityPlayer entityplayer = (EntityPlayer) list.get(i);

                    if (entityplayer.func_71045_bC() != null && this.func_70877_b(entityplayer.func_71045_bC())) {
                        return entityplayer;
                    }
                }
            } else if (this.func_70874_b() > 0) {
                list = this.field_70170_p.func_72872_a(this.getClass(), this.field_70121_D.func_72314_b((double) f, (double) f, (double) f));

                for (i = 0; i < list.size(); ++i) {
                    entityanimal = (EntityAnimal) list.get(i);
                    if (entityanimal != this && entityanimal.func_70874_b() < 0) {
                        return entityanimal;
                    }
                }
            }

            return null;
        }
    }

    @Override
    public boolean func_70601_bi() {
        int i = MathHelper.func_76128_c(this.field_70165_t);
        int j = MathHelper.func_76128_c(this.field_70121_D.field_72338_b);
        int k = MathHelper.func_76128_c(this.field_70161_v);

        return this.field_70170_p.func_147439_a(i, j - 1, k) == Blocks.field_150349_c && this.field_70170_p.func_72883_k(i, j, k) > 8 && super.func_70601_bi();
    }

    @Override
    public int func_70627_aG() {
        return 120;
    }

    @Override
    protected boolean func_70692_ba() {
        return false;
    }

    @Override
    protected int func_70693_a(EntityPlayer entityplayer) {
        return 1 + this.field_70170_p.field_73012_v.nextInt(3);
    }

    public boolean func_70877_b(ItemStack itemstack) {
        return itemstack.func_77973_b() == Items.field_151015_O;
    }

    @Override
    public boolean func_70085_c(EntityPlayer entityplayer) {
        ItemStack itemstack = entityplayer.field_71071_by.func_70448_g();

        if (itemstack != null && this.func_70877_b(itemstack) && this.func_70874_b() == 0 && this.field_70881_d <= 0) {
            if (!entityplayer.field_71075_bZ.field_75098_d) {
                --itemstack.field_77994_a;
                if (itemstack.field_77994_a <= 0) {
                    entityplayer.field_71071_by.func_70299_a(entityplayer.field_71071_by.field_70461_c, (ItemStack) null);
                }
            }

            this.func_146082_f(entityplayer);
            return true;
        } else {
            return super.func_70085_c(entityplayer);
        }
    }

    public void func_146082_f(EntityPlayer entityplayer) {
        this.field_70881_d = 600;
        this.field_146084_br = entityplayer;
        this.field_70789_a = null;
        this.field_70170_p.func_72960_a(this, (byte) 18);
    }

    public EntityPlayer func_146083_cb() {
        return this.field_146084_br;
    }

    public boolean func_70880_s() {
        return this.field_70881_d > 0;
    }

    public void func_70875_t() {
        this.field_70881_d = 0;
    }

    public boolean func_70878_b(EntityAnimal entityanimal) {
        return entityanimal == this ? false : (entityanimal.getClass() != this.getClass() ? false : this.func_70880_s() && entityanimal.func_70880_s());
    }
}
