package net.minecraft.world.biome;

import net.minecraft.util.WeightedRandom$Item;

public class BiomeGenBase$SpawnListEntry extends WeightedRandom$Item {

    public Class field_76300_b;
    public int field_76301_c;
    public int field_76299_d;

    public BiomeGenBase$SpawnListEntry(Class oclass, int i, int j, int k) {
        super(i);
        this.field_76300_b = oclass;
        this.field_76301_c = j;
        this.field_76299_d = k;
    }

    public String toString() {
        return this.field_76300_b.getSimpleName() + "*(" + this.field_76301_c + "-" + this.field_76299_d + "):" + this.field_76292_a;
    }
}
