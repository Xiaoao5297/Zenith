package io.netty.channel.udt;

import com.barchart.udt.OptionUDT;
import com.barchart.udt.SocketUDT;
import com.barchart.udt.nio.ChannelUDT;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import java.io.IOException;
import java.util.Map;

public class DefaultUdtChannelConfig extends DefaultChannelConfig implements UdtChannelConfig {

    private static final int K = 1024;
    private static final int M = 1048576;
    private volatile int protocolReceiveBuferSize = 10485760;
    private volatile int protocolSendBuferSize = 10485760;
    private volatile int systemReceiveBufferSize = 1048576;
    private volatile int systemSendBuferSize = 1048576;
    private volatile int allocatorReceiveBufferSize = 131072;
    private volatile int allocatorSendBufferSize = 131072;
    private volatile int soLinger;
    private volatile boolean reuseAddress = true;

    public DefaultUdtChannelConfig(UdtChannel udtchannel, ChannelUDT channeludt, boolean flag) throws IOException {
        super(udtchannel);
        if (flag) {
            this.apply(channeludt);
        }

    }

    protected void apply(ChannelUDT channeludt) throws IOException {
        SocketUDT socketudt = channeludt.socketUDT();

        socketudt.setReuseAddress(this.isReuseAddress());
        socketudt.setSendBufferSize(this.getSendBufferSize());
        if (this.getSoLinger() <= 0) {
            socketudt.setSoLinger(false, 0);
        } else {
            socketudt.setSoLinger(true, this.getSoLinger());
        }

        socketudt.setOption(OptionUDT.Protocol_Receive_Buffer_Size, this.getProtocolReceiveBufferSize());
        socketudt.setOption(OptionUDT.Protocol_Send_Buffer_Size, this.getProtocolSendBufferSize());
        socketudt.setOption(OptionUDT.System_Receive_Buffer_Size, this.getSystemReceiveBufferSize());
        socketudt.setOption(OptionUDT.System_Send_Buffer_Size, this.getSystemSendBufferSize());
    }

    @Override
    public int getProtocolReceiveBufferSize() {
        return this.protocolReceiveBuferSize;
    }

    @Override
    public <T> T getOption(ChannelOption<T> channeloption) {
        return channeloption == UdtChannelOption.PROTOCOL_RECEIVE_BUFFER_SIZE ? this.getProtocolReceiveBufferSize() : (channeloption == UdtChannelOption.PROTOCOL_SEND_BUFFER_SIZE ? this.getProtocolSendBufferSize() : (channeloption == UdtChannelOption.SYSTEM_RECEIVE_BUFFER_SIZE ? this.getSystemReceiveBufferSize() : (channeloption == UdtChannelOption.SYSTEM_SEND_BUFFER_SIZE ? this.getSystemSendBufferSize() : (channeloption == UdtChannelOption.SO_RCVBUF ? this.getReceiveBufferSize() : (channeloption == UdtChannelOption.SO_SNDBUF ? this.getSendBufferSize() : (channeloption == UdtChannelOption.SO_REUSEADDR ? this.isReuseAddress() : (channeloption == UdtChannelOption.SO_LINGER ? this.getSoLinger() : super.getOption(channeloption))))))));
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), new ChannelOption[]{UdtChannelOption.PROTOCOL_RECEIVE_BUFFER_SIZE, UdtChannelOption.PROTOCOL_SEND_BUFFER_SIZE, UdtChannelOption.SYSTEM_RECEIVE_BUFFER_SIZE, UdtChannelOption.SYSTEM_SEND_BUFFER_SIZE, UdtChannelOption.SO_RCVBUF, UdtChannelOption.SO_SNDBUF, UdtChannelOption.SO_REUSEADDR, UdtChannelOption.SO_LINGER});
    }

    @Override
    public int getReceiveBufferSize() {
        return this.allocatorReceiveBufferSize;
    }

    @Override
    public int getSendBufferSize() {
        return this.allocatorSendBufferSize;
    }

    @Override
    public int getSoLinger() {
        return this.soLinger;
    }

    @Override
    public boolean isReuseAddress() {
        return this.reuseAddress;
    }

    @Override
    public UdtChannelConfig setProtocolReceiveBufferSize(int i) {
        this.protocolReceiveBuferSize = i;
        return this;
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> channeloption, T t0) {
        this.validate(channeloption, t0);
        if (channeloption == UdtChannelOption.PROTOCOL_RECEIVE_BUFFER_SIZE) {
            this.setProtocolReceiveBufferSize((Integer) t0);
        } else if (channeloption == UdtChannelOption.PROTOCOL_SEND_BUFFER_SIZE) {
            this.setProtocolSendBufferSize((Integer) t0);
        } else if (channeloption == UdtChannelOption.SYSTEM_RECEIVE_BUFFER_SIZE) {
            this.setSystemReceiveBufferSize((Integer) t0);
        } else if (channeloption == UdtChannelOption.SYSTEM_SEND_BUFFER_SIZE) {
            this.setSystemSendBufferSize((Integer) t0);
        } else if (channeloption == UdtChannelOption.SO_RCVBUF) {
            this.setReceiveBufferSize((Integer) t0);
        } else if (channeloption == UdtChannelOption.SO_SNDBUF) {
            this.setSendBufferSize((Integer) t0);
        } else if (channeloption == UdtChannelOption.SO_REUSEADDR) {
            this.setReuseAddress((Boolean) t0);
        } else {
            if (channeloption != UdtChannelOption.SO_LINGER) {
                return super.setOption(channeloption, t0);
            }

            this.setSoLinger((Integer) t0);
        }

        return true;
    }

    @Override
    public UdtChannelConfig setReceiveBufferSize(int i) {
        this.allocatorReceiveBufferSize = i;
        return this;
    }

    @Override
    public UdtChannelConfig setReuseAddress(boolean flag) {
        this.reuseAddress = flag;
        return this;
    }

    @Override
    public UdtChannelConfig setSendBufferSize(int i) {
        this.allocatorSendBufferSize = i;
        return this;
    }

    @Override
    public UdtChannelConfig setSoLinger(int i) {
        this.soLinger = i;
        return this;
    }

    @Override
    public int getSystemReceiveBufferSize() {
        return this.systemReceiveBufferSize;
    }

    @Override
    public UdtChannelConfig setSystemSendBufferSize(int i) {
        this.systemReceiveBufferSize = i;
        return this;
    }

    @Override
    public int getProtocolSendBufferSize() {
        return this.protocolSendBuferSize;
    }

    @Override
    public UdtChannelConfig setProtocolSendBufferSize(int i) {
        this.protocolSendBuferSize = i;
        return this;
    }

    @Override
    public UdtChannelConfig setSystemReceiveBufferSize(int i) {
        this.systemSendBuferSize = i;
        return this;
    }

    @Override
    public int getSystemSendBufferSize() {
        return this.systemSendBuferSize;
    }

    @Override
    public UdtChannelConfig setConnectTimeoutMillis(int i) {
        super.setConnectTimeoutMillis(i);
        return this;
    }

    @Override
    public UdtChannelConfig setMaxMessagesPerRead(int i) {
        super.setMaxMessagesPerRead(i);
        return this;
    }

    @Override
    public UdtChannelConfig setWriteSpinCount(int i) {
        super.setWriteSpinCount(i);
        return this;
    }

    @Override
    public UdtChannelConfig setAllocator(ByteBufAllocator bytebufallocator) {
        super.setAllocator(bytebufallocator);
        return this;
    }

    @Override
    public UdtChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator recvbytebufallocator) {
        super.setRecvByteBufAllocator(recvbytebufallocator);
        return this;
    }

    @Override
    public UdtChannelConfig setAutoRead(boolean flag) {
        super.setAutoRead(flag);
        return this;
    }

    @Override
    public UdtChannelConfig setWriteBufferLowWaterMark(int i) {
        super.setWriteBufferLowWaterMark(i);
        return this;
    }

    @Override
    public UdtChannelConfig setWriteBufferHighWaterMark(int i) {
        super.setWriteBufferHighWaterMark(i);
        return this;
    }

    @Override
    public UdtChannelConfig setMessageSizeEstimator(MessageSizeEstimator messagesizeestimator) {
        super.setMessageSizeEstimator(messagesizeestimator);
        return this;
    }
}
