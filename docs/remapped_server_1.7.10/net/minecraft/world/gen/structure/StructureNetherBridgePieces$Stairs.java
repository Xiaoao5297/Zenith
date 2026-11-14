package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureNetherBridgePieces$Stairs extends StructureNetherBridgePieces$Piece {

    public StructureNetherBridgePieces$Stairs() {}

    public StructureNetherBridgePieces$Stairs(int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(i);
        this.field_74885_f = j;
        this.field_74887_e = structureboundingbox;
    }

    @Override
    public void func_74861_a(StructureComponent structurecomponent, List list, Random random) {
        this.func_74965_c((StructureNetherBridgePieces$Start) structurecomponent, list, random, 6, 2, false);
    }

    public static StructureNetherBridgePieces$Stairs func_74973_a(List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, -2, 0, 0, 7, 11, 7, l);

        return func_74964_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureNetherBridgePieces$Stairs(i1, random, structureboundingbox, l) : null;
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        this.func_151549_a(world, structureboundingbox, 0, 0, 0, 6, 1, 6, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 0, 2, 0, 6, 10, 6, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 0, 2, 0, 1, 8, 0, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 5, 2, 0, 6, 8, 0, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 0, 2, 1, 0, 8, 6, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 6, 2, 1, 6, 8, 6, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 1, 2, 6, 5, 8, 6, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 0, 3, 2, 0, 5, 4, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
        this.func_151549_a(world, structureboundingbox, 6, 3, 2, 6, 5, 2, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
        this.func_151549_a(world, structureboundingbox, 6, 3, 4, 6, 5, 4, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
        this.func_151550_a(world, Blocks.field_150385_bj, 0, 5, 2, 5, structureboundingbox);
        this.func_151549_a(world, structureboundingbox, 4, 2, 5, 4, 3, 5, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 3, 2, 5, 3, 4, 5, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 2, 2, 5, 2, 5, 5, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 1, 2, 5, 1, 6, 5, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 1, 7, 1, 5, 7, 4, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 6, 8, 2, 6, 8, 4, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 2, 6, 0, 4, 8, 0, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 2, 5, 0, 4, 5, 0, Blocks.field_150386_bk, Blocks.field_150386_bk, false);

        for (int i = 0; i <= 6; ++i) {
            for (int j = 0; j <= 6; ++j) {
                this.func_151554_b(world, Blocks.field_150385_bj, 0, i, -1, j, structureboundingbox);
            }
        }

        return true;
    }
}
