package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;

public class BlockMobSpawner extends BlockContainer {

    protected BlockMobSpawner() {
        super(Material.field_151576_e);
    }

    @Override
    public TileEntity func_149915_a(World world, int i) {
        return new TileEntityMobSpawner();
    }

    @Override
    public Item func_149650_a(int i, Random random, int j) {
        return null;
    }

    @Override
    public int func_149745_a(Random random) {
        return 0;
    }

    @Override
    public void func_149690_a(World world, int i, int j, int k, int l, float f, int i1) {
        super.func_149690_a(world, i, j, k, l, f, i1);
        int j1 = 15 + world.field_73012_v.nextInt(15) + world.field_73012_v.nextInt(15);

        this.func_149657_c(world, i, j, k, j1);
    }

    @Override
    public boolean func_149662_c() {
        return false;
    }
}
