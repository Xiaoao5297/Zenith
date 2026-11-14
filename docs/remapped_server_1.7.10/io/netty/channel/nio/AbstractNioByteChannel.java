package io.netty.channel.nio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.FileRegion;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

public abstract class AbstractNioByteChannel extends AbstractNioChannel {

    protected AbstractNioByteChannel(Channel channel, SelectableChannel selectablechannel) {
        super(channel, selectablechannel, 1);
    }

    @Override
    protected AbstractNioChannel.AbstractNioUnsafe newUnsafe() {
        return new AbstractNioByteChannel.NioByteUnsafe();
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer channeloutboundbuffer) throws Exception {
        int i = -1;

        while (true) {
            Object object = channeloutboundbuffer.current();

            if (object == null) {
                this.clearOpWrite();
                break;
            }

            if (object instanceof ByteBuf) {
                ByteBuf bytebuf = (ByteBuf) object;
                int j = bytebuf.readableBytes();

                if (j == 0) {
                    channeloutboundbuffer.remove();
                } else {
                    if (!bytebuf.isDirect()) {
                        ByteBufAllocator bytebufallocator = this.alloc();

                        if (bytebufallocator.isDirectBufferPooled()) {
                            bytebuf = bytebufallocator.directBuffer(j).writeBytes(bytebuf);
                            channeloutboundbuffer.current(bytebuf);
                        }
                    }

                    boolean flag = false;
                    long k = 0L;

                    if (i == -1) {
                        i = this.config().getWriteSpinCount();
                    }

                    for (int l = i - 1; l >= 0; --l) {
                        int i1 = this.doWriteBytes(bytebuf);

                        if (i1 == 0) {
                            break;
                        }

                        k += (long) i1;
                        if (!bytebuf.isReadable()) {
                            flag = true;
                            break;
                        }
                    }

                    channeloutboundbuffer.progress(k);
                    if (!flag) {
                        this.setOpWrite();
                        break;
                    }

                    channeloutboundbuffer.remove();
                }
            } else {
                if (!(object instanceof FileRegion)) {
                    throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(object));
                }

                FileRegion fileregion = (FileRegion) object;
                boolean flag1 = false;
                long j1 = 0L;

                if (i == -1) {
                    i = this.config().getWriteSpinCount();
                }

                for (int k1 = i - 1; k1 >= 0; --k1) {
                    long l1 = this.doWriteFileRegion(fileregion);

                    if (l1 == 0L) {
                        break;
                    }

                    j1 += l1;
                    if (fileregion.transfered() >= fileregion.count()) {
                        flag1 = true;
                        break;
                    }
                }

                channeloutboundbuffer.progress(j1);
                if (!flag1) {
                    this.setOpWrite();
                    break;
                }

                channeloutboundbuffer.remove();
            }
        }

    }

    protected abstract long doWriteFileRegion(FileRegion fileregion) throws Exception;

    protected abstract int doReadBytes(ByteBuf bytebuf) throws Exception;

    protected abstract int doWriteBytes(ByteBuf bytebuf) throws Exception;

    protected final void setOpWrite() {
        SelectionKey selectionkey = this.selectionKey();
        int i = selectionkey.interestOps();

        if ((i & 4) == 0) {
            selectionkey.interestOps(i | 4);
        }

    }

    protected final void clearOpWrite() {
        SelectionKey selectionkey = this.selectionKey();
        int i = selectionkey.interestOps();

        if ((i & 4) != 0) {
            selectionkey.interestOps(i & -5);
        }

    }

    private final class NioByteUnsafe extends AbstractNioChannel.AbstractNioUnsafe {

        private RecvByteBufAllocator.Handle allocHandle;

        private NioByteUnsafe() {
            super();
        }

        @Override
        public void read() {
            assert AbstractNioByteChannel.this.eventLoop().inEventLoop();

            SelectionKey selectionkey = AbstractNioByteChannel.this.selectionKey();
            ChannelConfig channelconfig = AbstractNioByteChannel.this.config();

            if (!channelconfig.isAutoRead()) {
                int i = selectionkey.interestOps();

                if ((i & AbstractNioByteChannel.this.readInterestOp) != 0) {
                    selectionkey.interestOps(i & ~AbstractNioByteChannel.this.readInterestOp);
                }
            }

            ChannelPipeline channelpipeline = AbstractNioByteChannel.this.pipeline();
            RecvByteBufAllocator.Handle recvbytebufallocator_handle = this.allocHandle;

            if (recvbytebufallocator_handle == null) {
                this.allocHandle = recvbytebufallocator_handle = channelconfig.getRecvByteBufAllocator().newHandle();
            }

            ByteBufAllocator bytebufallocator = channelconfig.getAllocator();
            int j = channelconfig.getMaxMessagesPerRead();
            boolean flag = false;
            Throwable throwable = null;
            ByteBuf bytebuf = null;
            int k = 0;

            try {
                do {
                    bytebuf = recvbytebufallocator_handle.allocate(bytebufallocator);
                    int l = AbstractNioByteChannel.this.doReadBytes(bytebuf);

                    if (l == 0) {
                        bytebuf.release();
                        bytebuf = null;
                        break;
                    }

                    if (l < 0) {
                        flag = true;
                        bytebuf.release();
                        bytebuf = null;
                        break;
                    }

                    channelpipeline.fireChannelRead(bytebuf);
                    recvbytebufallocator_handle.record(l);
                    bytebuf = null;
                    ++k;
                } while (k != j);
            } catch (Throwable throwable1) {
                throwable = throwable1;
            } finally {
                if (bytebuf != null) {
                    if (bytebuf.isReadable()) {
                        channelpipeline.fireChannelRead(bytebuf);
                    } else {
                        bytebuf.release();
                    }
                }

                channelpipeline.fireChannelReadComplete();
                if (throwable != null) {
                    if (throwable instanceof IOException) {
                        flag = true;
                    }

                    AbstractNioByteChannel.this.pipeline().fireExceptionCaught(throwable);
                }

                if (flag) {
                    AbstractNioByteChannel.this.setInputShutdown();
                    if (AbstractNioByteChannel.this.isOpen()) {
                        if (Boolean.TRUE.equals(AbstractNioByteChannel.this.config().getOption(ChannelOption.ALLOW_HALF_CLOSURE))) {
                            selectionkey.interestOps(selectionkey.interestOps() & ~AbstractNioByteChannel.this.readInterestOp);
                            channelpipeline.fireUserEventTriggered(ChannelInputShutdownEvent.INSTANCE);
                        } else {
                            this.close(this.voidPromise());
                        }
                    }
                }

            }

        }
    }
}
