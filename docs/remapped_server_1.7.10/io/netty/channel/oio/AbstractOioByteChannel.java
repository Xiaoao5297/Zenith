package io.netty.channel.oio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.FileRegion;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.util.internal.StringUtil;
import java.io.IOException;

public abstract class AbstractOioByteChannel extends AbstractOioChannel {

    private volatile boolean inputShutdown;
    private static final ChannelMetadata METADATA = new ChannelMetadata(false);

    protected AbstractOioByteChannel(Channel channel) {
        super(channel);
    }

    protected boolean isInputShutdown() {
        return this.inputShutdown;
    }

    @Override
    public ChannelMetadata metadata() {
        return AbstractOioByteChannel.METADATA;
    }

    protected boolean checkInputShutdown() {
        if (this.inputShutdown) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException interruptedexception) {
                ;
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void doRead() {
        if (!this.checkInputShutdown()) {
            ChannelPipeline channelpipeline = this.pipeline();
            ByteBuf bytebuf = this.alloc().buffer();
            boolean flag = false;
            boolean flag1 = false;
            Throwable throwable = null;

            try {
                do {
                    int i = this.doReadBytes(bytebuf);

                    if (i > 0) {
                        flag1 = true;
                    } else if (i < 0) {
                        flag = true;
                    }

                    int j = this.available();

                    if (j <= 0) {
                        break;
                    }

                    if (!bytebuf.isWritable()) {
                        int k = bytebuf.capacity();
                        int l = bytebuf.maxCapacity();

                        if (k == l) {
                            if (flag1) {
                                flag1 = false;
                                channelpipeline.fireChannelRead(bytebuf);
                                bytebuf = this.alloc().buffer();
                            }
                        } else {
                            int i1 = bytebuf.writerIndex();

                            if (i1 + j > l) {
                                bytebuf.capacity(l);
                            } else {
                                bytebuf.ensureWritable(j);
                            }
                        }
                    }
                } while (this.config().isAutoRead());
            } catch (Throwable throwable1) {
                throwable = throwable1;
            } finally {
                if (flag1) {
                    channelpipeline.fireChannelRead(bytebuf);
                } else {
                    bytebuf.release();
                }

                channelpipeline.fireChannelReadComplete();
                if (throwable != null) {
                    if (throwable instanceof IOException) {
                        flag = true;
                        this.pipeline().fireExceptionCaught(throwable);
                    } else {
                        channelpipeline.fireExceptionCaught(throwable);
                        this.unsafe().close(this.voidPromise());
                    }
                }

                if (flag) {
                    this.inputShutdown = true;
                    if (this.isOpen()) {
                        if (Boolean.TRUE.equals(this.config().getOption(ChannelOption.ALLOW_HALF_CLOSURE))) {
                            channelpipeline.fireUserEventTriggered(ChannelInputShutdownEvent.INSTANCE);
                        } else {
                            this.unsafe().close(this.unsafe().voidPromise());
                        }
                    }
                }

            }

        }
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer channeloutboundbuffer) throws Exception {
        while (true) {
            Object object = channeloutboundbuffer.current();

            if (!(object instanceof ByteBuf)) {
                if (object instanceof FileRegion) {
                    this.doWriteFileRegion((FileRegion) object);
                    channeloutboundbuffer.remove();
                } else {
                    channeloutboundbuffer.remove(new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(object)));
                }
            } else {
                ByteBuf bytebuf = (ByteBuf) object;

                while (bytebuf.isReadable()) {
                    this.doWriteBytes(bytebuf);
                }

                channeloutboundbuffer.remove();
            }
        }
    }

    protected abstract int available();

    protected abstract int doReadBytes(ByteBuf bytebuf) throws Exception;

    protected abstract void doWriteBytes(ByteBuf bytebuf) throws Exception;

    protected abstract void doWriteFileRegion(FileRegion fileregion) throws Exception;
}
