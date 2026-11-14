package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufHolder;
import io.netty.util.Recycler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public final class ChannelOutboundBuffer {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelOutboundBuffer.class);
    private static final int INITIAL_CAPACITY = 32;
    private static final Recycler<ChannelOutboundBuffer> RECYCLER = new Recycler<ChannelOutboundBuffer>() {
        @Override
        protected ChannelOutboundBuffer newObject(Recycler.Handle recycler_handle) {
            return new ChannelOutboundBuffer(recycler_handle);
        }
    };
    private final Recycler.Handle handle;
    private AbstractChannel channel;
    private ChannelOutboundBuffer.Entry[] buffer;
    private int flushed;
    private int unflushed;
    private int tail;
    private ByteBuffer[] nioBuffers;
    private int nioBufferCount;
    private long nioBufferSize;
    private boolean inFail;
    private static final AtomicLongFieldUpdater<ChannelOutboundBuffer> TOTAL_PENDING_SIZE_UPDATER = AtomicLongFieldUpdater.newUpdater(ChannelOutboundBuffer.class, "totalPendingSize");
    private volatile long totalPendingSize;
    private static final AtomicIntegerFieldUpdater<ChannelOutboundBuffer> WRITABLE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(ChannelOutboundBuffer.class, "writable");
    private volatile int writable;

    static ChannelOutboundBuffer newInstance(AbstractChannel abstractchannel) {
        ChannelOutboundBuffer channeloutboundbuffer = (ChannelOutboundBuffer) ChannelOutboundBuffer.RECYCLER.get();

        channeloutboundbuffer.channel = abstractchannel;
        channeloutboundbuffer.totalPendingSize = 0L;
        channeloutboundbuffer.writable = 1;
        return channeloutboundbuffer;
    }

    private ChannelOutboundBuffer(Recycler.Handle recycler_handle) {
        this.writable = 1;
        this.handle = recycler_handle;
        this.buffer = new ChannelOutboundBuffer.Entry[32];

        for (int i = 0; i < this.buffer.length; ++i) {
            this.buffer[i] = new ChannelOutboundBuffer.Entry();
        }

        this.nioBuffers = new ByteBuffer[32];
    }

    void addMessage(Object object, ChannelPromise channelpromise) {
        int i = this.channel.estimatorHandle().size(object);

        if (i < 0) {
            i = 0;
        }

        ChannelOutboundBuffer.Entry channeloutboundbuffer_entry = this.buffer[this.tail++];

        channeloutboundbuffer_entry.msg = object;
        channeloutboundbuffer_entry.pendingSize = i;
        channeloutboundbuffer_entry.promise = channelpromise;
        channeloutboundbuffer_entry.total = total(object);
        this.tail &= this.buffer.length - 1;
        if (this.tail == this.flushed) {
            this.addCapacity();
        }

        this.incrementPendingOutboundBytes(i);
    }

    private void addCapacity() {
        int i = this.flushed;
        int j = this.buffer.length;
        int k = j - i;
        int l = this.size();
        int i1 = j << 1;

        if (i1 < 0) {
            throw new IllegalStateException();
        } else {
            ChannelOutboundBuffer.Entry[] achanneloutboundbuffer_entry = new ChannelOutboundBuffer.Entry[i1];

            System.arraycopy(this.buffer, i, achanneloutboundbuffer_entry, 0, k);
            System.arraycopy(this.buffer, 0, achanneloutboundbuffer_entry, k, i);

            for (int j1 = j; j1 < achanneloutboundbuffer_entry.length; ++j1) {
                achanneloutboundbuffer_entry[j1] = new ChannelOutboundBuffer.Entry();
            }

            this.buffer = achanneloutboundbuffer_entry;
            this.flushed = 0;
            this.unflushed = l;
            this.tail = j;
        }
    }

    void addFlush() {
        this.unflushed = this.tail;
    }

    void incrementPendingOutboundBytes(int i) {
        AbstractChannel abstractchannel = this.channel;

        if (i != 0 && abstractchannel != null) {
            long j = this.totalPendingSize;

            long k;

            for (k = j + (long) i; !ChannelOutboundBuffer.TOTAL_PENDING_SIZE_UPDATER.compareAndSet(this, j, k); k = j + (long) i) {
                j = this.totalPendingSize;
            }

            int l = abstractchannel.config().getWriteBufferHighWaterMark();

            if (k > (long) l && ChannelOutboundBuffer.WRITABLE_UPDATER.compareAndSet(this, 1, 0)) {
                abstractchannel.pipeline().fireChannelWritabilityChanged();
            }

        }
    }

    void decrementPendingOutboundBytes(int i) {
        AbstractChannel abstractchannel = this.channel;

        if (i != 0 && abstractchannel != null) {
            long j = this.totalPendingSize;

            long k;

            for (k = j - (long) i; !ChannelOutboundBuffer.TOTAL_PENDING_SIZE_UPDATER.compareAndSet(this, j, k); k = j - (long) i) {
                j = this.totalPendingSize;
            }

            int l = abstractchannel.config().getWriteBufferLowWaterMark();

            if ((k == 0L || k < (long) l) && ChannelOutboundBuffer.WRITABLE_UPDATER.compareAndSet(this, 0, 1)) {
                abstractchannel.pipeline().fireChannelWritabilityChanged();
            }

        }
    }

    private static long total(Object object) {
        return object instanceof ByteBuf ? (long) ((ByteBuf) object).readableBytes() : (object instanceof FileRegion ? ((FileRegion) object).count() : (object instanceof ByteBufHolder ? (long) ((ByteBufHolder) object).content().readableBytes() : -1L));
    }

    public Object current() {
        return this.isEmpty() ? null : this.buffer[this.flushed].msg;
    }

    public void current(Object object) {
        ChannelOutboundBuffer.Entry channeloutboundbuffer_entry = this.buffer[this.flushed];

        safeRelease(channeloutboundbuffer_entry.msg);
        channeloutboundbuffer_entry.msg = object;
    }

    public void progress(long i) {
        ChannelOutboundBuffer.Entry channeloutboundbuffer_entry = this.buffer[this.flushed];
        ChannelPromise channelpromise = channeloutboundbuffer_entry.promise;

        if (channelpromise instanceof ChannelProgressivePromise) {
            long j = channeloutboundbuffer_entry.progress + i;

            channeloutboundbuffer_entry.progress = j;
            ((ChannelProgressivePromise) channelpromise).tryProgress(j, channeloutboundbuffer_entry.total);
        }

    }

    public boolean remove() {
        if (this.isEmpty()) {
            return false;
        } else {
            ChannelOutboundBuffer.Entry channeloutboundbuffer_entry = this.buffer[this.flushed];
            Object object = channeloutboundbuffer_entry.msg;

            if (object == null) {
                return false;
            } else {
                ChannelPromise channelpromise = channeloutboundbuffer_entry.promise;
                int i = channeloutboundbuffer_entry.pendingSize;

                channeloutboundbuffer_entry.clear();
                this.flushed = this.flushed + 1 & this.buffer.length - 1;
                safeRelease(object);
                channelpromise.trySuccess();
                this.decrementPendingOutboundBytes(i);
                return true;
            }
        }
    }

    public boolean remove(Throwable throwable) {
        if (this.isEmpty()) {
            return false;
        } else {
            ChannelOutboundBuffer.Entry channeloutboundbuffer_entry = this.buffer[this.flushed];
            Object object = channeloutboundbuffer_entry.msg;

            if (object == null) {
                return false;
            } else {
                ChannelPromise channelpromise = channeloutboundbuffer_entry.promise;
                int i = channeloutboundbuffer_entry.pendingSize;

                channeloutboundbuffer_entry.clear();
                this.flushed = this.flushed + 1 & this.buffer.length - 1;
                safeRelease(object);
                safeFail(channelpromise, throwable);
                this.decrementPendingOutboundBytes(i);
                return true;
            }
        }
    }

    public ByteBuffer[] nioBuffers() {
        long i = 0L;
        int j = 0;
        int k = this.buffer.length - 1;
        ByteBufAllocator bytebufallocator = this.channel.alloc();
        ByteBuffer[] abytebuffer = this.nioBuffers;

        Object object;

        for (int l = this.flushed; l != this.unflushed && (object = this.buffer[l].msg) != null; l = l + 1 & k) {
            if (!(object instanceof ByteBuf)) {
                this.nioBufferCount = 0;
                this.nioBufferSize = 0L;
                return null;
            }

            ChannelOutboundBuffer.Entry channeloutboundbuffer_entry = this.buffer[l];
            ByteBuf bytebuf = (ByteBuf) object;
            int i1 = bytebuf.readerIndex();
            int j1 = bytebuf.writerIndex() - i1;

            if (j1 > 0) {
                i += (long) j1;
                int k1 = channeloutboundbuffer_entry.count;

                if (k1 == -1) {
                    channeloutboundbuffer_entry.count = k1 = bytebuf.nioBufferCount();
                }

                if (j + k1 > abytebuffer.length) {
                    this.nioBuffers = abytebuffer = doubleNioBufferArray(abytebuffer, j);
                }

                if (!bytebuf.isDirect() && bytebufallocator.isDirectBufferPooled()) {
                    j = fillBufferArrayNonDirect(channeloutboundbuffer_entry, bytebuf, i1, j1, bytebufallocator, abytebuffer, j);
                } else if (k1 == 1) {
                    ByteBuffer bytebuffer = channeloutboundbuffer_entry.buf;

                    if (bytebuffer == null) {
                        channeloutboundbuffer_entry.buf = bytebuffer = bytebuf.internalNioBuffer(i1, j1);
                    }

                    abytebuffer[j++] = bytebuffer;
                } else {
                    ByteBuffer[] abytebuffer1 = channeloutboundbuffer_entry.buffers;

                    if (abytebuffer1 == null) {
                        channeloutboundbuffer_entry.buffers = abytebuffer1 = bytebuf.nioBuffers();
                    }

                    j = fillBufferArray(abytebuffer1, abytebuffer, j);
                }
            }
        }

        this.nioBufferCount = j;
        this.nioBufferSize = i;
        return abytebuffer;
    }

    private static int fillBufferArray(ByteBuffer[] abytebuffer, ByteBuffer[] abytebuffer1, int i) {
        ByteBuffer[] abytebuffer2 = abytebuffer;
        int j = abytebuffer.length;

        for (int k = 0; k < j; ++k) {
            ByteBuffer bytebuffer = abytebuffer2[k];

            if (bytebuffer == null) {
                break;
            }

            abytebuffer1[i++] = bytebuffer;
        }

        return i;
    }

    private static int fillBufferArrayNonDirect(ChannelOutboundBuffer.Entry channeloutboundbuffer_entry, ByteBuf bytebuf, int i, int j, ByteBufAllocator bytebufallocator, ByteBuffer[] abytebuffer, int k) {
        ByteBuf bytebuf1 = bytebufallocator.directBuffer(j);

        bytebuf1.writeBytes(bytebuf, i, j);
        bytebuf.release();
        channeloutboundbuffer_entry.msg = bytebuf1;
        ByteBuffer bytebuffer = channeloutboundbuffer_entry.buf = bytebuf1.internalNioBuffer(0, j);

        channeloutboundbuffer_entry.count = 1;
        abytebuffer[k++] = bytebuffer;
        return k;
    }

    private static ByteBuffer[] doubleNioBufferArray(ByteBuffer[] abytebuffer, int i) {
        int j = abytebuffer.length << 1;

        if (j < 0) {
            throw new IllegalStateException();
        } else {
            ByteBuffer[] abytebuffer1 = new ByteBuffer[j];

            System.arraycopy(abytebuffer, 0, abytebuffer1, 0, i);
            return abytebuffer1;
        }
    }

    public int nioBufferCount() {
        return this.nioBufferCount;
    }

    public long nioBufferSize() {
        return this.nioBufferSize;
    }

    boolean getWritable() {
        return this.writable != 0;
    }

    public int size() {
        return this.unflushed - this.flushed & this.buffer.length - 1;
    }

    public boolean isEmpty() {
        return this.unflushed == this.flushed;
    }

    void failFlushed(Throwable throwable) {
        if (!this.inFail) {
            try {
                this.inFail = true;

                while (this.remove(throwable)) {
                    ;
                }
            } finally {
                this.inFail = false;
            }

        }
    }

    void close(final ClosedChannelException closedchannelexception) {
        if (this.inFail) {
            this.channel.eventLoop().execute(new Runnable() {
                public void run() {
                    ChannelOutboundBuffer.this.close(closedchannelexception);
                }
            });
        } else {
            this.inFail = true;
            if (this.channel.isOpen()) {
                throw new IllegalStateException("close() must be invoked after the channel is closed.");
            } else if (!this.isEmpty()) {
                throw new IllegalStateException("close() must be invoked after all flushed writes are handled.");
            } else {
                int i = this.tail - this.unflushed & this.buffer.length - 1;

                try {
                    for (int j = 0; j < i; ++j) {
                        ChannelOutboundBuffer.Entry channeloutboundbuffer_entry = this.buffer[this.unflushed + j & this.buffer.length - 1];

                        safeRelease(channeloutboundbuffer_entry.msg);
                        channeloutboundbuffer_entry.msg = null;
                        safeFail(channeloutboundbuffer_entry.promise, closedchannelexception);
                        channeloutboundbuffer_entry.promise = null;
                        int k = channeloutboundbuffer_entry.pendingSize;
                        long l = this.totalPendingSize;

                        for (long i1 = l - (long) k; !ChannelOutboundBuffer.TOTAL_PENDING_SIZE_UPDATER.compareAndSet(this, l, i1); i1 = l - (long) k) {
                            l = this.totalPendingSize;
                        }

                        channeloutboundbuffer_entry.pendingSize = 0;
                    }
                } finally {
                    this.tail = this.unflushed;
                    this.inFail = false;
                }

                this.recycle();
            }
        }
    }

    private static void safeRelease(Object object) {
        try {
            ReferenceCountUtil.release(object);
        } catch (Throwable throwable) {
            ChannelOutboundBuffer.logger.warn("Failed to release a message.", throwable);
        }

    }

    private static void safeFail(ChannelPromise channelpromise, Throwable throwable) {
        if (!(channelpromise instanceof VoidChannelPromise) && !channelpromise.tryFailure(throwable)) {
            ChannelOutboundBuffer.logger.warn("Promise done already: {} - new exception is:", channelpromise, throwable);
        }

    }

    public void recycle() {
        if (this.buffer.length > 32) {
            ChannelOutboundBuffer.Entry[] achanneloutboundbuffer_entry = new ChannelOutboundBuffer.Entry[32];

            System.arraycopy(this.buffer, 0, achanneloutboundbuffer_entry, 0, 32);
            this.buffer = achanneloutboundbuffer_entry;
        }

        if (this.nioBuffers.length > 32) {
            this.nioBuffers = new ByteBuffer[32];
        } else {
            Arrays.fill(this.nioBuffers, (Object) null);
        }

        this.flushed = 0;
        this.unflushed = 0;
        this.tail = 0;
        this.channel = null;
        ChannelOutboundBuffer.RECYCLER.recycle(this, this.handle);
    }

    private static final class Entry {

        Object msg;
        ByteBuffer[] buffers;
        ByteBuffer buf;
        ChannelPromise promise;
        long progress;
        long total;
        int pendingSize;
        int count;

        private Entry() {
            this.count = -1;
        }

        public void clear() {
            this.buffers = null;
            this.buf = null;
            this.msg = null;
            this.promise = null;
            this.progress = 0L;
            this.total = 0L;
            this.pendingSize = 0;
            this.count = -1;
        }
    }
}
