package io.netty.handler.traffic;

import io.netty.channel.ChannelHandlerContext;

public class ChannelTrafficShapingHandler extends AbstractTrafficShapingHandler {

    public ChannelTrafficShapingHandler(long i, long j, long k) {
        super(i, j, k);
    }

    public ChannelTrafficShapingHandler(long i, long j) {
        super(i, j);
    }

    public ChannelTrafficShapingHandler(long i) {
        super(i);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext channelhandlercontext) throws Exception {
        TrafficCounter trafficcounter = new TrafficCounter(this, channelhandlercontext.executor(), "ChannelTC" + channelhandlercontext.channel().hashCode(), this.checkInterval);

        this.setTrafficCounter(trafficcounter);
        trafficcounter.start();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext channelhandlercontext) throws Exception {
        if (this.trafficCounter != null) {
            this.trafficCounter.stop();
        }

    }
}
