package net.minecraft.world.gen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;

public class ChunkProviderFlat implements IChunkProvider {

    private World field_73163_a;
    private Random field_73161_b;
    private final Block[] field_82700_c = new Block[256];
    private final byte[] field_82698_d = new byte[256];
    private final FlatGeneratorInfo field_82699_e;
    private final List field_82696_f = new ArrayList();
    private final boolean field_82697_g;
    private final boolean field_82702_h;
    private WorldGenLakes field_82703_i;
    private WorldGenLakes field_82701_j;

    public ChunkProviderFlat(World world, long i, boolean flag, String s) {
        this.field_73163_a = world;
        this.field_73161_b = new Random(i);
        this.field_82699_e = FlatGeneratorInfo.func_82651_a(s);
        if (flag) {
            Map map = this.field_82699_e.func_82644_b();

            if (map.containsKey("village")) {
                Map map1 = (Map) map.get("village");

                if (!map1.containsKey("size")) {
                    map1.put("size", "1");
                }

                this.field_82696_f.add(new MapGenVillage(map1));
            }

            if (map.containsKey("biome_1")) {
                this.field_82696_f.add(new MapGenScatteredFeature((Map) map.get("biome_1")));
            }

            if (map.containsKey("mineshaft")) {
                this.field_82696_f.add(new MapGenMineshaft((Map) map.get("mineshaft")));
            }

            if (map.containsKey("stronghold")) {
                this.field_82696_f.add(new MapGenStronghold((Map) map.get("stronghold")));
            }
        }

        this.field_82697_g = this.field_82699_e.func_82644_b().containsKey("decoration");
        if (this.field_82699_e.func_82644_b().containsKey("lake")) {
            this.field_82703_i = new WorldGenLakes(Blocks.field_150355_j);
        }

        if (this.field_82699_e.func_82644_b().containsKey("lava_lake")) {
            this.field_82701_j = new WorldGenLakes(Blocks.field_150353_l);
        }

        this.field_82702_h = this.field_82699_e.func_82644_b().containsKey("dungeon");
        Iterator iterator = this.field_82699_e.func_82650_c().iterator();

        while (iterator.hasNext()) {
            FlatLayerInfo flatlayerinfo = (FlatLayerInfo) iterator.next();

            for (int j = flatlayerinfo.func_82656_d(); j < flatlayerinfo.func_82656_d() + flatlayerinfo.func_82657_a(); ++j) {
                this.field_82700_c[j] = flatlayerinfo.func_151536_b();
                this.field_82698_d[j] = (byte) flatlayerinfo.func_82658_c();
            }
        }

    }

    @Override
    public Chunk func_73158_c(int i, int j) {
        return this.func_73154_d(i, j);
    }

    @Override
    public Chunk func_73154_d(int i, int j) {
        Chunk chunk = new Chunk(this.field_73163_a, i, j);

        int k;

        for (int l = 0; l < this.field_82700_c.length; ++l) {
            Block block = this.field_82700_c[l];

            if (block != null) {
                k = l >> 4;
                ExtendedBlockStorage extendedblockstorage = chunk.func_76587_i()[k];

                if (extendedblockstorage == null) {
                    extendedblockstorage = new ExtendedBlockStorage(l, !this.field_73163_a.field_73011_w.field_76576_e);
                    chunk.func_76587_i()[k] = extendedblockstorage;
                }

                for (int i1 = 0; i1 < 16; ++i1) {
                    for (int j1 = 0; j1 < 16; ++j1) {
                        extendedblockstorage.func_150818_a(i1, l & 15, j1, block);
                        extendedblockstorage.func_76654_b(i1, l & 15, j1, this.field_82698_d[l]);
                    }
                }
            }
        }

        chunk.func_76603_b();
        BiomeGenBase[] abiomegenbase = this.field_73163_a.func_72959_q().func_76933_b((BiomeGenBase[]) null, i * 16, j * 16, 16, 16);
        byte[] abyte = chunk.func_76605_m();

        for (k = 0; k < abyte.length; ++k) {
            abyte[k] = (byte) abiomegenbase[k].field_76756_M;
        }

        Iterator iterator = this.field_82696_f.iterator();

        while (iterator.hasNext()) {
            MapGenBase mapgenbase = (MapGenBase) iterator.next();

            mapgenbase.func_151539_a(this, this.field_73163_a, i, j, (Block[]) null);
        }

        chunk.func_76603_b();
        return chunk;
    }

    @Override
    public boolean func_73149_a(int i, int j) {
        return true;
    }

    @Override
    public void func_73153_a(IChunkProvider ichunkprovider, int i, int j) {
        int k = i * 16;
        int l = j * 16;
        BiomeGenBase biomegenbase = this.field_73163_a.func_72807_a(k + 16, l + 16);
        boolean flag = false;

        this.field_73161_b.setSeed(this.field_73163_a.func_72905_C());
        long i1 = this.field_73161_b.nextLong() / 2L * 2L + 1L;
        long j1 = this.field_73161_b.nextLong() / 2L * 2L + 1L;

        this.field_73161_b.setSeed((long) i * i1 + (long) j * j1 ^ this.field_73163_a.func_72905_C());
        Iterator iterator = this.field_82696_f.iterator();

        while (iterator.hasNext()) {
            MapGenStructure mapgenstructure = (MapGenStructure) iterator.next();
            boolean flag1 = mapgenstructure.func_75051_a(this.field_73163_a, this.field_73161_b, i, j);

            if (mapgenstructure instanceof MapGenVillage) {
                flag |= flag1;
            }
        }

        int k1;
        int l1;
        int i2;

        if (this.field_82703_i != null && !flag && this.field_73161_b.nextInt(4) == 0) {
            k1 = k + this.field_73161_b.nextInt(16) + 8;
            l1 = this.field_73161_b.nextInt(256);
            i2 = l + this.field_73161_b.nextInt(16) + 8;
            this.field_82703_i.func_76484_a(this.field_73163_a, this.field_73161_b, k1, l1, i2);
        }

        if (this.field_82701_j != null && !flag && this.field_73161_b.nextInt(8) == 0) {
            k1 = k + this.field_73161_b.nextInt(16) + 8;
            l1 = this.field_73161_b.nextInt(this.field_73161_b.nextInt(248) + 8);
            i2 = l + this.field_73161_b.nextInt(16) + 8;
            if (l1 < 63 || this.field_73161_b.nextInt(10) == 0) {
                this.field_82701_j.func_76484_a(this.field_73163_a, this.field_73161_b, k1, l1, i2);
            }
        }

        if (this.field_82702_h) {
            for (k1 = 0; k1 < 8; ++k1) {
                l1 = k + this.field_73161_b.nextInt(16) + 8;
                i2 = this.field_73161_b.nextInt(256);
                int j2 = l + this.field_73161_b.nextInt(16) + 8;

                (new WorldGenDungeons()).func_76484_a(this.field_73163_a, this.field_73161_b, l1, i2, j2);
            }
        }

        if (this.field_82697_g) {
            biomegenbase.func_76728_a(this.field_73163_a, this.field_73161_b, k, l);
        }

    }

    @Override
    public boolean func_73151_a(boolean flag, IProgressUpdate iprogressupdate) {
        return true;
    }

    @Override
    public void func_104112_b() {}

    @Override
    public boolean func_73156_b() {
        return false;
    }

    @Override
    public boolean func_73157_c() {
        return true;
    }

    @Override
    public String func_73148_d() {
        return "FlatLevelSource";
    }

    @Override
    public List func_73155_a(EnumCreatureType enumcreaturetype, int i, int j, int k) {
        BiomeGenBase biomegenbase = this.field_73163_a.func_72807_a(i, k);

        return biomegenbase.func_76747_a(enumcreaturetype);
    }

    @Override
    public ChunkPosition func_147416_a(World world, String s, int i, int j, int k) {
        if ("Stronghold".equals(s)) {
            Iterator iterator = this.field_82696_f.iterator();

            while (iterator.hasNext()) {
                MapGenStructure mapgenstructure = (MapGenStructure) iterator.next();

                if (mapgenstructure instanceof MapGenStronghold) {
                    return mapgenstructure.func_151545_a(world, i, j, k);
                }
            }
        }

        return null;
    }

    @Override
    public int func_73152_e() {
        return 0;
    }

    @Override
    public void func_82695_e(int i, int j) {
        Iterator iterator = this.field_82696_f.iterator();

        while (iterator.hasNext()) {
            MapGenStructure mapgenstructure = (MapGenStructure) iterator.next();

            mapgenstructure.func_151539_a(this, this.field_73163_a, i, j, (Block[]) null);
        }

    }
}
