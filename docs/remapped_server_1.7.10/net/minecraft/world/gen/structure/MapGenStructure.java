package net.minecraft.world.gen.structure;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenBase;

public abstract class MapGenStructure extends MapGenBase {

    private MapGenStructureData field_143029_e;
    protected Map field_75053_d = new HashMap();

    public MapGenStructure() {}

    public abstract String func_143025_a();

    @Override
    protected final void func_151538_a(World world, int i, int j, int k, int l, Block[] ablock) {
        this.func_143027_a(world);
        if (!this.field_75053_d.containsKey(ChunkCoordIntPair.func_77272_a(i, j))) {
            this.field_75038_b.nextInt();

            try {
                if (this.func_75047_a(i, j)) {
                    StructureStart structurestart = this.func_75049_b(i, j);

                    this.field_75053_d.put(ChunkCoordIntPair.func_77272_a(i, j), structurestart);
                    this.func_143026_a(i, j, structurestart);
                }

            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.func_85055_a(throwable, "Exception preparing structure feature");
                CrashReportCategory crashreportcategory = crashreport.func_85058_a("Feature being prepared");

                crashreportcategory.func_71500_a("Is feature chunk", new MapGenStructure$1(this, i, j));
                crashreportcategory.func_71507_a("Chunk location", String.format("%d,%d", i, j));
                crashreportcategory.func_71500_a("Chunk pos hash", new MapGenStructure$2(this, i, j));
                crashreportcategory.func_71500_a("Structure type", new MapGenStructure$3(this));
                throw new ReportedException(crashreport);
            }
        }
    }

    public boolean func_75051_a(World world, Random random, int i, int j) {
        this.func_143027_a(world);
        int k = (i << 4) + 8;
        int l = (j << 4) + 8;
        boolean flag = false;
        Iterator iterator = this.field_75053_d.values().iterator();

        while (iterator.hasNext()) {
            StructureStart structurestart = (StructureStart) iterator.next();

            if (structurestart.func_75069_d() && structurestart.func_75071_a().func_78885_a(k, l, k + 15, l + 15)) {
                structurestart.func_75068_a(world, random, new StructureBoundingBox(k, l, k + 15, l + 15));
                flag = true;
                this.func_143026_a(structurestart.func_143019_e(), structurestart.func_143018_f(), structurestart);
            }
        }

        return flag;
    }

    public boolean func_75048_a(int i, int j, int k) {
        this.func_143027_a(this.field_75039_c);
        return this.func_143028_c(i, j, k) != null;
    }

    protected StructureStart func_143028_c(int i, int j, int k) {
        Iterator iterator = this.field_75053_d.values().iterator();

        while (iterator.hasNext()) {
            StructureStart structurestart = (StructureStart) iterator.next();

            if (structurestart.func_75069_d() && structurestart.func_75071_a().func_78885_a(i, k, i, k)) {
                Iterator iterator1 = structurestart.func_75073_b().iterator();

                while (iterator1.hasNext()) {
                    StructureComponent structurecomponent = (StructureComponent) iterator1.next();

                    if (structurecomponent.func_74874_b().func_78890_b(i, j, k)) {
                        return structurestart;
                    }
                }
            }
        }

        return null;
    }

    public boolean func_142038_b(int i, int j, int k) {
        this.func_143027_a(this.field_75039_c);
        Iterator iterator = this.field_75053_d.values().iterator();

        StructureStart structurestart;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            structurestart = (StructureStart) iterator.next();
        } while (!structurestart.func_75069_d());

        return structurestart.func_75071_a().func_78885_a(i, k, i, k);
    }

    public ChunkPosition func_151545_a(World world, int i, int j, int k) {
        this.field_75039_c = world;
        this.func_143027_a(world);
        this.field_75038_b.setSeed(world.func_72905_C());
        long l = this.field_75038_b.nextLong();
        long i1 = this.field_75038_b.nextLong();
        long j1 = (long) (i >> 4) * l;
        long k1 = (long) (k >> 4) * i1;

        this.field_75038_b.setSeed(j1 ^ k1 ^ world.func_72905_C());
        this.func_151538_a(world, i >> 4, k >> 4, 0, 0, (Block[]) null);
        double d0 = Double.MAX_VALUE;
        ChunkPosition chunkposition = null;
        Iterator iterator = this.field_75053_d.values().iterator();

        ChunkPosition chunkposition1;
        int l1;
        int i2;
        int j2;
        double d1;

        while (iterator.hasNext()) {
            StructureStart structurestart = (StructureStart) iterator.next();

            if (structurestart.func_75069_d()) {
                StructureComponent structurecomponent = (StructureComponent) structurestart.func_75073_b().get(0);

                chunkposition1 = structurecomponent.func_151553_a();
                l1 = chunkposition1.field_151329_a - i;
                i2 = chunkposition1.field_151327_b - j;
                j2 = chunkposition1.field_151328_c - k;
                d1 = (double) (l1 * l1 + i2 * i2 + j2 * j2);
                if (d1 < d0) {
                    d0 = d1;
                    chunkposition = chunkposition1;
                }
            }
        }

        if (chunkposition != null) {
            return chunkposition;
        } else {
            List list = this.func_75052_o_();

            if (list != null) {
                ChunkPosition chunkposition2 = null;
                Iterator iterator1 = list.iterator();

                while (iterator1.hasNext()) {
                    chunkposition1 = (ChunkPosition) iterator1.next();
                    l1 = chunkposition1.field_151329_a - i;
                    i2 = chunkposition1.field_151327_b - j;
                    j2 = chunkposition1.field_151328_c - k;
                    d1 = (double) (l1 * l1 + i2 * i2 + j2 * j2);
                    if (d1 < d0) {
                        d0 = d1;
                        chunkposition2 = chunkposition1;
                    }
                }

                return chunkposition2;
            } else {
                return null;
            }
        }
    }

    protected List func_75052_o_() {
        return null;
    }

    private void func_143027_a(World world) {
        if (this.field_143029_e == null) {
            this.field_143029_e = (MapGenStructureData) world.func_72943_a(MapGenStructureData.class, this.func_143025_a());
            if (this.field_143029_e == null) {
                this.field_143029_e = new MapGenStructureData(this.func_143025_a());
                world.func_72823_a(this.func_143025_a(), this.field_143029_e);
            } else {
                NBTTagCompound nbttagcompound = this.field_143029_e.func_143041_a();
                Iterator iterator = nbttagcompound.func_150296_c().iterator();

                while (iterator.hasNext()) {
                    String s = (String) iterator.next();
                    NBTBase nbtbase = nbttagcompound.func_74781_a(s);

                    if (nbtbase.func_74732_a() == 10) {
                        NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbtbase;

                        if (nbttagcompound1.func_74764_b("ChunkX") && nbttagcompound1.func_74764_b("ChunkZ")) {
                            int i = nbttagcompound1.func_74762_e("ChunkX");
                            int j = nbttagcompound1.func_74762_e("ChunkZ");
                            StructureStart structurestart = MapGenStructureIO.func_143035_a(nbttagcompound1, world);

                            if (structurestart != null) {
                                this.field_75053_d.put(ChunkCoordIntPair.func_77272_a(i, j), structurestart);
                            }
                        }
                    }
                }
            }
        }

    }

    private void func_143026_a(int i, int j, StructureStart structurestart) {
        this.field_143029_e.func_143043_a(structurestart.func_143021_a(i, j), i, j);
        this.field_143029_e.func_76185_a();
    }

    protected abstract boolean func_75047_a(int i, int j);

    protected abstract StructureStart func_75049_b(int i, int j);
}
