package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class DefaultChannelConfig implements ChannelConfig {

    private static final ByteBufAllocator DEFAULT_ALLOCATOR = UnpooledByteBufAllocator.DEFAULT;
    private static final RecvByteBufAllocator DEFAULT_RCVBUF_ALLOCATOR = AdaptiveRecvByteBufAllocator.DEFAULT;
    private static final MessageSizeEstimator DEFAULT_MSG_SIZE_ESTIMATOR = DefaultMessageSizeEstimator.DEFAULT;
    private static final int DEFAULT_CONNECT_TIMEOUT = 30000;
    protected final Channel channel;
    private volatile ByteBufAllocator allocator;
    private volatile RecvByteBufAllocator rcvBufAllocator;
    private volatile MessageSizeEstimator msgSizeEstimator;
    private volatile int connectTimeoutMillis;
    private volatile int maxMessagesPerRead;
    private volatile int writeSpinCount;
    private volatile boolean autoRead;
    private volatile int writeBufferHighWaterMark;
    private volatile int writeBufferLowWaterMark;

    public DefaultChannelConfig(Channel channel) {
        this.allocator = DefaultChannelConfig.DEFAULT_ALLOCATOR;
        this.rcvBufAllocator = DefaultChannelConfig.DEFAULT_RCVBUF_ALLOCATOR;
        this.msgSizeEstimator = DefaultChannelConfig.DEFAULT_MSG_SIZE_ESTIMATOR;
        this.connectTimeoutMillis = 30000;
        this.writeSpinCount = 16;
        this.autoRead = true;
        this.writeBufferHighWaterMark = 65536;
        this.writeBufferLowWaterMark = 32768;
        if (channel == null) {
            throw new NullPointerException("channel");
        } else {
            this.channel = channel;
            if (channel instanceof ServerChannel) {
                this.maxMessagesPerRead = 16;
            } else {
                this.maxMessagesPerRead = 1;
            }

        }
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions((Map) null, ChannelOption.CONNECT_TIMEOUT_MILLIS, ChannelOption.MAX_MESSAGES_PER_READ, ChannelOption.WRITE_SPIN_COUNT, ChannelOption.ALLOCATOR, ChannelOption.AUTO_READ, ChannelOption.RCVBUF_ALLOCATOR, ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, ChannelOption.MESSAGE_SIZE_ESTIMATOR);
    }

    protected Map<ChannelOption<?>, Object> getOptions(Map<ChannelOption<?>, Object> map, ChannelOption<?>... achanneloption) {
        if (map == null) {
            map = new IdentityHashMap();
        }

        ChannelOption[] achanneloption1 = achanneloption;
        int i = achanneloption.length;

        for (int j = 0; j < i; ++j) {
            ChannelOption channeloption = achanneloption1[j];

            ((Map) map).put(channeloption, this.getOption(channeloption));
        }

        return (Map) map;
    }

    @Override
    public boolean setOptions(Map<ChannelOption<?>, ?> map) {
        if (map == null) {
            throw new NullPointerException("options");
        } else {
            boolean flag = true;
            Iterator iterator = map.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();

                if (!this.setOption((ChannelOption) entry.getKey(), entry.getValue())) {
                    flag = false;
                }
            }

            return flag;
        }
    }

    @Override
    public <T> T getOption(ChannelOption<T> channeloption) {
        if (channeloption == null) {
            throw new NullPointerException("option");
        } else {
            return channeloption == ChannelOption.CONNECT_TIMEOUT_MILLIS ? this.getConnectTimeoutMillis() : (channeloption == ChannelOption.MAX_MESSAGES_PER_READ ? this.getMaxMessagesPerRead() : (channeloption == ChannelOption.WRITE_SPIN_COUNT ? this.getWriteSpinCount() : (channeloption == ChannelOption.ALLOCATOR ? this.getAllocator() : (channeloption == ChannelOption.RCVBUF_ALLOCATOR ? this.getRecvByteBufAllocator() : (channeloption == ChannelOption.AUTO_READ ? this.isAutoRead() : (channeloption == ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK ? this.getWriteBufferHighWaterMark() : (channeloption == ChannelOption.WRITE_BUFFER_LOW_WATER_MARK ? this.getWriteBufferLowWaterMark() : (channeloption == ChannelOption.MESSAGE_SIZE_ESTIMATOR ? this.getMessageSizeEstimator() : null))))))));
        }
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> channeloption, T t0) {
        this.validate(channeloption, t0);
        if (channeloption == ChannelOption.CONNECT_TIMEOUT_MILLIS) {
            this.setConnectTimeoutMillis((Integer) t0);
        } else if (channeloption == ChannelOption.MAX_MESSAGES_PER_READ) {
            this.setMaxMessagesPerRead((Integer) t0);
        } else if (channeloption == ChannelOption.WRITE_SPIN_COUNT) {
            this.setWriteSpinCount((Integer) t0);
        } else if (channeloption == ChannelOption.ALLOCATOR) {
            this.setAllocator((ByteBufAllocator) t0);
        } else if (channeloption == ChannelOption.RCVBUF_ALLOCATOR) {
            this.setRecvByteBufAllocator((RecvByteBufAllocator) t0);
        } else if (channeloption == ChannelOption.AUTO_READ) {
            this.setAutoRead((Boolean) t0);
        } else if (channeloption == ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK) {
            this.setWriteBufferHighWaterMark((Integer) t0);
        } else if (channeloption == ChannelOption.WRITE_BUFFER_LOW_WATER_MARK) {
            this.setWriteBufferLowWaterMark((Integer) t0);
        } else {
            if (channeloption != ChannelOption.MESSAGE_SIZE_ESTIMATOR) {
                return false;
            }

            this.setMessageSizeEstimator((MessageSizeEstimator) t0);
        }

        return true;
    }

    protected <T> void validate(ChannelOption<T> channeloption, T t0) {
        if (channeloption == null) {
            throw new NullPointerException("option");
        } else {
            channeloption.validate(t0);
        }
    }

    @Override
    public int getConnectTimeoutMillis() {
        return this.connectTimeoutMillis;
    }

    @Override
    public ChannelConfig setConnectTimeoutMillis(int i) {
        if (i < 0) {
            throw new IllegalArgumentException(String.format("connectTimeoutMillis: %d (expected: >= 0)", i));
        } else {
            this.connectTimeoutMillis = i;
            return this;
        }
    }

    @Override
    public int getMaxMessagesPerRead() {
        return this.maxMessagesPerRead;
    }

    @Override
    public ChannelConfig setMaxMessagesPerRead(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("maxMessagesPerRead: " + i + " (expected: > 0)");
        } else {
            this.maxMessagesPerRead = i;
            return this;
        }
    }

    @Override
    public int getWriteSpinCount() {
        return this.writeSpinCount;
    }

    @Override
    public ChannelConfig setWriteSpinCount(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("writeSpinCount must be a positive integer.");
        } else {
            this.writeSpinCount = i;
            return this;
        }
    }

    @Override
    public ByteBufAllocator getAllocator() {
        return this.allocator;
    }

    @Override
    public ChannelConfig setAllocator(ByteBufAllocator bytebufallocator) {
        if (bytebufallocator == null) {
            throw new NullPointerException("allocator");
        } else {
            this.allocator = bytebufallocator;
            return this;
        }
    }

    @Override
    public RecvByteBufAllocator getRecvByteBufAllocator() {
        return this.rcvBufAllocator;
    }

    @Override
    public ChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator recvbytebufallocator) {
        if (recvbytebufallocator == null) {
            throw new NullPointerException("allocator");
        } else {
            this.rcvBufAllocator = recvbytebufallocator;
            return this;
        }
    }

    @Override
    public boolean isAutoRead() {
        return this.autoRead;
    }

    @Override
    public ChannelConfig setAutoRead(boolean flag) {
        boolean flag1 = this.autoRead;

        this.autoRead = flag;
        if (flag && !flag1) {
            this.channel.read();
        }

        return this;
    }

    @Override
    public int getWriteBufferHighWaterMark() {
        return this.writeBufferHighWaterMark;
    }

    @Override
    public ChannelConfig setWriteBufferHighWaterMark(int i) {
        if (i < this.getWriteBufferLowWaterMark()) {
            throw new IllegalArgumentException("writeBufferHighWaterMark cannot be less than writeBufferLowWaterMark (" + this.getWriteBufferLowWaterMark() + "): " + i);
        } else if (i < 0) {
            throw new IllegalArgumentException("writeBufferHighWaterMark must be >= 0");
        } else {
            this.writeBufferHighWaterMark = i;
            return this;
        }
    }

    @Override
    public int getWriteBufferLowWaterMark() {
        return this.writeBufferLowWaterMark;
    }

    @Override
    public ChannelConfig setWriteBufferLowWaterMark(int i) {
        if (i > this.getWriteBufferHighWaterMark()) {
            throw new IllegalArgumentException("writeBufferLowWaterMark cannot be greater than writeBufferHighWaterMark (" + this.getWriteBufferHighWaterMark() + "): " + i);
        } else if (i < 0) {
            throw new IllegalArgumentException("writeBufferLowWaterMark must be >= 0");
        } else {
            this.writeBufferLowWaterMark = i;
            return this;
        }
    }

    @Override
    public MessageSizeEstimator getMessageSizeEstimator() {
        return this.msgSizeEstimator;
    }

    @Override
    public ChannelConfig setMessageSizeEstimator(MessageSizeEstimator messagesizeestimator) {
        if (messagesizeestimator == null) {
            throw new NullPointerException("estimator");
        } else {
            this.msgSizeEstimator = messagesizeestimator;
            return this;
        }
    }
}
