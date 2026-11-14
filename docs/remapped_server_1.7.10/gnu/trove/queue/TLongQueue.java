package gnu.trove.queue;

import gnu.trove.TLongCollection;

public interface TLongQueue extends TLongCollection {

    long element();

    boolean offer(long i);

    long peek();

    long poll();
}
