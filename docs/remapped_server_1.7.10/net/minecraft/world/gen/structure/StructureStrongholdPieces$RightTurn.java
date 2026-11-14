package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureStrongholdPieces$RightTurn extends StructureStrongholdPieces$LeftTurn {

    public StructureStrongholdPieces$RightTurn() {}

    @Override
    public void func_74861_a(StructureComponent structurecomponent, List list, Random random) {
        if (this.field_74885_f != 2 && this.field_74885_f != 3) {
            this.func_74989_b((StructureStrongholdPieces$Stairs2) structurecomponent, list, random, 1, 1);
        } else {
            this.func_74987_c((StructureStrongholdPieces$Stairs2) structurecomponent, list, random, 1, 1);
        }

    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        if (this.func_74860_a(world, structureboundingbox)) {
            return false;
        } else {
            this.func_74882_a(world, structureboundingbox, 0, 0, 0, 4, 4, 4, true, random, StructureStrongholdPieces.access$200());
            this.func_74990_a(world, random, structureboundingbox, this.field_143013_d, 1, 1, 0);
            if (this.field_74885_f != 2 && this.field_74885_f != 3) {
                this.func_151549_a(world, structureboundingbox, 0, 1, 1, 0, 3, 3, Blocks.field_150350_a, Blocks.field_150350_a, false);
            } else {
                this.func_151549_a(world, structureboundingbox, 4, 1, 1, 4, 3, 3, Blocks.field_150350_a, Blocks.field_150350_a, false);
            }

            return true;
        }
    }
}
