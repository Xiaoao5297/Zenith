package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureVillagePieces$House1 extends StructureVillagePieces$Village {

    public StructureVillagePieces$House1() {}

    public StructureVillagePieces$House1(StructureVillagePieces$Start structurevillagepieces$start, int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(structurevillagepieces$start, i);
        this.field_74885_f = j;
        this.field_74887_e = structureboundingbox;
    }

    public static StructureVillagePieces$House1 func_74898_a(StructureVillagePieces$Start structurevillagepieces$start, List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, 0, 0, 0, 9, 9, 6, l);

        return func_74895_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureVillagePieces$House1(structurevillagepieces$start, i1, random, structureboundingbox, l) : null;
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(world, structureboundingbox);
            if (this.field_143015_k < 0) {
                return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 9 - 1, 0);
        }

        this.func_151549_a(world, structureboundingbox, 1, 1, 1, 7, 5, 4, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 0, 0, 0, 8, 0, 5, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 0, 5, 0, 8, 5, 5, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 0, 6, 1, 8, 6, 4, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 0, 7, 2, 8, 7, 3, Blocks.field_150347_e, Blocks.field_150347_e, false);
        int i = this.func_151555_a(Blocks.field_150476_ad, 3);
        int j = this.func_151555_a(Blocks.field_150476_ad, 2);

        int k;
        int l;

        for (l = -1; l <= 2; ++l) {
            for (k = 0; k <= 8; ++k) {
                this.func_151550_a(world, Blocks.field_150476_ad, i, k, 6 + l, l, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150476_ad, j, k, 6 + l, 5 - l, structureboundingbox);
            }
        }

        this.func_151549_a(world, structureboundingbox, 0, 1, 0, 0, 1, 5, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 1, 1, 5, 8, 1, 5, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 8, 1, 0, 8, 1, 4, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 2, 1, 0, 7, 1, 0, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 0, 2, 0, 0, 4, 0, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 0, 2, 5, 0, 4, 5, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 8, 2, 5, 8, 4, 5, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 8, 2, 0, 8, 4, 0, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 0, 2, 1, 0, 4, 4, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151549_a(world, structureboundingbox, 1, 2, 5, 7, 4, 5, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151549_a(world, structureboundingbox, 8, 2, 1, 8, 4, 4, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151549_a(world, structureboundingbox, 1, 2, 0, 7, 4, 0, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 4, 2, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 5, 2, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 6, 2, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 4, 3, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 5, 3, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 6, 3, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 0, 2, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 0, 2, 3, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 0, 3, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 0, 3, 3, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 8, 2, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 8, 2, 3, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 8, 3, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 8, 3, 3, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 2, 2, 5, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 3, 2, 5, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 5, 2, 5, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 6, 2, 5, structureboundingbox);
        this.func_151549_a(world, structureboundingbox, 1, 4, 1, 7, 4, 1, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151549_a(world, structureboundingbox, 1, 4, 4, 7, 4, 4, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151549_a(world, structureboundingbox, 1, 3, 4, 7, 3, 4, Blocks.field_150342_X, Blocks.field_150342_X, false);
        this.func_151550_a(world, Blocks.field_150344_f, 0, 7, 1, 4, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150476_ad, this.func_151555_a(Blocks.field_150476_ad, 0), 7, 1, 3, structureboundingbox);
        l = this.func_151555_a(Blocks.field_150476_ad, 3);
        this.func_151550_a(world, Blocks.field_150476_ad, l, 6, 1, 4, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150476_ad, l, 5, 1, 4, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150476_ad, l, 4, 1, 4, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150476_ad, l, 3, 1, 4, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150422_aJ, 0, 6, 1, 3, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150452_aw, 0, 6, 2, 3, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150422_aJ, 0, 4, 1, 3, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150452_aw, 0, 4, 2, 3, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150462_ai, 0, 7, 1, 1, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150350_a, 0, 1, 1, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150350_a, 0, 1, 2, 0, structureboundingbox);
        this.func_74881_a(world, structureboundingbox, random, 1, 1, 0, this.func_151555_a(Blocks.field_150466_ao, 1));
        if (this.func_151548_a(world, 1, 0, -1, structureboundingbox).func_149688_o() == Material.field_151579_a && this.func_151548_a(world, 1, -1, -1, structureboundingbox).func_149688_o() != Material.field_151579_a) {
            this.func_151550_a(world, Blocks.field_150446_ar, this.func_151555_a(Blocks.field_150446_ar, 3), 1, 0, -1, structureboundingbox);
        }

        for (k = 0; k < 6; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.func_74871_b(world, i1, 9, k, structureboundingbox);
                this.func_151554_b(world, Blocks.field_150347_e, 0, i1, -1, k, structureboundingbox);
            }
        }

        this.func_74893_a(world, structureboundingbox, 2, 1, 2, 1);
        return true;
    }

    @Override
    protected int func_74888_b(int i) {
        return 1;
    }
}
