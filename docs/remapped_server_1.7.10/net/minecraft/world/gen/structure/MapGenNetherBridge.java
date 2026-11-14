package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.world.biome.BiomeGenBase$SpawnListEntry;

public class MapGenNetherBridge extends MapGenStructure {

    private List field_75060_e = new ArrayList();

    public MapGenNetherBridge() {
        this.field_75060_e.add(new BiomeGenBase$SpawnListEntry(EntityBlaze.class, 10, 2, 3));
        this.field_75060_e.add(new BiomeGenBase$SpawnListEntry(EntityPigZombie.class, 5, 4, 4));
        this.field_75060_e.add(new BiomeGenBase$SpawnListEntry(EntitySkeleton.class, 10, 4, 4));
        this.field_75060_e.add(new BiomeGenBase$SpawnListEntry(EntityMagmaCube.class, 3, 4, 4));
    }

    @Override
    public String func_143025_a() {
        return "Fortress";
    }

    public List func_75059_a() {
        return this.field_75060_e;
    }

    @Override
    protected boolean func_75047_a(int i, int j) {
        int k = i >> 4;
        int l = j >> 4;

        this.field_75038_b.setSeed((long) (k ^ l << 4) ^ this.field_75039_c.func_72905_C());
        this.field_75038_b.nextInt();
        return this.field_75038_b.nextInt(3) != 0 ? false : (i != (k << 4) + 4 + this.field_75038_b.nextInt(8) ? false : j == (l << 4) + 4 + this.field_75038_b.nextInt(8));
    }

    @Override
    protected StructureStart func_75049_b(int i, int j) {
        return new MapGenNetherBridge$Start(this.field_75039_c, this.field_75038_b, i, j);
    }
}
