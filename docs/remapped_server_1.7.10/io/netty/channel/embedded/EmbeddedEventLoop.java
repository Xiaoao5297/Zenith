package io.netty.channel.embedded;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.concurrent.Future;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

final class EmbeddedEventLoop extends AbstractEventExecutor implements EventLoop {

    private final Queue<Runnable> tasks = new ArrayDeque(2);

    EmbeddedEventLoop() {}

    public void execute(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("command");
        } else {
            this.tasks.add(runnable);
        }
    }

    void runTasks() {
        while (true) {
            Runnable runnable = (Runnable) this.tasks.poll();

            if (runnable == null) {
                return;
            }

            runnable.run();
        }
    }

    @Override
    public Future<?> shutdownGracefully(long i, long j, TimeUnit timeunit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<?> terminationFuture() {
        throw new UnsupportedOperationException();
    }

    /** @deprecated */
    @Deprecated
    @Override
    public void shutdown() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isShuttingDown() {
        return false;
    }

    public boolean isShutdown() {
        return false;
    }

    public boolean isTerminated() {
        return false;
    }

    public boolean awaitTermination(long i, TimeUnit timeunit) throws InterruptedException {
        Thread.sleep(timeunit.toMillis(i));
        return false;
    }

    @Override
    public ChannelFuture register(Channel channel) {
        return this.register(channel, channel.newPromise());
    }

    @Override
    public ChannelFuture register(Channel channel, ChannelPromise channelpromise) {
        channel.unsafe().register(this, channelpromise);
        return channelpromise;
    }

    @Override
    public boolean inEventLoop() {
        return true;
    }

    @Override
    public boolean inEventLoop(Thread thread) {
        return true;
    }

    @Override
    public EventLoop next() {
        return this;
    }

    @Override
    public EventLoopGroup parent() {
        return this;
    }
}
