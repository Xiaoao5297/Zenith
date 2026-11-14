package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase$SpawnListEntry;

public class MapGenScatteredFeature extends MapGenStructure {

    private static List field_75061_e = Arrays.asList(BiomeGenBase.field_76769_d, BiomeGenBase.field_76786_s, BiomeGenBase.field_76782_w, BiomeGenBase.field_76792_x, BiomeGenBase.field_76780_h);
    private List field_82668_f;
    private int field_82669_g;
    private int field_82670_h;

    public MapGenScatteredFeature() {
        this.field_82668_f = new ArrayList();
        this.field_82669_g = 32;
        this.field_82670_h = 8;
        this.field_82668_f.add(new BiomeGenBase$SpawnListEntry(EntityWitch.class, 1, 1, 1));
    }

    public MapGenScatteredFeature(Map map) {
        this();
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            if (((String) entry.getKey()).equals("distance")) {
                this.field_82669_g = MathHelper.func_82714_a((String) entry.getValue(), this.field_82669_g, this.field_82670_h + 1);
            }
        }

    }

    @Override
    public String func_143025_a() {
        return "Temple";
    }

    @Override
    protected boolean func_75047_a(int i, int j) {
        int k = i;
        int l = j;

        if (i < 0) {
            i -= this.field_82669_g - 1;
        }

        if (j < 0) {
            j -= this.field_82669_g - 1;
        }

        int i1 = i / this.field_82669_g;
        int j1 = j / this.field_82669_g;
        Random random = this.field_75039_c.func_72843_D(i1, j1, 14357617);

        i1 *= this.field_82669_g;
        j1 *= this.field_82669_g;
        i1 += random.nextInt(this.field_82669_g - this.field_82670_h);
        j1 += random.nextInt(this.field_82669_g - this.field_82670_h);
        if (k == i1 && l == j1) {
            BiomeGenBase biomegenbase = this.field_75039_c.func_72959_q().func_76935_a(k * 16 + 8, l * 16 + 8);
            Iterator iterator = MapGenScatteredFeature.field_75061_e.iterator();

            while (iterator.hasNext()) {
                BiomeGenBase biomegenbase1 = (BiomeGenBase) iterator.next();

                if (biomegenbase == biomegenbase1) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected StructureStart func_75049_b(int i, int j) {
        return new MapGenScatteredFeature$Start(this.field_75039_c, this.field_75038_b, i, j);
    }

    public boolean func_143030_a(int i, int j, int k) {
        StructureStart structurestart = this.func_143028_c(i, j, k);

        if (structurestart != null && structurestart instanceof MapGenScatteredFeature$Start && !structurestart.field_75075_a.isEmpty()) {
            StructureComponent structurecomponent = (StructureComponent) structurestart.field_75075_a.getFirst();

            return structurecomponent instanceof ComponentScatteredFeaturePieces$SwampHut;
        } else {
            return false;
        }
    }

    public List func_82667_a() {
        return this.field_82668_f;
    }
}
