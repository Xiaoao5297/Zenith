package io.netty.channel.socket;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Map;

public class DefaultDatagramChannelConfig extends DefaultChannelConfig implements DatagramChannelConfig {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultDatagramChannelConfig.class);
    private static final RecvByteBufAllocator DEFAULT_RCVBUF_ALLOCATOR = new FixedRecvByteBufAllocator(2048);
    private final DatagramSocket javaSocket;
    private volatile boolean activeOnOpen;

    public DefaultDatagramChannelConfig(DatagramChannel datagramchannel, DatagramSocket datagramsocket) {
        super(datagramchannel);
        if (datagramsocket == null) {
            throw new NullPointerException("javaSocket");
        } else {
            this.javaSocket = datagramsocket;
            this.setRecvByteBufAllocator(DefaultDatagramChannelConfig.DEFAULT_RCVBUF_ALLOCATOR);
        }
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), new ChannelOption[]{ChannelOption.SO_BROADCAST, ChannelOption.SO_RCVBUF, ChannelOption.SO_SNDBUF, ChannelOption.SO_REUSEADDR, ChannelOption.IP_MULTICAST_LOOP_DISABLED, ChannelOption.IP_MULTICAST_ADDR, ChannelOption.IP_MULTICAST_IF, ChannelOption.IP_MULTICAST_TTL, ChannelOption.IP_TOS, ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION});
    }

    @Override
    public <T> T getOption(ChannelOption<T> channeloption) {
        if (channeloption == ChannelOption.SO_BROADCAST) {
            return this.isBroadcast();
        } else if (channeloption == ChannelOption.SO_RCVBUF) {
            return this.getReceiveBufferSize();
        } else if (channeloption == ChannelOption.SO_SNDBUF) {
            return this.getSendBufferSize();
        } else if (channeloption == ChannelOption.SO_REUSEADDR) {
            return this.isReuseAddress();
        } else if (channeloption == ChannelOption.IP_MULTICAST_LOOP_DISABLED) {
            return this.isLoopbackModeDisabled();
        } else if (channeloption == ChannelOption.IP_MULTICAST_ADDR) {
            InetAddress inetaddress = this.getInterface();

            return inetaddress;
        } else if (channeloption == ChannelOption.IP_MULTICAST_IF) {
            NetworkInterface networkinterface = this.getNetworkInterface();

            return networkinterface;
        } else {
            return channeloption == ChannelOption.IP_MULTICAST_TTL ? this.getTimeToLive() : (channeloption == ChannelOption.IP_TOS ? this.getTrafficClass() : (channeloption == ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION ? this.activeOnOpen : super.getOption(channeloption)));
        }
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> channeloption, T t0) {
        this.validate(channeloption, t0);
        if (channeloption == ChannelOption.SO_BROADCAST) {
            this.setBroadcast((Boolean) t0);
        } else if (channeloption == ChannelOption.SO_RCVBUF) {
            this.setReceiveBufferSize((Integer) t0);
        } else if (channeloption == ChannelOption.SO_SNDBUF) {
            this.setSendBufferSize((Integer) t0);
        } else if (channeloption == ChannelOption.SO_REUSEADDR) {
            this.setReuseAddress((Boolean) t0);
        } else if (channeloption == ChannelOption.IP_MULTICAST_LOOP_DISABLED) {
            this.setLoopbackModeDisabled((Boolean) t0);
        } else if (channeloption == ChannelOption.IP_MULTICAST_ADDR) {
            this.setInterface((InetAddress) t0);
        } else if (channeloption == ChannelOption.IP_MULTICAST_IF) {
            this.setNetworkInterface((NetworkInterface) t0);
        } else if (channeloption == ChannelOption.IP_MULTICAST_TTL) {
            this.setTimeToLive((Integer) t0);
        } else if (channeloption == ChannelOption.IP_TOS) {
            this.setTrafficClass((Integer) t0);
        } else {
            if (channeloption != ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) {
                return super.setOption(channeloption, t0);
            }

            this.setActiveOnOpen((Boolean) t0);
        }

        return true;
    }

    private void setActiveOnOpen(boolean flag) {
        if (this.channel.isRegistered()) {
            throw new IllegalStateException("Can only changed before channel was registered");
        } else {
            this.activeOnOpen = flag;
        }
    }

    @Override
    public boolean isBroadcast() {
        try {
            return this.javaSocket.getBroadcast();
        } catch (SocketException socketexception) {
            throw new ChannelException(socketexception);
        }
    }

    @Override
    public DatagramChannelConfig setBroadcast(boolean flag) {
        try {
            if (flag && !PlatformDependent.isWindows() && !PlatformDependent.isRoot() && !this.javaSocket.getLocalAddress().isAnyLocalAddress()) {
                DefaultDatagramChannelConfig.logger.warn("A non-root user can't receive a broadcast packet if the socket is not bound to a wildcard address; setting the SO_BROADCAST flag anyway as requested on the socket which is bound to " + this.javaSocket.getLocalSocketAddress() + '.');
            }

            this.javaSocket.setBroadcast(flag);
            return this;
        } catch (SocketException socketexception) {
            throw new ChannelException(socketexception);
        }
    }

    @Override
    public InetAddress getInterface() {
        if (this.javaSocket instanceof MulticastSocket) {
            try {
                return ((MulticastSocket) this.javaSocket).getInterface();
            } catch (SocketException socketexception) {
                throw new ChannelException(socketexception);
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public DatagramChannelConfig setInterface(InetAddress inetaddress) {
        if (this.javaSocket instanceof MulticastSocket) {
            try {
                ((MulticastSocket) this.javaSocket).setInterface(inetaddress);
                return this;
            } catch (SocketException socketexception) {
                throw new ChannelException(socketexception);
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean isLoopbackModeDisabled() {
        if (this.javaSocket instanceof MulticastSocket) {
            try {
                return ((MulticastSocket) this.javaSocket).getLoopbackMode();
            } catch (SocketException socketexception) {
                throw new ChannelException(socketexception);
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public DatagramChannelConfig setLoopbackModeDisabled(boolean flag) {
        if (this.javaSocket instanceof MulticastSocket) {
            try {
                ((MulticastSocket) this.javaSocket).setLoopbackMode(flag);
                return this;
            } catch (SocketException socketexception) {
                throw new ChannelException(socketexception);
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public NetworkInterface getNetworkInterface() {
        if (this.javaSocket instanceof MulticastSocket) {
            try {
                return ((MulticastSocket) this.javaSocket).getNetworkInterface();
            } catch (SocketException socketexception) {
                throw new ChannelException(socketexception);
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public DatagramChannelConfig setNetworkInterface(NetworkInterface networkinterface) {
        if (this.javaSocket instanceof MulticastSocket) {
            try {
                ((MulticastSocket) this.javaSocket).setNetworkInterface(networkinterface);
                return this;
            } catch (SocketException socketexception) {
                throw new ChannelException(socketexception);
            }
        } else {
            throw new UnsupportedOperationException();
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
    public DatagramChannelConfig setReuseAddress(boolean flag) {
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
    public DatagramChannelConfig setReceiveBufferSize(int i) {
        try {
            this.javaSocket.setReceiveBufferSize(i);
            return this;
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
    public DatagramChannelConfig setSendBufferSize(int i) {
        try {
            this.javaSocket.setSendBufferSize(i);
            return this;
        } catch (SocketException socketexception) {
            throw new ChannelException(socketexception);
        }
    }

    @Override
    public int getTimeToLive() {
        if (this.javaSocket instanceof MulticastSocket) {
            try {
                return ((MulticastSocket) this.javaSocket).getTimeToLive();
            } catch (IOException ioexception) {
                throw new ChannelException(ioexception);
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public DatagramChannelConfig setTimeToLive(int i) {
        if (this.javaSocket instanceof MulticastSocket) {
            try {
                ((MulticastSocket) this.javaSocket).setTimeToLive(i);
                return this;
            } catch (IOException ioexception) {
                throw new ChannelException(ioexception);
            }
        } else {
            throw new UnsupportedOperationException();
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
    public DatagramChannelConfig setTrafficClass(int i) {
        try {
            this.javaSocket.setTrafficClass(i);
            return this;
        } catch (SocketException socketexception) {
            throw new ChannelException(socketexception);
        }
    }

    @Override
    public DatagramChannelConfig setWriteSpinCount(int i) {
        super.setWriteSpinCount(i);
        return this;
    }

    @Override
    public DatagramChannelConfig setConnectTimeoutMillis(int i) {
        super.setConnectTimeoutMillis(i);
        return this;
    }

    @Override
    public DatagramChannelConfig setMaxMessagesPerRead(int i) {
        super.setMaxMessagesPerRead(i);
        return this;
    }

    @Override
    public DatagramChannelConfig setAllocator(ByteBufAllocator bytebufallocator) {
        super.setAllocator(bytebufallocator);
        return this;
    }

    @Override
    public DatagramChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator recvbytebufallocator) {
        super.setRecvByteBufAllocator(recvbytebufallocator);
        return this;
    }

    @Override
    public DatagramChannelConfig setAutoRead(boolean flag) {
        super.setAutoRead(flag);
        return this;
    }

    @Override
    public DatagramChannelConfig setWriteBufferHighWaterMark(int i) {
        super.setWriteBufferHighWaterMark(i);
        return this;
    }

    @Override
    public DatagramChannelConfig setWriteBufferLowWaterMark(int i) {
        super.setWriteBufferLowWaterMark(i);
        return this;
    }

    @Override
    public DatagramChannelConfig setMessageSizeEstimator(MessageSizeEstimator messagesizeestimator) {
        super.setMessageSizeEstimator(messagesizeestimator);
        return this;
    }
}
