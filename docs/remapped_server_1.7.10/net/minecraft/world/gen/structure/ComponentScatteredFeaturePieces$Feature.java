package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

abstract class ComponentScatteredFeaturePieces$Feature extends StructureComponent {

    protected int field_74939_a;
    protected int field_74937_b;
    protected int field_74938_c;
    protected int field_74936_d = -1;

    public ComponentScatteredFeaturePieces$Feature() {}

    protected ComponentScatteredFeaturePieces$Feature(Random random, int i, int j, int k, int l, int i1, int j1) {
        super(0);
        this.field_74939_a = l;
        this.field_74937_b = i1;
        this.field_74938_c = j1;
        this.field_74885_f = random.nextInt(4);
        switch (this.field_74885_f) {
            case 0:
            case 2:
                this.field_74887_e = new StructureBoundingBox(i, j, k, i + l - 1, j + i1 - 1, k + j1 - 1);
                break;
            default:
                this.field_74887_e = new StructureBoundingBox(i, j, k, i + j1 - 1, j + i1 - 1, k + l - 1);
        }

    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound) {
        nbttagcompound.func_74768_a("Width", this.field_74939_a);
        nbttagcompound.func_74768_a("Height", this.field_74937_b);
        nbttagcompound.func_74768_a("Depth", this.field_74938_c);
        nbttagcompound.func_74768_a("HPos", this.field_74936_d);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound) {
        this.field_74939_a = nbttagcompound.func_74762_e("Width");
        this.field_74937_b = nbttagcompound.func_74762_e("Height");
        this.field_74938_c = nbttagcompound.func_74762_e("Depth");
        this.field_74936_d = nbttagcompound.func_74762_e("HPos");
    }

    protected boolean func_74935_a(World world, StructureBoundingBox structureboundingbox, int i) {
        if (this.field_74936_d >= 0) {
            return true;
        } else {
            int j = 0;
            int k = 0;

            for (int l = this.field_74887_e.field_78896_c; l <= this.field_74887_e.field_78892_f; ++l) {
                for (int i1 = this.field_74887_e.field_78897_a; i1 <= this.field_74887_e.field_78893_d; ++i1) {
                    if (structureboundingbox.func_78890_b(i1, 64, l)) {
                        j += Math.max(world.func_72825_h(i1, l), world.field_73011_w.func_76557_i());
                        ++k;
                    }
                }
            }

            if (k == 0) {
                return false;
            } else {
                this.field_74936_d = j / k;
                this.field_74887_e.func_78886_a(0, this.field_74936_d - this.field_74887_e.field_78895_b + i, 0);
                return true;
            }
        }
    }
}
