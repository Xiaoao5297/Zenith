package io.netty.channel.group;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.BlockingOperationException;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ImmediateEventExecutor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

final class DefaultChannelGroupFuture extends DefaultPromise<Void> implements ChannelGroupFuture {

    private final ChannelGroup group;
    private final Map<Channel, ChannelFuture> futures;
    private int successCount;
    private int failureCount;
    private final ChannelFutureListener childListener = new ChannelFutureListener() {
        public void operationComplete(ChannelFuture channelfuture) throws Exception {
            boolean flag = channelfuture.isSuccess();
            DefaultChannelGroupFuture defaultchannelgroupfuture = DefaultChannelGroupFuture.this;
            boolean flag1;

            synchronized (DefaultChannelGroupFuture.this) {
                if (flag) {
                    DefaultChannelGroupFuture.this.successCount++;
                } else {
                    DefaultChannelGroupFuture.this.failureCount++;
                }

                flag1 = DefaultChannelGroupFuture.this.successCount + DefaultChannelGroupFuture.this.failureCount == DefaultChannelGroupFuture.this.futures.size();

                assert DefaultChannelGroupFuture.this.successCount + DefaultChannelGroupFuture.this.failureCount <= DefaultChannelGroupFuture.this.futures.size();
            }

            if (flag1) {
                if (DefaultChannelGroupFuture.this.failureCount > 0) {
                    ArrayList arraylist = new ArrayList(DefaultChannelGroupFuture.this.failureCount);
                    Iterator iterator = DefaultChannelGroupFuture.this.futures.values().iterator();

                    while (iterator.hasNext()) {
                        ChannelFuture channelfuture1 = (ChannelFuture) iterator.next();

                        if (!channelfuture1.isSuccess()) {
                            arraylist.add(new DefaultChannelGroupFuture.DefaultEntry<>(channelfuture1.channel(), channelfuture1.cause()));
                        }
                    }

                    DefaultChannelGroupFuture.this.setFailure0(new ChannelGroupException(arraylist));
                } else {
                    DefaultChannelGroupFuture.this.setSuccess0();
                }
            }

        }
    };

    public DefaultChannelGroupFuture(ChannelGroup channelgroup, Collection<ChannelFuture> collection, EventExecutor eventexecutor) {
        super(eventexecutor);
        if (channelgroup == null) {
            throw new NullPointerException("group");
        } else if (collection == null) {
            throw new NullPointerException("futures");
        } else {
            this.group = channelgroup;
            LinkedHashMap linkedhashmap = new LinkedHashMap();
            Iterator iterator = collection.iterator();

            ChannelFuture channelfuture;

            while (iterator.hasNext()) {
                channelfuture = (ChannelFuture) iterator.next();
                linkedhashmap.put(channelfuture.channel(), channelfuture);
            }

            this.futures = Collections.unmodifiableMap(linkedhashmap);
            iterator = this.futures.values().iterator();

            while (iterator.hasNext()) {
                channelfuture = (ChannelFuture) iterator.next();
                channelfuture.addListener(this.childListener);
            }

            if (this.futures.isEmpty()) {
                this.setSuccess0();
            }

        }
    }

    DefaultChannelGroupFuture(ChannelGroup channelgroup, Map<Channel, ChannelFuture> map, EventExecutor eventexecutor) {
        super(eventexecutor);
        this.group = channelgroup;
        this.futures = Collections.unmodifiableMap(map);
        Iterator iterator = this.futures.values().iterator();

        while (iterator.hasNext()) {
            ChannelFuture channelfuture = (ChannelFuture) iterator.next();

            channelfuture.addListener(this.childListener);
        }

        if (this.futures.isEmpty()) {
            this.setSuccess0();
        }

    }

    @Override
    public ChannelGroup group() {
        return this.group;
    }

    @Override
    public ChannelFuture find(Channel channel) {
        return (ChannelFuture) this.futures.get(channel);
    }

    @Override
    public Iterator<ChannelFuture> iterator() {
        return this.futures.values().iterator();
    }

    @Override
    public synchronized boolean isPartialSuccess() {
        return this.successCount != 0 && this.successCount != this.futures.size();
    }

    @Override
    public synchronized boolean isPartialFailure() {
        return this.failureCount != 0 && this.failureCount != this.futures.size();
    }

    @Override
    public DefaultChannelGroupFuture addListener(GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {
        super.addListener(genericfuturelistener);
        return this;
    }

    @Override
    public DefaultChannelGroupFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
        super.addListeners(agenericfuturelistener);
        return this;
    }

    @Override
    public DefaultChannelGroupFuture removeListener(GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {
        super.removeListener(genericfuturelistener);
        return this;
    }

    @Override
    public DefaultChannelGroupFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
        super.removeListeners(agenericfuturelistener);
        return this;
    }

    @Override
    public DefaultChannelGroupFuture await() throws InterruptedException {
        super.await();
        return this;
    }

    @Override
    public DefaultChannelGroupFuture awaitUninterruptibly() {
        super.awaitUninterruptibly();
        return this;
    }

    @Override
    public DefaultChannelGroupFuture syncUninterruptibly() {
        super.syncUninterruptibly();
        return this;
    }

    @Override
    public DefaultChannelGroupFuture sync() throws InterruptedException {
        super.sync();
        return this;
    }

    @Override
    public ChannelGroupException cause() {
        return (ChannelGroupException) super.cause();
    }

    private void setSuccess0() {
        super.setSuccess((Object) null);
    }

    private void setFailure0(ChannelGroupException channelgroupexception) {
        super.setFailure(channelgroupexception);
    }

    public DefaultChannelGroupFuture setSuccess(Void ovoid) {
        throw new IllegalStateException();
    }

    public boolean trySuccess(Void ovoid) {
        throw new IllegalStateException();
    }

    @Override
    public DefaultChannelGroupFuture setFailure(Throwable throwable) {
        throw new IllegalStateException();
    }

    @Override
    public boolean tryFailure(Throwable throwable) {
        throw new IllegalStateException();
    }

    @Override
    protected void checkDeadLock() {
        EventExecutor eventexecutor = this.executor();

        if (eventexecutor != null && eventexecutor != ImmediateEventExecutor.INSTANCE && eventexecutor.inEventLoop()) {
            throw new BlockingOperationException();
        }
    }

    private static final class DefaultEntry<K, V> implements Entry<K, V> {

        private final K key;
        private final V value;

        public DefaultEntry(K k0, V v0) {
            this.key = k0;
            this.value = v0;
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.value;
        }

        public V setValue(V v0) {
            throw new UnsupportedOperationException("read-only");
        }
    }
}
