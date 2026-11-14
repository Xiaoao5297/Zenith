package io.netty.channel.socket.nio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.FileRegion;
import io.netty.channel.nio.AbstractNioByteChannel;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.socket.DefaultSocketChannelConfig;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

public class NioSocketChannel extends AbstractNioByteChannel implements SocketChannel {

    private static final ChannelMetadata METADATA = new ChannelMetadata(false);
    private final SocketChannelConfig config;

    private static java.nio.channels.SocketChannel newSocket() {
        try {
            return java.nio.channels.SocketChannel.open();
        } catch (IOException ioexception) {
            throw new ChannelException("Failed to open a socket.", ioexception);
        }
    }

    public NioSocketChannel() {
        this(newSocket());
    }

    public NioSocketChannel(java.nio.channels.SocketChannel java_nio_channels_socketchannel) {
        this((Channel) null, java_nio_channels_socketchannel);
    }

    public NioSocketChannel(Channel channel, java.nio.channels.SocketChannel java_nio_channels_socketchannel) {
        super(channel, java_nio_channels_socketchannel);
        this.config = new DefaultSocketChannelConfig(this, java_nio_channels_socketchannel.socket());
    }

    @Override
    public ServerSocketChannel parent() {
        return (ServerSocketChannel) super.parent();
    }

    @Override
    public ChannelMetadata metadata() {
        return NioSocketChannel.METADATA;
    }

    @Override
    public SocketChannelConfig config() {
        return this.config;
    }

    @Override
    protected java.nio.channels.SocketChannel javaChannel() {
        return (java.nio.channels.SocketChannel) super.javaChannel();
    }

    @Override
    public boolean isActive() {
        java.nio.channels.SocketChannel java_nio_channels_socketchannel = this.javaChannel();

        return java_nio_channels_socketchannel.isOpen() && java_nio_channels_socketchannel.isConnected();
    }

    @Override
    public boolean isInputShutdown() {
        return super.isInputShutdown();
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress) super.localAddress();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress) super.remoteAddress();
    }

    @Override
    public boolean isOutputShutdown() {
        return this.javaChannel().socket().isOutputShutdown() || !this.isActive();
    }

    @Override
    public ChannelFuture shutdownOutput() {
        return this.shutdownOutput(this.newPromise());
    }

    @Override
    public ChannelFuture shutdownOutput(final ChannelPromise channelpromise) {
        NioEventLoop nioeventloop = this.eventLoop();

        if (nioeventloop.inEventLoop()) {
            try {
                this.javaChannel().socket().shutdownOutput();
                channelpromise.setSuccess();
            } catch (Throwable throwable) {
                channelpromise.setFailure(throwable);
            }
        } else {
            nioeventloop.execute(new Runnable() {
                public void run() {
                    NioSocketChannel.this.shutdownOutput(channelpromise);
                }
            });
        }

        return channelpromise;
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.javaChannel().socket().getLocalSocketAddress();
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return this.javaChannel().socket().getRemoteSocketAddress();
    }

    @Override
    protected void doBind(SocketAddress socketaddress) throws Exception {
        this.javaChannel().socket().bind(socketaddress);
    }

    @Override
    protected boolean doConnect(SocketAddress socketaddress, SocketAddress socketaddress1) throws Exception {
        if (socketaddress1 != null) {
            this.javaChannel().socket().bind(socketaddress1);
        }

        boolean flag = false;

        boolean flag1;

        try {
            boolean flag2 = this.javaChannel().connect(socketaddress);

            if (!flag2) {
                this.selectionKey().interestOps(8);
            }

            flag = true;
            flag1 = flag2;
        } finally {
            if (!flag) {
                this.doClose();
            }

        }

        return flag1;
    }

    @Override
    protected void doFinishConnect() throws Exception {
        if (!this.javaChannel().finishConnect()) {
            throw new Error();
        }
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }

    @Override
    protected void doClose() throws Exception {
        this.javaChannel().close();
    }

    @Override
    protected int doReadBytes(ByteBuf bytebuf) throws Exception {
        return bytebuf.writeBytes((ScatteringByteChannel) this.javaChannel(), bytebuf.writableBytes());
    }

    @Override
    protected int doWriteBytes(ByteBuf bytebuf) throws Exception {
        int i = bytebuf.readableBytes();
        int j = bytebuf.readBytes((GatheringByteChannel) this.javaChannel(), i);

        return j;
    }

    @Override
    protected long doWriteFileRegion(FileRegion fileregion) throws Exception {
        long i = fileregion.transfered();
        long j = fileregion.transferTo(this.javaChannel(), i);

        return j;
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer channeloutboundbuffer) throws Exception {
        while (true) {
            int i = channeloutboundbuffer.size();

            if (i <= 1) {
                super.doWrite(channeloutboundbuffer);
                return;
            }

            ByteBuffer[] abytebuffer = channeloutboundbuffer.nioBuffers();

            if (abytebuffer == null) {
                super.doWrite(channeloutboundbuffer);
                return;
            }

            int j = channeloutboundbuffer.nioBufferCount();
            long k = channeloutboundbuffer.nioBufferSize();
            java.nio.channels.SocketChannel java_nio_channels_socketchannel = this.javaChannel();
            long l = 0L;
            boolean flag = false;

            int i1;

            for (i1 = this.config().getWriteSpinCount() - 1; i1 >= 0; --i1) {
                long j1 = java_nio_channels_socketchannel.write(abytebuffer, 0, j);

                if (j1 == 0L) {
                    break;
                }

                k -= j1;
                l += j1;
                if (k == 0L) {
                    flag = true;
                    break;
                }
            }

            if (flag) {
                for (i1 = i; i1 > 0; --i1) {
                    channeloutboundbuffer.remove();
                }

                if (!channeloutboundbuffer.isEmpty()) {
                    continue;
                }

                this.clearOpWrite();
            } else {
                for (i1 = i; i1 > 0; --i1) {
                    ByteBuf bytebuf = (ByteBuf) channeloutboundbuffer.current();
                    int k1 = bytebuf.readerIndex();
                    int l1 = bytebuf.writerIndex() - k1;

                    if ((long) l1 >= l) {
                        if ((long) l1 > l) {
                            bytebuf.readerIndex(k1 + (int) l);
                            channeloutboundbuffer.progress(l);
                        } else {
                            channeloutboundbuffer.progress((long) l1);
                            channeloutboundbuffer.remove();
                        }
                        break;
                    }

                    channeloutboundbuffer.progress((long) l1);
                    channeloutboundbuffer.remove();
                    l -= (long) l1;
                }

                this.setOpWrite();
            }

            return;
        }
    }
}
