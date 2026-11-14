package io.netty.channel.socket;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.util.NetUtil;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Map;

public class DefaultServerSocketChannelConfig extends DefaultChannelConfig implements ServerSocketChannelConfig {

    protected final ServerSocket javaSocket;
    private volatile int backlog;

    public DefaultServerSocketChannelConfig(ServerSocketChannel serversocketchannel, ServerSocket serversocket) {
        super(serversocketchannel);
        this.backlog = NetUtil.SOMAXCONN;
        if (serversocket == null) {
            throw new NullPointerException("javaSocket");
        } else {
            this.javaSocket = serversocket;
        }
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), new ChannelOption[]{ChannelOption.SO_RCVBUF, ChannelOption.SO_REUSEADDR, ChannelOption.SO_BACKLOG});
    }

    @Override
    public <T> T getOption(ChannelOption<T> channeloption) {
        return channeloption == ChannelOption.SO_RCVBUF ? this.getReceiveBufferSize() : (channeloption == ChannelOption.SO_REUSEADDR ? this.isReuseAddress() : (channeloption == ChannelOption.SO_BACKLOG ? this.getBacklog() : super.getOption(channeloption)));
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> channeloption, T t0) {
        this.validate(channeloption, t0);
        if (channeloption == ChannelOption.SO_RCVBUF) {
            this.setReceiveBufferSize((Integer) t0);
        } else if (channeloption == ChannelOption.SO_REUSEADDR) {
            this.setReuseAddress((Boolean) t0);
        } else {
            if (channeloption != ChannelOption.SO_BACKLOG) {
                return super.setOption(channeloption, t0);
            }

            this.setBacklog((Integer) t0);
        }

        return true;
    }

    @Override
    public boolean isReuseAddress() {
        try {
            return this.javaSocket.getReuseAddress();
        } catch (SocketException socketexception) {
            throw new ChannelException(socketexception);
        }
    }

    @Override
    public ServerSocketChannelConfig setReuseAddress(boolean flag) {
        try {
            this.javaSocket.setReuseAddress(flag);
            return this;
        } catch (SocketException socketexception) {
            throw new ChannelException(socketexception);
        }
    }

    @Override
    public int getReceiveBufferSize() {
        try {
            return this.javaSocket.getReceiveBufferSize();
        } catch (SocketException socketexception) {
            throw new ChannelException(socketexception);
        }
    }

    @Override
    public ServerSocketChannelConfig setReceiveBufferSize(int i) {
        try {
            this.javaSocket.setReceiveBufferSize(i);
            return this;
        } catch (SocketException socketexception) {
            throw new ChannelException(socketexception);
        }
    }

    @Override
    public ServerSocketChannelConfig setPerformancePreferences(int i, int j, int k) {
        this.javaSocket.setPerformancePreferences(i, j, k);
        return this;
    }

    @Override
    public int getBacklog() {
        return this.backlog;
    }

    @Override
    public ServerSocketChannelConfig setBacklog(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("backlog: " + i);
        } else {
            this.backlog = i;
            return this;
        }
    }

    @Override
    public ServerSocketChannelConfig setConnectTimeoutMillis(int i) {
        super.setConnectTimeoutMillis(i);
        return this;
    }

    @Override
    public ServerSocketChannelConfig setMaxMessagesPerRead(int i) {
        super.setMaxMessagesPerRead(i);
        return this;
    }

    @Override
    public ServerSocketChannelConfig setWriteSpinCount(int i) {
        super.setWriteSpinCount(i);
        return this;
    }

    @Override
    public ServerSocketChannelConfig setAllocator(ByteBufAllocator bytebufallocator) {
        super.setAllocator(bytebufallocator);
        return this;
    }

    @Override
    public ServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator recvbytebufallocator) {
        super.setRecvByteBufAllocator(recvbytebufallocator);
        return this;
    }

    @Override
    public ServerSocketChannelConfig setAutoRead(boolean flag) {
        super.setAutoRead(flag);
        return this;
    }

    @Override
    public ServerSocketChannelConfig setWriteBufferHighWaterMark(int i) {
        super.setWriteBufferHighWaterMark(i);
        return this;
    }

    @Override
    public ServerSocketChannelConfig setWriteBufferLowWaterMark(int i) {
        super.setWriteBufferLowWaterMark(i);
        return this;
    }

    @Override
    public ServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator messagesizeestimator) {
        super.setMessageSizeEstimator(messagesizeestimator);
        return this;
    }
}
