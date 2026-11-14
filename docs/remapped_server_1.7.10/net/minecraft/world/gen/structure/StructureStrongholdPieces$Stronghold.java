package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

abstract class StructureStrongholdPieces$Stronghold extends StructureComponent {

    protected StructureStrongholdPieces$Stronghold$Door field_143013_d;

    public StructureStrongholdPieces$Stronghold() {
        this.field_143013_d = StructureStrongholdPieces$Stronghold$Door.OPENING;
    }

    protected StructureStrongholdPieces$Stronghold(int i) {
        super(i);
        this.field_143013_d = StructureStrongholdPieces$Stronghold$Door.OPENING;
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        nbttagcompound.func_74778_a("EntryDoor", this.field_143013_d.name());
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        this.field_143013_d = StructureStrongholdPieces$Stronghold$Door.valueOf(nbttagcompound.func_74779_i("EntryDoor"));
    }

    protected void func_74990_a(World world, Random random, StructureBoundingBox structureboundingbox, StructureStrongholdPieces$Stronghold$Door structurestrongholdpieces$stronghold$door, int i, int j, int k) {
        // $FF: Couldn't be decompiled
    }

    protected StructureStrongholdPieces$Stronghold$Door func_74988_a(Random random) {
        int i = random.nextInt(5);

        switch (i) {
            case 0:
            case 1:
            default:
                return StructureStrongholdPieces$Stronghold$Door.OPENING;
            case 2:
                return StructureStrongholdPieces$Stronghold$Door.WOOD_DOOR;
            case 3:
                return StructureStrongholdPieces$Stronghold$Door.GRATES;
            case 4:
                return StructureStrongholdPieces$Stronghold$Door.IRON_DOOR;
        }
    }

    protected StructureComponent func_74986_a(StructureStrongholdPieces$Stairs2 structurestrongholdpieces$stairs2, List list, Random random, int i, int j) {
        switch (this.field_74885_f) {
            case 0:
                return StructureStrongholdPieces.access$000(structurestrongholdpieces$stairs2, list, random, this.field_74887_e.field_78897_a + i, this.field_74887_e.field_78895_b + j, this.field_74887_e.field_78892_f + 1, this.field_74885_f, this.func_74877_c());
            case 1:
                return StructureStrongholdPieces.access$000(structurestrongholdpieces$stairs2, list, random, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + j, this.field_74887_e.field_78896_c + i, this.field_74885_f, this.func_74877_c());
            case 2:
                return StructureStrongholdPieces.access$000(structurestrongholdpieces$stairs2, list, random, this.field_74887_e.field_78897_a + i, this.field_74887_e.field_78895_b + j, this.field_74887_e.field_78896_c - 1, this.field_74885_f, this.func_74877_c());
            case 3:
                return StructureStrongholdPieces.access$000(structurestrongholdpieces$stairs2, list, random, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + j, this.field_74887_e.field_78896_c + i, this.field_74885_f, this.func_74877_c());
            default:
                return null;
        }
    }

    protected StructureComponent func_74989_b(StructureStrongholdPieces$Stairs2 structurestrongholdpieces$stairs2, List list, Random random, int i, int j) {
        switch (this.field_74885_f) {
            case 0:
                return StructureStrongholdPieces.access$000(structurestrongholdpieces$stairs2, list, random, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + i, this.field_74887_e.field_78896_c + j, 1, this.func_74877_c());
            case 1:
                return StructureStrongholdPieces.access$000(structurestrongholdpieces$stairs2, list, random, this.field_74887_e.field_78897_a + j, this.field_74887_e.field_78895_b + i, this.field_74887_e.field_78896_c - 1, 2, this.func_74877_c());
            case 2:
                return StructureStrongholdPieces.access$000(structurestrongholdpieces$stairs2, list, random, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + i, this.field_74887_e.field_78896_c + j, 1, this.func_74877_c());
            case 3:
                return StructureStrongholdPieces.access$000(structurestrongholdpieces$stairs2, list, random, this.field_74887_e.field_78897_a + j, this.field_74887_e.field_78895_b + i, this.field_74887_e.field_78896_c - 1, 2, this.func_74877_c());
            default:
                return null;
        }
    }

    protected StructureComponent func_74987_c(StructureStrongholdPieces$Stairs2 structurestrongholdpieces$stairs2, List list, Random random, int i, int j) {
        switch (this.field_74885_f) {
            case 0:
                return StructureStrongholdPieces.access$000(structurestrongholdpieces$stairs2, list, random, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + i, this.field_74887_e.field_78896_c + j, 3, this.func_74877_c());
            case 1:
                return StructureStrongholdPieces.access$000(structurestrongholdpieces$stairs2, list, random, this.field_74887_e.field_78897_a + j, this.field_74887_e.field_78895_b + i, this.field_74887_e.field_78892_f + 1, 0, this.func_74877_c());
            case 2:
                return StructureStrongholdPieces.access$000(structurestrongholdpieces$stairs2, list, random, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + i, this.field_74887_e.field_78896_c + j, 3, this.func_74877_c());
            case 3:
                return StructureStrongholdPieces.access$000(structurestrongholdpieces$stairs2, list, random, this.field_74887_e.field_78897_a + j, this.field_74887_e.field_78895_b + i, this.field_74887_e.field_78892_f + 1, 0, this.func_74877_c());
            default:
                return null;
        }
    }

    protected static boolean func_74991_a(StructureBoundingBox structureboundingbox) {
        return structureboundingbox != null && structureboundingbox.field_78895_b > 10;
    }
}
