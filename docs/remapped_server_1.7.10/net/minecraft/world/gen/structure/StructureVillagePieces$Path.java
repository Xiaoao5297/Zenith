package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class StructureVillagePieces$Path extends StructureVillagePieces$Road {

    private int field_74934_a;

    public StructureVillagePieces$Path() {}

    public StructureVillagePieces$Path(StructureVillagePieces$Start structurevillagepieces$start, int i, Random random, StructureBoundingBox structureboundingbox, int j) {
        super(structurevillagepieces$start, i);
        this.field_74885_f = j;
        this.field_74887_e = structureboundingbox;
        this.field_74934_a = Math.max(structureboundingbox.func_78883_b(), structureboundingbox.func_78880_d());
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        super.func_143012_a(nbttagcompound);
        nbttagcompound.func_74768_a("Length", this.field_74934_a);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        super.func_143011_b(nbttagcompound);
        this.field_74934_a = nbttagcompound.func_74762_e("Length");
    }

    @Override
    public void func_74861_a(StructureComponent structurecomponent, List list, Random random) {
        boolean flag = false;

        StructureComponent structurecomponent1;
        int i;

        for (i = random.nextInt(5); i < this.field_74934_a - 8; i += 2 + random.nextInt(5)) {
            structurecomponent1 = this.func_74891_a((StructureVillagePieces$Start) structurecomponent, list, random, 0, i);
            if (structurecomponent1 != null) {
                i += Math.max(structurecomponent1.field_74887_e.func_78883_b(), structurecomponent1.field_74887_e.func_78880_d());
                flag = true;
            }
        }

        for (i = random.nextInt(5); i < this.field_74934_a - 8; i += 2 + random.nextInt(5)) {
            structurecomponent1 = this.func_74894_b((StructureVillagePieces$Start) structurecomponent, list, random, 0, i);
            if (structurecomponent1 != null) {
                i += Math.max(structurecomponent1.field_74887_e.func_78883_b(), structurecomponent1.field_74887_e.func_78880_d());
                flag = true;
            }
        }

        if (flag && random.nextInt(3) > 0) {
            switch (this.field_74885_f) {
                case 0:
                    StructureVillagePieces.access$100((StructureVillagePieces$Start) structurecomponent, list, random, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f - 2, 1, this.func_74877_c());
                    break;
                case 1:
                    StructureVillagePieces.access$100((StructureVillagePieces$Start) structurecomponent, list, random, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, 2, this.func_74877_c());
                    break;
                case 2:
                    StructureVillagePieces.access$100((StructureVillagePieces$Start) structurecomponent, list, random, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c, 1, this.func_74877_c());
                    break;
                case 3:
                    StructureVillagePieces.access$100((StructureVillagePieces$Start) structurecomponent, list, random, this.field_74887_e.field_78893_d - 2, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, 2, this.func_74877_c());
            }
        }

        if (flag && random.nextInt(3) > 0) {
            switch (this.field_74885_f) {
                case 0:
                    StructureVillagePieces.access$100((StructureVillagePieces$Start) structurecomponent, list, random, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f - 2, 3, this.func_74877_c());
                    break;
                case 1:
                    StructureVillagePieces.access$100((StructureVillagePieces$Start) structurecomponent, list, random, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, 0, this.func_74877_c());
                    break;
                case 2:
                    StructureVillagePieces.access$100((StructureVillagePieces$Start) structurecomponent, list, random, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c, 3, this.func_74877_c());
                    break;
                case 3:
                    StructureVillagePieces.access$100((StructureVillagePieces$Start) structurecomponent, list, random, this.field_74887_e.field_78893_d - 2, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, 0, this.func_74877_c());
            }
        }

    }

    public static StructureBoundingBox func_74933_a(StructureVillagePieces$Start structurevillagepieces$start, List list, Random random, int i, int j, int k, int l) {
        for (int i1 = 7 * MathHelper.func_76136_a(random, 3, 5); i1 >= 7; i1 -= 7) {
            StructureBoundingBox structureboundingbox = StructureBoundingBox.func_78889_a(i, j, k, 0, 0, 0, 3, 3, i1, l);

            if (StructureComponent.func_74883_a(list, structureboundingbox) == null) {
                return structureboundingbox;
            }
        }

        return null;
    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        Block block = this.func_151558_b(Blocks.field_150351_n, 0);

        for (int i = this.field_74887_e.field_78897_a; i <= this.field_74887_e.field_78893_d; ++i) {
            for (int j = this.field_74887_e.field_78896_c; j <= this.field_74887_e.field_78892_f; ++j) {
                if (structureboundingbox.func_78890_b(i, 64, j)) {
                    int k = world.func_72825_h(i, j) - 1;

                    world.func_147465_d(i, k, j, block, 0, 2);
                }
            }
        }

        return true;
    }
}
