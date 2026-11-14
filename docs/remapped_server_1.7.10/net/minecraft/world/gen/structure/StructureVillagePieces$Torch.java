package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureVillagePieces$Torch extends StructureVillagePieces$Village {

    public StructureVillagePieces$Torch() {}

    public StructureVillagePieces$Torch(StructureVillagePieces$Start structurevillagepieces$start, int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(structurevillagepieces$start, i);
        this.field_74885_f = j;
        this.field_74887_e = structureboundingbox;
    }

    public static StructureBoundingBox func_74904_a(StructureVillagePieces$Start structurevillagepieces$start, List list, Random random, int i, int j, int k, int l) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, 0, 0, 0, 3, 4, 2, l);

        return StructureComponent.func_74883_a(list, structureboundingbox) != null ? null : structureboundingbox;
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(world, structureboundingbox);
            if (this.field_143015_k < 0) {
                return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 4 - 1, 0);
        }

        this.func_151549_a(world, structureboundingbox, 0, 0, 0, 2, 3, 1, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151550_a(world, Blocks.field_150422_aJ, 0, 1, 0, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150422_aJ, 0, 1, 1, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150422_aJ, 0, 1, 2, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150325_L, 15, 1, 3, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150478_aa, 0, 0, 3, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150478_aa, 0, 1, 3, 1, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150478_aa, 0, 2, 3, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150478_aa, 0, 1, 3, -1, structureboundingbox);
        return true;
    }
}
