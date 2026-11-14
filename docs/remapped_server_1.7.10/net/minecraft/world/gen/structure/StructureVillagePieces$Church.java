package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureVillagePieces$Church extends StructureVillagePieces$Village {

    public StructureVillagePieces$Church() {}

    public StructureVillagePieces$Church(StructureVillagePieces$Start structurevillagepieces$start, int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(structurevillagepieces$start, i);
        this.field_74885_f = j;
        this.field_74887_e = structureboundingbox;
    }

    public static StructureVillagePieces$Church func_74919_a(StructureVillagePieces$Start structurevillagepieces$start, List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, 0, 0, 0, 5, 12, 9, l);

        return func_74895_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureVillagePieces$Church(structurevillagepieces$start, i1, random, structureboundingbox, l) : null;
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(world, structureboundingbox);
            if (this.field_143015_k < 0) {
                return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 12 - 1, 0);
        }

        this.func_151549_a(world, structureboundingbox, 1, 1, 1, 3, 3, 7, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 1, 5, 1, 3, 9, 3, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 1, 0, 0, 3, 0, 8, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 1, 1, 0, 3, 10, 0, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 0, 1, 1, 0, 10, 3, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 4, 1, 1, 4, 10, 3, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 0, 0, 4, 0, 4, 7, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 4, 0, 4, 4, 4, 7, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 1, 1, 8, 3, 4, 8, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 1, 5, 4, 3, 10, 4, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 1, 5, 5, 3, 5, 7, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 0, 9, 0, 4, 9, 4, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 0, 4, 0, 4, 4, 4, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151550_a(world, Blocks.field_150347_e, 0, 0, 11, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150347_e, 0, 4, 11, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150347_e, 0, 2, 11, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150347_e, 0, 2, 11, 4, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150347_e, 0, 1, 1, 6, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150347_e, 0, 1, 1, 7, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150347_e, 0, 2, 1, 7, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150347_e, 0, 3, 1, 6, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150347_e, 0, 3, 1, 7, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150446_ar, this.func_151555_a(Blocks.field_150446_ar, 3), 1, 1, 5, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150446_ar, this.func_151555_a(Blocks.field_150446_ar, 3), 2, 1, 6, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150446_ar, this.func_151555_a(Blocks.field_150446_ar, 3), 3, 1, 5, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150446_ar, this.func_151555_a(Blocks.field_150446_ar, 1), 1, 2, 7, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150446_ar, this.func_151555_a(Blocks.field_150446_ar, 0), 3, 2, 7, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 0, 2, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 0, 3, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 4, 2, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 4, 3, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 0, 6, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 0, 7, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 4, 6, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 4, 7, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 2, 6, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 2, 7, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 2, 6, 4, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 2, 7, 4, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 0, 3, 6, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 4, 3, 6, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 2, 3, 8, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150478_aa, 0, 2, 4, 7, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150478_aa, 0, 1, 4, 6, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150478_aa, 0, 3, 4, 6, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150478_aa, 0, 2, 4, 5, structureboundingbox);
        int i = this.func_151555_a(Blocks.field_150468_ap, 4);

        int j;

        for (j = 1; j <= 9; ++j) {
            this.func_151550_a(world, Blocks.field_150468_ap, i, 3, j, 3, structureboundingbox);
        }

        this.func_151550_a(world, Blocks.field_150350_a, 0, 2, 1, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150350_a, 0, 2, 2, 0, structureboundingbox);
        this.func_74881_a(world, structureboundingbox, random, 2, 1, 0, this.func_151555_a(Blocks.field_150466_ao, 1));
        if (this.func_151548_a(world, 2, 0, -1, structureboundingbox).func_149688_o() == Material.field_151579_a && this.func_151548_a(world, 2, -1, -1, structureboundingbox).func_149688_o() != Material.field_151579_a) {
            this.func_151550_a(world, Blocks.field_150446_ar, this.func_151555_a(Blocks.field_150446_ar, 3), 2, 0, -1, structureboundingbox);
        }

        for (j = 0; j < 9; ++j) {
            for (int k = 0; k < 5; ++k) {
                this.func_74871_b(world, k, 12, j, structureboundingbox);
                this.func_151554_b(world, Blocks.field_150347_e, 0, k, -1, j, structureboundingbox);
            }
        }

        this.func_74893_a(world, structureboundingbox, 2, 1, 2, 1);
        return true;
    }

    @Override
    protected int func_74888_b(int i) {
        return 2;
    }
}
