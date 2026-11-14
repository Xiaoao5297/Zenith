package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class StructureStrongholdPieces$Crossing extends StructureStrongholdPieces$Stronghold {

    private boolean field_74996_b;
    private boolean field_74997_c;
    private boolean field_74995_d;
    private boolean field_74999_h;

    public StructureStrongholdPieces$Crossing() {}

    public StructureStrongholdPieces$Crossing(int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(i);
        this.field_74885_f = j;
        this.field_143013_d = this.func_74988_a(random);
        this.field_74887_e = structureboundingbox;
        this.field_74996_b = random.nextBoolean();
        this.field_74997_c = random.nextBoolean();
        this.field_74995_d = random.nextBoolean();
        this.field_74999_h = random.nextInt(3) > 0;
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        super.func_143012_a(nbttagcompound);
        nbttagcompound.func_74757_a("leftLow", this.field_74996_b);
        nbttagcompound.func_74757_a("leftHigh", this.field_74997_c);
        nbttagcompound.func_74757_a("rightLow", this.field_74995_d);
        nbttagcompound.func_74757_a("rightHigh", this.field_74999_h);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        super.func_143011_b(nbttagcompound);
        this.field_74996_b = nbttagcompound.func_74767_n("leftLow");
        this.field_74997_c = nbttagcompound.func_74767_n("leftHigh");
        this.field_74995_d = nbttagcompound.func_74767_n("rightLow");
        this.field_74999_h = nbttagcompound.func_74767_n("rightHigh");
    }

    @Override
    public void func_74861_a(StructureComponent structurecomponent, List list, Random random) {
        int i = 3;
        int j = 5;

        if (this.field_74885_f == 1 || this.field_74885_f == 2) {
            i = 8 - i;
            j = 8 - j;
        }

        this.func_74986_a((StructureStrongholdPieces$Stairs2) structurecomponent, list, random, 5, 1);
        if (this.field_74996_b) {
            this.func_74989_b((StructureStrongholdPieces$Stairs2) structurecomponent, list, random, i, 1);
        }

        if (this.field_74997_c) {
            this.func_74989_b((StructureStrongholdPieces$Stairs2) structurecomponent, list, random, j, 7);
        }

        if (this.field_74995_d) {
            this.func_74987_c((StructureStrongholdPieces$Stairs2) structurecomponent, list, random, i, 1);
        }

        if (this.field_74999_h) {
            this.func_74987_c((StructureStrongholdPieces$Stairs2) structurecomponent, list, random, j, 7);
        }

    }

    public static StructureStrongholdPieces$Crossing func_74994_a(List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, -4, -3, 0, 10, 9, 11, l);

        return func_74991_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureStrongholdPieces$Crossing(i1, random, structureboundingbox, l) : null;
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        if (this.func_74860_a(world, structureboundingbox)) {
            return false;
        } else {
            this.func_74882_a(world, structureboundingbox, 0, 0, 0, 9, 8, 10, true, random, StructureStrongholdPieces.access$200());
            this.func_74990_a(world, random, structureboundingbox, this.field_143013_d, 4, 3, 0);
            if (this.field_74996_b) {
                this.func_151549_a(world, structureboundingbox, 0, 3, 1, 0, 5, 3, Blocks.field_150350_a, Blocks.field_150350_a, false);
            }

            if (this.field_74995_d) {
                this.func_151549_a(world, structureboundingbox, 9, 3, 1, 9, 5, 3, Blocks.field_150350_a, Blocks.field_150350_a, false);
            }

            if (this.field_74997_c) {
                this.func_151549_a(world, structureboundingbox, 0, 5, 7, 0, 7, 9, Blocks.field_150350_a, Blocks.field_150350_a, false);
            }

            if (this.field_74999_h) {
                this.func_151549_a(world, structureboundingbox, 9, 5, 7, 9, 7, 9, Blocks.field_150350_a, Blocks.field_150350_a, false);
            }

            this.func_151549_a(world, structureboundingbox, 5, 1, 10, 7, 3, 10, Blocks.field_150350_a, Blocks.field_150350_a, false);
            this.func_74882_a(world, structureboundingbox, 1, 2, 1, 8, 2, 6, false, random, StructureStrongholdPieces.access$200());
            this.func_74882_a(world, structureboundingbox, 4, 1, 5, 4, 4, 9, false, random, StructureStrongholdPieces.access$200());
            this.func_74882_a(world, structureboundingbox, 8, 1, 5, 8, 4, 9, false, random, StructureStrongholdPieces.access$200());
            this.func_74882_a(world, structureboundingbox, 1, 4, 7, 3, 4, 9, false, random, StructureStrongholdPieces.access$200());
            this.func_74882_a(world, structureboundingbox, 1, 3, 5, 3, 3, 6, false, random, StructureStrongholdPieces.access$200());
            this.func_151549_a(world, structureboundingbox, 1, 3, 4, 3, 3, 4, Blocks.field_150333_U, Blocks.field_150333_U, false);
            this.func_151549_a(world, structureboundingbox, 1, 4, 6, 3, 4, 6, Blocks.field_150333_U, Blocks.field_150333_U, false);
            this.func_74882_a(world, structureboundingbox, 5, 1, 7, 7, 1, 8, false, random, StructureStrongholdPieces.access$200());
            this.func_151549_a(world, structureboundingbox, 5, 1, 9, 7, 1, 9, Blocks.field_150333_U, Blocks.field_150333_U, false);
            this.func_151549_a(world, structureboundingbox, 5, 2, 7, 7, 2, 7, Blocks.field_150333_U, Blocks.field_150333_U, false);
            this.func_151549_a(world, structureboundingbox, 4, 5, 7, 4, 5, 9, Blocks.field_150333_U, Blocks.field_150333_U, false);
            this.func_151549_a(world, structureboundingbox, 8, 5, 7, 8, 5, 9, Blocks.field_150333_U, Blocks.field_150333_U, false);
            this.func_151549_a(world, structureboundingbox, 5, 5, 7, 7, 5, 9, Blocks.field_150334_T, Blocks.field_150334_T, false);
            this.func_151550_a(world, Blocks.field_150478_aa, 0, 6, 5, 6, structureboundingbox);
            return true;
        }
    }
}
