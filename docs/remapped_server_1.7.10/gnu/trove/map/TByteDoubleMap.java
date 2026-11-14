package gnu.trove.map;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TByteDoubleIterator;
import gnu.trove.procedure.TByteDoubleProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TByteSet;
import java.util.Map;

public interface TByteDoubleMap {

    byte getNoEntryKey();

    double getNoEntryValue();

    double put(byte b0, double d0);

    double putIfAbsent(byte b0, double d0);

    void putAll(Map<? extends Byte, ? extends Double> map);

    void putAll(TByteDoubleMap tbytedoublemap);

    double get(byte b0);

    void clear();

    boolean isEmpty();

    double remove(byte b0);

    int size();

    TByteSet keySet();

    byte[] keys();

    byte[] keys(byte[] abyte);

    TDoubleCollection valueCollection();

    double[] values();

    double[] values(double[] adouble);

    boolean containsValue(double d0);

    boolean containsKey(byte b0);

    TByteDoubleIterator iterator();

    boolean forEachKey(TByteProcedure tbyteprocedure);

    boolean forEachValue(TDoubleProcedure tdoubleprocedure);

    boolean forEachEntry(TByteDoubleProcedure tbytedoubleprocedure);

    void transformValues(TDoubleFunction tdoublefunction);

    boolean retainEntries(TByteDoubleProcedure tbytedoubleprocedure);

    boolean increment(byte b0);

    boolean adjustValue(byte b0, double d0);

    double adjustOrPutValue(byte b0, double d0, double d1);
}
