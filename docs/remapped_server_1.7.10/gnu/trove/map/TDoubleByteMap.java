package gnu.trove.map;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TDoubleByteIterator;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TDoubleByteProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TDoubleSet;
import java.util.Map;

public interface TDoubleByteMap {

    double getNoEntryKey();

    byte getNoEntryValue();

    byte put(double d0, byte b0);

    byte putIfAbsent(double d0, byte b0);

    void putAll(Map<? extends Double, ? extends Byte> map);

    void putAll(TDoubleByteMap tdoublebytemap);

    byte get(double d0);

    void clear();

    boolean isEmpty();

    byte remove(double d0);

    int size();

    TDoubleSet keySet();

    double[] keys();

    double[] keys(double[] adouble);

    TByteCollection valueCollection();

    byte[] values();

    byte[] values(byte[] abyte);

    boolean containsValue(byte b0);

    boolean containsKey(double d0);

    TDoubleByteIterator iterator();

    boolean forEachKey(TDoubleProcedure tdoubleprocedure);

    boolean forEachValue(TByteProcedure tbyteprocedure);

    boolean forEachEntry(TDoubleByteProcedure tdoublebyteprocedure);

    void transformValues(TByteFunction tbytefunction);

    boolean retainEntries(TDoubleByteProcedure tdoublebyteprocedure);

    boolean increment(double d0);

    boolean adjustValue(double d0, byte b0);

    byte adjustOrPutValue(double d0, byte b0, byte b1);
}
