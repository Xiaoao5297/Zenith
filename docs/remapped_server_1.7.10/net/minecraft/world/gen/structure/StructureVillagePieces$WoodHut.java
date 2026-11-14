package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class StructureVillagePieces$WoodHut extends StructureVillagePieces$Village {

    private boolean field_74909_b;
    private int field_74910_c;

    public StructureVillagePieces$WoodHut() {}

    public StructureVillagePieces$WoodHut(StructureVillagePieces$Start structurevillagepieces$start, int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(structurevillagepieces$start, i);
        this.field_74885_f = j;
        this.field_74887_e = structureboundingbox;
        this.field_74909_b = random.nextBoolean();
        this.field_74910_c = random.nextInt(3);
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        super.func_143012_a(nbttagcompound);
        nbttagcompound.func_74768_a("T", this.field_74910_c);
        nbttagcompound.func_74757_a("C", this.field_74909_b);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        super.func_143011_b(nbttagcompound);
        this.field_74910_c = nbttagcompound.func_74762_e("T");
        this.field_74909_b = nbttagcompound.func_74767_n("C");
    }

    public static StructureVillagePieces$WoodHut func_74908_a(StructureVillagePieces$Start structurevillagepieces$start, List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, 0, 0, 0, 4, 6, 5, l);

        return func_74895_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureVillagePieces$WoodHut(structurevillagepieces$start, i1, random, structureboundingbox, l) : null;
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

        this.func_151549_a(world, structureboundingbox, 1, 1, 1, 3, 5, 4, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 0, 0, 0, 3, 0, 4, Blocks.field_150347_e, Blocks.field_150347_e, false);
        this.func_151549_a(world, structureboundingbox, 1, 0, 1, 2, 0, 3, Blocks.field_150346_d, Blocks.field_150346_d, false);
        if (this.field_74909_b) {
            this.func_151549_a(world, structureboundingbox, 1, 4, 1, 2, 4, 3, Blocks.field_150364_r, Blocks.field_150364_r, false);
        } else {
            this.func_151549_a(world, structureboundingbox, 1, 5, 1, 2, 5, 3, Blocks.field_150364_r, Blocks.field_150364_r, false);
        }

        this.func_151550_a(world, Blocks.field_150364_r, 0, 1, 4, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150364_r, 0, 2, 4, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150364_r, 0, 1, 4, 4, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150364_r, 0, 2, 4, 4, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150364_r, 0, 0, 4, 1, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150364_r, 0, 0, 4, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150364_r, 0, 0, 4, 3, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150364_r, 0, 3, 4, 1, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150364_r, 0, 3, 4, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150364_r, 0, 3, 4, 3, structureboundingbox);
        this.func_151549_a(world, structureboundingbox, 0, 1, 0, 0, 3, 0, Blocks.field_150364_r, Blocks.field_150364_r, false);
        this.func_151549_a(world, structureboundingbox, 3, 1, 0, 3, 3, 0, Blocks.field_150364_r, Blocks.field_150364_r, false);
        this.func_151549_a(world, structureboundingbox, 0, 1, 4, 0, 3, 4, Blocks.field_150364_r, Blocks.field_150364_r, false);
        this.func_151549_a(world, structureboundingbox, 3, 1, 4, 3, 3, 4, Blocks.field_150364_r, Blocks.field_150364_r, false);
        this.func_151549_a(world, structureboundingbox, 0, 1, 1, 0, 3, 3, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151549_a(world, structureboundingbox, 3, 1, 1, 3, 3, 3, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151549_a(world, structureboundingbox, 1, 1, 0, 2, 3, 0, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151549_a(world, structureboundingbox, 1, 1, 4, 2, 3, 4, Blocks.field_150344_f, Blocks.field_150344_f, false);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 0, 2, 2, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150410_aZ, 0, 3, 2, 2, structureboundingbox);
        if (this.field_74910_c > 0) {
            this.func_151550_a(world, Blocks.field_150422_aJ, 0, this.field_74910_c, 1, 3, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150452_aw, 0, this.field_74910_c, 2, 3, structureboundingbox);
        }

        this.func_151550_a(world, Blocks.field_150350_a, 0, 1, 1, 0, structureboundingbox);
        this.func_151550_a(world, Blocks.field_150350_a, 0, 1, 2, 0, structureboundingbox);
        this.func_74881_a(world, structureboundingbox, random, 1, 1, 0, this.func_151555_a(Blocks.field_150466_ao, 1));
        if (this.func_151548_a(world, 1, 0, -1, structureboundingbox).func_149688_o() == Material.field_151579_a && this.func_151548_a(world, 1, -1, -1, structureboundingbox).func_149688_o() != Material.field_151579_a) {
            this.func_151550_a(world, Blocks.field_150446_ar, this.func_151555_a(Blocks.field_150446_ar, 3), 1, 0, -1, structureboundingbox);
        }

        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 4; ++j) {
                this.func_74871_b(world, j, 6, i, structureboundingbox);
                this.func_151554_b(world, Blocks.field_150347_e, 0, j, -1, i, structureboundingbox);
            }
        }

        this.func_74893_a(world, structureboundingbox, 1, 1, 2, 1);
        return true;
    }
}
