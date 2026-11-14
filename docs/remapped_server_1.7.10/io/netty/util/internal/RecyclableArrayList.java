package io.netty.util.internal;

import io.netty.util.Recycler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

public final class RecyclableArrayList extends ArrayList<Object> {

    private static final long serialVersionUID = -8605125654176467947L;
    private static final int DEFAULT_INITIAL_CAPACITY = 8;
    private static final Recycler<RecyclableArrayList> RECYCLER = new Recycler<RecyclableArrayList>() {
        @Override
        protected RecyclableArrayList newObject(Recycler.Handle recycler_handle) {
            return new RecyclableArrayList(recycler_handle);
        }
    };
    private final Recycler.Handle handle;

    public static RecyclableArrayList newInstance() {
        return newInstance(8);
    }

    public static RecyclableArrayList newInstance(int i) {
        RecyclableArrayList recyclablearraylist = (RecyclableArrayList) RecyclableArrayList.RECYCLER.get();

        recyclablearraylist.ensureCapacity(i);
        return recyclablearraylist;
    }

    private RecyclableArrayList(Recycler.Handle recycler_handle) {
        this(recycler_handle, 8);
    }

    private RecyclableArrayList(Recycler.Handle recycler_handle, int i) {
        super(i);
        this.handle = recycler_handle;
    }

    public boolean addAll(Collection<?> collection) {
        checkNullElements(collection);
        return super.addAll(collection);
    }

    public boolean addAll(int i, Collection<?> collection) {
        checkNullElements(collection);
        return super.addAll(i, collection);
    }

    private static void checkNullElements(Collection<?> collection) {
        if (collection instanceof RandomAccess && collection instanceof List) {
            List list = (List) collection;
            int i = list.size();

            for (int j = 0; j < i; ++j) {
                if (list.get(j) == null) {
                    throw new IllegalArgumentException("c contains null values");
                }
            }
        } else {
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                Object object = iterator.next();

                if (object == null) {
                    throw new IllegalArgumentException("c contains null values");
                }
            }
        }

    }

    public boolean add(Object object) {
        if (object == null) {
            throw new NullPointerException("element");
        } else {
            return super.add(object);
        }
    }

    public void add(int i, Object object) {
        if (object == null) {
            throw new NullPointerException("element");
        } else {
            super.add(i, object);
        }
    }

    public Object set(int i, Object object) {
        if (object == null) {
            throw new NullPointerException("element");
        } else {
            return super.set(i, object);
        }
    }

    public boolean recycle() {
        this.clear();
        return RecyclableArrayList.RECYCLER.recycle(this, this.handle);
    }
}
