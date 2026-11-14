package io.netty.handler.traffic;

import io.netty.channel.ChannelHandler;
import io.netty.util.concurrent.EventExecutor;
import java.util.concurrent.ScheduledExecutorService;

@ChannelHandler.Sharable
public class GlobalTrafficShapingHandler extends AbstractTrafficShapingHandler {

    void createGlobalTrafficCounter(ScheduledExecutorService scheduledexecutorservice) {
        if (scheduledexecutorservice == null) {
            throw new NullPointerException("executor");
        } else {
            TrafficCounter trafficcounter = new TrafficCounter(this, scheduledexecutorservice, "GlobalTC", this.checkInterval);

            this.setTrafficCounter(trafficcounter);
            trafficcounter.start();
        }
    }

    public GlobalTrafficShapingHandler(ScheduledExecutorService scheduledexecutorservice, long i, long j, long k) {
        super(i, j, k);
        this.createGlobalTrafficCounter(scheduledexecutorservice);
    }

    public GlobalTrafficShapingHandler(ScheduledExecutorService scheduledexecutorservice, long i, long j) {
        super(i, j);
        this.createGlobalTrafficCounter(scheduledexecutorservice);
    }

    public GlobalTrafficShapingHandler(ScheduledExecutorService scheduledexecutorservice, long i) {
        super(i);
        this.createGlobalTrafficCounter(scheduledexecutorservice);
    }

    public GlobalTrafficShapingHandler(EventExecutor eventexecutor) {
        this.createGlobalTrafficCounter(eventexecutor);
    }

    public final void release() {
        if (this.trafficCounter != null) {
            this.trafficCounter.stop();
        }

    }
}
