package io.netty.channel.sctp;

import com.sun.nio.sctp.SctpStandardSocketOptions;
import com.sun.nio.sctp.SctpStandardSocketOptions.InitMaxStreams;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.util.NetUtil;
import java.io.IOException;
import java.util.Map;

public class DefaultSctpServerChannelConfig extends DefaultChannelConfig implements SctpServerChannelConfig {

    private final com.sun.nio.sctp.SctpServerChannel javaChannel;
    private volatile int backlog;

    public DefaultSctpServerChannelConfig(SctpServerChannel sctpserverchannel, com.sun.nio.sctp.SctpServerChannel com_sun_nio_sctp_sctpserverchannel) {
        super(sctpserverchannel);
        this.backlog = NetUtil.SOMAXCONN;
        if (com_sun_nio_sctp_sctpserverchannel == null) {
            throw new NullPointerException("javaChannel");
        } else {
            this.javaChannel = com_sun_nio_sctp_sctpserverchannel;
        }
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), new ChannelOption[]{ChannelOption.SO_RCVBUF, ChannelOption.SO_SNDBUF, SctpChannelOption.SCTP_INIT_MAXSTREAMS});
    }

    @Override
    public <T> T getOption(ChannelOption<T> channeloption) {
        return channeloption == ChannelOption.SO_RCVBUF ? this.getReceiveBufferSize() : (channeloption == ChannelOption.SO_SNDBUF ? this.getSendBufferSize() : super.getOption(channeloption));
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> channeloption, T t0) {
        this.validate(channeloption, t0);
        if (channeloption == ChannelOption.SO_RCVBUF) {
            this.setReceiveBufferSize((Integer) t0);
        } else if (channeloption == ChannelOption.SO_SNDBUF) {
            this.setSendBufferSize((Integer) t0);
        } else {
            if (channeloption != SctpStandardSocketOptions.SCTP_INIT_MAXSTREAMS) {
                return super.setOption(channeloption, t0);
            }

            this.setInitMaxStreams((InitMaxStreams) t0);
        }

        return true;
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
    public SctpServerChannelConfig setSendBufferSize(int i) {
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
    public SctpServerChannelConfig setReceiveBufferSize(int i) {
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
    public SctpServerChannelConfig setInitMaxStreams(InitMaxStreams initmaxstreams) {
        try {
            this.javaChannel.setOption(SctpStandardSocketOptions.SCTP_INIT_MAXSTREAMS, initmaxstreams);
            return this;
        } catch (IOException ioexception) {
            throw new ChannelException(ioexception);
        }
    }

    @Override
    public int getBacklog() {
        return this.backlog;
    }

    @Override
    public SctpServerChannelConfig setBacklog(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("backlog: " + i);
        } else {
            this.backlog = i;
            return this;
        }
    }

    @Override
    public SctpServerChannelConfig setMaxMessagesPerRead(int i) {
        super.setMaxMessagesPerRead(i);
        return this;
    }

    @Override
    public SctpServerChannelConfig setWriteSpinCount(int i) {
        super.setWriteSpinCount(i);
        return this;
    }

    @Override
    public SctpServerChannelConfig setConnectTimeoutMillis(int i) {
        super.setConnectTimeoutMillis(i);
        return this;
    }

    @Override
    public SctpServerChannelConfig setAllocator(ByteBufAllocator bytebufallocator) {
        super.setAllocator(bytebufallocator);
        return this;
    }

    @Override
    public SctpServerChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator recvbytebufallocator) {
        super.setRecvByteBufAllocator(recvbytebufallocator);
        return this;
    }

    @Override
    public SctpServerChannelConfig setAutoRead(boolean flag) {
        super.setAutoRead(flag);
        return this;
    }

    @Override
    public SctpServerChannelConfig setWriteBufferLowWaterMark(int i) {
        super.setWriteBufferLowWaterMark(i);
        return this;
    }

    @Override
    public SctpServerChannelConfig setWriteBufferHighWaterMark(int i) {
        super.setWriteBufferHighWaterMark(i);
        return this;
    }

    @Override
    public SctpServerChannelConfig setMessageSizeEstimator(MessageSizeEstimator messagesizeestimator) {
        super.setMessageSizeEstimator(messagesizeestimator);
        return this;
    }
}
