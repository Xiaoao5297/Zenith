package io.netty.channel.socket.oio;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.DefaultServerSocketChannelConfig;
import io.netty.channel.socket.ServerSocketChannel;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;

public class DefaultOioServerSocketChannelConfig extends DefaultServerSocketChannelConfig implements OioServerSocketChannelConfig {

    public DefaultOioServerSocketChannelConfig(ServerSocketChannel serversocketchannel, ServerSocket serversocket) {
        super(serversocketchannel, serversocket);
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), new ChannelOption[]{ChannelOption.SO_TIMEOUT});
    }

    @Override
    public <T> T getOption(ChannelOption<T> channeloption) {
        return channeloption == ChannelOption.SO_TIMEOUT ? this.getSoTimeout() : super.getOption(channeloption);
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> channeloption, T t0) {
        this.validate(channeloption, t0);
        if (channeloption == ChannelOption.SO_TIMEOUT) {
            this.setSoTimeout((Integer) t0);
            return true;
        } else {
            return super.setOption(channeloption, t0);
        }
    }

    @Override
    public OioServerSocketChannelConfig setSoTimeout(int i) {
        try {
            this.javaSocket.setSoTimeout(i);
            return this;
        } catch (IOException ioexception) {
            throw new ChannelException(ioexception);
        }
    }

    @Override
    public int getSoTimeout() {
        try {
            return this.javaSocket.getSoTimeout();
        } catch (IOException ioexception) {
            throw new ChannelException(ioexception);
        }
    }

    @Override
    public OioServerSocketChannelConfig setBacklog(int i) {
        super.setBacklog(i);
        return this;
    }

    @Override
    public OioServerSocketChannelConfig setReuseAddress(boolean flag) {
        super.setReuseAddress(flag);
        return this;
    }

    @Override
    public OioServerSocketChannelConfig setReceiveBufferSize(int i) {
        super.setReceiveBufferSize(i);
        return this;
    }

    @Override
    public OioServerSocketChannelConfig setPerformancePreferences(int i, int j, int k) {
        super.setPerformancePreferences(i, j, k);
        return this;
    }

    @Override
    public OioServerSocketChannelConfig setConnectTimeoutMillis(int i) {
        super.setConnectTimeoutMillis(i);
        return this;
    }

    @Override
    public OioServerSocketChannelConfig setMaxMessagesPerRead(int i) {
        super.setMaxMessagesPerRead(i);
        return this;
    }

    @Override
    public OioServerSocketChannelConfig setWriteSpinCount(int i) {
        super.setWriteSpinCount(i);
        return this;
    }

    @Override
    public OioServerSocketChannelConfig setAllocator(ByteBufAllocator bytebufallocator) {
        super.setAllocator(bytebufallocator);
        return this;
    }

    @Override
    public OioServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator recvbytebufallocator) {
        super.setRecvByteBufAllocator(recvbytebufallocator);
        return this;
    }

    @Override
    public OioServerSocketChannelConfig setAutoRead(boolean flag) {
        super.setAutoRead(flag);
        return this;
    }

    @Override
    public OioServerSocketChannelConfig setWriteBufferHighWaterMark(int i) {
        super.setWriteBufferHighWaterMark(i);
        return this;
    }

    @Override
    public OioServerSocketChannelConfig setWriteBufferLowWaterMark(int i) {
        super.setWriteBufferLowWaterMark(i);
        return this;
    }

    @Override
    public OioServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator messagesizeestimator) {
        super.setMessageSizeEstimator(messagesizeestimator);
        return this;
    }
}
