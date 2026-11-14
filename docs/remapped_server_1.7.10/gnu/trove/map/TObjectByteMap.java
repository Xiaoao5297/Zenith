package gnu.trove.map;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TObjectByteIterator;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TObjectByteProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.util.Map;
import java.util.Set;

public interface TObjectByteMap<K> {

    byte getNoEntryValue();

    int size();

    boolean isEmpty();

    boolean containsKey(Object object);

    boolean containsValue(byte b0);

    byte get(Object object);

    byte put(K k0, byte b0);

    byte putIfAbsent(K k0, byte b0);

    byte remove(Object object);

    void putAll(Map<? extends K, ? extends Byte> map);

    void putAll(TObjectByteMap<? extends K> tobjectbytemap);

    void clear();

    Set<K> keySet();

    Object[] keys();

    K[] keys(K[] ak);

    TByteCollection valueCollection();

    byte[] values();

    byte[] values(byte[] abyte);

    TObjectByteIterator<K> iterator();

    boolean increment(K k0);

    boolean adjustValue(K k0, byte b0);

    byte adjustOrPutValue(K k0, byte b0, byte b1);

    boolean forEachKey(TObjectProcedure<? super K> tobjectprocedure);

    boolean forEachValue(TByteProcedure tbyteprocedure);

    boolean forEachEntry(TObjectByteProcedure<? super K> tobjectbyteprocedure);

    void transformValues(TByteFunction tbytefunction);

    boolean retainEntries(TObjectByteProcedure<? super K> tobjectbyteprocedure);

    boolean equals(Object object);

    int hashCode();
}
