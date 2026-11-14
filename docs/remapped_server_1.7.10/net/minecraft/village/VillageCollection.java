package net.minecraft.village;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BlockDoor;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class VillageCollection extends WorldSavedData {

    private World field_75556_a;
    private final List field_75554_b = new ArrayList();
    private final List field_75555_c = new ArrayList();
    private final List field_75552_d = new ArrayList();
    private int field_75553_e;

    public VillageCollection(String s) {
        super(s);
    }

    public VillageCollection(World world) {
        super("villages");
        this.field_75556_a = world;
        this.func_76185_a();
    }

    public void func_82566_a(World world) {
        this.field_75556_a = world;
        Iterator iterator = this.field_75552_d.iterator();

        while (iterator.hasNext()) {
            Village village = (Village) iterator.next();

            village.func_82691_a(world);
        }

    }

    public void func_75551_a(int i, int j, int k) {
        if (this.field_75554_b.size() <= 64) {
            if (!this.func_75548_d(i, j, k)) {
                this.field_75554_b.add(new ChunkCoordinates(i, j, k));
            }

        }
    }

    public void func_75544_a() {
        ++this.field_75553_e;
        Iterator iterator = this.field_75552_d.iterator();

        while (iterator.hasNext()) {
            Village village = (Village) iterator.next();

            village.func_75560_a(this.field_75553_e);
        }

        this.func_75549_c();
        this.func_75543_d();
        this.func_75545_e();
        if (this.field_75553_e % 400 == 0) {
            this.func_76185_a();
        }

    }

    private void func_75549_c() {
        Iterator iterator = this.field_75552_d.iterator();

        while (iterator.hasNext()) {
            Village village = (Village) iterator.next();

            if (village.func_75566_g()) {
                iterator.remove();
                this.func_76185_a();
            }
        }

    }

    public List func_75540_b() {
        return this.field_75552_d;
    }

    public Village func_75550_a(int i, int j, int k, int l) {
        Village village = null;
        float f = Float.MAX_VALUE;
        Iterator iterator = this.field_75552_d.iterator();

        while (iterator.hasNext()) {
            Village village1 = (Village) iterator.next();
            float f1 = village1.func_75577_a().func_71569_e(i, j, k);

            if (f1 < f) {
                float f2 = (float) (l + village1.func_75568_b());

                if (f1 <= f2 * f2) {
                    village = village1;
                    f = f1;
                }
            }
        }

        return village;
    }

    private void func_75543_d() {
        if (!this.field_75554_b.isEmpty()) {
            this.func_75546_a((ChunkCoordinates) this.field_75554_b.remove(0));
        }
    }

    private void func_75545_e() {
        int i = 0;

        while (i < this.field_75555_c.size()) {
            VillageDoorInfo villagedoorinfo = (VillageDoorInfo) this.field_75555_c.get(i);
            boolean flag = false;
            Iterator iterator = this.field_75552_d.iterator();

            while (true) {
                if (iterator.hasNext()) {
                    Village village = (Village) iterator.next();
                    int j = (int) village.func_75577_a().func_71569_e(villagedoorinfo.field_75481_a, villagedoorinfo.field_75479_b, villagedoorinfo.field_75480_c);
                    int k = 32 + village.func_75568_b();

                    if (j > k * k) {
                        continue;
                    }

                    village.func_75576_a(villagedoorinfo);
                    flag = true;
                }

                if (!flag) {
                    Village village1 = new Village(this.field_75556_a);

                    village1.func_75576_a(villagedoorinfo);
                    this.field_75552_d.add(village1);
                    this.func_76185_a();
                }

                ++i;
                break;
            }
        }

        this.field_75555_c.clear();
    }

    private void func_75546_a(ChunkCoordinates chunkcoordinates) {
        byte b0 = 16;
        byte b1 = 4;
        byte b2 = 16;

        for (int i = chunkcoordinates.field_71574_a - b0; i < chunkcoordinates.field_71574_a + b0; ++i) {
            for (int j = chunkcoordinates.field_71572_b - b1; j < chunkcoordinates.field_71572_b + b1; ++j) {
                for (int k = chunkcoordinates.field_71573_c - b2; k < chunkcoordinates.field_71573_c + b2; ++k) {
                    if (this.func_75541_e(i, j, k)) {
                        VillageDoorInfo villagedoorinfo = this.func_75547_b(i, j, k);

                        if (villagedoorinfo == null) {
                            this.func_75542_c(i, j, k);
                        } else {
                            villagedoorinfo.field_75475_f = this.field_75553_e;
                        }
                    }
                }
            }
        }

    }

    private VillageDoorInfo func_75547_b(int i, int j, int k) {
        Iterator iterator = this.field_75555_c.iterator();

        VillageDoorInfo villagedoorinfo;

        do {
            if (!iterator.hasNext()) {
                iterator = this.field_75552_d.iterator();

                VillageDoorInfo villagedoorinfo1;

                do {
                    if (!iterator.hasNext()) {
                        return null;
                    }

                    Village village = (Village) iterator.next();

                    villagedoorinfo1 = village.func_75578_e(i, j, k);
                } while (villagedoorinfo1 == null);

                return villagedoorinfo1;
            }

            villagedoorinfo = (VillageDoorInfo) iterator.next();
        } while (villagedoorinfo.field_75481_a != i || villagedoorinfo.field_75480_c != k || Math.abs(villagedoorinfo.field_75479_b - j) > 1);

        return villagedoorinfo;
    }

    private void func_75542_c(int i, int j, int k) {
        int l = ((BlockDoor) Blocks.field_150466_ao).func_150013_e(this.field_75556_a, i, j, k);
        int i1;
        int j1;

        if (l != 0 && l != 2) {
            i1 = 0;

            for (j1 = -5; j1 < 0; ++j1) {
                if (this.field_75556_a.func_72937_j(i, j, k + j1)) {
                    --i1;
                }
            }

            for (j1 = 1; j1 <= 5; ++j1) {
                if (this.field_75556_a.func_72937_j(i, j, k + j1)) {
                    ++i1;
                }
            }

            if (i1 != 0) {
                this.field_75555_c.add(new VillageDoorInfo(i, j, k, 0, i1 > 0 ? -2 : 2, this.field_75553_e));
            }
        } else {
            i1 = 0;

            for (j1 = -5; j1 < 0; ++j1) {
                if (this.field_75556_a.func_72937_j(i + j1, j, k)) {
                    --i1;
                }
            }

            for (j1 = 1; j1 <= 5; ++j1) {
                if (this.field_75556_a.func_72937_j(i + j1, j, k)) {
                    ++i1;
                }
            }

            if (i1 != 0) {
                this.field_75555_c.add(new VillageDoorInfo(i, j, k, i1 > 0 ? -2 : 2, 0, this.field_75553_e));
            }
        }

    }

    private boolean func_75548_d(int i, int j, int k) {
        Iterator iterator = this.field_75554_b.iterator();

        ChunkCoordinates chunkcoordinates;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            chunkcoordinates = (ChunkCoordinates) iterator.next();
        } while (chunkcoordinates.field_71574_a != i || chunkcoordinates.field_71572_b != j || chunkcoordinates.field_71573_c != k);

        return true;
    }

    private boolean func_75541_e(int i, int j, int k) {
        return this.field_75556_a.func_147439_a(i, j, k) == Blocks.field_150466_ao;
    }

    @Override
    public void func_76184_a(NBTTagCompound nbttagcompound) {
        this.field_75553_e = nbttagcompound.func_74762_e("Tick");
        NBTTagList nbttaglist = nbttagcompound.func_150295_c("Villages", 10);

        for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.func_150305_b(i);
            Village village = new Village();

            village.func_82690_a(nbttagcompound1);
            this.field_75552_d.add(village);
        }

    }

    @Override
    public void func_76187_b(NBTTagCompound nbttagcompound) {
        nbttagcompound.func_74768_a("Tick", this.field_75553_e);
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.field_75552_d.iterator();

        while (iterator.hasNext()) {
            Village village = (Village) iterator.next();
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            village.func_82689_b(nbttagcompound1);
            nbttaglist.func_74742_a(nbttagcompound1);
        }

        nbttagcompound.func_74782_a("Villages", nbttaglist);
    }
}
