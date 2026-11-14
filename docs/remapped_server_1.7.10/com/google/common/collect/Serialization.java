package com.google.common.collect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

final class Serialization {

    private Serialization() {}

    static int readCount(ObjectInputStream objectinputstream) throws IOException {
        return objectinputstream.readInt();
    }

    static <K, V> void writeMap(Map<K, V> map, ObjectOutputStream objectoutputstream) throws IOException {
        objectoutputstream.writeInt(map.size());
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            objectoutputstream.writeObject(entry.getKey());
            objectoutputstream.writeObject(entry.getValue());
        }

    }

    static <K, V> void populateMap(Map<K, V> map, ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
        int i = objectinputstream.readInt();

        populateMap(map, objectinputstream, i);
    }

    static <K, V> void populateMap(Map<K, V> map, ObjectInputStream objectinputstream, int i) throws IOException, ClassNotFoundException {
        for (int j = 0; j < i; ++j) {
            Object object = objectinputstream.readObject();
            Object object1 = objectinputstream.readObject();

            map.put(object, object1);
        }

    }

    static <E> void writeMultiset(Multiset<E> multiset, ObjectOutputStream objectoutputstream) throws IOException {
        int i = multiset.entrySet().size();

        objectoutputstream.writeInt(i);
        Iterator iterator = multiset.entrySet().iterator();

        while (iterator.hasNext()) {
            Multiset.Entry multiset_entry = (Multiset.Entry) iterator.next();

            objectoutputstream.writeObject(multiset_entry.getElement());
            objectoutputstream.writeInt(multiset_entry.getCount());
        }

    }

    static <E> void populateMultiset(Multiset<E> multiset, ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
        int i = objectinputstream.readInt();

        populateMultiset(multiset, objectinputstream, i);
    }

    static <E> void populateMultiset(Multiset<E> multiset, ObjectInputStream objectinputstream, int i) throws IOException, ClassNotFoundException {
        for (int j = 0; j < i; ++j) {
            Object object = objectinputstream.readObject();
            int k = objectinputstream.readInt();

            multiset.add(object, k);
        }

    }

    static <K, V> void writeMultimap(Multimap<K, V> multimap, ObjectOutputStream objectoutputstream) throws IOException {
        objectoutputstream.writeInt(multimap.asMap().size());
        Iterator iterator = multimap.asMap().entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            objectoutputstream.writeObject(entry.getKey());
            objectoutputstream.writeInt(((Collection) entry.getValue()).size());
            Iterator iterator1 = ((Collection) entry.getValue()).iterator();

            while (iterator1.hasNext()) {
                Object object = iterator1.next();

                objectoutputstream.writeObject(object);
            }
        }

    }

    static <K, V> void populateMultimap(Multimap<K, V> multimap, ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
        int i = objectinputstream.readInt();

        populateMultimap(multimap, objectinputstream, i);
    }

    static <K, V> void populateMultimap(Multimap<K, V> multimap, ObjectInputStream objectinputstream, int i) throws IOException, ClassNotFoundException {
        for (int j = 0; j < i; ++j) {
            Object object = objectinputstream.readObject();
            Collection collection = multimap.get(object);
            int k = objectinputstream.readInt();

            for (int l = 0; l < k; ++l) {
                Object object1 = objectinputstream.readObject();

                collection.add(object1);
            }
        }

    }

    static <T> Serialization.FieldSetter<T> getFieldSetter(Class<T> oclass, String s) {
        try {
            Field field = oclass.getDeclaredField(s);

            return new Serialization.FieldSetter<>(field);
        } catch (NoSuchFieldException nosuchfieldexception) {
            throw new AssertionError(nosuchfieldexception);
        }
    }

    static final class FieldSetter<T> {

        private final Field field;

        private FieldSetter(Field field) {
            this.field = field;
            field.setAccessible(true);
        }

        void set(T t0, Object object) {
            try {
                this.field.set(t0, object);
            } catch (IllegalAccessException illegalaccessexception) {
                throw new AssertionError(illegalaccessexception);
            }
        }

        void set(T t0, int i) {
            try {
                this.field.set(t0, i);
            } catch (IllegalAccessException illegalaccessexception) {
                throw new AssertionError(illegalaccessexception);
            }
        }
    }
}
