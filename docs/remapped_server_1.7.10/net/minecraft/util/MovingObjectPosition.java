package net.minecraft.util;

import net.minecraft.entity.Entity;

public class MovingObjectPosition {

    public MovingObjectPosition$MovingObjectType field_72313_a;
    public int field_72311_b;
    public int field_72312_c;
    public int field_72309_d;
    public int field_72310_e;
    public Vec3 field_72307_f;
    public Entity field_72308_g;

    public MovingObjectPosition(int i, int j, int k, int l, Vec3 vec3) {
        this(i, j, k, l, vec3, true);
    }

    public MovingObjectPosition(int i, int j, int k, int l, Vec3 vec3, boolean flag) {
        this.field_72313_a = flag ? MovingObjectPosition$MovingObjectType.BLOCK : MovingObjectPosition$MovingObjectType.MISS;
        this.field_72311_b = i;
        this.field_72312_c = j;
        this.field_72309_d = k;
        this.field_72310_e = l;
        this.field_72307_f = Vec3.func_72443_a(vec3.field_72450_a, vec3.field_72448_b, vec3.field_72449_c);
    }

    public MovingObjectPosition(Entity entity) {
        this(entity, Vec3.func_72443_a(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v));
    }

    public MovingObjectPosition(Entity entity, Vec3 vec3) {
        this.field_72313_a = MovingObjectPosition$MovingObjectType.ENTITY;
        this.field_72308_g = entity;
        this.field_72307_f = vec3;
    }

    public String toString() {
        return "HitResult{type=" + this.field_72313_a + ", x=" + this.field_72311_b + ", y=" + this.field_72312_c + ", z=" + this.field_72309_d + ", f=" + this.field_72310_e + ", pos=" + this.field_72307_f + ", entity=" + this.field_72308_g + '}';
    }
}
