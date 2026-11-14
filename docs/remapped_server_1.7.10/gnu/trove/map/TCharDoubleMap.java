package gnu.trove.map;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TCharDoubleIterator;
import gnu.trove.procedure.TCharDoubleProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TCharSet;
import java.util.Map;

public interface TCharDoubleMap {

    char getNoEntryKey();

    double getNoEntryValue();

    double put(char c0, double d0);

    double putIfAbsent(char c0, double d0);

    void putAll(Map<? extends Character, ? extends Double> map);

    void putAll(TCharDoubleMap tchardoublemap);

    double get(char c0);

    void clear();

    boolean isEmpty();

    double remove(char c0);

    int size();

    TCharSet keySet();

    char[] keys();

    char[] keys(char[] achar);

    TDoubleCollection valueCollection();

    double[] values();

    double[] values(double[] adouble);

    boolean containsValue(double d0);

    boolean containsKey(char c0);

    TCharDoubleIterator iterator();

    boolean forEachKey(TCharProcedure tcharprocedure);

    boolean forEachValue(TDoubleProcedure tdoubleprocedure);

    boolean forEachEntry(TCharDoubleProcedure tchardoubleprocedure);

    void transformValues(TDoubleFunction tdoublefunction);

    boolean retainEntries(TCharDoubleProcedure tchardoubleprocedure);

    boolean increment(char c0);

    boolean adjustValue(char c0, double d0);

    double adjustOrPutValue(char c0, double d0, double d1);
}
