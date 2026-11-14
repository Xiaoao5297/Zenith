package gnu.trove.list;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.procedure.TLongProcedure;
import java.util.Random;

public interface TLongList extends TLongCollection {

    @Override
    long getNoEntryValue();

    @Override
    int size();

    @Override
    boolean isEmpty();

    @Override
    boolean add(long i);

    void add(long[] along);

    void add(long[] along, int i, int j);

    void insert(int i, long j);

    void insert(int i, long[] along);

    void insert(int i, long[] along, int j, int k);

    long get(int i);

    long set(int i, long j);

    void set(int i, long[] along);

    void set(int i, long[] along, int j, int k);

    long replace(int i, long j);

    @Override
    void clear();

    @Override
    boolean remove(long i);

    long removeAt(int i);

    void remove(int i, int j);

    void transformValues(TLongFunction tlongfunction);

    void reverse();

    void reverse(int i, int j);

    void shuffle(Random random);

    TLongList subList(int i, int j);

    @Override
    long[] toArray();

    long[] toArray(int i, int j);

    @Override
    long[] toArray(long[] along);

    long[] toArray(long[] along, int i, int j);

    long[] toArray(long[] along, int i, int j, int k);

    @Override
    boolean forEach(TLongProcedure tlongprocedure);

    boolean forEachDescending(TLongProcedure tlongprocedure);

    void sort();

    void sort(int i, int j);

    void fill(long i);

    void fill(int i, int j, long k);

    int binarySearch(long i);

    int binarySearch(long i, int j, int k);

    int indexOf(long i);

    int indexOf(int i, long j);

    int lastIndexOf(long i);

    int lastIndexOf(int i, long j);

    @Override
    boolean contains(long i);

    TLongList grep(TLongProcedure tlongprocedure);

    TLongList inverseGrep(TLongProcedure tlongprocedure);

    long max();

    long min();

    long sum();
}
