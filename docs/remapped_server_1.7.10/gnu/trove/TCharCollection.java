package gnu.trove;

import gnu.trove.iterator.TCharIterator;
import gnu.trove.procedure.TCharProcedure;
import java.util.Collection;

public interface TCharCollection {

    long serialVersionUID = 1L;

    char getNoEntryValue();

    int size();

    boolean isEmpty();

    boolean contains(char c0);

    TCharIterator iterator();

    char[] toArray();

    char[] toArray(char[] achar);

    boolean add(char c0);

    boolean remove(char c0);

    boolean containsAll(Collection<?> collection);

    boolean containsAll(TCharCollection tcharcollection);

    boolean containsAll(char[] achar);

    boolean addAll(Collection<? extends Character> collection);

    boolean addAll(TCharCollection tcharcollection);

    boolean addAll(char[] achar);

    boolean retainAll(Collection<?> collection);

    boolean retainAll(TCharCollection tcharcollection);

    boolean retainAll(char[] achar);

    boolean removeAll(Collection<?> collection);

    boolean removeAll(TCharCollection tcharcollection);

    boolean removeAll(char[] achar);

    void clear();

    boolean forEach(TCharProcedure tcharprocedure);

    boolean equals(Object object);

    int hashCode();
}
