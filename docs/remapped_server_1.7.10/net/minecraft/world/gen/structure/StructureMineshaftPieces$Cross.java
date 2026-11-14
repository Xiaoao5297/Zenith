package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class StructureMineshaftPieces$Cross extends StructureComponent {

    private int field_74953_a;
    private boolean field_74952_b;

    public StructureMineshaftPieces$Cross() {}

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        nbttagcompound.func_74757_a("tf", this.field_74952_b);
        nbttagcompound.func_74768_a("D", this.field_74953_a);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        this.field_74952_b = nbttagcompound.func_74767_n("tf");
        this.field_74953_a = nbttagcompound.func_74762_e("D");
    }

    public StructureMineshaftPieces$Cross(int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(i);
        this.field_74953_a = j;
        this.field_74887_e = structureboundingbox;
        this.field_74952_b = structureboundingbox.func_78882_c() > 3;
    }

    public static StructureBoundingBox func_74951_a(List list, Random random, int i, int j, int k, int l) {
        StructureBoundingBox structureboundingbox = new StructureBoundingBox(i, j, k, i, j + 2, k);

        if (random.nextInt(4) == 0) {
            structureboundingbox.field_78894_e += 4;
        }

        switch (l) {
            case 0:
                structureboundingbox.field_78897_a = i - 1;
                structureboundingbox.field_78893_d = i + 3;
                structureboundingbox.field_78892_f = k + 4;
                break;
            case 1:
                structureboundingbox.field_78897_a = i - 4;
                structureboundingbox.field_78896_c = k - 1;
                structureboundingbox.field_78892_f = k + 3;
                break;
            case 2:
                structureboundingbox.field_78897_a = i - 1;
                structureboundingbox.field_78893_d = i + 3;
                structureboundingbox.field_78896_c = k - 4;
                break;
            case 3:
                structureboundingbox.field_78893_d = i + 4;
                structureboundingbox.field_78896_c = k - 1;
                structureboundingbox.field_78892_f = k + 3;
        }

        return StructureComponent.func_74883_a(list, structureboundingbox) != null ? null : structureboundingbox;
    }

    @Override
    public void func_74861_a(StructureComponent structurecomponent, List list, Random random) {
        int i = this.func_74877_c();

        switch (this.field_74953_a) {
            case 0:
                StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, 0, i);
                StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, 1, i);
                StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, 3, i);
                break;
            case 1:
                StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, 2, i);
                StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, 0, i);
                StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, 1, i);
                break;
            case 2:
                StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, 2, i);
                StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, 1, i);
                StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, 3, i);
                break;
            case 3:
                StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, 2, i);
                StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, 0, i);
                StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, 3, i);
        }

        if (this.field_74952_b) {
            if (random.nextBoolean()) {
                StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b + 3 + 1, this.field_74887_e.field_78896_c - 1, 2, i);
            }

            if (random.nextBoolean()) {
                StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + 3 + 1, this.field_74887_e.field_78896_c + 1, 1, i);
            }

            if (random.nextBoolean()) {
                StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + 3 + 1, this.field_74887_e.field_78896_c + 1, 3, i);
            }

            if (random.nextBoolean()) {
                StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b + 3 + 1, this.field_74887_e.field_78892_f + 1, 0, i);
            }
        }

    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        if (this.func_74860_a(world, structureboundingbox)) {
            return false;
        } else {
            if (this.field_74952_b) {
                this.func_151549_a(world, structureboundingbox, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c, this.field_74887_e.field_78893_d - 1, this.field_74887_e.field_78895_b + 3 - 1, this.field_74887_e.field_78892_f, Blocks.field_150350_a, Blocks.field_150350_a, false);
                this.func_151549_a(world, structureboundingbox, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, this.field_74887_e.field_78893_d, this.field_74887_e.field_78895_b + 3 - 1, this.field_74887_e.field_78892_f - 1, Blocks.field_150350_a, Blocks.field_150350_a, false);
                this.func_151549_a(world, structureboundingbox, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78894_e - 2, this.field_74887_e.field_78896_c, this.field_74887_e.field_78893_d - 1, this.field_74887_e.field_78894_e, this.field_74887_e.field_78892_f, Blocks.field_150350_a, Blocks.field_150350_a, false);
                this.func_151549_a(world, structureboundingbox, this.field_74887_e.field_78897_a, this.field_74887_e.field_78894_e - 2, this.field_74887_e.field_78896_c + 1, this.field_74887_e.field_78893_d, this.field_74887_e.field_78894_e, this.field_74887_e.field_78892_f - 1, Blocks.field_150350_a, Blocks.field_150350_a, false);
                this.func_151549_a(world, structureboundingbox, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b + 3, this.field_74887_e.field_78896_c + 1, this.field_74887_e.field_78893_d - 1, this.field_74887_e.field_78895_b + 3, this.field_74887_e.field_78892_f - 1, Blocks.field_150350_a, Blocks.field_150350_a, false);
            } else {
                this.func_151549_a(world, structureboundingbox, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c, this.field_74887_e.field_78893_d - 1, this.field_74887_e.field_78894_e, this.field_74887_e.field_78892_f, Blocks.field_150350_a, Blocks.field_150350_a, false);
                this.func_151549_a(world, structureboundingbox, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, this.field_74887_e.field_78893_d, this.field_74887_e.field_78894_e, this.field_74887_e.field_78892_f - 1, Blocks.field_150350_a, Blocks.field_150350_a, false);
            }

            this.func_151549_a(world, structureboundingbox, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78894_e, this.field_74887_e.field_78896_c + 1, Blocks.field_150344_f, Blocks.field_150350_a, false);
            this.func_151549_a(world, structureboundingbox, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f - 1, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78894_e, this.field_74887_e.field_78892_f - 1, Blocks.field_150344_f, Blocks.field_150350_a, false);
            this.func_151549_a(world, structureboundingbox, this.field_74887_e.field_78893_d - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c + 1, this.field_74887_e.field_78893_d - 1, this.field_74887_e.field_78894_e, this.field_74887_e.field_78896_c + 1, Blocks.field_150344_f, Blocks.field_150350_a, false);
            this.func_151549_a(world, structureboundingbox, this.field_74887_e.field_78893_d - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f - 1, this.field_74887_e.field_78893_d - 1, this.field_74887_e.field_78894_e, this.field_74887_e.field_78892_f - 1, Blocks.field_150344_f, Blocks.field_150350_a, false);

            for (int i = this.field_74887_e.field_78897_a; i <= this.field_74887_e.field_78893_d; ++i) {
                for (int j = this.field_74887_e.field_78896_c; j <= this.field_74887_e.field_78892_f; ++j) {
                    if (this.func_151548_a(world, i, this.field_74887_e.field_78895_b - 1, j, structureboundingbox).func_149688_o() == Material.field_151579_a) {
                        this.func_151550_a(world, Blocks.field_150344_f, 0, i, this.field_74887_e.field_78895_b - 1, j, structureboundingbox);
                    }
                }
            }

            return true;
        }
    }
}
