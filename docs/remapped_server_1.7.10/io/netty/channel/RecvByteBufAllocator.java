package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public interface RecvByteBufAllocator {

    RecvByteBufAllocator.Handle newHandle();

    public interface Handle {

        ByteBuf allocate(ByteBufAllocator bytebufallocator);

        int guess();

        void record(int i);
    }
}
