package io.netty.handler.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class MessageToMessageDecoder<I> extends ChannelInboundHandlerAdapter {

    private final TypeParameterMatcher matcher;

    protected MessageToMessageDecoder() {
        this.matcher = TypeParameterMatcher.find(this, MessageToMessageDecoder.class, "I");
    }

    protected MessageToMessageDecoder(Class<? extends I> oclass) {
        this.matcher = TypeParameterMatcher.get(oclass);
    }

    public boolean acceptInboundMessage(Object object) throws Exception {
        return this.matcher.match(object);
    }

    @Override
    public void channelRead(ChannelHandlerContext channelhandlercontext, Object object) throws Exception {
        RecyclableArrayList recyclablearraylist = RecyclableArrayList.newInstance();
        boolean flag = false;

        try {
            flag = true;
            if (this.acceptInboundMessage(object)) {
                Object object1 = object;

                try {
                    this.decode(channelhandlercontext, object1, recyclablearraylist);
                } finally {
                    ReferenceCountUtil.release(object);
                }

                flag = false;
            } else {
                recyclablearraylist.add(object);
                flag = false;
            }
        } catch (DecoderException decoderexception) {
            throw decoderexception;
        } catch (Exception exception) {
            throw new DecoderException(exception);
        } finally {
            if (flag) {
                int i = recyclablearraylist.size();

                for (int j = 0; j < i; ++j) {
                    channelhandlercontext.fireChannelRead(recyclablearraylist.get(j));
                }

                recyclablearraylist.recycle();
            }
        }

        int k = recyclablearraylist.size();

        for (int l = 0; l < k; ++l) {
            channelhandlercontext.fireChannelRead(recyclablearraylist.get(l));
        }

        recyclablearraylist.recycle();
    }

    protected abstract void decode(ChannelHandlerContext channelhandlercontext, I i0, List<Object> list) throws Exception;
}
