package io.netty.channel.udt;

import com.barchart.udt.nio.ChannelUDT;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import java.io.IOException;
import java.util.Map;

public class DefaultUdtServerChannelConfig extends DefaultUdtChannelConfig implements UdtServerChannelConfig {

    private volatile int backlog = 64;

    public DefaultUdtServerChannelConfig(UdtChannel udtchannel, ChannelUDT channeludt, boolean flag) throws IOException {
        super(udtchannel, channeludt, flag);
        if (flag) {
            this.apply(channeludt);
        }

    }

    @Override
    protected void apply(ChannelUDT channeludt) throws IOException {}

    @Override
    public int getBacklog() {
        return this.backlog;
    }

    @Override
    public <T> T getOption(ChannelOption<T> channeloption) {
        return channeloption == ChannelOption.SO_BACKLOG ? this.getBacklog() : super.getOption(channeloption);
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), new ChannelOption[]{ChannelOption.SO_BACKLOG});
    }

    @Override
    public UdtServerChannelConfig setBacklog(int i) {
        this.backlog = i;
        return this;
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> channeloption, T t0) {
        this.validate(channeloption, t0);
        if (channeloption == ChannelOption.SO_BACKLOG) {
            this.setBacklog((Integer) t0);
            return true;
        } else {
            return super.setOption(channeloption, t0);
        }
    }

    @Override
    public UdtServerChannelConfig setProtocolReceiveBufferSize(int i) {
        super.setProtocolReceiveBufferSize(i);
        return this;
    }

    @Override
    public UdtServerChannelConfig setProtocolSendBufferSize(int i) {
        super.setProtocolSendBufferSize(i);
        return this;
    }

    @Override
    public UdtServerChannelConfig setReceiveBufferSize(int i) {
        super.setReceiveBufferSize(i);
        return this;
    }

    @Override
    public UdtServerChannelConfig setReuseAddress(boolean flag) {
        super.setReuseAddress(flag);
        return this;
    }

    @Override
    public UdtServerChannelConfig setSendBufferSize(int i) {
        super.setSendBufferSize(i);
        return this;
    }

    @Override
    public UdtServerChannelConfig setSoLinger(int i) {
        super.setSoLinger(i);
        return this;
    }

    @Override
    public UdtServerChannelConfig setSystemReceiveBufferSize(int i) {
        super.setSystemReceiveBufferSize(i);
        return this;
    }

    @Override
    public UdtServerChannelConfig setSystemSendBufferSize(int i) {
        super.setSystemSendBufferSize(i);
        return this;
    }

    @Override
    public UdtServerChannelConfig setConnectTimeoutMillis(int i) {
        super.setConnectTimeoutMillis(i);
        return this;
    }

    @Override
    public UdtServerChannelConfig setMaxMessagesPerRead(int i) {
        super.setMaxMessagesPerRead(i);
        return this;
    }

    @Override
    public UdtServerChannelConfig setWriteSpinCount(int i) {
        super.setWriteSpinCount(i);
        return this;
    }

    @Override
    public UdtServerChannelConfig setAllocator(ByteBufAllocator bytebufallocator) {
        super.setAllocator(bytebufallocator);
        return this;
    }

    @Override
    public UdtServerChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator recvbytebufallocator) {
        super.setRecvByteBufAllocator(recvbytebufallocator);
        return this;
    }

    @Override
    public UdtServerChannelConfig setAutoRead(boolean flag) {
        super.setAutoRead(flag);
        return this;
    }

    @Override
    public UdtServerChannelConfig setWriteBufferLowWaterMark(int i) {
        super.setWriteBufferLowWaterMark(i);
        return this;
    }

    @Override
    public UdtServerChannelConfig setWriteBufferHighWaterMark(int i) {
        super.setWriteBufferHighWaterMark(i);
        return this;
    }

    @Override
    public UdtServerChannelConfig setMessageSizeEstimator(MessageSizeEstimator messagesizeestimator) {
        super.setMessageSizeEstimator(messagesizeestimator);
        return this;
    }
}
