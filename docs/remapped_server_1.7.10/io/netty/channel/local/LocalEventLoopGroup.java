package io.netty.channel.local;

import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import java.util.concurrent.ThreadFactory;

public class LocalEventLoopGroup extends MultithreadEventLoopGroup {

    public LocalEventLoopGroup() {
        this(0);
    }

    public LocalEventLoopGroup(int i) {
        this(i, (ThreadFactory) null);
    }

    public LocalEventLoopGroup(int i, ThreadFactory threadfactory) {
        super(i, threadfactory);
    }

    @Override
    protected EventExecutor newChild(ThreadFactory threadfactory, Object... aobject) throws Exception {
        return new LocalEventLoop(this, threadfactory);
    }
}
