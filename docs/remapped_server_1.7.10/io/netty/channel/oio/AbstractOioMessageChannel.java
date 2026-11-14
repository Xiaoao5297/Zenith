package io.netty.channel.oio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractOioMessageChannel extends AbstractOioChannel {

    private final List<Object> readBuf = new ArrayList();

    protected AbstractOioMessageChannel(Channel channel) {
        super(channel);
    }

    @Override
    protected void doRead() {
        ChannelPipeline channelpipeline = this.pipeline();
        boolean flag = false;
        Throwable throwable = null;

        int i;

        try {
            i = this.doReadMessages(this.readBuf);
            if (i < 0) {
                flag = true;
            }
        } catch (Throwable throwable1) {
            throwable = throwable1;
        }

        i = this.readBuf.size();

        for (int j = 0; j < i; ++j) {
            channelpipeline.fireChannelRead(this.readBuf.get(j));
        }

        this.readBuf.clear();
        channelpipeline.fireChannelReadComplete();
        if (throwable != null) {
            if (throwable instanceof IOException) {
                flag = true;
            }

            this.pipeline().fireExceptionCaught(throwable);
        }

        if (flag && this.isOpen()) {
            this.unsafe().close(this.unsafe().voidPromise());
        }

    }

    protected abstract int doReadMessages(List<Object> list) throws Exception;
}
