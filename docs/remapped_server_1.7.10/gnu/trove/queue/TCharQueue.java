package gnu.trove.queue;

import gnu.trove.TCharCollection;

public interface TCharQueue extends TCharCollection {

    char element();

    boolean offer(char c0);

    char peek();

    char poll();
}
