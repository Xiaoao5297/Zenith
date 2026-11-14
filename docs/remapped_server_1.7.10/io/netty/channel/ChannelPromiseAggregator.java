package io.netty.channel;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public final class ChannelPromiseAggregator implements ChannelFutureListener {

    private final ChannelPromise aggregatePromise;
    private Set<ChannelPromise> pendingPromises;

    public ChannelPromiseAggregator(ChannelPromise channelpromise) {
        if (channelpromise == null) {
            throw new NullPointerException("aggregatePromise");
        } else {
            this.aggregatePromise = channelpromise;
        }
    }

    public ChannelPromiseAggregator add(ChannelPromise... achannelpromise) {
        if (achannelpromise == null) {
            throw new NullPointerException("promises");
        } else if (achannelpromise.length == 0) {
            return this;
        } else {
            synchronized (this) {
                if (this.pendingPromises == null) {
                    int i;

                    if (achannelpromise.length > 1) {
                        i = achannelpromise.length;
                    } else {
                        i = 2;
                    }

                    this.pendingPromises = new LinkedHashSet(i);
                }

                ChannelPromise[] achannelpromise1 = achannelpromise;
                int j = achannelpromise.length;

                for (int k = 0; k < j; ++k) {
                    ChannelPromise channelpromise = achannelpromise1[k];

                    if (channelpromise != null) {
                        this.pendingPromises.add(channelpromise);
                        channelpromise.addListener(this);
                    }
                }

                return this;
            }
        }
    }

    public synchronized void operationComplete(ChannelFuture channelfuture) throws Exception {
        if (this.pendingPromises == null) {
            this.aggregatePromise.setSuccess();
        } else {
            this.pendingPromises.remove(channelfuture);
            if (!channelfuture.isSuccess()) {
                this.aggregatePromise.setFailure(channelfuture.cause());
                Iterator iterator = this.pendingPromises.iterator();

                while (iterator.hasNext()) {
                    ChannelPromise channelpromise = (ChannelPromise) iterator.next();

                    channelpromise.setFailure(channelfuture.cause());
                }
            } else if (this.pendingPromises.isEmpty()) {
                this.aggregatePromise.setSuccess();
            }
        }

    }
}
