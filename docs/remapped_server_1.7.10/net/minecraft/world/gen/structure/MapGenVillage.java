package net.minecraft.world.gen.structure;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenVillage extends MapGenStructure {

    public static final List field_75055_e = Arrays.asList(BiomeGenBase.field_76772_c, BiomeGenBase.field_76769_d, BiomeGenBase.field_150588_X);
    private int field_75054_f;
    private int field_82665_g;
    private int field_82666_h;

    public MapGenVillage() {
        this.field_82665_g = 32;
        this.field_82666_h = 8;
    }

    public MapGenVillage(Map map) {
        this();
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            if (((String) entry.getKey()).equals("size")) {
                this.field_75054_f = MathHelper.func_82714_a((String) entry.getValue(), this.field_75054_f, 0);
            } else if (((String) entry.getKey()).equals("distance")) {
                this.field_82665_g = MathHelper.func_82714_a((String) entry.getValue(), this.field_82665_g, this.field_82666_h + 1);
            }
        }

    }

    @Override
    public String func_143025_a() {
        return "Village";
    }

    @Override
    protected boolean func_75047_a(int i, int j) {
        int k = i;
        int l = j;

        if (i < 0) {
            i -= this.field_82665_g - 1;
        }

        if (j < 0) {
            j -= this.field_82665_g - 1;
        }

        int i1 = i / this.field_82665_g;
        int j1 = j / this.field_82665_g;
        Random random = this.field_75039_c.func_72843_D(i1, j1, 10387312);

        i1 *= this.field_82665_g;
        j1 *= this.field_82665_g;
        i1 += random.nextInt(this.field_82665_g - this.field_82666_h);
        j1 += random.nextInt(this.field_82665_g - this.field_82666_h);
        if (k == i1 && l == j1) {
            boolean flag = this.field_75039_c.func_72959_q().func_76940_a(k * 16 + 8, l * 16 + 8, 0, MapGenVillage.field_75055_e);

            if (flag) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected StructureStart func_75049_b(int i, int j) {
        return new MapGenVillage$Start(this.field_75039_c, this.field_75038_b, i, j, this.field_75054_f);
    }
}
