package io.netty.util.concurrent;

import java.util.concurrent.Executor;

public final class ImmediateExecutor implements Executor {

    public static final ImmediateExecutor INSTANCE = new ImmediateExecutor();

    private ImmediateExecutor() {}

    public void execute(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("command");
        } else {
            runnable.run();
        }
    }
}
