package io.netty.handler.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class MessageToMessageEncoder<I> extends ChannelOutboundHandlerAdapter {

    private final TypeParameterMatcher matcher;

    protected MessageToMessageEncoder() {
        this.matcher = TypeParameterMatcher.find(this, MessageToMessageEncoder.class, "I");
    }

    protected MessageToMessageEncoder(Class<? extends I> oclass) {
        this.matcher = TypeParameterMatcher.get(oclass);
    }

    public boolean acceptOutboundMessage(Object object) throws Exception {
        return this.matcher.match(object);
    }

    @Override
    public void write(ChannelHandlerContext channelhandlercontext, Object object, ChannelPromise channelpromise) throws Exception {
        RecyclableArrayList recyclablearraylist = null;
        boolean flag = false;

        try {
            flag = true;
            if (this.acceptOutboundMessage(object)) {
                recyclablearraylist = RecyclableArrayList.newInstance();
                Object object1 = object;

                try {
                    this.encode(channelhandlercontext, object1, recyclablearraylist);
                } finally {
                    ReferenceCountUtil.release(object);
                }

                if (recyclablearraylist.isEmpty()) {
                    recyclablearraylist.recycle();
                    recyclablearraylist = null;
                    throw new EncoderException(StringUtil.simpleClassName((Object) this) + " must produce at least one message.");
                }

                flag = false;
            } else {
                channelhandlercontext.write(object, channelpromise);
                flag = false;
            }
        } catch (EncoderException encoderexception) {
            throw encoderexception;
        } catch (Throwable throwable) {
            throw new EncoderException(throwable);
        } finally {
            if (flag) {
                if (recyclablearraylist != null) {
                    int i = recyclablearraylist.size() - 1;

                    if (i >= 0) {
                        for (int j = 0; j < i; ++j) {
                            channelhandlercontext.write(recyclablearraylist.get(j));
                        }

                        channelhandlercontext.write(recyclablearraylist.get(i), channelpromise);
                    }

                    recyclablearraylist.recycle();
                }

            }
        }

        if (recyclablearraylist != null) {
            int k = recyclablearraylist.size() - 1;

            if (k >= 0) {
                for (int l = 0; l < k; ++l) {
                    channelhandlercontext.write(recyclablearraylist.get(l));
                }

                channelhandlercontext.write(recyclablearraylist.get(k), channelpromise);
            }

            recyclablearraylist.recycle();
        }

    }

    protected abstract void encode(ChannelHandlerContext channelhandlercontext, I i0, List<Object> list) throws Exception;
}
