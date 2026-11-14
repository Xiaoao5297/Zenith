package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public final class EnumHashBiMap<K extends Enum<K>, V> extends AbstractBiMap<K, V> {

    private transient Class<K> keyType;
    @GwtIncompatible("only needed in emulated source.")
    private static final long serialVersionUID = 0L;

    public static <K extends Enum<K>, V> EnumHashBiMap<K, V> create(Class<K> oclass) {
        return new EnumHashBiMap<>(oclass);
    }

    public static <K extends Enum<K>, V> EnumHashBiMap<K, V> create(Map<K, ? extends V> map) {
        EnumHashBiMap enumhashbimap = create(EnumBiMap.inferKeyType(map));

        enumhashbimap.putAll(map);
        return enumhashbimap;
    }

    private EnumHashBiMap(Class<K> oclass) {
        super(WellBehavedMap.wrap(new EnumMap(oclass)), (Map) Maps.newHashMapWithExpectedSize(((Enum[]) oclass.getEnumConstants()).length));
        this.keyType = oclass;
    }

    K checkKey(K k0) {
        return (Enum) Preconditions.checkNotNull(k0);
    }

    public V put(K k0, @Nullable V v0) {
        return super.put(k0, v0);
    }

    public V forcePut(K k0, @Nullable V v0) {
        return super.forcePut(k0, v0);
    }

    public Class<K> keyType() {
        return this.keyType;
    }

    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(ObjectOutputStream objectoutputstream) throws IOException {
        objectoutputstream.defaultWriteObject();
        objectoutputstream.writeObject(this.keyType);
        Serialization.writeMap(this, objectoutputstream);
    }

    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
        objectinputstream.defaultReadObject();
        this.keyType = (Class) objectinputstream.readObject();
        this.setDelegates(WellBehavedMap.wrap(new EnumMap(this.keyType)), new HashMap(((Enum[]) this.keyType.getEnumConstants()).length * 3 / 2));
        Serialization.populateMap(this, objectinputstream);
    }
}
