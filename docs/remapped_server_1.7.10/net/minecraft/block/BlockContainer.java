package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class BlockContainer extends Block implements ITileEntityProvider {

    protected BlockContainer(Material material) {
        super(material);
        this.field_149758_A = true;
    }

    @Override
    public void func_149726_b(World world, int i, int j, int k) {
        super.func_149726_b(world, i, j, k);
    }

    @Override
    public void func_149749_a(World world, int i, int j, int k, Block block, int l) {
        super.func_149749_a(world, i, j, k, block, l);
        world.func_147475_p(i, j, k);
    }

    @Override
    public boolean func_149696_a(World world, int i, int j, int k, int l, int i1) {
        super.func_149696_a(world, i, j, k, l, i1);
        TileEntity tileentity = world.func_147438_o(i, j, k);

        return tileentity != null ? tileentity.func_145842_c(l, i1) : false;
    }
}
