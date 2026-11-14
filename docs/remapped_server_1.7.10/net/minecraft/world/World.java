package net.minecraft.world;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.village.VillageCollection;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;

public abstract class World implements IBlockAccess {

    public boolean field_72999_e;
    public List field_72996_f = new ArrayList();
    protected List field_72997_g = new ArrayList();
    public List field_147482_g = new ArrayList();
    private List field_147484_a = new ArrayList();
    private List field_147483_b = new ArrayList();
    public List field_73010_i = new ArrayList();
    public List field_73007_j = new ArrayList();
    private long field_73001_c = 16777215L;
    public int field_73008_k;
    protected int field_73005_l = (new Random()).nextInt();
    protected final int field_73006_m = 1013904223;
    protected float field_73003_n;
    protected float field_73004_o;
    protected float field_73018_p;
    protected float field_73017_q;
    public int field_73016_r;
    public EnumDifficulty field_73013_u;
    public Random field_73012_v = new Random();
    public final WorldProvider field_73011_w;
    protected List field_73021_x = new ArrayList();
    protected IChunkProvider field_73020_y;
    protected final ISaveHandler field_73019_z;
    protected WorldInfo field_72986_A;
    public boolean field_72987_B;
    public MapStorage field_72988_C;
    public final VillageCollection field_72982_D;
    protected final VillageSiege field_72983_E = new VillageSiege(this);
    public final Profiler field_72984_F;
    private final Calendar field_83016_L = Calendar.getInstance();
    protected Scoreboard field_96442_D = new Scoreboard();
    public boolean field_72995_K;
    protected Set field_72993_I = new HashSet();
    private int field_72990_M;
    protected boolean field_72985_G;
    protected boolean field_72992_H;
    private ArrayList field_72998_d;
    private boolean field_147481_N;
    int[] field_72994_J;

    public BiomeGenBase func_72807_a(int i, int j) {
        if (this.func_72899_e(i, 0, j)) {
            Chunk chunk = this.func_72938_d(i, j);

            try {
                return chunk.func_76591_a(i & 15, j & 15, this.field_73011_w.field_76578_c);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.func_85055_a(throwable, "Getting biome");
                CrashReportCategory crashreportcategory = crashreport.func_85058_a("Coordinates of biome request");

                crashreportcategory.func_71500_a("Location", new World$1(this, i, j));
                throw new ReportedException(crashreport);
            }
        } else {
            return this.field_73011_w.field_76578_c.func_76935_a(i, j);
        }
    }

    public WorldChunkManager func_72959_q() {
        return this.field_73011_w.field_76578_c;
    }

    public World(ISaveHandler isavehandler, String s, WorldSettings worldsettings, WorldProvider worldprovider, Profiler profiler) {
        this.field_72990_M = this.field_73012_v.nextInt(12000);
        this.field_72985_G = true;
        this.field_72992_H = true;
        this.field_72998_d = new ArrayList();
        this.field_72994_J = new int['\u8000'];
        this.field_73019_z = isavehandler;
        this.field_72984_F = profiler;
        this.field_72988_C = new MapStorage(isavehandler);
        this.field_72986_A = isavehandler.func_75757_d();
        if (worldprovider != null) {
            this.field_73011_w = worldprovider;
        } else if (this.field_72986_A != null && this.field_72986_A.func_76076_i() != 0) {
            this.field_73011_w = WorldProvider.func_76570_a(this.field_72986_A.func_76076_i());
        } else {
            this.field_73011_w = WorldProvider.func_76570_a(0);
        }

        if (this.field_72986_A == null) {
            this.field_72986_A = new WorldInfo(worldsettings, s);
        } else {
            this.field_72986_A.func_76062_a(s);
        }

        this.field_73011_w.func_76558_a(this);
        this.field_73020_y = this.func_72970_h();
        if (!this.field_72986_A.func_76070_v()) {
            try {
                this.func_72963_a(worldsettings);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.func_85055_a(throwable, "Exception initializing level");

                try {
                    this.func_72914_a(crashreport);
                } catch (Throwable throwable1) {
                    ;
                }

                throw new ReportedException(crashreport);
            }

            this.field_72986_A.func_76091_d(true);
        }

        VillageCollection villagecollection = (VillageCollection) this.field_72988_C.func_75742_a(VillageCollection.class, "villages");

        if (villagecollection == null) {
            this.field_72982_D = new VillageCollection(this);
            this.field_72988_C.func_75745_a("villages", this.field_72982_D);
        } else {
            this.field_72982_D = villagecollection;
            this.field_72982_D.func_82566_a(this);
        }

        this.func_72966_v();
        this.func_72947_a();
    }

    protected abstract IChunkProvider func_72970_h();

    protected void func_72963_a(WorldSettings worldsettings) {
        this.field_72986_A.func_76091_d(true);
    }

    public Block func_147474_b(int i, int j) {
        int k;

        for (k = 63; !this.func_147437_c(i, k + 1, j); ++k) {
            ;
        }

        return this.func_147439_a(i, k, j);
    }

    @Override
    public Block func_147439_a(int i, int j, int k) {
        if (i >= -30000000 && k >= -30000000 && i < 30000000 && k < 30000000 && j >= 0 && j < 256) {
            Chunk chunk = null;

            try {
                chunk = this.func_72964_e(i >> 4, k >> 4);
                return chunk.func_150810_a(i & 15, j, k & 15);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.func_85055_a(throwable, "Exception getting block type in world");
                CrashReportCategory crashreportcategory = crashreport.func_85058_a("Requested block coordinates");

                crashreportcategory.func_71507_a("Found chunk", chunk == null);
                crashreportcategory.func_71507_a("Location", CrashReportCategory.func_85071_a(i, j, k));
                throw new ReportedException(crashreport);
            }
        } else {
            return Blocks.field_150350_a;
        }
    }

    public boolean func_147437_c(int i, int j, int k) {
        return this.func_147439_a(i, j, k).func_149688_o() == Material.field_151579_a;
    }

    public boolean func_72899_e(int i, int j, int k) {
        return j >= 0 && j < 256 ? this.func_72916_c(i >> 4, k >> 4) : false;
    }

    public boolean func_72873_a(int i, int j, int k, int l) {
        return this.func_72904_c(i - l, j - l, k - l, i + l, j + l, k + l);
    }

    public boolean func_72904_c(int i, int j, int k, int l, int i1, int j1) {
        if (i1 >= 0 && j < 256) {
            i >>= 4;
            k >>= 4;
            l >>= 4;
            j1 >>= 4;

            for (int k1 = i; k1 <= l; ++k1) {
                for (int l1 = k; l1 <= j1; ++l1) {
                    if (!this.func_72916_c(k1, l1)) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    protected boolean func_72916_c(int i, int j) {
        return this.field_73020_y.func_73149_a(i, j);
    }

    public Chunk func_72938_d(int i, int j) {
        return this.func_72964_e(i >> 4, j >> 4);
    }

    public Chunk func_72964_e(int i, int j) {
        return this.field_73020_y.func_73154_d(i, j);
    }

    public boolean func_147465_d(int i, int j, int k, Block block, int l, int i1) {
        if (i >= -30000000 && k >= -30000000 && i < 30000000 && k < 30000000) {
            if (j < 0) {
                return false;
            } else if (j >= 256) {
                return false;
            } else {
                Chunk chunk = this.func_72964_e(i >> 4, k >> 4);
                Block block1 = null;

                if ((i1 & 1) != 0) {
                    block1 = chunk.func_150810_a(i & 15, j, k & 15);
                }

                boolean flag = chunk.func_150807_a(i & 15, j, k & 15, block, l);

                this.field_72984_F.func_76320_a("checkLight");
                this.func_147451_t(i, j, k);
                this.field_72984_F.func_76319_b();
                if (flag) {
                    if ((i1 & 2) != 0 && (!this.field_72995_K || (i1 & 4) == 0) && chunk.func_150802_k()) {
                        this.func_147471_g(i, j, k);
                    }

                    if (!this.field_72995_K && (i1 & 1) != 0) {
                        this.func_147444_c(i, j, k, block1);
                        if (block.func_149740_M()) {
                            this.func_147453_f(i, j, k, block);
                        }
                    }
                }

                return flag;
            }
        } else {
            return false;
        }
    }

    @Override
    public int func_72805_g(int i, int j, int k) {
        if (i >= -30000000 && k >= -30000000 && i < 30000000 && k < 30000000) {
            if (j < 0) {
                return 0;
            } else if (j >= 256) {
                return 0;
            } else {
                Chunk chunk = this.func_72964_e(i >> 4, k >> 4);

                i &= 15;
                k &= 15;
                return chunk.func_76628_c(i, j, k);
            }
        } else {
            return 0;
        }
    }

    public boolean func_72921_c(int i, int j, int k, int l, int i1) {
        if (i >= -30000000 && k >= -30000000 && i < 30000000 && k < 30000000) {
            if (j < 0) {
                return false;
            } else if (j >= 256) {
                return false;
            } else {
                Chunk chunk = this.func_72964_e(i >> 4, k >> 4);
                int j1 = i & 15;
                int k1 = k & 15;
                boolean flag = chunk.func_76589_b(j1, j, k1, l);

                if (flag) {
                    Block block = chunk.func_150810_a(j1, j, k1);

                    if ((i1 & 2) != 0 && (!this.field_72995_K || (i1 & 4) == 0) && chunk.func_150802_k()) {
                        this.func_147471_g(i, j, k);
                    }

                    if (!this.field_72995_K && (i1 & 1) != 0) {
                        this.func_147444_c(i, j, k, block);
                        if (block.func_149740_M()) {
                            this.func_147453_f(i, j, k, block);
                        }
                    }
                }

                return flag;
            }
        } else {
            return false;
        }
    }

    public boolean func_147468_f(int i, int j, int k) {
        return this.func_147465_d(i, j, k, Blocks.field_150350_a, 0, 3);
    }

    public boolean func_147480_a(int i, int j, int k, boolean flag) {
        Block block = this.func_147439_a(i, j, k);

        if (block.func_149688_o() == Material.field_151579_a) {
            return false;
        } else {
            int l = this.func_72805_g(i, j, k);

            this.func_72926_e(2001, i, j, k, Block.func_149682_b(block) + (l << 12));
            if (flag) {
                block.func_149697_b(this, i, j, k, l, 0);
            }

            return this.func_147465_d(i, j, k, Blocks.field_150350_a, 0, 3);
        }
    }

    public boolean func_147449_b(int i, int j, int k, Block block) {
        return this.func_147465_d(i, j, k, block, 0, 3);
    }

    public void func_147471_g(int i, int j, int k) {
        for (int l = 0; l < this.field_73021_x.size(); ++l) {
            ((IWorldAccess) this.field_73021_x.get(l)).func_147586_a(i, j, k);
        }

    }

    public void func_147444_c(int i, int j, int k, Block block) {
        this.func_147459_d(i, j, k, block);
    }

    public void func_72975_g(int i, int j, int k, int l) {
        int i1;

        if (k > l) {
            i1 = l;
            l = k;
            k = i1;
        }

        if (!this.field_73011_w.field_76576_e) {
            for (i1 = k; i1 <= l; ++i1) {
                this.func_147463_c(EnumSkyBlock.Sky, i, i1, j);
            }
        }

        this.func_147458_c(i, k, j, i, l, j);
    }

    public void func_147458_c(int i, int j, int k, int l, int i1, int j1) {
        for (int k1 = 0; k1 < this.field_73021_x.size(); ++k1) {
            ((IWorldAccess) this.field_73021_x.get(k1)).func_147585_a(i, j, k, l, i1, j1);
        }

    }

    public void func_147459_d(int i, int j, int k, Block block) {
        this.func_147460_e(i - 1, j, k, block);
        this.func_147460_e(i + 1, j, k, block);
        this.func_147460_e(i, j - 1, k, block);
        this.func_147460_e(i, j + 1, k, block);
        this.func_147460_e(i, j, k - 1, block);
        this.func_147460_e(i, j, k + 1, block);
    }

    public void func_147441_b(int i, int j, int k, Block block, int l) {
        if (l != 4) {
            this.func_147460_e(i - 1, j, k, block);
        }

        if (l != 5) {
            this.func_147460_e(i + 1, j, k, block);
        }

        if (l != 0) {
            this.func_147460_e(i, j - 1, k, block);
        }

        if (l != 1) {
            this.func_147460_e(i, j + 1, k, block);
        }

        if (l != 2) {
            this.func_147460_e(i, j, k - 1, block);
        }

        if (l != 3) {
            this.func_147460_e(i, j, k + 1, block);
        }

    }

    public void func_147460_e(int i, int j, int k, Block block) {
        if (!this.field_72995_K) {
            Block block1 = this.func_147439_a(i, j, k);

            try {
                block1.func_149695_a(this, i, j, k, block);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.func_85055_a(throwable, "Exception while updating neighbours");
                CrashReportCategory crashreportcategory = crashreport.func_85058_a("Block being updated");

                int l;

                try {
                    l = this.func_72805_g(i, j, k);
                } catch (Throwable throwable1) {
                    l = -1;
                }

                crashreportcategory.func_71500_a("Source block type", new World$2(this, block));
                CrashReportCategory.func_147153_a(crashreportcategory, i, j, k, block1, l);
                throw new ReportedException(crashreport);
            }
        }
    }

    public boolean func_147477_a(int i, int j, int k, Block block) {
        return false;
    }

    public boolean func_72937_j(int i, int j, int k) {
        return this.func_72964_e(i >> 4, k >> 4).func_76619_d(i & 15, j, k & 15);
    }

    public int func_72883_k(int i, int j, int k) {
        if (j < 0) {
            return 0;
        } else {
            if (j >= 256) {
                j = 255;
            }

            return this.func_72964_e(i >> 4, k >> 4).func_76629_c(i & 15, j, k & 15, 0);
        }
    }

    public int func_72957_l(int i, int j, int k) {
        return this.func_72849_a(i, j, k, true);
    }

    public int func_72849_a(int i, int j, int k, boolean flag) {
        if (i >= -30000000 && k >= -30000000 && i < 30000000 && k < 30000000) {
            if (flag && this.func_147439_a(i, j, k).func_149710_n()) {
                int l = this.func_72849_a(i, j + 1, k, false);
                int i1 = this.func_72849_a(i + 1, j, k, false);
                int j1 = this.func_72849_a(i - 1, j, k, false);
                int k1 = this.func_72849_a(i, j, k + 1, false);
                int l1 = this.func_72849_a(i, j, k - 1, false);

                if (i1 > l) {
                    l = i1;
                }

                if (j1 > l) {
                    l = j1;
                }

                if (k1 > l) {
                    l = k1;
                }

                if (l1 > l) {
                    l = l1;
                }

                return l;
            } else if (j < 0) {
                return 0;
            } else {
                if (j >= 256) {
                    j = 255;
                }

                Chunk chunk = this.func_72964_e(i >> 4, k >> 4);

                i &= 15;
                k &= 15;
                return chunk.func_76629_c(i, j, k, this.field_73008_k);
            }
        } else {
            return 15;
        }
    }

    public int func_72976_f(int i, int j) {
        if (i >= -30000000 && j >= -30000000 && i < 30000000 && j < 30000000) {
            if (!this.func_72916_c(i >> 4, j >> 4)) {
                return 0;
            } else {
                Chunk chunk = this.func_72964_e(i >> 4, j >> 4);

                return chunk.func_76611_b(i & 15, j & 15);
            }
        } else {
            return 64;
        }
    }

    public int func_82734_g(int i, int j) {
        if (i >= -30000000 && j >= -30000000 && i < 30000000 && j < 30000000) {
            if (!this.func_72916_c(i >> 4, j >> 4)) {
                return 0;
            } else {
                Chunk chunk = this.func_72964_e(i >> 4, j >> 4);

                return chunk.field_82912_p;
            }
        } else {
            return 64;
        }
    }

    public int func_72972_b(EnumSkyBlock enumskyblock, int i, int j, int k) {
        if (j < 0) {
            j = 0;
        }

        if (j >= 256) {
            j = 255;
        }

        if (i >= -30000000 && k >= -30000000 && i < 30000000 && k < 30000000) {
            int l = i >> 4;
            int i1 = k >> 4;

            if (!this.func_72916_c(l, i1)) {
                return enumskyblock.field_77198_c;
            } else {
                Chunk chunk = this.func_72964_e(l, i1);

                return chunk.func_76614_a(enumskyblock, i & 15, j, k & 15);
            }
        } else {
            return enumskyblock.field_77198_c;
        }
    }

    public void func_72915_b(EnumSkyBlock enumskyblock, int i, int j, int k, int l) {
        if (i >= -30000000 && k >= -30000000 && i < 30000000 && k < 30000000) {
            if (j >= 0) {
                if (j < 256) {
                    if (this.func_72916_c(i >> 4, k >> 4)) {
                        Chunk chunk = this.func_72964_e(i >> 4, k >> 4);

                        chunk.func_76633_a(enumskyblock, i & 15, j, k & 15, l);

                        for (int i1 = 0; i1 < this.field_73021_x.size(); ++i1) {
                            ((IWorldAccess) this.field_73021_x.get(i1)).func_147588_b(i, j, k);
                        }

                    }
                }
            }
        }
    }

    public void func_147479_m(int i, int j, int k) {
        for (int l = 0; l < this.field_73021_x.size(); ++l) {
            ((IWorldAccess) this.field_73021_x.get(l)).func_147588_b(i, j, k);
        }

    }

    public float func_72801_o(int i, int j, int k) {
        return this.field_73011_w.field_76573_f[this.func_72957_l(i, j, k)];
    }

    public boolean func_72935_r() {
        return this.field_73008_k < 4;
    }

    public MovingObjectPosition func_72933_a(Vec3 vec3, Vec3 vec31) {
        return this.func_147447_a(vec3, vec31, false, false, false);
    }

    public MovingObjectPosition func_72901_a(Vec3 vec3, Vec3 vec31, boolean flag) {
        return this.func_147447_a(vec3, vec31, flag, false, false);
    }

    public MovingObjectPosition func_147447_a(Vec3 vec3, Vec3 vec31, boolean flag, boolean flag1, boolean flag2) {
        if (!Double.isNaN(vec3.field_72450_a) && !Double.isNaN(vec3.field_72448_b) && !Double.isNaN(vec3.field_72449_c)) {
            if (!Double.isNaN(vec31.field_72450_a) && !Double.isNaN(vec31.field_72448_b) && !Double.isNaN(vec31.field_72449_c)) {
                int i = MathHelper.func_76128_c(vec31.field_72450_a);
                int j = MathHelper.func_76128_c(vec31.field_72448_b);
                int k = MathHelper.func_76128_c(vec31.field_72449_c);
                int l = MathHelper.func_76128_c(vec3.field_72450_a);
                int i1 = MathHelper.func_76128_c(vec3.field_72448_b);
                int j1 = MathHelper.func_76128_c(vec3.field_72449_c);
                Block block = this.func_147439_a(l, i1, j1);
                int k1 = this.func_72805_g(l, i1, j1);

                if ((!flag1 || block.func_149668_a(this, l, i1, j1) != null) && block.func_149678_a(k1, flag)) {
                    MovingObjectPosition movingobjectposition = block.func_149731_a(this, l, i1, j1, vec3, vec31);

                    if (movingobjectposition != null) {
                        return movingobjectposition;
                    }
                }

                MovingObjectPosition movingobjectposition1 = null;

                k1 = 200;

                while (k1-- >= 0) {
                    if (Double.isNaN(vec3.field_72450_a) || Double.isNaN(vec3.field_72448_b) || Double.isNaN(vec3.field_72449_c)) {
                        return null;
                    }

                    if (l == i && i1 == j && j1 == k) {
                        return flag2 ? movingobjectposition1 : null;
                    }

                    boolean flag3 = true;
                    boolean flag4 = true;
                    boolean flag5 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (i > l) {
                        d0 = (double) l + 1.0D;
                    } else if (i < l) {
                        d0 = (double) l + 0.0D;
                    } else {
                        flag3 = false;
                    }

                    if (j > i1) {
                        d1 = (double) i1 + 1.0D;
                    } else if (j < i1) {
                        d1 = (double) i1 + 0.0D;
                    } else {
                        flag4 = false;
                    }

                    if (k > j1) {
                        d2 = (double) j1 + 1.0D;
                    } else if (k < j1) {
                        d2 = (double) j1 + 0.0D;
                    } else {
                        flag5 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = vec31.field_72450_a - vec3.field_72450_a;
                    double d7 = vec31.field_72448_b - vec3.field_72448_b;
                    double d8 = vec31.field_72449_c - vec3.field_72449_c;

                    if (flag3) {
                        d3 = (d0 - vec3.field_72450_a) / d6;
                    }

                    if (flag4) {
                        d4 = (d1 - vec3.field_72448_b) / d7;
                    }

                    if (flag5) {
                        d5 = (d2 - vec3.field_72449_c) / d8;
                    }

                    boolean flag6 = false;
                    byte b0;

                    if (d3 < d4 && d3 < d5) {
                        if (i > l) {
                            b0 = 4;
                        } else {
                            b0 = 5;
                        }

                        vec3.field_72450_a = d0;
                        vec3.field_72448_b += d7 * d3;
                        vec3.field_72449_c += d8 * d3;
                    } else if (d4 < d5) {
                        if (j > i1) {
                            b0 = 0;
                        } else {
                            b0 = 1;
                        }

                        vec3.field_72450_a += d6 * d4;
                        vec3.field_72448_b = d1;
                        vec3.field_72449_c += d8 * d4;
                    } else {
                        if (k > j1) {
                            b0 = 2;
                        } else {
                            b0 = 3;
                        }

                        vec3.field_72450_a += d6 * d5;
                        vec3.field_72448_b += d7 * d5;
                        vec3.field_72449_c = d2;
                    }

                    Vec3 vec32 = Vec3.func_72443_a(vec3.field_72450_a, vec3.field_72448_b, vec3.field_72449_c);

                    l = (int) (vec32.field_72450_a = (double) MathHelper.func_76128_c(vec3.field_72450_a));
                    if (b0 == 5) {
                        --l;
                        ++vec32.field_72450_a;
                    }

                    i1 = (int) (vec32.field_72448_b = (double) MathHelper.func_76128_c(vec3.field_72448_b));
                    if (b0 == 1) {
                        --i1;
                        ++vec32.field_72448_b;
                    }

                    j1 = (int) (vec32.field_72449_c = (double) MathHelper.func_76128_c(vec3.field_72449_c));
                    if (b0 == 3) {
                        --j1;
                        ++vec32.field_72449_c;
                    }

                    Block block1 = this.func_147439_a(l, i1, j1);
                    int l1 = this.func_72805_g(l, i1, j1);

                    if (!flag1 || block1.func_149668_a(this, l, i1, j1) != null) {
                        if (block1.func_149678_a(l1, flag)) {
                            MovingObjectPosition movingobjectposition2 = block1.func_149731_a(this, l, i1, j1, vec3, vec31);

                            if (movingobjectposition2 != null) {
                                return movingobjectposition2;
                            }
                        } else {
                            movingobjectposition1 = new MovingObjectPosition(l, i1, j1, b0, vec3, false);
                        }
                    }
                }

                return flag2 ? movingobjectposition1 : null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void func_72956_a(Entity entity, String s, float f, float f1) {
        for (int i = 0; i < this.field_73021_x.size(); ++i) {
            ((IWorldAccess) this.field_73021_x.get(i)).func_72704_a(s, entity.field_70165_t, entity.field_70163_u - (double) entity.field_70129_M, entity.field_70161_v, f, f1);
        }

    }

    public void func_85173_a(EntityPlayer entityplayer, String s, float f, float f1) {
        for (int i = 0; i < this.field_73021_x.size(); ++i) {
            ((IWorldAccess) this.field_73021_x.get(i)).func_85102_a(entityplayer, s, entityplayer.field_70165_t, entityplayer.field_70163_u - (double) entityplayer.field_70129_M, entityplayer.field_70161_v, f, f1);
        }

    }

    public void func_72908_a(double d0, double d1, double d2, String s, float f, float f1) {
        for (int i = 0; i < this.field_73021_x.size(); ++i) {
            ((IWorldAccess) this.field_73021_x.get(i)).func_72704_a(s, d0, d1, d2, f, f1);
        }

    }

    public void func_72980_b(double d0, double d1, double d2, String s, float f, float f1, boolean flag) {}

    public void func_72934_a(String s, int i, int j, int k) {
        for (int l = 0; l < this.field_73021_x.size(); ++l) {
            ((IWorldAccess) this.field_73021_x.get(l)).func_72702_a(s, i, j, k);
        }

    }

    public void func_72869_a(String s, double d0, double d1, double d2, double d3, double d4, double d5) {
        for (int i = 0; i < this.field_73021_x.size(); ++i) {
            ((IWorldAccess) this.field_73021_x.get(i)).func_72708_a(s, d0, d1, d2, d3, d4, d5);
        }

    }

    public boolean func_72942_c(Entity entity) {
        this.field_73007_j.add(entity);
        return true;
    }

    public boolean func_72838_d(Entity entity) {
        int i = MathHelper.func_76128_c(entity.field_70165_t / 16.0D);
        int j = MathHelper.func_76128_c(entity.field_70161_v / 16.0D);
        boolean flag = entity.field_98038_p;

        if (entity instanceof EntityPlayer) {
            flag = true;
        }

        if (!flag && !this.func_72916_c(i, j)) {
            return false;
        } else {
            if (entity instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) entity;

                this.field_73010_i.add(entityplayer);
                this.func_72854_c();
            }

            this.func_72964_e(i, j).func_76612_a(entity);
            this.field_72996_f.add(entity);
            this.func_72923_a(entity);
            return true;
        }
    }

    protected void func_72923_a(Entity entity) {
        for (int i = 0; i < this.field_73021_x.size(); ++i) {
            ((IWorldAccess) this.field_73021_x.get(i)).func_72703_a(entity);
        }

    }

    protected void func_72847_b(Entity entity) {
        for (int i = 0; i < this.field_73021_x.size(); ++i) {
            ((IWorldAccess) this.field_73021_x.get(i)).func_72709_b(entity);
        }

    }

    public void func_72900_e(Entity entity) {
        if (entity.field_70153_n != null) {
            entity.field_70153_n.func_70078_a((Entity) null);
        }

        if (entity.field_70154_o != null) {
            entity.func_70078_a((Entity) null);
        }

        entity.func_70106_y();
        if (entity instanceof EntityPlayer) {
            this.field_73010_i.remove(entity);
            this.func_72854_c();
            this.func_72847_b(entity);
        }

    }

    public void func_72973_f(Entity entity) {
        entity.func_70106_y();
        if (entity instanceof EntityPlayer) {
            this.field_73010_i.remove(entity);
            this.func_72854_c();
        }

        int i = entity.field_70176_ah;
        int j = entity.field_70164_aj;

        if (entity.field_70175_ag && this.func_72916_c(i, j)) {
            this.func_72964_e(i, j).func_76622_b(entity);
        }

        this.field_72996_f.remove(entity);
        this.func_72847_b(entity);
    }

    public void func_72954_a(IWorldAccess iworldaccess) {
        this.field_73021_x.add(iworldaccess);
    }

    public List func_72945_a(Entity entity, AxisAlignedBB axisalignedbb) {
        this.field_72998_d.clear();
        int i = MathHelper.func_76128_c(axisalignedbb.field_72340_a);
        int j = MathHelper.func_76128_c(axisalignedbb.field_72336_d + 1.0D);
        int k = MathHelper.func_76128_c(axisalignedbb.field_72338_b);
        int l = MathHelper.func_76128_c(axisalignedbb.field_72337_e + 1.0D);
        int i1 = MathHelper.func_76128_c(axisalignedbb.field_72339_c);
        int j1 = MathHelper.func_76128_c(axisalignedbb.field_72334_f + 1.0D);

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = i1; l1 < j1; ++l1) {
                if (this.func_72899_e(k1, 64, l1)) {
                    for (int i2 = k - 1; i2 < l; ++i2) {
                        Block block;

                        if (k1 >= -30000000 && k1 < 30000000 && l1 >= -30000000 && l1 < 30000000) {
                            block = this.func_147439_a(k1, i2, l1);
                        } else {
                            block = Blocks.field_150348_b;
                        }

                        block.func_149743_a(this, k1, i2, l1, axisalignedbb, this.field_72998_d, entity);
                    }
                }
            }
        }

        double d0 = 0.25D;
        List list = this.func_72839_b(entity, axisalignedbb.func_72314_b(d0, d0, d0));

        for (int j2 = 0; j2 < list.size(); ++j2) {
            AxisAlignedBB axisalignedbb1 = ((Entity) list.get(j2)).func_70046_E();

            if (axisalignedbb1 != null && axisalignedbb1.func_72326_a(axisalignedbb)) {
                this.field_72998_d.add(axisalignedbb1);
            }

            axisalignedbb1 = entity.func_70114_g((Entity) list.get(j2));
            if (axisalignedbb1 != null && axisalignedbb1.func_72326_a(axisalignedbb)) {
                this.field_72998_d.add(axisalignedbb1);
            }
        }

        return this.field_72998_d;
    }

    public List func_147461_a(AxisAlignedBB axisalignedbb) {
        this.field_72998_d.clear();
        int i = MathHelper.func_76128_c(axisalignedbb.field_72340_a);
        int j = MathHelper.func_76128_c(axisalignedbb.field_72336_d + 1.0D);
        int k = MathHelper.func_76128_c(axisalignedbb.field_72338_b);
        int l = MathHelper.func_76128_c(axisalignedbb.field_72337_e + 1.0D);
        int i1 = MathHelper.func_76128_c(axisalignedbb.field_72339_c);
        int j1 = MathHelper.func_76128_c(axisalignedbb.field_72334_f + 1.0D);

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = i1; l1 < j1; ++l1) {
                if (this.func_72899_e(k1, 64, l1)) {
                    for (int i2 = k - 1; i2 < l; ++i2) {
                        Block block;

                        if (k1 >= -30000000 && k1 < 30000000 && l1 >= -30000000 && l1 < 30000000) {
                            block = this.func_147439_a(k1, i2, l1);
                        } else {
                            block = Blocks.field_150357_h;
                        }

                        block.func_149743_a(this, k1, i2, l1, axisalignedbb, this.field_72998_d, (Entity) null);
                    }
                }
            }
        }

        return this.field_72998_d;
    }

    public int func_72967_a(float f) {
        float f1 = this.func_72826_c(f);
        float f2 = 1.0F - (MathHelper.func_76134_b(f1 * 3.1415927F * 2.0F) * 2.0F + 0.5F);

        if (f2 < 0.0F) {
            f2 = 0.0F;
        }

        if (f2 > 1.0F) {
            f2 = 1.0F;
        }

        f2 = 1.0F - f2;
        f2 = (float) ((double) f2 * (1.0D - (double) (this.func_72867_j(f) * 5.0F) / 16.0D));
        f2 = (float) ((double) f2 * (1.0D - (double) (this.func_72819_i(f) * 5.0F) / 16.0D));
        f2 = 1.0F - f2;
        return (int) (f2 * 11.0F);
    }

    public float func_72826_c(float f) {
        return this.field_73011_w.func_76563_a(this.field_72986_A.func_76073_f(), f);
    }

    public float func_130001_d() {
        return WorldProvider.field_111203_a[this.field_73011_w.func_76559_b(this.field_72986_A.func_76073_f())];
    }

    public float func_72929_e(float f) {
        float f1 = this.func_72826_c(f);

        return f1 * 3.1415927F * 2.0F;
    }

    public int func_72874_g(int i, int j) {
        return this.func_72938_d(i, j).func_76626_d(i & 15, j & 15);
    }

    public int func_72825_h(int i, int j) {
        Chunk chunk = this.func_72938_d(i, j);
        int k = chunk.func_76625_h() + 15;

        i &= 15;

        for (j &= 15; k > 0; --k) {
            Block block = chunk.func_150810_a(i, k, j);

            if (block.func_149688_o().func_76230_c() && block.func_149688_o() != Material.field_151584_j) {
                return k + 1;
            }
        }

        return -1;
    }

    public void func_147464_a(int i, int j, int k, Block block, int l) {}

    public void func_147454_a(int i, int j, int k, Block block, int l, int i1) {}

    public void func_147446_b(int i, int j, int k, Block block, int l, int i1) {}

    public void func_72939_s() {
        this.field_72984_F.func_76320_a("entities");
        this.field_72984_F.func_76320_a("global");

        Entity entity;
        int i;
        CrashReport crashreport;
        CrashReportCategory crashreportcategory;

        for (i = 0; i < this.field_73007_j.size(); ++i) {
            entity = (Entity) this.field_73007_j.get(i);

            try {
                ++entity.field_70173_aa;
                entity.func_70071_h_();
            } catch (Throwable throwable) {
                crashreport = CrashReport.func_85055_a(throwable, "Ticking entity");
                crashreportcategory = crashreport.func_85058_a("Entity being ticked");
                if (entity == null) {
                    crashreportcategory.func_71507_a("Entity", "~~NULL~~");
                } else {
                    entity.func_85029_a(crashreportcategory);
                }

                throw new ReportedException(crashreport);
            }

            if (entity.field_70128_L) {
                this.field_73007_j.remove(i--);
            }
        }

        this.field_72984_F.func_76318_c("remove");
        this.field_72996_f.removeAll(this.field_72997_g);

        int j;
        int k;

        for (i = 0; i < this.field_72997_g.size(); ++i) {
            entity = (Entity) this.field_72997_g.get(i);
            j = entity.field_70176_ah;
            k = entity.field_70164_aj;
            if (entity.field_70175_ag && this.func_72916_c(j, k)) {
                this.func_72964_e(j, k).func_76622_b(entity);
            }
        }

        for (i = 0; i < this.field_72997_g.size(); ++i) {
            this.func_72847_b((Entity) this.field_72997_g.get(i));
        }

        this.field_72997_g.clear();
        this.field_72984_F.func_76318_c("regular");

        for (i = 0; i < this.field_72996_f.size(); ++i) {
            entity = (Entity) this.field_72996_f.get(i);
            if (entity.field_70154_o != null) {
                if (!entity.field_70154_o.field_70128_L && entity.field_70154_o.field_70153_n == entity) {
                    continue;
                }

                entity.field_70154_o.field_70153_n = null;
                entity.field_70154_o = null;
            }

            this.field_72984_F.func_76320_a("tick");
            if (!entity.field_70128_L) {
                try {
                    this.func_72870_g(entity);
                } catch (Throwable throwable1) {
                    crashreport = CrashReport.func_85055_a(throwable1, "Ticking entity");
                    crashreportcategory = crashreport.func_85058_a("Entity being ticked");
                    entity.func_85029_a(crashreportcategory);
                    throw new ReportedException(crashreport);
                }
            }

            this.field_72984_F.func_76319_b();
            this.field_72984_F.func_76320_a("remove");
            if (entity.field_70128_L) {
                j = entity.field_70176_ah;
                k = entity.field_70164_aj;
                if (entity.field_70175_ag && this.func_72916_c(j, k)) {
                    this.func_72964_e(j, k).func_76622_b(entity);
                }

                this.field_72996_f.remove(i--);
                this.func_72847_b(entity);
            }

            this.field_72984_F.func_76319_b();
        }

        this.field_72984_F.func_76318_c("blockEntities");
        this.field_147481_N = true;
        Iterator iterator = this.field_147482_g.iterator();

        while (iterator.hasNext()) {
            TileEntity tileentity = (TileEntity) iterator.next();

            if (!tileentity.func_145837_r() && tileentity.func_145830_o() && this.func_72899_e(tileentity.field_145851_c, tileentity.field_145848_d, tileentity.field_145849_e)) {
                try {
                    tileentity.func_145845_h();
                } catch (Throwable throwable2) {
                    crashreport = CrashReport.func_85055_a(throwable2, "Ticking block entity");
                    crashreportcategory = crashreport.func_85058_a("Block entity being ticked");
                    tileentity.func_145828_a(crashreportcategory);
                    throw new ReportedException(crashreport);
                }
            }

            if (tileentity.func_145837_r()) {
                iterator.remove();
                if (this.func_72916_c(tileentity.field_145851_c >> 4, tileentity.field_145849_e >> 4)) {
                    Chunk chunk = this.func_72964_e(tileentity.field_145851_c >> 4, tileentity.field_145849_e >> 4);

                    if (chunk != null) {
                        chunk.func_150805_f(tileentity.field_145851_c & 15, tileentity.field_145848_d, tileentity.field_145849_e & 15);
                    }
                }
            }
        }

        this.field_147481_N = false;
        if (!this.field_147483_b.isEmpty()) {
            this.field_147482_g.removeAll(this.field_147483_b);
            this.field_147483_b.clear();
        }

        this.field_72984_F.func_76318_c("pendingBlockEntities");
        if (!this.field_147484_a.isEmpty()) {
            for (int l = 0; l < this.field_147484_a.size(); ++l) {
                TileEntity tileentity1 = (TileEntity) this.field_147484_a.get(l);

                if (!tileentity1.func_145837_r()) {
                    if (!this.field_147482_g.contains(tileentity1)) {
                        this.field_147482_g.add(tileentity1);
                    }

                    if (this.func_72916_c(tileentity1.field_145851_c >> 4, tileentity1.field_145849_e >> 4)) {
                        Chunk chunk1 = this.func_72964_e(tileentity1.field_145851_c >> 4, tileentity1.field_145849_e >> 4);

                        if (chunk1 != null) {
                            chunk1.func_150812_a(tileentity1.field_145851_c & 15, tileentity1.field_145848_d, tileentity1.field_145849_e & 15, tileentity1);
                        }
                    }

                    this.func_147471_g(tileentity1.field_145851_c, tileentity1.field_145848_d, tileentity1.field_145849_e);
                }
            }

            this.field_147484_a.clear();
        }

        this.field_72984_F.func_76319_b();
        this.field_72984_F.func_76319_b();
    }

    public void func_147448_a(Collection collection) {
        if (this.field_147481_N) {
            this.field_147484_a.addAll(collection);
        } else {
            this.field_147482_g.addAll(collection);
        }

    }

    public void func_72870_g(Entity entity) {
        this.func_72866_a(entity, true);
    }

    public void func_72866_a(Entity entity, boolean flag) {
        int i = MathHelper.func_76128_c(entity.field_70165_t);
        int j = MathHelper.func_76128_c(entity.field_70161_v);
        byte b0 = 32;

        if (!flag || this.func_72904_c(i - b0, 0, j - b0, i + b0, 0, j + b0)) {
            entity.field_70142_S = entity.field_70165_t;
            entity.field_70137_T = entity.field_70163_u;
            entity.field_70136_U = entity.field_70161_v;
            entity.field_70126_B = entity.field_70177_z;
            entity.field_70127_C = entity.field_70125_A;
            if (flag && entity.field_70175_ag) {
                ++entity.field_70173_aa;
                if (entity.field_70154_o != null) {
                    entity.func_70098_U();
                } else {
                    entity.func_70071_h_();
                }
            }

            this.field_72984_F.func_76320_a("chunkCheck");
            if (Double.isNaN(entity.field_70165_t) || Double.isInfinite(entity.field_70165_t)) {
                entity.field_70165_t = entity.field_70142_S;
            }

            if (Double.isNaN(entity.field_70163_u) || Double.isInfinite(entity.field_70163_u)) {
                entity.field_70163_u = entity.field_70137_T;
            }

            if (Double.isNaN(entity.field_70161_v) || Double.isInfinite(entity.field_70161_v)) {
                entity.field_70161_v = entity.field_70136_U;
            }

            if (Double.isNaN((double) entity.field_70125_A) || Double.isInfinite((double) entity.field_70125_A)) {
                entity.field_70125_A = entity.field_70127_C;
            }

            if (Double.isNaN((double) entity.field_70177_z) || Double.isInfinite((double) entity.field_70177_z)) {
                entity.field_70177_z = entity.field_70126_B;
            }

            int k = MathHelper.func_76128_c(entity.field_70165_t / 16.0D);
            int l = MathHelper.func_76128_c(entity.field_70163_u / 16.0D);
            int i1 = MathHelper.func_76128_c(entity.field_70161_v / 16.0D);

            if (!entity.field_70175_ag || entity.field_70176_ah != k || entity.field_70162_ai != l || entity.field_70164_aj != i1) {
                if (entity.field_70175_ag && this.func_72916_c(entity.field_70176_ah, entity.field_70164_aj)) {
                    this.func_72964_e(entity.field_70176_ah, entity.field_70164_aj).func_76608_a(entity, entity.field_70162_ai);
                }

                if (this.func_72916_c(k, i1)) {
                    entity.field_70175_ag = true;
                    this.func_72964_e(k, i1).func_76612_a(entity);
                } else {
                    entity.field_70175_ag = false;
                }
            }

            this.field_72984_F.func_76319_b();
            if (flag && entity.field_70175_ag && entity.field_70153_n != null) {
                if (!entity.field_70153_n.field_70128_L && entity.field_70153_n.field_70154_o == entity) {
                    this.func_72870_g(entity.field_70153_n);
                } else {
                    entity.field_70153_n.field_70154_o = null;
                    entity.field_70153_n = null;
                }
            }

        }
    }

    public boolean func_72855_b(AxisAlignedBB axisalignedbb) {
        return this.func_72917_a(axisalignedbb, (Entity) null);
    }

    public boolean func_72917_a(AxisAlignedBB axisalignedbb, Entity entity) {
        List list = this.func_72839_b((Entity) null, axisalignedbb);

        for (int i = 0; i < list.size(); ++i) {
            Entity entity1 = (Entity) list.get(i);

            if (!entity1.field_70128_L && entity1.field_70156_m && entity1 != entity) {
                return false;
            }
        }

        return true;
    }

    public boolean func_72829_c(AxisAlignedBB axisalignedbb) {
        int i = MathHelper.func_76128_c(axisalignedbb.field_72340_a);
        int j = MathHelper.func_76128_c(axisalignedbb.field_72336_d + 1.0D);
        int k = MathHelper.func_76128_c(axisalignedbb.field_72338_b);
        int l = MathHelper.func_76128_c(axisalignedbb.field_72337_e + 1.0D);
        int i1 = MathHelper.func_76128_c(axisalignedbb.field_72339_c);
        int j1 = MathHelper.func_76128_c(axisalignedbb.field_72334_f + 1.0D);

        if (axisalignedbb.field_72340_a < 0.0D) {
            --i;
        }

        if (axisalignedbb.field_72338_b < 0.0D) {
            --k;
        }

        if (axisalignedbb.field_72339_c < 0.0D) {
            --i1;
        }

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    Block block = this.func_147439_a(k1, l1, i2);

                    if (block.func_149688_o() != Material.field_151579_a) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean func_72953_d(AxisAlignedBB axisalignedbb) {
        int i = MathHelper.func_76128_c(axisalignedbb.field_72340_a);
        int j = MathHelper.func_76128_c(axisalignedbb.field_72336_d + 1.0D);
        int k = MathHelper.func_76128_c(axisalignedbb.field_72338_b);
        int l = MathHelper.func_76128_c(axisalignedbb.field_72337_e + 1.0D);
        int i1 = MathHelper.func_76128_c(axisalignedbb.field_72339_c);
        int j1 = MathHelper.func_76128_c(axisalignedbb.field_72334_f + 1.0D);

        if (axisalignedbb.field_72340_a < 0.0D) {
            --i;
        }

        if (axisalignedbb.field_72338_b < 0.0D) {
            --k;
        }

        if (axisalignedbb.field_72339_c < 0.0D) {
            --i1;
        }

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    Block block = this.func_147439_a(k1, l1, i2);

                    if (block.func_149688_o().func_76224_d()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean func_147470_e(AxisAlignedBB axisalignedbb) {
        int i = MathHelper.func_76128_c(axisalignedbb.field_72340_a);
        int j = MathHelper.func_76128_c(axisalignedbb.field_72336_d + 1.0D);
        int k = MathHelper.func_76128_c(axisalignedbb.field_72338_b);
        int l = MathHelper.func_76128_c(axisalignedbb.field_72337_e + 1.0D);
        int i1 = MathHelper.func_76128_c(axisalignedbb.field_72339_c);
        int j1 = MathHelper.func_76128_c(axisalignedbb.field_72334_f + 1.0D);

        if (this.func_72904_c(i, k, i1, j, l, j1)) {
            for (int k1 = i; k1 < j; ++k1) {
                for (int l1 = k; l1 < l; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        Block block = this.func_147439_a(k1, l1, i2);

                        if (block == Blocks.field_150480_ab || block == Blocks.field_150356_k || block == Blocks.field_150353_l) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean func_72918_a(AxisAlignedBB axisalignedbb, Material material, Entity entity) {
        int i = MathHelper.func_76128_c(axisalignedbb.field_72340_a);
        int j = MathHelper.func_76128_c(axisalignedbb.field_72336_d + 1.0D);
        int k = MathHelper.func_76128_c(axisalignedbb.field_72338_b);
        int l = MathHelper.func_76128_c(axisalignedbb.field_72337_e + 1.0D);
        int i1 = MathHelper.func_76128_c(axisalignedbb.field_72339_c);
        int j1 = MathHelper.func_76128_c(axisalignedbb.field_72334_f + 1.0D);

        if (!this.func_72904_c(i, k, i1, j, l, j1)) {
            return false;
        } else {
            boolean flag = false;
            Vec3 vec3 = Vec3.func_72443_a(0.0D, 0.0D, 0.0D);

            for (int k1 = i; k1 < j; ++k1) {
                for (int l1 = k; l1 < l; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        Block block = this.func_147439_a(k1, l1, i2);

                        if (block.func_149688_o() == material) {
                            double d0 = (double) ((float) (l1 + 1) - BlockLiquid.func_149801_b(this.func_72805_g(k1, l1, i2)));

                            if ((double) l >= d0) {
                                flag = true;
                                block.func_149640_a(this, k1, l1, i2, entity, vec3);
                            }
                        }
                    }
                }
            }

            if (vec3.func_72433_c() > 0.0D && entity.func_96092_aw()) {
                vec3 = vec3.func_72432_b();
                double d1 = 0.014D;

                entity.field_70159_w += vec3.field_72450_a * d1;
                entity.field_70181_x += vec3.field_72448_b * d1;
                entity.field_70179_y += vec3.field_72449_c * d1;
            }

            return flag;
        }
    }

    public boolean func_72875_a(AxisAlignedBB axisalignedbb, Material material) {
        int i = MathHelper.func_76128_c(axisalignedbb.field_72340_a);
        int j = MathHelper.func_76128_c(axisalignedbb.field_72336_d + 1.0D);
        int k = MathHelper.func_76128_c(axisalignedbb.field_72338_b);
        int l = MathHelper.func_76128_c(axisalignedbb.field_72337_e + 1.0D);
        int i1 = MathHelper.func_76128_c(axisalignedbb.field_72339_c);
        int j1 = MathHelper.func_76128_c(axisalignedbb.field_72334_f + 1.0D);

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    if (this.func_147439_a(k1, l1, i2).func_149688_o() == material) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean func_72830_b(AxisAlignedBB axisalignedbb, Material material) {
        int i = MathHelper.func_76128_c(axisalignedbb.field_72340_a);
        int j = MathHelper.func_76128_c(axisalignedbb.field_72336_d + 1.0D);
        int k = MathHelper.func_76128_c(axisalignedbb.field_72338_b);
        int l = MathHelper.func_76128_c(axisalignedbb.field_72337_e + 1.0D);
        int i1 = MathHelper.func_76128_c(axisalignedbb.field_72339_c);
        int j1 = MathHelper.func_76128_c(axisalignedbb.field_72334_f + 1.0D);

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    Block block = this.func_147439_a(k1, l1, i2);

                    if (block.func_149688_o() == material) {
                        int j2 = this.func_72805_g(k1, l1, i2);
                        double d0 = (double) (l1 + 1);

                        if (j2 < 8) {
                            d0 = (double) (l1 + 1) - (double) j2 / 8.0D;
                        }

                        if (d0 >= axisalignedbb.field_72338_b) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public Explosion func_72876_a(Entity entity, double d0, double d1, double d2, float f, boolean flag) {
        return this.func_72885_a(entity, d0, d1, d2, f, false, flag);
    }

    public Explosion func_72885_a(Entity entity, double d0, double d1, double d2, float f, boolean flag, boolean flag1) {
        Explosion explosion = new Explosion(this, entity, d0, d1, d2, f);

        explosion.field_77286_a = flag;
        explosion.field_82755_b = flag1;
        explosion.func_77278_a();
        explosion.func_77279_a(true);
        return explosion;
    }

    public float func_72842_a(Vec3 vec3, AxisAlignedBB axisalignedbb) {
        double d0 = 1.0D / ((axisalignedbb.field_72336_d - axisalignedbb.field_72340_a) * 2.0D + 1.0D);
        double d1 = 1.0D / ((axisalignedbb.field_72337_e - axisalignedbb.field_72338_b) * 2.0D + 1.0D);
        double d2 = 1.0D / ((axisalignedbb.field_72334_f - axisalignedbb.field_72339_c) * 2.0D + 1.0D);

        if (d0 >= 0.0D && d1 >= 0.0D && d2 >= 0.0D) {
            int i = 0;
            int j = 0;

            for (float f = 0.0F; f <= 1.0F; f = (float) ((double) f + d0)) {
                for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float) ((double) f1 + d1)) {
                    for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float) ((double) f2 + d2)) {
                        double d3 = axisalignedbb.field_72340_a + (axisalignedbb.field_72336_d - axisalignedbb.field_72340_a) * (double) f;
                        double d4 = axisalignedbb.field_72338_b + (axisalignedbb.field_72337_e - axisalignedbb.field_72338_b) * (double) f1;
                        double d5 = axisalignedbb.field_72339_c + (axisalignedbb.field_72334_f - axisalignedbb.field_72339_c) * (double) f2;

                        if (this.func_72933_a(Vec3.func_72443_a(d3, d4, d5), vec3) == null) {
                            ++i;
                        }

                        ++j;
                    }
                }
            }

            return (float) i / (float) j;
        } else {
            return 0.0F;
        }
    }

    public boolean func_72886_a(EntityPlayer entityplayer, int i, int j, int k, int l) {
        if (l == 0) {
            --j;
        }

        if (l == 1) {
            ++j;
        }

        if (l == 2) {
            --k;
        }

        if (l == 3) {
            ++k;
        }

        if (l == 4) {
            --i;
        }

        if (l == 5) {
            ++i;
        }

        if (this.func_147439_a(i, j, k) == Blocks.field_150480_ab) {
            this.func_72889_a(entityplayer, 1004, i, j, k, 0);
            this.func_147468_f(i, j, k);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public TileEntity func_147438_o(int i, int j, int k) {
        if (j >= 0 && j < 256) {
            TileEntity tileentity = null;
            TileEntity tileentity1;
            int l;

            if (this.field_147481_N) {
                for (l = 0; l < this.field_147484_a.size(); ++l) {
                    tileentity1 = (TileEntity) this.field_147484_a.get(l);
                    if (!tileentity1.func_145837_r() && tileentity1.field_145851_c == i && tileentity1.field_145848_d == j && tileentity1.field_145849_e == k) {
                        tileentity = tileentity1;
                        break;
                    }
                }
            }

            if (tileentity == null) {
                Chunk chunk = this.func_72964_e(i >> 4, k >> 4);

                if (chunk != null) {
                    tileentity = chunk.func_150806_e(i & 15, j, k & 15);
                }
            }

            if (tileentity == null) {
                for (l = 0; l < this.field_147484_a.size(); ++l) {
                    tileentity1 = (TileEntity) this.field_147484_a.get(l);
                    if (!tileentity1.func_145837_r() && tileentity1.field_145851_c == i && tileentity1.field_145848_d == j && tileentity1.field_145849_e == k) {
                        tileentity = tileentity1;
                        break;
                    }
                }
            }

            return tileentity;
        } else {
            return null;
        }
    }

    public void func_147455_a(int i, int j, int k, TileEntity tileentity) {
        if (tileentity != null && !tileentity.func_145837_r()) {
            if (this.field_147481_N) {
                tileentity.field_145851_c = i;
                tileentity.field_145848_d = j;
                tileentity.field_145849_e = k;
                Iterator iterator = this.field_147484_a.iterator();

                while (iterator.hasNext()) {
                    TileEntity tileentity1 = (TileEntity) iterator.next();

                    if (tileentity1.field_145851_c == i && tileentity1.field_145848_d == j && tileentity1.field_145849_e == k) {
                        tileentity1.func_145843_s();
                        iterator.remove();
                    }
                }

                this.field_147484_a.add(tileentity);
            } else {
                this.field_147482_g.add(tileentity);
                Chunk chunk = this.func_72964_e(i >> 4, k >> 4);

                if (chunk != null) {
                    chunk.func_150812_a(i & 15, j, k & 15, tileentity);
                }
            }
        }

    }

    public void func_147475_p(int i, int j, int k) {
        TileEntity tileentity = this.func_147438_o(i, j, k);

        if (tileentity != null && this.field_147481_N) {
            tileentity.func_145843_s();
            this.field_147484_a.remove(tileentity);
        } else {
            if (tileentity != null) {
                this.field_147484_a.remove(tileentity);
                this.field_147482_g.remove(tileentity);
            }

            Chunk chunk = this.func_72964_e(i >> 4, k >> 4);

            if (chunk != null) {
                chunk.func_150805_f(i & 15, j, k & 15);
            }
        }

    }

    public void func_147457_a(TileEntity tileentity) {
        this.field_147483_b.add(tileentity);
    }

    public boolean func_147469_q(int i, int j, int k) {
        AxisAlignedBB axisalignedbb = this.func_147439_a(i, j, k).func_149668_a(this, i, j, k);

        return axisalignedbb != null && axisalignedbb.func_72320_b() >= 1.0D;
    }

    public static boolean func_147466_a(IBlockAccess iblockaccess, int i, int j, int k) {
        Block block = iblockaccess.func_147439_a(i, j, k);
        int l = iblockaccess.func_72805_g(i, j, k);

        return block.func_149688_o().func_76218_k() && block.func_149686_d() ? true : (block instanceof BlockStairs ? (l & 4) == 4 : (block instanceof BlockSlab ? (l & 8) == 8 : (block instanceof BlockHopper ? true : (block instanceof BlockSnow ? (l & 7) == 7 : false))));
    }

    public boolean func_147445_c(int i, int j, int k, boolean flag) {
        if (i >= -30000000 && k >= -30000000 && i < 30000000 && k < 30000000) {
            Chunk chunk = this.field_73020_y.func_73154_d(i >> 4, k >> 4);

            if (chunk != null && !chunk.func_76621_g()) {
                Block block = this.func_147439_a(i, j, k);

                return block.func_149688_o().func_76218_k() && block.func_149686_d();
            } else {
                return flag;
            }
        } else {
            return flag;
        }
    }

    public void func_72966_v() {
        int i = this.func_72967_a(1.0F);

        if (i != this.field_73008_k) {
            this.field_73008_k = i;
        }

    }

    public void func_72891_a(boolean flag, boolean flag1) {
        this.field_72985_G = flag;
        this.field_72992_H = flag1;
    }

    public void func_72835_b() {
        this.func_72979_l();
    }

    private void func_72947_a() {
        if (this.field_72986_A.func_76059_o()) {
            this.field_73004_o = 1.0F;
            if (this.field_72986_A.func_76061_m()) {
                this.field_73017_q = 1.0F;
            }
        }

    }

    protected void func_72979_l() {
        if (!this.field_73011_w.field_76576_e) {
            if (!this.field_72995_K) {
                int i = this.field_72986_A.func_76071_n();

                if (i <= 0) {
                    if (this.field_72986_A.func_76061_m()) {
                        this.field_72986_A.func_76090_f(this.field_73012_v.nextInt(12000) + 3600);
                    } else {
                        this.field_72986_A.func_76090_f(this.field_73012_v.nextInt(168000) + 12000);
                    }
                } else {
                    --i;
                    this.field_72986_A.func_76090_f(i);
                    if (i <= 0) {
                        this.field_72986_A.func_76069_a(!this.field_72986_A.func_76061_m());
                    }
                }

                this.field_73018_p = this.field_73017_q;
                if (this.field_72986_A.func_76061_m()) {
                    this.field_73017_q = (float) ((double) this.field_73017_q + 0.01D);
                } else {
                    this.field_73017_q = (float) ((double) this.field_73017_q - 0.01D);
                }

                this.field_73017_q = MathHelper.func_76131_a(this.field_73017_q, 0.0F, 1.0F);
                int j = this.field_72986_A.func_76083_p();

                if (j <= 0) {
                    if (this.field_72986_A.func_76059_o()) {
                        this.field_72986_A.func_76080_g(this.field_73012_v.nextInt(12000) + 12000);
                    } else {
                        this.field_72986_A.func_76080_g(this.field_73012_v.nextInt(168000) + 12000);
                    }
                } else {
                    --j;
                    this.field_72986_A.func_76080_g(j);
                    if (j <= 0) {
                        this.field_72986_A.func_76084_b(!this.field_72986_A.func_76059_o());
                    }
                }

                this.field_73003_n = this.field_73004_o;
                if (this.field_72986_A.func_76059_o()) {
                    this.field_73004_o = (float) ((double) this.field_73004_o + 0.01D);
                } else {
                    this.field_73004_o = (float) ((double) this.field_73004_o - 0.01D);
                }

                this.field_73004_o = MathHelper.func_76131_a(this.field_73004_o, 0.0F, 1.0F);
            }
        }
    }

    protected void func_72903_x() {
        this.field_72993_I.clear();
        this.field_72984_F.func_76320_a("buildList");

        EntityPlayer entityplayer;
        int i;
        int j;
        int k;
        int l;

        for (i = 0; i < this.field_73010_i.size(); ++i) {
            entityplayer = (EntityPlayer) this.field_73010_i.get(i);
            j = MathHelper.func_76128_c(entityplayer.field_70165_t / 16.0D);
            k = MathHelper.func_76128_c(entityplayer.field_70161_v / 16.0D);
            l = this.func_152379_p();

            for (int i1 = -l; i1 <= l; ++i1) {
                for (int j1 = -l; j1 <= l; ++j1) {
                    this.field_72993_I.add(new ChunkCoordIntPair(i1 + j, j1 + k));
                }
            }
        }

        this.field_72984_F.func_76319_b();
        if (this.field_72990_M > 0) {
            --this.field_72990_M;
        }

        this.field_72984_F.func_76320_a("playerCheckLight");
        if (!this.field_73010_i.isEmpty()) {
            i = this.field_73012_v.nextInt(this.field_73010_i.size());
            entityplayer = (EntityPlayer) this.field_73010_i.get(i);
            j = MathHelper.func_76128_c(entityplayer.field_70165_t) + this.field_73012_v.nextInt(11) - 5;
            k = MathHelper.func_76128_c(entityplayer.field_70163_u) + this.field_73012_v.nextInt(11) - 5;
            l = MathHelper.func_76128_c(entityplayer.field_70161_v) + this.field_73012_v.nextInt(11) - 5;
            this.func_147451_t(j, k, l);
        }

        this.field_72984_F.func_76319_b();
    }

    protected abstract int func_152379_p();

    protected void func_147467_a(int i, int j, Chunk chunk) {
        this.field_72984_F.func_76318_c("moodSound");
        if (this.field_72990_M == 0 && !this.field_72995_K) {
            this.field_73005_l = this.field_73005_l * 3 + 1013904223;
            int k = this.field_73005_l >> 2;
            int l = k & 15;
            int i1 = k >> 8 & 15;
            int j1 = k >> 16 & 255;
            Block block = chunk.func_150810_a(l, j1, i1);

            l += i;
            i1 += j;
            if (block.func_149688_o() == Material.field_151579_a && this.func_72883_k(l, j1, i1) <= this.field_73012_v.nextInt(8) && this.func_72972_b(EnumSkyBlock.Sky, l, j1, i1) <= 0) {
                EntityPlayer entityplayer = this.func_72977_a((double) l + 0.5D, (double) j1 + 0.5D, (double) i1 + 0.5D, 8.0D);

                if (entityplayer != null && entityplayer.func_70092_e((double) l + 0.5D, (double) j1 + 0.5D, (double) i1 + 0.5D) > 4.0D) {
                    this.func_72908_a((double) l + 0.5D, (double) j1 + 0.5D, (double) i1 + 0.5D, "ambient.cave.cave", 0.7F, 0.8F + this.field_73012_v.nextFloat() * 0.2F);
                    this.field_72990_M = this.field_73012_v.nextInt(12000) + 6000;
                }
            }
        }

        this.field_72984_F.func_76318_c("checkLight");
        chunk.func_76594_o();
    }

    protected void func_147456_g() {
        this.func_72903_x();
    }

    public boolean func_72884_u(int i, int j, int k) {
        return this.func_72834_c(i, j, k, false);
    }

    public boolean func_72850_v(int i, int j, int k) {
        return this.func_72834_c(i, j, k, true);
    }

    public boolean func_72834_c(int i, int j, int k, boolean flag) {
        BiomeGenBase biomegenbase = this.func_72807_a(i, k);
        float f = biomegenbase.func_150564_a(i, j, k);

        if (f > 0.15F) {
            return false;
        } else {
            if (j >= 0 && j < 256 && this.func_72972_b(EnumSkyBlock.Block, i, j, k) < 10) {
                Block block = this.func_147439_a(i, j, k);

                if ((block == Blocks.field_150355_j || block == Blocks.field_150358_i) && this.func_72805_g(i, j, k) == 0) {
                    if (!flag) {
                        return true;
                    }

                    boolean flag1 = true;

                    if (flag1 && this.func_147439_a(i - 1, j, k).func_149688_o() != Material.field_151586_h) {
                        flag1 = false;
                    }

                    if (flag1 && this.func_147439_a(i + 1, j, k).func_149688_o() != Material.field_151586_h) {
                        flag1 = false;
                    }

                    if (flag1 && this.func_147439_a(i, j, k - 1).func_149688_o() != Material.field_151586_h) {
                        flag1 = false;
                    }

                    if (flag1 && this.func_147439_a(i, j, k + 1).func_149688_o() != Material.field_151586_h) {
                        flag1 = false;
                    }

                    if (!flag1) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public boolean func_147478_e(int i, int j, int k, boolean flag) {
        BiomeGenBase biomegenbase = this.func_72807_a(i, k);
        float f = biomegenbase.func_150564_a(i, j, k);

        if (f > 0.15F) {
            return false;
        } else if (!flag) {
            return true;
        } else {
            if (j >= 0 && j < 256 && this.func_72972_b(EnumSkyBlock.Block, i, j, k) < 10) {
                Block block = this.func_147439_a(i, j, k);

                if (block.func_149688_o() == Material.field_151579_a && Blocks.field_150431_aC.func_149742_c(this, i, j, k)) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean func_147451_t(int i, int j, int k) {
        boolean flag = false;

        if (!this.field_73011_w.field_76576_e) {
            flag |= this.func_147463_c(EnumSkyBlock.Sky, i, j, k);
        }

        flag |= this.func_147463_c(EnumSkyBlock.Block, i, j, k);
        return flag;
    }

    private int func_98179_a(int i, int j, int k, EnumSkyBlock enumskyblock) {
        if (enumskyblock == EnumSkyBlock.Sky && this.func_72937_j(i, j, k)) {
            return 15;
        } else {
            Block block = this.func_147439_a(i, j, k);
            int l = enumskyblock == EnumSkyBlock.Sky ? 0 : block.func_149750_m();
            int i1 = block.func_149717_k();

            if (i1 >= 15 && block.func_149750_m() > 0) {
                i1 = 1;
            }

            if (i1 < 1) {
                i1 = 1;
            }

            if (i1 >= 15) {
                return 0;
            } else if (l >= 14) {
                return l;
            } else {
                for (int j1 = 0; j1 < 6; ++j1) {
                    int k1 = i + Facing.field_71586_b[j1];
                    int l1 = j + Facing.field_71587_c[j1];
                    int i2 = k + Facing.field_71585_d[j1];
                    int j2 = this.func_72972_b(enumskyblock, k1, l1, i2) - i1;

                    if (j2 > l) {
                        l = j2;
                    }

                    if (l >= 14) {
                        return l;
                    }
                }

                return l;
            }
        }
    }

    public boolean func_147463_c(EnumSkyBlock enumskyblock, int i, int j, int k) {
        if (!this.func_72873_a(i, j, k, 17)) {
            return false;
        } else {
            int l = 0;
            int i1 = 0;

            this.field_72984_F.func_76320_a("getBrightness");
            int j1 = this.func_72972_b(enumskyblock, i, j, k);
            int k1 = this.func_98179_a(i, j, k, enumskyblock);
            int l1;
            int i2;
            int j2;
            int k2;
            int l2;
            int i3;
            int j3;
            int k3;
            int l3;

            if (k1 > j1) {
                this.field_72994_J[i1++] = 133152;
            } else if (k1 < j1) {
                this.field_72994_J[i1++] = 133152 | j1 << 18;

                while (l < i1) {
                    l1 = this.field_72994_J[l++];
                    i2 = (l1 & 63) - 32 + i;
                    j2 = (l1 >> 6 & 63) - 32 + j;
                    k2 = (l1 >> 12 & 63) - 32 + k;
                    l2 = l1 >> 18 & 15;
                    i3 = this.func_72972_b(enumskyblock, i2, j2, k2);
                    if (i3 == l2) {
                        this.func_72915_b(enumskyblock, i2, j2, k2, 0);
                        if (l2 > 0) {
                            j3 = MathHelper.func_76130_a(i2 - i);
                            k3 = MathHelper.func_76130_a(j2 - j);
                            l3 = MathHelper.func_76130_a(k2 - k);
                            if (j3 + k3 + l3 < 17) {
                                for (int i4 = 0; i4 < 6; ++i4) {
                                    int j4 = i2 + Facing.field_71586_b[i4];
                                    int k4 = j2 + Facing.field_71587_c[i4];
                                    int l4 = k2 + Facing.field_71585_d[i4];
                                    int i5 = Math.max(1, this.func_147439_a(j4, k4, l4).func_149717_k());

                                    i3 = this.func_72972_b(enumskyblock, j4, k4, l4);
                                    if (i3 == l2 - i5 && i1 < this.field_72994_J.length) {
                                        this.field_72994_J[i1++] = j4 - i + 32 | k4 - j + 32 << 6 | l4 - k + 32 << 12 | l2 - i5 << 18;
                                    }
                                }
                            }
                        }
                    }
                }

                l = 0;
            }

            this.field_72984_F.func_76319_b();
            this.field_72984_F.func_76320_a("checkedPosition < toCheckCount");

            while (l < i1) {
                l1 = this.field_72994_J[l++];
                i2 = (l1 & 63) - 32 + i;
                j2 = (l1 >> 6 & 63) - 32 + j;
                k2 = (l1 >> 12 & 63) - 32 + k;
                l2 = this.func_72972_b(enumskyblock, i2, j2, k2);
                i3 = this.func_98179_a(i2, j2, k2, enumskyblock);
                if (i3 != l2) {
                    this.func_72915_b(enumskyblock, i2, j2, k2, i3);
                    if (i3 > l2) {
                        j3 = Math.abs(i2 - i);
                        k3 = Math.abs(j2 - j);
                        l3 = Math.abs(k2 - k);
                        boolean flag = i1 < this.field_72994_J.length - 6;

                        if (j3 + k3 + l3 < 17 && flag) {
                            if (this.func_72972_b(enumskyblock, i2 - 1, j2, k2) < i3) {
                                this.field_72994_J[i1++] = i2 - 1 - i + 32 + (j2 - j + 32 << 6) + (k2 - k + 32 << 12);
                            }

                            if (this.func_72972_b(enumskyblock, i2 + 1, j2, k2) < i3) {
                                this.field_72994_J[i1++] = i2 + 1 - i + 32 + (j2 - j + 32 << 6) + (k2 - k + 32 << 12);
                            }

                            if (this.func_72972_b(enumskyblock, i2, j2 - 1, k2) < i3) {
                                this.field_72994_J[i1++] = i2 - i + 32 + (j2 - 1 - j + 32 << 6) + (k2 - k + 32 << 12);
                            }

                            if (this.func_72972_b(enumskyblock, i2, j2 + 1, k2) < i3) {
                                this.field_72994_J[i1++] = i2 - i + 32 + (j2 + 1 - j + 32 << 6) + (k2 - k + 32 << 12);
                            }

                            if (this.func_72972_b(enumskyblock, i2, j2, k2 - 1) < i3) {
                                this.field_72994_J[i1++] = i2 - i + 32 + (j2 - j + 32 << 6) + (k2 - 1 - k + 32 << 12);
                            }

                            if (this.func_72972_b(enumskyblock, i2, j2, k2 + 1) < i3) {
                                this.field_72994_J[i1++] = i2 - i + 32 + (j2 - j + 32 << 6) + (k2 + 1 - k + 32 << 12);
                            }
                        }
                    }
                }
            }

            this.field_72984_F.func_76319_b();
            return true;
        }
    }

    public boolean func_72955_a(boolean flag) {
        return false;
    }

    public List func_72920_a(Chunk chunk, boolean flag) {
        return null;
    }

    public List func_72839_b(Entity entity, AxisAlignedBB axisalignedbb) {
        return this.func_94576_a(entity, axisalignedbb, (IEntitySelector) null);
    }

    public List func_94576_a(Entity entity, AxisAlignedBB axisalignedbb, IEntitySelector ientityselector) {
        ArrayList arraylist = new ArrayList();
        int i = MathHelper.func_76128_c((axisalignedbb.field_72340_a - 2.0D) / 16.0D);
        int j = MathHelper.func_76128_c((axisalignedbb.field_72336_d + 2.0D) / 16.0D);
        int k = MathHelper.func_76128_c((axisalignedbb.field_72339_c - 2.0D) / 16.0D);
        int l = MathHelper.func_76128_c((axisalignedbb.field_72334_f + 2.0D) / 16.0D);

        for (int i1 = i; i1 <= j; ++i1) {
            for (int j1 = k; j1 <= l; ++j1) {
                if (this.func_72916_c(i1, j1)) {
                    this.func_72964_e(i1, j1).func_76588_a(entity, axisalignedbb, arraylist, ientityselector);
                }
            }
        }

        return arraylist;
    }

    public List func_72872_a(Class oclass, AxisAlignedBB axisalignedbb) {
        return this.func_82733_a(oclass, axisalignedbb, (IEntitySelector) null);
    }

    public List func_82733_a(Class oclass, AxisAlignedBB axisalignedbb, IEntitySelector ientityselector) {
        int i = MathHelper.func_76128_c((axisalignedbb.field_72340_a - 2.0D) / 16.0D);
        int j = MathHelper.func_76128_c((axisalignedbb.field_72336_d + 2.0D) / 16.0D);
        int k = MathHelper.func_76128_c((axisalignedbb.field_72339_c - 2.0D) / 16.0D);
        int l = MathHelper.func_76128_c((axisalignedbb.field_72334_f + 2.0D) / 16.0D);
        ArrayList arraylist = new ArrayList();

        for (int i1 = i; i1 <= j; ++i1) {
            for (int j1 = k; j1 <= l; ++j1) {
                if (this.func_72916_c(i1, j1)) {
                    this.func_72964_e(i1, j1).func_76618_a(oclass, axisalignedbb, arraylist, ientityselector);
                }
            }
        }

        return arraylist;
    }

    public Entity func_72857_a(Class oclass, AxisAlignedBB axisalignedbb, Entity entity) {
        List list = this.func_72872_a(oclass, axisalignedbb);
        Entity entity1 = null;
        double d0 = Double.MAX_VALUE;

        for (int i = 0; i < list.size(); ++i) {
            Entity entity2 = (Entity) list.get(i);

            if (entity2 != entity) {
                double d1 = entity.func_70068_e(entity2);

                if (d1 <= d0) {
                    entity1 = entity2;
                    d0 = d1;
                }
            }
        }

        return entity1;
    }

    public abstract Entity func_73045_a(int i);

    public void func_147476_b(int i, int j, int k, TileEntity tileentity) {
        if (this.func_72899_e(i, j, k)) {
            this.func_72938_d(i, k).func_76630_e();
        }

    }

    public int func_72907_a(Class oclass) {
        int i = 0;

        for (int j = 0; j < this.field_72996_f.size(); ++j) {
            Entity entity = (Entity) this.field_72996_f.get(j);

            if ((!(entity instanceof EntityLiving) || !((EntityLiving) entity).func_104002_bU()) && oclass.isAssignableFrom(entity.getClass())) {
                ++i;
            }
        }

        return i;
    }

    public void func_72868_a(List list) {
        this.field_72996_f.addAll(list);

        for (int i = 0; i < list.size(); ++i) {
            this.func_72923_a((Entity) list.get(i));
        }

    }

    public void func_72828_b(List list) {
        this.field_72997_g.addAll(list);
    }

    public boolean func_147472_a(Block block, int i, int j, int k, boolean flag, int l, Entity entity, ItemStack itemstack) {
        Block block1 = this.func_147439_a(i, j, k);
        AxisAlignedBB axisalignedbb = flag ? null : block.func_149668_a(this, i, j, k);

        return axisalignedbb != null && !this.func_72917_a(axisalignedbb, entity) ? false : (block1.func_149688_o() == Material.field_151594_q && block == Blocks.field_150467_bQ ? true : block1.func_149688_o().func_76222_j() && block.func_149705_a(this, i, j, k, l, itemstack));
    }

    public PathEntity func_72865_a(Entity entity, Entity entity1, float f, boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        this.field_72984_F.func_76320_a("pathfind");
        int i = MathHelper.func_76128_c(entity.field_70165_t);
        int j = MathHelper.func_76128_c(entity.field_70163_u + 1.0D);
        int k = MathHelper.func_76128_c(entity.field_70161_v);
        int l = (int) (f + 16.0F);
        int i1 = i - l;
        int j1 = j - l;
        int k1 = k - l;
        int l1 = i + l;
        int i2 = j + l;
        int j2 = k + l;
        ChunkCache chunkcache = new ChunkCache(this, i1, j1, k1, l1, i2, j2, 0);
        PathEntity pathentity = (new PathFinder(chunkcache, flag, flag1, flag2, flag3)).func_75856_a(entity, entity1, f);

        this.field_72984_F.func_76319_b();
        return pathentity;
    }

    public PathEntity func_72844_a(Entity entity, int i, int j, int k, float f, boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        this.field_72984_F.func_76320_a("pathfind");
        int l = MathHelper.func_76128_c(entity.field_70165_t);
        int i1 = MathHelper.func_76128_c(entity.field_70163_u);
        int j1 = MathHelper.func_76128_c(entity.field_70161_v);
        int k1 = (int) (f + 8.0F);
        int l1 = l - k1;
        int i2 = i1 - k1;
        int j2 = j1 - k1;
        int k2 = l + k1;
        int l2 = i1 + k1;
        int i3 = j1 + k1;
        ChunkCache chunkcache = new ChunkCache(this, l1, i2, j2, k2, l2, i3, 0);
        PathEntity pathentity = (new PathFinder(chunkcache, flag, flag1, flag2, flag3)).func_75859_a(entity, i, j, k, f);

        this.field_72984_F.func_76319_b();
        return pathentity;
    }

    @Override
    public int func_72879_k(int i, int j, int k, int l) {
        return this.func_147439_a(i, j, k).func_149748_c(this, i, j, k, l);
    }

    public int func_94577_B(int i, int j, int k) {
        int l = 0;

        l = Math.max(l, this.func_72879_k(i, j - 1, k, 0));
        if (l >= 15) {
            return l;
        } else {
            l = Math.max(l, this.func_72879_k(i, j + 1, k, 1));
            if (l >= 15) {
                return l;
            } else {
                l = Math.max(l, this.func_72879_k(i, j, k - 1, 2));
                if (l >= 15) {
                    return l;
                } else {
                    l = Math.max(l, this.func_72879_k(i, j, k + 1, 3));
                    if (l >= 15) {
                        return l;
                    } else {
                        l = Math.max(l, this.func_72879_k(i - 1, j, k, 4));
                        if (l >= 15) {
                            return l;
                        } else {
                            l = Math.max(l, this.func_72879_k(i + 1, j, k, 5));
                            return l >= 15 ? l : l;
                        }
                    }
                }
            }
        }
    }

    public boolean func_94574_k(int i, int j, int k, int l) {
        return this.func_72878_l(i, j, k, l) > 0;
    }

    public int func_72878_l(int i, int j, int k, int l) {
        return this.func_147439_a(i, j, k).func_149721_r() ? this.func_94577_B(i, j, k) : this.func_147439_a(i, j, k).func_149709_b(this, i, j, k, l);
    }

    public boolean func_72864_z(int i, int j, int k) {
        return this.func_72878_l(i, j - 1, k, 0) > 0 ? true : (this.func_72878_l(i, j + 1, k, 1) > 0 ? true : (this.func_72878_l(i, j, k - 1, 2) > 0 ? true : (this.func_72878_l(i, j, k + 1, 3) > 0 ? true : (this.func_72878_l(i - 1, j, k, 4) > 0 ? true : this.func_72878_l(i + 1, j, k, 5) > 0))));
    }

    public int func_94572_D(int i, int j, int k) {
        int l = 0;

        for (int i1 = 0; i1 < 6; ++i1) {
            int j1 = this.func_72878_l(i + Facing.field_71586_b[i1], j + Facing.field_71587_c[i1], k + Facing.field_71585_d[i1], i1);

            if (j1 >= 15) {
                return 15;
            }

            if (j1 > l) {
                l = j1;
            }
        }

        return l;
    }

    public EntityPlayer func_72890_a(Entity entity, double d0) {
        return this.func_72977_a(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v, d0);
    }

    public EntityPlayer func_72977_a(double d0, double d1, double d2, double d3) {
        double d4 = -1.0D;
        EntityPlayer entityplayer = null;

        for (int i = 0; i < this.field_73010_i.size(); ++i) {
            EntityPlayer entityplayer1 = (EntityPlayer) this.field_73010_i.get(i);
            double d5 = entityplayer1.func_70092_e(d0, d1, d2);

            if ((d3 < 0.0D || d5 < d3 * d3) && (d4 == -1.0D || d5 < d4)) {
                d4 = d5;
                entityplayer = entityplayer1;
            }
        }

        return entityplayer;
    }

    public EntityPlayer func_72856_b(Entity entity, double d0) {
        return this.func_72846_b(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v, d0);
    }

    public EntityPlayer func_72846_b(double d0, double d1, double d2, double d3) {
        double d4 = -1.0D;
        EntityPlayer entityplayer = null;

        for (int i = 0; i < this.field_73010_i.size(); ++i) {
            EntityPlayer entityplayer1 = (EntityPlayer) this.field_73010_i.get(i);

            if (!entityplayer1.field_71075_bZ.field_75102_a && entityplayer1.func_70089_S()) {
                double d5 = entityplayer1.func_70092_e(d0, d1, d2);
                double d6 = d3;

                if (entityplayer1.func_70093_af()) {
                    d6 = d3 * 0.800000011920929D;
                }

                if (entityplayer1.func_82150_aj()) {
                    float f = entityplayer1.func_82243_bO();

                    if (f < 0.1F) {
                        f = 0.1F;
                    }

                    d6 *= (double) (0.7F * f);
                }

                if ((d3 < 0.0D || d5 < d6 * d6) && (d4 == -1.0D || d5 < d4)) {
                    d4 = d5;
                    entityplayer = entityplayer1;
                }
            }
        }

        return entityplayer;
    }

    public EntityPlayer func_72924_a(String s) {
        for (int i = 0; i < this.field_73010_i.size(); ++i) {
            EntityPlayer entityplayer = (EntityPlayer) this.field_73010_i.get(i);

            if (s.equals(entityplayer.func_70005_c_())) {
                return entityplayer;
            }
        }

        return null;
    }

    public EntityPlayer func_152378_a(UUID uuid) {
        for (int i = 0; i < this.field_73010_i.size(); ++i) {
            EntityPlayer entityplayer = (EntityPlayer) this.field_73010_i.get(i);

            if (uuid.equals(entityplayer.func_110124_au())) {
                return entityplayer;
            }
        }

        return null;
    }

    public void func_72906_B() {
        this.field_73019_z.func_75762_c();
    }

    public long func_72905_C() {
        return this.field_72986_A.func_76063_b();
    }

    public long func_82737_E() {
        return this.field_72986_A.func_82573_f();
    }

    public long func_72820_D() {
        return this.field_72986_A.func_76073_f();
    }

    public void func_72877_b(long i) {
        this.field_72986_A.func_76068_b(i);
    }

    public ChunkCoordinates func_72861_E() {
        return new ChunkCoordinates(this.field_72986_A.func_76079_c(), this.field_72986_A.func_76075_d(), this.field_72986_A.func_76074_e());
    }

    public void func_72950_A(int i, int j, int k) {
        this.field_72986_A.func_76081_a(i, j, k);
    }

    public boolean func_72962_a(EntityPlayer entityplayer, int i, int j, int k) {
        return true;
    }

    public void func_72960_a(Entity entity, byte b0) {}

    public IChunkProvider func_72863_F() {
        return this.field_73020_y;
    }

    public void func_147452_c(int i, int j, int k, Block block, int l, int i1) {
        block.func_149696_a(this, i, j, k, l, i1);
    }

    public ISaveHandler func_72860_G() {
        return this.field_73019_z;
    }

    public WorldInfo func_72912_H() {
        return this.field_72986_A;
    }

    public GameRules func_82736_K() {
        return this.field_72986_A.func_82574_x();
    }

    public void func_72854_c() {}

    public float func_72819_i(float f) {
        return (this.field_73018_p + (this.field_73017_q - this.field_73018_p) * f) * this.func_72867_j(f);
    }

    public float func_72867_j(float f) {
        return this.field_73003_n + (this.field_73004_o - this.field_73003_n) * f;
    }

    public boolean func_72911_I() {
        return (double) this.func_72819_i(1.0F) > 0.9D;
    }

    public boolean func_72896_J() {
        return (double) this.func_72867_j(1.0F) > 0.2D;
    }

    public boolean func_72951_B(int i, int j, int k) {
        if (!this.func_72896_J()) {
            return false;
        } else if (!this.func_72937_j(i, j, k)) {
            return false;
        } else if (this.func_72874_g(i, k) > j) {
            return false;
        } else {
            BiomeGenBase biomegenbase = this.func_72807_a(i, k);

            return biomegenbase.func_76746_c() ? false : (this.func_147478_e(i, j, k, false) ? false : biomegenbase.func_76738_d());
        }
    }

    public boolean func_72958_C(int i, int j, int k) {
        BiomeGenBase biomegenbase = this.func_72807_a(i, k);

        return biomegenbase.func_76736_e();
    }

    public void func_72823_a(String s, WorldSavedData worldsaveddata) {
        this.field_72988_C.func_75745_a(s, worldsaveddata);
    }

    public WorldSavedData func_72943_a(Class oclass, String s) {
        return this.field_72988_C.func_75742_a(oclass, s);
    }

    public int func_72841_b(String s) {
        return this.field_72988_C.func_75743_a(s);
    }

    public void func_82739_e(int i, int j, int k, int l, int i1) {
        for (int j1 = 0; j1 < this.field_73021_x.size(); ++j1) {
            ((IWorldAccess) this.field_73021_x.get(j1)).func_82746_a(i, j, k, l, i1);
        }

    }

    public void func_72926_e(int i, int j, int k, int l, int i1) {
        this.func_72889_a((EntityPlayer) null, i, j, k, l, i1);
    }

    public void func_72889_a(EntityPlayer entityplayer, int i, int j, int k, int l, int i1) {
        try {
            for (int j1 = 0; j1 < this.field_73021_x.size(); ++j1) {
                ((IWorldAccess) this.field_73021_x.get(j1)).func_72706_a(entityplayer, i, j, k, l, i1);
            }

        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.func_85055_a(throwable, "Playing level event");
            CrashReportCategory crashreportcategory = crashreport.func_85058_a("Level event being played");

            crashreportcategory.func_71507_a("Block coordinates", CrashReportCategory.func_85071_a(j, k, l));
            crashreportcategory.func_71507_a("Event source", entityplayer);
            crashreportcategory.func_71507_a("Event type", i);
            crashreportcategory.func_71507_a("Event data", i1);
            throw new ReportedException(crashreport);
        }
    }

    public int func_72800_K() {
        return 256;
    }

    public int func_72940_L() {
        return this.field_73011_w.field_76576_e ? 128 : 256;
    }

    public Random func_72843_D(int i, int j, int k) {
        long l = (long) i * 341873128712L + (long) j * 132897987541L + this.func_72912_H().func_76063_b() + (long) k;

        this.field_73012_v.setSeed(l);
        return this.field_73012_v;
    }

    public ChunkPosition func_147440_b(String s, int i, int j, int k) {
        return this.func_72863_F().func_147416_a(this, s, i, j, k);
    }

    public CrashReportCategory func_72914_a(CrashReport crashreport) {
        CrashReportCategory crashreportcategory = crashreport.func_85057_a("Affected level", 1);

        crashreportcategory.func_71507_a("Level name", this.field_72986_A == null ? "????" : this.field_72986_A.func_76065_j());
        crashreportcategory.func_71500_a("All players", new World$3(this));
        crashreportcategory.func_71500_a("Chunk stats", new World$4(this));

        try {
            this.field_72986_A.func_85118_a(crashreportcategory);
        } catch (Throwable throwable) {
            crashreportcategory.func_71499_a("Level Data Unobtainable", throwable);
        }

        return crashreportcategory;
    }

    public void func_147443_d(int i, int j, int k, int l, int i1) {
        for (int j1 = 0; j1 < this.field_73021_x.size(); ++j1) {
            IWorldAccess iworldaccess = (IWorldAccess) this.field_73021_x.get(j1);

            iworldaccess.func_147587_b(i, j, k, l, i1);
        }

    }

    public Calendar func_83015_S() {
        if (this.func_82737_E() % 600L == 0L) {
            this.field_83016_L.setTimeInMillis(MinecraftServer.func_130071_aq());
        }

        return this.field_83016_L;
    }

    public Scoreboard func_96441_U() {
        return this.field_96442_D;
    }

    public void func_147453_f(int i, int j, int k, Block block) {
        for (int l = 0; l < 4; ++l) {
            int i1 = i + Direction.field_71583_a[l];
            int j1 = k + Direction.field_71581_b[l];
            Block block1 = this.func_147439_a(i1, j, j1);

            if (Blocks.field_150441_bU.func_149907_e(block1)) {
                block1.func_149695_a(this, i1, j, j1, block);
            } else if (block1.func_149721_r()) {
                i1 += Direction.field_71583_a[l];
                j1 += Direction.field_71581_b[l];
                Block block2 = this.func_147439_a(i1, j, j1);

                if (Blocks.field_150441_bU.func_149907_e(block2)) {
                    block2.func_149695_a(this, i1, j, j1, block);
                }
            }
        }

    }

    public float func_147462_b(double d0, double d1, double d2) {
        return this.func_147473_B(MathHelper.func_76128_c(d0), MathHelper.func_76128_c(d1), MathHelper.func_76128_c(d2));
    }

    public float func_147473_B(int i, int j, int k) {
        float f = 0.0F;
        boolean flag = this.field_73013_u == EnumDifficulty.HARD;

        if (this.func_72899_e(i, j, k)) {
            float f1 = this.func_130001_d();

            f += MathHelper.func_76131_a((float) this.func_72938_d(i, k).field_111204_q / 3600000.0F, 0.0F, 1.0F) * (flag ? 1.0F : 0.75F);
            f += f1 * 0.25F;
        }

        if (this.field_73013_u == EnumDifficulty.EASY || this.field_73013_u == EnumDifficulty.PEACEFUL) {
            f *= (float) this.field_73013_u.func_151525_a() / 2.0F;
        }

        return MathHelper.func_76131_a(f, 0.0F, flag ? 1.5F : 1.0F);
    }

    public void func_147450_X() {
        Iterator iterator = this.field_73021_x.iterator();

        while (iterator.hasNext()) {
            IWorldAccess iworldaccess = (IWorldAccess) iterator.next();

            iworldaccess.func_147584_b();
        }

    }
}
