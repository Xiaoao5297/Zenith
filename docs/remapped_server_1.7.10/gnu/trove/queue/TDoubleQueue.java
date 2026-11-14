package gnu.trove.queue;

import gnu.trove.TDoubleCollection;

public interface TDoubleQueue extends TDoubleCollection {

    double element();

    boolean offer(double d0);

    double peek();

    double poll();
}
