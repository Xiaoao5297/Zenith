package net.minecraft.world.chunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk {

    private static final Logger field_150817_t = LogManager.getLogger();
    public static boolean field_76640_a;
    private ExtendedBlockStorage[] field_76652_q;
    private byte[] field_76651_r;
    public int[] field_76638_b;
    public boolean[] field_76639_c;
    public boolean field_76636_d;
    public World field_76637_e;
    public int[] field_76634_f;
    public final int field_76635_g;
    public final int field_76647_h;
    private boolean field_76650_s;
    public Map field_150816_i;
    public List[] field_76645_j;
    public boolean field_76646_k;
    public boolean field_150814_l;
    public boolean field_150815_m;
    public boolean field_76643_l;
    public boolean field_76644_m;
    public long field_76641_n;
    public boolean field_76642_o;
    public int field_82912_p;
    public long field_111204_q;
    private int field_76649_t;

    public Chunk(World world, int i, int j) {
        this.field_76652_q = new ExtendedBlockStorage[16];
        this.field_76651_r = new byte[256];
        this.field_76638_b = new int[256];
        this.field_76639_c = new boolean[256];
        this.field_150816_i = new HashMap();
        this.field_76649_t = 4096;
        this.field_76645_j = new List[16];
        this.field_76637_e = world;
        this.field_76635_g = i;
        this.field_76647_h = j;
        this.field_76634_f = new int[256];

        for (int k = 0; k < this.field_76645_j.length; ++k) {
            this.field_76645_j[k] = new ArrayList();
        }

        Arrays.fill(this.field_76638_b, -999);
        Arrays.fill(this.field_76651_r, (byte) -1);
    }

    public Chunk(World world, Block[] ablock, int i, int j) {
        this(world, i, j);
        int k = ablock.length / 256;
        boolean flag = !world.field_73011_w.field_76576_e;

        for (int l = 0; l < 16; ++l) {
            for (int i1 = 0; i1 < 16; ++i1) {
                for (int j1 = 0; j1 < k; ++j1) {
                    Block block = ablock[l << 11 | i1 << 7 | j1];

                    if (block != null && block.func_149688_o() != Material.field_151579_a) {
                        int k1 = j1 >> 4;

                        if (this.field_76652_q[k1] == null) {
                            this.field_76652_q[k1] = new ExtendedBlockStorage(k1 << 4, flag);
                        }

                        this.field_76652_q[k1].func_150818_a(l, j1 & 15, i1, block);
                    }
                }
            }
        }

    }

    public Chunk(World world, Block[] ablock, byte[] abyte, int i, int j) {
        this(world, i, j);
        int k = ablock.length / 256;
        boolean flag = !world.field_73011_w.field_76576_e;

        for (int l = 0; l < 16; ++l) {
            for (int i1 = 0; i1 < 16; ++i1) {
                for (int j1 = 0; j1 < k; ++j1) {
                    int k1 = l * k * 16 | i1 * k | j1;
                    Block block = ablock[k1];

                    if (block != null && block != Blocks.field_150350_a) {
                        int l1 = j1 >> 4;

                        if (this.field_76652_q[l1] == null) {
                            this.field_76652_q[l1] = new ExtendedBlockStorage(l1 << 4, flag);
                        }

                        this.field_76652_q[l1].func_150818_a(l, j1 & 15, i1, block);
                        this.field_76652_q[l1].func_76654_b(l, j1 & 15, i1, abyte[k1]);
                    }
                }
            }
        }

    }

    public boolean func_76600_a(int i, int j) {
        return i == this.field_76635_g && j == this.field_76647_h;
    }

    public int func_76611_b(int i, int j) {
        return this.field_76634_f[j << 4 | i];
    }

    public int func_76625_h() {
        for (int i = this.field_76652_q.length - 1; i >= 0; --i) {
            if (this.field_76652_q[i] != null) {
                return this.field_76652_q[i].func_76662_d();
            }
        }

        return 0;
    }

    public ExtendedBlockStorage[] func_76587_i() {
        return this.field_76652_q;
    }

    public void func_76603_b() {
        int i = this.func_76625_h();

        this.field_82912_p = Integer.MAX_VALUE;

        for (int j = 0; j < 16; ++j) {
            int k = 0;

            while (k < 16) {
                this.field_76638_b[j + (k << 4)] = -999;
                int l = i + 16 - 1;

                while (true) {
                    if (l > 0) {
                        if (this.func_150808_b(j, l - 1, k) == 0) {
                            --l;
                            continue;
                        }

                        this.field_76634_f[k << 4 | j] = l;
                        if (l < this.field_82912_p) {
                            this.field_82912_p = l;
                        }
                    }

                    if (!this.field_76637_e.field_73011_w.field_76576_e) {
                        l = 15;
                        int i1 = i + 16 - 1;

                        do {
                            int j1 = this.func_150808_b(j, i1, k);

                            if (j1 == 0 && l != 15) {
                                j1 = 1;
                            }

                            l -= j1;
                            if (l > 0) {
                                ExtendedBlockStorage extendedblockstorage = this.field_76652_q[i1 >> 4];

                                if (extendedblockstorage != null) {
                                    extendedblockstorage.func_76657_c(j, i1 & 15, k, l);
                                    this.field_76637_e.func_147479_m((this.field_76635_g << 4) + j, i1, (this.field_76647_h << 4) + k);
                                }
                            }

                            --i1;
                        } while (i1 > 0 && l > 0);
                    }

                    ++k;
                    break;
                }
            }
        }

        this.field_76643_l = true;
    }

    private void func_76595_e(int i, int j) {
        this.field_76639_c[i + j * 16] = true;
        this.field_76650_s = true;
    }

    private void func_150803_c(boolean flag) {
        this.field_76637_e.field_72984_F.func_76320_a("recheckGaps");
        if (this.field_76637_e.func_72873_a(this.field_76635_g * 16 + 8, 0, this.field_76647_h * 16 + 8, 16)) {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (this.field_76639_c[i + j * 16]) {
                        this.field_76639_c[i + j * 16] = false;
                        int k = this.func_76611_b(i, j);
                        int l = this.field_76635_g * 16 + i;
                        int i1 = this.field_76647_h * 16 + j;
                        int j1 = this.field_76637_e.func_82734_g(l - 1, i1);
                        int k1 = this.field_76637_e.func_82734_g(l + 1, i1);
                        int l1 = this.field_76637_e.func_82734_g(l, i1 - 1);
                        int i2 = this.field_76637_e.func_82734_g(l, i1 + 1);

                        if (k1 < j1) {
                            j1 = k1;
                        }

                        if (l1 < j1) {
                            j1 = l1;
                        }

                        if (i2 < j1) {
                            j1 = i2;
                        }

                        this.func_76599_g(l, i1, j1);
                        this.func_76599_g(l - 1, i1, k);
                        this.func_76599_g(l + 1, i1, k);
                        this.func_76599_g(l, i1 - 1, k);
                        this.func_76599_g(l, i1 + 1, k);
                        if (flag) {
                            this.field_76637_e.field_72984_F.func_76319_b();
                            return;
                        }
                    }
                }
            }

            this.field_76650_s = false;
        }

        this.field_76637_e.field_72984_F.func_76319_b();
    }

    private void func_76599_g(int i, int j, int k) {
        int l = this.field_76637_e.func_72976_f(i, j);

        if (l > k) {
            this.func_76609_d(i, j, k, l + 1);
        } else if (l < k) {
            this.func_76609_d(i, j, l, k + 1);
        }

    }

    private void func_76609_d(int i, int j, int k, int l) {
        if (l > k && this.field_76637_e.func_72873_a(i, 0, j, 16)) {
            for (int i1 = k; i1 < l; ++i1) {
                this.field_76637_e.func_147463_c(EnumSkyBlock.Sky, i, i1, j);
            }

            this.field_76643_l = true;
        }

    }

    private void func_76615_h(int i, int j, int k) {
        int l = this.field_76634_f[k << 4 | i] & 255;
        int i1 = l;

        if (j > l) {
            i1 = j;
        }

        while (i1 > 0 && this.func_150808_b(i, i1 - 1, k) == 0) {
            --i1;
        }

        if (i1 != l) {
            this.field_76637_e.func_72975_g(i + this.field_76635_g * 16, k + this.field_76647_h * 16, i1, l);
            this.field_76634_f[k << 4 | i] = i1;
            int j1 = this.field_76635_g * 16 + i;
            int k1 = this.field_76647_h * 16 + k;
            int l1;
            int i2;

            if (!this.field_76637_e.field_73011_w.field_76576_e) {
                ExtendedBlockStorage extendedblockstorage;

                if (i1 < l) {
                    for (l1 = i1; l1 < l; ++l1) {
                        extendedblockstorage = this.field_76652_q[l1 >> 4];
                        if (extendedblockstorage != null) {
                            extendedblockstorage.func_76657_c(i, l1 & 15, k, 15);
                            this.field_76637_e.func_147479_m((this.field_76635_g << 4) + i, l1, (this.field_76647_h << 4) + k);
                        }
                    }
                } else {
                    for (l1 = l; l1 < i1; ++l1) {
                        extendedblockstorage = this.field_76652_q[l1 >> 4];
                        if (extendedblockstorage != null) {
                            extendedblockstorage.func_76657_c(i, l1 & 15, k, 0);
                            this.field_76637_e.func_147479_m((this.field_76635_g << 4) + i, l1, (this.field_76647_h << 4) + k);
                        }
                    }
                }

                l1 = 15;

                while (i1 > 0 && l1 > 0) {
                    --i1;
                    i2 = this.func_150808_b(i, i1, k);
                    if (i2 == 0) {
                        i2 = 1;
                    }

                    l1 -= i2;
                    if (l1 < 0) {
                        l1 = 0;
                    }

                    ExtendedBlockStorage extendedblockstorage1 = this.field_76652_q[i1 >> 4];

                    if (extendedblockstorage1 != null) {
                        extendedblockstorage1.func_76657_c(i, i1 & 15, k, l1);
                    }
                }
            }

            l1 = this.field_76634_f[k << 4 | i];
            i2 = l;
            int j2 = l1;

            if (l1 < l) {
                i2 = l1;
                j2 = l;
            }

            if (l1 < this.field_82912_p) {
                this.field_82912_p = l1;
            }

            if (!this.field_76637_e.field_73011_w.field_76576_e) {
                this.func_76609_d(j1 - 1, k1, i2, j2);
                this.func_76609_d(j1 + 1, k1, i2, j2);
                this.func_76609_d(j1, k1 - 1, i2, j2);
                this.func_76609_d(j1, k1 + 1, i2, j2);
                this.func_76609_d(j1, k1, i2, j2);
            }

            this.field_76643_l = true;
        }
    }

    public int func_150808_b(int i, int j, int k) {
        return this.func_150810_a(i, j, k).func_149717_k();
    }

    public Block func_150810_a(int i, int j, int k) {
        Block block = Blocks.field_150350_a;

        if (j >> 4 < this.field_76652_q.length) {
            ExtendedBlockStorage extendedblockstorage = this.field_76652_q[j >> 4];

            if (extendedblockstorage != null) {
                try {
                    block = extendedblockstorage.func_150819_a(i, j & 15, k);
                } catch (Throwable throwable) {
                    CrashReport crashreport = CrashReport.func_85055_a(throwable, "Getting block");
                    CrashReportCategory crashreportcategory = crashreport.func_85058_a("Block being got");

                    crashreportcategory.func_71500_a("Location", new Chunk$1(this, i, j, k));
                    throw new ReportedException(crashreport);
                }
            }
        }

        return block;
    }

    public int func_76628_c(int i, int j, int k) {
        if (j >> 4 >= this.field_76652_q.length) {
            return 0;
        } else {
            ExtendedBlockStorage extendedblockstorage = this.field_76652_q[j >> 4];

            return extendedblockstorage != null ? extendedblockstorage.func_76665_b(i, j & 15, k) : 0;
        }
    }

    public boolean func_150807_a(int i, int j, int k, Block block, int l) {
        int i1 = k << 4 | i;

        if (j >= this.field_76638_b[i1] - 1) {
            this.field_76638_b[i1] = -999;
        }

        int j1 = this.field_76634_f[i1];
        Block block1 = this.func_150810_a(i, j, k);
        int k1 = this.func_76628_c(i, j, k);

        if (block1 == block && k1 == l) {
            return false;
        } else {
            ExtendedBlockStorage extendedblockstorage = this.field_76652_q[j >> 4];
            boolean flag = false;

            if (extendedblockstorage == null) {
                if (block == Blocks.field_150350_a) {
                    return false;
                }

                extendedblockstorage = this.field_76652_q[j >> 4] = new ExtendedBlockStorage(j >> 4 << 4, !this.field_76637_e.field_73011_w.field_76576_e);
                flag = j >= j1;
            }

            int l1 = this.field_76635_g * 16 + i;
            int i2 = this.field_76647_h * 16 + k;

            if (!this.field_76637_e.field_72995_K) {
                block1.func_149725_f(this.field_76637_e, l1, j, i2, k1);
            }

            extendedblockstorage.func_150818_a(i, j & 15, k, block);
            if (!this.field_76637_e.field_72995_K) {
                block1.func_149749_a(this.field_76637_e, l1, j, i2, block1, k1);
            } else if (block1 instanceof ITileEntityProvider && block1 != block) {
                this.field_76637_e.func_147475_p(l1, j, i2);
            }

            if (extendedblockstorage.func_150819_a(i, j & 15, k) != block) {
                return false;
            } else {
                extendedblockstorage.func_76654_b(i, j & 15, k, l);
                if (flag) {
                    this.func_76603_b();
                } else {
                    int j2 = block.func_149717_k();
                    int k2 = block1.func_149717_k();

                    if (j2 > 0) {
                        if (j >= j1) {
                            this.func_76615_h(i, j + 1, k);
                        }
                    } else if (j == j1 - 1) {
                        this.func_76615_h(i, j, k);
                    }

                    if (j2 != k2 && (j2 < k2 || this.func_76614_a(EnumSkyBlock.Sky, i, j, k) > 0 || this.func_76614_a(EnumSkyBlock.Block, i, j, k) > 0)) {
                        this.func_76595_e(i, k);
                    }
                }

                TileEntity tileentity;

                if (block1 instanceof ITileEntityProvider) {
                    tileentity = this.func_150806_e(i, j, k);
                    if (tileentity != null) {
                        tileentity.func_145836_u();
                    }
                }

                if (!this.field_76637_e.field_72995_K) {
                    block.func_149726_b(this.field_76637_e, l1, j, i2);
                }

                if (block instanceof ITileEntityProvider) {
                    tileentity = this.func_150806_e(i, j, k);
                    if (tileentity == null) {
                        tileentity = ((ITileEntityProvider) block).func_149915_a(this.field_76637_e, l);
                        this.field_76637_e.func_147455_a(l1, j, i2, tileentity);
                    }

                    if (tileentity != null) {
                        tileentity.func_145836_u();
                    }
                }

                this.field_76643_l = true;
                return true;
            }
        }
    }

    public boolean func_76589_b(int i, int j, int k, int l) {
        ExtendedBlockStorage extendedblockstorage = this.field_76652_q[j >> 4];

        if (extendedblockstorage == null) {
            return false;
        } else {
            int i1 = extendedblockstorage.func_76665_b(i, j & 15, k);

            if (i1 == l) {
                return false;
            } else {
                this.field_76643_l = true;
                extendedblockstorage.func_76654_b(i, j & 15, k, l);
                if (extendedblockstorage.func_150819_a(i, j & 15, k) instanceof ITileEntityProvider) {
                    TileEntity tileentity = this.func_150806_e(i, j, k);

                    if (tileentity != null) {
                        tileentity.func_145836_u();
                        tileentity.field_145847_g = l;
                    }
                }

                return true;
            }
        }
    }

    public int func_76614_a(EnumSkyBlock enumskyblock, int i, int j, int k) {
        ExtendedBlockStorage extendedblockstorage = this.field_76652_q[j >> 4];

        return extendedblockstorage == null ? (this.func_76619_d(i, j, k) ? enumskyblock.field_77198_c : 0) : (enumskyblock == EnumSkyBlock.Sky ? (this.field_76637_e.field_73011_w.field_76576_e ? 0 : extendedblockstorage.func_76670_c(i, j & 15, k)) : (enumskyblock == EnumSkyBlock.Block ? extendedblockstorage.func_76674_d(i, j & 15, k) : enumskyblock.field_77198_c));
    }

    public void func_76633_a(EnumSkyBlock enumskyblock, int i, int j, int k, int l) {
        ExtendedBlockStorage extendedblockstorage = this.field_76652_q[j >> 4];

        if (extendedblockstorage == null) {
            extendedblockstorage = this.field_76652_q[j >> 4] = new ExtendedBlockStorage(j >> 4 << 4, !this.field_76637_e.field_73011_w.field_76576_e);
            this.func_76603_b();
        }

        this.field_76643_l = true;
        if (enumskyblock == EnumSkyBlock.Sky) {
            if (!this.field_76637_e.field_73011_w.field_76576_e) {
                extendedblockstorage.func_76657_c(i, j & 15, k, l);
            }
        } else if (enumskyblock == EnumSkyBlock.Block) {
            extendedblockstorage.func_76677_d(i, j & 15, k, l);
        }

    }

    public int func_76629_c(int i, int j, int k, int l) {
        ExtendedBlockStorage extendedblockstorage = this.field_76652_q[j >> 4];

        if (extendedblockstorage == null) {
            return !this.field_76637_e.field_73011_w.field_76576_e && l < EnumSkyBlock.Sky.field_77198_c ? EnumSkyBlock.Sky.field_77198_c - l : 0;
        } else {
            int i1 = this.field_76637_e.field_73011_w.field_76576_e ? 0 : extendedblockstorage.func_76670_c(i, j & 15, k);

            if (i1 > 0) {
                Chunk.field_76640_a = true;
            }

            i1 -= l;
            int j1 = extendedblockstorage.func_76674_d(i, j & 15, k);

            if (j1 > i1) {
                i1 = j1;
            }

            return i1;
        }
    }

    public void func_76612_a(Entity entity) {
        this.field_76644_m = true;
        int i = MathHelper.func_76128_c(entity.field_70165_t / 16.0D);
        int j = MathHelper.func_76128_c(entity.field_70161_v / 16.0D);

        if (i != this.field_76635_g || j != this.field_76647_h) {
            Chunk.field_150817_t.warn("Wrong location! " + entity + " (at " + i + ", " + j + " instead of " + this.field_76635_g + ", " + this.field_76647_h + ")");
            Thread.dumpStack();
        }

        int k = MathHelper.func_76128_c(entity.field_70163_u / 16.0D);

        if (k < 0) {
            k = 0;
        }

        if (k >= this.field_76645_j.length) {
            k = this.field_76645_j.length - 1;
        }

        entity.field_70175_ag = true;
        entity.field_70176_ah = this.field_76635_g;
        entity.field_70162_ai = k;
        entity.field_70164_aj = this.field_76647_h;
        this.field_76645_j[k].add(entity);
    }

    public void func_76622_b(Entity entity) {
        this.func_76608_a(entity, entity.field_70162_ai);
    }

    public void func_76608_a(Entity entity, int i) {
        if (i < 0) {
            i = 0;
        }

        if (i >= this.field_76645_j.length) {
            i = this.field_76645_j.length - 1;
        }

        this.field_76645_j[i].remove(entity);
    }

    public boolean func_76619_d(int i, int j, int k) {
        return j >= this.field_76634_f[k << 4 | i];
    }

    public TileEntity func_150806_e(int i, int j, int k) {
        ChunkPosition chunkposition = new ChunkPosition(i, j, k);
        TileEntity tileentity = (TileEntity) this.field_150816_i.get(chunkposition);

        if (tileentity == null) {
            Block block = this.func_150810_a(i, j, k);

            if (!block.func_149716_u()) {
                return null;
            }

            tileentity = ((ITileEntityProvider) block).func_149915_a(this.field_76637_e, this.func_76628_c(i, j, k));
            this.field_76637_e.func_147455_a(this.field_76635_g * 16 + i, j, this.field_76647_h * 16 + k, tileentity);
        }

        if (tileentity != null && tileentity.func_145837_r()) {
            this.field_150816_i.remove(chunkposition);
            return null;
        } else {
            return tileentity;
        }
    }

    public void func_150813_a(TileEntity tileentity) {
        int i = tileentity.field_145851_c - this.field_76635_g * 16;
        int j = tileentity.field_145848_d;
        int k = tileentity.field_145849_e - this.field_76647_h * 16;

        this.func_150812_a(i, j, k, tileentity);
        if (this.field_76636_d) {
            this.field_76637_e.field_147482_g.add(tileentity);
        }

    }

    public void func_150812_a(int i, int j, int k, TileEntity tileentity) {
        ChunkPosition chunkposition = new ChunkPosition(i, j, k);

        tileentity.func_145834_a(this.field_76637_e);
        tileentity.field_145851_c = this.field_76635_g * 16 + i;
        tileentity.field_145848_d = j;
        tileentity.field_145849_e = this.field_76647_h * 16 + k;
        if (this.func_150810_a(i, j, k) instanceof ITileEntityProvider) {
            if (this.field_150816_i.containsKey(chunkposition)) {
                ((TileEntity) this.field_150816_i.get(chunkposition)).func_145843_s();
            }

            tileentity.func_145829_t();
            this.field_150816_i.put(chunkposition, tileentity);
        }
    }

    public void func_150805_f(int i, int j, int k) {
        ChunkPosition chunkposition = new ChunkPosition(i, j, k);

        if (this.field_76636_d) {
            TileEntity tileentity = (TileEntity) this.field_150816_i.remove(chunkposition);

            if (tileentity != null) {
                tileentity.func_145843_s();
            }
        }

    }

    public void func_76631_c() {
        this.field_76636_d = true;
        this.field_76637_e.func_147448_a(this.field_150816_i.values());

        for (int i = 0; i < this.field_76645_j.length; ++i) {
            Iterator iterator = this.field_76645_j[i].iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                entity.func_110123_P();
            }

            this.field_76637_e.func_72868_a(this.field_76645_j[i]);
        }

    }

    public void func_76623_d() {
        this.field_76636_d = false;
        Iterator iterator = this.field_150816_i.values().iterator();

        while (iterator.hasNext()) {
            TileEntity tileentity = (TileEntity) iterator.next();

            this.field_76637_e.func_147457_a(tileentity);
        }

        for (int i = 0; i < this.field_76645_j.length; ++i) {
            this.field_76637_e.func_72828_b(this.field_76645_j[i]);
        }

    }

    public void func_76630_e() {
        this.field_76643_l = true;
    }

    public void func_76588_a(Entity entity, AxisAlignedBB axisalignedbb, List list, IEntitySelector ientityselector) {
        int i = MathHelper.func_76128_c((axisalignedbb.field_72338_b - 2.0D) / 16.0D);
        int j = MathHelper.func_76128_c((axisalignedbb.field_72337_e + 2.0D) / 16.0D);

        i = MathHelper.func_76125_a(i, 0, this.field_76645_j.length - 1);
        j = MathHelper.func_76125_a(j, 0, this.field_76645_j.length - 1);

        for (int k = i; k <= j; ++k) {
            List list1 = this.field_76645_j[k];

            for (int l = 0; l < list1.size(); ++l) {
                Entity entity1 = (Entity) list1.get(l);

                if (entity1 != entity && entity1.field_70121_D.func_72326_a(axisalignedbb) && (ientityselector == null || ientityselector.func_82704_a(entity1))) {
                    list.add(entity1);
                    Entity[] aentity = entity1.func_70021_al();

                    if (aentity != null) {
                        for (int i1 = 0; i1 < aentity.length; ++i1) {
                            entity1 = aentity[i1];
                            if (entity1 != entity && entity1.field_70121_D.func_72326_a(axisalignedbb) && (ientityselector == null || ientityselector.func_82704_a(entity1))) {
                                list.add(entity1);
                            }
                        }
                    }
                }
            }
        }

    }

    public void func_76618_a(Class oclass, AxisAlignedBB axisalignedbb, List list, IEntitySelector ientityselector) {
        int i = MathHelper.func_76128_c((axisalignedbb.field_72338_b - 2.0D) / 16.0D);
        int j = MathHelper.func_76128_c((axisalignedbb.field_72337_e + 2.0D) / 16.0D);

        i = MathHelper.func_76125_a(i, 0, this.field_76645_j.length - 1);
        j = MathHelper.func_76125_a(j, 0, this.field_76645_j.length - 1);

        for (int k = i; k <= j; ++k) {
            List list1 = this.field_76645_j[k];

            for (int l = 0; l < list1.size(); ++l) {
                Entity entity = (Entity) list1.get(l);

                if (oclass.isAssignableFrom(entity.getClass()) && entity.field_70121_D.func_72326_a(axisalignedbb) && (ientityselector == null || ientityselector.func_82704_a(entity))) {
                    list.add(entity);
                }
            }
        }

    }

    public boolean func_76601_a(boolean flag) {
        if (flag) {
            if (this.field_76644_m && this.field_76637_e.func_82737_E() != this.field_76641_n || this.field_76643_l) {
                return true;
            }
        } else if (this.field_76644_m && this.field_76637_e.func_82737_E() >= this.field_76641_n + 600L) {
            return true;
        }

        return this.field_76643_l;
    }

    public Random func_76617_a(long i) {
        return new Random(this.field_76637_e.func_72905_C() + (long) (this.field_76635_g * this.field_76635_g * 4987142) + (long) (this.field_76635_g * 5947611) + (long) (this.field_76647_h * this.field_76647_h) * 4392871L + (long) (this.field_76647_h * 389711) ^ i);
    }

    public boolean func_76621_g() {
        return false;
    }

    public void func_76624_a(IChunkProvider ichunkprovider, IChunkProvider ichunkprovider1, int i, int j) {
        if (!this.field_76646_k && ichunkprovider.func_73149_a(i + 1, j + 1) && ichunkprovider.func_73149_a(i, j + 1) && ichunkprovider.func_73149_a(i + 1, j)) {
            ichunkprovider.func_73153_a(ichunkprovider1, i, j);
        }

        if (ichunkprovider.func_73149_a(i - 1, j) && !ichunkprovider.func_73154_d(i - 1, j).field_76646_k && ichunkprovider.func_73149_a(i - 1, j + 1) && ichunkprovider.func_73149_a(i, j + 1) && ichunkprovider.func_73149_a(i - 1, j + 1)) {
            ichunkprovider.func_73153_a(ichunkprovider1, i - 1, j);
        }

        if (ichunkprovider.func_73149_a(i, j - 1) && !ichunkprovider.func_73154_d(i, j - 1).field_76646_k && ichunkprovider.func_73149_a(i + 1, j - 1) && ichunkprovider.func_73149_a(i + 1, j - 1) && ichunkprovider.func_73149_a(i + 1, j)) {
            ichunkprovider.func_73153_a(ichunkprovider1, i, j - 1);
        }

        if (ichunkprovider.func_73149_a(i - 1, j - 1) && !ichunkprovider.func_73154_d(i - 1, j - 1).field_76646_k && ichunkprovider.func_73149_a(i, j - 1) && ichunkprovider.func_73149_a(i - 1, j)) {
            ichunkprovider.func_73153_a(ichunkprovider1, i - 1, j - 1);
        }

    }

    public int func_76626_d(int i, int j) {
        int k = i | j << 4;
        int l = this.field_76638_b[k];

        if (l == -999) {
            int i1 = this.func_76625_h() + 15;

            l = -1;

            while (i1 > 0 && l == -1) {
                Block block = this.func_150810_a(i, i1, j);
                Material material = block.func_149688_o();

                if (!material.func_76230_c() && !material.func_76224_d()) {
                    --i1;
                } else {
                    l = i1 + 1;
                }
            }

            this.field_76638_b[k] = l;
        }

        return l;
    }

    public void func_150804_b(boolean flag) {
        if (this.field_76650_s && !this.field_76637_e.field_73011_w.field_76576_e && !flag) {
            this.func_150803_c(this.field_76637_e.field_72995_K);
        }

        this.field_150815_m = true;
        if (!this.field_150814_l && this.field_76646_k) {
            this.func_150809_p();
        }

    }

    public boolean func_150802_k() {
        return this.field_150815_m && this.field_76646_k && this.field_150814_l;
    }

    public ChunkCoordIntPair func_76632_l() {
        return new ChunkCoordIntPair(this.field_76635_g, this.field_76647_h);
    }

    public boolean func_76606_c(int i, int j) {
        if (i < 0) {
            i = 0;
        }

        if (j >= 256) {
            j = 255;
        }

        for (int k = i; k <= j; k += 16) {
            ExtendedBlockStorage extendedblockstorage = this.field_76652_q[k >> 4];

            if (extendedblockstorage != null && !extendedblockstorage.func_76663_a()) {
                return false;
            }
        }

        return true;
    }

    public void func_76602_a(ExtendedBlockStorage[] aextendedblockstorage) {
        this.field_76652_q = aextendedblockstorage;
    }

    public BiomeGenBase func_76591_a(int i, int j, WorldChunkManager worldchunkmanager) {
        int k = this.field_76651_r[j << 4 | i] & 255;

        if (k == 255) {
            BiomeGenBase biomegenbase = worldchunkmanager.func_76935_a((this.field_76635_g << 4) + i, (this.field_76647_h << 4) + j);

            k = biomegenbase.field_76756_M;
            this.field_76651_r[j << 4 | i] = (byte) (k & 255);
        }

        return BiomeGenBase.func_150568_d(k) == null ? BiomeGenBase.field_76772_c : BiomeGenBase.func_150568_d(k);
    }

    public byte[] func_76605_m() {
        return this.field_76651_r;
    }

    public void func_76616_a(byte[] abyte) {
        this.field_76651_r = abyte;
    }

    public void func_76613_n() {
        this.field_76649_t = 0;
    }

    public void func_76594_o() {
        for (int i = 0; i < 8; ++i) {
            if (this.field_76649_t >= 4096) {
                return;
            }

            int j = this.field_76649_t % 16;
            int k = this.field_76649_t / 16 % 16;
            int l = this.field_76649_t / 256;

            ++this.field_76649_t;
            int i1 = (this.field_76635_g << 4) + k;
            int j1 = (this.field_76647_h << 4) + l;

            for (int k1 = 0; k1 < 16; ++k1) {
                int l1 = (j << 4) + k1;

                if (this.field_76652_q[j] == null && (k1 == 0 || k1 == 15 || k == 0 || k == 15 || l == 0 || l == 15) || this.field_76652_q[j] != null && this.field_76652_q[j].func_150819_a(k, k1, l).func_149688_o() == Material.field_151579_a) {
                    if (this.field_76637_e.func_147439_a(i1, l1 - 1, j1).func_149750_m() > 0) {
                        this.field_76637_e.func_147451_t(i1, l1 - 1, j1);
                    }

                    if (this.field_76637_e.func_147439_a(i1, l1 + 1, j1).func_149750_m() > 0) {
                        this.field_76637_e.func_147451_t(i1, l1 + 1, j1);
                    }

                    if (this.field_76637_e.func_147439_a(i1 - 1, l1, j1).func_149750_m() > 0) {
                        this.field_76637_e.func_147451_t(i1 - 1, l1, j1);
                    }

                    if (this.field_76637_e.func_147439_a(i1 + 1, l1, j1).func_149750_m() > 0) {
                        this.field_76637_e.func_147451_t(i1 + 1, l1, j1);
                    }

                    if (this.field_76637_e.func_147439_a(i1, l1, j1 - 1).func_149750_m() > 0) {
                        this.field_76637_e.func_147451_t(i1, l1, j1 - 1);
                    }

                    if (this.field_76637_e.func_147439_a(i1, l1, j1 + 1).func_149750_m() > 0) {
                        this.field_76637_e.func_147451_t(i1, l1, j1 + 1);
                    }

                    this.field_76637_e.func_147451_t(i1, l1, j1);
                }
            }
        }

    }

    public void func_150809_p() {
        this.field_76646_k = true;
        this.field_150814_l = true;
        if (!this.field_76637_e.field_73011_w.field_76576_e) {
            if (this.field_76637_e.func_72904_c(this.field_76635_g * 16 - 1, 0, this.field_76647_h * 16 - 1, this.field_76635_g * 16 + 1, 63, this.field_76647_h * 16 + 1)) {
                for (int i = 0; i < 16; ++i) {
                    for (int j = 0; j < 16; ++j) {
                        if (!this.func_150811_f(i, j)) {
                            this.field_150814_l = false;
                            break;
                        }
                    }
                }

                if (this.field_150814_l) {
                    Chunk chunk = this.field_76637_e.func_72938_d(this.field_76635_g * 16 - 1, this.field_76647_h * 16);

                    chunk.func_150801_a(3);
                    chunk = this.field_76637_e.func_72938_d(this.field_76635_g * 16 + 16, this.field_76647_h * 16);
                    chunk.func_150801_a(1);
                    chunk = this.field_76637_e.func_72938_d(this.field_76635_g * 16, this.field_76647_h * 16 - 1);
                    chunk.func_150801_a(0);
                    chunk = this.field_76637_e.func_72938_d(this.field_76635_g * 16, this.field_76647_h * 16 + 16);
                    chunk.func_150801_a(2);
                }
            } else {
                this.field_150814_l = false;
            }
        }

    }

    private void func_150801_a(int i) {
        if (this.field_76646_k) {
            int j;

            if (i == 3) {
                for (j = 0; j < 16; ++j) {
                    this.func_150811_f(15, j);
                }
            } else if (i == 1) {
                for (j = 0; j < 16; ++j) {
                    this.func_150811_f(0, j);
                }
            } else if (i == 0) {
                for (j = 0; j < 16; ++j) {
                    this.func_150811_f(j, 15);
                }
            } else if (i == 2) {
                for (j = 0; j < 16; ++j) {
                    this.func_150811_f(j, 0);
                }
            }

        }
    }

    private boolean func_150811_f(int i, int j) {
        int k = this.func_76625_h();
        boolean flag = false;
        boolean flag1 = false;

        int l;

        for (l = k + 16 - 1; l > 63 || l > 0 && !flag1; --l) {
            int i1 = this.func_150808_b(i, l, j);

            if (i1 == 255 && l < 63) {
                flag1 = true;
            }

            if (!flag && i1 > 0) {
                flag = true;
            } else if (flag && i1 == 0 && !this.field_76637_e.func_147451_t(this.field_76635_g * 16 + i, l, this.field_76647_h * 16 + j)) {
                return false;
            }
        }

        for (; l > 0; --l) {
            if (this.func_150810_a(i, l, j).func_149750_m() > 0) {
                this.field_76637_e.func_147451_t(this.field_76635_g * 16 + i, l, this.field_76647_h * 16 + j);
            }
        }

        return true;
    }
}
