package io.netty.channel.sctp;

import com.sun.nio.sctp.SctpStandardSocketOptions;
import com.sun.nio.sctp.SctpStandardSocketOptions.InitMaxStreams;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.util.Map;

public class DefaultSctpChannelConfig extends DefaultChannelConfig implements SctpChannelConfig {

    private final com.sun.nio.sctp.SctpChannel javaChannel;

    public DefaultSctpChannelConfig(SctpChannel sctpchannel, com.sun.nio.sctp.SctpChannel com_sun_nio_sctp_sctpchannel) {
        super(sctpchannel);
        if (com_sun_nio_sctp_sctpchannel == null) {
            throw new NullPointerException("javaChannel");
        } else {
            this.javaChannel = com_sun_nio_sctp_sctpchannel;
            if (PlatformDependent.canEnableTcpNoDelayByDefault()) {
                try {
                    this.setSctpNoDelay(true);
                } catch (Exception exception) {
                    ;
                }
            }

        }
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), new ChannelOption[]{SctpChannelOption.SO_RCVBUF, SctpChannelOption.SO_SNDBUF, SctpChannelOption.SCTP_NODELAY, SctpChannelOption.SCTP_INIT_MAXSTREAMS});
    }

    @Override
    public <T> T getOption(ChannelOption<T> channeloption) {
        return channeloption == SctpChannelOption.SO_RCVBUF ? this.getReceiveBufferSize() : (channeloption == SctpChannelOption.SO_SNDBUF ? this.getSendBufferSize() : (channeloption == SctpChannelOption.SCTP_NODELAY ? this.isSctpNoDelay() : super.getOption(channeloption)));
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> channeloption, T t0) {
        this.validate(channeloption, t0);
        if (channeloption == SctpChannelOption.SO_RCVBUF) {
            this.setReceiveBufferSize((Integer) t0);
        } else if (channeloption == SctpChannelOption.SO_SNDBUF) {
            this.setSendBufferSize((Integer) t0);
        } else if (channeloption == SctpChannelOption.SCTP_NODELAY) {
            this.setSctpNoDelay((Boolean) t0);
        } else {
            if (channeloption != SctpChannelOption.SCTP_INIT_MAXSTREAMS) {
                return super.setOption(channeloption, t0);
            }

            this.setInitMaxStreams((InitMaxStreams) t0);
        }

        return true;
    }

    @Override
    public boolean isSctpNoDelay() {
        try {
            return (Boolean) this.javaChannel.getOption(SctpStandardSocketOptions.SCTP_NODELAY);
        } catch (IOException ioexception) {
            throw new ChannelException(ioexception);
        }
    }

    @Override
    public SctpChannelConfig setSctpNoDelay(boolean flag) {
        try {
            this.javaChannel.setOption(SctpStandardSocketOptions.SCTP_NODELAY, flag);
            return this;
        } catch (IOException ioexception) {
            throw new ChannelException(ioexception);
        }
    }

    @Override
    public int getSendBufferSize() {
        try {
            return (Integer) this.javaChannel.getOption(SctpStandardSocketOptions.SO_SNDBUF);
        } catch (IOException ioexception) {
            throw new ChannelException(ioexception);
        }
    }

    @Override
    public SctpChannelConfig setSendBufferSize(int i) {
        try {
            this.javaChannel.setOption(SctpStandardSocketOptions.SO_SNDBUF, i);
            return this;
        } catch (IOException ioexception) {
            throw new ChannelException(ioexception);
        }
    }

    @Override
    public int getReceiveBufferSize() {
        try {
            return (Integer) this.javaChannel.getOption(SctpStandardSocketOptions.SO_RCVBUF);
        } catch (IOException ioexception) {
            throw new ChannelException(ioexception);
        }
    }

    @Override
    public SctpChannelConfig setReceiveBufferSize(int i) {
        try {
            this.javaChannel.setOption(SctpStandardSocketOptions.SO_RCVBUF, i);
            return this;
        } catch (IOException ioexception) {
            throw new ChannelException(ioexception);
        }
    }

    @Override
    public InitMaxStreams getInitMaxStreams() {
        try {
            return (InitMaxStreams) this.javaChannel.getOption(SctpStandardSocketOptions.SCTP_INIT_MAXSTREAMS);
        } catch (IOException ioexception) {
            throw new ChannelException(ioexception);
        }
    }

    @Override
    public SctpChannelConfig setInitMaxStreams(InitMaxStreams initmaxstreams) {
        try {
            this.javaChannel.setOption(SctpStandardSocketOptions.SCTP_INIT_MAXSTREAMS, initmaxstreams);
            return this;
        } catch (IOException ioexception) {
            throw new ChannelException(ioexception);
        }
    }

    @Override
    public SctpChannelConfig setConnectTimeoutMillis(int i) {
        super.setConnectTimeoutMillis(i);
        return this;
    }

    @Override
    public SctpChannelConfig setMaxMessagesPerRead(int i) {
        super.setMaxMessagesPerRead(i);
        return this;
    }

    @Override
    public SctpChannelConfig setWriteSpinCount(int i) {
        super.setWriteSpinCount(i);
        return this;
    }

    @Override
    public SctpChannelConfig setAllocator(ByteBufAllocator bytebufallocator) {
        super.setAllocator(bytebufallocator);
        return this;
    }

    @Override
    public SctpChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator recvbytebufallocator) {
        super.setRecvByteBufAllocator(recvbytebufallocator);
        return this;
    }

    @Override
    public SctpChannelConfig setAutoRead(boolean flag) {
        super.setAutoRead(flag);
        return this;
    }

    @Override
    public SctpChannelConfig setWriteBufferHighWaterMark(int i) {
        super.setWriteBufferHighWaterMark(i);
        return this;
    }

    @Override
    public SctpChannelConfig setWriteBufferLowWaterMark(int i) {
        super.setWriteBufferLowWaterMark(i);
        return this;
    }

    @Override
    public SctpChannelConfig setMessageSizeEstimator(MessageSizeEstimator messagesizeestimator) {
        super.setMessageSizeEstimator(messagesizeestimator);
        return this;
    }
}
