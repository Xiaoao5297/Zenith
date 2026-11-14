package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockRedstoneLight extends Block {

    private final boolean field_150171_a;

    public BlockRedstoneLight(boolean flag) {
        super(Material.field_151591_t);
        this.field_150171_a = flag;
        if (flag) {
            this.func_149715_a(1.0F);
        }

    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {
        if (!world.field_72995_K) {
            if (this.field_150171_a && !world.func_72864_z(i, j, k)) {
                world.func_147464_a(i, j, k, this, 4);
            } else if (!this.field_150171_a && world.func_72864_z(i, j, k)) {
                world.func_147465_d(i, j, k, Blocks.field_150374_bv, 0, 2);
            }
        }

    }

    @Override
    public void func_149695_a(World world, int i, int j, int k, Block block) {
        if (!world.field_72995_K) {
            if (this.field_150171_a && !world.func_72864_z(i, j, k)) {
                world.func_147464_a(i, j, k, this, 4);
            } else if (!this.field_150171_a && world.func_72864_z(i, j, k)) {
                world.func_147465_d(i, j, k, Blocks.field_150374_bv, 0, 2);
            }
        }

    }

    @Override
    public void func_149674_a(World world, int i, int j, int k, Random random) {
        if (!world.field_72995_K && this.field_150171_a && !world.func_72864_z(i, j, k)) {
            world.func_147465_d(i, j, k, Blocks.field_150379_bu, 0, 2);
        }

    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return Item.func_150898_a(Blocks.field_150379_bu);
    }

    @Override
    protected ItemStack func_149644_j(int i) {
        return new ItemStack(Blocks.field_150379_bu);
    }
}
