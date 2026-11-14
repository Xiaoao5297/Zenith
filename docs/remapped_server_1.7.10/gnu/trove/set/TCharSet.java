package gnu.trove.set;

import gnu.trove.TCharCollection;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.procedure.TCharProcedure;
import java.util.Collection;

public interface TCharSet extends TCharCollection {

    @Override
    char getNoEntryValue();

    @Override
    int size();

    @Override
    boolean isEmpty();

    @Override
    boolean contains(char c0);

    @Override
    TCharIterator iterator();

    @Override
    char[] toArray();

    @Override
    char[] toArray(char[] achar);

    @Override
    boolean add(char c0);

    @Override
    boolean remove(char c0);

    @Override
    boolean containsAll(Collection<?> collection);

    @Override
    boolean containsAll(TCharCollection tcharcollection);

    @Override
    boolean containsAll(char[] achar);

    @Override
    boolean addAll(Collection<? extends Character> collection);

    @Override
    boolean addAll(TCharCollection tcharcollection);

    @Override
    boolean addAll(char[] achar);

    @Override
    boolean retainAll(Collection<?> collection);

    @Override
    boolean retainAll(TCharCollection tcharcollection);

    @Override
    boolean retainAll(char[] achar);

    @Override
    boolean removeAll(Collection<?> collection);

    @Override
    boolean removeAll(TCharCollection tcharcollection);

    @Override
    boolean removeAll(char[] achar);

    @Override
    void clear();

    @Override
    boolean forEach(TCharProcedure tcharprocedure);

    @Override
    boolean equals(Object object);

    @Override
    int hashCode();
}
