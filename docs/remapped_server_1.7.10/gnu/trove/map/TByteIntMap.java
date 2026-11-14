package gnu.trove.map;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TByteIntIterator;
import gnu.trove.procedure.TByteIntProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TByteSet;
import java.util.Map;

public interface TByteIntMap {

    byte getNoEntryKey();

    int getNoEntryValue();

    int put(byte b0, int i);

    int putIfAbsent(byte b0, int i);

    void putAll(Map<? extends Byte, ? extends Integer> map);

    void putAll(TByteIntMap tbyteintmap);

    int get(byte b0);

    void clear();

    boolean isEmpty();

    int remove(byte b0);

    int size();

    TByteSet keySet();

    byte[] keys();

    byte[] keys(byte[] abyte);

    TIntCollection valueCollection();

    int[] values();

    int[] values(int[] aint);

    boolean containsValue(int i);

    boolean containsKey(byte b0);

    TByteIntIterator iterator();

    boolean forEachKey(TByteProcedure tbyteprocedure);

    boolean forEachValue(TIntProcedure tintprocedure);

    boolean forEachEntry(TByteIntProcedure tbyteintprocedure);

    void transformValues(TIntFunction tintfunction);

    boolean retainEntries(TByteIntProcedure tbyteintprocedure);

    boolean increment(byte b0);

    boolean adjustValue(byte b0, int i);

    int adjustOrPutValue(byte b0, int i, int j);
}
