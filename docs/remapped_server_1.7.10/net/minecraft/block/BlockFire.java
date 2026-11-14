package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;

public class BlockFire extends Block {

    private int[] field_149849_a = new int[256];
    private int[] field_149848_b = new int[256];

    protected BlockFire() {
        super(Material.field_151581_o);
        this.func_149675_a(true);
    }

    public static void func_149843_e() {
        Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150344_f), 5, 20);
        Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150373_bw), 5, 20);
        Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150376_bx), 5, 20);
        Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150422_aJ), 5, 20);
        Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150476_ad), 5, 20);
        Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150487_bG), 5, 20);
        Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150485_bF), 5, 20);
        Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150481_bH), 5, 20);
        Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150364_r), 5, 5);
        Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150363_s), 5, 5);
        Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150362_t), 30, 60);
        Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150361_u), 30, 60);
        Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150342_X), 30, 20);
        Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150335_W), 15, 100);
        Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150329_H), 60, 100);
        Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150398_cm), 60, 100);
        Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150327_N), 60, 100);
        Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150328_O), 60, 100);
        Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150325_L), 30, 60);
        Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150395_bd), 15, 100);
        Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150402_ci), 5, 5);
        Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150407_cf), 60, 20);
        Blocks.field_150480_ab.func_149842_a(func_149682_b(Blocks.field_150404_cg), 60, 20);
    }

    public void func_149842_a(int i, int j, int k) {
        this.field_149849_a[i] = j;
        this.field_149848_b[i] = k;
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        return null;
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    @Override
    public int func_149645_b() {
        return 3;
    }

    @Override
    public int func_149745_a(Random random) {
        return 0;
    }

    @Override
    public int func_149738_a(World world) {
        return 30;
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        if (world.func_82736_K().func_82766_b("doFireTick")) {
            boolean flag = world.func_147439_a(i, j - 1, k) == Blocks.field_150424_aL;

            if (world.field_73011_w instanceof WorldProviderEnd && world.func_147439_a(i, j - 1, k) == Blocks.field_150357_h) {
                flag = true;
            }

            if (!this.func_149742_c(world, i, j, k)) {
                world.func_147468_f(i, j, k);
            }

            if (!flag && world.func_72896_J() && (world.func_72951_B(i, j, k) || world.func_72951_B(i - 1, j, k) || world.func_72951_B(i + 1, j, k) || world.func_72951_B(i, j, k - 1) || world.func_72951_B(i, j, k + 1))) {
                world.func_147468_f(i, j, k);
            } else {
                int l = world.func_72805_g(i, j, k);

                if (l < 15) {
                    world.func_72921_c(i, j, k, l + random.nextInt(3) / 2, 4);
                }

                world.func_147464_a(i, j, k, this, this.func_149738_a(world) + random.nextInt(10));
                if (!flag && !this.func_149847_e(world, i, j, k)) {
                    if (!World.func_147466_a(world, i, j - 1, k) || l > 3) {
                        world.func_147468_f(i, j, k);
                    }

                } else if (!flag && !this.func_149844_e(world, i, j - 1, k) && l == 15 && random.nextInt(4) == 0) {
                    world.func_147468_f(i, j, k);
                } else {
                    boolean flag1 = world.func_72958_C(i, j, k);
                    byte b0 = 0;

                    if (flag1) {
                        b0 = -50;
                    }

                    this.func_149841_a(world, i + 1, j, k, 300 + b0, random, l);
                    this.func_149841_a(world, i - 1, j, k, 300 + b0, random, l);
                    this.func_149841_a(world, i, j - 1, k, 250 + b0, random, l);
                    this.func_149841_a(world, i, j + 1, k, 250 + b0, random, l);
                    this.func_149841_a(world, i, j, k - 1, 300 + b0, random, l);
                    this.func_149841_a(world, i, j, k + 1, 300 + b0, random, l);

                    for (int i1 = i - 1; i1 <= i + 1; ++i1) {
                        for (int j1 = k - 1; j1 <= k + 1; ++j1) {
                            for (int k1 = j - 1; k1 <= j + 4; ++k1) {
                                if (i1 != i || k1 != j || j1 != k) {
                                    int l1 = 100;

                                    if (k1 > j + 1) {
                                        l1 += (k1 - (j + 1)) * 100;
                                    }

                                    int i2 = this.func_149845_m(world, i1, k1, j1);

                                    if (i2 > 0) {
                                        int j2 = (i2 + 40 + world.field_73013_u.func_151525_a() * 7) / (l + 30);

                                        if (flag1) {
                                            j2 /= 2;
                                        }

                                        if (j2 > 0 && random.nextInt(l1) <= j2 && (!world.func_72896_J() || !world.func_72951_B(i1, k1, j1)) && !world.func_72951_B(i1 - 1, k1, k) && !world.func_72951_B(i1 + 1, k1, j1) && !world.func_72951_B(i1, k1, j1 - 1) && !world.func_72951_B(i1, k1, j1 + 1)) {
                                            int k2 = l + random.nextInt(5) / 4;

                                            if (k2 > 15) {
                                                k2 = 15;
                                            }

                                            world.func_147465_d(i1, k1, j1, this, k2, 3);
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    @Override
    public boolean func_149698_L() {
        return false;
    }

    private void func_149841_a(World world, int i, int j, int k, int l, Random random, int i1) {
        int j1 = this.field_149848_b[Block.func_149682_b(world.func_147439_a(i, j, k))];

        if (random.nextInt(l) < j1) {
            boolean flag = world.func_147439_a(i, j, k) == Blocks.field_150335_W;

            if (random.nextInt(i1 + 10) < 5 && !world.func_72951_B(i, j, k)) {
                int k1 = i1 + random.nextInt(5) / 4;

                if (k1 > 15) {
                    k1 = 15;
                }

                world.func_147465_d(i, j, k, this, k1, 3);
            } else {
                world.func_147468_f(i, j, k);
            }

            if (flag) {
                Blocks.field_150335_W.func_149664_b(world, i, j, k, 1);
            }
        }

    }

    private boolean func_149847_e(World world, int i, int j, int k) {
        return this.func_149844_e(world, i + 1, j, k) ? true : (this.func_149844_e(world, i - 1, j, k) ? true : (this.func_149844_e(world, i, j - 1, k) ? true : (this.func_149844_e(world, i, j + 1, k) ? true : (this.func_149844_e(world, i, j, k - 1) ? true : this.func_149844_e(world, i, j, k + 1)))));
    }

    private int func_149845_m(World world, int i, int j, int k) {
        int l = 0;

        if (!world.func_147437_c(i, j, k)) {
            return 0;
        } else {
            l = this.func_149846_a(world, i + 1, j, k, l);
            l = this.func_149846_a(world, i - 1, j, k, l);
            l = this.func_149846_a(world, i, j - 1, k, l);
            l = this.func_149846_a(world, i, j + 1, k, l);
            l = this.func_149846_a(world, i, j, k - 1, l);
            l = this.func_149846_a(world, i, j, k + 1, l);
            return l;
        }
    }

    @Override
    public boolean func_149703_v() {
        return false;
    }

    public boolean func_149844_e(IBlockAccess iblockaccess, int i, int j, int k) {
        return this.field_149849_a[Block.func_149682_b(iblockaccess.func_147439_a(i, j, k))] > 0;
    }

    public int func_149846_a(World world, int i, int j, int k, int l) {
        int i1 = this.field_149849_a[Block.func_149682_b(world.func_147439_a(i, j, k))];

        return i1 > l ? i1 : l;
    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        return World.func_147466_a(world, i, j - 1, k) || this.func_149847_e(world, i, j, k);
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        if (!World.func_147466_a(world, i, j - 1, k) && !this.func_149847_e(world, i, j, k)) {
            world.func_147468_f(i, j, k);
        }

    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {
        if (world.field_73011_w.field_76574_g > 0 || !Blocks.field_150427_aO.func_150000_e(world, i, j, k)) {
            if (!World.func_147466_a(world, i, j - 1, k) && !this.func_149847_e(world, i, j, k)) {
                world.func_147468_f(i, j, k);
            } else {
                world.func_147464_a(i, j, k, this, this.func_149738_a(world) + world.field_73012_v.nextInt(10));
            }
        }
    }

    @Override
    public MapColor func_149728_f(int i) {
        return MapColor.field_151656_f;
    }
}
