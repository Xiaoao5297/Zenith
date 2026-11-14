package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class StructureStrongholdPieces$Corridor extends StructureStrongholdPieces$Stronghold {

    private int field_74993_a;

    public StructureStrongholdPieces$Corridor() {}

    public StructureStrongholdPieces$Corridor(int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(i);
        this.field_74885_f = j;
        this.field_74887_e = structureboundingbox;
        this.field_74993_a = j != 2 && j != 0 ? structureboundingbox.func_78883_b() : structureboundingbox.func_78880_d();
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        super.func_143012_a(nbttagcompound);
        nbttagcompound.func_74768_a("Steps", this.field_74993_a);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        super.func_143011_b(nbttagcompound);
        this.field_74993_a = nbttagcompound.func_74762_e("Steps");
    }

    public static StructureBoundingBox func_74992_a(List list, Random random, int i, int j, int k, int l) {
        boolean flag = true;
        StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, -1, -1, 0, 5, 5, 4, l);
        StructureComponent structurecomponent = StructureComponent.func_74883_a(list, structureboundingbox);

        if (structurecomponent == null) {
            return null;
        } else {
            if (structurecomponent.func_74874_b().field_78895_b == structureboundingbox.field_78895_b) {
                for (int i1 = 3; i1 >= 1; --i1) {
                    structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, -1, -1, 0, 5, 5, i1 - 1, l);
                    if (!structurecomponent.func_74874_b().func_78884_a(structureboundingbox)) {
                        return StructureBoundingBox.func_78889_a(i, j, k, -1, -1, 0, 5, 5, i1, l);
                    }
                }
            }

            return null;
        }
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        if (this.func_74860_a(world, structureboundingbox)) {
            return false;
        } else {
            for (int i = 0; i < this.field_74993_a; ++i) {
                this.func_151550_a(world, Blocks.field_150417_aV, 0, 0, 0, i, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150417_aV, 0, 1, 0, i, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150417_aV, 0, 2, 0, i, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150417_aV, 0, 3, 0, i, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150417_aV, 0, 4, 0, i, structureboundingbox);

                for (int j = 1; j <= 3; ++j) {
                    this.func_151550_a(world, Blocks.field_150417_aV, 0, 0, j, i, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150350_a, 0, 1, j, i, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150350_a, 0, 2, j, i, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150350_a, 0, 3, j, i, structureboundingbox);
                    this.func_151550_a(world, Blocks.field_150417_aV, 0, 4, j, i, structureboundingbox);
                }

                this.func_151550_a(world, Blocks.field_150417_aV, 0, 0, 4, i, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150417_aV, 0, 1, 4, i, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150417_aV, 0, 2, 4, i, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150417_aV, 0, 3, 4, i, structureboundingbox);
                this.func_151550_a(world, Blocks.field_150417_aV, 0, 4, 4, i, structureboundingbox);
            }

            return true;
        }
    }
}
