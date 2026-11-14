package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BlockFalling extends Block {

    public static boolean field_149832_M;

    public BlockFalling() {
        super(Material.field_151595_p);
        this.func_149647_a(CreativeTabs.field_78030_b);
    }

    public BlockFalling(Material material) {
        super(material);
    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {
        world.func_147464_a(i, j, k, this, this.func_149738_a(world));
    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        world.func_147464_a(i, j, k, this, this.func_149738_a(world));
    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        if (!world.field_72995_K) {
            this.func_149830_m(world, i, j, k);
        }

    }

    private void func_149830_m(World world, int i, int j, int k) {
        if (func_149831_e(world, i, j - 1, k) && j >= 0) {
            byte b0 = 32;

            if (!BlockFalling.field_149832_M && world.func_72904_c(i - b0, j - b0, k - b0, i + b0, j + b0, k + b0)) {
                if (!world.field_72995_K) {
                    EntityFallingBlock entityfallingblock = new EntityFallingBlock(world, (double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), this, world.func_72805_g(i, j, k));

                    this.func_149829_a(entityfallingblock);
                    world.func_72838_d(entityfallingblock);
                }
            } else {
                world.func_147468_f(i, j, k);

                while (func_149831_e(world, i, j - 1, k) && j > 0) {
                    --j;
                }

                if (j > 0) {
                    world.func_147449_b(i, j, k, this);
                }
            }
        }

    }

    protected void func_149829_a(EntityFallingBlock entityfallingblock) {}

    @Override
    public int func_149738_a(World world) {
        return 2;
    }

    public static boolean func_149831_e(World world, int i, int j, int k) {
        Block block = world.func_147439_a(i, j, k);

        if (block.field_149764_J == Material.field_151579_a) {
            return true;
        } else if (block == Blocks.field_150480_ab) {
            return true;
        } else {
            Material material = block.field_149764_J;

            return material == Material.field_151586_h ? true : material == Material.field_151587_i;
        }
    }

    public void func_149828_a(World world, int i, int j, int k, int l) {}
}
