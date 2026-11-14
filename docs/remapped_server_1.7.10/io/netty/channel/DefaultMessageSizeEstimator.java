package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;

public final class DefaultMessageSizeEstimator implements MessageSizeEstimator {

    public static final MessageSizeEstimator DEFAULT = new DefaultMessageSizeEstimator(0);
    private final MessageSizeEstimator.Handle handle;

    public DefaultMessageSizeEstimator(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("unknownSize: " + i + " (expected: >= 0)");
        } else {
            this.handle = new DefaultMessageSizeEstimator.HandleImpl(i);
        }
    }

    @Override
    public MessageSizeEstimator.Handle newHandle() {
        return this.handle;
    }

    private static final class HandleImpl implements MessageSizeEstimator.Handle {

        private final int unknownSize;

        private HandleImpl(int i) {
            this.unknownSize = i;
        }

        @Override
        public int size(Object object) {
            return object instanceof ByteBuf ? ((ByteBuf) object).readableBytes() : (object instanceof ByteBufHolder ? ((ByteBufHolder) object).content().readableBytes() : (object instanceof FileRegion ? 0 : this.unknownSize));
        }
    }
}
