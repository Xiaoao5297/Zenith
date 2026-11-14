package gnu.trove.map;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TIntByteIterator;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TIntByteProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.util.Map;

public interface TIntByteMap {

    int getNoEntryKey();

    byte getNoEntryValue();

    byte put(int i, byte b0);

    byte putIfAbsent(int i, byte b0);

    void putAll(Map<? extends Integer, ? extends Byte> map);

    void putAll(TIntByteMap tintbytemap);

    byte get(int i);

    void clear();

    boolean isEmpty();

    byte remove(int i);

    int size();

    TIntSet keySet();

    int[] keys();

    int[] keys(int[] aint);

    TByteCollection valueCollection();

    byte[] values();

    byte[] values(byte[] abyte);

    boolean containsValue(byte b0);

    boolean containsKey(int i);

    TIntByteIterator iterator();

    boolean forEachKey(TIntProcedure tintprocedure);

    boolean forEachValue(TByteProcedure tbyteprocedure);

    boolean forEachEntry(TIntByteProcedure tintbyteprocedure);

    void transformValues(TByteFunction tbytefunction);

    boolean retainEntries(TIntByteProcedure tintbyteprocedure);

    boolean increment(int i);

    boolean adjustValue(int i, byte b0);

    byte adjustOrPutValue(int i, byte b0, byte b1);
}
