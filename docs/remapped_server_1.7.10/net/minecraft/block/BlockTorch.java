package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BlockTorch extends Block {

    protected BlockTorch() {
        super(Material.field_151594_q);
        this.func_149675_a(true);
        this.func_149647_a(CreativeTabs.field_78031_c);
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
        return 2;
    }

    private boolean func_150107_m(World world, int i, int j, int k) {
        if (World.func_147466_a(world, i, j, k)) {
            return true;
        } else {
            Block block = world.func_147439_a(i, j, k);

            return block == Blocks.field_150422_aJ || block == Blocks.field_150386_bk || block == Blocks.field_150359_w || block == Blocks.field_150463_bK;
        }
    }

    @Override
    public boolean func_149742_c(World world, int i, int j, int k) {
        return world.func_147445_c(i - 1, j, k, true) ? true : (world.func_147445_c(i + 1, j, k, true) ? true : (world.func_147445_c(i, j, k - 1, true) ? true : (world.func_147445_c(i, j, k + 1, true) ? true : this.func_150107_m(world, i, j - 1, k))));
    }

    @Override
    public int func_149660_a(World world, int i, int j, int k, int l, float f, float f1, float f2, int i1) {
        int j1 = i1;

        if (l == 1 && this.func_150107_m(world, i, j - 1, k)) {
            j1 = 5;
        }

        if (l == 2 && world.func_147445_c(i, j, k + 1, true)) {
            j1 = 4;
        }

        if (l == 3 && world.func_147445_c(i, j, k - 1, true)) {
            j1 = 3;
        }

        if (l == 4 && world.func_147445_c(i + 1, j, k, true)) {
            j1 = 2;
        }

        if (l == 5 && world.func_147445_c(i - 1, j, k, true)) {
            j1 = 1;
        }

        return j1;
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        super.func_149674_a(world, i, j, k, random);
        if (world.func_72805_g(i, j, k) == 0) {
            this.func_149726_b(world, i, j, k);
        }

    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {
        if (world.func_72805_g(i, j, k) == 0) {
            if (world.func_147445_c(i - 1, j, k, true)) {
                world.func_72921_c(i, j, k, 1, 2);
            } else if (world.func_147445_c(i + 1, j, k, true)) {
                world.func_72921_c(i, j, k, 2, 2);
            } else if (world.func_147445_c(i, j, k - 1, true)) {
                world.func_72921_c(i, j, k, 3, 2);
            } else if (world.func_147445_c(i, j, k + 1, true)) {
                world.func_72921_c(i, j, k, 4, 2);
            } else if (this.func_150107_m(world, i, j - 1, k)) {
                world.func_72921_c(i, j, k, 5, 2);
            }
        }

        this.func_150109_e(world, i, j, k);
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        this.func_150108_b(world, i, j, k, block);
    }

    protected boolean func_150108_b(World world, int i, int j, int k, Block block) {
        if (this.func_150109_e(world, i, j, k)) {
            int l = world.func_72805_g(i, j, k);
            boolean flag = false;

            if (!world.func_147445_c(i - 1, j, k, true) && l == 1) {
                flag = true;
            }

            if (!world.func_147445_c(i + 1, j, k, true) && l == 2) {
                flag = true;
            }

            if (!world.func_147445_c(i, j, k - 1, true) && l == 3) {
                flag = true;
            }

            if (!world.func_147445_c(i, j, k + 1, true) && l == 4) {
                flag = true;
            }

            if (!this.func_150107_m(world, i, j - 1, k) && l == 5) {
                flag = true;
            }

            if (flag) {
                this.func_149697_b(world, i, j, k, world.func_72805_g(i, j, k), 0);
                world.func_147468_f(i, j, k);
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    protected boolean func_150109_e(World world, int i, int j, int k) {
        if (!this.func_149742_c(world, i, j, k)) {
            if (world.func_147439_a(i, j, k) == this) {
                this.func_149697_b(world, i, j, k, world.func_72805_g(i, j, k), 0);
                world.func_147468_f(i, j, k);
            }

            return false;
        } else {
            return true;
        }
    }

    @Override
    public MovingObjectPosition func_149731_a(World world, int i, int j, int k, Vec3 vec3, Vec3 vec31) {
        int l = world.func_72805_g(i, j, k) & 7;
        float f = 0.15F;

        if (l == 1) {
            this.func_149676_a(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
        } else if (l == 2) {
            this.func_149676_a(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
        } else if (l == 3) {
            this.func_149676_a(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
        } else if (l == 4) {
            this.func_149676_a(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
        } else {
            f = 0.1F;
            this.func_149676_a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.6F, 0.5F + f);
        }

        return super.func_149731_a(world, i, j, k, vec3, vec31);
    }
}
