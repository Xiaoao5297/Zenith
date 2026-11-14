package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureStrongholdPieces$StairsStraight extends StructureStrongholdPieces$Stronghold {

    public StructureStrongholdPieces$StairsStraight() {}

    public StructureStrongholdPieces$StairsStraight(int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(i);
        this.field_74885_f = j;
        this.field_143013_d = this.func_74988_a(random);
        this.field_74887_e = structureboundingbox;
    }

    @Override
    public void func_74861_a(StructureComponent structurecomponent, List list, Random random) {
        this.func_74986_a((StructureStrongholdPieces$Stairs2) structurecomponent, list, random, 1, 1);
    }

    public static StructureStrongholdPieces$StairsStraight func_75028_a(List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, -1, -7, 0, 5, 11, 8, l);

        return func_74991_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureStrongholdPieces$StairsStraight(i1, random, structureboundingbox, l) : null;
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        if (this.func_74860_a(world, structureboundingbox)) {
            return false;
        } else {
            this.func_74882_a(world, structureboundingbox, 0, 0, 0, 4, 10, 7, true, random, StructureStrongholdPieces.access$200());
            this.func_74990_a(world, random, structureboundingbox, this.field_143013_d, 1, 7, 0);
            this.func_74990_a(world, random, structureboundingbox, StructureStrongholdPieces$Stronghold$Door.OPENING, 1, 1, 7);
            int i = this.func_151555_a(Blocks.field_150446_ar, 2);

            for (int j = 0; j < 6; ++j) {
                this.func_151550_a(world, Blocks.field_150446_ar, i, 1, 6 - j, 1 + j, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150446_ar, i, 2, 6 - j, 1 + j, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150446_ar, i, 3, 6 - j, 1 + j, structureboundingbox);
                if (j < 5) {
                    this.func_151550_a(world, Blocks.field_150417_aV, 0, 1, 5 - j, 1 + j, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150417_aV, 0, 2, 5 - j, 1 + j, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150417_aV, 0, 3, 5 - j, 1 + j, structureboundingbox);
                }
            }

            return true;
        }
    }
}
