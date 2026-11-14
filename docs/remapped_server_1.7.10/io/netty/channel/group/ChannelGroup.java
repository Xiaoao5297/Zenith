package io.netty.channel.group;

import io.netty.channel.Channel;
import java.util.Set;

public interface ChannelGroup extends Set<Channel>, Comparable<ChannelGroup> {

    String name();

    ChannelGroupFuture write(Object object);

    ChannelGroupFuture write(Object object, ChannelMatcher channelmatcher);

    ChannelGroup flush();

    ChannelGroup flush(ChannelMatcher channelmatcher);

    ChannelGroupFuture flushAndWrite(Object object);

    ChannelGroupFuture flushAndWrite(Object object, ChannelMatcher channelmatcher);

    ChannelGroupFuture disconnect();

    ChannelGroupFuture disconnect(ChannelMatcher channelmatcher);

    ChannelGroupFuture close();

    ChannelGroupFuture close(ChannelMatcher channelmatcher);

    /** @deprecated */
    @Deprecated
    ChannelGroupFuture deregister();

    /** @deprecated */
    @Deprecated
    ChannelGroupFuture deregister(ChannelMatcher channelmatcher);
}
