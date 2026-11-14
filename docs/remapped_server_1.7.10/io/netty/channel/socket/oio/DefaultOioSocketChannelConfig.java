package io.netty.channel.socket.oio;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.DefaultSocketChannelConfig;
import io.netty.channel.socket.SocketChannel;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class DefaultOioSocketChannelConfig extends DefaultSocketChannelConfig implements OioSocketChannelConfig {

    public DefaultOioSocketChannelConfig(SocketChannel socketchannel, Socket socket) {
        super(socketchannel, socket);
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
    public OioSocketChannelConfig setSoTimeout(int i) {
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
    public OioSocketChannelConfig setTcpNoDelay(boolean flag) {
        super.setTcpNoDelay(flag);
        return this;
    }

    @Override
    public OioSocketChannelConfig setSoLinger(int i) {
        super.setSoLinger(i);
        return this;
    }

    @Override
    public OioSocketChannelConfig setSendBufferSize(int i) {
        super.setSendBufferSize(i);
        return this;
    }

    @Override
    public OioSocketChannelConfig setReceiveBufferSize(int i) {
        super.setReceiveBufferSize(i);
        return this;
    }

    @Override
    public OioSocketChannelConfig setKeepAlive(boolean flag) {
        super.setKeepAlive(flag);
        return this;
    }

    @Override
    public OioSocketChannelConfig setTrafficClass(int i) {
        super.setTrafficClass(i);
        return this;
    }

    @Override
    public OioSocketChannelConfig setReuseAddress(boolean flag) {
        super.setReuseAddress(flag);
        return this;
    }

    @Override
    public OioSocketChannelConfig setPerformancePreferences(int i, int j, int k) {
        super.setPerformancePreferences(i, j, k);
        return this;
    }

    @Override
    public OioSocketChannelConfig setAllowHalfClosure(boolean flag) {
        super.setAllowHalfClosure(flag);
        return this;
    }

    @Override
    public OioSocketChannelConfig setConnectTimeoutMillis(int i) {
        super.setConnectTimeoutMillis(i);
        return this;
    }

    @Override
    public OioSocketChannelConfig setMaxMessagesPerRead(int i) {
        super.setMaxMessagesPerRead(i);
        return this;
    }

    @Override
    public OioSocketChannelConfig setWriteSpinCount(int i) {
        super.setWriteSpinCount(i);
        return this;
    }

    @Override
    public OioSocketChannelConfig setAllocator(ByteBufAllocator bytebufallocator) {
        super.setAllocator(bytebufallocator);
        return this;
    }

    @Override
    public OioSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator recvbytebufallocator) {
        super.setRecvByteBufAllocator(recvbytebufallocator);
        return this;
    }

    @Override
    public OioSocketChannelConfig setAutoRead(boolean flag) {
        super.setAutoRead(flag);
        return this;
    }

    @Override
    public OioSocketChannelConfig setWriteBufferHighWaterMark(int i) {
        super.setWriteBufferHighWaterMark(i);
        return this;
    }

    @Override
    public OioSocketChannelConfig setWriteBufferLowWaterMark(int i) {
        super.setWriteBufferLowWaterMark(i);
        return this;
    }

    @Override
    public OioSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator messagesizeestimator) {
        super.setMessageSizeEstimator(messagesizeestimator);
        return this;
    }
}
