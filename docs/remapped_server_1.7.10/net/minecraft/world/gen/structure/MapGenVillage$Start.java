package net.minecraft.world.gen.structure;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class MapGenVillage$Start extends StructureStart {

    private boolean field_75076_c;

    public MapGenVillage$Start() {}

    public MapGenVillage$Start(World world, Random random, int i, int j, int k) {
        super(i, j);
        List list = StructureVillagePieces.func_75084_a(random, k);
        StructureVillagePieces$Start structurevillagepieces$start = new StructureVillagePieces$Start(world.func_72959_q(), 0, random, (i << 4) + 2, (j << 4) + 2, list, k);

        this.field_75075_a.add(structurevillagepieces$start);
        structurevillagepieces$start.func_74861_a(structurevillagepieces$start, this.field_75075_a, random);
        List list1 = structurevillagepieces$start.field_74930_j;
        List list2 = structurevillagepieces$start.field_74932_i;

        int l;

        while (!list1.isEmpty() || !list2.isEmpty()) {
            StructureComponent structurecomponent;

            if (list1.isEmpty()) {
                l = random.nextInt(list2.size());
                structurecomponent = (StructureComponent) list2.remove(l);
                structurecomponent.func_74861_a(structurevillagepieces$start, this.field_75075_a, random);
            } else {
                l = random.nextInt(list1.size());
                structurecomponent = (StructureComponent) list1.remove(l);
                structurecomponent.func_74861_a(structurevillagepieces$start, this.field_75075_a, random);
            }
        }

        this.func_75072_c();
        l = 0;
        Iterator iterator = this.field_75075_a.iterator();

        while (iterator.hasNext()) {
            StructureComponent structurecomponent1 = (StructureComponent) iterator.next();

            if (!(structurecomponent1 instanceof StructureVillagePieces$Road)) {
                ++l;
            }
        }

        this.field_75076_c = l > 2;
    }

    @Override
    public boolean func_75069_d() {
        return this.field_75076_c;
    }

    @Override
    public void func_143022_a(NBTTagCompound nbttagcompound) {
        super.func_143022_a(nbttagcompound);
        nbttagcompound.func_74757_a("Valid", this.field_75076_c);
    }

    @Override
    public void func_143017_b(NBTTagCompound nbttagcompound) {
        super.func_143017_b(nbttagcompound);
        this.field_75076_c = nbttagcompound.func_74767_n("Valid");
    }
}
