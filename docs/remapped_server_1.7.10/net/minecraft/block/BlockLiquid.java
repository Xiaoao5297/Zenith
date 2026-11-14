package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockLiquid extends Block {

    protected BlockLiquid(Material material) {
        super(material);
        float f = 0.0F;
        float f1 = 0.0F;

        this.func_149676_a(0.0F + f1, 0.0F + f, 0.0F + f1, 1.0F + f1, 1.0F + f, 1.0F + f1);
        this.func_149675_a(true);
    }

    @Override
    public boolean func_149655_b(IBlockAccess iblockaccess, int i, int j, int k) {
        return this.field_149764_J != Material.field_151587_i;
    }

    public static float func_149801_b(int i) {
        if (i >= 8) {
            i = 0;
        }

        return (float) (i + 1) / 9.0F;
    }

    protected int func_149804_e(World world, int i, int j, int k) {
        return world.func_147439_a(i, j, k).func_149688_o() == this.field_149764_J ? world.func_72805_g(i, j, k) : -1;
    }

    protected int func_149798_e(IBlockAccess iblockaccess, int i, int j, int k) {
        if (iblockaccess.func_147439_a(i, j, k).func_149688_o() != this.field_149764_J) {
            return -1;
        } else {
            int l = iblockaccess.func_72805_g(i, j, k);

            if (l >= 8) {
                l = 0;
            }

            return l;
        }
    }

    @Override
    public boolean func_149686_d() {
        return false;
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }

    @Override
    public boolean func_149678_a(int i, boolean flag) {
        return flag && i == 0;
    }

    @Override
    public boolean func_149747_d(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        Material material = iblockaccess.func_147439_a(i, j, k).func_149688_o();

        return material == this.field_149764_J ? false : (l == 1 ? true : (material == Material.field_151588_w ? false : super.func_149747_d(iblockaccess, i, j, k, l)));
    }

    @Override
    public AxisAlignedBB func_149668_a(World world, int i, int j, int k) {
        return null;
    }

    @Override
    public int func_149645_b() {
        return 4;
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return null;
    }

    @Override
    public int func_149745_a(Random random) {
        return 0;
    }

    private Vec3 func_149800_f(IBlockAccess iblockaccess, int i, int j, int k) {
        Vec3 vec3 = Vec3.func_72443_a(0.0D, 0.0D, 0.0D);
        int l = this.func_149798_e(iblockaccess, i, j, k);

        for (int i1 = 0; i1 < 4; ++i1) {
            int j1 = i;
            int k1 = k;

            if (i1 == 0) {
                j1 = i - 1;
            }

            if (i1 == 1) {
                k1 = k - 1;
            }

            if (i1 == 2) {
                ++j1;
            }

            if (i1 == 3) {
                ++k1;
            }

            int l1 = this.func_149798_e(iblockaccess, j1, j, k1);
            int i2;

            if (l1 < 0) {
                if (!iblockaccess.func_147439_a(j1, j, k1).func_149688_o().func_76230_c()) {
                    l1 = this.func_149798_e(iblockaccess, j1, j - 1, k1);
                    if (l1 >= 0) {
                        i2 = l1 - (l - 8);
                        vec3 = vec3.func_72441_c((double) ((j1 - i) * i2), (double) ((j - j) * i2), (double) ((k1 - k) * i2));
                    }
                }
            } else if (l1 >= 0) {
                i2 = l1 - l;
                vec3 = vec3.func_72441_c((double) ((j1 - i) * i2), (double) ((j - j) * i2), (double) ((k1 - k) * i2));
            }
        }

        if (iblockaccess.func_72805_g(i, j, k) >= 8) {
            boolean flag = false;

            if (flag || this.func_149747_d(iblockaccess, i, j, k - 1, 2)) {
                flag = true;
            }

            if (flag || this.func_149747_d(iblockaccess, i, j, k + 1, 3)) {
                flag = true;
            }

            if (flag || this.func_149747_d(iblockaccess, i - 1, j, k, 4)) {
                flag = true;
            }

            if (flag || this.func_149747_d(iblockaccess, i + 1, j, k, 5)) {
                flag = true;
            }

            if (flag || this.func_149747_d(iblockaccess, i, j + 1, k - 1, 2)) {
                flag = true;
            }

            if (flag || this.func_149747_d(iblockaccess, i, j + 1, k + 1, 3)) {
                flag = true;
            }

            if (flag || this.func_149747_d(iblockaccess, i - 1, j + 1, k, 4)) {
                flag = true;
            }

            if (flag || this.func_149747_d(iblockaccess, i + 1, j + 1, k, 5)) {
                flag = true;
            }

            if (flag) {
                vec3 = vec3.func_72432_b().func_72441_c(0.0D, -6.0D, 0.0D);
            }
        }

        vec3 = vec3.func_72432_b();
        return vec3;
    }

    @Override
    public void func_149640_a(World world, int i, int j, int k, Entity entity, Vec3 vec3) {
        Vec3 vec31 = this.func_149800_f(world, i, j, k);

        vec3.field_72450_a += vec31.field_72450_a;
        vec3.field_72448_b += vec31.field_72448_b;
        vec3.field_72449_c += vec31.field_72449_c;
    }

    @Override
    public int func_149738_a(World world) {
        return this.field_149764_J == Material.field_151586_h ? 5 : (this.field_149764_J == Material.field_151587_i ? (world.field_73011_w.field_76576_e ? 10 : 30) : 0);
    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {
        this.func_149805_n(world, i, j, k);
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        this.func_149805_n(world, i, j, k);
    }

    private void func_149805_n(World world, int i, int j, int k) {
        if (world.func_147439_a(i, j, k) == this) {
            if (this.field_149764_J == Material.field_151587_i) {
                boolean flag = false;

                if (flag || world.func_147439_a(i, j, k - 1).func_149688_o() == Material.field_151586_h) {
                    flag = true;
                }

                if (flag || world.func_147439_a(i, j, k + 1).func_149688_o() == Material.field_151586_h) {
                    flag = true;
                }

                if (flag || world.func_147439_a(i - 1, j, k).func_149688_o() == Material.field_151586_h) {
                    flag = true;
                }

                if (flag || world.func_147439_a(i + 1, j, k).func_149688_o() == Material.field_151586_h) {
                    flag = true;
                }

                if (flag || world.func_147439_a(i, j + 1, k).func_149688_o() == Material.field_151586_h) {
                    flag = true;
                }

                if (flag) {
                    int l = world.func_72805_g(i, j, k);

                    if (l == 0) {
                        world.func_147449_b(i, j, k, Blocks.field_150343_Z);
                    } else if (l <= 4) {
                        world.func_147449_b(i, j, k, Blocks.field_150347_e);
                    }

                    this.func_149799_m(world, i, j, k);
                }
            }

        }
    }

    protected void func_149799_m(World world, int i, int j, int k) {
        world.func_72908_a((double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), "random.fizz", 0.5F, 2.6F + (world.field_73012_v.nextFloat() - world.field_73012_v.nextFloat()) * 0.8F);

        for (int l = 0; l < 8; ++l) {
            world.func_72869_a("largesmoke", (double) i + Math.random(), (double) j + 1.2D, (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
        }

    }
}
