package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureNetherBridgePieces$NetherStalkRoom extends StructureNetherBridgePieces$Piece {

    public StructureNetherBridgePieces$NetherStalkRoom() {}

    public StructureNetherBridgePieces$NetherStalkRoom(int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(i);
        this.field_74885_f = j;
        this.field_74887_e = structureboundingbox;
    }

    @Override
    public void func_74861_a(StructureComponent structurecomponent, List list, Random random) {
        this.func_74963_a((StructureNetherBridgePieces$Start) structurecomponent, list, random, 5, 3, true);
        this.func_74963_a((StructureNetherBridgePieces$Start) structurecomponent, list, random, 5, 11, true);
    }

    public static StructureNetherBridgePieces$NetherStalkRoom func_74977_a(List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, -5, -3, 0, 13, 14, 13, l);

        return func_74964_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureNetherBridgePieces$NetherStalkRoom(i1, random, structureboundingbox, l) : null;
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        this.func_151549_a(world, structureboundingbox, 0, 3, 0, 12, 4, 12, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 0, 5, 0, 12, 13, 12, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 0, 5, 0, 1, 12, 12, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 11, 5, 0, 12, 12, 12, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 2, 5, 11, 4, 12, 12, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 8, 5, 11, 10, 12, 12, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 5, 9, 11, 7, 12, 12, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 2, 5, 0, 4, 12, 1, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 8, 5, 0, 10, 12, 1, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 5, 9, 0, 7, 12, 1, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 2, 11, 2, 10, 12, 10, Blocks.field_150385_bj, Blocks.field_150385_bj, false);

        int i;

        for (i = 1; i <= 11; i += 2) {
            this.func_151549_a(world, structureboundingbox, i, 10, 0, i, 11, 0, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
            this.func_151549_a(world, structureboundingbox, i, 10, 12, i, 11, 12, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
            this.func_151549_a(world, structureboundingbox, 0, 10, i, 0, 11, i, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
            this.func_151549_a(world, structureboundingbox, 12, 10, i, 12, 11, i, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
            this.func_151550_a(world, Blocks.field_150385_bj, 0, i, 13, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150385_bj, 0, i, 13, 12, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150385_bj, 0, 0, 13, i, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150385_bj, 0, 12, 13, i, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150386_bk, 0, i + 1, 13, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150386_bk, 0, i + 1, 13, 12, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150386_bk, 0, 0, 13, i + 1, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150386_bk, 0, 12, 13, i + 1, structureboundingbox);
        }

        this.func_151550_a(world, Blocks.field_150386_bk, 0, 0, 13, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150386_bk, 0, 0, 13, 12, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150386_bk, 0, 0, 13, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150386_bk, 0, 12, 13, 0, structureboundingbox);

        for (i = 3; i <= 9; i += 2) {
            this.func_151549_a(world, structureboundingbox, 1, 7, i, 1, 8, i, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
            this.func_151549_a(world, structureboundingbox, 11, 7, i, 11, 8, i, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
        }

        i = this.func_151555_a(Blocks.field_150387_bl, 3);

        int j;
        int k;
        int l;

        for (k = 0; k <= 6; ++k) {
            j = k + 4;

            for (l = 5; l <= 7; ++l) {
                this.func_151550_a(world, Blocks.field_150387_bl, i, l, 5 + k, j, structureboundingbox);
            }

            if (j >= 5 && j <= 8) {
                this.func_151549_a(world, structureboundingbox, 5, 5, j, 7, k + 4, j, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
            } else if (j >= 9 && j <= 10) {
                this.func_151549_a(world, structureboundingbox, 5, 8, j, 7, k + 4, j, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
            }

            if (k >= 1) {
                this.func_151549_a(world, structureboundingbox, 5, 6 + k, j, 7, 9 + k, j, Blocks.field_150350_a, Blocks.field_150350_a, false);
            }
        }

        for (k = 5; k <= 7; ++k) {
            this.func_151550_a(world, Blocks.field_150387_bl, i, k, 12, 11, structureboundingbox);
        }

        this.func_151549_a(world, structureboundingbox, 5, 6, 7, 5, 7, 7, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
        this.func_151549_a(world, structureboundingbox, 7, 6, 7, 7, 7, 7, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
        this.func_151549_a(world, structureboundingbox, 5, 13, 12, 7, 13, 12, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 2, 5, 2, 3, 5, 3, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 2, 5, 9, 3, 5, 10, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 2, 5, 4, 2, 5, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 9, 5, 2, 10, 5, 3, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 9, 5, 9, 10, 5, 10, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 10, 5, 4, 10, 5, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        k = this.func_151555_a(Blocks.field_150387_bl, 0);
        j = this.func_151555_a(Blocks.field_150387_bl, 1);
        this.func_151550_a(world, Blocks.field_150387_bl, j, 4, 5, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150387_bl, j, 4, 5, 3, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150387_bl, j, 4, 5, 9, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150387_bl, j, 4, 5, 10, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150387_bl, k, 8, 5, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150387_bl, k, 8, 5, 3, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150387_bl, k, 8, 5, 9, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150387_bl, k, 8, 5, 10, structureboundingbox);
        this.func_151549_a(world, structureboundingbox, 3, 4, 4, 4, 4, 8, Blocks.field_150425_aM, Blocks.field_150425_aM, false);
        this.func_151549_a(world, structureboundingbox, 8, 4, 4, 9, 4, 8, Blocks.field_150425_aM, Blocks.field_150425_aM, false);
        this.func_151549_a(world, structureboundingbox, 3, 5, 4, 4, 5, 8, Blocks.field_150388_bm, Blocks.field_150388_bm, false);
        this.func_151549_a(world, structureboundingbox, 8, 5, 4, 9, 5, 8, Blocks.field_150388_bm, Blocks.field_150388_bm, false);
        this.func_151549_a(world, structureboundingbox, 4, 2, 0, 8, 2, 12, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 0, 2, 4, 12, 2, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 4, 0, 0, 8, 1, 3, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 4, 0, 9, 8, 1, 12, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 0, 0, 4, 3, 1, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 9, 0, 4, 12, 1, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);

        int i1;

        for (l = 4; l <= 8; ++l) {
            for (i1 = 0; i1 <= 2; ++i1) {
                this.func_151554_b(world, Blocks.field_150385_bj, 0, l, -1, i1, structureboundingbox);
                this.func_151554_b(world, Blocks.field_150385_bj, 0, l, -1, 12 - i1, structureboundingbox);
            }
        }

        for (l = 0; l <= 2; ++l) {
            for (i1 = 4; i1 <= 8; ++i1) {
                this.func_151554_b(world, Blocks.field_150385_bj, 0, l, -1, i1, structureboundingbox);
                this.func_151554_b(world, Blocks.field_150385_bj, 0, 12 - l, -1, i1, structureboundingbox);
            }
        }

        return true;
    }
}
