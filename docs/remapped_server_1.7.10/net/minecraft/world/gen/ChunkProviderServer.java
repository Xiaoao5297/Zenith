package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderServer implements IChunkProvider {

    private static final Logger field_147417_b = LogManager.getLogger();
    private Set field_73248_b = Collections.newSetFromMap(new ConcurrentHashMap());
    private Chunk field_73249_c;
    private IChunkProvider field_73246_d;
    private IChunkLoader field_73247_e;
    public boolean field_73250_a = true;
    private LongHashMap field_73244_f = new LongHashMap();
    private List field_73245_g = new ArrayList();
    private WorldServer field_73251_h;

    public ChunkProviderServer(WorldServer worldserver, IChunkLoader ichunkloader, IChunkProvider ichunkprovider) {
        this.field_73249_c = new EmptyChunk(worldserver, 0, 0);
        this.field_73251_h = worldserver;
        this.field_73247_e = ichunkloader;
        this.field_73246_d = ichunkprovider;
    }

    @Override
    public boolean func_73149_a(int i, int j) {
        return this.field_73244_f.func_76161_b(ChunkCoordIntPair.func_77272_a(i, j));
    }

    public List func_152380_a() {
        return this.field_73245_g;
    }

    public void func_73241_b(int i, int j) {
        if (this.field_73251_h.field_73011_w.func_76567_e()) {
            ChunkCoordinates chunkcoordinates = this.field_73251_h.func_72861_E();
            int k = i * 16 + 8 - chunkcoordinates.field_71574_a;
            int l = j * 16 + 8 - chunkcoordinates.field_71573_c;
            short short0 = 128;

            if (k < -short0 || k > short0 || l < -short0 || l > short0) {
                this.field_73248_b.add(ChunkCoordIntPair.func_77272_a(i, j));
            }
        } else {
            this.field_73248_b.add(ChunkCoordIntPair.func_77272_a(i, j));
        }

    }

    public void func_73240_a() {
        Iterator iterator = this.field_73245_g.iterator();

        while (iterator.hasNext()) {
            Chunk chunk = (Chunk) iterator.next();

            this.func_73241_b(chunk.field_76635_g, chunk.field_76647_h);
        }

    }

    @Override
    public Chunk func_73158_c(int i, int j) {
        long k = ChunkCoordIntPair.func_77272_a(i, j);

        this.field_73248_b.remove(k);
        Chunk chunk = (Chunk) this.field_73244_f.func_76164_a(k);

        if (chunk == null) {
            chunk = this.func_73239_e(i, j);
            if (chunk == null) {
                if (this.field_73246_d == null) {
                    chunk = this.field_73249_c;
                } else {
                    try {
                        chunk = this.field_73246_d.func_73154_d(i, j);
                    } catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.func_85055_a(throwable, "Exception generating new chunk");
                        CrashReportCategory crashreportcategory = crashreport.func_85058_a("Chunk to be generated");

                        crashreportcategory.func_71507_a("Location", String.format("%d,%d", i, j));
                        crashreportcategory.func_71507_a("Position hash", k);
                        crashreportcategory.func_71507_a("Generator", this.field_73246_d.func_73148_d());
                        throw new ReportedException(crashreport);
                    }
                }
            }

            this.field_73244_f.func_76163_a(k, chunk);
            this.field_73245_g.add(chunk);
            chunk.func_76631_c();
            chunk.func_76624_a(this, this, i, j);
        }

        return chunk;
    }

    @Override
    public Chunk func_73154_d(int i, int j) {
        Chunk chunk = (Chunk) this.field_73244_f.func_76164_a(ChunkCoordIntPair.func_77272_a(i, j));

        return chunk == null ? (!this.field_73251_h.field_72987_B && !this.field_73250_a ? this.field_73249_c : this.func_73158_c(i, j)) : chunk;
    }

    private Chunk func_73239_e(int i, int j) {
        if (this.field_73247_e == null) {
            return null;
        } else {
            try {
                Chunk chunk = this.field_73247_e.func_75815_a(this.field_73251_h, i, j);

                if (chunk != null) {
                    chunk.field_76641_n = this.field_73251_h.func_82737_E();
                    if (this.field_73246_d != null) {
                        this.field_73246_d.func_82695_e(i, j);
                    }
                }

                return chunk;
            } catch (Exception exception) {
                ChunkProviderServer.field_147417_b.error("Couldn't load chunk", (Throwable) exception);
                return null;
            }
        }
    }

    private void func_73243_a(Chunk chunk) {
        if (this.field_73247_e != null) {
            try {
                this.field_73247_e.func_75819_b(this.field_73251_h, chunk);
            } catch (Exception exception) {
                ChunkProviderServer.field_147417_b.error("Couldn't save entities", (Throwable) exception);
            }

        }
    }

    private void func_73242_b(Chunk chunk) {
        if (this.field_73247_e != null) {
            try {
                chunk.field_76641_n = this.field_73251_h.func_82737_E();
                this.field_73247_e.func_75816_a(this.field_73251_h, chunk);
            } catch (IOException ioexception) {
                ChunkProviderServer.field_147417_b.error("Couldn't save chunk", (Throwable) ioexception);
            } catch (MinecraftException minecraftexception) {
                ChunkProviderServer.field_147417_b.error("Couldn't save chunk; already in use by another instance of Minecraft?", (Throwable) minecraftexception);
            }

        }
    }

    @Override
    public void func_73153_a(IChunkProvider ichunkprovider, int i, int j) {
        Chunk chunk = this.func_73154_d(i, j);

        if (!chunk.field_76646_k) {
            chunk.func_150809_p();
            if (this.field_73246_d != null) {
                this.field_73246_d.func_73153_a(ichunkprovider, i, j);
                chunk.func_76630_e();
            }
        }

    }

    @Override
    public boolean func_73151_a(boolean flag, IProgressUpdate iprogressupdate) {
        int i = 0;
        ArrayList arraylist = Lists.newArrayList((Iterable) this.field_73245_g);

        for (int j = 0; j < arraylist.size(); ++j) {
            Chunk chunk = (Chunk) arraylist.get(j);

            if (flag) {
                this.func_73243_a(chunk);
            }

            if (chunk.func_76601_a(flag)) {
                this.func_73242_b(chunk);
                chunk.field_76643_l = false;
                ++i;
                if (i == 24 && !flag) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void func_104112_b() {
        if (this.field_73247_e != null) {
            this.field_73247_e.func_75818_b();
        }

    }

    @Override
    public boolean func_73156_b() {
        if (!this.field_73251_h.field_73058_d) {
            for (int i = 0; i < 100; ++i) {
                if (!this.field_73248_b.isEmpty()) {
                    Long olong = (Long) this.field_73248_b.iterator().next();
                    Chunk chunk = (Chunk) this.field_73244_f.func_76164_a(olong);

                    if (chunk != null) {
                        chunk.func_76623_d();
                        this.func_73242_b(chunk);
                        this.func_73243_a(chunk);
                        this.field_73245_g.remove(chunk);
                    }

                    this.field_73248_b.remove(olong);
                    this.field_73244_f.func_76159_d(olong);
                }
            }

            if (this.field_73247_e != null) {
                this.field_73247_e.func_75817_a();
            }
        }

        return this.field_73246_d.func_73156_b();
    }

    @Override
    public boolean func_73157_c() {
        return !this.field_73251_h.field_73058_d;
    }

    @Override
    public String func_73148_d() {
        return "ServerChunkCache: " + this.field_73244_f.func_76162_a() + " Drop: " + this.field_73248_b.size();
    }

    @Override
    public List func_73155_a(EnumCreatureType enumcreaturetype, int i, int j, int k) {
        return this.field_73246_d.func_73155_a(enumcreaturetype, i, j, k);
    }

    @Override
    public ChunkPosition func_147416_a(World world, String s, int i, int j, int k) {
        return this.field_73246_d.func_147416_a(world, s, i, j, k);
    }

    @Override
    public int func_73152_e() {
        return this.field_73244_f.func_76162_a();
    }

    @Override
    public void func_82695_e(int i, int j) {}
}
