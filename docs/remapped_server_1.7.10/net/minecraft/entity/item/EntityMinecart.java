package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityMinecart extends Entity {

    private boolean field_70499_f;
    private String field_94102_c;
    private static final int[][][] field_70500_g = new int[][][]{{{0, 0, -1}, {0, 0, 1}}, {{-1, 0, 0}, {1, 0, 0}}, {{-1, -1, 0}, {1, 0, 0}}, {{-1, 0, 0}, {1, -1, 0}}, {{0, 0, -1}, {0, -1, 1}}, {{0, -1, -1}, {0, 0, 1}}, {{0, 0, 1}, {1, 0, 0}}, {{0, 0, 1}, {-1, 0, 0}}, {{0, 0, -1}, {-1, 0, 0}}, {{0, 0, -1}, {1, 0, 0}}};
    private int field_70510_h;
    private double field_70511_i;
    private double field_70509_j;
    private double field_70514_an;
    private double field_70512_ao;
    private double field_70513_ap;

    public EntityMinecart(World world) {
        super(world);
        this.field_70156_m = true;
        this.func_70105_a(0.98F, 0.7F);
        this.field_70129_M = this.field_70131_O / 2.0F;
    }

    public static EntityMinecart func_94090_a(World world, double d0, double d1, double d2, int i) {
        switch (i) {
            case 1:
                return new EntityMinecartChest(world, d0, d1, d2);
            case 2:
                return new EntityMinecartFurnace(world, d0, d1, d2);
            case 3:
                return new EntityMinecartTNT(world, d0, d1, d2);
            case 4:
                return new EntityMinecartMobSpawner(world, d0, d1, d2);
            case 5:
                return new EntityMinecartHopper(world, d0, d1, d2);
            case 6:
                return new EntityMinecartCommandBlock(world, d0, d1, d2);
            default:
                return new EntityMinecartEmpty(world, d0, d1, d2);
        }
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
        this.field_70180_af.func_75682_a(20, new Integer(0));
        this.field_70180_af.func_75682_a(21, new Integer(6));
        this.field_70180_af.func_75682_a(22, (byte) 0);
    }

    @Override
    public AxisAlignedBB func_70114_g(Entity entity) {
        return entity.func_70104_M() ? entity.field_70121_D : null;
    }

    @Override
    public AxisAlignedBB func_70046_E() {
        return null;
    }

    @Override
    public boolean func_70104_M() {
        return true;
    }

    public EntityMinecart(World world, double d0, double d1, double d2) {
        this(world);
        this.func_70107_b(d0, d1, d2);
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
        if (!this.field_70170_p.field_72995_K && !this.field_70128_L) {
            if (this.func_85032_ar()) {
                return false;
            } else {
                this.func_70494_i(-this.func_70493_k());
                this.func_70497_h(10);
                this.func_70018_K();
                this.func_70492_c(this.func_70491_i() + f * 10.0F);
                boolean flag = damagesource.func_76346_g() instanceof EntityPlayer && ((EntityPlayer) damagesource.func_76346_g()).field_71075_bZ.field_75098_d;

                if (flag || this.func_70491_i() > 40.0F) {
                    if (this.field_70153_n != null) {
                        this.field_70153_n.func_70078_a(this);
                    }

                    if (flag && !this.func_145818_k_()) {
                        this.func_70106_y();
                    } else {
                        this.func_94095_a(damagesource);
                    }
                }

                return true;
            }
        } else {
            return true;
        }
    }

    public void func_94095_a(DamageSource damagesource) {
        this.func_70106_y();
        ItemStack itemstack = new ItemStack(Items.field_151143_au, 1);

        if (this.field_94102_c != null) {
            itemstack.func_151001_c(this.field_94102_c);
        }

        this.func_70099_a(itemstack, 0.0F);
    }

    @Override
    public boolean func_70067_L() {
        return !this.field_70128_L;
    }

    @Override
    public void func_70106_y() {
        super.func_70106_y();
    }

    @Override
    public void func_70071_h_() {
        if (this.func_70496_j() > 0) {
            this.func_70497_h(this.func_70496_j() - 1);
        }

        if (this.func_70491_i() > 0.0F) {
            this.func_70492_c(this.func_70491_i() - 1.0F);
        }

        if (this.field_70163_u < -64.0D) {
            this.func_70076_C();
        }

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

        if (this.field_70170_p.field_72995_K) {
            if (this.field_70510_h > 0) {
                double d0 = this.field_70165_t + (this.field_70511_i - this.field_70165_t) / (double) this.field_70510_h;
                double d1 = this.field_70163_u + (this.field_70509_j - this.field_70163_u) / (double) this.field_70510_h;
                double d2 = this.field_70161_v + (this.field_70514_an - this.field_70161_v) / (double) this.field_70510_h;
                double d3 = MathHelper.func_76138_g(this.field_70512_ao - (double) this.field_70177_z);

                this.field_70177_z = (float) ((double) this.field_70177_z + d3 / (double) this.field_70510_h);
                this.field_70125_A = (float) ((double) this.field_70125_A + (this.field_70513_ap - (double) this.field_70125_A) / (double) this.field_70510_h);
                --this.field_70510_h;
                this.func_70107_b(d0, d1, d2);
                this.func_70101_b(this.field_70177_z, this.field_70125_A);
            } else {
                this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
                this.func_70101_b(this.field_70177_z, this.field_70125_A);
            }

        } else {
            this.field_70169_q = this.field_70165_t;
            this.field_70167_r = this.field_70163_u;
            this.field_70166_s = this.field_70161_v;
            this.field_70181_x -= 0.03999999910593033D;
            int k = MathHelper.func_76128_c(this.field_70165_t);

            i = MathHelper.func_76128_c(this.field_70163_u);
            j = MathHelper.func_76128_c(this.field_70161_v);
            if (BlockRailBase.func_150049_b_(this.field_70170_p, k, i - 1, j)) {
                --i;
            }

            double d4 = 0.4D;
            double d5 = 0.0078125D;
            Block block = this.field_70170_p.func_147439_a(k, i, j);

            if (BlockRailBase.func_150051_a(block)) {
                int l = this.field_70170_p.func_72805_g(k, i, j);

                this.func_145821_a(k, i, j, d4, d5, block, l);
                if (block == Blocks.field_150408_cc) {
                    this.func_96095_a(k, i, j, (l & 8) != 0);
                }
            } else {
                this.func_94088_b(d4);
            }

            this.func_145775_I();
            this.field_70125_A = 0.0F;
            double d6 = this.field_70169_q - this.field_70165_t;
            double d7 = this.field_70166_s - this.field_70161_v;

            if (d6 * d6 + d7 * d7 > 0.001D) {
                this.field_70177_z = (float) (Math.atan2(d7, d6) * 180.0D / Math.PI);
                if (this.field_70499_f) {
                    this.field_70177_z += 180.0F;
                }
            }

            double d8 = (double) MathHelper.func_76142_g(this.field_70177_z - this.field_70126_B);

            if (d8 < -170.0D || d8 >= 170.0D) {
                this.field_70177_z += 180.0F;
                this.field_70499_f = !this.field_70499_f;
            }

            this.func_70101_b(this.field_70177_z, this.field_70125_A);
            List list = this.field_70170_p.func_72839_b(this, this.field_70121_D.func_72314_b(0.20000000298023224D, 0.0D, 0.20000000298023224D));

            if (list != null && !list.isEmpty()) {
                for (int i1 = 0; i1 < list.size(); ++i1) {
                    Entity entity = (Entity) list.get(i1);

                    if (entity != this.field_70153_n && entity.func_70104_M() && entity instanceof EntityMinecart) {
                        entity.func_70108_f(this);
                    }
                }
            }

            if (this.field_70153_n != null && this.field_70153_n.field_70128_L) {
                if (this.field_70153_n.field_70154_o == this) {
                    this.field_70153_n.field_70154_o = null;
                }

                this.field_70153_n = null;
            }

        }
    }

    public void func_96095_a(int i, int j, int k, boolean flag) {}

    protected void func_94088_b(double d0) {
        if (this.field_70159_w < -d0) {
            this.field_70159_w = -d0;
        }

        if (this.field_70159_w > d0) {
            this.field_70159_w = d0;
        }

        if (this.field_70179_y < -d0) {
            this.field_70179_y = -d0;
        }

        if (this.field_70179_y > d0) {
            this.field_70179_y = d0;
        }

        if (this.field_70122_E) {
            this.field_70159_w *= 0.5D;
            this.field_70181_x *= 0.5D;
            this.field_70179_y *= 0.5D;
        }

        this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
        if (!this.field_70122_E) {
            this.field_70159_w *= 0.949999988079071D;
            this.field_70181_x *= 0.949999988079071D;
            this.field_70179_y *= 0.949999988079071D;
        }

    }

    protected void func_145821_a(int i, int j, int k, double d0, double d1, Block block, int l) {
        this.field_70143_R = 0.0F;
        Vec3 vec3 = this.func_70489_a(this.field_70165_t, this.field_70163_u, this.field_70161_v);

        this.field_70163_u = (double) j;
        boolean flag = false;
        boolean flag1 = false;

        if (block == Blocks.field_150318_D) {
            flag = (l & 8) != 0;
            flag1 = !flag;
        }

        if (((BlockRailBase) block).func_150050_e()) {
            l &= 7;
        }

        if (l >= 2 && l <= 5) {
            this.field_70163_u = (double) (j + 1);
        }

        if (l == 2) {
            this.field_70159_w -= d1;
        }

        if (l == 3) {
            this.field_70159_w += d1;
        }

        if (l == 4) {
            this.field_70179_y += d1;
        }

        if (l == 5) {
            this.field_70179_y -= d1;
        }

        int[][] aint = EntityMinecart.field_70500_g[l];
        double d2 = (double) (aint[1][0] - aint[0][0]);
        double d3 = (double) (aint[1][2] - aint[0][2]);
        double d4 = Math.sqrt(d2 * d2 + d3 * d3);
        double d5 = this.field_70159_w * d2 + this.field_70179_y * d3;

        if (d5 < 0.0D) {
            d2 = -d2;
            d3 = -d3;
        }

        double d6 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);

        if (d6 > 2.0D) {
            d6 = 2.0D;
        }

        this.field_70159_w = d6 * d2 / d4;
        this.field_70179_y = d6 * d3 / d4;
        double d7;
        double d8;
        double d9;
        double d10;

        if (this.field_70153_n != null && this.field_70153_n instanceof EntityLivingBase) {
            d7 = (double) ((EntityLivingBase) this.field_70153_n).field_70701_bs;
            if (d7 > 0.0D) {
                d8 = -Math.sin((double) (this.field_70153_n.field_70177_z * 3.1415927F / 180.0F));
                d9 = Math.cos((double) (this.field_70153_n.field_70177_z * 3.1415927F / 180.0F));
                d10 = this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y;
                if (d10 < 0.01D) {
                    this.field_70159_w += d8 * 0.1D;
                    this.field_70179_y += d9 * 0.1D;
                    flag1 = false;
                }
            }
        }

        if (flag1) {
            d7 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
            if (d7 < 0.03D) {
                this.field_70159_w *= 0.0D;
                this.field_70181_x *= 0.0D;
                this.field_70179_y *= 0.0D;
            } else {
                this.field_70159_w *= 0.5D;
                this.field_70181_x *= 0.0D;
                this.field_70179_y *= 0.5D;
            }
        }

        d7 = 0.0D;
        d8 = (double) i + 0.5D + (double) aint[0][0] * 0.5D;
        d9 = (double) k + 0.5D + (double) aint[0][2] * 0.5D;
        d10 = (double) i + 0.5D + (double) aint[1][0] * 0.5D;
        double d11 = (double) k + 0.5D + (double) aint[1][2] * 0.5D;

        d2 = d10 - d8;
        d3 = d11 - d9;
        double d12;
        double d13;

        if (d2 == 0.0D) {
            this.field_70165_t = (double) i + 0.5D;
            d7 = this.field_70161_v - (double) k;
        } else if (d3 == 0.0D) {
            this.field_70161_v = (double) k + 0.5D;
            d7 = this.field_70165_t - (double) i;
        } else {
            d12 = this.field_70165_t - d8;
            d13 = this.field_70161_v - d9;
            d7 = (d12 * d2 + d13 * d3) * 2.0D;
        }

        this.field_70165_t = d8 + d2 * d7;
        this.field_70161_v = d9 + d3 * d7;
        this.func_70107_b(this.field_70165_t, this.field_70163_u + (double) this.field_70129_M, this.field_70161_v);
        d12 = this.field_70159_w;
        d13 = this.field_70179_y;
        if (this.field_70153_n != null) {
            d12 *= 0.75D;
            d13 *= 0.75D;
        }

        if (d12 < -d0) {
            d12 = -d0;
        }

        if (d12 > d0) {
            d12 = d0;
        }

        if (d13 < -d0) {
            d13 = -d0;
        }

        if (d13 > d0) {
            d13 = d0;
        }

        this.func_70091_d(d12, 0.0D, d13);
        if (aint[0][1] != 0 && MathHelper.func_76128_c(this.field_70165_t) - i == aint[0][0] && MathHelper.func_76128_c(this.field_70161_v) - k == aint[0][2]) {
            this.func_70107_b(this.field_70165_t, this.field_70163_u + (double) aint[0][1], this.field_70161_v);
        } else if (aint[1][1] != 0 && MathHelper.func_76128_c(this.field_70165_t) - i == aint[1][0] && MathHelper.func_76128_c(this.field_70161_v) - k == aint[1][2]) {
            this.func_70107_b(this.field_70165_t, this.field_70163_u + (double) aint[1][1], this.field_70161_v);
        }

        this.func_94101_h();
        Vec3 vec31 = this.func_70489_a(this.field_70165_t, this.field_70163_u, this.field_70161_v);

        if (vec31 != null && vec3 != null) {
            double d14 = (vec3.field_72448_b - vec31.field_72448_b) * 0.05D;

            d6 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
            if (d6 > 0.0D) {
                this.field_70159_w = this.field_70159_w / d6 * (d6 + d14);
                this.field_70179_y = this.field_70179_y / d6 * (d6 + d14);
            }

            this.func_70107_b(this.field_70165_t, vec31.field_72448_b, this.field_70161_v);
        }

        int i1 = MathHelper.func_76128_c(this.field_70165_t);
        int j1 = MathHelper.func_76128_c(this.field_70161_v);

        if (i1 != i || j1 != k) {
            d6 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
            this.field_70159_w = d6 * (double) (i1 - i);
            this.field_70179_y = d6 * (double) (j1 - k);
        }

        if (flag) {
            double d15 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);

            if (d15 > 0.01D) {
                double d16 = 0.06D;

                this.field_70159_w += this.field_70159_w / d15 * d16;
                this.field_70179_y += this.field_70179_y / d15 * d16;
            } else if (l == 1) {
                if (this.field_70170_p.func_147439_a(i - 1, j, k).func_149721_r()) {
                    this.field_70159_w = 0.02D;
                } else if (this.field_70170_p.func_147439_a(i + 1, j, k).func_149721_r()) {
                    this.field_70159_w = -0.02D;
                }
            } else if (l == 0) {
                if (this.field_70170_p.func_147439_a(i, j, k - 1).func_149721_r()) {
                    this.field_70179_y = 0.02D;
                } else if (this.field_70170_p.func_147439_a(i, j, k + 1).func_149721_r()) {
                    this.field_70179_y = -0.02D;
                }
            }
        }

    }

    protected void func_94101_h() {
        if (this.field_70153_n != null) {
            this.field_70159_w *= 0.996999979019165D;
            this.field_70181_x *= 0.0D;
            this.field_70179_y *= 0.996999979019165D;
        } else {
            this.field_70159_w *= 0.9599999785423279D;
            this.field_70181_x *= 0.0D;
            this.field_70179_y *= 0.9599999785423279D;
        }

    }

    public Vec3 func_70489_a(double d0, double d1, double d2) {
        int i = MathHelper.func_76128_c(d0);
        int j = MathHelper.func_76128_c(d1);
        int k = MathHelper.func_76128_c(d2);

        if (BlockRailBase.func_150049_b_(this.field_70170_p, i, j - 1, k)) {
            --j;
        }

        Block block = this.field_70170_p.func_147439_a(i, j, k);

        if (BlockRailBase.func_150051_a(block)) {
            int l = this.field_70170_p.func_72805_g(i, j, k);

            d1 = (double) j;
            if (((BlockRailBase) block).func_150050_e()) {
                l &= 7;
            }

            if (l >= 2 && l <= 5) {
                d1 = (double) (j + 1);
            }

            int[][] aint = EntityMinecart.field_70500_g[l];
            double d3 = 0.0D;
            double d4 = (double) i + 0.5D + (double) aint[0][0] * 0.5D;
            double d5 = (double) j + 0.5D + (double) aint[0][1] * 0.5D;
            double d6 = (double) k + 0.5D + (double) aint[0][2] * 0.5D;
            double d7 = (double) i + 0.5D + (double) aint[1][0] * 0.5D;
            double d8 = (double) j + 0.5D + (double) aint[1][1] * 0.5D;
            double d9 = (double) k + 0.5D + (double) aint[1][2] * 0.5D;
            double d10 = d7 - d4;
            double d11 = (d8 - d5) * 2.0D;
            double d12 = d9 - d6;

            if (d10 == 0.0D) {
                d0 = (double) i + 0.5D;
                d3 = d2 - (double) k;
            } else if (d12 == 0.0D) {
                d2 = (double) k + 0.5D;
                d3 = d0 - (double) i;
            } else {
                double d13 = d0 - d4;
                double d14 = d2 - d6;

                d3 = (d13 * d10 + d14 * d12) * 2.0D;
            }

            d0 = d4 + d10 * d3;
            d1 = d5 + d11 * d3;
            d2 = d6 + d12 * d3;
            if (d11 < 0.0D) {
                ++d1;
            }

            if (d11 > 0.0D) {
                d1 += 0.5D;
            }

            return Vec3.func_72443_a(d0, d1, d2);
        } else {
            return null;
        }
    }

    @Override
    protected void func_70037_a(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.func_74767_n("CustomDisplayTile")) {
            this.func_145819_k(nbttagcompound.func_74762_e("DisplayTile"));
            this.func_94092_k(nbttagcompound.func_74762_e("DisplayData"));
            this.func_94086_l(nbttagcompound.func_74762_e("DisplayOffset"));
        }

        if (nbttagcompound.func_150297_b("CustomName", 8) && nbttagcompound.func_74779_i("CustomName").length() > 0) {
            this.field_94102_c = nbttagcompound.func_74779_i("CustomName");
        }

    }

    @Override
    protected void func_70014_b(NBTTagCompound nbttagcompound) {
        if (this.func_94100_s()) {
            nbttagcompound.func_74757_a("CustomDisplayTile", true);
            nbttagcompound.func_74768_a("DisplayTile", this.func_145820_n().func_149688_o() == Material.field_151579_a ? 0 : Block.func_149682_b(this.func_145820_n()));
            nbttagcompound.func_74768_a("DisplayData", this.func_94098_o());
            nbttagcompound.func_74768_a("DisplayOffset", this.func_94099_q());
        }

        if (this.field_94102_c != null && this.field_94102_c.length() > 0) {
            nbttagcompound.func_74778_a("CustomName", this.field_94102_c);
        }

    }

    @Override
    public void func_70108_f(Entity entity) {
        if (!this.field_70170_p.field_72995_K) {
            if (entity != this.field_70153_n) {
                if (entity instanceof EntityLivingBase && !(entity instanceof EntityPlayer) && !(entity instanceof EntityIronGolem) && this.func_94087_l() == 0 && this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y > 0.01D && this.field_70153_n == null && entity.field_70154_o == null) {
                    entity.func_70078_a(this);
                }

                double d0 = entity.field_70165_t - this.field_70165_t;
                double d1 = entity.field_70161_v - this.field_70161_v;
                double d2 = d0 * d0 + d1 * d1;

                if (d2 >= 9.999999747378752E-5D) {
                    d2 = (double) MathHelper.func_76133_a(d2);
                    d0 /= d2;
                    d1 /= d2;
                    double d3 = 1.0D / d2;

                    if (d3 > 1.0D) {
                        d3 = 1.0D;
                    }

                    d0 *= d3;
                    d1 *= d3;
                    d0 *= 0.10000000149011612D;
                    d1 *= 0.10000000149011612D;
                    d0 *= (double) (1.0F - this.field_70144_Y);
                    d1 *= (double) (1.0F - this.field_70144_Y);
                    d0 *= 0.5D;
                    d1 *= 0.5D;
                    if (entity instanceof EntityMinecart) {
                        double d4 = entity.field_70165_t - this.field_70165_t;
                        double d5 = entity.field_70161_v - this.field_70161_v;
                        Vec3 vec3 = Vec3.func_72443_a(d4, 0.0D, d5).func_72432_b();
                        Vec3 vec31 = Vec3.func_72443_a((double) MathHelper.func_76134_b(this.field_70177_z * 3.1415927F / 180.0F), 0.0D, (double) MathHelper.func_76126_a(this.field_70177_z * 3.1415927F / 180.0F)).func_72432_b();
                        double d6 = Math.abs(vec3.func_72430_b(vec31));

                        if (d6 < 0.800000011920929D) {
                            return;
                        }

                        double d7 = entity.field_70159_w + this.field_70159_w;
                        double d8 = entity.field_70179_y + this.field_70179_y;

                        if (((EntityMinecart) entity).func_94087_l() == 2 && this.func_94087_l() != 2) {
                            this.field_70159_w *= 0.20000000298023224D;
                            this.field_70179_y *= 0.20000000298023224D;
                            this.func_70024_g(entity.field_70159_w - d0, 0.0D, entity.field_70179_y - d1);
                            entity.field_70159_w *= 0.949999988079071D;
                            entity.field_70179_y *= 0.949999988079071D;
                        } else if (((EntityMinecart) entity).func_94087_l() != 2 && this.func_94087_l() == 2) {
                            entity.field_70159_w *= 0.20000000298023224D;
                            entity.field_70179_y *= 0.20000000298023224D;
                            entity.func_70024_g(this.field_70159_w + d0, 0.0D, this.field_70179_y + d1);
                            this.field_70159_w *= 0.949999988079071D;
                            this.field_70179_y *= 0.949999988079071D;
                        } else {
                            d7 /= 2.0D;
                            d8 /= 2.0D;
                            this.field_70159_w *= 0.20000000298023224D;
                            this.field_70179_y *= 0.20000000298023224D;
                            this.func_70024_g(d7 - d0, 0.0D, d8 - d1);
                            entity.field_70159_w *= 0.20000000298023224D;
                            entity.field_70179_y *= 0.20000000298023224D;
                            entity.func_70024_g(d7 + d0, 0.0D, d8 + d1);
                        }
                    } else {
                        this.func_70024_g(-d0, 0.0D, -d1);
                        entity.func_70024_g(d0 / 4.0D, 0.0D, d1 / 4.0D);
                    }
                }

            }
        }
    }

    public void func_70492_c(float f) {
        this.field_70180_af.func_75692_b(19, f);
    }

    public float func_70491_i() {
        return this.field_70180_af.func_111145_d(19);
    }

    public void func_70497_h(int i) {
        this.field_70180_af.func_75692_b(17, i);
    }

    public int func_70496_j() {
        return this.field_70180_af.func_75679_c(17);
    }

    public void func_70494_i(int i) {
        this.field_70180_af.func_75692_b(18, i);
    }

    public int func_70493_k() {
        return this.field_70180_af.func_75679_c(18);
    }

    public abstract int func_94087_l();

    public Block func_145820_n() {
        if (!this.func_94100_s()) {
            return this.func_145817_o();
        } else {
            int i = this.func_70096_w().func_75679_c(20) & '\uffff';

            return Block.func_149729_e(i);
        }
    }

    public Block func_145817_o() {
        return Blocks.field_150350_a;
    }

    public int func_94098_o() {
        return !this.func_94100_s() ? this.func_94097_p() : this.func_70096_w().func_75679_c(20) >> 16;
    }

    public int func_94097_p() {
        return 0;
    }

    public int func_94099_q() {
        return !this.func_94100_s() ? this.func_94085_r() : this.func_70096_w().func_75679_c(21);
    }

    public int func_94085_r() {
        return 6;
    }

    public void func_145819_k(int i) {
        this.func_70096_w().func_75692_b(20, i & '\uffff' | this.func_94098_o() << 16);
        this.func_94096_e(true);
    }

    public void func_94092_k(int i) {
        this.func_70096_w().func_75692_b(20, Block.func_149682_b(this.func_145820_n()) & '\uffff' | i << 16);
        this.func_94096_e(true);
    }

    public void func_94086_l(int i) {
        this.func_70096_w().func_75692_b(21, i);
        this.func_94096_e(true);
    }

    public boolean func_94100_s() {
        return this.func_70096_w().func_75683_a(22) == 1;
    }

    public void func_94096_e(boolean flag) {
        this.func_70096_w().func_75692_b(22, (byte) (flag ? 1 : 0));
    }

    public void func_96094_a(String s) {
        this.field_94102_c = s;
    }

    @Override
    public String func_70005_c_() {
        return this.field_94102_c != null ? this.field_94102_c : super.func_70005_c_();
    }

    public boolean func_145818_k_() {
        return this.field_94102_c != null;
    }

    public String func_95999_t() {
        return this.field_94102_c;
    }
}
