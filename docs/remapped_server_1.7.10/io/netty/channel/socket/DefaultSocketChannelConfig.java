package io.netty.channel.socket;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.util.internal.PlatformDependent;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

public class DefaultSocketChannelConfig extends DefaultChannelConfig implements SocketChannelConfig {

    protected final Socket javaSocket;
    private volatile boolean allowHalfClosure;

    public DefaultSocketChannelConfig(SocketChannel socketchannel, Socket socket) {
        super(socketchannel);
        if (socket == null) {
            throw new NullPointerException("javaSocket");
        } else {
            this.javaSocket = socket;
            if (PlatformDependent.canEnableTcpNoDelayByDefault()) {
                try {
                    this.setTcpNoDelay(true);
                } catch (Exception exception) {
                    ;
                }
            }

        }
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), new ChannelOption[]{ChannelOption.SO_RCVBUF, ChannelOption.SO_SNDBUF, ChannelOption.TCP_NODELAY, ChannelOption.SO_KEEPALIVE, ChannelOption.SO_REUSEADDR, ChannelOption.SO_LINGER, ChannelOption.IP_TOS, ChannelOption.ALLOW_HALF_CLOSURE});
    }

    @Override
    public <T> T getOption(ChannelOption<T> channeloption) {
        return channeloption == ChannelOption.SO_RCVBUF ? this.getReceiveBufferSize() : (channeloption == ChannelOption.SO_SNDBUF ? this.getSendBufferSize() : (channeloption == ChannelOption.TCP_NODELAY ? this.isTcpNoDelay() : (channeloption == ChannelOption.SO_KEEPALIVE ? this.isKeepAlive() : (channeloption == ChannelOption.SO_REUSEADDR ? this.isReuseAddress() : (channeloption == ChannelOption.SO_LINGER ? this.getSoLinger() : (channeloption == ChannelOption.IP_TOS ? this.getTrafficClass() : (channeloption == ChannelOption.ALLOW_HALF_CLOSURE ? this.isAllowHalfClosure() : super.getOption(channeloption))))))));
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> channeloption, T t0) {
        this.validate(channeloption, t0);
        if (channeloption == ChannelOption.SO_RCVBUF) {
            this.setReceiveBufferSize((Integer) t0);
        } else if (channeloption == ChannelOption.SO_SNDBUF) {
            this.setSendBufferSize((Integer) t0);
        } else if (channeloption == ChannelOption.TCP_NODELAY) {
            this.setTcpNoDelay((Boolean) t0);
        } else if (channeloption == ChannelOption.SO_KEEPALIVE) {
            this.setKeepAlive((Boolean) t0);
        } else if (channeloption == ChannelOption.SO_REUSEADDR) {
            this.setReuseAddress((Boolean) t0);
        } else if (channeloption == ChannelOption.SO_LINGER) {
            this.setSoLinger((Integer) t0);
        } else if (channeloption == ChannelOption.IP_TOS) {
            this.setTrafficClass((Integer) t0);
        } else {
            if (channeloption != ChannelOption.ALLOW_HALF_CLOSURE) {
                return super.setOption(channeloption, t0);
            }

            this.setAllowHalfClosure((Boolean) t0);
        }

        return true;
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
    public int getSendBufferSize() {
        try {
            return this.javaSocket.getSendBufferSize();
        } catch (SocketException socketexception) {
            throw new ChannelException(socketexception);
        }
    }

    @Override
    public int getSoLinger() {
        try {
            return this.javaSocket.getSoLinger();
        } catch (SocketException socketexception) {
            throw new ChannelException(socketexception);
        }
    }

    @Override
    public int getTrafficClass() {
        try {
            return this.javaSocket.getTrafficClass();
        } catch (SocketException socketexception) {
            throw new ChannelException(socketexception);
        }
    }

    @Override
    public boolean isKeepAlive() {
        try {
            return this.javaSocket.getKeepAlive();
        } catch (SocketException socketexception) {
            throw new ChannelException(socketexception);
        }
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
    public boolean isTcpNoDelay() {
        try {
            return this.javaSocket.getTcpNoDelay();
        } catch (SocketException socketexception) {
            throw new ChannelException(socketexception);
        }
    }

    @Override
    public SocketChannelConfig setKeepAlive(boolean flag) {
        try {
            this.javaSocket.setKeepAlive(flag);
            return this;
        } catch (SocketException socketexception) {
            throw new ChannelException(socketexception);
        }
    }

    @Override
    public SocketChannelConfig setPerformancePreferences(int i, int j, int k) {
        this.javaSocket.setPerformancePreferences(i, j, k);
        return this;
    }

    @Override
    public SocketChannelConfig setReceiveBufferSize(int i) {
        try {
            this.javaSocket.setReceiveBufferSize(i);
            return this;
        } catch (SocketException socketexception) {
            throw new ChannelException(socketexception);
        }
    }

    @Override
    public SocketChannelConfig setReuseAddress(boolean flag) {
        try {
            this.javaSocket.setReuseAddress(flag);
            return this;
        } catch (SocketException socketexception) {
            throw new ChannelException(socketexception);
        }
    }

    @Override
    public SocketChannelConfig setSendBufferSize(int i) {
        try {
            this.javaSocket.setSendBufferSize(i);
            return this;
        } catch (SocketException socketexception) {
            throw new ChannelException(socketexception);
        }
    }

    @Override
    public SocketChannelConfig setSoLinger(int i) {
        try {
            if (i < 0) {
                this.javaSocket.setSoLinger(false, 0);
            } else {
                this.javaSocket.setSoLinger(true, i);
            }

            return this;
        } catch (SocketException socketexception) {
            throw new ChannelException(socketexception);
        }
    }

    @Override
    public SocketChannelConfig setTcpNoDelay(boolean flag) {
        try {
            this.javaSocket.setTcpNoDelay(flag);
            return this;
        } catch (SocketException socketexception) {
            throw new ChannelException(socketexception);
        }
    }

    @Override
    public SocketChannelConfig setTrafficClass(int i) {
        try {
            this.javaSocket.setTrafficClass(i);
            return this;
        } catch (SocketException socketexception) {
            throw new ChannelException(socketexception);
        }
    }

    @Override
    public boolean isAllowHalfClosure() {
        return this.allowHalfClosure;
    }

    @Override
    public SocketChannelConfig setAllowHalfClosure(boolean flag) {
        this.allowHalfClosure = flag;
        return this;
    }

    @Override
    public SocketChannelConfig setConnectTimeoutMillis(int i) {
        super.setConnectTimeoutMillis(i);
        return this;
    }

    @Override
    public SocketChannelConfig setMaxMessagesPerRead(int i) {
        super.setMaxMessagesPerRead(i);
        return this;
    }

    @Override
    public SocketChannelConfig setWriteSpinCount(int i) {
        super.setWriteSpinCount(i);
        return this;
    }

    @Override
    public SocketChannelConfig setAllocator(ByteBufAllocator bytebufallocator) {
        super.setAllocator(bytebufallocator);
        return this;
    }

    @Override
    public SocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator recvbytebufallocator) {
        super.setRecvByteBufAllocator(recvbytebufallocator);
        return this;
    }

    @Override
    public SocketChannelConfig setAutoRead(boolean flag) {
        super.setAutoRead(flag);
        return this;
    }

    @Override
    public SocketChannelConfig setWriteBufferHighWaterMark(int i) {
        super.setWriteBufferHighWaterMark(i);
        return this;
    }

    @Override
    public SocketChannelConfig setWriteBufferLowWaterMark(int i) {
        super.setWriteBufferLowWaterMark(i);
        return this;
    }

    @Override
    public SocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator messagesizeestimator) {
        super.setMessageSizeEstimator(messagesizeestimator);
        return this;
    }
}
