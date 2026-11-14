package io.netty.util.concurrent;

import java.util.concurrent.ThreadFactory;

final class DefaultEventExecutor extends SingleThreadEventExecutor {

    DefaultEventExecutor(DefaultEventExecutorGroup defaulteventexecutorgroup, ThreadFactory threadfactory) {
        super(defaulteventexecutorgroup, threadfactory, true);
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
