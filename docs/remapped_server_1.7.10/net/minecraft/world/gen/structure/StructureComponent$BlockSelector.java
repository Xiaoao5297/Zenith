package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public abstract class StructureComponent$BlockSelector {

    protected Block field_151562_a;
    protected int field_75065_b;

    protected StructureComponent$BlockSelector() {
        this.field_151562_a = Blocks.field_150350_a;
    }

    public abstract void func_75062_a(Random random, int i, int j, int k, boolean flag);

    public Block func_151561_a() {
        return this.field_151562_a;
    }

    public int func_75064_b() {
        return this.field_75065_b;
    }
}
