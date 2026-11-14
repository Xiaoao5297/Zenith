package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EnumMap;
import java.util.Map;

@GwtCompatible(emulated = true)
public final class EnumBiMap<K extends Enum<K>, V extends Enum<V>> extends AbstractBiMap<K, V> {

    private transient Class<K> keyType;
    private transient Class<V> valueType;
    @GwtIncompatible("not needed in emulated source.")
    private static final long serialVersionUID = 0L;

    public static <K extends Enum<K>, V extends Enum<V>> EnumBiMap<K, V> create(Class<K> oclass, Class<V> oclass1) {
        return new EnumBiMap<>(oclass, oclass1);
    }

    public static <K extends Enum<K>, V extends Enum<V>> EnumBiMap<K, V> create(Map<K, V> map) {
        EnumBiMap enumbimap = create(inferKeyType(map), inferValueType(map));

        enumbimap.putAll(map);
        return enumbimap;
    }

    private EnumBiMap(Class<K> oclass, Class<V> oclass1) {
        super(WellBehavedMap.wrap(new EnumMap(oclass)), (Map) WellBehavedMap.wrap(new EnumMap(oclass1)));
        this.keyType = oclass;
        this.valueType = oclass1;
    }

    static <K extends Enum<K>> Class<K> inferKeyType(Map<K, ?> map) {
        if (map instanceof EnumBiMap) {
            return ((EnumBiMap) map).keyType();
        } else if (map instanceof EnumHashBiMap) {
            return ((EnumHashBiMap) map).keyType();
        } else {
            Preconditions.checkArgument(!map.isEmpty());
            return ((Enum) map.keySet().iterator().next()).getDeclaringClass();
        }
    }

    private static <V extends Enum<V>> Class<V> inferValueType(Map<?, V> map) {
        if (map instanceof EnumBiMap) {
            return ((EnumBiMap) map).valueType;
        } else {
            Preconditions.checkArgument(!map.isEmpty());
            return ((Enum) map.values().iterator().next()).getDeclaringClass();
        }
    }

    public Class<K> keyType() {
        return this.keyType;
    }

    public Class<V> valueType() {
        return this.valueType;
    }

    K checkKey(K k0) {
        return (Enum) Preconditions.checkNotNull(k0);
    }

    V checkValue(V v0) {
        return (Enum) Preconditions.checkNotNull(v0);
    }

    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
        objectoutputstream.defaultWriteObject();
        objectoutputstream.writeObject(this.keyType);
        objectoutputstream.writeObject(this.valueType);
        Serialization.writeMap(this, objectoutputstream);
    }

    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
        objectinputstream.defaultReadObject();
        this.keyType = (Class) objectinputstream.readObject();
        this.valueType = (Class) objectinputstream.readObject();
        this.setDelegates(WellBehavedMap.wrap(new EnumMap(this.keyType)), WellBehavedMap.wrap(new EnumMap(this.valueType)));
        Serialization.populateMap(this, objectinputstream);
    }
}
