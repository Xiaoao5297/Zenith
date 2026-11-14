package net.minecraft.block;

import net.minecraft.world.World;

public class BlockRail extends BlockRailBase {

    protected BlockRail() {
        super(false);
    }

    @Override
    protected void func_150048_a(World world, int i, int j, int k, int l, int i1, Block block) {
        if (block.func_149744_f() && (new BlockRailBase$Rail(this, world, i, j, k)).func_150650_a() == 3) {
            this.func_150052_a(world, i, j, k, false);
        }

    }
}
