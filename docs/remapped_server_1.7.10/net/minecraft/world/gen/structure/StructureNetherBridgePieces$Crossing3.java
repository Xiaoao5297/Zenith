package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureNetherBridgePieces$Crossing3 extends StructureNetherBridgePieces$Piece {

    public StructureNetherBridgePieces$Crossing3() {}

    public StructureNetherBridgePieces$Crossing3(int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(i);
        this.field_74885_f = j;
        this.field_74887_e = structureboundingbox;
    }

    protected StructureNetherBridgePieces$Crossing3(Random random, int i, int j) {
        super(0);
        this.field_74885_f = random.nextInt(4);
        switch (this.field_74885_f) {
            case 0:
            case 2:
                this.field_74887_e = new StructureBoundingBox(i, 64, j, i + 19 - 1, 73, j + 19 - 1);
                break;
            default:
                this.field_74887_e = new StructureBoundingBox(i, 64, j, i + 19 - 1, 73, j + 19 - 1);
        }

    }

    @Override
    public void func_74861_a(StructureComponent structurecomponent, List list, Random random) {
        this.func_74963_a((StructureNetherBridgePieces$Start) structurecomponent, list, random, 8, 3, false);
        this.func_74961_b((StructureNetherBridgePieces$Start) structurecomponent, list, random, 3, 8, false);
        this.func_74965_c((StructureNetherBridgePieces$Start) structurecomponent, list, random, 3, 8, false);
    }

    public static StructureNetherBridgePieces$Crossing3 func_74966_a(List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, -8, -3, 0, 19, 10, 19, l);

        return func_74964_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureNetherBridgePieces$Crossing3(i1, random, structureboundingbox, l) : null;
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        this.func_151549_a(world, structureboundingbox, 7, 3, 0, 11, 4, 18, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 0, 3, 7, 18, 4, 11, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 8, 5, 0, 10, 7, 18, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 0, 5, 8, 18, 7, 10, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 7, 5, 0, 7, 5, 7, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 7, 5, 11, 7, 5, 18, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 11, 5, 0, 11, 5, 7, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 11, 5, 11, 11, 5, 18, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 0, 5, 7, 7, 5, 7, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 11, 5, 7, 18, 5, 7, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 0, 5, 11, 7, 5, 11, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 11, 5, 11, 18, 5, 11, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 7, 2, 0, 11, 2, 5, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 7, 2, 13, 11, 2, 18, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 7, 0, 0, 11, 1, 3, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 7, 0, 15, 11, 1, 18, Blocks.field_150385_bj, Blocks.field_150385_bj, false);

        int i;
        int j;

        for (i = 7; i <= 11; ++i) {
            for (j = 0; j <= 2; ++j) {
                this.func_151554_b(world, Blocks.field_150385_bj, 0, i, -1, j, structureboundingbox);
                this.func_151554_b(world, Blocks.field_150385_bj, 0, i, -1, 18 - j, structureboundingbox);
            }
        }

        this.func_151549_a(world, structureboundingbox, 0, 2, 7, 5, 2, 11, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 13, 2, 7, 18, 2, 11, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 0, 0, 7, 3, 1, 11, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 15, 0, 7, 18, 1, 11, Blocks.field_150385_bj, Blocks.field_150385_bj, false);

        for (i = 0; i <= 2; ++i) {
            for (j = 7; j <= 11; ++j) {
                this.func_151554_b(world, Blocks.field_150385_bj, 0, i, -1, j, structureboundingbox);
                this.func_151554_b(world, Blocks.field_150385_bj, 0, 18 - i, -1, j, structureboundingbox);
            }
        }

        return true;
    }
}
