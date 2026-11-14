package io.netty.channel;

public class ThreadPerChannelEventLoop extends SingleThreadEventLoop {

    private final ThreadPerChannelEventLoopGroup parent;
    private Channel ch;

    public ThreadPerChannelEventLoop(ThreadPerChannelEventLoopGroup threadperchanneleventloopgroup) {
        super((EventLoopGroup) threadperchanneleventloopgroup, threadperchanneleventloopgroup.threadFactory, true);
        this.parent = threadperchanneleventloopgroup;
    }

    @Override
    public ChannelFuture register(Channel channel, ChannelPromise channelpromise) {
        return super.register(channel, channelpromise).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelfuture) throws Exception {
                if (channelfuture.isSuccess()) {
                    ThreadPerChannelEventLoop.this.ch = channelfuture.channel();
                } else {
                    ThreadPerChannelEventLoop.this.deregister();
                }

            }
        });
    }

    @Override
    protected void run() {
        while (true) {
            Runnable runnable = this.takeTask();

            if (runnable != null) {
                runnable.run();
                this.updateLastExecutionTime();
            }

            Channel channel = this.ch;

            if (this.isShuttingDown()) {
                if (channel != null) {
                    channel.unsafe().close(channel.unsafe().voidPromise());
                }

                if (this.confirmShutdown()) {
                    return;
                }
            } else if (channel != null && !channel.isRegistered()) {
                this.runAllTasks();
                this.deregister();
            }
        }
    }

    protected void deregister() {
        this.ch = null;
        this.parent.activeChildren.remove(this);
        this.parent.idleChildren.add(this);
    }
}
