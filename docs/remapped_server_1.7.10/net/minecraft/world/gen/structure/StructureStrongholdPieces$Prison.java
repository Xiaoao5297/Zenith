package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureStrongholdPieces$Prison extends StructureStrongholdPieces$Stronghold {

    public StructureStrongholdPieces$Prison() {}

    public StructureStrongholdPieces$Prison(int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(i);
        this.field_74885_f = j;
        this.field_143013_d = this.func_74988_a(random);
        this.field_74887_e = structureboundingbox;
    }

    @Override
    public void func_74861_a(StructureComponent structurecomponent, List list, Random random) {
        this.func_74986_a((StructureStrongholdPieces$Stairs2) structurecomponent, list, random, 1, 1);
    }

    public static StructureStrongholdPieces$Prison func_75016_a(List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, -1, -1, 0, 9, 5, 11, l);

        return func_74991_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureStrongholdPieces$Prison(i1, random, structureboundingbox, l) : null;
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        if (this.func_74860_a(world, structureboundingbox)) {
            return false;
        } else {
            this.func_74882_a(world, structureboundingbox, 0, 0, 0, 8, 4, 10, true, random, StructureStrongholdPieces.access$200());
            this.func_74990_a(world, random, structureboundingbox, this.field_143013_d, 1, 1, 0);
            this.func_151549_a(world, structureboundingbox, 1, 1, 10, 3, 3, 10, Blocks.field_150350_a, Blocks.field_150350_a, false);
            this.func_74882_a(world, structureboundingbox, 4, 1, 1, 4, 3, 1, false, random, StructureStrongholdPieces.access$200());
            this.func_74882_a(world, structureboundingbox, 4, 1, 3, 4, 3, 3, false, random, StructureStrongholdPieces.access$200());
            this.func_74882_a(world, structureboundingbox, 4, 1, 7, 4, 3, 7, false, random, StructureStrongholdPieces.access$200());
            this.func_74882_a(world, structureboundingbox, 4, 1, 9, 4, 3, 9, false, random, StructureStrongholdPieces.access$200());
            this.func_151549_a(world, structureboundingbox, 4, 1, 4, 4, 3, 6, Blocks.field_150411_aY, Blocks.field_150411_aY, false);
            this.func_151549_a(world, structureboundingbox, 5, 1, 5, 7, 3, 5, Blocks.field_150411_aY, Blocks.field_150411_aY, false);
            this.func_151550_a(world, Blocks.field_150411_aY, 0, 4, 3, 2, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150411_aY, 0, 4, 3, 8, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150454_av, this.func_151555_a(Blocks.field_150454_av, 3), 4, 1, 2, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150454_av, this.func_151555_a(Blocks.field_150454_av, 3) + 8, 4, 2, 2, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150454_av, this.func_151555_a(Blocks.field_150454_av, 3), 4, 1, 8, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150454_av, this.func_151555_a(Blocks.field_150454_av, 3) + 8, 4, 2, 8, structureboundingbox);
            return true;
        }
    }
}
