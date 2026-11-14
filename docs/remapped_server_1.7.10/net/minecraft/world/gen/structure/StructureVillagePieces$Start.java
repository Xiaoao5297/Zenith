package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

public class StructureVillagePieces$Start extends StructureVillagePieces$Well {

    public WorldChunkManager field_74929_a;
    public boolean field_74927_b;
    public int field_74928_c;
    public StructureVillagePieces$PieceWeight field_74926_d;
    public List field_74931_h;
    public List field_74932_i = new ArrayList();
    public List field_74930_j = new ArrayList();

    public StructureVillagePieces$Start() {}

    public StructureVillagePieces$Start(WorldChunkManager worldchunkmanager, int i, Random random, int j, int k, List list, int l) {
        super((StructureVillagePieces$Start) null, 0, random, j, k);
        this.field_74929_a = worldchunkmanager;
        this.field_74931_h = list;
        this.field_74928_c = l;
        BiomeGenBase biomegenbase = worldchunkmanager.func_76935_a(j, k);

        this.field_74927_b = biomegenbase == BiomeGenBase.field_76769_d || biomegenbase == BiomeGenBase.field_76786_s;
    }

    public WorldChunkManager func_74925_d() {
        return this.field_74929_a;
    }
}
