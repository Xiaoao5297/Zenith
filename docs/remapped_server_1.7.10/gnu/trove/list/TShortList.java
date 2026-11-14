package gnu.trove.list;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.procedure.TShortProcedure;
import java.util.Random;

public interface TShortList extends TShortCollection {

    @Override
    short getNoEntryValue();

    @Override
    int size();

    @Override
    boolean isEmpty();

    @Override
    boolean add(short short0);

    void add(short[] ashort);

    void add(short[] ashort, int i, int j);

    void insert(int i, short short0);

    void insert(int i, short[] ashort);

    void insert(int i, short[] ashort, int j, int k);

    short get(int i);

    short set(int i, short short0);

    void set(int i, short[] ashort);

    void set(int i, short[] ashort, int j, int k);

    short replace(int i, short short0);

    @Override
    void clear();

    @Override
    boolean remove(short short0);

    short removeAt(int i);

    void remove(int i, int j);

    void transformValues(TShortFunction tshortfunction);

    void reverse();

    void reverse(int i, int j);

    void shuffle(Random random);

    TShortList subList(int i, int j);

    @Override
    short[] toArray();

    short[] toArray(int i, int j);

    @Override
    short[] toArray(short[] ashort);

    short[] toArray(short[] ashort, int i, int j);

    short[] toArray(short[] ashort, int i, int j, int k);

    @Override
    boolean forEach(TShortProcedure tshortprocedure);

    boolean forEachDescending(TShortProcedure tshortprocedure);

    void sort();

    void sort(int i, int j);

    void fill(short short0);

    void fill(int i, int j, short short0);

    int binarySearch(short short0);

    int binarySearch(short short0, int i, int j);

    int indexOf(short short0);

    int indexOf(int i, short short0);

    int lastIndexOf(short short0);

    int lastIndexOf(int i, short short0);

    @Override
    boolean contains(short short0);

    TShortList grep(TShortProcedure tshortprocedure);

    TShortList inverseGrep(TShortProcedure tshortprocedure);

    short max();

    short min();

    short sum();
}
