package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenStronghold extends MapGenStructure {

    private List field_151546_e;
    private boolean field_75056_f;
    private ChunkCoordIntPair[] field_75057_g;
    private double field_82671_h;
    private int field_82672_i;

    public MapGenStronghold() {
        this.field_75057_g = new ChunkCoordIntPair[3];
        this.field_82671_h = 32.0D;
        this.field_82672_i = 3;
        this.field_151546_e = new ArrayList();
        BiomeGenBase[] abiomegenbase = BiomeGenBase.func_150565_n();
        int i = abiomegenbase.length;

        for (int j = 0; j < i; ++j) {
            BiomeGenBase biomegenbase = abiomegenbase[j];

            if (biomegenbase != null && biomegenbase.field_76748_D > 0.0F) {
                this.field_151546_e.add(biomegenbase);
            }
        }

    }

    public MapGenStronghold(Map map) {
        this();
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            if (((String) entry.getKey()).equals("distance")) {
                this.field_82671_h = MathHelper.func_82713_a((String) entry.getValue(), this.field_82671_h, 1.0D);
            } else if (((String) entry.getKey()).equals("count")) {
                this.field_75057_g = new ChunkCoordIntPair[MathHelper.func_82714_a((String) entry.getValue(), this.field_75057_g.length, 1)];
            } else if (((String) entry.getKey()).equals("spread")) {
                this.field_82672_i = MathHelper.func_82714_a((String) entry.getValue(), this.field_82672_i, 1);
            }
        }

    }

    @Override
    public String func_143025_a() {
        return "Stronghold";
    }

    @Override
    protected boolean func_75047_a(int i, int j) {
        if (!this.field_75056_f) {
            Random random = new Random();

            random.setSeed(this.field_75039_c.func_72905_C());
            double d0 = random.nextDouble() * Math.PI * 2.0D;
            int k = 1;

            for (int l = 0; l < this.field_75057_g.length; ++l) {
                double d1 = (1.25D * (double) k + random.nextDouble()) * this.field_82671_h * (double) k;
                int i1 = (int) Math.round(Math.cos(d0) * d1);
                int j1 = (int) Math.round(Math.sin(d0) * d1);
                ChunkPosition chunkposition = this.field_75039_c.func_72959_q().func_150795_a((i1 << 4) + 8, (j1 << 4) + 8, 112, this.field_151546_e, random);

                if (chunkposition != null) {
                    i1 = chunkposition.field_151329_a >> 4;
                    j1 = chunkposition.field_151328_c >> 4;
                }

                this.field_75057_g[l] = new ChunkCoordIntPair(i1, j1);
                d0 += 6.283185307179586D * (double) k / (double) this.field_82672_i;
                if (l == this.field_82672_i) {
                    k += 2 + random.nextInt(5);
                    this.field_82672_i += 1 + random.nextInt(2);
                }
            }

            this.field_75056_f = true;
        }

        ChunkCoordIntPair[] achunkcoordintpair = this.field_75057_g;
        int k1 = achunkcoordintpair.length;

        for (int l1 = 0; l1 < k1; ++l1) {
            ChunkCoordIntPair chunkcoordintpair = achunkcoordintpair[l1];

            if (i == chunkcoordintpair.field_77276_a && j == chunkcoordintpair.field_77275_b) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected List func_75052_o_() {
        ArrayList arraylist = new ArrayList();
        ChunkCoordIntPair[] achunkcoordintpair = this.field_75057_g;
        int i = achunkcoordintpair.length;

        for (int j = 0; j < i; ++j) {
            ChunkCoordIntPair chunkcoordintpair = achunkcoordintpair[j];

            if (chunkcoordintpair != null) {
                arraylist.add(chunkcoordintpair.func_151349_a(64));
            }
        }

        return arraylist;
    }

    @Override
    protected StructureStart func_75049_b(int i, int j) {
        MapGenStronghold$Start mapgenstronghold$start;

        for (mapgenstronghold$start = new MapGenStronghold$Start(this.field_75039_c, this.field_75038_b, i, j); mapgenstronghold$start.func_75073_b().isEmpty() || ((StructureStrongholdPieces$Stairs2) mapgenstronghold$start.func_75073_b().get(0)).field_75025_b == null; mapgenstronghold$start = new MapGenStronghold$Start(this.field_75039_c, this.field_75038_b, i, j)) {
            ;
        }

        return mapgenstronghold$start;
    }
}
