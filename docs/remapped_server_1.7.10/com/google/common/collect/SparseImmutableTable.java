package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.concurrent.Immutable;

@GwtCompatible
@Immutable
final class SparseImmutableTable<R, C, V> extends RegularImmutableTable<R, C, V> {

    private final ImmutableMap<R, Map<C, V>> rowMap;
    private final ImmutableMap<C, Map<R, V>> columnMap;
    private final int[] iterationOrderRow;
    private final int[] iterationOrderColumn;

    SparseImmutableTable(ImmutableList<Table.Cell<R, C, V>> immutablelist, ImmutableSet<R> immutableset, ImmutableSet<C> immutableset1) {
        HashMap hashmap = Maps.newHashMap();
        LinkedHashMap linkedhashmap = Maps.newLinkedHashMap();
        Iterator iterator = immutableset.iterator();

        while (iterator.hasNext()) {
            Object object = iterator.next();

            hashmap.put(object, linkedhashmap.size());
            linkedhashmap.put(object, new LinkedHashMap());
        }

        LinkedHashMap linkedhashmap1 = Maps.newLinkedHashMap();
        Iterator iterator1 = immutableset1.iterator();

        while (iterator1.hasNext()) {
            Object object1 = iterator1.next();

            linkedhashmap1.put(object1, new LinkedHashMap());
        }

        int[] aint = new int[immutablelist.size()];
        int[] aint1 = new int[immutablelist.size()];

        for (int i = 0; i < immutablelist.size(); ++i) {
            Table.Cell table_cell = (Table.Cell) immutablelist.get(i);
            Object object2 = table_cell.getRowKey();
            Object object3 = table_cell.getColumnKey();
            Object object4 = table_cell.getValue();

            aint[i] = (Integer) hashmap.get(object2);
            Map map = (Map) linkedhashmap.get(object2);

            aint1[i] = map.size();
            Object object5 = map.put(object3, object4);

            if (object5 != null) {
                throw new IllegalArgumentException("Duplicate value for row=" + object2 + ", column=" + object3 + ": " + object4 + ", " + object5);
            }

            ((Map) linkedhashmap1.get(object3)).put(object2, object4);
        }

        this.iterationOrderRow = aint;
        this.iterationOrderColumn = aint1;
        ImmutableMap.Builder immutablemap_builder = ImmutableMap.builder();
        Iterator iterator2 = linkedhashmap.entrySet().iterator();

        while (iterator2.hasNext()) {
            Entry entry = (Entry) iterator2.next();

            immutablemap_builder.put(entry.getKey(), ImmutableMap.copyOf((Map) entry.getValue()));
        }

        this.rowMap = immutablemap_builder.build();
        ImmutableMap.Builder immutablemap_builder1 = ImmutableMap.builder();
        Iterator iterator3 = linkedhashmap1.entrySet().iterator();

        while (iterator3.hasNext()) {
            Entry entry1 = (Entry) iterator3.next();

            immutablemap_builder1.put(entry1.getKey(), ImmutableMap.copyOf((Map) entry1.getValue()));
        }

        this.columnMap = immutablemap_builder1.build();
    }

    @Override
    public ImmutableMap<C, Map<R, V>> columnMap() {
        return this.columnMap;
    }

    @Override
    public ImmutableMap<R, Map<C, V>> rowMap() {
        return this.rowMap;
    }

    @Override
    public int size() {
        return this.iterationOrderRow.length;
    }

    @Override
    Table.Cell<R, C, V> getCell(int i) {
        int j = this.iterationOrderRow[i];
        Entry entry = (Entry) this.rowMap.entrySet().asList().get(j);
        ImmutableMap immutablemap = (ImmutableMap) entry.getValue();
        int k = this.iterationOrderColumn[i];
        Entry entry1 = (Entry) immutablemap.entrySet().asList().get(k);

        return cellOf(entry.getKey(), entry1.getKey(), entry1.getValue());
    }

    @Override
    V getValue(int i) {
        int j = this.iterationOrderRow[i];
        ImmutableMap immutablemap = (ImmutableMap) this.rowMap.values().asList().get(j);
        int k = this.iterationOrderColumn[i];

        return immutablemap.values().asList().get(k);
    }
}
