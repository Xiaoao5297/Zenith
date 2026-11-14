package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureNetherBridgePieces$Corridor4 extends StructureNetherBridgePieces$Piece {

    public StructureNetherBridgePieces$Corridor4() {}

    public StructureNetherBridgePieces$Corridor4(int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(i);
        this.field_74885_f = j;
        this.field_74887_e = structureboundingbox;
    }

    @Override
    public void func_74861_a(StructureComponent structurecomponent, List list, Random random) {
        byte b0 = 1;

        if (this.field_74885_f == 1 || this.field_74885_f == 2) {
            b0 = 5;
        }

        this.func_74961_b((StructureNetherBridgePieces$Start) structurecomponent, list, random, 0, b0, random.nextInt(8) > 0);
        this.func_74965_c((StructureNetherBridgePieces$Start) structurecomponent, list, random, 0, b0, random.nextInt(8) > 0);
    }

    public static StructureNetherBridgePieces$Corridor4 func_74985_a(List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, -3, 0, 0, 9, 7, 9, l);

        return func_74964_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureNetherBridgePieces$Corridor4(i1, random, structureboundingbox, l) : null;
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        this.func_151549_a(world, structureboundingbox, 0, 0, 0, 8, 1, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 0, 2, 0, 8, 5, 8, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 0, 6, 0, 8, 6, 5, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 0, 2, 0, 2, 5, 0, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 6, 2, 0, 8, 5, 0, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 1, 3, 0, 1, 4, 0, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
        this.func_151549_a(world, structureboundingbox, 7, 3, 0, 7, 4, 0, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
        this.func_151549_a(world, structureboundingbox, 0, 2, 4, 8, 2, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 1, 1, 4, 2, 2, 4, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 6, 1, 4, 7, 2, 4, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 0, 3, 8, 8, 3, 8, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
        this.func_151549_a(world, structureboundingbox, 0, 3, 6, 0, 3, 7, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
        this.func_151549_a(world, structureboundingbox, 8, 3, 6, 8, 3, 7, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
        this.func_151549_a(world, structureboundingbox, 0, 3, 4, 0, 5, 5, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 8, 3, 4, 8, 5, 5, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 1, 3, 5, 2, 5, 5, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 6, 3, 5, 7, 5, 5, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 1, 4, 5, 1, 5, 5, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
        this.func_151549_a(world, structureboundingbox, 7, 4, 5, 7, 5, 5, Blocks.field_150386_bk, Blocks.field_150386_bk, false);

        for (int i = 0; i <= 5; ++i) {
            for (int j = 0; j <= 8; ++j) {
                this.func_151554_b(world, Blocks.field_150385_bj, 0, j, -1, i, structureboundingbox);
            }
        }

        return true;
    }
}
