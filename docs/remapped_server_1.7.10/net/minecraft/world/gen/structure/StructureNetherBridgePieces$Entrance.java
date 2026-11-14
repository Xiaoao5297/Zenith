package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureNetherBridgePieces$Entrance extends StructureNetherBridgePieces$Piece {

    public StructureNetherBridgePieces$Entrance() {}

    public StructureNetherBridgePieces$Entrance(int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(i);
        this.field_74885_f = j;
        this.field_74887_e = structureboundingbox;
    }

    @Override
    public void func_74861_a(StructureComponent structurecomponent, List list, Random random) {
        this.func_74963_a((StructureNetherBridgePieces$Start) structurecomponent, list, random, 5, 3, true);
    }

    public static StructureNetherBridgePieces$Entrance func_74984_a(List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, -5, -3, 0, 13, 14, 13, l);

        return func_74964_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureNetherBridgePieces$Entrance(i1, random, structureboundingbox, l) : null;
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
        this.func_151549_a(world, structureboundingbox, 5, 8, 0, 7, 8, 0, Blocks.field_150386_bk, Blocks.field_150386_bk, false);

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

        this.func_151549_a(world, structureboundingbox, 4, 2, 0, 8, 2, 12, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 0, 2, 4, 12, 2, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 4, 0, 0, 8, 1, 3, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 4, 0, 9, 8, 1, 12, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 0, 0, 4, 3, 1, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 9, 0, 4, 12, 1, 8, Blocks.field_150385_bj, Blocks.field_150385_bj, false);

        int j;

        for (i = 4; i <= 8; ++i) {
            for (j = 0; j <= 2; ++j) {
                this.func_151554_b(world, Blocks.field_150385_bj, 0, i, -1, j, structureboundingbox);
                this.func_151554_b(world, Blocks.field_150385_bj, 0, i, -1, 12 - j, structureboundingbox);
            }
        }

        for (i = 0; i <= 2; ++i) {
            for (j = 4; j <= 8; ++j) {
                this.func_151554_b(world, Blocks.field_150385_bj, 0, i, -1, j, structureboundingbox);
                this.func_151554_b(world, Blocks.field_150385_bj, 0, 12 - i, -1, j, structureboundingbox);
            }
        }

        this.func_151549_a(world, structureboundingbox, 5, 5, 5, 7, 5, 7, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 6, 1, 6, 6, 4, 6, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151550_a(world, Blocks.field_150385_bj, 0, 6, 0, 6, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150356_k, 0, 6, 5, 6, structureboundingbox);
        i = this.func_74865_a(6, 6);
        j = this.func_74862_a(5);
        int k = this.func_74873_b(6, 6);

        if (structureboundingbox.func_78890_b(i, j, k)) {
            world.field_72999_e = true;
            Blocks.field_150356_k.func_149674_a(world, i, j, k, random);
            world.field_72999_e = false;
        }

        return true;
    }
}
