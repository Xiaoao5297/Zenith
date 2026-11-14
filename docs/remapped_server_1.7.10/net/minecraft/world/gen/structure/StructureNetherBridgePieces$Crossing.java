package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureNetherBridgePieces$Crossing extends StructureNetherBridgePieces$Piece {

    public StructureNetherBridgePieces$Crossing() {}

    public StructureNetherBridgePieces$Crossing(int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(i);
        this.field_74885_f = j;
        this.field_74887_e = structureboundingbox;
    }

    @Override
    public void func_74861_a(StructureComponent structurecomponent, List list, Random random) {
        this.func_74963_a((StructureNetherBridgePieces$Start) structurecomponent, list, random, 2, 0, false);
        this.func_74961_b((StructureNetherBridgePieces$Start) structurecomponent, list, random, 0, 2, false);
        this.func_74965_c((StructureNetherBridgePieces$Start) structurecomponent, list, random, 0, 2, false);
    }

    public static StructureNetherBridgePieces$Crossing func_74974_a(List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, -2, 0, 0, 7, 9, 7, l);

        return func_74964_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureNetherBridgePieces$Crossing(i1, random, structureboundingbox, l) : null;
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        this.func_151549_a(world, structureboundingbox, 0, 0, 0, 6, 1, 6, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 0, 2, 0, 6, 7, 6, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 0, 2, 0, 1, 6, 0, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 0, 2, 6, 1, 6, 6, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 5, 2, 0, 6, 6, 0, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 5, 2, 6, 6, 6, 6, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 0, 2, 0, 0, 6, 1, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 0, 2, 5, 0, 6, 6, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 6, 2, 0, 6, 6, 1, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 6, 2, 5, 6, 6, 6, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 2, 6, 0, 4, 6, 0, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 2, 5, 0, 4, 5, 0, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
        this.func_151549_a(world, structureboundingbox, 2, 6, 6, 4, 6, 6, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 2, 5, 6, 4, 5, 6, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
        this.func_151549_a(world, structureboundingbox, 0, 6, 2, 0, 6, 4, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 0, 5, 2, 0, 5, 4, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
        this.func_151549_a(world, structureboundingbox, 6, 6, 2, 6, 6, 4, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 6, 5, 2, 6, 5, 4, Blocks.field_150386_bk, Blocks.field_150386_bk, false);

        for (int i = 0; i <= 6; ++i) {
            for (int j = 0; j <= 6; ++j) {
                this.func_151554_b(world, Blocks.field_150385_bj, 0, i, -1, j, structureboundingbox);
            }
        }

        return true;
    }
}
