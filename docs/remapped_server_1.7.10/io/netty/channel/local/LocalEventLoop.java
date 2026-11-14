package io.netty.channel.local;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.SingleThreadEventLoop;
import java.util.concurrent.ThreadFactory;

final class LocalEventLoop extends SingleThreadEventLoop {

    LocalEventLoop(LocalEventLoopGroup localeventloopgroup, ThreadFactory threadfactory) {
        super((EventLoopGroup) localeventloopgroup, threadfactory, true);
    }

    @Override
    protected void run() {
        do {
            Runnable runnable = this.takeTask();

            if (runnable != null) {
                runnable.run();
                this.updateLastExecutionTime();
            }
        } while (!this.confirmShutdown());

    }
}
