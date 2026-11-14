package net.minecraft.village;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Village {

    private World field_75586_a;
    private final List field_75584_b = new ArrayList();
    private final ChunkCoordinates field_75585_c = new ChunkCoordinates(0, 0, 0);
    private final ChunkCoordinates field_75582_d = new ChunkCoordinates(0, 0, 0);
    private int field_75583_e;
    private int field_75580_f;
    private int field_75581_g;
    private int field_75588_h;
    private int field_82694_i;
    private TreeMap field_82693_j = new TreeMap();
    private List field_75589_i = new ArrayList();
    private int field_75587_j;

    public Village() {}

    public Village(World world) {
        this.field_75586_a = world;
    }

    public void func_82691_a(World world) {
        this.field_75586_a = world;
    }

    public void func_75560_a(int i) {
        this.field_75581_g = i;
        this.func_75557_k();
        this.func_75565_j();
        if (i % 20 == 0) {
            this.func_75572_i();
        }

        if (i % 30 == 0) {
            this.func_75579_h();
        }

        int j = this.field_75588_h / 10;

        if (this.field_75587_j < j && this.field_75584_b.size() > 20 && this.field_75586_a.field_73012_v.nextInt(7000) == 0) {
            Vec3 vec3 = this.func_75559_a(MathHelper.func_76141_d((float) this.field_75582_d.field_71574_a), MathHelper.func_76141_d((float) this.field_75582_d.field_71572_b), MathHelper.func_76141_d((float) this.field_75582_d.field_71573_c), 2, 4, 2);

            if (vec3 != null) {
                EntityIronGolem entityirongolem = new EntityIronGolem(this.field_75586_a);

                entityirongolem.func_70107_b(vec3.field_72450_a, vec3.field_72448_b, vec3.field_72449_c);
                this.field_75586_a.func_72838_d(entityirongolem);
                ++this.field_75587_j;
            }
        }

    }

    private Vec3 func_75559_a(int i, int j, int k, int l, int i1, int j1) {
        for (int k1 = 0; k1 < 10; ++k1) {
            int l1 = i + this.field_75586_a.field_73012_v.nextInt(16) - 8;
            int i2 = j + this.field_75586_a.field_73012_v.nextInt(6) - 3;
            int j2 = k + this.field_75586_a.field_73012_v.nextInt(16) - 8;

            if (this.func_75570_a(l1, i2, j2) && this.func_75563_b(l1, i2, j2, l, i1, j1)) {
                return Vec3.func_72443_a((double) l1, (double) i2, (double) j2);
            }
        }

        return null;
    }

    private boolean func_75563_b(int i, int j, int k, int l, int i1, int j1) {
        if (!World.func_147466_a(this.field_75586_a, i, j - 1, k)) {
            return false;
        } else {
            int k1 = i - l / 2;
            int l1 = k - j1 / 2;

            for (int i2 = k1; i2 < k1 + l; ++i2) {
                for (int j2 = j; j2 < j + i1; ++j2) {
                    for (int k2 = l1; k2 < l1 + j1; ++k2) {
                        if (this.field_75586_a.func_147439_a(i2, j2, k2).func_149721_r()) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }

    private void func_75579_h() {
        List list = this.field_75586_a.func_72872_a(EntityIronGolem.class, AxisAlignedBB.func_72330_a((double) (this.field_75582_d.field_71574_a - this.field_75583_e), (double) (this.field_75582_d.field_71572_b - 4), (double) (this.field_75582_d.field_71573_c - this.field_75583_e), (double) (this.field_75582_d.field_71574_a + this.field_75583_e), (double) (this.field_75582_d.field_71572_b + 4), (double) (this.field_75582_d.field_71573_c + this.field_75583_e)));

        this.field_75587_j = list.size();
    }

    private void func_75572_i() {
        List list = this.field_75586_a.func_72872_a(EntityVillager.class, AxisAlignedBB.func_72330_a((double) (this.field_75582_d.field_71574_a - this.field_75583_e), (double) (this.field_75582_d.field_71572_b - 4), (double) (this.field_75582_d.field_71573_c - this.field_75583_e), (double) (this.field_75582_d.field_71574_a + this.field_75583_e), (double) (this.field_75582_d.field_71572_b + 4), (double) (this.field_75582_d.field_71573_c + this.field_75583_e)));

        this.field_75588_h = list.size();
        if (this.field_75588_h == 0) {
            this.field_82693_j.clear();
        }

    }

    public ChunkCoordinates func_75577_a() {
        return this.field_75582_d;
    }

    public int func_75568_b() {
        return this.field_75583_e;
    }

    public int func_75567_c() {
        return this.field_75584_b.size();
    }

    public int func_75561_d() {
        return this.field_75581_g - this.field_75580_f;
    }

    public int func_75562_e() {
        return this.field_75588_h;
    }

    public boolean func_75570_a(int i, int j, int k) {
        return this.field_75582_d.func_71569_e(i, j, k) < (float) (this.field_75583_e * this.field_75583_e);
    }

    public List func_75558_f() {
        return this.field_75584_b;
    }

    public VillageDoorInfo func_75564_b(int i, int j, int k) {
        VillageDoorInfo villagedoorinfo = null;
        int l = Integer.MAX_VALUE;
        Iterator iterator = this.field_75584_b.iterator();

        while (iterator.hasNext()) {
            VillageDoorInfo villagedoorinfo1 = (VillageDoorInfo) iterator.next();
            int i1 = villagedoorinfo1.func_75474_b(i, j, k);

            if (i1 < l) {
                villagedoorinfo = villagedoorinfo1;
                l = i1;
            }
        }

        return villagedoorinfo;
    }

    public VillageDoorInfo func_75569_c(int i, int j, int k) {
        VillageDoorInfo villagedoorinfo = null;
        int l = Integer.MAX_VALUE;
        Iterator iterator = this.field_75584_b.iterator();

        while (iterator.hasNext()) {
            VillageDoorInfo villagedoorinfo1 = (VillageDoorInfo) iterator.next();
            int i1 = villagedoorinfo1.func_75474_b(i, j, k);

            if (i1 > 256) {
                i1 *= 1000;
            } else {
                i1 = villagedoorinfo1.func_75468_f();
            }

            if (i1 < l) {
                villagedoorinfo = villagedoorinfo1;
                l = i1;
            }
        }

        return villagedoorinfo;
    }

    public VillageDoorInfo func_75578_e(int i, int j, int k) {
        if (this.field_75582_d.func_71569_e(i, j, k) > (float) (this.field_75583_e * this.field_75583_e)) {
            return null;
        } else {
            Iterator iterator = this.field_75584_b.iterator();

            VillageDoorInfo villagedoorinfo;

            do {
                if (!iterator.hasNext()) {
                    return null;
                }

                villagedoorinfo = (VillageDoorInfo) iterator.next();
            } while (villagedoorinfo.field_75481_a != i || villagedoorinfo.field_75480_c != k || Math.abs(villagedoorinfo.field_75479_b - j) > 1);

            return villagedoorinfo;
        }
    }

    public void func_75576_a(VillageDoorInfo villagedoorinfo) {
        this.field_75584_b.add(villagedoorinfo);
        this.field_75585_c.field_71574_a += villagedoorinfo.field_75481_a;
        this.field_75585_c.field_71572_b += villagedoorinfo.field_75479_b;
        this.field_75585_c.field_71573_c += villagedoorinfo.field_75480_c;
        this.func_75573_l();
        this.field_75580_f = villagedoorinfo.field_75475_f;
    }

    public boolean func_75566_g() {
        return this.field_75584_b.isEmpty();
    }

    public void func_75575_a(EntityLivingBase entitylivingbase) {
        Iterator iterator = this.field_75589_i.iterator();

        Village$VillageAgressor village$villageagressor;

        do {
            if (!iterator.hasNext()) {
                this.field_75589_i.add(new Village$VillageAgressor(this, entitylivingbase, this.field_75581_g));
                return;
            }

            village$villageagressor = (Village$VillageAgressor) iterator.next();
        } while (village$villageagressor.field_75592_a != entitylivingbase);

        village$villageagressor.field_75590_b = this.field_75581_g;
    }

    public EntityLivingBase func_75571_b(EntityLivingBase entitylivingbase) {
        double d0 = Double.MAX_VALUE;
        Village$VillageAgressor village$villageagressor = null;

        for (int i = 0; i < this.field_75589_i.size(); ++i) {
            Village$VillageAgressor village$villageagressor1 = (Village$VillageAgressor) this.field_75589_i.get(i);
            double d1 = village$villageagressor1.field_75592_a.func_70068_e(entitylivingbase);

            if (d1 <= d0) {
                village$villageagressor = village$villageagressor1;
                d0 = d1;
            }
        }

        return village$villageagressor != null ? village$villageagressor.field_75592_a : null;
    }

    public EntityPlayer func_82685_c(EntityLivingBase entitylivingbase) {
        double d0 = Double.MAX_VALUE;
        EntityPlayer entityplayer = null;
        Iterator iterator = this.field_82693_j.keySet().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            if (this.func_82687_d(s)) {
                EntityPlayer entityplayer1 = this.field_75586_a.func_72924_a(s);

                if (entityplayer1 != null) {
                    double d1 = entityplayer1.func_70068_e(entitylivingbase);

                    if (d1 <= d0) {
                        entityplayer = entityplayer1;
                        d0 = d1;
                    }
                }
            }
        }

        return entityplayer;
    }

    private void func_75565_j() {
        Iterator iterator = this.field_75589_i.iterator();

        while (iterator.hasNext()) {
            Village$VillageAgressor village$villageagressor = (Village$VillageAgressor) iterator.next();

            if (!village$villageagressor.field_75592_a.func_70089_S() || Math.abs(this.field_75581_g - village$villageagressor.field_75590_b) > 300) {
                iterator.remove();
            }
        }

    }

    private void func_75557_k() {
        boolean flag = false;
        boolean flag1 = this.field_75586_a.field_73012_v.nextInt(50) == 0;
        Iterator iterator = this.field_75584_b.iterator();

        while (iterator.hasNext()) {
            VillageDoorInfo villagedoorinfo = (VillageDoorInfo) iterator.next();

            if (flag1) {
                villagedoorinfo.func_75466_d();
            }

            if (!this.func_75574_f(villagedoorinfo.field_75481_a, villagedoorinfo.field_75479_b, villagedoorinfo.field_75480_c) || Math.abs(this.field_75581_g - villagedoorinfo.field_75475_f) > 1200) {
                this.field_75585_c.field_71574_a -= villagedoorinfo.field_75481_a;
                this.field_75585_c.field_71572_b -= villagedoorinfo.field_75479_b;
                this.field_75585_c.field_71573_c -= villagedoorinfo.field_75480_c;
                flag = true;
                villagedoorinfo.field_75476_g = true;
                iterator.remove();
            }
        }

        if (flag) {
            this.func_75573_l();
        }

    }

    private boolean func_75574_f(int i, int j, int k) {
        return this.field_75586_a.func_147439_a(i, j, k) == Blocks.field_150466_ao;
    }

    private void func_75573_l() {
        int i = this.field_75584_b.size();

        if (i == 0) {
            this.field_75582_d.func_71571_b(0, 0, 0);
            this.field_75583_e = 0;
        } else {
            this.field_75582_d.func_71571_b(this.field_75585_c.field_71574_a / i, this.field_75585_c.field_71572_b / i, this.field_75585_c.field_71573_c / i);
            int j = 0;

            VillageDoorInfo villagedoorinfo;

            for (Iterator iterator = this.field_75584_b.iterator(); iterator.hasNext(); j = Math.max(villagedoorinfo.func_75474_b(this.field_75582_d.field_71574_a, this.field_75582_d.field_71572_b, this.field_75582_d.field_71573_c), j)) {
                villagedoorinfo = (VillageDoorInfo) iterator.next();
            }

            this.field_75583_e = Math.max(32, (int) Math.sqrt((double) j) + 1);
        }
    }

    public int func_82684_a(String s) {
        Integer integer = (Integer) this.field_82693_j.get(s);

        return integer != null ? integer : 0;
    }

    public int func_82688_a(String s, int i) {
        int j = this.func_82684_a(s);
        int k = MathHelper.func_76125_a(j + i, -30, 10);

        this.field_82693_j.put(s, k);
        return k;
    }

    public boolean func_82687_d(String s) {
        return this.func_82684_a(s) <= -15;
    }

    public void func_82690_a(NBTTagCompound nbttagcompound) {
        this.field_75588_h = nbttagcompound.func_74762_e("PopSize");
        this.field_75583_e = nbttagcompound.func_74762_e("Radius");
        this.field_75587_j = nbttagcompound.func_74762_e("Golems");
        this.field_75580_f = nbttagcompound.func_74762_e("Stable");
        this.field_75581_g = nbttagcompound.func_74762_e("Tick");
        this.field_82694_i = nbttagcompound.func_74762_e("MTick");
        this.field_75582_d.field_71574_a = nbttagcompound.func_74762_e("CX");
        this.field_75582_d.field_71572_b = nbttagcompound.func_74762_e("CY");
        this.field_75582_d.field_71573_c = nbttagcompound.func_74762_e("CZ");
        this.field_75585_c.field_71574_a = nbttagcompound.func_74762_e("ACX");
        this.field_75585_c.field_71572_b = nbttagcompound.func_74762_e("ACY");
        this.field_75585_c.field_71573_c = nbttagcompound.func_74762_e("ACZ");
        NBTTagList nbttaglist = nbttagcompound.func_150295_c("Doors", 10);

        for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.func_150305_b(i);
            VillageDoorInfo villagedoorinfo = new VillageDoorInfo(nbttagcompound1.func_74762_e("X"), nbttagcompound1.func_74762_e("Y"), nbttagcompound1.func_74762_e("Z"), nbttagcompound1.func_74762_e("IDX"), nbttagcompound1.func_74762_e("IDZ"), nbttagcompound1.func_74762_e("TS"));

            this.field_75584_b.add(villagedoorinfo);
        }

        NBTTagList nbttaglist1 = nbttagcompound.func_150295_c("Players", 10);

        for (int j = 0; j < nbttaglist1.func_74745_c(); ++j) {
            NBTTagCompound nbttagcompound2 = nbttaglist1.func_150305_b(j);

            this.field_82693_j.put(nbttagcompound2.func_74779_i("Name"), nbttagcompound2.func_74762_e("S"));
        }

    }

    public void func_82689_b(NBTTagCompound nbttagcompound) {
        nbttagcompound.func_74768_a("PopSize", this.field_75588_h);
        nbttagcompound.func_74768_a("Radius", this.field_75583_e);
        nbttagcompound.func_74768_a("Golems", this.field_75587_j);
        nbttagcompound.func_74768_a("Stable", this.field_75580_f);
        nbttagcompound.func_74768_a("Tick", this.field_75581_g);
        nbttagcompound.func_74768_a("MTick", this.field_82694_i);
        nbttagcompound.func_74768_a("CX", this.field_75582_d.field_71574_a);
        nbttagcompound.func_74768_a("CY", this.field_75582_d.field_71572_b);
        nbttagcompound.func_74768_a("CZ", this.field_75582_d.field_71573_c);
        nbttagcompound.func_74768_a("ACX", this.field_75585_c.field_71574_a);
        nbttagcompound.func_74768_a("ACY", this.field_75585_c.field_71572_b);
        nbttagcompound.func_74768_a("ACZ", this.field_75585_c.field_71573_c);
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.field_75584_b.iterator();

        while (iterator.hasNext()) {
            VillageDoorInfo villagedoorinfo = (VillageDoorInfo) iterator.next();
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            nbttagcompound1.func_74768_a("X", villagedoorinfo.field_75481_a);
            nbttagcompound1.func_74768_a("Y", villagedoorinfo.field_75479_b);
            nbttagcompound1.func_74768_a("Z", villagedoorinfo.field_75480_c);
            nbttagcompound1.func_74768_a("IDX", villagedoorinfo.field_75477_d);
            nbttagcompound1.func_74768_a("IDZ", villagedoorinfo.field_75478_e);
            nbttagcompound1.func_74768_a("TS", villagedoorinfo.field_75475_f);
            nbttaglist.func_74742_a(nbttagcompound1);
        }

        nbttagcompound.func_74782_a("Doors", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();
        Iterator iterator1 = this.field_82693_j.keySet().iterator();

        while (iterator1.hasNext()) {
            String s = (String) iterator1.next();
            NBTTagCompound nbttagcompound2 = new NBTTagCompound();

            nbttagcompound2.func_74778_a("Name", s);
            nbttagcompound2.func_74768_a("S", (Integer) this.field_82693_j.get(s));
            nbttaglist1.func_74742_a(nbttagcompound2);
        }

        nbttagcompound.func_74782_a("Players", nbttaglist1);
    }

    public void func_82692_h() {
        this.field_82694_i = this.field_75581_g;
    }

    public boolean func_82686_i() {
        return this.field_82694_i == 0 || this.field_75581_g - this.field_82694_i >= 3600;
    }

    public void func_82683_b(int i) {
        Iterator iterator = this.field_82693_j.keySet().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            this.func_82688_a(s, i);
        }

    }
}
