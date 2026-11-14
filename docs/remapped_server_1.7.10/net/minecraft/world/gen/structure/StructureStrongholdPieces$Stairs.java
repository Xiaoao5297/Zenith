package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class StructureStrongholdPieces$Stairs extends StructureStrongholdPieces$Stronghold {

    private boolean field_75024_a;

    public StructureStrongholdPieces$Stairs() {}

    public StructureStrongholdPieces$Stairs(int i, Random random, int j, int k) {
        super(i);
        this.field_75024_a = true;
        this.field_74885_f = random.nextInt(4);
        this.field_143013_d = StructureStrongholdPieces$Stronghold$Door.OPENING;
        switch (this.field_74885_f) {
            case 0:
            case 2:
                this.field_74887_e = new StructureBoundingBox(j, 64, k, j + 5 - 1, 74, k + 5 - 1);
                break;
            default:
                this.field_74887_e = new StructureBoundingBox(j, 64, k, j + 5 - 1, 74, k + 5 - 1);
        }

    }

    public StructureStrongholdPieces$Stairs(int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(i);
        this.field_75024_a = false;
        this.field_74885_f = j;
        this.field_143013_d = this.func_74988_a(random);
        this.field_74887_e = structureboundingbox;
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        super.func_143012_a(nbttagcompound);
        nbttagcompound.func_74757_a("Source", this.field_75024_a);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        super.func_143011_b(nbttagcompound);
        this.field_75024_a = nbttagcompound.func_74767_n("Source");
    }

    @Override
    public void func_74861_a(StructureComponent structurecomponent, List list, Random random) {
        if (this.field_75024_a) {
            StructureStrongholdPieces.access$102(StructureStrongholdPieces$Crossing.class);
        }

        this.func_74986_a((StructureStrongholdPieces$Stairs2) structurecomponent, list, random, 1, 1);
    }

    public static StructureStrongholdPieces$Stairs func_75022_a(List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, -1, -7, 0, 5, 11, 5, l);

        return func_74991_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureStrongholdPieces$Stairs(i1, random, structureboundingbox, l) : null;
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        if (this.func_74860_a(world, structureboundingbox)) {
            return false;
        } else {
            this.func_74882_a(world, structureboundingbox, 0, 0, 0, 4, 10, 4, true, random, StructureStrongholdPieces.access$200());
            this.func_74990_a(world, random, structureboundingbox, this.field_143013_d, 1, 7, 0);
            this.func_74990_a(world, random, structureboundingbox, StructureStrongholdPieces$Stronghold$Door.OPENING, 1, 1, 4);
            this.func_151550_a(world, Blocks.field_150417_aV, 0, 2, 6, 1, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150417_aV, 0, 1, 5, 1, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150333_U, 0, 1, 6, 1, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150417_aV, 0, 1, 5, 2, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150417_aV, 0, 1, 4, 3, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150333_U, 0, 1, 5, 3, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150417_aV, 0, 2, 4, 3, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150417_aV, 0, 3, 3, 3, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150333_U, 0, 3, 4, 3, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150417_aV, 0, 3, 3, 2, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150417_aV, 0, 3, 2, 1, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150333_U, 0, 3, 3, 1, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150417_aV, 0, 2, 2, 1, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150417_aV, 0, 1, 1, 1, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150333_U, 0, 1, 2, 1, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150417_aV, 0, 1, 1, 2, structureboundingbox);
            this.func_151550_a(world, Blocks.field_150333_U, 0, 1, 1, 3, structureboundingbox);
            return true;
        }
    }
}
