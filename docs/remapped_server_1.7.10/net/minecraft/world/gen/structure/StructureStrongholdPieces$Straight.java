package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class StructureStrongholdPieces$Straight extends StructureStrongholdPieces$Stronghold {

    private boolean field_75019_b;
    private boolean field_75020_c;

    public StructureStrongholdPieces$Straight() {}

    public StructureStrongholdPieces$Straight(int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(i);
        this.field_74885_f = j;
        this.field_143013_d = this.func_74988_a(random);
        this.field_74887_e = structureboundingbox;
        this.field_75019_b = random.nextInt(2) == 0;
        this.field_75020_c = random.nextInt(2) == 0;
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        super.func_143012_a(nbttagcompound);
        nbttagcompound.func_74757_a("Left", this.field_75019_b);
        nbttagcompound.func_74757_a("Right", this.field_75020_c);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        super.func_143011_b(nbttagcompound);
        this.field_75019_b = nbttagcompound.func_74767_n("Left");
        this.field_75020_c = nbttagcompound.func_74767_n("Right");
    }

    @Override
    public void func_74861_a(StructureComponent structurecomponent, List list, Random random) {
        this.func_74986_a((StructureStrongholdPieces$Stairs2) structurecomponent, list, random, 1, 1);
        if (this.field_75019_b) {
            this.func_74989_b((StructureStrongholdPieces$Stairs2) structurecomponent, list, random, 1, 2);
        }

        if (this.field_75020_c) {
            this.func_74987_c((StructureStrongholdPieces$Stairs2) structurecomponent, list, random, 1, 2);
        }

    }

    public static StructureStrongholdPieces$Straight func_75018_a(List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, -1, -1, 0, 5, 5, 7, l);

        return func_74991_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureStrongholdPieces$Straight(i1, random, structureboundingbox, l) : null;
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        if (this.func_74860_a(world, structureboundingbox)) {
            return false;
        } else {
            this.func_74882_a(world, structureboundingbox, 0, 0, 0, 4, 4, 6, true, random, StructureStrongholdPieces.access$200());
            this.func_74990_a(world, random, structureboundingbox, this.field_143013_d, 1, 1, 0);
            this.func_74990_a(world, random, structureboundingbox, StructureStrongholdPieces$Stronghold$Door.OPENING, 1, 1, 6);
            this.func_151552_a(world, structureboundingbox, random, 0.1F, 1, 2, 1, Blocks.field_150478_aa, 0);
            this.func_151552_a(world, structureboundingbox, random, 0.1F, 3, 2, 1, Blocks.field_150478_aa, 0);
            this.func_151552_a(world, structureboundingbox, random, 0.1F, 1, 2, 5, Blocks.field_150478_aa, 0);
            this.func_151552_a(world, structureboundingbox, random, 0.1F, 3, 2, 5, Blocks.field_150478_aa, 0);
            if (this.field_75019_b) {
                this.func_151549_a(world, structureboundingbox, 0, 1, 2, 0, 3, 4, Blocks.field_150350_a, Blocks.field_150350_a, false);
            }

            if (this.field_75020_c) {
                this.func_151549_a(world, structureboundingbox, 4, 1, 2, 4, 3, 4, Blocks.field_150350_a, Blocks.field_150350_a, false);
            }

            return true;
        }
    }
}
