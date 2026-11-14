package io.netty.channel.oio;

import io.netty.channel.ThreadPerChannelEventLoopGroup;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class OioEventLoopGroup extends ThreadPerChannelEventLoopGroup {

    public OioEventLoopGroup() {
        this(0);
    }

    public OioEventLoopGroup(int i) {
        this(i, Executors.defaultThreadFactory());
    }

    public OioEventLoopGroup(int i, ThreadFactory threadfactory) {
        super(i, threadfactory);
    }
}
