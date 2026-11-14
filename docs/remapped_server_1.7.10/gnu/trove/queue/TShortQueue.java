package gnu.trove.queue;

import gnu.trove.TShortCollection;

public interface TShortQueue extends TShortCollection {

    short element();

    boolean offer(short short0);

    short peek();

    short poll();
}
