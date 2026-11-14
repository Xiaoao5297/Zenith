package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;

public class ObjectIntIdentityMap implements IObjectIntIterable {

    private IdentityHashMap field_148749_a = new IdentityHashMap(512);
    private List field_148748_b = Lists.newArrayList();

    public ObjectIntIdentityMap() {}

    public void func_148746_a(Object object, int i) {
        this.field_148749_a.put(object, i);

        while (this.field_148748_b.size() <= i) {
            this.field_148748_b.add((Object) null);
        }

        this.field_148748_b.set(i, object);
    }

    public int func_148747_b(Object object) {
        Integer integer = (Integer) this.field_148749_a.get(object);

        return integer == null ? -1 : integer;
    }

    public Object func_148745_a(int i) {
        return i >= 0 && i < this.field_148748_b.size() ? this.field_148748_b.get(i) : null;
    }

    public Iterator iterator() {
        return Iterators.filter(this.field_148748_b.iterator(), Predicates.notNull());
    }

    public boolean func_148744_b(int i) {
        return this.func_148745_a(i) != null;
    }
}
