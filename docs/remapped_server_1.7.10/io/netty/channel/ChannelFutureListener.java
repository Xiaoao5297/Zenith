package io.netty.channel;

import io.netty.util.concurrent.GenericFutureListener;

public interface ChannelFutureListener extends GenericFutureListener<ChannelFuture> {

    ChannelFutureListener CLOSE = new ChannelFutureListener() {
        public void operationComplete(ChannelFuture channelfuture) {
            channelfuture.channel().close();
        }
    };
    ChannelFutureListener CLOSE_ON_FAILURE = new ChannelFutureListener() {
        public void operationComplete(ChannelFuture channelfuture) {
            if (!channelfuture.isSuccess()) {
                channelfuture.channel().close();
            }

        }
    };
    ChannelFutureListener FIRE_EXCEPTION_ON_FAILURE = new ChannelFutureListener() {
        public void operationComplete(ChannelFuture channelfuture) {
            if (!channelfuture.isSuccess()) {
                channelfuture.channel().pipeline().fireExceptionCaught(channelfuture.cause());
            }

        }
    };
}
