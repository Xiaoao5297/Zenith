package net.minecraft.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class PathFinder {

    private IBlockAccess field_75868_a;
    private Path field_75866_b = new Path();
    private IntHashMap field_75867_c = new IntHashMap();
    private PathPoint[] field_75864_d = new PathPoint[32];
    private boolean field_75865_e;
    private boolean field_75862_f;
    private boolean field_75863_g;
    private boolean field_75869_h;

    public PathFinder(IBlockAccess iblockaccess, boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        this.field_75868_a = iblockaccess;
        this.field_75865_e = flag;
        this.field_75862_f = flag1;
        this.field_75863_g = flag2;
        this.field_75869_h = flag3;
    }

    public PathEntity func_75856_a(Entity entity, Entity entity1, float f) {
        return this.func_75857_a(entity, entity1.field_70165_t, entity1.field_70121_D.field_72338_b, entity1.field_70161_v, f);
    }

    public PathEntity func_75859_a(Entity entity, int i, int j, int k, float f) {
        return this.func_75857_a(entity, (double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), f);
    }

    private PathEntity func_75857_a(Entity entity, double d0, double d1, double d2, float f) {
        this.field_75866_b.func_75848_a();
        this.field_75867_c.func_76046_c();
        boolean flag = this.field_75863_g;
        int i = MathHelper.func_76128_c(entity.field_70121_D.field_72338_b + 0.5D);

        if (this.field_75869_h && entity.func_70090_H()) {
            i = (int) entity.field_70121_D.field_72338_b;

            for (Block block = this.field_75868_a.func_147439_a(MathHelper.func_76128_c(entity.field_70165_t), i, MathHelper.func_76128_c(entity.field_70161_v)); block == Blocks.field_150358_i || block == Blocks.field_150355_j; block = this.field_75868_a.func_147439_a(MathHelper.func_76128_c(entity.field_70165_t), i, MathHelper.func_76128_c(entity.field_70161_v))) {
                ++i;
            }

            flag = this.field_75863_g;
            this.field_75863_g = false;
        } else {
            i = MathHelper.func_76128_c(entity.field_70121_D.field_72338_b + 0.5D);
        }

        PathPoint pathpoint = this.func_75854_a(MathHelper.func_76128_c(entity.field_70121_D.field_72340_a), i, MathHelper.func_76128_c(entity.field_70121_D.field_72339_c));
        PathPoint pathpoint1 = this.func_75854_a(MathHelper.func_76128_c(d0 - (double) (entity.field_70130_N / 2.0F)), MathHelper.func_76128_c(d1), MathHelper.func_76128_c(d2 - (double) (entity.field_70130_N / 2.0F)));
        PathPoint pathpoint2 = new PathPoint(MathHelper.func_76141_d(entity.field_70130_N + 1.0F), MathHelper.func_76141_d(entity.field_70131_O + 1.0F), MathHelper.func_76141_d(entity.field_70130_N + 1.0F));
        PathEntity pathentity = this.func_75861_a(entity, pathpoint, pathpoint1, pathpoint2, f);

        this.field_75863_g = flag;
        return pathentity;
    }

    private PathEntity func_75861_a(Entity entity, PathPoint pathpoint, PathPoint pathpoint1, PathPoint pathpoint2, float f) {
        pathpoint.field_75836_e = 0.0F;
        pathpoint.field_75833_f = pathpoint.func_75832_b(pathpoint1);
        pathpoint.field_75834_g = pathpoint.field_75833_f;
        this.field_75866_b.func_75848_a();
        this.field_75866_b.func_75849_a(pathpoint);
        PathPoint pathpoint3 = pathpoint;

        while (!this.field_75866_b.func_75845_e()) {
            PathPoint pathpoint4 = this.field_75866_b.func_75844_c();

            if (pathpoint4.equals(pathpoint1)) {
                return this.func_75853_a(pathpoint, pathpoint1);
            }

            if (pathpoint4.func_75832_b(pathpoint1) < pathpoint3.func_75832_b(pathpoint1)) {
                pathpoint3 = pathpoint4;
            }

            pathpoint4.field_75842_i = true;
            int i = this.func_75860_b(entity, pathpoint4, pathpoint2, pathpoint1, f);

            for (int j = 0; j < i; ++j) {
                PathPoint pathpoint5 = this.field_75864_d[j];
                float f1 = pathpoint4.field_75836_e + pathpoint4.func_75832_b(pathpoint5);

                if (!pathpoint5.func_75831_a() || f1 < pathpoint5.field_75836_e) {
                    pathpoint5.field_75841_h = pathpoint4;
                    pathpoint5.field_75836_e = f1;
                    pathpoint5.field_75833_f = pathpoint5.func_75832_b(pathpoint1);
                    if (pathpoint5.func_75831_a()) {
                        this.field_75866_b.func_75850_a(pathpoint5, pathpoint5.field_75836_e + pathpoint5.field_75833_f);
                    } else {
                        pathpoint5.field_75834_g = pathpoint5.field_75836_e + pathpoint5.field_75833_f;
                        this.field_75866_b.func_75849_a(pathpoint5);
                    }
                }
            }
        }

        if (pathpoint3 == pathpoint) {
            return null;
        } else {
            return this.func_75853_a(pathpoint, pathpoint3);
        }
    }

    private int func_75860_b(Entity entity, PathPoint pathpoint, PathPoint pathpoint1, PathPoint pathpoint2, float f) {
        int i = 0;
        byte b0 = 0;

        if (this.func_75855_a(entity, pathpoint.field_75839_a, pathpoint.field_75837_b + 1, pathpoint.field_75838_c, pathpoint1) == 1) {
            b0 = 1;
        }

        PathPoint pathpoint3 = this.func_75858_a(entity, pathpoint.field_75839_a, pathpoint.field_75837_b, pathpoint.field_75838_c + 1, pathpoint1, b0);
        PathPoint pathpoint4 = this.func_75858_a(entity, pathpoint.field_75839_a - 1, pathpoint.field_75837_b, pathpoint.field_75838_c, pathpoint1, b0);
        PathPoint pathpoint5 = this.func_75858_a(entity, pathpoint.field_75839_a + 1, pathpoint.field_75837_b, pathpoint.field_75838_c, pathpoint1, b0);
        PathPoint pathpoint6 = this.func_75858_a(entity, pathpoint.field_75839_a, pathpoint.field_75837_b, pathpoint.field_75838_c - 1, pathpoint1, b0);

        if (pathpoint3 != null && !pathpoint3.field_75842_i && pathpoint3.func_75829_a(pathpoint2) < f) {
            this.field_75864_d[i++] = pathpoint3;
        }

        if (pathpoint4 != null && !pathpoint4.field_75842_i && pathpoint4.func_75829_a(pathpoint2) < f) {
            this.field_75864_d[i++] = pathpoint4;
        }

        if (pathpoint5 != null && !pathpoint5.field_75842_i && pathpoint5.func_75829_a(pathpoint2) < f) {
            this.field_75864_d[i++] = pathpoint5;
        }

        if (pathpoint6 != null && !pathpoint6.field_75842_i && pathpoint6.func_75829_a(pathpoint2) < f) {
            this.field_75864_d[i++] = pathpoint6;
        }

        return i;
    }

    private PathPoint func_75858_a(Entity entity, int i, int j, int k, PathPoint pathpoint, int l) {
        PathPoint pathpoint1 = null;
        int i1 = this.func_75855_a(entity, i, j, k, pathpoint);

        if (i1 == 2) {
            return this.func_75854_a(i, j, k);
        } else {
            if (i1 == 1) {
                pathpoint1 = this.func_75854_a(i, j, k);
            }

            if (pathpoint1 == null && l > 0 && i1 != -3 && i1 != -4 && this.func_75855_a(entity, i, j + l, k, pathpoint) == 1) {
                pathpoint1 = this.func_75854_a(i, j + l, k);
                j += l;
            }

            if (pathpoint1 != null) {
                int j1 = 0;
                int k1 = 0;

                while (j > 0) {
                    k1 = this.func_75855_a(entity, i, j - 1, k, pathpoint);
                    if (this.field_75863_g && k1 == -1) {
                        return null;
                    }

                    if (k1 != 1) {
                        break;
                    }

                    if (j1++ >= entity.func_82143_as()) {
                        return null;
                    }

                    --j;
                    if (j > 0) {
                        pathpoint1 = this.func_75854_a(i, j, k);
                    }
                }

                if (k1 == -2) {
                    return null;
                }
            }

            return pathpoint1;
        }
    }

    private final PathPoint func_75854_a(int i, int j, int k) {
        int l = PathPoint.func_75830_a(i, j, k);
        PathPoint pathpoint = (PathPoint) this.field_75867_c.func_76041_a(l);

        if (pathpoint == null) {
            pathpoint = new PathPoint(i, j, k);
            this.field_75867_c.func_76038_a(l, pathpoint);
        }

        return pathpoint;
    }

    public int func_75855_a(Entity entity, int i, int j, int k, PathPoint pathpoint) {
        return func_82565_a(entity, i, j, k, pathpoint, this.field_75863_g, this.field_75862_f, this.field_75865_e);
    }

    public static int func_82565_a(Entity entity, int i, int j, int k, PathPoint pathpoint, boolean flag, boolean flag1, boolean flag2) {
        boolean flag3 = false;

        for (int l = i; l < i + pathpoint.field_75839_a; ++l) {
            for (int i1 = j; i1 < j + pathpoint.field_75837_b; ++i1) {
                for (int j1 = k; j1 < k + pathpoint.field_75838_c; ++j1) {
                    Block block = entity.field_70170_p.func_147439_a(l, i1, j1);

                    if (block.func_149688_o() != Material.field_151579_a) {
                        if (block == Blocks.field_150415_aT) {
                            flag3 = true;
                        } else if (block != Blocks.field_150358_i && block != Blocks.field_150355_j) {
                            if (!flag2 && block == Blocks.field_150466_ao) {
                                return 0;
                            }
                        } else {
                            if (flag) {
                                return -1;
                            }

                            flag3 = true;
                        }

                        int k1 = block.func_149645_b();

                        if (entity.field_70170_p.func_147439_a(l, i1, j1).func_149645_b() == 9) {
                            int l1 = MathHelper.func_76128_c(entity.field_70165_t);
                            int i2 = MathHelper.func_76128_c(entity.field_70163_u);
                            int j2 = MathHelper.func_76128_c(entity.field_70161_v);

                            if (entity.field_70170_p.func_147439_a(l1, i2, j2).func_149645_b() != 9 && entity.field_70170_p.func_147439_a(l1, i2 - 1, j2).func_149645_b() != 9) {
                                return -3;
                            }
                        } else if (!block.func_149655_b(entity.field_70170_p, l, i1, j1) && (!flag1 || block != Blocks.field_150466_ao)) {
                            if (k1 == 11 || block == Blocks.field_150396_be || k1 == 32) {
                                return -3;
                            }

                            if (block == Blocks.field_150415_aT) {
                                return -4;
                            }

                            Material material = block.func_149688_o();

                            if (material != Material.field_151587_i) {
                                return 0;
                            }

                            if (!entity.func_70058_J()) {
                                return -2;
                            }
                        }
                    }
                }
            }
        }

        return flag3 ? 2 : 1;
    }

    private PathEntity func_75853_a(PathPoint pathpoint, PathPoint pathpoint1) {
        int i = 1;

        PathPoint pathpoint2;

        for (pathpoint2 = pathpoint1; pathpoint2.field_75841_h != null; pathpoint2 = pathpoint2.field_75841_h) {
            ++i;
        }

        PathPoint[] apathpoint = new PathPoint[i];

        pathpoint2 = pathpoint1;
        --i;

        for (apathpoint[i] = pathpoint1; pathpoint2.field_75841_h != null; apathpoint[i] = pathpoint2) {
            pathpoint2 = pathpoint2.field_75841_h;
            --i;
        }

        return new PathEntity(apathpoint);
    }
}
