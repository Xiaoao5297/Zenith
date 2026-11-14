package gnu.trove.impl.unmodifiable;

import gnu.trove.function.TFloatFunction;
import gnu.trove.list.TFloatList;
import gnu.trove.procedure.TFloatProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TUnmodifiableFloatList extends TUnmodifiableFloatCollection implements TFloatList {

    static final long serialVersionUID = -283967356065247728L;
    final TFloatList list;

    public TUnmodifiableFloatList(TFloatList tfloatlist) {
        super(tfloatlist);
        this.list = tfloatlist;
    }

    @Override
    public boolean equals(Object object) {
        return object == this || this.list.equals(object);
    }

    @Override
    public int hashCode() {
        return this.list.hashCode();
    }

    @Override
    public float get(int i) {
        return this.list.get(i);
    }

    @Override
    public int indexOf(float f) {
        return this.list.indexOf(f);
    }

    @Override
    public int lastIndexOf(float f) {
        return this.list.lastIndexOf(f);
    }

    @Override
    public float[] toArray(int i, int j) {
        return this.list.toArray(i, j);
    }

    @Override
    public float[] toArray(float[] afloat, int i, int j) {
        return this.list.toArray(afloat, i, j);
    }

    @Override
    public float[] toArray(float[] afloat, int i, int j, int k) {
        return this.list.toArray(afloat, i, j, k);
    }

    @Override
    public boolean forEachDescending(TFloatProcedure tfloatprocedure) {
        return this.list.forEachDescending(tfloatprocedure);
    }

    @Override
    public int binarySearch(float f) {
        return this.list.binarySearch(f);
    }

    @Override
    public int binarySearch(float f, int i, int j) {
        return this.list.binarySearch(f, i, j);
    }

    @Override
    public int indexOf(int i, float f) {
        return this.list.indexOf(i, f);
    }

    @Override
    public int lastIndexOf(int i, float f) {
        return this.list.lastIndexOf(i, f);
    }

    @Override
    public TFloatList grep(TFloatProcedure tfloatprocedure) {
        return this.list.grep(tfloatprocedure);
    }

    @Override
    public TFloatList inverseGrep(TFloatProcedure tfloatprocedure) {
        return this.list.inverseGrep(tfloatprocedure);
    }

    @Override
    public float max() {
        return this.list.max();
    }

    @Override
    public float min() {
        return this.list.min();
    }

    @Override
    public float sum() {
        return this.list.sum();
    }

    @Override
    public TFloatList subList(int i, int j) {
        return new TUnmodifiableFloatList(this.list.subList(i, j));
    }

    private Object readResolve() {
        return this.list instanceof RandomAccess ? new TUnmodifiableRandomAccessFloatList(this.list) : this;
    }

    @Override
    public void add(float[] afloat) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(float[] afloat, int i, int j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float removeAt(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(int i, int j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int i, float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int i, float[] afloat) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insert(int i, float[] afloat, int j, int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float set(int i, float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int i, float[] afloat) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int i, float[] afloat, int j, int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float replace(int i, float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TFloatFunction tfloatfunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reverse() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reverse(int i, int j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void shuffle(Random random) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sort() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sort(int i, int j) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fill(float f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fill(int i, int j, float f) {
        throw new UnsupportedOperationException();
    }
}
