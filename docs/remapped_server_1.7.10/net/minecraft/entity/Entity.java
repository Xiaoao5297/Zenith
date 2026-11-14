package net.minecraft.entity;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.Block$SoundType;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class Entity {

    private static int field_70152_a;
    private int field_145783_c;
    public double field_70155_l;
    public boolean field_70156_m;
    public Entity field_70153_n;
    public Entity field_70154_o;
    public boolean field_98038_p;
    public World field_70170_p;
    public double field_70169_q;
    public double field_70167_r;
    public double field_70166_s;
    public double field_70165_t;
    public double field_70163_u;
    public double field_70161_v;
    public double field_70159_w;
    public double field_70181_x;
    public double field_70179_y;
    public float field_70177_z;
    public float field_70125_A;
    public float field_70126_B;
    public float field_70127_C;
    public final AxisAlignedBB field_70121_D;
    public boolean field_70122_E;
    public boolean field_70123_F;
    public boolean field_70124_G;
    public boolean field_70132_H;
    public boolean field_70133_I;
    protected boolean field_70134_J;
    public boolean field_70135_K;
    public boolean field_70128_L;
    public float field_70129_M;
    public float field_70130_N;
    public float field_70131_O;
    public float field_70141_P;
    public float field_70140_Q;
    public float field_82151_R;
    public float field_70143_R;
    private int field_70150_b;
    public double field_70142_S;
    public double field_70137_T;
    public double field_70136_U;
    public float field_70139_V;
    public float field_70138_W;
    public boolean field_70145_X;
    public float field_70144_Y;
    protected Random field_70146_Z;
    public int field_70173_aa;
    public int field_70174_ab;
    private int field_70151_c;
    protected boolean field_70171_ac;
    public int field_70172_ad;
    private boolean field_70148_d;
    protected boolean field_70178_ae;
    protected DataWatcher field_70180_af;
    private double field_70149_e;
    private double field_70147_f;
    public boolean field_70175_ag;
    public int field_70176_ah;
    public int field_70162_ai;
    public int field_70164_aj;
    public boolean field_70158_ak;
    public boolean field_70160_al;
    public int field_71088_bW;
    protected boolean field_71087_bX;
    protected int field_82153_h;
    public int field_71093_bK;
    protected int field_82152_aq;
    private boolean field_83001_bt;
    protected UUID field_96093_i;
    public Entity$EnumEntitySize field_70168_am;

    public int func_145782_y() {
        return this.field_145783_c;
    }

    public void func_145769_d(int i) {
        this.field_145783_c = i;
    }

    public Entity(World world) {
        this.field_145783_c = Entity.field_70152_a++;
        this.field_70155_l = 1.0D;
        this.field_70121_D = AxisAlignedBB.func_72330_a(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        this.field_70135_K = true;
        this.field_70130_N = 0.6F;
        this.field_70131_O = 1.8F;
        this.field_70150_b = 1;
        this.field_70146_Z = new Random();
        this.field_70174_ab = 1;
        this.field_70148_d = true;
        this.field_96093_i = UUID.randomUUID();
        this.field_70168_am = Entity$EnumEntitySize.SIZE_2;
        this.field_70170_p = world;
        this.func_70107_b(0.0D, 0.0D, 0.0D);
        if (world != null) {
            this.field_71093_bK = world.field_73011_w.field_76574_g;
        }

        this.field_70180_af = new DataWatcher(this);
        this.field_70180_af.func_75682_a(0, (byte) 0);
        this.field_70180_af.func_75682_a(1, (short) 300);
        this.func_70088_a();
    }

    protected abstract void func_70088_a();

    public DataWatcher func_70096_w() {
        return this.field_70180_af;
    }

    public boolean equals(Object object) {
        return object instanceof Entity ? ((Entity) object).field_145783_c == this.field_145783_c : false;
    }

    public int hashCode() {
        return this.field_145783_c;
    }

    public void func_70106_y() {
        this.field_70128_L = true;
    }

    protected void func_70105_a(float f, float f1) {
        float f2;

        if (f != this.field_70130_N || f1 != this.field_70131_O) {
            f2 = this.field_70130_N;
            this.field_70130_N = f;
            this.field_70131_O = f1;
            this.field_70121_D.field_72336_d = this.field_70121_D.field_72340_a + (double) this.field_70130_N;
            this.field_70121_D.field_72334_f = this.field_70121_D.field_72339_c + (double) this.field_70130_N;
            this.field_70121_D.field_72337_e = this.field_70121_D.field_72338_b + (double) this.field_70131_O;
            if (this.field_70130_N > f2 && !this.field_70148_d && !this.field_70170_p.field_72995_K) {
                this.func_70091_d((double) (f2 - this.field_70130_N), 0.0D, (double) (f2 - this.field_70130_N));
            }
        }

        f2 = f % 2.0F;
        if ((double) f2 < 0.375D) {
            this.field_70168_am = Entity$EnumEntitySize.SIZE_1;
        } else if ((double) f2 < 0.75D) {
            this.field_70168_am = Entity$EnumEntitySize.SIZE_2;
        } else if ((double) f2 < 1.0D) {
            this.field_70168_am = Entity$EnumEntitySize.SIZE_3;
        } else if ((double) f2 < 1.375D) {
            this.field_70168_am = Entity$EnumEntitySize.SIZE_4;
        } else if ((double) f2 < 1.75D) {
            this.field_70168_am = Entity$EnumEntitySize.SIZE_5;
        } else {
            this.field_70168_am = Entity$EnumEntitySize.SIZE_6;
        }

    }

    protected void func_70101_b(float f, float f1) {
        this.field_70177_z = f % 360.0F;
        this.field_70125_A = f1 % 360.0F;
    }

    public void func_70107_b(double d0, double d1, double d2) {
        this.field_70165_t = d0;
        this.field_70163_u = d1;
        this.field_70161_v = d2;
        float f = this.field_70130_N / 2.0F;
        float f1 = this.field_70131_O;

        this.field_70121_D.func_72324_b(d0 - (double) f, d1 - (double) this.field_70129_M + (double) this.field_70139_V, d2 - (double) f, d0 + (double) f, d1 - (double) this.field_70129_M + (double) this.field_70139_V + (double) f1, d2 + (double) f);
    }

    public void func_70071_h_() {
        this.func_70030_z();
    }

    public void func_70030_z() {
        this.field_70170_p.field_72984_F.func_76320_a("entityBaseTick");
        if (this.field_70154_o != null && this.field_70154_o.field_70128_L) {
            this.field_70154_o = null;
        }

        this.field_70141_P = this.field_70140_Q;
        this.field_70169_q = this.field_70165_t;
        this.field_70167_r = this.field_70163_u;
        this.field_70166_s = this.field_70161_v;
        this.field_70127_C = this.field_70125_A;
        this.field_70126_B = this.field_70177_z;
        int i;
        int j;

        if (!this.field_70170_p.field_72995_K && this.field_70170_p instanceof WorldServer) {
            this.field_70170_p.field_72984_F.func_76320_a("portal");
            MinecraftServer minecraftserver = ((WorldServer) this.field_70170_p).func_73046_m();

            i = this.func_82145_z();
            if (this.field_71087_bX) {
                if (minecraftserver.func_71255_r()) {
                    if (this.field_70154_o == null && this.field_82153_h++ >= i) {
                        this.field_82153_h = i;
                        this.field_71088_bW = this.func_82147_ab();
                        if (this.field_70170_p.field_73011_w.field_76574_g == -1) {
                            j = 0;
                        } else {
                            j = -1;
                        }

                        this.func_71027_c(j);
                    }

                    this.field_71087_bX = false;
                }
            } else {
                if (this.field_82153_h > 0) {
                    this.field_82153_h -= 4;
                }

                if (this.field_82153_h < 0) {
                    this.field_82153_h = 0;
                }
            }

            if (this.field_71088_bW > 0) {
                --this.field_71088_bW;
            }

            this.field_70170_p.field_72984_F.func_76319_b();
        }

        if (this.func_70051_ag() && !this.func_70090_H()) {
            int k = MathHelper.func_76128_c(this.field_70165_t);

            i = MathHelper.func_76128_c(this.field_70163_u - 0.20000000298023224D - (double) this.field_70129_M);
            j = MathHelper.func_76128_c(this.field_70161_v);
            Block block = this.field_70170_p.func_147439_a(k, i, j);

            if (block.func_149688_o() != Material.field_151579_a) {
                this.field_70170_p.func_72869_a("blockcrack_" + Block.func_149682_b(block) + "_" + this.field_70170_p.func_72805_g(k, i, j), this.field_70165_t + ((double) this.field_70146_Z.nextFloat() - 0.5D) * (double) this.field_70130_N, this.field_70121_D.field_72338_b + 0.1D, this.field_70161_v + ((double) this.field_70146_Z.nextFloat() - 0.5D) * (double) this.field_70130_N, -this.field_70159_w * 4.0D, 1.5D, -this.field_70179_y * 4.0D);
            }
        }

        this.func_70072_I();
        if (this.field_70170_p.field_72995_K) {
            this.field_70151_c = 0;
        } else if (this.field_70151_c > 0) {
            if (this.field_70178_ae) {
                this.field_70151_c -= 4;
                if (this.field_70151_c < 0) {
                    this.field_70151_c = 0;
                }
            } else {
                if (this.field_70151_c % 20 == 0) {
                    this.func_70097_a(DamageSource.field_76370_b, 1.0F);
                }

                --this.field_70151_c;
            }
        }

        if (this.func_70058_J()) {
            this.func_70044_A();
            this.field_70143_R *= 0.5F;
        }

        if (this.field_70163_u < -64.0D) {
            this.func_70076_C();
        }

        if (!this.field_70170_p.field_72995_K) {
            this.func_70052_a(0, this.field_70151_c > 0);
        }

        this.field_70148_d = false;
        this.field_70170_p.field_72984_F.func_76319_b();
    }

    public int func_82145_z() {
        return 0;
    }

    protected void func_70044_A() {
        if (!this.field_70178_ae) {
            this.func_70097_a(DamageSource.field_76371_c, 4.0F);
            this.func_70015_d(15);
        }

    }

    public void func_70015_d(int i) {
        int j = i * 20;

        j = EnchantmentProtection.func_92093_a(this, j);
        if (this.field_70151_c < j) {
            this.field_70151_c = j;
        }

    }

    public void func_70066_B() {
        this.field_70151_c = 0;
    }

    protected void func_70076_C() {
        this.func_70106_y();
    }

    public boolean func_70038_c(double d0, double d1, double d2) {
        AxisAlignedBB axisalignedbb = this.field_70121_D.func_72325_c(d0, d1, d2);
        List list = this.field_70170_p.func_72945_a(this, axisalignedbb);

        return !list.isEmpty() ? false : !this.field_70170_p.func_72953_d(axisalignedbb);
    }

    public void func_70091_d(double d0, double d1, double d2) {
        if (this.field_70145_X) {
            this.field_70121_D.func_72317_d(d0, d1, d2);
            this.field_70165_t = (this.field_70121_D.field_72340_a + this.field_70121_D.field_72336_d) / 2.0D;
            this.field_70163_u = this.field_70121_D.field_72338_b + (double) this.field_70129_M - (double) this.field_70139_V;
            this.field_70161_v = (this.field_70121_D.field_72339_c + this.field_70121_D.field_72334_f) / 2.0D;
        } else {
            this.field_70170_p.field_72984_F.func_76320_a("move");
            this.field_70139_V *= 0.4F;
            double d3 = this.field_70165_t;
            double d4 = this.field_70163_u;
            double d5 = this.field_70161_v;

            if (this.field_70134_J) {
                this.field_70134_J = false;
                d0 *= 0.25D;
                d1 *= 0.05000000074505806D;
                d2 *= 0.25D;
                this.field_70159_w = 0.0D;
                this.field_70181_x = 0.0D;
                this.field_70179_y = 0.0D;
            }

            double d6 = d0;
            double d7 = d1;
            double d8 = d2;
            AxisAlignedBB axisalignedbb = this.field_70121_D.func_72329_c();
            boolean flag = this.field_70122_E && this.func_70093_af() && this instanceof EntityPlayer;

            if (flag) {
                double d9;

                for (d9 = 0.05D; d0 != 0.0D && this.field_70170_p.func_72945_a(this, this.field_70121_D.func_72325_c(d0, -1.0D, 0.0D)).isEmpty(); d6 = d0) {
                    if (d0 < d9 && d0 >= -d9) {
                        d0 = 0.0D;
                    } else if (d0 > 0.0D) {
                        d0 -= d9;
                    } else {
                        d0 += d9;
                    }
                }

                for (; d2 != 0.0D && this.field_70170_p.func_72945_a(this, this.field_70121_D.func_72325_c(0.0D, -1.0D, d2)).isEmpty(); d8 = d2) {
                    if (d2 < d9 && d2 >= -d9) {
                        d2 = 0.0D;
                    } else if (d2 > 0.0D) {
                        d2 -= d9;
                    } else {
                        d2 += d9;
                    }
                }

                while (d0 != 0.0D && d2 != 0.0D && this.field_70170_p.func_72945_a(this, this.field_70121_D.func_72325_c(d0, -1.0D, d2)).isEmpty()) {
                    if (d0 < d9 && d0 >= -d9) {
                        d0 = 0.0D;
                    } else if (d0 > 0.0D) {
                        d0 -= d9;
                    } else {
                        d0 += d9;
                    }

                    if (d2 < d9 && d2 >= -d9) {
                        d2 = 0.0D;
                    } else if (d2 > 0.0D) {
                        d2 -= d9;
                    } else {
                        d2 += d9;
                    }

                    d6 = d0;
                    d8 = d2;
                }
            }

            List list = this.field_70170_p.func_72945_a(this, this.field_70121_D.func_72321_a(d0, d1, d2));

            for (int i = 0; i < list.size(); ++i) {
                d1 = ((AxisAlignedBB) list.get(i)).func_72323_b(this.field_70121_D, d1);
            }

            this.field_70121_D.func_72317_d(0.0D, d1, 0.0D);
            if (!this.field_70135_K && d7 != d1) {
                d2 = 0.0D;
                d1 = 0.0D;
                d0 = 0.0D;
            }

            boolean flag1 = this.field_70122_E || d7 != d1 && d7 < 0.0D;

            int j;

            for (j = 0; j < list.size(); ++j) {
                d0 = ((AxisAlignedBB) list.get(j)).func_72316_a(this.field_70121_D, d0);
            }

            this.field_70121_D.func_72317_d(d0, 0.0D, 0.0D);
            if (!this.field_70135_K && d6 != d0) {
                d2 = 0.0D;
                d1 = 0.0D;
                d0 = 0.0D;
            }

            for (j = 0; j < list.size(); ++j) {
                d2 = ((AxisAlignedBB) list.get(j)).func_72322_c(this.field_70121_D, d2);
            }

            this.field_70121_D.func_72317_d(0.0D, 0.0D, d2);
            if (!this.field_70135_K && d8 != d2) {
                d2 = 0.0D;
                d1 = 0.0D;
                d0 = 0.0D;
            }

            double d10;
            double d11;
            double d12;
            int k;

            if (this.field_70138_W > 0.0F && flag1 && (flag || this.field_70139_V < 0.05F) && (d6 != d0 || d8 != d2)) {
                d10 = d0;
                d11 = d1;
                d12 = d2;
                d0 = d6;
                d1 = (double) this.field_70138_W;
                d2 = d8;
                AxisAlignedBB axisalignedbb1 = this.field_70121_D.func_72329_c();

                this.field_70121_D.func_72328_c(axisalignedbb);
                list = this.field_70170_p.func_72945_a(this, this.field_70121_D.func_72321_a(d6, d1, d8));

                for (k = 0; k < list.size(); ++k) {
                    d1 = ((AxisAlignedBB) list.get(k)).func_72323_b(this.field_70121_D, d1);
                }

                this.field_70121_D.func_72317_d(0.0D, d1, 0.0D);
                if (!this.field_70135_K && d7 != d1) {
                    d2 = 0.0D;
                    d1 = 0.0D;
                    d0 = 0.0D;
                }

                for (k = 0; k < list.size(); ++k) {
                    d0 = ((AxisAlignedBB) list.get(k)).func_72316_a(this.field_70121_D, d0);
                }

                this.field_70121_D.func_72317_d(d0, 0.0D, 0.0D);
                if (!this.field_70135_K && d6 != d0) {
                    d2 = 0.0D;
                    d1 = 0.0D;
                    d0 = 0.0D;
                }

                for (k = 0; k < list.size(); ++k) {
                    d2 = ((AxisAlignedBB) list.get(k)).func_72322_c(this.field_70121_D, d2);
                }

                this.field_70121_D.func_72317_d(0.0D, 0.0D, d2);
                if (!this.field_70135_K && d8 != d2) {
                    d2 = 0.0D;
                    d1 = 0.0D;
                    d0 = 0.0D;
                }

                if (!this.field_70135_K && d7 != d1) {
                    d2 = 0.0D;
                    d1 = 0.0D;
                    d0 = 0.0D;
                } else {
                    d1 = (double) (-this.field_70138_W);

                    for (k = 0; k < list.size(); ++k) {
                        d1 = ((AxisAlignedBB) list.get(k)).func_72323_b(this.field_70121_D, d1);
                    }

                    this.field_70121_D.func_72317_d(0.0D, d1, 0.0D);
                }

                if (d10 * d10 + d12 * d12 >= d0 * d0 + d2 * d2) {
                    d0 = d10;
                    d1 = d11;
                    d2 = d12;
                    this.field_70121_D.func_72328_c(axisalignedbb1);
                }
            }

            this.field_70170_p.field_72984_F.func_76319_b();
            this.field_70170_p.field_72984_F.func_76320_a("rest");
            this.field_70165_t = (this.field_70121_D.field_72340_a + this.field_70121_D.field_72336_d) / 2.0D;
            this.field_70163_u = this.field_70121_D.field_72338_b + (double) this.field_70129_M - (double) this.field_70139_V;
            this.field_70161_v = (this.field_70121_D.field_72339_c + this.field_70121_D.field_72334_f) / 2.0D;
            this.field_70123_F = d6 != d0 || d8 != d2;
            this.field_70124_G = d7 != d1;
            this.field_70122_E = d7 != d1 && d7 < 0.0D;
            this.field_70132_H = this.field_70123_F || this.field_70124_G;
            this.func_70064_a(d1, this.field_70122_E);
            if (d6 != d0) {
                this.field_70159_w = 0.0D;
            }

            if (d7 != d1) {
                this.field_70181_x = 0.0D;
            }

            if (d8 != d2) {
                this.field_70179_y = 0.0D;
            }

            d10 = this.field_70165_t - d3;
            d11 = this.field_70163_u - d4;
            d12 = this.field_70161_v - d5;
            if (this.func_70041_e_() && !flag && this.field_70154_o == null) {
                int l = MathHelper.func_76128_c(this.field_70165_t);

                k = MathHelper.func_76128_c(this.field_70163_u - 0.20000000298023224D - (double) this.field_70129_M);
                int i1 = MathHelper.func_76128_c(this.field_70161_v);
                Block block = this.field_70170_p.func_147439_a(l, k, i1);
                int j1 = this.field_70170_p.func_147439_a(l, k - 1, i1).func_149645_b();

                if (j1 == 11 || j1 == 32 || j1 == 21) {
                    block = this.field_70170_p.func_147439_a(l, k - 1, i1);
                }

                if (block != Blocks.field_150468_ap) {
                    d11 = 0.0D;
                }

                this.field_70140_Q = (float) ((double) this.field_70140_Q + (double) MathHelper.func_76133_a(d10 * d10 + d12 * d12) * 0.6D);
                this.field_82151_R = (float) ((double) this.field_82151_R + (double) MathHelper.func_76133_a(d10 * d10 + d11 * d11 + d12 * d12) * 0.6D);
                if (this.field_82151_R > (float) this.field_70150_b && block.func_149688_o() != Material.field_151579_a) {
                    this.field_70150_b = (int) this.field_82151_R + 1;
                    if (this.func_70090_H()) {
                        float f = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w * 0.20000000298023224D + this.field_70181_x * this.field_70181_x + this.field_70179_y * this.field_70179_y * 0.20000000298023224D) * 0.35F;

                        if (f > 1.0F) {
                            f = 1.0F;
                        }

                        this.func_85030_a(this.func_145776_H(), f, 1.0F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.4F);
                    }

                    this.func_145780_a(l, k, i1, block);
                    block.func_149724_b(this.field_70170_p, l, k, i1, this);
                }
            }

            try {
                this.func_145775_I();
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.func_85055_a(throwable, "Checking entity block collision");
                CrashReportCategory crashreportcategory = crashreport.func_85058_a("Entity being checked for collision");

                this.func_85029_a(crashreportcategory);
                throw new ReportedException(crashreport);
            }

            boolean flag2 = this.func_70026_G();

            if (this.field_70170_p.func_147470_e(this.field_70121_D.func_72331_e(0.001D, 0.001D, 0.001D))) {
                this.func_70081_e(1);
                if (!flag2) {
                    ++this.field_70151_c;
                    if (this.field_70151_c == 0) {
                        this.func_70015_d(8);
                    }
                }
            } else if (this.field_70151_c <= 0) {
                this.field_70151_c = -this.field_70174_ab;
            }

            if (flag2 && this.field_70151_c > 0) {
                this.func_85030_a("random.fizz", 0.7F, 1.6F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.4F);
                this.field_70151_c = -this.field_70174_ab;
            }

            this.field_70170_p.field_72984_F.func_76319_b();
        }
    }

    protected String func_145776_H() {
        return "game.neutral.swim";
    }

    protected void func_145775_I() {
        int i = MathHelper.func_76128_c(this.field_70121_D.field_72340_a + 0.001D);
        int j = MathHelper.func_76128_c(this.field_70121_D.field_72338_b + 0.001D);
        int k = MathHelper.func_76128_c(this.field_70121_D.field_72339_c + 0.001D);
        int l = MathHelper.func_76128_c(this.field_70121_D.field_72336_d - 0.001D);
        int i1 = MathHelper.func_76128_c(this.field_70121_D.field_72337_e - 0.001D);
        int j1 = MathHelper.func_76128_c(this.field_70121_D.field_72334_f - 0.001D);

        if (this.field_70170_p.func_72904_c(i, j, k, l, i1, j1)) {
            for (int k1 = i; k1 <= l; ++k1) {
                for (int l1 = j; l1 <= i1; ++l1) {
                    for (int i2 = k; i2 <= j1; ++i2) {
                        Block block = this.field_70170_p.func_147439_a(k1, l1, i2);

                        try {
                            block.func_149670_a(this.field_70170_p, k1, l1, i2, this);
                        } catch (Throwable throwable) {
                            CrashReport crashreport = CrashReport.func_85055_a(throwable, "Colliding entity with block");
                            CrashReportCategory crashreportcategory = crashreport.func_85058_a("Block being collided with");

                            CrashReportCategory.func_147153_a(crashreportcategory, k1, l1, i2, block, this.field_70170_p.func_72805_g(k1, l1, i2));
                            throw new ReportedException(crashreport);
                        }
                    }
                }
            }
        }

    }

    protected void func_145780_a(int i, int j, int k, Block block) {
        Block$SoundType block$soundtype = block.field_149762_H;

        if (this.field_70170_p.func_147439_a(i, j + 1, k) == Blocks.field_150431_aC) {
            block$soundtype = Blocks.field_150431_aC.field_149762_H;
            this.func_85030_a(block$soundtype.func_150498_e(), block$soundtype.func_150497_c() * 0.15F, block$soundtype.func_150494_d());
        } else if (!block.func_149688_o().func_76224_d()) {
            this.func_85030_a(block$soundtype.func_150498_e(), block$soundtype.func_150497_c() * 0.15F, block$soundtype.func_150494_d());
        }

    }

    public void func_85030_a(String s, float f, float f1) {
        this.field_70170_p.func_72956_a(this, s, f, f1);
    }

    protected boolean func_70041_e_() {
        return true;
    }

    protected void func_70064_a(double d0, boolean flag) {
        if (flag) {
            if (this.field_70143_R > 0.0F) {
                this.func_70069_a(this.field_70143_R);
                this.field_70143_R = 0.0F;
            }
        } else if (d0 < 0.0D) {
            this.field_70143_R = (float) ((double) this.field_70143_R - d0);
        }

    }

    public AxisAlignedBB func_70046_E() {
        return null;
    }

    protected void func_70081_e(int i) {
        if (!this.field_70178_ae) {
            this.func_70097_a(DamageSource.field_76372_a, (float) i);
        }

    }

    public final boolean func_70045_F() {
        return this.field_70178_ae;
    }

    protected void func_70069_a(float f) {
        if (this.field_70153_n != null) {
            this.field_70153_n.func_70069_a(f);
        }

    }

    public boolean func_70026_G() {
        return this.field_70171_ac || this.field_70170_p.func_72951_B(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v)) || this.field_70170_p.func_72951_B(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u + (double) this.field_70131_O), MathHelper.func_76128_c(this.field_70161_v));
    }

    public boolean func_70090_H() {
        return this.field_70171_ac;
    }

    public boolean func_70072_I() {
        if (this.field_70170_p.func_72918_a(this.field_70121_D.func_72314_b(0.0D, -0.4000000059604645D, 0.0D).func_72331_e(0.001D, 0.001D, 0.001D), Material.field_151586_h, this)) {
            if (!this.field_70171_ac && !this.field_70148_d) {
                float f = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w * 0.20000000298023224D + this.field_70181_x * this.field_70181_x + this.field_70179_y * this.field_70179_y * 0.20000000298023224D) * 0.2F;

                if (f > 1.0F) {
                    f = 1.0F;
                }

                this.func_85030_a(this.func_145777_O(), f, 1.0F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.4F);
                float f1 = (float) MathHelper.func_76128_c(this.field_70121_D.field_72338_b);

                float f2;
                float f3;
                int i;

                for (i = 0; (float) i < 1.0F + this.field_70130_N * 20.0F; ++i) {
                    f2 = (this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * this.field_70130_N;
                    f3 = (this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * this.field_70130_N;
                    this.field_70170_p.func_72869_a("bubble", this.field_70165_t + (double) f2, (double) (f1 + 1.0F), this.field_70161_v + (double) f3, this.field_70159_w, this.field_70181_x - (double) (this.field_70146_Z.nextFloat() * 0.2F), this.field_70179_y);
                }

                for (i = 0; (float) i < 1.0F + this.field_70130_N * 20.0F; ++i) {
                    f2 = (this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * this.field_70130_N;
                    f3 = (this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * this.field_70130_N;
                    this.field_70170_p.func_72869_a("splash", this.field_70165_t + (double) f2, (double) (f1 + 1.0F), this.field_70161_v + (double) f3, this.field_70159_w, this.field_70181_x, this.field_70179_y);
                }
            }

            this.field_70143_R = 0.0F;
            this.field_70171_ac = true;
            this.field_70151_c = 0;
        } else {
            this.field_70171_ac = false;
        }

        return this.field_70171_ac;
    }

    protected String func_145777_O() {
        return "game.neutral.swim.splash";
    }

    public boolean func_70055_a(Material material) {
        double d0 = this.field_70163_u + (double) this.func_70047_e();
        int i = MathHelper.func_76128_c(this.field_70165_t);
        int j = MathHelper.func_76141_d((float) MathHelper.func_76128_c(d0));
        int k = MathHelper.func_76128_c(this.field_70161_v);
        Block block = this.field_70170_p.func_147439_a(i, j, k);

        if (block.func_149688_o() == material) {
            float f = BlockLiquid.func_149801_b(this.field_70170_p.func_72805_g(i, j, k)) - 0.11111111F;
            float f1 = (float) (j + 1) - f;

            return d0 < (double) f1;
        } else {
            return false;
        }
    }

    public float func_70047_e() {
        return 0.0F;
    }

    public boolean func_70058_J() {
        return this.field_70170_p.func_72875_a(this.field_70121_D.func_72314_b(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), Material.field_151587_i);
    }

    public void func_70060_a(float f, float f1, float f2) {
        float f3 = f * f + f1 * f1;

        if (f3 >= 1.0E-4F) {
            f3 = MathHelper.func_76129_c(f3);
            if (f3 < 1.0F) {
                f3 = 1.0F;
            }

            f3 = f2 / f3;
            f *= f3;
            f1 *= f3;
            float f4 = MathHelper.func_76126_a(this.field_70177_z * 3.1415927F / 180.0F);
            float f5 = MathHelper.func_76134_b(this.field_70177_z * 3.1415927F / 180.0F);

            this.field_70159_w += (double) (f * f5 - f1 * f4);
            this.field_70179_y += (double) (f1 * f5 + f * f4);
        }
    }

    public float func_70013_c(float f) {
        int i = MathHelper.func_76128_c(this.field_70165_t);
        int j = MathHelper.func_76128_c(this.field_70161_v);

        if (this.field_70170_p.func_72899_e(i, 0, j)) {
            double d0 = (this.field_70121_D.field_72337_e - this.field_70121_D.field_72338_b) * 0.66D;
            int k = MathHelper.func_76128_c(this.field_70163_u - (double) this.field_70129_M + d0);

            return this.field_70170_p.func_72801_o(i, k, j);
        } else {
            return 0.0F;
        }
    }

    public void func_70029_a(World world) {
        this.field_70170_p = world;
    }

    public void func_70080_a(double d0, double d1, double d2, float f, float f1) {
        this.field_70169_q = this.field_70165_t = d0;
        this.field_70167_r = this.field_70163_u = d1;
        this.field_70166_s = this.field_70161_v = d2;
        this.field_70126_B = this.field_70177_z = f;
        this.field_70127_C = this.field_70125_A = f1;
        this.field_70139_V = 0.0F;
        double d3 = (double) (this.field_70126_B - f);

        if (d3 < -180.0D) {
            this.field_70126_B += 360.0F;
        }

        if (d3 >= 180.0D) {
            this.field_70126_B -= 360.0F;
        }

        this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
        this.func_70101_b(f, f1);
    }

    public void func_70012_b(double d0, double d1, double d2, float f, float f1) {
        this.field_70142_S = this.field_70169_q = this.field_70165_t = d0;
        this.field_70137_T = this.field_70167_r = this.field_70163_u = d1 + (double) this.field_70129_M;
        this.field_70136_U = this.field_70166_s = this.field_70161_v = d2;
        this.field_70177_z = f;
        this.field_70125_A = f1;
        this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
    }

    public float func_70032_d(Entity entity) {
        float f = (float) (this.field_70165_t - entity.field_70165_t);
        float f1 = (float) (this.field_70163_u - entity.field_70163_u);
        float f2 = (float) (this.field_70161_v - entity.field_70161_v);

        return MathHelper.func_76129_c(f * f + f1 * f1 + f2 * f2);
    }

    public double func_70092_e(double d0, double d1, double d2) {
        double d3 = this.field_70165_t - d0;
        double d4 = this.field_70163_u - d1;
        double d5 = this.field_70161_v - d2;

        return d3 * d3 + d4 * d4 + d5 * d5;
    }

    public double func_70011_f(double d0, double d1, double d2) {
        double d3 = this.field_70165_t - d0;
        double d4 = this.field_70163_u - d1;
        double d5 = this.field_70161_v - d2;

        return (double) MathHelper.func_76133_a(d3 * d3 + d4 * d4 + d5 * d5);
    }

    public double func_70068_e(Entity entity) {
        double d0 = this.field_70165_t - entity.field_70165_t;
        double d1 = this.field_70163_u - entity.field_70163_u;
        double d2 = this.field_70161_v - entity.field_70161_v;

        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public void func_70100_b_(EntityPlayer entityplayer) {}

    public void func_70108_f(Entity entity) {
        if (entity.field_70153_n != this && entity.field_70154_o != this) {
            double d0 = entity.field_70165_t - this.field_70165_t;
            double d1 = entity.field_70161_v - this.field_70161_v;
            double d2 = MathHelper.func_76132_a(d0, d1);

            if (d2 >= 0.009999999776482582D) {
                d2 = (double) MathHelper.func_76133_a(d2);
                d0 /= d2;
                d1 /= d2;
                double d3 = 1.0D / d2;

                if (d3 > 1.0D) {
                    d3 = 1.0D;
                }

                d0 *= d3;
                d1 *= d3;
                d0 *= 0.05000000074505806D;
                d1 *= 0.05000000074505806D;
                d0 *= (double) (1.0F - this.field_70144_Y);
                d1 *= (double) (1.0F - this.field_70144_Y);
                this.func_70024_g(-d0, 0.0D, -d1);
                entity.func_70024_g(d0, 0.0D, d1);
            }

        }
    }

    public void func_70024_g(double d0, double d1, double d2) {
        this.field_70159_w += d0;
        this.field_70181_x += d1;
        this.field_70179_y += d2;
        this.field_70160_al = true;
    }

    protected void func_70018_K() {
        this.field_70133_I = true;
    }

    public boolean func_70097_a(DamageSource damagesource, float f) {
        if (this.func_85032_ar()) {
            return false;
        } else {
            this.func_70018_K();
            return false;
        }
    }

    public boolean func_70067_L() {
        return false;
    }

    public boolean func_70104_M() {
        return false;
    }

    public void func_70084_c(Entity entity, int i) {}

    public boolean func_98035_c(NBTTagCompound nbttagcompound) {
        String s = this.func_70022_Q();

        if (!this.field_70128_L && s != null) {
            nbttagcompound.func_74778_a("id", s);
            this.func_70109_d(nbttagcompound);
            return true;
        } else {
            return false;
        }
    }

    public boolean func_70039_c(NBTTagCompound nbttagcompound) {
        String s = this.func_70022_Q();

        if (!this.field_70128_L && s != null && this.field_70153_n == null) {
            nbttagcompound.func_74778_a("id", s);
            this.func_70109_d(nbttagcompound);
            return true;
        } else {
            return false;
        }
    }

    public void func_70109_d(NBTTagCompound nbttagcompound) {
        try {
            nbttagcompound.func_74782_a("Pos", this.func_70087_a(this.field_70165_t, this.field_70163_u + (double) this.field_70139_V, this.field_70161_v));
            nbttagcompound.func_74782_a("Motion", this.func_70087_a(this.field_70159_w, this.field_70181_x, this.field_70179_y));
            nbttagcompound.func_74782_a("Rotation", this.func_70049_a(this.field_70177_z, this.field_70125_A));
            nbttagcompound.func_74776_a("FallDistance", this.field_70143_R);
            nbttagcompound.func_74777_a("Fire", (short) this.field_70151_c);
            nbttagcompound.func_74777_a("Air", (short) this.func_70086_ai());
            nbttagcompound.func_74757_a("OnGround", this.field_70122_E);
            nbttagcompound.func_74768_a("Dimension", this.field_71093_bK);
            nbttagcompound.func_74757_a("Invulnerable", this.field_83001_bt);
            nbttagcompound.func_74768_a("PortalCooldown", this.field_71088_bW);
            nbttagcompound.func_74772_a("UUIDMost", this.func_110124_au().getMostSignificantBits());
            nbttagcompound.func_74772_a("UUIDLeast", this.func_110124_au().getLeastSignificantBits());
            this.func_70014_b(nbttagcompound);
            if (this.field_70154_o != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                if (this.field_70154_o.func_98035_c(nbttagcompound1)) {
                    nbttagcompound.func_74782_a("Riding", nbttagcompound1);
                }
            }

        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.func_85055_a(throwable, "Saving entity NBT");
            CrashReportCategory crashreportcategory = crashreport.func_85058_a("Entity being saved");

            this.func_85029_a(crashreportcategory);
            throw new ReportedException(crashreport);
        }
    }

    public void func_70020_e(NBTTagCompound nbttagcompound) {
        try {
            NBTTagList nbttaglist = nbttagcompound.func_150295_c("Pos", 6);
            NBTTagList nbttaglist1 = nbttagcompound.func_150295_c("Motion", 6);
            NBTTagList nbttaglist2 = nbttagcompound.func_150295_c("Rotation", 5);

            this.field_70159_w = nbttaglist1.func_150309_d(0);
            this.field_70181_x = nbttaglist1.func_150309_d(1);
            this.field_70179_y = nbttaglist1.func_150309_d(2);
            if (Math.abs(this.field_70159_w) > 10.0D) {
                this.field_70159_w = 0.0D;
            }

            if (Math.abs(this.field_70181_x) > 10.0D) {
                this.field_70181_x = 0.0D;
            }

            if (Math.abs(this.field_70179_y) > 10.0D) {
                this.field_70179_y = 0.0D;
            }

            this.field_70169_q = this.field_70142_S = this.field_70165_t = nbttaglist.func_150309_d(0);
            this.field_70167_r = this.field_70137_T = this.field_70163_u = nbttaglist.func_150309_d(1);
            this.field_70166_s = this.field_70136_U = this.field_70161_v = nbttaglist.func_150309_d(2);
            this.field_70126_B = this.field_70177_z = nbttaglist2.func_150308_e(0);
            this.field_70127_C = this.field_70125_A = nbttaglist2.func_150308_e(1);
            this.field_70143_R = nbttagcompound.func_74760_g("FallDistance");
            this.field_70151_c = nbttagcompound.func_74765_d("Fire");
            this.func_70050_g(nbttagcompound.func_74765_d("Air"));
            this.field_70122_E = nbttagcompound.func_74767_n("OnGround");
            this.field_71093_bK = nbttagcompound.func_74762_e("Dimension");
            this.field_83001_bt = nbttagcompound.func_74767_n("Invulnerable");
            this.field_71088_bW = nbttagcompound.func_74762_e("PortalCooldown");
            if (nbttagcompound.func_150297_b("UUIDMost", 4) && nbttagcompound.func_150297_b("UUIDLeast", 4)) {
                this.field_96093_i = new UUID(nbttagcompound.func_74763_f("UUIDMost"), nbttagcompound.func_74763_f("UUIDLeast"));
            }

            this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
            this.func_70101_b(this.field_70177_z, this.field_70125_A);
            this.func_70037_a(nbttagcompound);
            if (this.func_142008_O()) {
                this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
            }

        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.func_85055_a(throwable, "Loading entity NBT");
            CrashReportCategory crashreportcategory = crashreport.func_85058_a("Entity being loaded");

            this.func_85029_a(crashreportcategory);
            throw new ReportedException(crashreport);
        }
    }

    protected boolean func_142008_O() {
        return true;
    }

    protected final String func_70022_Q() {
        return EntityList.func_75621_b(this);
    }

    protected abstract void func_70037_a(NBTTagCompound nbttagcompound);

    protected abstract void func_70014_b(NBTTagCompound nbttagcompound);

    public void func_110123_P() {}

    protected NBTTagList func_70087_a(double... adouble) {
        NBTTagList nbttaglist = new NBTTagList();
        double[] adouble1 = adouble;
        int i = adouble.length;

        for (int j = 0; j < i; ++j) {
            double d0 = adouble1[j];

            nbttaglist.func_74742_a(new NBTTagDouble(d0));
        }

        return nbttaglist;
    }

    protected NBTTagList func_70049_a(float... afloat) {
        NBTTagList nbttaglist = new NBTTagList();
        float[] afloat1 = afloat;
        int i = afloat.length;

        for (int j = 0; j < i; ++j) {
            float f = afloat1[j];

            nbttaglist.func_74742_a(new NBTTagFloat(f));
        }

        return nbttaglist;
    }

    public EntityItem func_145779_a(Item item, int i) {
        return this.func_145778_a(item, i, 0.0F);
    }

    public EntityItem func_145778_a(Item item, int i, float f) {
        return this.func_70099_a(new ItemStack(item, i, 0), f);
    }

    public EntityItem func_70099_a(ItemStack itemstack, float f) {
        if (itemstack.field_77994_a != 0 && itemstack.func_77973_b() != null) {
            EntityItem entityitem = new EntityItem(this.field_70170_p, this.field_70165_t, this.field_70163_u + (double) f, this.field_70161_v, itemstack);

            entityitem.field_145804_b = 10;
            this.field_70170_p.func_72838_d(entityitem);
            return entityitem;
        } else {
            return null;
        }
    }

    public boolean func_70089_S() {
        return !this.field_70128_L;
    }

    public boolean func_70094_T() {
        for (int i = 0; i < 8; ++i) {
            float f = ((float) ((i >> 0) % 2) - 0.5F) * this.field_70130_N * 0.8F;
            float f1 = ((float) ((i >> 1) % 2) - 0.5F) * 0.1F;
            float f2 = ((float) ((i >> 2) % 2) - 0.5F) * this.field_70130_N * 0.8F;
            int j = MathHelper.func_76128_c(this.field_70165_t + (double) f);
            int k = MathHelper.func_76128_c(this.field_70163_u + (double) this.func_70047_e() + (double) f1);
            int l = MathHelper.func_76128_c(this.field_70161_v + (double) f2);

            if (this.field_70170_p.func_147439_a(j, k, l).func_149721_r()) {
                return true;
            }
        }

        return false;
    }

    public boolean func_130002_c(EntityPlayer entityplayer) {
        return false;
    }

    public AxisAlignedBB func_70114_g(Entity entity) {
        return null;
    }

    public void func_70098_U() {
        if (this.field_70154_o.field_70128_L) {
            this.field_70154_o = null;
        } else {
            this.field_70159_w = 0.0D;
            this.field_70181_x = 0.0D;
            this.field_70179_y = 0.0D;
            this.func_70071_h_();
            if (this.field_70154_o != null) {
                this.field_70154_o.func_70043_V();
                this.field_70147_f += (double) (this.field_70154_o.field_70177_z - this.field_70154_o.field_70126_B);

                for (this.field_70149_e += (double) (this.field_70154_o.field_70125_A - this.field_70154_o.field_70127_C); this.field_70147_f >= 180.0D; this.field_70147_f -= 360.0D) {
                    ;
                }

                while (this.field_70147_f < -180.0D) {
                    this.field_70147_f += 360.0D;
                }

                while (this.field_70149_e >= 180.0D) {
                    this.field_70149_e -= 360.0D;
                }

                while (this.field_70149_e < -180.0D) {
                    this.field_70149_e += 360.0D;
                }

                double d0 = this.field_70147_f * 0.5D;
                double d1 = this.field_70149_e * 0.5D;
                float f = 10.0F;

                if (d0 > (double) f) {
                    d0 = (double) f;
                }

                if (d0 < (double) (-f)) {
                    d0 = (double) (-f);
                }

                if (d1 > (double) f) {
                    d1 = (double) f;
                }

                if (d1 < (double) (-f)) {
                    d1 = (double) (-f);
                }

                this.field_70147_f -= d0;
                this.field_70149_e -= d1;
            }
        }
    }

    public void func_70043_V() {
        if (this.field_70153_n != null) {
            this.field_70153_n.func_70107_b(this.field_70165_t, this.field_70163_u + this.func_70042_X() + this.field_70153_n.func_70033_W(), this.field_70161_v);
        }
    }

    public double func_70033_W() {
        return (double) this.field_70129_M;
    }

    public double func_70042_X() {
        return (double) this.field_70131_O * 0.75D;
    }

    public void func_70078_a(Entity entity) {
        this.field_70149_e = 0.0D;
        this.field_70147_f = 0.0D;
        if (entity == null) {
            if (this.field_70154_o != null) {
                this.func_70012_b(this.field_70154_o.field_70165_t, this.field_70154_o.field_70121_D.field_72338_b + (double) this.field_70154_o.field_70131_O, this.field_70154_o.field_70161_v, this.field_70177_z, this.field_70125_A);
                this.field_70154_o.field_70153_n = null;
            }

            this.field_70154_o = null;
        } else {
            if (this.field_70154_o != null) {
                this.field_70154_o.field_70153_n = null;
            }

            if (entity != null) {
                for (Entity entity1 = entity.field_70154_o; entity1 != null; entity1 = entity1.field_70154_o) {
                    if (entity1 == this) {
                        return;
                    }
                }
            }

            this.field_70154_o = entity;
            entity.field_70153_n = this;
        }
    }

    public float func_70111_Y() {
        return 0.1F;
    }

    public Vec3 func_70040_Z() {
        return null;
    }

    public void func_70063_aa() {
        if (this.field_71088_bW > 0) {
            this.field_71088_bW = this.func_82147_ab();
        } else {
            double d0 = this.field_70169_q - this.field_70165_t;
            double d1 = this.field_70166_s - this.field_70161_v;

            if (!this.field_70170_p.field_72995_K && !this.field_71087_bX) {
                this.field_82152_aq = Direction.func_82372_a(d0, d1);
            }

            this.field_71087_bX = true;
        }
    }

    public int func_82147_ab() {
        return 300;
    }

    public ItemStack[] func_70035_c() {
        return null;
    }

    public void func_70062_b(int i, ItemStack itemstack) {}

    public boolean func_70027_ad() {
        boolean flag = this.field_70170_p != null && this.field_70170_p.field_72995_K;

        return !this.field_70178_ae && (this.field_70151_c > 0 || flag && this.func_70083_f(0));
    }

    public boolean func_70115_ae() {
        return this.field_70154_o != null;
    }

    public boolean func_70093_af() {
        return this.func_70083_f(1);
    }

    public void func_70095_a(boolean flag) {
        this.func_70052_a(1, flag);
    }

    public boolean func_70051_ag() {
        return this.func_70083_f(3);
    }

    public void func_70031_b(boolean flag) {
        this.func_70052_a(3, flag);
    }

    public boolean func_82150_aj() {
        return this.func_70083_f(5);
    }

    public void func_82142_c(boolean flag) {
        this.func_70052_a(5, flag);
    }

    public void func_70019_c(boolean flag) {
        this.func_70052_a(4, flag);
    }

    protected boolean func_70083_f(int i) {
        return (this.field_70180_af.func_75683_a(0) & 1 << i) != 0;
    }

    protected void func_70052_a(int i, boolean flag) {
        byte b0 = this.field_70180_af.func_75683_a(0);

        if (flag) {
            this.field_70180_af.func_75692_b(0, (byte) (b0 | 1 << i));
        } else {
            this.field_70180_af.func_75692_b(0, (byte) (b0 & ~(1 << i)));
        }

    }

    public int func_70086_ai() {
        return this.field_70180_af.func_75693_b(1);
    }

    public void func_70050_g(int i) {
        this.field_70180_af.func_75692_b(1, (short) i);
    }

    public void func_70077_a(EntityLightningBolt entitylightningbolt) {
        this.func_70081_e(5);
        ++this.field_70151_c;
        if (this.field_70151_c == 0) {
            this.func_70015_d(8);
        }

    }

    public void func_70074_a(EntityLivingBase entitylivingbase) {}

    protected boolean func_145771_j(double d0, double d1, double d2) {
        int i = MathHelper.func_76128_c(d0);
        int j = MathHelper.func_76128_c(d1);
        int k = MathHelper.func_76128_c(d2);
        double d3 = d0 - (double) i;
        double d4 = d1 - (double) j;
        double d5 = d2 - (double) k;
        List list = this.field_70170_p.func_147461_a(this.field_70121_D);

        if (list.isEmpty() && !this.field_70170_p.func_147469_q(i, j, k)) {
            return false;
        } else {
            boolean flag = !this.field_70170_p.func_147469_q(i - 1, j, k);
            boolean flag1 = !this.field_70170_p.func_147469_q(i + 1, j, k);
            boolean flag2 = !this.field_70170_p.func_147469_q(i, j - 1, k);
            boolean flag3 = !this.field_70170_p.func_147469_q(i, j + 1, k);
            boolean flag4 = !this.field_70170_p.func_147469_q(i, j, k - 1);
            boolean flag5 = !this.field_70170_p.func_147469_q(i, j, k + 1);
            byte b0 = 3;
            double d6 = 9999.0D;

            if (flag && d3 < d6) {
                d6 = d3;
                b0 = 0;
            }

            if (flag1 && 1.0D - d3 < d6) {
                d6 = 1.0D - d3;
                b0 = 1;
            }

            if (flag3 && 1.0D - d4 < d6) {
                d6 = 1.0D - d4;
                b0 = 3;
            }

            if (flag4 && d5 < d6) {
                d6 = d5;
                b0 = 4;
            }

            if (flag5 && 1.0D - d5 < d6) {
                d6 = 1.0D - d5;
                b0 = 5;
            }

            float f = this.field_70146_Z.nextFloat() * 0.2F + 0.1F;

            if (b0 == 0) {
                this.field_70159_w = (double) (-f);
            }

            if (b0 == 1) {
                this.field_70159_w = (double) f;
            }

            if (b0 == 2) {
                this.field_70181_x = (double) (-f);
            }

            if (b0 == 3) {
                this.field_70181_x = (double) f;
            }

            if (b0 == 4) {
                this.field_70179_y = (double) (-f);
            }

            if (b0 == 5) {
                this.field_70179_y = (double) f;
            }

            return true;
        }
    }

    public void func_70110_aj() {
        this.field_70134_J = true;
        this.field_70143_R = 0.0F;
    }

    public String func_70005_c_() {
        String s = EntityList.func_75621_b(this);

        if (s == null) {
            s = "generic";
        }

        return StatCollector.func_74838_a("entity." + s + ".name");
    }

    public Entity[] func_70021_al() {
        return null;
    }

    public boolean func_70028_i(Entity entity) {
        return this == entity;
    }

    public float func_70079_am() {
        return 0.0F;
    }

    public boolean func_70075_an() {
        return true;
    }

    public boolean func_85031_j(Entity entity) {
        return false;
    }

    public String toString() {
        return String.format("%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", this.getClass().getSimpleName(), this.func_70005_c_(), this.field_145783_c, this.field_70170_p == null ? "~NULL~" : this.field_70170_p.func_72912_H().func_76065_j(), this.field_70165_t, this.field_70163_u, this.field_70161_v);
    }

    public boolean func_85032_ar() {
        return this.field_83001_bt;
    }

    public void func_82149_j(Entity entity) {
        this.func_70012_b(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v, entity.field_70177_z, entity.field_70125_A);
    }

    public void func_82141_a(Entity entity, boolean flag) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        entity.func_70109_d(nbttagcompound);
        this.func_70020_e(nbttagcompound);
        this.field_71088_bW = entity.field_71088_bW;
        this.field_82152_aq = entity.field_82152_aq;
    }

    public void func_71027_c(int i) {
        if (!this.field_70170_p.field_72995_K && !this.field_70128_L) {
            this.field_70170_p.field_72984_F.func_76320_a("changeDimension");
            MinecraftServer minecraftserver = MinecraftServer.func_71276_C();
            int j = this.field_71093_bK;
            WorldServer worldserver = minecraftserver.func_71218_a(j);
            WorldServer worldserver1 = minecraftserver.func_71218_a(i);

            this.field_71093_bK = i;
            if (j == 1 && i == 1) {
                worldserver1 = minecraftserver.func_71218_a(0);
                this.field_71093_bK = 0;
            }

            this.field_70170_p.func_72900_e(this);
            this.field_70128_L = false;
            this.field_70170_p.field_72984_F.func_76320_a("reposition");
            minecraftserver.func_71203_ab().func_82448_a(this, j, worldserver, worldserver1);
            this.field_70170_p.field_72984_F.func_76318_c("reloading");
            Entity entity = EntityList.func_75620_a(EntityList.func_75621_b(this), worldserver1);

            if (entity != null) {
                entity.func_82141_a(this, true);
                if (j == 1 && i == 1) {
                    ChunkCoordinates chunkcoordinates = worldserver1.func_72861_E();

                    chunkcoordinates.field_71572_b = this.field_70170_p.func_72825_h(chunkcoordinates.field_71574_a, chunkcoordinates.field_71573_c);
                    entity.func_70012_b((double) chunkcoordinates.field_71574_a, (double) chunkcoordinates.field_71572_b, (double) chunkcoordinates.field_71573_c, entity.field_70177_z, entity.field_70125_A);
                }

                worldserver1.func_72838_d(entity);
            }

            this.field_70128_L = true;
            this.field_70170_p.field_72984_F.func_76319_b();
            worldserver.func_82742_i();
            worldserver1.func_82742_i();
            this.field_70170_p.field_72984_F.func_76319_b();
        }
    }

    public float func_145772_a(Explosion explosion, World world, int i, int j, int k, Block block) {
        return block.func_149638_a(this);
    }

    public boolean func_145774_a(Explosion explosion, World world, int i, int j, int k, Block block, float f) {
        return true;
    }

    public int func_82143_as() {
        return 3;
    }

    public int func_82148_at() {
        return this.field_82152_aq;
    }

    public boolean func_145773_az() {
        return false;
    }

    public void func_85029_a(CrashReportCategory crashreportcategory) {
        crashreportcategory.func_71500_a("Entity Type", new Entity$1(this));
        crashreportcategory.func_71507_a("Entity ID", this.field_145783_c);
        crashreportcategory.func_71500_a("Entity Name", new Entity$2(this));
        crashreportcategory.func_71507_a("Entity's Exact location", String.format("%.2f, %.2f, %.2f", this.field_70165_t, this.field_70163_u, this.field_70161_v));
        crashreportcategory.func_71507_a("Entity's Block location", CrashReportCategory.func_85071_a(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v)));
        crashreportcategory.func_71507_a("Entity's Momentum", String.format("%.2f, %.2f, %.2f", this.field_70159_w, this.field_70181_x, this.field_70179_y));
    }

    public UUID func_110124_au() {
        return this.field_96093_i;
    }

    public boolean func_96092_aw() {
        return true;
    }

    public IChatComponent func_145748_c_() {
        return new ChatComponentText(this.func_70005_c_());
    }

    public void func_145781_i(int i) {}
}
