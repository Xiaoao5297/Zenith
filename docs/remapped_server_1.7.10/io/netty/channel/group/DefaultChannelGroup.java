package io.netty.channel.group;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ServerChannel;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.ConcurrentSet;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultChannelGroup extends AbstractSet<Channel> implements ChannelGroup {

    private static final AtomicInteger nextId = new AtomicInteger();
    private final String name;
    private final EventExecutor executor;
    private final ConcurrentSet<Channel> serverChannels;
    private final ConcurrentSet<Channel> nonServerChannels;
    private final ChannelFutureListener remover;

    public DefaultChannelGroup(EventExecutor eventexecutor) {
        this("group-0x" + Integer.toHexString(DefaultChannelGroup.nextId.incrementAndGet()), eventexecutor);
    }

    public DefaultChannelGroup(String s, EventExecutor eventexecutor) {
        this.serverChannels = new ConcurrentSet<>();
        this.nonServerChannels = new ConcurrentSet<>();
        this.remover = new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelfuture) throws Exception {
                DefaultChannelGroup.this.remove(channelfuture.channel());
            }
        };
        if (s == null) {
            throw new NullPointerException("name");
        } else {
            this.name = s;
            this.executor = eventexecutor;
        }
    }

    @Override
    public String name() {
        return this.name;
    }

    public boolean isEmpty() {
        return this.nonServerChannels.isEmpty() && this.serverChannels.isEmpty();
    }

    public int size() {
        return this.nonServerChannels.size() + this.serverChannels.size();
    }

    public boolean contains(Object object) {
        if (object instanceof Channel) {
            Channel channel = (Channel) object;

            return object instanceof ServerChannel ? this.serverChannels.contains(channel) : this.nonServerChannels.contains(channel);
        } else {
            return false;
        }
    }

    public boolean add(Channel channel) {
        ConcurrentSet concurrentset = channel instanceof ServerChannel ? this.serverChannels : this.nonServerChannels;
        boolean flag = concurrentset.add(channel);

        if (flag) {
            channel.closeFuture().addListener(this.remover);
        }

        return flag;
    }

    public boolean remove(Object object) {
        if (!(object instanceof Channel)) {
            return false;
        } else {
            Channel channel = (Channel) object;
            boolean flag;

            if (channel instanceof ServerChannel) {
                flag = this.serverChannels.remove(channel);
            } else {
                flag = this.nonServerChannels.remove(channel);
            }

            if (!flag) {
                return false;
            } else {
                channel.closeFuture().removeListener(this.remover);
                return true;
            }
        }
    }

    public void clear() {
        this.nonServerChannels.clear();
        this.serverChannels.clear();
    }

    public Iterator<Channel> iterator() {
        return new CombinedIterator<>(this.serverChannels.iterator(), this.nonServerChannels.iterator());
    }

    public Object[] toArray() {
        ArrayList arraylist = new ArrayList(this.size());

        arraylist.addAll(this.serverChannels);
        arraylist.addAll(this.nonServerChannels);
        return arraylist.toArray();
    }

    public <T> T[] toArray(T[] at) {
        ArrayList arraylist = new ArrayList(this.size());

        arraylist.addAll(this.serverChannels);
        arraylist.addAll(this.nonServerChannels);
        return arraylist.toArray(at);
    }

    @Override
    public ChannelGroupFuture close() {
        return this.close(ChannelMatchers.all());
    }

    @Override
    public ChannelGroupFuture disconnect() {
        return this.disconnect(ChannelMatchers.all());
    }

    @Override
    public ChannelGroupFuture deregister() {
        return this.deregister(ChannelMatchers.all());
    }

    @Override
    public ChannelGroupFuture write(Object object) {
        return this.write(object, ChannelMatchers.all());
    }

    private static Object safeDuplicate(Object object) {
        return object instanceof ByteBuf ? ((ByteBuf) object).duplicate().retain() : (object instanceof ByteBufHolder ? ((ByteBufHolder) object).duplicate().retain() : ReferenceCountUtil.retain(object));
    }

    @Override
    public ChannelGroupFuture write(Object object, ChannelMatcher channelmatcher) {
        if (object == null) {
            throw new NullPointerException("message");
        } else if (channelmatcher == null) {
            throw new NullPointerException("matcher");
        } else {
            LinkedHashMap linkedhashmap = new LinkedHashMap(this.size());
            Iterator iterator = this.nonServerChannels.iterator();

            while (iterator.hasNext()) {
                Channel channel = (Channel) iterator.next();

                if (channelmatcher.matches(channel)) {
                    linkedhashmap.put(channel, channel.write(safeDuplicate(object)));
                }
            }

            ReferenceCountUtil.release(object);
            return new DefaultChannelGroupFuture(this, linkedhashmap, this.executor);
        }
    }

    @Override
    public ChannelGroup flush() {
        return this.flush(ChannelMatchers.all());
    }

    @Override
    public ChannelGroupFuture flushAndWrite(Object object) {
        return this.flushAndWrite(object, ChannelMatchers.all());
    }

    @Override
    public ChannelGroupFuture disconnect(ChannelMatcher channelmatcher) {
        if (channelmatcher == null) {
            throw new NullPointerException("matcher");
        } else {
            LinkedHashMap linkedhashmap = new LinkedHashMap(this.size());
            Iterator iterator = this.serverChannels.iterator();

            Channel channel;

            while (iterator.hasNext()) {
                channel = (Channel) iterator.next();
                if (channelmatcher.matches(channel)) {
                    linkedhashmap.put(channel, channel.disconnect());
                }
            }

            iterator = this.nonServerChannels.iterator();

            while (iterator.hasNext()) {
                channel = (Channel) iterator.next();
                if (channelmatcher.matches(channel)) {
                    linkedhashmap.put(channel, channel.disconnect());
                }
            }

            return new DefaultChannelGroupFuture(this, linkedhashmap, this.executor);
        }
    }

    @Override
    public ChannelGroupFuture close(ChannelMatcher channelmatcher) {
        if (channelmatcher == null) {
            throw new NullPointerException("matcher");
        } else {
            LinkedHashMap linkedhashmap = new LinkedHashMap(this.size());
            Iterator iterator = this.serverChannels.iterator();

            Channel channel;

            while (iterator.hasNext()) {
                channel = (Channel) iterator.next();
                if (channelmatcher.matches(channel)) {
                    linkedhashmap.put(channel, channel.close());
                }
            }

            iterator = this.nonServerChannels.iterator();

            while (iterator.hasNext()) {
                channel = (Channel) iterator.next();
                if (channelmatcher.matches(channel)) {
                    linkedhashmap.put(channel, channel.close());
                }
            }

            return new DefaultChannelGroupFuture(this, linkedhashmap, this.executor);
        }
    }

    @Override
    public ChannelGroupFuture deregister(ChannelMatcher channelmatcher) {
        if (channelmatcher == null) {
            throw new NullPointerException("matcher");
        } else {
            LinkedHashMap linkedhashmap = new LinkedHashMap(this.size());
            Iterator iterator = this.serverChannels.iterator();

            Channel channel;

            while (iterator.hasNext()) {
                channel = (Channel) iterator.next();
                if (channelmatcher.matches(channel)) {
                    linkedhashmap.put(channel, channel.deregister());
                }
            }

            iterator = this.nonServerChannels.iterator();

            while (iterator.hasNext()) {
                channel = (Channel) iterator.next();
                if (channelmatcher.matches(channel)) {
                    linkedhashmap.put(channel, channel.deregister());
                }
            }

            return new DefaultChannelGroupFuture(this, linkedhashmap, this.executor);
        }
    }

    @Override
    public ChannelGroup flush(ChannelMatcher channelmatcher) {
        Iterator iterator = this.nonServerChannels.iterator();

        while (iterator.hasNext()) {
            Channel channel = (Channel) iterator.next();

            if (channelmatcher.matches(channel)) {
                channel.flush();
            }
        }

        return this;
    }

    @Override
    public ChannelGroupFuture flushAndWrite(Object object, ChannelMatcher channelmatcher) {
        if (object == null) {
            throw new NullPointerException("message");
        } else {
            LinkedHashMap linkedhashmap = new LinkedHashMap(this.size());
            Iterator iterator = this.nonServerChannels.iterator();

            while (iterator.hasNext()) {
                Channel channel = (Channel) iterator.next();

                if (channelmatcher.matches(channel)) {
                    linkedhashmap.put(channel, channel.writeAndFlush(safeDuplicate(object)));
                }
            }

            ReferenceCountUtil.release(object);
            return new DefaultChannelGroupFuture(this, linkedhashmap, this.executor);
        }
    }

    public int hashCode() {
        return System.identityHashCode(this);
    }

    public boolean equals(Object object) {
        return this == object;
    }

    public int compareTo(ChannelGroup channelgroup) {
        int i = this.name().compareTo(channelgroup.name());

        return i != 0 ? i : System.identityHashCode(this) - System.identityHashCode(channelgroup);
    }

    public String toString() {
        return this.getClass().getSimpleName() + "(name: " + this.name() + ", size: " + this.size() + ')';
    }
}
