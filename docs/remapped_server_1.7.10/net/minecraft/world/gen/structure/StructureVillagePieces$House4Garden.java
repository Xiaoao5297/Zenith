package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class StructureVillagePieces$House4Garden extends StructureVillagePieces$Village {

    private boolean field_74913_b;

    public StructureVillagePieces$House4Garden() {}

    public StructureVillagePieces$House4Garden(StructureVillagePieces$Start structurevillagepieces$start, int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(structurevillagepieces$start, i);
        this.field_74885_f = j;
        this.field_74887_e = structureboundingbox;
        this.field_74913_b = random.nextBoolean();
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        super.func_143012_a(nbttagcompound);
        nbttagcompound.func_74757_a("Terrace", this.field_74913_b);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        super.func_143011_b(nbttagcompound);
        this.field_74913_b = nbttagcompound.func_74767_n("Terrace");
    }

    public static StructureVillagePieces$House4Garden func_74912_a(StructureVillagePieces$Start structurevillagepieces$start, List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, 0, 0, 0, 5, 6, 5, l);

        return StructureComponent.func_74883_a(list, structureboundingbox) != null ? null : new StructureVillagePieces$House4Garden(structurevillagepieces$start, i1, random, structureboundingbox, l);
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(world, structureboundingbox);
            if (this.field_143015_k < 0) {
                return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 6 - 1, 0);
        }

        this.func_151549_a(world, structureboundingbox, 0, 0, 0, 4, 0, 4, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 0, 4, 0, 4, 4, 4, Blocks.field_150364_r, Blocks.field_150364_r, false);
        this.func_151549_a(world, structureboundingbox, 1, 4, 1, 3, 4, 3, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151550_a(world, Blocks.field_150347_e, 0, 0, 1, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150347_e, 0, 0, 2, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150347_e, 0, 0, 3, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150347_e, 0, 4, 1, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150347_e, 0, 4, 2, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150347_e, 0, 4, 3, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150347_e, 0, 0, 1, 4, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150347_e, 0, 0, 2, 4, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150347_e, 0, 0, 3, 4, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150347_e, 0, 4, 1, 4, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150347_e, 0, 4, 2, 4, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150347_e, 0, 4, 3, 4, structureboundingbox);
        this.func_151549_a(world, structureboundingbox, 0, 1, 1, 0, 3, 3, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151549_a(world, structureboundingbox, 4, 1, 1, 4, 3, 3, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151549_a(world, structureboundingbox, 1, 1, 4, 3, 3, 4, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 0, 2, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 2, 2, 4, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 4, 2, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150344_f, 0, 1, 1, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150344_f, 0, 1, 2, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150344_f, 0, 1, 3, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150344_f, 0, 2, 3, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150344_f, 0, 3, 3, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150344_f, 0, 3, 2, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150344_f, 0, 3, 1, 0, structureboundingbox);
        if (this.func_151548_a(world, 2, 0, -1, structureboundingbox).func_149688_o() == Material.field_151579_a && this.func_151548_a(world, 2, -1, -1, structureboundingbox).func_149688_o() != Material.field_151579_a) {
            this.func_151550_a(world, Blocks.field_150446_ar, this.func_151555_a(Blocks.field_150446_ar, 3), 2, 0, -1, structureboundingbox);
        }

        this.func_151549_a(world, structureboundingbox, 1, 1, 1, 3, 3, 3, Blocks.field_150350_a, Blocks.field_150350_a, false);
        if (this.field_74913_b) {
            this.func_151550_a(world, Blocks.field_150422_aJ, 0, 0, 5, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150422_aJ, 0, 1, 5, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150422_aJ, 0, 2, 5, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150422_aJ, 0, 3, 5, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150422_aJ, 0, 4, 5, 0, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150422_aJ, 0, 0, 5, 4, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150422_aJ, 0, 1, 5, 4, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150422_aJ, 0, 2, 5, 4, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150422_aJ, 0, 3, 5, 4, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150422_aJ, 0, 4, 5, 4, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150422_aJ, 0, 4, 5, 1, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150422_aJ, 0, 4, 5, 2, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150422_aJ, 0, 4, 5, 3, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150422_aJ, 0, 0, 5, 1, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150422_aJ, 0, 0, 5, 2, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150422_aJ, 0, 0, 5, 3, structureboundingbox);
        }

        int i;

        if (this.field_74913_b) {
            i = this.func_151555_a(Blocks.field_150468_ap, 3);
            this.func_151550_a(world, Blocks.field_150468_ap, i, 3, 1, 3, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150468_ap, i, 3, 2, 3, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150468_ap, i, 3, 3, 3, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150468_ap, i, 3, 4, 3, structureboundingbox);
        }

        this.func_151550_a(world, Blocks.field_150478_aa, 0, 2, 3, 1, structureboundingbox);

        for (i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                this.func_74871_b(world, j, 6, i, structureboundingbox);
                this.func_151554_b(world, Blocks.field_150347_e, 0, j, -1, i, structureboundingbox);
            }
        }

        this.func_74893_a(world, structureboundingbox, 1, 1, 2, 1);
        return true;
    }
}
