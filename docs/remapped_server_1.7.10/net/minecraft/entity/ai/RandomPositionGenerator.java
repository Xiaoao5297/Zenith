package net.minecraft.entity.ai;

import java.util.Random;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RandomPositionGenerator {

    private static Vec3 field_75465_a = Vec3.func_72443_a(0.0D, 0.0D, 0.0D);

    public static Vec3 func_75463_a(EntityCreature entitycreature, int i, int j) {
        return func_75462_c(entitycreature, i, j, (Vec3) null);
    }

    public static Vec3 func_75464_a(EntityCreature entitycreature, int i, int j, Vec3 vec3) {
        RandomPositionGenerator.field_75465_a.field_72450_a = vec3.field_72450_a - entitycreature.field_70165_t;
        RandomPositionGenerator.field_75465_a.field_72448_b = vec3.field_72448_b - entitycreature.field_70163_u;
        RandomPositionGenerator.field_75465_a.field_72449_c = vec3.field_72449_c - entitycreature.field_70161_v;
        return func_75462_c(entitycreature, i, j, RandomPositionGenerator.field_75465_a);
    }

    public static Vec3 func_75461_b(EntityCreature entitycreature, int i, int j, Vec3 vec3) {
        RandomPositionGenerator.field_75465_a.field_72450_a = entitycreature.field_70165_t - vec3.field_72450_a;
        RandomPositionGenerator.field_75465_a.field_72448_b = entitycreature.field_70163_u - vec3.field_72448_b;
        RandomPositionGenerator.field_75465_a.field_72449_c = entitycreature.field_70161_v - vec3.field_72449_c;
        return func_75462_c(entitycreature, i, j, RandomPositionGenerator.field_75465_a);
    }

    private static Vec3 func_75462_c(EntityCreature entitycreature, int i, int j, Vec3 vec3) {
        Random random = entitycreature.func_70681_au();
        boolean flag = false;
        int k = 0;
        int l = 0;
        int i1 = 0;
        float f = -99999.0F;
        boolean flag1;

        if (entitycreature.func_110175_bO()) {
            double d0 = (double) (entitycreature.func_110172_bL().func_71569_e(MathHelper.func_76128_c(entitycreature.field_70165_t), MathHelper.func_76128_c(entitycreature.field_70163_u), MathHelper.func_76128_c(entitycreature.field_70161_v)) + 4.0F);
            double d1 = (double) (entitycreature.func_110174_bM() + (float) i);

            flag1 = d0 < d1 * d1;
        } else {
            flag1 = false;
        }

        for (int j1 = 0; j1 < 10; ++j1) {
            int k1 = random.nextInt(2 * i) - i;
            int l1 = random.nextInt(2 * j) - j;
            int i2 = random.nextInt(2 * i) - i;

            if (vec3 == null || (double) k1 * vec3.field_72450_a + (double) i2 * vec3.field_72449_c >= 0.0D) {
                k1 += MathHelper.func_76128_c(entitycreature.field_70165_t);
                l1 += MathHelper.func_76128_c(entitycreature.field_70163_u);
                i2 += MathHelper.func_76128_c(entitycreature.field_70161_v);
                if (!flag1 || entitycreature.func_110176_b(k1, l1, i2)) {
                    float f1 = entitycreature.func_70783_a(k1, l1, i2);

                    if (f1 > f) {
                        f = f1;
                        k = k1;
                        l = l1;
                        i1 = i2;
                        flag = true;
                    }
                }
            }
        }

        if (flag) {
            return Vec3.func_72443_a((double) k, (double) l, (double) i1);
        } else {
            return null;
        }
    }
}
