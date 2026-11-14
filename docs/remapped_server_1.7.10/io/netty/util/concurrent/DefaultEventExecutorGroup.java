package io.netty.util.concurrent;

import java.util.concurrent.ThreadFactory;

public class DefaultEventExecutorGroup extends MultithreadEventExecutorGroup {

    public DefaultEventExecutorGroup(int i) {
        this(i, (ThreadFactory) null);
    }

    public DefaultEventExecutorGroup(int i, ThreadFactory threadfactory) {
        super(i, threadfactory);
    }

    @Override
    protected EventExecutor newChild(ThreadFactory threadfactory, Object... aobject) throws Exception {
        return new DefaultEventExecutor(this, threadfactory);
    }
}
