package net.minecraft.nbt;

import java.util.ArrayList;
import java.util.Iterator;

class JsonToNBT$List extends JsonToNBT$Any {

    protected ArrayList field_150492_b = new ArrayList();

    public JsonToNBT$List(String s) {
        this.field_150490_a = s;
    }

    @Override
    public NBTBase func_150489_a() {
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.field_150492_b.iterator();

        while (iterator.hasNext()) {
            JsonToNBT$Any jsontonbt$any = (JsonToNBT$Any) iterator.next();

            nbttaglist.func_74742_a(jsontonbt$any.func_150489_a());
        }

        return nbttaglist;
    }
}
