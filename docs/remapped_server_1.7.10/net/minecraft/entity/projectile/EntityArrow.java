package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityArrow extends Entity implements IProjectile {

    private int field_145791_d = -1;
    private int field_145792_e = -1;
    private int field_145789_f = -1;
    private Block field_145790_g;
    private int field_70253_h;
    private boolean field_70254_i;
    public int field_70251_a;
    public int field_70249_b;
    public Entity field_70250_c;
    private int field_70252_j;
    private int field_70257_an;
    private double field_70255_ao = 2.0D;
    private int field_70256_ap;

    public EntityArrow(World world) {
        super(world);
        this.field_70155_l = 10.0D;
        this.func_70105_a(0.5F, 0.5F);
    }

    public EntityArrow(World world, double d0, double d1, double d2) {
        super(world);
        this.field_70155_l = 10.0D;
        this.func_70105_a(0.5F, 0.5F);
        this.func_70107_b(d0, d1, d2);
        this.field_70129_M = 0.0F;
    }

    public EntityArrow(World world, EntityLivingBase entitylivingbase, EntityLivingBase entitylivingbase1, float f, float f1) {
        super(world);
        this.field_70155_l = 10.0D;
        this.field_70250_c = entitylivingbase;
        if (entitylivingbase instanceof EntityPlayer) {
            this.field_70251_a = 1;
        }

        this.field_70163_u = entitylivingbase.field_70163_u + (double) entitylivingbase.func_70047_e() - 0.10000000149011612D;
        double d0 = entitylivingbase1.field_70165_t - entitylivingbase.field_70165_t;
        double d1 = entitylivingbase1.field_70121_D.field_72338_b + (double) (entitylivingbase1.field_70131_O / 3.0F) - this.field_70163_u;
        double d2 = entitylivingbase1.field_70161_v - entitylivingbase.field_70161_v;
        double d3 = (double) MathHelper.func_76133_a(d0 * d0 + d2 * d2);

        if (d3 >= 1.0E-7D) {
            float f2 = (float) (Math.atan2(d2, d0) * 180.0D / 3.1415927410125732D) - 90.0F;
            float f3 = (float) (-(Math.atan2(d1, d3) * 180.0D / 3.1415927410125732D));
            double d4 = d0 / d3;
            double d5 = d2 / d3;

            this.func_70012_b(entitylivingbase.field_70165_t + d4, this.field_70163_u, entitylivingbase.field_70161_v + d5, f2, f3);
            this.field_70129_M = 0.0F;
            float f4 = (float) d3 * 0.2F;

            this.func_70186_c(d0, d1 + (double) f4, d2, f, f1);
        }
    }

    public EntityArrow(World world, EntityLivingBase entitylivingbase, float f) {
        super(world);
        this.field_70155_l = 10.0D;
        this.field_70250_c = entitylivingbase;
        if (entitylivingbase instanceof EntityPlayer) {
            this.field_70251_a = 1;
        }

        this.func_70105_a(0.5F, 0.5F);
        this.func_70012_b(entitylivingbase.field_70165_t, entitylivingbase.field_70163_u + (double) entitylivingbase.func_70047_e(), entitylivingbase.field_70161_v, entitylivingbase.field_70177_z, entitylivingbase.field_70125_A);
        this.field_70165_t -= (double) (MathHelper.func_76134_b(this.field_70177_z / 180.0F * 3.1415927F) * 0.16F);
        this.field_70163_u -= 0.10000000149011612D;
        this.field_70161_v -= (double) (MathHelper.func_76126_a(this.field_70177_z / 180.0F * 3.1415927F) * 0.16F);
        this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
        this.field_70129_M = 0.0F;
        this.field_70159_w = (double) (-MathHelper.func_76126_a(this.field_70177_z / 180.0F * 3.1415927F) * MathHelper.func_76134_b(this.field_70125_A / 180.0F * 3.1415927F));
        this.field_70179_y = (double) (MathHelper.func_76134_b(this.field_70177_z / 180.0F * 3.1415927F) * MathHelper.func_76134_b(this.field_70125_A / 180.0F * 3.1415927F));
        this.field_70181_x = (double) (-MathHelper.func_76126_a(this.field_70125_A / 180.0F * 3.1415927F));
        this.func_70186_c(this.field_70159_w, this.field_70181_x, this.field_70179_y, f * 1.5F, 1.0F);
    }

    @Override
    protected void func_70088_a() {
        this.field_70180_af.func_75682_a(16, (byte) 0);
    }

    @Override
    public void func_70186_c(double d0, double d1, double d2, float f, float f1) {
        float f2 = MathHelper.func_76133_a(d0 * d0 + d1 * d1 + d2 * d2);

        d0 /= (double) f2;
        d1 /= (double) f2;
        d2 /= (double) f2;
        d0 += this.field_70146_Z.nextGaussian() * (double) (this.field_70146_Z.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double) f1;
        d1 += this.field_70146_Z.nextGaussian() * (double) (this.field_70146_Z.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double) f1;
        d2 += this.field_70146_Z.nextGaussian() * (double) (this.field_70146_Z.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double) f1;
        d0 *= (double) f;
        d1 *= (double) f;
        d2 *= (double) f;
        this.field_70159_w = d0;
        this.field_70181_x = d1;
        this.field_70179_y = d2;
        float f3 = MathHelper.func_76133_a(d0 * d0 + d2 * d2);

        this.field_70126_B = this.field_70177_z = (float) (Math.atan2(d0, d2) * 180.0D / 3.1415927410125732D);
        this.field_70127_C = this.field_70125_A = (float) (Math.atan2(d1, (double) f3) * 180.0D / 3.1415927410125732D);
        this.field_70252_j = 0;
    }

    @Override
    public void func_70071_h_() {
        super.func_70071_h_();
        if (this.field_70127_C == 0.0F && this.field_70126_B == 0.0F) {
            float f = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);

            this.field_70126_B = this.field_70177_z = (float) (Math.atan2(this.field_70159_w, this.field_70179_y) * 180.0D / 3.1415927410125732D);
            this.field_70127_C = this.field_70125_A = (float) (Math.atan2(this.field_70181_x, (double) f) * 180.0D / 3.1415927410125732D);
        }

        Block block = this.field_70170_p.func_147439_a(this.field_145791_d, this.field_145792_e, this.field_145789_f);

        if (block.func_149688_o() != Material.field_151579_a) {
            block.func_149719_a(this.field_70170_p, this.field_145791_d, this.field_145792_e, this.field_145789_f);
            AxisAlignedBB axisalignedbb = block.func_149668_a(this.field_70170_p, this.field_145791_d, this.field_145792_e, this.field_145789_f);

            if (axisalignedbb != null && axisalignedbb.func_72318_a(Vec3.func_72443_a(this.field_70165_t, this.field_70163_u, this.field_70161_v))) {
                this.field_70254_i = true;
            }
        }

        if (this.field_70249_b > 0) {
            --this.field_70249_b;
        }

        if (this.field_70254_i) {
            int i = this.field_70170_p.func_72805_g(this.field_145791_d, this.field_145792_e, this.field_145789_f);

            if (block == this.field_145790_g && i == this.field_70253_h) {
                ++this.field_70252_j;
                if (this.field_70252_j == 1200) {
                    this.func_70106_y();
                }

            } else {
                this.field_70254_i = false;
                this.field_70159_w *= (double) (this.field_70146_Z.nextFloat() * 0.2F);
                this.field_70181_x *= (double) (this.field_70146_Z.nextFloat() * 0.2F);
                this.field_70179_y *= (double) (this.field_70146_Z.nextFloat() * 0.2F);
                this.field_70252_j = 0;
                this.field_70257_an = 0;
            }
        } else {
            ++this.field_70257_an;
            Vec3 vec3 = Vec3.func_72443_a(this.field_70165_t, this.field_70163_u, this.field_70161_v);
            Vec3 vec31 = Vec3.func_72443_a(this.field_70165_t + this.field_70159_w, this.field_70163_u + this.field_70181_x, this.field_70161_v + this.field_70179_y);
            MovingObjectPosition movingobjectposition = this.field_70170_p.func_147447_a(vec3, vec31, false, true, false);

            vec3 = Vec3.func_72443_a(this.field_70165_t, this.field_70163_u, this.field_70161_v);
            vec31 = Vec3.func_72443_a(this.field_70165_t + this.field_70159_w, this.field_70163_u + this.field_70181_x, this.field_70161_v + this.field_70179_y);
            if (movingobjectposition != null) {
                vec31 = Vec3.func_72443_a(movingobjectposition.field_72307_f.field_72450_a, movingobjectposition.field_72307_f.field_72448_b, movingobjectposition.field_72307_f.field_72449_c);
            }

            Entity entity = null;
            List list = this.field_70170_p.func_72839_b(this, this.field_70121_D.func_72321_a(this.field_70159_w, this.field_70181_x, this.field_70179_y).func_72314_b(1.0D, 1.0D, 1.0D));
            double d0 = 0.0D;

            int j;
            float f1;

            for (j = 0; j < list.size(); ++j) {
                Entity entity1 = (Entity) list.get(j);

                if (entity1.func_70067_L() && (entity1 != this.field_70250_c || this.field_70257_an >= 5)) {
                    f1 = 0.3F;
                    AxisAlignedBB axisalignedbb1 = entity1.field_70121_D.func_72314_b((double) f1, (double) f1, (double) f1);
                    MovingObjectPosition movingobjectposition1 = axisalignedbb1.func_72327_a(vec3, vec31);

                    if (movingobjectposition1 != null) {
                        double d1 = vec3.func_72438_d(movingobjectposition1.field_72307_f);

                        if (d1 < d0 || d0 == 0.0D) {
                            entity = entity1;
                            d0 = d1;
                        }
                    }
                }
            }

            if (entity != null) {
                movingobjectposition = new MovingObjectPosition(entity);
            }

            if (movingobjectposition != null && movingobjectposition.field_72308_g != null && movingobjectposition.field_72308_g instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) movingobjectposition.field_72308_g;

                if (entityplayer.field_71075_bZ.field_75102_a || this.field_70250_c instanceof EntityPlayer && !((EntityPlayer) this.field_70250_c).func_96122_a(entityplayer)) {
                    movingobjectposition = null;
                }
            }

            float f2;
            float f3;

            if (movingobjectposition != null) {
                if (movingobjectposition.field_72308_g != null) {
                    f2 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70181_x * this.field_70181_x + this.field_70179_y * this.field_70179_y);
                    int k = MathHelper.func_76143_f((double) f2 * this.field_70255_ao);

                    if (this.func_70241_g()) {
                        k += this.field_70146_Z.nextInt(k / 2 + 2);
                    }

                    DamageSource damagesource = null;

                    if (this.field_70250_c == null) {
                        damagesource = DamageSource.func_76353_a(this, this);
                    } else {
                        damagesource = DamageSource.func_76353_a(this, this.field_70250_c);
                    }

                    if (this.func_70027_ad() && !(movingobjectposition.field_72308_g instanceof EntityEnderman)) {
                        movingobjectposition.field_72308_g.func_70015_d(5);
                    }

                    if (movingobjectposition.field_72308_g.func_70097_a(damagesource, (float) k)) {
                        if (movingobjectposition.field_72308_g instanceof EntityLivingBase) {
                            EntityLivingBase entitylivingbase = (EntityLivingBase) movingobjectposition.field_72308_g;

                            if (!this.field_70170_p.field_72995_K) {
                                entitylivingbase.func_85034_r(entitylivingbase.func_85035_bI() + 1);
                            }

                            if (this.field_70256_ap > 0) {
                                f3 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
                                if (f3 > 0.0F) {
                                    movingobjectposition.field_72308_g.func_70024_g(this.field_70159_w * (double) this.field_70256_ap * 0.6000000238418579D / (double) f3, 0.1D, this.field_70179_y * (double) this.field_70256_ap * 0.6000000238418579D / (double) f3);
                                }
                            }

                            if (this.field_70250_c != null && this.field_70250_c instanceof EntityLivingBase) {
                                EnchantmentHelper.func_151384_a(entitylivingbase, this.field_70250_c);
                                EnchantmentHelper.func_151385_b((EntityLivingBase) this.field_70250_c, entitylivingbase);
                            }

                            if (this.field_70250_c != null && movingobjectposition.field_72308_g != this.field_70250_c && movingobjectposition.field_72308_g instanceof EntityPlayer && this.field_70250_c instanceof EntityPlayerMP) {
                                ((EntityPlayerMP) this.field_70250_c).field_71135_a.func_147359_a(new S2BPacketChangeGameState(6, 0.0F));
                            }
                        }

                        this.func_85030_a("random.bowhit", 1.0F, 1.2F / (this.field_70146_Z.nextFloat() * 0.2F + 0.9F));
                        if (!(movingobjectposition.field_72308_g instanceof EntityEnderman)) {
                            this.func_70106_y();
                        }
                    } else {
                        this.field_70159_w *= -0.10000000149011612D;
                        this.field_70181_x *= -0.10000000149011612D;
                        this.field_70179_y *= -0.10000000149011612D;
                        this.field_70177_z += 180.0F;
                        this.field_70126_B += 180.0F;
                        this.field_70257_an = 0;
                    }
                } else {
                    this.field_145791_d = movingobjectposition.field_72311_b;
                    this.field_145792_e = movingobjectposition.field_72312_c;
                    this.field_145789_f = movingobjectposition.field_72309_d;
                    this.field_145790_g = this.field_70170_p.func_147439_a(this.field_145791_d, this.field_145792_e, this.field_145789_f);
                    this.field_70253_h = this.field_70170_p.func_72805_g(this.field_145791_d, this.field_145792_e, this.field_145789_f);
                    this.field_70159_w = (double) ((float) (movingobjectposition.field_72307_f.field_72450_a - this.field_70165_t));
                    this.field_70181_x = (double) ((float) (movingobjectposition.field_72307_f.field_72448_b - this.field_70163_u));
                    this.field_70179_y = (double) ((float) (movingobjectposition.field_72307_f.field_72449_c - this.field_70161_v));
                    f2 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70181_x * this.field_70181_x + this.field_70179_y * this.field_70179_y);
                    this.field_70165_t -= this.field_70159_w / (double) f2 * 0.05000000074505806D;
                    this.field_70163_u -= this.field_70181_x / (double) f2 * 0.05000000074505806D;
                    this.field_70161_v -= this.field_70179_y / (double) f2 * 0.05000000074505806D;
                    this.func_85030_a("random.bowhit", 1.0F, 1.2F / (this.field_70146_Z.nextFloat() * 0.2F + 0.9F));
                    this.field_70254_i = true;
                    this.field_70249_b = 7;
                    this.func_70243_d(false);
                    if (this.field_145790_g.func_149688_o() != Material.field_151579_a) {
                        this.field_145790_g.func_149670_a(this.field_70170_p, this.field_145791_d, this.field_145792_e, this.field_145789_f, this);
                    }
                }
            }

            if (this.func_70241_g()) {
                for (j = 0; j < 4; ++j) {
                    this.field_70170_p.func_72869_a("crit", this.field_70165_t + this.field_70159_w * (double) j / 4.0D, this.field_70163_u + this.field_70181_x * (double) j / 4.0D, this.field_70161_v + this.field_70179_y * (double) j / 4.0D, -this.field_70159_w, -this.field_70181_x + 0.2D, -this.field_70179_y);
                }
            }

            this.field_70165_t += this.field_70159_w;
            this.field_70163_u += this.field_70181_x;
            this.field_70161_v += this.field_70179_y;
            f2 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
            this.field_70177_z = (float) (Math.atan2(this.field_70159_w, this.field_70179_y) * 180.0D / 3.1415927410125732D);

            for (this.field_70125_A = (float) (Math.atan2(this.field_70181_x, (double) f2) * 180.0D / 3.1415927410125732D); this.field_70125_A - this.field_70127_C < -180.0F; this.field_70127_C -= 360.0F) {
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
            float f4 = 0.99F;

            f1 = 0.05F;
            if (this.func_70090_H()) {
                for (int l = 0; l < 4; ++l) {
                    f3 = 0.25F;
                    this.field_70170_p.func_72869_a("bubble", this.field_70165_t - this.field_70159_w * (double) f3, this.field_70163_u - this.field_70181_x * (double) f3, this.field_70161_v - this.field_70179_y * (double) f3, this.field_70159_w, this.field_70181_x, this.field_70179_y);
                }

                f4 = 0.8F;
            }

            if (this.func_70026_G()) {
                this.func_70066_B();
            }

            this.field_70159_w *= (double) f4;
            this.field_70181_x *= (double) f4;
            this.field_70179_y *= (double) f4;
            this.field_70181_x -= (double) f1;
            this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
            this.func_145775_I();
        }
    }

    @Override
    public void func_70014_b(NBTTagCompound nbttagcompound) {
        nbttagcompound.func_74777_a("xTile", (short) this.field_145791_d);
        nbttagcompound.func_74777_a("yTile", (short) this.field_145792_e);
        nbttagcompound.func_74777_a("zTile", (short) this.field_145789_f);
        nbttagcompound.func_74777_a("life", (short) this.field_70252_j);
        nbttagcompound.func_74774_a("inTile", (byte) Block.func_149682_b(this.field_145790_g));
        nbttagcompound.func_74774_a("inData", (byte) this.field_70253_h);
        nbttagcompound.func_74774_a("shake", (byte) this.field_70249_b);
        nbttagcompound.func_74774_a("inGround", (byte) (this.field_70254_i ? 1 : 0));
        nbttagcompound.func_74774_a("pickup", (byte) this.field_70251_a);
        nbttagcompound.func_74780_a("damage", this.field_70255_ao);
    }

    @Override
    public void func_70037_a(NBTTagCompound nbttagcompound) {
        this.field_145791_d = nbttagcompound.func_74765_d("xTile");
        this.field_145792_e = nbttagcompound.func_74765_d("yTile");
        this.field_145789_f = nbttagcompound.func_74765_d("zTile");
        this.field_70252_j = nbttagcompound.func_74765_d("life");
        this.field_145790_g = Block.func_149729_e(nbttagcompound.func_74771_c("inTile") & 255);
        this.field_70253_h = nbttagcompound.func_74771_c("inData") & 255;
        this.field_70249_b = nbttagcompound.func_74771_c("shake") & 255;
        this.field_70254_i = nbttagcompound.func_74771_c("inGround") == 1;
        if (nbttagcompound.func_150297_b("damage", 99)) {
            this.field_70255_ao = nbttagcompound.func_74769_h("damage");
        }

        if (nbttagcompound.func_150297_b("pickup", 99)) {
            this.field_70251_a = nbttagcompound.func_74771_c("pickup");
        } else if (nbttagcompound.func_150297_b("player", 99)) {
            this.field_70251_a = nbttagcompound.func_74767_n("player") ? 1 : 0;
        }

    }

    @Override
    public void func_70100_b_(EntityPlayer entityplayer) {
        if (!this.field_70170_p.field_72995_K && this.field_70254_i && this.field_70249_b <= 0) {
            boolean flag = this.field_70251_a == 1 || this.field_70251_a == 2 && entityplayer.field_71075_bZ.field_75098_d;

            if (this.field_70251_a == 1 && !entityplayer.field_71071_by.func_70441_a(new ItemStack(Items.field_151032_g, 1))) {
                flag = false;
            }

            if (flag) {
                this.func_85030_a("random.pop", 0.2F, ((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                entityplayer.func_71001_a(this, 1);
                this.func_70106_y();
            }

        }
    }

    @Override
    protected boolean func_70041_e_() {
        return false;
    }

    public void func_70239_b(double d0) {
        this.field_70255_ao = d0;
    }

    public double func_70242_d() {
        return this.field_70255_ao;
    }

    public void func_70240_a(int i) {
        this.field_70256_ap = i;
    }

    @Override
    public boolean func_70075_an() {
        return false;
    }

    public void func_70243_d(boolean flag) {
        byte b0 = this.field_70180_af.func_75683_a(16);

        if (flag) {
            this.field_70180_af.func_75692_b(16, (byte) (b0 | 1));
        } else {
            this.field_70180_af.func_75692_b(16, (byte) (b0 & -2));
        }

    }

    public boolean func_70241_g() {
        byte b0 = this.field_70180_af.func_75683_a(16);

        return (b0 & 1) != 0;
    }
}
