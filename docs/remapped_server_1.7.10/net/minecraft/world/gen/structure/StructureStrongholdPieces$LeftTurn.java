package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureStrongholdPieces$LeftTurn extends StructureStrongholdPieces$Stronghold {

    public StructureStrongholdPieces$LeftTurn() {}

    public StructureStrongholdPieces$LeftTurn(int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(i);
        this.field_74885_f = j;
        this.field_143013_d = this.func_74988_a(random);
        this.field_74887_e = structureboundingbox;
    }

    @Override
    public void func_74861_a(StructureComponent structurecomponent, List list, Random random) {
        if (this.field_74885_f != 2 && this.field_74885_f != 3) {
            this.func_74987_c((StructureStrongholdPieces$Stairs2) structurecomponent, list, random, 1, 1);
        } else {
            this.func_74989_b((StructureStrongholdPieces$Stairs2) structurecomponent, list, random, 1, 1);
        }

    }

    public static StructureStrongholdPieces$LeftTurn func_75010_a(List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, -1, -1, 0, 5, 5, 5, l);

        return func_74991_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureStrongholdPieces$LeftTurn(i1, random, structureboundingbox, l) : null;
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        if (this.func_74860_a(world, structureboundingbox)) {
            return false;
        } else {
            this.func_74882_a(world, structureboundingbox, 0, 0, 0, 4, 4, 4, true, random, StructureStrongholdPieces.access$200());
            this.func_74990_a(world, random, structureboundingbox, this.field_143013_d, 1, 1, 0);
            if (this.field_74885_f != 2 && this.field_74885_f != 3) {
                this.func_151549_a(world, structureboundingbox, 4, 1, 1, 4, 3, 3, Blocks.field_150350_a, Blocks.field_150350_a, false);
            } else {
                this.func_151549_a(world, structureboundingbox, 0, 1, 1, 0, 3, 3, Blocks.field_150350_a, Blocks.field_150350_a, false);
            }

            return true;
        }
    }
}
