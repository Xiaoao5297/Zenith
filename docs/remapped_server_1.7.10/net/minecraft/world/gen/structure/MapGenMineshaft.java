package net.minecraft.world.gen.structure;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.MathHelper;

public class MapGenMineshaft extends MapGenStructure {

    private double field_82673_e = 0.004D;

    public MapGenMineshaft() {}

    @Override
    public String func_143025_a() {
        return "Mineshaft";
    }

    public MapGenMineshaft(Map map) {
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            if (((String) entry.getKey()).equals("chance")) {
                this.field_82673_e = MathHelper.func_82712_a((String) entry.getValue(), this.field_82673_e);
            }
        }

    }

    @Override
    protected boolean func_75047_a(int i, int j) {
        return this.field_75038_b.nextDouble() < this.field_82673_e && this.field_75038_b.nextInt(80) < Math.max(Math.abs(i), Math.abs(j));
    }

    @Override
    protected StructureStart func_75049_b(int i, int j) {
        return new StructureMineshaftStart(this.field_75039_c, this.field_75038_b, i, j);
    }
}
