package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureNetherBridgePieces$Corridor3 extends StructureNetherBridgePieces$Piece {

    public StructureNetherBridgePieces$Corridor3() {}

    public StructureNetherBridgePieces$Corridor3(int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(i);
        this.field_74885_f = j;
        this.field_74887_e = structureboundingbox;
    }

    @Override
    public void func_74861_a(StructureComponent structurecomponent, List list, Random random) {
        this.func_74963_a((StructureNetherBridgePieces$Start) structurecomponent, list, random, 1, 0, true);
    }

    public static StructureNetherBridgePieces$Corridor3 func_74982_a(List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, -1, -7, 0, 5, 14, 10, l);

        return func_74964_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureNetherBridgePieces$Corridor3(i1, random, structureboundingbox, l) : null;
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        int i = this.func_151555_a(Blocks.field_150387_bl, 2);

        for (int j = 0; j <= 9; ++j) {
            int k = Math.max(1, 7 - j);
            int l = Math.min(Math.max(k + 5, 14 - j), 13);
            int i1 = j;

            this.func_151549_a(world, structureboundingbox, 0, 0, j, 4, k, j, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
            this.func_151549_a(world, structureboundingbox, 1, k + 1, j, 3, l - 1, j, Blocks.field_150350_a, Blocks.field_150350_a, false);
            if (j <= 6) {
                this.func_151550_a(world, Blocks.field_150387_bl, i, 1, k + 1, j, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150387_bl, i, 2, k + 1, j, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150387_bl, i, 3, k + 1, j, structureboundingbox);
            }

            this.func_151549_a(world, structureboundingbox, 0, l, j, 4, l, j, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
            this.func_151549_a(world, structureboundingbox, 0, k + 1, j, 0, l - 1, j, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
            this.func_151549_a(world, structureboundingbox, 4, k + 1, j, 4, l - 1, j, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
            if ((j & 1) == 0) {
                this.func_151549_a(world, structureboundingbox, 0, k + 2, j, 0, k + 3, j, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
                this.func_151549_a(world, structureboundingbox, 4, k + 2, j, 4, k + 3, j, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
            }

            for (int j1 = 0; j1 <= 4; ++j1) {
                this.func_151554_b(world, Blocks.field_150385_bj, 0, j1, -1, i1, structureboundingbox);
            }
        }

        return true;
    }
}
