package net.minecraft.world.gen.structure;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class StructureMineshaftPieces$Room extends StructureComponent {

    private List field_74949_a = new LinkedList();

    public StructureMineshaftPieces$Room() {}

    public StructureMineshaftPieces$Room(int i, Random random, int j, int k) {
        super(i);
        this.field_74887_e = new StructureBoundingBox(j, 50, k, j + 7 + random.nextInt(6), 54 + random.nextInt(6), k + 7 + random.nextInt(6));
    }

    @Override
    public void func_74861_a(StructureComponent structurecomponent, List list, Random random) {
        int i = this.func_74877_c();
        int j = this.field_74887_e.func_78882_c() - 3 - 1;

        if (j <= 0) {
            j = 1;
        }

        int k;
        StructureComponent structurecomponent1;
        StructureBoundingBox structureboundingbox;

        for (k = 0; k < this.field_74887_e.func_78883_b(); k += 4) {
            k += random.nextInt(this.field_74887_e.func_78883_b());
            if (k + 3 > this.field_74887_e.func_78883_b()) {
                break;
            }

            structurecomponent1 = StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a + k, this.field_74887_e.field_78895_b + random.nextInt(j) + 1, this.field_74887_e.field_78896_c - 1, 2, i);
            if (structurecomponent1 != null) {
                structureboundingbox = structurecomponent1.func_74874_b();
                this.field_74949_a.add(new StructureBoundingBox(structureboundingbox.field_78897_a, structureboundingbox.field_78895_b, this.field_74887_e.field_78896_c, structureboundingbox.field_78893_d, structureboundingbox.field_78894_e, this.field_74887_e.field_78896_c + 1));
            }
        }

        for (k = 0; k < this.field_74887_e.func_78883_b(); k += 4) {
            k += random.nextInt(this.field_74887_e.func_78883_b());
            if (k + 3 > this.field_74887_e.func_78883_b()) {
                break;
            }

            structurecomponent1 = StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a + k, this.field_74887_e.field_78895_b + random.nextInt(j) + 1, this.field_74887_e.field_78892_f + 1, 0, i);
            if (structurecomponent1 != null) {
                structureboundingbox = structurecomponent1.func_74874_b();
                this.field_74949_a.add(new StructureBoundingBox(structureboundingbox.field_78897_a, structureboundingbox.field_78895_b, this.field_74887_e.field_78892_f - 1, structureboundingbox.field_78893_d, structureboundingbox.field_78894_e, this.field_74887_e.field_78892_f));
            }
        }

        for (k = 0; k < this.field_74887_e.func_78880_d(); k += 4) {
            k += random.nextInt(this.field_74887_e.func_78880_d());
            if (k + 3 > this.field_74887_e.func_78880_d()) {
                break;
            }

            structurecomponent1 = StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + random.nextInt(j) + 1, this.field_74887_e.field_78896_c + k, 1, i);
            if (structurecomponent1 != null) {
                structureboundingbox = structurecomponent1.func_74874_b();
                this.field_74949_a.add(new StructureBoundingBox(this.field_74887_e.field_78897_a, structureboundingbox.field_78895_b, structureboundingbox.field_78896_c, this.field_74887_e.field_78897_a + 1, structureboundingbox.field_78894_e, structureboundingbox.field_78892_f));
            }
        }

        for (k = 0; k < this.field_74887_e.func_78880_d(); k += 4) {
            k += random.nextInt(this.field_74887_e.func_78880_d());
            if (k + 3 > this.field_74887_e.func_78880_d()) {
                break;
            }

            structurecomponent1 = StructureMineshaftPieces.access$000(structurecomponent, list, random, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + random.nextInt(j) + 1, this.field_74887_e.field_78896_c + k, 3, i);
            if (structurecomponent1 != null) {
                structureboundingbox = structurecomponent1.func_74874_b();
                this.field_74949_a.add(new StructureBoundingBox(this.field_74887_e.field_78893_d - 1, structureboundingbox.field_78895_b, structureboundingbox.field_78896_c, this.field_74887_e.field_78893_d, structureboundingbox.field_78894_e, structureboundingbox.field_78892_f));
            }
        }

    }

    @Override
    public boolean func_74875_a(World world, Random random, StructureBoundingBox structureboundingbox) {
        if (this.func_74860_a(world, structureboundingbox)) {
            return false;
        } else {
            this.func_151549_a(world, structureboundingbox, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c, this.field_74887_e.field_78893_d, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f, Blocks.field_150346_d, Blocks.field_150350_a, true);
            this.func_151549_a(world, structureboundingbox, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b + 1, this.field_74887_e.field_78896_c, this.field_74887_e.field_78893_d, Math.min(this.field_74887_e.field_78895_b + 3, this.field_74887_e.field_78894_e), this.field_74887_e.field_78892_f, Blocks.field_150350_a, Blocks.field_150350_a, false);
            Iterator iterator = this.field_74949_a.iterator();

            while (iterator.hasNext()) {
                StructureBoundingBox structureboundingbox1 = (StructureBoundingBox) iterator.next();

                this.func_151549_a(world, structureboundingbox, structureboundingbox1.field_78897_a, structureboundingbox1.field_78894_e - 2, structureboundingbox1.field_78896_c, structureboundingbox1.field_78893_d, structureboundingbox1.field_78894_e, structureboundingbox1.field_78892_f, Blocks.field_150350_a, Blocks.field_150350_a, false);
            }

            this.func_151547_a(world, structureboundingbox, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b + 4, this.field_74887_e.field_78896_c, this.field_74887_e.field_78893_d, this.field_74887_e.field_78894_e, this.field_74887_e.field_78892_f, Blocks.field_150350_a, false);
            return true;
        }
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.field_74949_a.iterator();

        while (iterator.hasNext()) {
            StructureBoundingBox structureboundingbox = (StructureBoundingBox) iterator.next();

            nbttaglist.func_74742_a(structureboundingbox.func_151535_h());
        }

        nbttagcompound.func_74782_a("Entrances", nbttaglist);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        NBTTagList nbttaglist = nbttagcompound.func_150295_c("Entrances", 11);

        for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
            this.field_74949_a.add(new StructureBoundingBox(nbttaglist.func_150306_c(i)));
        }

    }
}
