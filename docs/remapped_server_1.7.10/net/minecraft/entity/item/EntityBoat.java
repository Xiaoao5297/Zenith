package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBoat extends Entity {

    private boolean field_70279_a;
    private double field_70276_b;
    private int field_70277_c;
    private double field_70274_d;
    private double field_70275_e;
    private double field_70272_f;
    private double field_70273_g;
    private double field_70281_h;

    public EntityBoat(World world) {
        super(world);
        this.field_70279_a = true;
        this.field_70276_b = 0.07D;
        this.field_70156_m = true;
        this.func_70105_a(1.5F, 0.6F);
        this.field_70129_M = this.field_70131_O / 2.0F;
    }

    @Override
    protected boolean func_70041_e_() {
        return false;
    }

    @Override
    protected void func_70088_a() {
        this.field_70180_af.func_75682_a(17, new Integer(0));
        this.field_70180_af.func_75682_a(18, new Integer(1));
        this.field_70180_af.func_75682_a(19, new Float(0.0F));
    }

    @Override
    public AxisAlignedBB func_70114_g(Entity entity) {
        return entity.field_70121_D;
    }

    @Override
    public AxisAlignedBB func_70046_E() {
        return this.field_70121_D;
    }

    @Override
    public boolean func_70104_M() {
        return true;
    }

    public EntityBoat(World world, double d0, double d1, double d2) {
        this(world);
        this.func_70107_b(d0, d1 + (double) this.field_70129_M, d2);
        this.field_70159_w = 0.0D;
        this.field_70181_x = 0.0D;
        this.field_70179_y = 0.0D;
        this.field_70169_q = d0;
        this.field_70167_r = d1;
        this.field_70166_s = d2;
    }

    @Override
    public double func_70042_X() {
        return (double) this.field_70131_O * 0.0D - 0.30000001192092896D;
    }

    @Override
    public boolean func_70097_a(DamageSource damagesource, float f) {
        if (this.func_85032_ar()) {
            return false;
        } else if (!this.field_70170_p.field_72995_K && !this.field_70128_L) {
            this.func_70269_c(-this.func_70267_i());
            this.func_70265_b(10);
            this.func_70266_a(this.func_70271_g() + f * 10.0F);
            this.func_70018_K();
            boolean flag = damagesource.func_76346_g() instanceof EntityPlayer && ((EntityPlayer) damagesource.func_76346_g()).field_71075_bZ.field_75098_d;

            if (flag || this.func_70271_g() > 40.0F) {
                if (this.field_70153_n != null) {
                    this.field_70153_n.func_70078_a(this);
                }

                if (!flag) {
                    this.func_145778_a(Items.field_151124_az, 1, 0.0F);
                }

                this.func_70106_y();
            }

            return true;
        } else {
            return true;
        }
    }

    @Override
    public boolean func_70067_L() {
        return !this.field_70128_L;
    }

    @Override
    public void func_70071_h_() {
        super.func_70071_h_();
        if (this.func_70268_h() > 0) {
            this.func_70265_b(this.func_70268_h() - 1);
        }

        if (this.func_70271_g() > 0.0F) {
            this.func_70266_a(this.func_70271_g() - 1.0F);
        }

        this.field_70169_q = this.field_70165_t;
        this.field_70167_r = this.field_70163_u;
        this.field_70166_s = this.field_70161_v;
        byte b0 = 5;
        double d0 = 0.0D;

        for (int i = 0; i < b0; ++i) {
            double d1 = this.field_70121_D.field_72338_b + (this.field_70121_D.field_72337_e - this.field_70121_D.field_72338_b) * (double) (i + 0) / (double) b0 - 0.125D;
            double d2 = this.field_70121_D.field_72338_b + (this.field_70121_D.field_72337_e - this.field_70121_D.field_72338_b) * (double) (i + 1) / (double) b0 - 0.125D;
            AxisAlignedBB axisalignedbb = AxisAlignedBB.func_72330_a(this.field_70121_D.field_72340_a, d1, this.field_70121_D.field_72339_c, this.field_70121_D.field_72336_d, d2, this.field_70121_D.field_72334_f);

            if (this.field_70170_p.func_72830_b(axisalignedbb, Material.field_151586_h)) {
                d0 += 1.0D / (double) b0;
            }
        }

        double d3 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
        double d4;
        double d5;
        int j;

        if (d3 > 0.26249999999999996D) {
            d4 = Math.cos((double) this.field_70177_z * Math.PI / 180.0D);
            d5 = Math.sin((double) this.field_70177_z * Math.PI / 180.0D);

            for (j = 0; (double) j < 1.0D + d3 * 60.0D; ++j) {
                double d6 = (double) (this.field_70146_Z.nextFloat() * 2.0F - 1.0F);
                double d7 = (double) (this.field_70146_Z.nextInt(2) * 2 - 1) * 0.7D;
                double d8;
                double d9;

                if (this.field_70146_Z.nextBoolean()) {
                    d8 = this.field_70165_t - d4 * d6 * 0.8D + d5 * d7;
                    d9 = this.field_70161_v - d5 * d6 * 0.8D - d4 * d7;
                    this.field_70170_p.func_72869_a("splash", d8, this.field_70163_u - 0.125D, d9, this.field_70159_w, this.field_70181_x, this.field_70179_y);
                } else {
                    d8 = this.field_70165_t + d4 + d5 * d6 * 0.7D;
                    d9 = this.field_70161_v + d5 - d4 * d6 * 0.7D;
                    this.field_70170_p.func_72869_a("splash", d8, this.field_70163_u - 0.125D, d9, this.field_70159_w, this.field_70181_x, this.field_70179_y);
                }
            }
        }

        double d10;
        double d11;

        if (this.field_70170_p.field_72995_K && this.field_70279_a) {
            if (this.field_70277_c > 0) {
                d4 = this.field_70165_t + (this.field_70274_d - this.field_70165_t) / (double) this.field_70277_c;
                d5 = this.field_70163_u + (this.field_70275_e - this.field_70163_u) / (double) this.field_70277_c;
                d10 = this.field_70161_v + (this.field_70272_f - this.field_70161_v) / (double) this.field_70277_c;
                d11 = MathHelper.func_76138_g(this.field_70273_g - (double) this.field_70177_z);
                this.field_70177_z = (float) ((double) this.field_70177_z + d11 / (double) this.field_70277_c);
                this.field_70125_A = (float) ((double) this.field_70125_A + (this.field_70281_h - (double) this.field_70125_A) / (double) this.field_70277_c);
                --this.field_70277_c;
                this.func_70107_b(d4, d5, d10);
                this.func_70101_b(this.field_70177_z, this.field_70125_A);
            } else {
                d4 = this.field_70165_t + this.field_70159_w;
                d5 = this.field_70163_u + this.field_70181_x;
                d10 = this.field_70161_v + this.field_70179_y;
                this.func_70107_b(d4, d5, d10);
                if (this.field_70122_E) {
                    this.field_70159_w *= 0.5D;
                    this.field_70181_x *= 0.5D;
                    this.field_70179_y *= 0.5D;
                }

                this.field_70159_w *= 0.9900000095367432D;
                this.field_70181_x *= 0.949999988079071D;
                this.field_70179_y *= 0.9900000095367432D;
            }

        } else {
            if (d0 < 1.0D) {
                d4 = d0 * 2.0D - 1.0D;
                this.field_70181_x += 0.03999999910593033D * d4;
            } else {
                if (this.field_70181_x < 0.0D) {
                    this.field_70181_x /= 2.0D;
                }

                this.field_70181_x += 0.007000000216066837D;
            }

            if (this.field_70153_n != null && this.field_70153_n instanceof EntityLivingBase) {
                EntityLivingBase entitylivingbase = (EntityLivingBase) this.field_70153_n;
                float f = this.field_70153_n.field_70177_z + -entitylivingbase.field_70702_br * 90.0F;

                this.field_70159_w += -Math.sin((double) (f * 3.1415927F / 180.0F)) * this.field_70276_b * (double) entitylivingbase.field_70701_bs * 0.05000000074505806D;
                this.field_70179_y += Math.cos((double) (f * 3.1415927F / 180.0F)) * this.field_70276_b * (double) entitylivingbase.field_70701_bs * 0.05000000074505806D;
            }

            d4 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
            if (d4 > 0.35D) {
                d5 = 0.35D / d4;
                this.field_70159_w *= d5;
                this.field_70179_y *= d5;
                d4 = 0.35D;
            }

            if (d4 > d3 && this.field_70276_b < 0.35D) {
                this.field_70276_b += (0.35D - this.field_70276_b) / 35.0D;
                if (this.field_70276_b > 0.35D) {
                    this.field_70276_b = 0.35D;
                }
            } else {
                this.field_70276_b -= (this.field_70276_b - 0.07D) / 35.0D;
                if (this.field_70276_b < 0.07D) {
                    this.field_70276_b = 0.07D;
                }
            }

            int k;

            for (k = 0; k < 4; ++k) {
                int l = MathHelper.func_76128_c(this.field_70165_t + ((double) (k % 2) - 0.5D) * 0.8D);

                j = MathHelper.func_76128_c(this.field_70161_v + ((double) (k / 2) - 0.5D) * 0.8D);

                for (int i1 = 0; i1 < 2; ++i1) {
                    int j1 = MathHelper.func_76128_c(this.field_70163_u) + i1;
                    Block block = this.field_70170_p.func_147439_a(l, j1, j);

                    if (block == Blocks.field_150431_aC) {
                        this.field_70170_p.func_147468_f(l, j1, j);
                        this.field_70123_F = false;
                    } else if (block == Blocks.field_150392_bi) {
                        this.field_70170_p.func_147480_a(l, j1, j, true);
                        this.field_70123_F = false;
                    }
                }
            }

            if (this.field_70122_E) {
                this.field_70159_w *= 0.5D;
                this.field_70181_x *= 0.5D;
                this.field_70179_y *= 0.5D;
            }

            this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
            if (this.field_70123_F && d3 > 0.2D) {
                if (!this.field_70170_p.field_72995_K && !this.field_70128_L) {
                    this.func_70106_y();

                    for (k = 0; k < 3; ++k) {
                        this.func_145778_a(Item.func_150898_a(Blocks.field_150344_f), 1, 0.0F);
                    }

                    for (k = 0; k < 2; ++k) {
                        this.func_145778_a(Items.field_151055_y, 1, 0.0F);
                    }
                }
            } else {
                this.field_70159_w *= 0.9900000095367432D;
                this.field_70181_x *= 0.949999988079071D;
                this.field_70179_y *= 0.9900000095367432D;
            }

            this.field_70125_A = 0.0F;
            d5 = (double) this.field_70177_z;
            d10 = this.field_70169_q - this.field_70165_t;
            d11 = this.field_70166_s - this.field_70161_v;
            if (d10 * d10 + d11 * d11 > 0.001D) {
                d5 = (double) ((float) (Math.atan2(d11, d10) * 180.0D / Math.PI));
            }

            double d12 = MathHelper.func_76138_g(d5 - (double) this.field_70177_z);

            if (d12 > 20.0D) {
                d12 = 20.0D;
            }

            if (d12 < -20.0D) {
                d12 = -20.0D;
            }

            this.field_70177_z = (float) ((double) this.field_70177_z + d12);
            this.func_70101_b(this.field_70177_z, this.field_70125_A);
            if (!this.field_70170_p.field_72995_K) {
                List list = this.field_70170_p.func_72839_b(this, this.field_70121_D.func_72314_b(0.20000000298023224D, 0.0D, 0.20000000298023224D));

                if (list != null && !list.isEmpty()) {
                    for (int k1 = 0; k1 < list.size(); ++k1) {
                        Entity entity = (Entity) list.get(k1);

                        if (entity != this.field_70153_n && entity.func_70104_M() && entity instanceof EntityBoat) {
                            entity.func_70108_f(this);
                        }
                    }
                }

                if (this.field_70153_n != null && this.field_70153_n.field_70128_L) {
                    this.field_70153_n = null;
                }

            }
        }
    }

    @Override
    public void func_70043_V() {
        if (this.field_70153_n != null) {
            double d0 = Math.cos((double) this.field_70177_z * Math.PI / 180.0D) * 0.4D;
            double d1 = Math.sin((double) this.field_70177_z * Math.PI / 180.0D) * 0.4D;

            this.field_70153_n.func_70107_b(this.field_70165_t + d0, this.field_70163_u + this.func_70042_X() + this.field_70153_n.func_70033_W(), this.field_70161_v + d1);
        }
    }

    @Override
    protected void func_70014_b(NBTTagCompound nbttagcompound) {}

    @Override
    protected void func_70037_a(NBTTagCompound nbttagcompound) {}

    @Override
    public boolean func_130002_c(EntityPlayer entityplayer) {
        if (this.field_70153_n != null && this.field_70153_n instanceof EntityPlayer && this.field_70153_n != entityplayer) {
            return true;
        } else {
            if (!this.field_70170_p.field_72995_K) {
                entityplayer.func_70078_a(this);
            }

            return true;
        }
    }

    @Override
    protected void func_70064_a(double d0, boolean flag) {
        int i = MathHelper.func_76128_c(this.field_70165_t);
        int j = MathHelper.func_76128_c(this.field_70163_u);
        int k = MathHelper.func_76128_c(this.field_70161_v);

        if (flag) {
            if (this.field_70143_R > 3.0F) {
                this.func_70069_a(this.field_70143_R);
                if (!this.field_70170_p.field_72995_K && !this.field_70128_L) {
                    this.func_70106_y();

                    int l;

                    for (l = 0; l < 3; ++l) {
                        this.func_145778_a(Item.func_150898_a(Blocks.field_150344_f), 1, 0.0F);
                    }

                    for (l = 0; l < 2; ++l) {
                        this.func_145778_a(Items.field_151055_y, 1, 0.0F);
                    }
                }

                this.field_70143_R = 0.0F;
            }
        } else if (this.field_70170_p.func_147439_a(i, j - 1, k).func_149688_o() != Material.field_151586_h && d0 < 0.0D) {
            this.field_70143_R = (float) ((double) this.field_70143_R - d0);
        }

    }

    public void func_70266_a(float f) {
        this.field_70180_af.func_75692_b(19, f);
    }

    public float func_70271_g() {
        return this.field_70180_af.func_111145_d(19);
    }

    public void func_70265_b(int i) {
        this.field_70180_af.func_75692_b(17, i);
    }

    public int func_70268_h() {
        return this.field_70180_af.func_75679_c(17);
    }

    public void func_70269_c(int i) {
        this.field_70180_af.func_75692_b(18, i);
    }

    public int func_70267_i() {
        return this.field_70180_af.func_75679_c(18);
    }
}
