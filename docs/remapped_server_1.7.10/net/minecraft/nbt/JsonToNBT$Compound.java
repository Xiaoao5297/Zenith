package net.minecraft.nbt;

import java.util.ArrayList;
import java.util.Iterator;

class JsonToNBT$Compound extends JsonToNBT$Any {

    protected ArrayList field_150491_b = new ArrayList();

    public JsonToNBT$Compound(String s) {
        this.field_150490_a = s;
    }

    @Override
    public NBTBase func_150489_a() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        Iterator iterator = this.field_150491_b.iterator();

        while (iterator.hasNext()) {
            JsonToNBT$Any jsontonbt$any = (JsonToNBT$Any) iterator.next();

            nbttagcompound.func_74782_a(jsontonbt$any.field_150490_a, jsontonbt$any.func_150489_a());
        }

        return nbttagcompound;
    }
}
