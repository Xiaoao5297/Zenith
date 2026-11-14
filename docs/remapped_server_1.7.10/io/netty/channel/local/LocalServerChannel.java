package io.netty.channel.local;

import io.netty.channel.AbstractServerChannel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.EventLoop;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.Queue;

public class LocalServerChannel extends AbstractServerChannel {

    private final ChannelConfig config = new DefaultChannelConfig(this);
    private final Queue<Object> inboundBuffer = new ArrayDeque();
    private final Runnable shutdownHook = new Runnable() {
        public void run() {
            LocalServerChannel.this.unsafe().close(LocalServerChannel.this.unsafe().voidPromise());
        }
    };
    private volatile int state;
    private volatile LocalAddress localAddress;
    private volatile boolean acceptInProgress;

    public LocalServerChannel() {}

    @Override
    public ChannelConfig config() {
        return this.config;
    }

    @Override
    public LocalAddress localAddress() {
        return (LocalAddress) super.localAddress();
    }

    @Override
    public LocalAddress remoteAddress() {
        return (LocalAddress) super.remoteAddress();
    }

    @Override
    public boolean isOpen() {
        return this.state < 2;
    }

    @Override
    public boolean isActive() {
        return this.state == 1;
    }

    @Override
    protected boolean isCompatible(EventLoop eventloop) {
        return eventloop instanceof SingleThreadEventLoop;
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.localAddress;
    }

    @Override
    protected void doRegister() throws Exception {
        ((SingleThreadEventExecutor) this.eventLoop()).addShutdownHook(this.shutdownHook);
    }

    @Override
    protected void doBind(SocketAddress socketaddress) throws Exception {
        this.localAddress = LocalChannelRegistry.register(this, this.localAddress, socketaddress);
        this.state = 1;
    }

    @Override
    protected void doClose() throws Exception {
        if (this.state <= 1) {
            LocalChannelRegistry.unregister(this.localAddress);
            this.localAddress = null;
            this.state = 2;
        }

    }

    @Override
    protected void doDeregister() throws Exception {
        ((SingleThreadEventExecutor) this.eventLoop()).removeShutdownHook(this.shutdownHook);
    }

    @Override
    protected void doBeginRead() throws Exception {
        if (!this.acceptInProgress) {
            Queue queue = this.inboundBuffer;

            if (queue.isEmpty()) {
                this.acceptInProgress = true;
            } else {
                ChannelPipeline channelpipeline = this.pipeline();

                while (true) {
                    Object object = queue.poll();

                    if (object == null) {
                        channelpipeline.fireChannelReadComplete();
                        return;
                    }

                    channelpipeline.fireChannelRead(object);
                }
            }
        }
    }

    LocalChannel serve(LocalChannel localchannel) {
        final LocalChannel localchannel1 = new LocalChannel(this, localchannel);

        if (this.eventLoop().inEventLoop()) {
            this.serve0(localchannel1);
        } else {
            this.eventLoop().execute(new Runnable() {
                public void run() {
                    LocalServerChannel.this.serve0(localchannel1);
                }
            });
        }

        return localchannel1;
    }

    private void serve0(LocalChannel localchannel) {
        this.inboundBuffer.add(localchannel);
        if (this.acceptInProgress) {
            this.acceptInProgress = false;
            ChannelPipeline channelpipeline = this.pipeline();

            while (true) {
                Object object = this.inboundBuffer.poll();

                if (object == null) {
                    channelpipeline.fireChannelReadComplete();
                    break;
                }

                channelpipeline.fireChannelRead(object);
            }
        }

    }
}
