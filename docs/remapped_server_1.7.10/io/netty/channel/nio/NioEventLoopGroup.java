package io.netty.channel.nio;

import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.concurrent.ThreadFactory;

public class NioEventLoopGroup extends MultithreadEventLoopGroup {

    public NioEventLoopGroup() {
        this(0);
    }

    public NioEventLoopGroup(int i) {
        this(i, (ThreadFactory) null);
    }

    public NioEventLoopGroup(int i, ThreadFactory threadfactory) {
        this(i, threadfactory, SelectorProvider.provider());
    }

    public NioEventLoopGroup(int i, ThreadFactory threadfactory, SelectorProvider selectorprovider) {
        super(i, threadfactory, selectorprovider);
    }

    public void setIoRatio(int i) {
        Iterator iterator = this.children().iterator();

        while (iterator.hasNext()) {
            EventExecutor eventexecutor = (EventExecutor) iterator.next();

            ((NioEventLoop) eventexecutor).setIoRatio(i);
        }

    }

    public void rebuildSelectors() {
        Iterator iterator = this.children().iterator();

        while (iterator.hasNext()) {
            EventExecutor eventexecutor = (EventExecutor) iterator.next();

            ((NioEventLoop) eventexecutor).rebuildSelector();
        }

    }

    @Override
    protected EventExecutor newChild(ThreadFactory threadfactory, Object... aobject) throws Exception {
        return new NioEventLoop(this, threadfactory, (SelectorProvider) aobject[0]);
    }
}
