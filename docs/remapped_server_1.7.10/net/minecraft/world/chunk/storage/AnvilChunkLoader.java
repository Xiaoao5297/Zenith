package net.minecraft.world.chunk.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraft.world.storage.ThreadedFileIOBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilChunkLoader implements IChunkLoader, IThreadedFileIO {

    private static final Logger field_151505_a = LogManager.getLogger();
    private List field_75828_a = new ArrayList();
    private Set field_75826_b = new HashSet();
    private Object field_75827_c = new Object();
    private final File field_75825_d;

    public AnvilChunkLoader(File file) {
        this.field_75825_d = file;
    }

    @Override
    public Chunk func_75815_a(World world, int i, int j) {
        NBTTagCompound nbttagcompound = null;
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i, j);
        Object object = this.field_75827_c;

        synchronized (this.field_75827_c) {
            if (this.field_75826_b.contains(chunkcoordintpair)) {
                for (int k = 0; k < this.field_75828_a.size(); ++k) {
                    if (((AnvilChunkLoader$PendingChunk) this.field_75828_a.get(k)).field_76548_a.equals(chunkcoordintpair)) {
                        nbttagcompound = ((AnvilChunkLoader$PendingChunk) this.field_75828_a.get(k)).field_76547_b;
                        break;
                    }
                }
            }
        }

        if (nbttagcompound == null) {
            DataInputStream datainputstream = RegionFileCache.func_76549_c(this.field_75825_d, i, j);

            if (datainputstream == null) {
                return null;
            }

            nbttagcompound = CompressedStreamTools.func_74794_a(datainputstream);
        }

        return this.func_75822_a(world, i, j, nbttagcompound);
    }

    protected Chunk func_75822_a(World world, int i, int j, NBTTagCompound nbttagcompound) {
        if (!nbttagcompound.func_150297_b("Level", 10)) {
            AnvilChunkLoader.field_151505_a.error("Chunk file at " + i + "," + j + " is missing level data, skipping");
            return null;
        } else if (!nbttagcompound.func_74775_l("Level").func_150297_b("Sections", 9)) {
            AnvilChunkLoader.field_151505_a.error("Chunk file at " + i + "," + j + " is missing block data, skipping");
            return null;
        } else {
            Chunk chunk = this.func_75823_a(world, nbttagcompound.func_74775_l("Level"));

            if (!chunk.func_76600_a(i, j)) {
                AnvilChunkLoader.field_151505_a.error("Chunk file at " + i + "," + j + " is in the wrong location; relocating. (Expected " + i + ", " + j + ", got " + chunk.field_76635_g + ", " + chunk.field_76647_h + ")");
                nbttagcompound.func_74768_a("xPos", i);
                nbttagcompound.func_74768_a("zPos", j);
                chunk = this.func_75823_a(world, nbttagcompound.func_74775_l("Level"));
            }

            return chunk;
        }
    }

    @Override
    public void func_75816_a(World world, Chunk chunk) {
        world.func_72906_B();

        try {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            nbttagcompound.func_74782_a("Level", nbttagcompound1);
            this.func_75820_a(chunk, world, nbttagcompound1);
            this.func_75824_a(chunk.func_76632_l(), nbttagcompound);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    protected void func_75824_a(ChunkCoordIntPair chunkcoordintpair, NBTTagCompound nbttagcompound) {
        Object object = this.field_75827_c;

        synchronized (this.field_75827_c) {
            if (this.field_75826_b.contains(chunkcoordintpair)) {
                for (int i = 0; i < this.field_75828_a.size(); ++i) {
                    if (((AnvilChunkLoader$PendingChunk) this.field_75828_a.get(i)).field_76548_a.equals(chunkcoordintpair)) {
                        this.field_75828_a.set(i, new AnvilChunkLoader$PendingChunk(chunkcoordintpair, nbttagcompound));
                        return;
                    }
                }
            }

            this.field_75828_a.add(new AnvilChunkLoader$PendingChunk(chunkcoordintpair, nbttagcompound));
            this.field_75826_b.add(chunkcoordintpair);
            ThreadedFileIOBase.field_75741_a.func_75735_a(this);
        }
    }

    @Override
    public boolean func_75814_c() {
        AnvilChunkLoader$PendingChunk anvilchunkloader$pendingchunk = null;
        Object object = this.field_75827_c;

        synchronized (this.field_75827_c) {
            if (this.field_75828_a.isEmpty()) {
                return false;
            }

            anvilchunkloader$pendingchunk = (AnvilChunkLoader$PendingChunk) this.field_75828_a.remove(0);
            this.field_75826_b.remove(anvilchunkloader$pendingchunk.field_76548_a);
        }

        if (anvilchunkloader$pendingchunk != null) {
            try {
                this.func_75821_a(anvilchunkloader$pendingchunk);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        return true;
    }

    private void func_75821_a(AnvilChunkLoader$PendingChunk anvilchunkloader$pendingchunk) {
        DataOutputStream dataoutputstream = RegionFileCache.func_76552_d(this.field_75825_d, anvilchunkloader$pendingchunk.field_76548_a.field_77276_a, anvilchunkloader$pendingchunk.field_76548_a.field_77275_b);

        CompressedStreamTools.func_74800_a(anvilchunkloader$pendingchunk.field_76547_b, dataoutputstream);
        dataoutputstream.close();
    }

    @Override
    public void func_75819_b(World world, Chunk chunk) {}

    @Override
    public void func_75817_a() {}

    @Override
    public void func_75818_b() {
        while (this.func_75814_c()) {
            ;
        }

    }

    private void func_75820_a(Chunk chunk, World world, NBTTagCompound nbttagcompound) {
        nbttagcompound.func_74774_a("V", (byte) 1);
        nbttagcompound.func_74768_a("xPos", chunk.field_76635_g);
        nbttagcompound.func_74768_a("zPos", chunk.field_76647_h);
        nbttagcompound.func_74772_a("LastUpdate", world.func_82737_E());
        nbttagcompound.func_74783_a("HeightMap", chunk.field_76634_f);
        nbttagcompound.func_74757_a("TerrainPopulated", chunk.field_76646_k);
        nbttagcompound.func_74757_a("LightPopulated", chunk.field_150814_l);
        nbttagcompound.func_74772_a("InhabitedTime", chunk.field_111204_q);
        ExtendedBlockStorage[] aextendedblockstorage = chunk.func_76587_i();
        NBTTagList nbttaglist = new NBTTagList();
        boolean flag = !world.field_73011_w.field_76576_e;
        ExtendedBlockStorage[] aextendedblockstorage1 = aextendedblockstorage;
        int i = aextendedblockstorage.length;

        NBTTagCompound nbttagcompound1;

        for (int j = 0; j < i; ++j) {
            ExtendedBlockStorage extendedblockstorage = aextendedblockstorage1[j];

            if (extendedblockstorage != null) {
                nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.func_74774_a("Y", (byte) (extendedblockstorage.func_76662_d() >> 4 & 255));
                nbttagcompound1.func_74773_a("Blocks", extendedblockstorage.func_76658_g());
                if (extendedblockstorage.func_76660_i() != null) {
                    nbttagcompound1.func_74773_a("Add", extendedblockstorage.func_76660_i().field_76585_a);
                }

                nbttagcompound1.func_74773_a("Data", extendedblockstorage.func_76669_j().field_76585_a);
                nbttagcompound1.func_74773_a("BlockLight", extendedblockstorage.func_76661_k().field_76585_a);
                if (flag) {
                    nbttagcompound1.func_74773_a("SkyLight", extendedblockstorage.func_76671_l().field_76585_a);
                } else {
                    nbttagcompound1.func_74773_a("SkyLight", new byte[extendedblockstorage.func_76661_k().field_76585_a.length]);
                }

                nbttaglist.func_74742_a(nbttagcompound1);
            }
        }

        nbttagcompound.func_74782_a("Sections", nbttaglist);
        nbttagcompound.func_74773_a("Biomes", chunk.func_76605_m());
        chunk.field_76644_m = false;
        NBTTagList nbttaglist1 = new NBTTagList();

        Iterator iterator;

        for (i = 0; i < chunk.field_76645_j.length; ++i) {
            iterator = chunk.field_76645_j[i].iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                nbttagcompound1 = new NBTTagCompound();
                if (entity.func_70039_c(nbttagcompound1)) {
                    chunk.field_76644_m = true;
                    nbttaglist1.func_74742_a(nbttagcompound1);
                }
            }
        }

        nbttagcompound.func_74782_a("Entities", nbttaglist1);
        NBTTagList nbttaglist2 = new NBTTagList();

        iterator = chunk.field_150816_i.values().iterator();

        while (iterator.hasNext()) {
            TileEntity tileentity = (TileEntity) iterator.next();

            nbttagcompound1 = new NBTTagCompound();
            tileentity.func_145841_b(nbttagcompound1);
            nbttaglist2.func_74742_a(nbttagcompound1);
        }

        nbttagcompound.func_74782_a("TileEntities", nbttaglist2);
        List list = world.func_72920_a(chunk, false);

        if (list != null) {
            long k = world.func_82737_E();
            NBTTagList nbttaglist3 = new NBTTagList();
            Iterator iterator1 = list.iterator();

            while (iterator1.hasNext()) {
                NextTickListEntry nextticklistentry = (NextTickListEntry) iterator1.next();
                NBTTagCompound nbttagcompound2 = new NBTTagCompound();

                nbttagcompound2.func_74768_a("i", Block.func_149682_b(nextticklistentry.func_151351_a()));
                nbttagcompound2.func_74768_a("x", nextticklistentry.field_77183_a);
                nbttagcompound2.func_74768_a("y", nextticklistentry.field_77181_b);
                nbttagcompound2.func_74768_a("z", nextticklistentry.field_77182_c);
                nbttagcompound2.func_74768_a("t", (int) (nextticklistentry.field_77180_e - k));
                nbttagcompound2.func_74768_a("p", nextticklistentry.field_82754_f);
                nbttaglist3.func_74742_a(nbttagcompound2);
            }

            nbttagcompound.func_74782_a("TileTicks", nbttaglist3);
        }

    }

    private Chunk func_75823_a(World world, NBTTagCompound nbttagcompound) {
        int i = nbttagcompound.func_74762_e("xPos");
        int j = nbttagcompound.func_74762_e("zPos");
        Chunk chunk = new Chunk(world, i, j);

        chunk.field_76634_f = nbttagcompound.func_74759_k("HeightMap");
        chunk.field_76646_k = nbttagcompound.func_74767_n("TerrainPopulated");
        chunk.field_150814_l = nbttagcompound.func_74767_n("LightPopulated");
        chunk.field_111204_q = nbttagcompound.func_74763_f("InhabitedTime");
        NBTTagList nbttaglist = nbttagcompound.func_150295_c("Sections", 10);
        byte b0 = 16;
        ExtendedBlockStorage[] aextendedblockstorage = new ExtendedBlockStorage[b0];
        boolean flag = !world.field_73011_w.field_76576_e;

        int k;

        for (int l = 0; l < nbttaglist.func_74745_c(); ++l) {
            NBTTagCompound nbttagcompound1 = nbttaglist.func_150305_b(l);

            k = nbttagcompound1.func_74771_c("Y");
            ExtendedBlockStorage extendedblockstorage = new ExtendedBlockStorage(k << 4, flag);

            extendedblockstorage.func_76664_a(nbttagcompound1.func_74770_j("Blocks"));
            if (nbttagcompound1.func_150297_b("Add", 7)) {
                extendedblockstorage.func_76673_a(new NibbleArray(nbttagcompound1.func_74770_j("Add"), 4));
            }

            extendedblockstorage.func_76668_b(new NibbleArray(nbttagcompound1.func_74770_j("Data"), 4));
            extendedblockstorage.func_76659_c(new NibbleArray(nbttagcompound1.func_74770_j("BlockLight"), 4));
            if (flag) {
                extendedblockstorage.func_76666_d(new NibbleArray(nbttagcompound1.func_74770_j("SkyLight"), 4));
            }

            extendedblockstorage.func_76672_e();
            aextendedblockstorage[k] = extendedblockstorage;
        }

        chunk.func_76602_a(aextendedblockstorage);
        if (nbttagcompound.func_150297_b("Biomes", 7)) {
            chunk.func_76616_a(nbttagcompound.func_74770_j("Biomes"));
        }

        NBTTagList nbttaglist1 = nbttagcompound.func_150295_c("Entities", 10);

        if (nbttaglist1 != null) {
            for (int i1 = 0; i1 < nbttaglist1.func_74745_c(); ++i1) {
                NBTTagCompound nbttagcompound2 = nbttaglist1.func_150305_b(i1);
                Entity entity = EntityList.func_75615_a(nbttagcompound2, world);

                chunk.field_76644_m = true;
                if (entity != null) {
                    chunk.func_76612_a(entity);
                    Entity entity1 = entity;

                    for (NBTTagCompound nbttagcompound3 = nbttagcompound2; nbttagcompound3.func_150297_b("Riding", 10); nbttagcompound3 = nbttagcompound3.func_74775_l("Riding")) {
                        Entity entity2 = EntityList.func_75615_a(nbttagcompound3.func_74775_l("Riding"), world);

                        if (entity2 != null) {
                            chunk.func_76612_a(entity2);
                            entity1.func_70078_a(entity2);
                        }

                        entity1 = entity2;
                    }
                }
            }
        }

        NBTTagList nbttaglist2 = nbttagcompound.func_150295_c("TileEntities", 10);

        if (nbttaglist2 != null) {
            for (k = 0; k < nbttaglist2.func_74745_c(); ++k) {
                NBTTagCompound nbttagcompound4 = nbttaglist2.func_150305_b(k);
                TileEntity tileentity = TileEntity.func_145827_c(nbttagcompound4);

                if (tileentity != null) {
                    chunk.func_150813_a(tileentity);
                }
            }
        }

        if (nbttagcompound.func_150297_b("TileTicks", 9)) {
            NBTTagList nbttaglist3 = nbttagcompound.func_150295_c("TileTicks", 10);

            if (nbttaglist3 != null) {
                for (int j1 = 0; j1 < nbttaglist3.func_74745_c(); ++j1) {
                    NBTTagCompound nbttagcompound5 = nbttaglist3.func_150305_b(j1);

                    world.func_147446_b(nbttagcompound5.func_74762_e("x"), nbttagcompound5.func_74762_e("y"), nbttagcompound5.func_74762_e("z"), Block.func_149729_e(nbttagcompound5.func_74762_e("i")), nbttagcompound5.func_74762_e("t"), nbttagcompound5.func_74762_e("p"));
                }
            }
        }

        return chunk;
    }
}
