package net.minecraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

public class PathEntity {

    private final PathPoint[] field_75884_a;
    private int field_75882_b;
    private int field_75883_c;

    public PathEntity(PathPoint[] apathpoint) {
        this.field_75884_a = apathpoint;
        this.field_75883_c = apathpoint.length;
    }

    public void func_75875_a() {
        ++this.field_75882_b;
    }

    public boolean func_75879_b() {
        return this.field_75882_b >= this.field_75883_c;
    }

    public PathPoint func_75870_c() {
        return this.field_75883_c > 0 ? this.field_75884_a[this.field_75883_c - 1] : null;
    }

    public PathPoint func_75877_a(int i) {
        return this.field_75884_a[i];
    }

    public int func_75874_d() {
        return this.field_75883_c;
    }

    public void func_75871_b(int i) {
        this.field_75883_c = i;
    }

    public int func_75873_e() {
        return this.field_75882_b;
    }

    public void func_75872_c(int i) {
        this.field_75882_b = i;
    }

    public Vec3 func_75881_a(Entity entity, int i) {
        double d0 = (double) this.field_75884_a[i].field_75839_a + (double) ((int) (entity.field_70130_N + 1.0F)) * 0.5D;
        double d1 = (double) this.field_75884_a[i].field_75837_b;
        double d2 = (double) this.field_75884_a[i].field_75838_c + (double) ((int) (entity.field_70130_N + 1.0F)) * 0.5D;

        return Vec3.func_72443_a(d0, d1, d2);
    }

    public Vec3 func_75878_a(Entity entity) {
        return this.func_75881_a(entity, this.field_75882_b);
    }

    public boolean func_75876_a(PathEntity pathentity) {
        if (pathentity == null) {
            return false;
        } else if (pathentity.field_75884_a.length != this.field_75884_a.length) {
            return false;
        } else {
            for (int i = 0; i < this.field_75884_a.length; ++i) {
                if (this.field_75884_a[i].field_75839_a != pathentity.field_75884_a[i].field_75839_a || this.field_75884_a[i].field_75837_b != pathentity.field_75884_a[i].field_75837_b || this.field_75884_a[i].field_75838_c != pathentity.field_75884_a[i].field_75838_c) {
                    return false;
                }
            }

            return true;
        }
    }

    public boolean func_75880_b(Vec3 vec3) {
        PathPoint pathpoint = this.func_75870_c();

        return pathpoint == null ? false : pathpoint.field_75839_a == (int) vec3.field_72450_a && pathpoint.field_75838_c == (int) vec3.field_72449_c;
    }
}
