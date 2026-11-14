package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class StructureMineshaftPieces$Stairs extends StructureComponent {

    public StructureMineshaftPieces$Stairs() {}

    public StructureMineshaftPieces$Stairs(int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(i);
        this.field_74885_f = j;
        this.field_74887_e = structureboundingbox;
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {}

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {}

    public static StructureBoundingBox func_74950_a(List list, Random random, int i, int j, int k, int l) {
        StructureBoundingBox structureboundingbox = new StructureBoundingBox(i, j - 5, k, i, j + 2, k);

        switch (l) {
            case 0:
                structureboundingbox.field_78893_d = i + 2;
                structureboundingbox.field_78892_f = k + 8;
                break;
            case 1:
                structureboundingbox.field_78897_a = i - 8;
                structureboundingbox.field_78892_f = k + 2;
                break;
            case 2:
                structureboundingbox.field_78893_d = i + 2;
                structureboundingbox.field_78896_c = k - 8;
                break;
            case 3:
                structureboundingbox.field_78893_d = i + 8;
                structureboundingbox.field_78892_f = k + 2;
        }

        return StructureComponent.func_74883_a(list, structureboundingbox) != null ? null : structureboundingbox;
    }

    @Override
    public void func_74861_a(StructureComponent structurecomponent, List list, Random random) {
        int i = this.func_74877_c();

        switch (this.field_74885_f) {
            case 0:
                StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, 0, i);
                break;
            case 1:
                StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c, 1, i);
                break;
            case 2:
                StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, 2, i);
                break;
            case 3:
                StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c, 3, i);
        }

    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        if (this.func_74860_a(world, structureboundingbox)) {
            return false;
        } else {
            this.func_151549_a(world, structureboundingbox, 0, 5, 0, 2, 7, 1, Blocks.field_150350_a, Blocks.field_150350_a, false);
            this.func_151549_a(world, structureboundingbox, 0, 0, 7, 2, 2, 8, Blocks.field_150350_a, Blocks.field_150350_a, false);

            for (int i = 0; i < 5; ++i) {
                this.func_151549_a(world, structureboundingbox, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, Blocks.field_150350_a, Blocks.field_150350_a, false);
            }

            return true;
        }
    }
}
