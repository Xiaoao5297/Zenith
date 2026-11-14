package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class StructureNetherBridgePieces$Corridor extends StructureNetherBridgePieces$Piece {

    private boolean field_111021_b;

    public StructureNetherBridgePieces$Corridor() {}

    public StructureNetherBridgePieces$Corridor(int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(i);
        this.field_74885_f = j;
        this.field_74887_e = structureboundingbox;
        this.field_111021_b = random.nextInt(3) == 0;
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        super.func_143011_b(nbttagcompound);
        this.field_111021_b = nbttagcompound.func_74767_n("Chest");
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        super.func_143012_a(nbttagcompound);
        nbttagcompound.func_74757_a("Chest", this.field_111021_b);
    }

    @Override
    public void func_74861_a(StructureComponent structurecomponent, List list, Random random) {
        this.func_74961_b((StructureNetherBridgePieces$Start) structurecomponent, list, random, 0, 1, true);
    }

    public static StructureNetherBridgePieces$Corridor func_74978_a(List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, -1, 0, 0, 5, 7, 5, l);

        return func_74964_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureNetherBridgePieces$Corridor(i1, random, structureboundingbox, l) : null;
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        this.func_151549_a(world, structureboundingbox, 0, 0, 0, 4, 1, 4, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 0, 2, 0, 4, 5, 4, Blocks.field_150350_a, Blocks.field_150350_a, false);
        this.func_151549_a(world, structureboundingbox, 4, 2, 0, 4, 5, 4, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 4, 3, 1, 4, 4, 1, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
        this.func_151549_a(world, structureboundingbox, 4, 3, 3, 4, 4, 3, Blocks.field_150386_bk, Blocks.field_150386_bk, false);
        this.func_151549_a(world, structureboundingbox, 0, 2, 0, 0, 5, 0, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 0, 2, 4, 3, 5, 4, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 1, 3, 4, 1, 4, 4, Blocks.field_150386_bk, Blocks.field_150385_bj, false);
        this.func_151549_a(world, structureboundingbox, 3, 3, 4, 3, 4, 4, Blocks.field_150386_bk, Blocks.field_150385_bj, false);
        int i;
        int j;

        if (this.field_111021_b) {
            i = this.func_74862_a(2);
            j = this.func_74865_a(3, 3);
            int k = this.func_74873_b(3, 3);

            if (structureboundingbox.func_78890_b(j, i, k)) {
                this.field_111021_b = false;
                this.func_74879_a(world, structureboundingbox, random, 3, 2, 3, StructureNetherBridgePieces$Corridor.field_111019_a, 2 + random.nextInt(4));
            }
        }

        this.func_151549_a(world, structureboundingbox, 0, 6, 0, 4, 6, 4, Blocks.field_150385_bj, Blocks.field_150385_bj, false);

        for (i = 0; i <= 4; ++i) {
            for (j = 0; j <= 4; ++j) {
                this.func_151554_b(world, Blocks.field_150385_bj, 0, i, -1, j, structureboundingbox);
            }
        }

        return true;
    }
}
