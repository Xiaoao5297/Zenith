package io.netty.channel.rxtx;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import java.util.Map;

final class DefaultRxtxChannelConfig extends DefaultChannelConfig implements RxtxChannelConfig {

    private volatile int baudrate = 115200;
    private volatile boolean dtr;
    private volatile boolean rts;
    private volatile RxtxChannelConfig.Stopbits stopbits;
    private volatile RxtxChannelConfig.Databits databits;
    private volatile RxtxChannelConfig.Paritybit paritybit;
    private volatile int waitTime;
    private volatile int readTimeout;

    public DefaultRxtxChannelConfig(RxtxChannel rxtxchannel) {
        super(rxtxchannel);
        this.stopbits = RxtxChannelConfig.Stopbits.STOPBITS_1;
        this.databits = RxtxChannelConfig.Databits.DATABITS_8;
        this.paritybit = RxtxChannelConfig.Paritybit.NONE;
        this.readTimeout = 1000;
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), new ChannelOption[]{RxtxChannelOption.BAUD_RATE, RxtxChannelOption.DTR, RxtxChannelOption.RTS, RxtxChannelOption.STOP_BITS, RxtxChannelOption.DATA_BITS, RxtxChannelOption.PARITY_BIT, RxtxChannelOption.WAIT_TIME});
    }

    @Override
    public <T> T getOption(ChannelOption<T> channeloption) {
        return channeloption == RxtxChannelOption.BAUD_RATE ? this.getBaudrate() : (channeloption == RxtxChannelOption.DTR ? this.isDtr() : (channeloption == RxtxChannelOption.RTS ? this.isRts() : (channeloption == RxtxChannelOption.STOP_BITS ? this.getStopbits() : (channeloption == RxtxChannelOption.DATA_BITS ? this.getDatabits() : (channeloption == RxtxChannelOption.PARITY_BIT ? this.getParitybit() : (channeloption == RxtxChannelOption.WAIT_TIME ? this.getWaitTimeMillis() : (channeloption == RxtxChannelOption.READ_TIMEOUT ? this.getReadTimeout() : super.getOption(channeloption))))))));
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> channeloption, T t0) {
        this.validate(channeloption, t0);
        if (channeloption == RxtxChannelOption.BAUD_RATE) {
            this.setBaudrate((Integer) t0);
        } else if (channeloption == RxtxChannelOption.DTR) {
            this.setDtr((Boolean) t0);
        } else if (channeloption == RxtxChannelOption.RTS) {
            this.setRts((Boolean) t0);
        } else if (channeloption == RxtxChannelOption.STOP_BITS) {
            this.setStopbits((RxtxChannelConfig.Stopbits) t0);
        } else if (channeloption == RxtxChannelOption.DATA_BITS) {
            this.setDatabits((RxtxChannelConfig.Databits) t0);
        } else if (channeloption == RxtxChannelOption.PARITY_BIT) {
            this.setParitybit((RxtxChannelConfig.Paritybit) t0);
        } else if (channeloption == RxtxChannelOption.WAIT_TIME) {
            this.setWaitTimeMillis((Integer) t0);
        } else {
            if (channeloption != RxtxChannelOption.READ_TIMEOUT) {
                return super.setOption(channeloption, t0);
            }

            this.setReadTimeout((Integer) t0);
        }

        return true;
    }

    @Override
    public RxtxChannelConfig setBaudrate(int i) {
        this.baudrate = i;
        return this;
    }

    @Override
    public RxtxChannelConfig setStopbits(RxtxChannelConfig.Stopbits rxtxchannelconfig_stopbits) {
        this.stopbits = rxtxchannelconfig_stopbits;
        return this;
    }

    @Override
    public RxtxChannelConfig setDatabits(RxtxChannelConfig.Databits rxtxchannelconfig_databits) {
        this.databits = rxtxchannelconfig_databits;
        return this;
    }

    @Override
    public RxtxChannelConfig setParitybit(RxtxChannelConfig.Paritybit rxtxchannelconfig_paritybit) {
        this.paritybit = rxtxchannelconfig_paritybit;
        return this;
    }

    @Override
    public int getBaudrate() {
        return this.baudrate;
    }

    @Override
    public RxtxChannelConfig.Stopbits getStopbits() {
        return this.stopbits;
    }

    @Override
    public RxtxChannelConfig.Databits getDatabits() {
        return this.databits;
    }

    @Override
    public RxtxChannelConfig.Paritybit getParitybit() {
        return this.paritybit;
    }

    @Override
    public boolean isDtr() {
        return this.dtr;
    }

    @Override
    public RxtxChannelConfig setDtr(boolean flag) {
        this.dtr = flag;
        return this;
    }

    @Override
    public boolean isRts() {
        return this.rts;
    }

    @Override
    public RxtxChannelConfig setRts(boolean flag) {
        this.rts = flag;
        return this;
    }

    @Override
    public int getWaitTimeMillis() {
        return this.waitTime;
    }

    @Override
    public RxtxChannelConfig setWaitTimeMillis(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Wait time must be >= 0");
        } else {
            this.waitTime = i;
            return this;
        }
    }

    @Override
    public RxtxChannelConfig setReadTimeout(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("readTime must be >= 0");
        } else {
            this.readTimeout = i;
            return this;
        }
    }

    @Override
    public int getReadTimeout() {
        return this.readTimeout;
    }

    @Override
    public RxtxChannelConfig setConnectTimeoutMillis(int i) {
        super.setConnectTimeoutMillis(i);
        return this;
    }

    @Override
    public RxtxChannelConfig setMaxMessagesPerRead(int i) {
        super.setMaxMessagesPerRead(i);
        return this;
    }

    @Override
    public RxtxChannelConfig setWriteSpinCount(int i) {
        super.setWriteSpinCount(i);
        return this;
    }

    @Override
    public RxtxChannelConfig setAllocator(ByteBufAllocator bytebufallocator) {
        super.setAllocator(bytebufallocator);
        return this;
    }

    @Override
    public RxtxChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator recvbytebufallocator) {
        super.setRecvByteBufAllocator(recvbytebufallocator);
        return this;
    }

    @Override
    public RxtxChannelConfig setAutoRead(boolean flag) {
        super.setAutoRead(flag);
        return this;
    }

    @Override
    public RxtxChannelConfig setWriteBufferHighWaterMark(int i) {
        super.setWriteBufferHighWaterMark(i);
        return this;
    }

    @Override
    public RxtxChannelConfig setWriteBufferLowWaterMark(int i) {
        super.setWriteBufferLowWaterMark(i);
        return this;
    }

    @Override
    public RxtxChannelConfig setMessageSizeEstimator(MessageSizeEstimator messagesizeestimator) {
        super.setMessageSizeEstimator(messagesizeestimator);
        return this;
    }
}
