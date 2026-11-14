package net.minecraft.world.storage;

import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings$GameType;
import net.minecraft.world.WorldType;

public class WorldInfo {

    private long field_76100_a;
    private WorldType field_76098_b;
    private String field_82576_c;
    private int field_76099_c;
    private int field_76096_d;
    private int field_76097_e;
    private long field_82575_g;
    private long field_76094_f;
    private long field_76095_g;
    private long field_76107_h;
    private NBTTagCompound field_76108_i;
    private int field_76105_j;
    private String field_76106_k;
    private int field_76103_l;
    private boolean field_76104_m;
    private int field_76101_n;
    private boolean field_76102_o;
    private int field_76114_p;
    private WorldSettings$GameType field_76113_q;
    private boolean field_76112_r;
    private boolean field_76111_s;
    private boolean field_76110_t;
    private boolean field_76109_u;
    private GameRules field_82577_x;

    protected WorldInfo() {
        this.field_76098_b = WorldType.field_77137_b;
        this.field_82576_c = "";
        this.field_82577_x = new GameRules();
    }

    public WorldInfo(NBTTagCompound nbttagcompound) {
        this.field_76098_b = WorldType.field_77137_b;
        this.field_82576_c = "";
        this.field_82577_x = new GameRules();
        this.field_76100_a = nbttagcompound.func_74763_f("RandomSeed");
        if (nbttagcompound.func_150297_b("generatorName", 8)) {
            String s = nbttagcompound.func_74779_i("generatorName");

            this.field_76098_b = WorldType.func_77130_a(s);
            if (this.field_76098_b == null) {
                this.field_76098_b = WorldType.field_77137_b;
            } else if (this.field_76098_b.func_77125_e()) {
                int i = 0;

                if (nbttagcompound.func_150297_b("generatorVersion", 99)) {
                    i = nbttagcompound.func_74762_e("generatorVersion");
                }

                this.field_76098_b = this.field_76098_b.func_77132_a(i);
            }

            if (nbttagcompound.func_150297_b("generatorOptions", 8)) {
                this.field_82576_c = nbttagcompound.func_74779_i("generatorOptions");
            }
        }

        this.field_76113_q = WorldSettings$GameType.func_77146_a(nbttagcompound.func_74762_e("GameType"));
        if (nbttagcompound.func_150297_b("MapFeatures", 99)) {
            this.field_76112_r = nbttagcompound.func_74767_n("MapFeatures");
        } else {
            this.field_76112_r = true;
        }

        this.field_76099_c = nbttagcompound.func_74762_e("SpawnX");
        this.field_76096_d = nbttagcompound.func_74762_e("SpawnY");
        this.field_76097_e = nbttagcompound.func_74762_e("SpawnZ");
        this.field_82575_g = nbttagcompound.func_74763_f("Time");
        if (nbttagcompound.func_150297_b("DayTime", 99)) {
            this.field_76094_f = nbttagcompound.func_74763_f("DayTime");
        } else {
            this.field_76094_f = this.field_82575_g;
        }

        this.field_76095_g = nbttagcompound.func_74763_f("LastPlayed");
        this.field_76107_h = nbttagcompound.func_74763_f("SizeOnDisk");
        this.field_76106_k = nbttagcompound.func_74779_i("LevelName");
        this.field_76103_l = nbttagcompound.func_74762_e("version");
        this.field_76101_n = nbttagcompound.func_74762_e("rainTime");
        this.field_76104_m = nbttagcompound.func_74767_n("raining");
        this.field_76114_p = nbttagcompound.func_74762_e("thunderTime");
        this.field_76102_o = nbttagcompound.func_74767_n("thundering");
        this.field_76111_s = nbttagcompound.func_74767_n("hardcore");
        if (nbttagcompound.func_150297_b("initialized", 99)) {
            this.field_76109_u = nbttagcompound.func_74767_n("initialized");
        } else {
            this.field_76109_u = true;
        }

        if (nbttagcompound.func_150297_b("allowCommands", 99)) {
            this.field_76110_t = nbttagcompound.func_74767_n("allowCommands");
        } else {
            this.field_76110_t = this.field_76113_q == WorldSettings$GameType.CREATIVE;
        }

        if (nbttagcompound.func_150297_b("Player", 10)) {
            this.field_76108_i = nbttagcompound.func_74775_l("Player");
            this.field_76105_j = this.field_76108_i.func_74762_e("Dimension");
        }

        if (nbttagcompound.func_150297_b("GameRules", 10)) {
            this.field_82577_x.func_82768_a(nbttagcompound.func_74775_l("GameRules"));
        }

    }

    public WorldInfo(WorldSettings worldsettings, String s) {
        this.field_76098_b = WorldType.field_77137_b;
        this.field_82576_c = "";
        this.field_82577_x = new GameRules();
        this.field_76100_a = worldsettings.func_77160_d();
        this.field_76113_q = worldsettings.func_77162_e();
        this.field_76112_r = worldsettings.func_77164_g();
        this.field_76106_k = s;
        this.field_76111_s = worldsettings.func_77158_f();
        this.field_76098_b = worldsettings.func_77165_h();
        this.field_82576_c = worldsettings.func_82749_j();
        this.field_76110_t = worldsettings.func_77163_i();
        this.field_76109_u = false;
    }

    public WorldInfo(WorldInfo worldinfo) {
        this.field_76098_b = WorldType.field_77137_b;
        this.field_82576_c = "";
        this.field_82577_x = new GameRules();
        this.field_76100_a = worldinfo.field_76100_a;
        this.field_76098_b = worldinfo.field_76098_b;
        this.field_82576_c = worldinfo.field_82576_c;
        this.field_76113_q = worldinfo.field_76113_q;
        this.field_76112_r = worldinfo.field_76112_r;
        this.field_76099_c = worldinfo.field_76099_c;
        this.field_76096_d = worldinfo.field_76096_d;
        this.field_76097_e = worldinfo.field_76097_e;
        this.field_82575_g = worldinfo.field_82575_g;
        this.field_76094_f = worldinfo.field_76094_f;
        this.field_76095_g = worldinfo.field_76095_g;
        this.field_76107_h = worldinfo.field_76107_h;
        this.field_76108_i = worldinfo.field_76108_i;
        this.field_76105_j = worldinfo.field_76105_j;
        this.field_76106_k = worldinfo.field_76106_k;
        this.field_76103_l = worldinfo.field_76103_l;
        this.field_76101_n = worldinfo.field_76101_n;
        this.field_76104_m = worldinfo.field_76104_m;
        this.field_76114_p = worldinfo.field_76114_p;
        this.field_76102_o = worldinfo.field_76102_o;
        this.field_76111_s = worldinfo.field_76111_s;
        this.field_76110_t = worldinfo.field_76110_t;
        this.field_76109_u = worldinfo.field_76109_u;
        this.field_82577_x = worldinfo.field_82577_x;
    }

    public NBTTagCompound func_76066_a() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        this.func_76064_a(nbttagcompound, this.field_76108_i);
        return nbttagcompound;
    }

    public NBTTagCompound func_76082_a(NBTTagCompound nbttagcompound) {
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        this.func_76064_a(nbttagcompound1, nbttagcompound);
        return nbttagcompound1;
    }

    private void func_76064_a(NBTTagCompound nbttagcompound, NBTTagCompound nbttagcompound1) {
        nbttagcompound.func_74772_a("RandomSeed", this.field_76100_a);
        nbttagcompound.func_74778_a("generatorName", this.field_76098_b.func_77127_a());
        nbttagcompound.func_74768_a("generatorVersion", this.field_76098_b.func_77131_c());
        nbttagcompound.func_74778_a("generatorOptions", this.field_82576_c);
        nbttagcompound.func_74768_a("GameType", this.field_76113_q.func_77148_a());
        nbttagcompound.func_74757_a("MapFeatures", this.field_76112_r);
        nbttagcompound.func_74768_a("SpawnX", this.field_76099_c);
        nbttagcompound.func_74768_a("SpawnY", this.field_76096_d);
        nbttagcompound.func_74768_a("SpawnZ", this.field_76097_e);
        nbttagcompound.func_74772_a("Time", this.field_82575_g);
        nbttagcompound.func_74772_a("DayTime", this.field_76094_f);
        nbttagcompound.func_74772_a("SizeOnDisk", this.field_76107_h);
        nbttagcompound.func_74772_a("LastPlayed", MinecraftServer.func_130071_aq());
        nbttagcompound.func_74778_a("LevelName", this.field_76106_k);
        nbttagcompound.func_74768_a("version", this.field_76103_l);
        nbttagcompound.func_74768_a("rainTime", this.field_76101_n);
        nbttagcompound.func_74757_a("raining", this.field_76104_m);
        nbttagcompound.func_74768_a("thunderTime", this.field_76114_p);
        nbttagcompound.func_74757_a("thundering", this.field_76102_o);
        nbttagcompound.func_74757_a("hardcore", this.field_76111_s);
        nbttagcompound.func_74757_a("allowCommands", this.field_76110_t);
        nbttagcompound.func_74757_a("initialized", this.field_76109_u);
        nbttagcompound.func_74782_a("GameRules", this.field_82577_x.func_82770_a());
        if (nbttagcompound1 != null) {
            nbttagcompound.func_74782_a("Player", nbttagcompound1);
        }

    }

    public long func_76063_b() {
        return this.field_76100_a;
    }

    public int func_76079_c() {
        return this.field_76099_c;
    }

    public int func_76075_d() {
        return this.field_76096_d;
    }

    public int func_76074_e() {
        return this.field_76097_e;
    }

    public long func_82573_f() {
        return this.field_82575_g;
    }

    public long func_76073_f() {
        return this.field_76094_f;
    }

    public NBTTagCompound func_76072_h() {
        return this.field_76108_i;
    }

    public int func_76076_i() {
        return this.field_76105_j;
    }

    public void func_82572_b(long i) {
        this.field_82575_g = i;
    }

    public void func_76068_b(long i) {
        this.field_76094_f = i;
    }

    public void func_76081_a(int i, int j, int k) {
        this.field_76099_c = i;
        this.field_76096_d = j;
        this.field_76097_e = k;
    }

    public String func_76065_j() {
        return this.field_76106_k;
    }

    public void func_76062_a(String s) {
        this.field_76106_k = s;
    }

    public int func_76088_k() {
        return this.field_76103_l;
    }

    public void func_76078_e(int i) {
        this.field_76103_l = i;
    }

    public boolean func_76061_m() {
        return this.field_76102_o;
    }

    public void func_76069_a(boolean flag) {
        this.field_76102_o = flag;
    }

    public int func_76071_n() {
        return this.field_76114_p;
    }

    public void func_76090_f(int i) {
        this.field_76114_p = i;
    }

    public boolean func_76059_o() {
        return this.field_76104_m;
    }

    public void func_76084_b(boolean flag) {
        this.field_76104_m = flag;
    }

    public int func_76083_p() {
        return this.field_76101_n;
    }

    public void func_76080_g(int i) {
        this.field_76101_n = i;
    }

    public WorldSettings$GameType func_76077_q() {
        return this.field_76113_q;
    }

    public boolean func_76089_r() {
        return this.field_76112_r;
    }

    public void func_76060_a(WorldSettings$GameType worldsettings$gametype) {
        this.field_76113_q = worldsettings$gametype;
    }

    public boolean func_76093_s() {
        return this.field_76111_s;
    }

    public WorldType func_76067_t() {
        return this.field_76098_b;
    }

    public void func_76085_a(WorldType worldtype) {
        this.field_76098_b = worldtype;
    }

    public String func_82571_y() {
        return this.field_82576_c;
    }

    public boolean func_76086_u() {
        return this.field_76110_t;
    }

    public boolean func_76070_v() {
        return this.field_76109_u;
    }

    public void func_76091_d(boolean flag) {
        this.field_76109_u = flag;
    }

    public GameRules func_82574_x() {
        return this.field_82577_x;
    }

    public void func_85118_a(CrashReportCategory crashreportcategory) {
        crashreportcategory.func_71500_a("Level seed", new WorldInfo$1(this));
        crashreportcategory.func_71500_a("Level generator", new WorldInfo$2(this));
        crashreportcategory.func_71500_a("Level generator options", new WorldInfo$3(this));
        crashreportcategory.func_71500_a("Level spawn location", new WorldInfo$4(this));
        crashreportcategory.func_71500_a("Level time", new WorldInfo$5(this));
        crashreportcategory.func_71500_a("Level dimension", new WorldInfo$6(this));
        crashreportcategory.func_71500_a("Level storage version", new WorldInfo$7(this));
        crashreportcategory.func_71500_a("Level weather", new WorldInfo$8(this));
        crashreportcategory.func_71500_a("Level game mode", new WorldInfo$9(this));
    }
}
