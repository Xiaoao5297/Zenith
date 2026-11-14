package com.google.common.util.concurrent;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

public final class ExecutionList {

    @VisibleForTesting
    static final Logger log = Logger.getLogger(ExecutionList.class.getName());
    @GuardedBy("this")
    private ExecutionList.RunnableExecutorPair runnables;
    @GuardedBy("this")
    private boolean executed;

    public ExecutionList() {}

    public void add(Runnable runnable, Executor executor) {
        Preconditions.checkNotNull(runnable, "Runnable was null.");
        Preconditions.checkNotNull(executor, "Executor was null.");
        synchronized (this) {
            if (!this.executed) {
                this.runnables = new ExecutionList.RunnableExecutorPair(runnable, executor, this.runnables);
                return;
            }
        }

        executeListener(runnable, executor);
    }

    public void execute() {
        ExecutionList.RunnableExecutorPair executionlist_runnableexecutorpair;

        synchronized (this) {
            if (this.executed) {
                return;
            }

            this.executed = true;
            executionlist_runnableexecutorpair = this.runnables;
            this.runnables = null;
        }

        ExecutionList.RunnableExecutorPair executionlist_runnableexecutorpair1;
        ExecutionList.RunnableExecutorPair executionlist_runnableexecutorpair2;

        for (executionlist_runnableexecutorpair2 = null; executionlist_runnableexecutorpair != null; executionlist_runnableexecutorpair2 = executionlist_runnableexecutorpair1) {
            executionlist_runnableexecutorpair1 = executionlist_runnableexecutorpair;
            executionlist_runnableexecutorpair = executionlist_runnableexecutorpair.next;
            executionlist_runnableexecutorpair1.next = executionlist_runnableexecutorpair2;
        }

        while (executionlist_runnableexecutorpair2 != null) {
            executeListener(executionlist_runnableexecutorpair2.runnable, executionlist_runnableexecutorpair2.executor);
            executionlist_runnableexecutorpair2 = executionlist_runnableexecutorpair2.next;
        }

    }

    private static void executeListener(Runnable runnable, Executor executor) {
        try {
            executor.execute(runnable);
        } catch (RuntimeException runtimeexception) {
            ExecutionList.log.log(Level.SEVERE, "RuntimeException while executing runnable " + runnable + " with executor " + executor, runtimeexception);
        }

    }

    private static final class RunnableExecutorPair {

        final Runnable runnable;
        final Executor executor;
        @Nullable
        ExecutionList.RunnableExecutorPair next;

        RunnableExecutorPair(Runnable runnable, Executor executor, ExecutionList.RunnableExecutorPair executionlist_runnableexecutorpair) {
            this.runnable = runnable;
            this.executor = executor;
            this.next = executionlist_runnableexecutorpair;
        }
    }
}
