package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class StructureNetherBridgePieces$End extends StructureNetherBridgePieces$Piece {

    private int field_74972_a;

    public StructureNetherBridgePieces$End() {}

    public StructureNetherBridgePieces$End(int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(i);
        this.field_74885_f = j;
        this.field_74887_e = structureboundingbox;
        this.field_74972_a = random.nextInt();
    }

    public static StructureNetherBridgePieces$End func_74971_a(List list, Random random, int i, int j, int k, int l, int i1) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, -1, -3, 0, 5, 10, 8, l);

        return func_74964_a(structureboundingbox) && StructureComponent.func_74883_a(list, structureboundingbox) == null ? new StructureNetherBridgePieces$End(i1, random, structureboundingbox, l) : null;
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        super.func_143011_b(nbttagcompound);
        this.field_74972_a = nbttagcompound.func_74762_e("Seed");
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        super.func_143012_a(nbttagcompound);
        nbttagcompound.func_74768_a("Seed", this.field_74972_a);
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        Random random1 = new Random((long) this.field_74972_a);

        int i;
        int j;
        int k;

        for (j = 0; j <= 4; ++j) {
            for (k = 3; k <= 4; ++k) {
                i = random1.nextInt(8);
                this.func_151549_a(world, structureboundingbox, j, k, 0, j, k, i, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
            }
        }

        j = random1.nextInt(8);
        this.func_151549_a(world, structureboundingbox, 0, 5, 0, 0, 5, j, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        j = random1.nextInt(8);
        this.func_151549_a(world, structureboundingbox, 4, 5, 0, 4, 5, j, Blocks.field_150385_bj, Blocks.field_150385_bj, false);

        for (j = 0; j <= 4; ++j) {
            k = random1.nextInt(5);
            this.func_151549_a(world, structureboundingbox, j, 2, 0, j, 2, k, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
        }

        for (j = 0; j <= 4; ++j) {
            for (k = 0; k <= 1; ++k) {
                i = random1.nextInt(3);
                this.func_151549_a(world, structureboundingbox, j, k, 0, j, k, i, Blocks.field_150385_bj, Blocks.field_150385_bj, false);
            }
        }

        return true;
    }
}
