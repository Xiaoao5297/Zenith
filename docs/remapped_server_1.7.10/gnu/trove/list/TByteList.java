package gnu.trove.list;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.procedure.TByteProcedure;
import java.util.Random;

public interface TByteList extends TByteCollection {

    @Override
    byte getNoEntryValue();

    @Override
    int size();

    @Override
    boolean isEmpty();

    @Override
    boolean add(byte b0);

    void add(byte[] abyte);

    void add(byte[] abyte, int i, int j);

    void insert(int i, byte b0);

    void insert(int i, byte[] abyte);

    void insert(int i, byte[] abyte, int j, int k);

    byte get(int i);

    byte set(int i, byte b0);

    void set(int i, byte[] abyte);

    void set(int i, byte[] abyte, int j, int k);

    byte replace(int i, byte b0);

    @Override
    void clear();

    @Override
    boolean remove(byte b0);

    byte removeAt(int i);

    void remove(int i, int j);

    void transformValues(TByteFunction tbytefunction);

    void reverse();

    void reverse(int i, int j);

    void shuffle(Random random);

    TByteList subList(int i, int j);

    @Override
    byte[] toArray();

    byte[] toArray(int i, int j);

    @Override
    byte[] toArray(byte[] abyte);

    byte[] toArray(byte[] abyte, int i, int j);

    byte[] toArray(byte[] abyte, int i, int j, int k);

    @Override
    boolean forEach(TByteProcedure tbyteprocedure);

    boolean forEachDescending(TByteProcedure tbyteprocedure);

    void sort();

    void sort(int i, int j);

    void fill(byte b0);

    void fill(int i, int j, byte b0);

    int binarySearch(byte b0);

    int binarySearch(byte b0, int i, int j);

    int indexOf(byte b0);

    int indexOf(int i, byte b0);

    int lastIndexOf(byte b0);

    int lastIndexOf(int i, byte b0);

    @Override
    boolean contains(byte b0);

    TByteList grep(TByteProcedure tbyteprocedure);

    TByteList inverseGrep(TByteProcedure tbyteprocedure);

    byte max();

    byte min();

    byte sum();
}
