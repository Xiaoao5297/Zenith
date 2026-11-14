package io.netty.channel.rxtx;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;

public interface RxtxChannelConfig extends ChannelConfig {

    RxtxChannelConfig setBaudrate(int i);

    RxtxChannelConfig setStopbits(RxtxChannelConfig.Stopbits rxtxchannelconfig_stopbits);

    RxtxChannelConfig setDatabits(RxtxChannelConfig.Databits rxtxchannelconfig_databits);

    RxtxChannelConfig setParitybit(RxtxChannelConfig.Paritybit rxtxchannelconfig_paritybit);

    int getBaudrate();

    RxtxChannelConfig.Stopbits getStopbits();

    RxtxChannelConfig.Databits getDatabits();

    RxtxChannelConfig.Paritybit getParitybit();

    boolean isDtr();

    RxtxChannelConfig setDtr(boolean flag);

    boolean isRts();

    RxtxChannelConfig setRts(boolean flag);

    int getWaitTimeMillis();

    RxtxChannelConfig setWaitTimeMillis(int i);

    RxtxChannelConfig setReadTimeout(int i);

    int getReadTimeout();

    @Override
    RxtxChannelConfig setConnectTimeoutMillis(int i);

    @Override
    RxtxChannelConfig setMaxMessagesPerRead(int i);

    @Override
    RxtxChannelConfig setWriteSpinCount(int i);

    @Override
    RxtxChannelConfig setAllocator(ByteBufAllocator bytebufallocator);

    @Override
    RxtxChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator recvbytebufallocator);

    @Override
    RxtxChannelConfig setAutoRead(boolean flag);

    @Override
    RxtxChannelConfig setWriteBufferHighWaterMark(int i);

    @Override
    RxtxChannelConfig setWriteBufferLowWaterMark(int i);

    @Override
    RxtxChannelConfig setMessageSizeEstimator(MessageSizeEstimator messagesizeestimator);

    public static enum Paritybit {

        NONE(0), ODD(1), EVEN(2), MARK(3), SPACE(4);

        private final int value;

        private Paritybit(int i) {
            this.value = i;
        }

        public int value() {
            return this.value;
        }

        public static RxtxChannelConfig.Paritybit valueOf(int i) {
            RxtxChannelConfig.Paritybit[] arxtxchannelconfig_paritybit = values();
            int j = arxtxchannelconfig_paritybit.length;

            for (int k = 0; k < j; ++k) {
                RxtxChannelConfig.Paritybit rxtxchannelconfig_paritybit = arxtxchannelconfig_paritybit[k];

                if (rxtxchannelconfig_paritybit.value == i) {
                    return rxtxchannelconfig_paritybit;
                }
            }

            throw new IllegalArgumentException("unknown " + RxtxChannelConfig.Paritybit.class.getSimpleName() + " value: " + i);
        }
    }

    public static enum Databits {

        DATABITS_5(5), DATABITS_6(6), DATABITS_7(7), DATABITS_8(8);

        private final int value;

        private Databits(int i) {
            this.value = i;
        }

        public int value() {
            return this.value;
        }

        public static RxtxChannelConfig.Databits valueOf(int i) {
            RxtxChannelConfig.Databits[] arxtxchannelconfig_databits = values();
            int j = arxtxchannelconfig_databits.length;

            for (int k = 0; k < j; ++k) {
                RxtxChannelConfig.Databits rxtxchannelconfig_databits = arxtxchannelconfig_databits[k];

                if (rxtxchannelconfig_databits.value == i) {
                    return rxtxchannelconfig_databits;
                }
            }

            throw new IllegalArgumentException("unknown " + RxtxChannelConfig.Databits.class.getSimpleName() + " value: " + i);
        }
    }

    public static enum Stopbits {

        STOPBITS_1(1), STOPBITS_2(2), STOPBITS_1_5(3);

        private final int value;

        private Stopbits(int i) {
            this.value = i;
        }

        public int value() {
            return this.value;
        }

        public static RxtxChannelConfig.Stopbits valueOf(int i) {
            RxtxChannelConfig.Stopbits[] arxtxchannelconfig_stopbits = values();
            int j = arxtxchannelconfig_stopbits.length;

            for (int k = 0; k < j; ++k) {
                RxtxChannelConfig.Stopbits rxtxchannelconfig_stopbits = arxtxchannelconfig_stopbits[k];

                if (rxtxchannelconfig_stopbits.value == i) {
                    return rxtxchannelconfig_stopbits;
                }
            }

            throw new IllegalArgumentException("unknown " + RxtxChannelConfig.Stopbits.class.getSimpleName() + " value: " + i);
        }
    }
}
