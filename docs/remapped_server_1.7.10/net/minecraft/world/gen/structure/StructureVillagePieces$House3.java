package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureVillagePieces$House3 extends StructureVillagePieces$Village {

    public StructureVillagePieces$House3() {}

    public StructureVillagePieces$House3(StructureVillagePieces$Start structurevillagepieces$start, int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(structurevillagepieces$start, i);
        this.field_74885_f = j;
        this.field_74887_e = structureboundingbox;
    }

    public static StructureVillagePieces$House3 func_74921_a(StructureVillagePieces$Start structurevillagepieces$start, List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, 0, 0, 0, 9, 7, 12, l);

        return func_74895_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureVillagePieces$House3(structurevillagepieces$start, i1, random, structureboundingbox, l) : null;
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(world, structureboundingbox);
            if (this.field_143015_k < 0) {
                return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 7 - 1, 0);
        }

        this.func_151549_a(world, structureboundingbox, 1, 1, 1, 7, 4, 4, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 2, 1, 6, 8, 4, 10, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 2, 0, 5, 8, 0, 10, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151549_a(world, structureboundingbox, 1, 0, 1, 7, 0, 4, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151549_a(world, structureboundingbox, 0, 0, 0, 0, 3, 5, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 8, 0, 0, 8, 3, 10, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 1, 0, 0, 7, 2, 0, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 1, 0, 5, 2, 1, 5, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 2, 0, 6, 2, 3, 10, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 3, 0, 10, 7, 3, 10, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 1, 2, 0, 7, 3, 0, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151549_a(world, structureboundingbox, 1, 2, 5, 2, 3, 5, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151549_a(world, structureboundingbox, 0, 4, 1, 8, 4, 1, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151549_a(world, structureboundingbox, 0, 4, 4, 3, 4, 4, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151549_a(world, structureboundingbox, 0, 5, 2, 8, 5, 3, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151550_a(world, Blocks.field_150344_f, 0, 0, 4, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150344_f, 0, 0, 4, 3, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150344_f, 0, 8, 4, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150344_f, 0, 8, 4, 3, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150344_f, 0, 8, 4, 4, structureboundingbox);
        int i = this.func_151555_a(Blocks.field_150476_ad, 3);
        int j = this.func_151555_a(Blocks.field_150476_ad, 2);

        int k;
        int l;

        for (l = -1; l <= 2; ++l) {
            for (k = 0; k <= 8; ++k) {
                this.func_151550_a(world, Blocks.field_150476_ad, i, k, 4 + l, l, structureboundingbox);
                if ((l > -1 || k <= 1) && (l > 0 || k <= 3) && (l > 1 || k <= 4 || k >= 6)) {
                    this.func_151550_a(world, Blocks.field_150476_ad, j, k, 4 + l, 5 - l, structureboundingbox);
                }
            }
        }

        this.func_151549_a(world, structureboundingbox, 3, 4, 5, 3, 4, 10, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151549_a(world, structureboundingbox, 7, 4, 2, 7, 4, 10, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151549_a(world, structureboundingbox, 4, 5, 4, 4, 5, 10, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151549_a(world, structureboundingbox, 6, 5, 4, 6, 5, 10, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151549_a(world, structureboundingbox, 5, 6, 3, 5, 6, 10, Blocks.field_150344_f, Blocks.field_150344_f, false);
        l = this.func_151555_a(Blocks.field_150476_ad, 0);

        int i1;

        for (k = 4; k >= 1; --k) {
            this.func_151550_a(world, Blocks.field_150344_f, 0, k, 2 + k, 7 - k, structureboundingbox);

            for (i1 = 8 - k; i1 <= 10; ++i1) {
                this.func_151550_a(world, Blocks.field_150476_ad, l, k, 2 + k, i1, structureboundingbox);
            }
        }

        k = this.func_151555_a(Blocks.field_150476_ad, 1);
        this.func_151550_a(world, Blocks.field_150344_f, 0, 6, 6, 3, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150344_f, 0, 7, 5, 4, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150476_ad, k, 6, 6, 4, structureboundingbox);

        int j1;

        for (i1 = 6; i1 <= 8; ++i1) {
            for (j1 = 5; j1 <= 10; ++j1) {
                this.func_151550_a(world, Blocks.field_150476_ad, k, i1, 12 - i1, j1, structureboundingbox);
            }
        }

        this.func_151550_a(world, Blocks.field_150364_r, 0, 0, 2, 1, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150364_r, 0, 0, 2, 4, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 0, 2, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 0, 2, 3, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150364_r, 0, 4, 2, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 5, 2, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150364_r, 0, 6, 2, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150364_r, 0, 8, 2, 1, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 8, 2, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 8, 2, 3, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150364_r, 0, 8, 2, 4, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150344_f, 0, 8, 2, 5, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150364_r, 0, 8, 2, 6, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 8, 2, 7, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 8, 2, 8, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150364_r, 0, 8, 2, 9, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150364_r, 0, 2, 2, 6, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 2, 2, 7, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 2, 2, 8, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150364_r, 0, 2, 2, 9, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150364_r, 0, 4, 4, 10, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 5, 4, 10, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150364_r, 0, 6, 4, 10, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150344_f, 0, 5, 5, 10, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150350_a, 0, 2, 1, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150350_a, 0, 2, 2, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150478_aa, 0, 2, 3, 1, structureboundingbox);
        this.func_74881_a(world, structureboundingbox, random, 2, 1, 0, this.func_151555_a(Blocks.field_150466_ao, 1));
        this.func_151549_a(world, structureboundingbox, 1, 0, -1, 3, 2, -1, Blocks.field_150350_a, Blocks.field_150350_a, false);
        if (this.func_151548_a(world, 2, 0, -1, structureboundingbox).func_149688_o() == Material.field_151579_a && this.func_151548_a(world, 2, -1, -1, structureboundingbox).func_149688_o() != Material.field_151579_a) {
            this.func_151550_a(world, Blocks.field_150446_ar, this.func_151555_a(Blocks.field_150446_ar, 3), 2, 0, -1, structureboundingbox);
        }

        for (i1 = 0; i1 < 5; ++i1) {
            for (j1 = 0; j1 < 9; ++j1) {
                this.func_74871_b(world, j1, 7, i1, structureboundingbox);
                this.func_151554_b(world, Blocks.field_150347_e, 0, j1, -1, i1, structureboundingbox);
            }
        }

        for (i1 = 5; i1 < 11; ++i1) {
            for (j1 = 2; j1 < 9; ++j1) {
                this.func_74871_b(world, j1, 7, i1, structureboundingbox);
                this.func_151554_b(world, Blocks.field_150347_e, 0, j1, -1, i1, structureboundingbox);
            }
        }

        this.func_74893_a(world, structureboundingbox, 4, 1, 2, 2);
        return true;
    }
}
