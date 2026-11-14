package net.minecraft.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class PathNavigate {

    private EntityLiving field_75515_a;
    private World field_75513_b;
    private PathEntity field_75514_c;
    private double field_75511_d;
    private IAttributeInstance field_75512_e;
    private boolean field_75509_f;
    private int field_75510_g;
    private int field_75520_h;
    private Vec3 field_75521_i = Vec3.func_72443_a(0.0D, 0.0D, 0.0D);
    private boolean field_75518_j = true;
    private boolean field_75519_k;
    private boolean field_75516_l;
    private boolean field_75517_m;

    public PathNavigate(EntityLiving entityliving, World world) {
        this.field_75515_a = entityliving;
        this.field_75513_b = world;
        this.field_75512_e = entityliving.func_110148_a(SharedMonsterAttributes.field_111265_b);
    }

    public void func_75491_a(boolean flag) {
        this.field_75516_l = flag;
    }

    public boolean func_75486_a() {
        return this.field_75516_l;
    }

    public void func_75498_b(boolean flag) {
        this.field_75519_k = flag;
    }

    public void func_75490_c(boolean flag) {
        this.field_75518_j = flag;
    }

    public boolean func_75507_c() {
        return this.field_75519_k;
    }

    public void func_75504_d(boolean flag) {
        this.field_75509_f = flag;
    }

    public void func_75489_a(double d0) {
        this.field_75511_d = d0;
    }

    public void func_75495_e(boolean flag) {
        this.field_75517_m = flag;
    }

    public float func_111269_d() {
        return (float) this.field_75512_e.func_111126_e();
    }

    public PathEntity func_75488_a(double d0, double d1, double d2) {
        return !this.func_75485_k() ? null : this.field_75513_b.func_72844_a(this.field_75515_a, MathHelper.func_76128_c(d0), (int) d1, MathHelper.func_76128_c(d2), this.func_111269_d(), this.field_75518_j, this.field_75519_k, this.field_75516_l, this.field_75517_m);
    }

    public boolean func_75492_a(double d0, double d1, double d2, double d3) {
        PathEntity pathentity = this.func_75488_a((double) MathHelper.func_76128_c(d0), (double) ((int) d1), (double) MathHelper.func_76128_c(d2));

        return this.func_75484_a(pathentity, d3);
    }

    public PathEntity func_75494_a(Entity entity) {
        return !this.func_75485_k() ? null : this.field_75513_b.func_72865_a(this.field_75515_a, entity, this.func_111269_d(), this.field_75518_j, this.field_75519_k, this.field_75516_l, this.field_75517_m);
    }

    public boolean func_75497_a(Entity entity, double d0) {
        PathEntity pathentity = this.func_75494_a(entity);

        return pathentity != null ? this.func_75484_a(pathentity, d0) : false;
    }

    public boolean func_75484_a(PathEntity pathentity, double d0) {
        if (pathentity == null) {
            this.field_75514_c = null;
            return false;
        } else {
            if (!pathentity.func_75876_a(this.field_75514_c)) {
                this.field_75514_c = pathentity;
            }

            if (this.field_75509_f) {
                this.func_75487_m();
            }

            if (this.field_75514_c.func_75874_d() == 0) {
                return false;
            } else {
                this.field_75511_d = d0;
                Vec3 vec3 = this.func_75502_i();

                this.field_75520_h = this.field_75510_g;
                this.field_75521_i.field_72450_a = vec3.field_72450_a;
                this.field_75521_i.field_72448_b = vec3.field_72448_b;
                this.field_75521_i.field_72449_c = vec3.field_72449_c;
                return true;
            }
        }
    }

    public PathEntity func_75505_d() {
        return this.field_75514_c;
    }

    public void func_75501_e() {
        ++this.field_75510_g;
        if (!this.func_75500_f()) {
            if (this.func_75485_k()) {
                this.func_75508_h();
            }

            if (!this.func_75500_f()) {
                Vec3 vec3 = this.field_75514_c.func_75878_a(this.field_75515_a);

                if (vec3 != null) {
                    this.field_75515_a.func_70605_aq().func_75642_a(vec3.field_72450_a, vec3.field_72448_b, vec3.field_72449_c, this.field_75511_d);
                }
            }
        }
    }

    private void func_75508_h() {
        Vec3 vec3 = this.func_75502_i();
        int i = this.field_75514_c.func_75874_d();

        for (int j = this.field_75514_c.func_75873_e(); j < this.field_75514_c.func_75874_d(); ++j) {
            if (this.field_75514_c.func_75877_a(j).field_75837_b != (int) vec3.field_72448_b) {
                i = j;
                break;
            }
        }

        float f = this.field_75515_a.field_70130_N * this.field_75515_a.field_70130_N;

        int k;

        for (k = this.field_75514_c.func_75873_e(); k < i; ++k) {
            if (vec3.func_72436_e(this.field_75514_c.func_75881_a(this.field_75515_a, k)) < (double) f) {
                this.field_75514_c.func_75872_c(k + 1);
            }
        }

        k = MathHelper.func_76123_f(this.field_75515_a.field_70130_N);
        int l = (int) this.field_75515_a.field_70131_O + 1;
        int i1 = k;

        for (int j1 = i - 1; j1 >= this.field_75514_c.func_75873_e(); --j1) {
            if (this.func_75493_a(vec3, this.field_75514_c.func_75881_a(this.field_75515_a, j1), k, l, i1)) {
                this.field_75514_c.func_75872_c(j1);
                break;
            }
        }

        if (this.field_75510_g - this.field_75520_h > 100) {
            if (vec3.func_72436_e(this.field_75521_i) < 2.25D) {
                this.func_75499_g();
            }

            this.field_75520_h = this.field_75510_g;
            this.field_75521_i.field_72450_a = vec3.field_72450_a;
            this.field_75521_i.field_72448_b = vec3.field_72448_b;
            this.field_75521_i.field_72449_c = vec3.field_72449_c;
        }

    }

    public boolean func_75500_f() {
        return this.field_75514_c == null || this.field_75514_c.func_75879_b();
    }

    public void func_75499_g() {
        this.field_75514_c = null;
    }

    private Vec3 func_75502_i() {
        return Vec3.func_72443_a(this.field_75515_a.field_70165_t, (double) this.func_75503_j(), this.field_75515_a.field_70161_v);
    }

    private int func_75503_j() {
        if (this.field_75515_a.func_70090_H() && this.field_75517_m) {
            int i = (int) this.field_75515_a.field_70121_D.field_72338_b;
            Block block = this.field_75513_b.func_147439_a(MathHelper.func_76128_c(this.field_75515_a.field_70165_t), i, MathHelper.func_76128_c(this.field_75515_a.field_70161_v));
            int j = 0;

            do {
                if (block != Blocks.field_150358_i && block != Blocks.field_150355_j) {
                    return i;
                }

                ++i;
                block = this.field_75513_b.func_147439_a(MathHelper.func_76128_c(this.field_75515_a.field_70165_t), i, MathHelper.func_76128_c(this.field_75515_a.field_70161_v));
                ++j;
            } while (j <= 16);

            return (int) this.field_75515_a.field_70121_D.field_72338_b;
        } else {
            return (int) (this.field_75515_a.field_70121_D.field_72338_b + 0.5D);
        }
    }

    private boolean func_75485_k() {
        return this.field_75515_a.field_70122_E || this.field_75517_m && this.func_75506_l() || this.field_75515_a.func_70115_ae() && this.field_75515_a instanceof EntityZombie && this.field_75515_a.field_70154_o instanceof EntityChicken;
    }

    private boolean func_75506_l() {
        return this.field_75515_a.func_70090_H() || this.field_75515_a.func_70058_J();
    }

    private void func_75487_m() {
        if (!this.field_75513_b.func_72937_j(MathHelper.func_76128_c(this.field_75515_a.field_70165_t), (int) (this.field_75515_a.field_70121_D.field_72338_b + 0.5D), MathHelper.func_76128_c(this.field_75515_a.field_70161_v))) {
            for (int i = 0; i < this.field_75514_c.func_75874_d(); ++i) {
                PathPoint pathpoint = this.field_75514_c.func_75877_a(i);

                if (this.field_75513_b.func_72937_j(pathpoint.field_75839_a, pathpoint.field_75837_b, pathpoint.field_75838_c)) {
                    this.field_75514_c.func_75871_b(i - 1);
                    return;
                }
            }

        }
    }

    private boolean func_75493_a(Vec3 vec3, Vec3 vec31, int i, int j, int k) {
        int l = MathHelper.func_76128_c(vec3.field_72450_a);
        int i1 = MathHelper.func_76128_c(vec3.field_72449_c);
        double d0 = vec31.field_72450_a - vec3.field_72450_a;
        double d1 = vec31.field_72449_c - vec3.field_72449_c;
        double d2 = d0 * d0 + d1 * d1;

        if (d2 < 1.0E-8D) {
            return false;
        } else {
            double d3 = 1.0D / Math.sqrt(d2);

            d0 *= d3;
            d1 *= d3;
            i += 2;
            k += 2;
            if (!this.func_75483_a(l, (int) vec3.field_72448_b, i1, i, j, k, vec3, d0, d1)) {
                return false;
            } else {
                i -= 2;
                k -= 2;
                double d4 = 1.0D / Math.abs(d0);
                double d5 = 1.0D / Math.abs(d1);
                double d6 = (double) (l * 1) - vec3.field_72450_a;
                double d7 = (double) (i1 * 1) - vec3.field_72449_c;

                if (d0 >= 0.0D) {
                    ++d6;
                }

                if (d1 >= 0.0D) {
                    ++d7;
                }

                d6 /= d0;
                d7 /= d1;
                int j1 = d0 < 0.0D ? -1 : 1;
                int k1 = d1 < 0.0D ? -1 : 1;
                int l1 = MathHelper.func_76128_c(vec31.field_72450_a);
                int i2 = MathHelper.func_76128_c(vec31.field_72449_c);
                int j2 = l1 - l;
                int k2 = i2 - i1;

                do {
                    if (j2 * j1 <= 0 && k2 * k1 <= 0) {
                        return true;
                    }

                    if (d6 < d7) {
                        d6 += d4;
                        l += j1;
                        j2 = l1 - l;
                    } else {
                        d7 += d5;
                        i1 += k1;
                        k2 = i2 - i1;
                    }
                } while (this.func_75483_a(l, (int) vec3.field_72448_b, i1, i, j, k, vec3, d0, d1));

                return false;
            }
        }
    }

    private boolean func_75483_a(int i, int j, int k, int l, int i1, int j1, Vec3 vec3, double d0, double d1) {
        int k1 = i - l / 2;
        int l1 = k - j1 / 2;

        if (!this.func_75496_b(k1, j, l1, l, i1, j1, vec3, d0, d1)) {
            return false;
        } else {
            for (int i2 = k1; i2 < k1 + l; ++i2) {
                for (int j2 = l1; j2 < l1 + j1; ++j2) {
                    double d2 = (double) i2 + 0.5D - vec3.field_72450_a;
                    double d3 = (double) j2 + 0.5D - vec3.field_72449_c;

                    if (d2 * d0 + d3 * d1 >= 0.0D) {
                        Block block = this.field_75513_b.func_147439_a(i2, j - 1, j2);
                        Material material = block.func_149688_o();

                        if (material == Material.field_151579_a) {
                            return false;
                        }

                        if (material == Material.field_151586_h && !this.field_75515_a.func_70090_H()) {
                            return false;
                        }

                        if (material == Material.field_151587_i) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }

    private boolean func_75496_b(int i, int j, int k, int l, int i1, int j1, Vec3 vec3, double d0, double d1) {
        for (int k1 = i; k1 < i + l; ++k1) {
            for (int l1 = j; l1 < j + i1; ++l1) {
                for (int i2 = k; i2 < k + j1; ++i2) {
                    double d2 = (double) k1 + 0.5D - vec3.field_72450_a;
                    double d3 = (double) i2 + 0.5D - vec3.field_72449_c;

                    if (d2 * d0 + d3 * d1 >= 0.0D) {
                        Block block = this.field_75513_b.func_147439_a(k1, l1, i2);

                        if (!block.func_149655_b(this.field_75513_b, k1, l1, i2)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }
}
