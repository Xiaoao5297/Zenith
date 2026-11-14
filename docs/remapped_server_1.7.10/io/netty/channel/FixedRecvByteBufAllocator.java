package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class FixedRecvByteBufAllocator implements RecvByteBufAllocator {

    private final RecvByteBufAllocator.Handle handle;

    public FixedRecvByteBufAllocator(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("bufferSize must greater than 0: " + i);
        } else {
            this.handle = new FixedRecvByteBufAllocator.HandleImpl(i);
        }
    }

    @Override
    public RecvByteBufAllocator.Handle newHandle() {
        return this.handle;
    }

    private static final class HandleImpl implements RecvByteBufAllocator.Handle {

        private final int bufferSize;

        HandleImpl(int i) {
            this.bufferSize = i;
        }

        @Override
        public ByteBuf allocate(ByteBufAllocator bytebufallocator) {
            return bytebufallocator.ioBuffer(this.bufferSize);
        }

        @Override
        public int guess() {
            return this.bufferSize;
        }

        @Override
        public void record(int i) {}
    }
}
