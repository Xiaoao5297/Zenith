package io.netty.channel.nio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ServerChannel;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNioMessageChannel extends AbstractNioChannel {

    protected AbstractNioMessageChannel(Channel channel, SelectableChannel selectablechannel, int i) {
        super(channel, selectablechannel, i);
    }

    @Override
    protected AbstractNioChannel.AbstractNioUnsafe newUnsafe() {
        return new AbstractNioMessageChannel.NioMessageUnsafe();
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer channeloutboundbuffer) throws Exception {
        SelectionKey selectionkey = this.selectionKey();
        int i = selectionkey.interestOps();

        while (true) {
            Object object = channeloutboundbuffer.current();

            if (object == null) {
                if ((i & 4) != 0) {
                    selectionkey.interestOps(i & -5);
                }
                break;
            }

            boolean flag = false;

            for (int j = this.config().getWriteSpinCount() - 1; j >= 0; --j) {
                if (this.doWriteMessage(object, channeloutboundbuffer)) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                if ((i & 4) == 0) {
                    selectionkey.interestOps(i | 4);
                }
                break;
            }

            channeloutboundbuffer.remove();
        }

    }

    protected abstract int doReadMessages(List<Object> list) throws Exception;

    protected abstract boolean doWriteMessage(Object object, ChannelOutboundBuffer channeloutboundbuffer) throws Exception;

    private final class NioMessageUnsafe extends AbstractNioChannel.AbstractNioUnsafe {

        private final List<Object> readBuf;

        private NioMessageUnsafe() {
            super();
            this.readBuf = new ArrayList();
        }

        @Override
        public void read() {
            assert AbstractNioMessageChannel.this.eventLoop().inEventLoop();

            SelectionKey selectionkey = AbstractNioMessageChannel.this.selectionKey();

            if (!AbstractNioMessageChannel.this.config().isAutoRead()) {
                int i = selectionkey.interestOps();

                if ((i & AbstractNioMessageChannel.this.readInterestOp) != 0) {
                    selectionkey.interestOps(i & ~AbstractNioMessageChannel.this.readInterestOp);
                }
            }

            ChannelConfig channelconfig = AbstractNioMessageChannel.this.config();
            int j = channelconfig.getMaxMessagesPerRead();
            boolean flag = channelconfig.isAutoRead();
            ChannelPipeline channelpipeline = AbstractNioMessageChannel.this.pipeline();
            boolean flag1 = false;
            Throwable throwable = null;

            int k;

            try {
                do {
                    k = AbstractNioMessageChannel.this.doReadMessages(this.readBuf);
                    if (k == 0) {
                        break;
                    }

                    if (k < 0) {
                        flag1 = true;
                        break;
                    }
                } while (!(this.readBuf.size() >= j | !flag));
            } catch (Throwable throwable1) {
                throwable = throwable1;
            }

            k = this.readBuf.size();

            for (int l = 0; l < k; ++l) {
                channelpipeline.fireChannelRead(this.readBuf.get(l));
            }

            this.readBuf.clear();
            channelpipeline.fireChannelReadComplete();
            if (throwable != null) {
                if (throwable instanceof IOException) {
                    flag1 = !(AbstractNioMessageChannel.this instanceof ServerChannel);
                }

                channelpipeline.fireExceptionCaught(throwable);
            }

            if (flag1 && AbstractNioMessageChannel.this.isOpen()) {
                this.close(this.voidPromise());
            }

        }
    }
}
