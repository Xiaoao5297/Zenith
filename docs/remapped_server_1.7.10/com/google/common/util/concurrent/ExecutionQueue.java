package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
final class ExecutionQueue {

    private static final Logger logger = Logger.getLogger(ExecutionQueue.class.getName());
    private final ConcurrentLinkedQueue<ExecutionQueue.RunnableExecutorPair> queuedListeners = Queues.newConcurrentLinkedQueue();
    private final ReentrantLock lock = new ReentrantLock();

    ExecutionQueue() {}

    void add(Runnable runnable, Executor executor) {
        this.queuedListeners.add(new ExecutionQueue.RunnableExecutorPair(runnable, executor));
    }

    void execute() {
        Iterator iterator = this.queuedListeners.iterator();

        while (iterator.hasNext()) {
            ((ExecutionQueue.RunnableExecutorPair) iterator.next()).submit();
            iterator.remove();
        }

    }

    private final class RunnableExecutorPair implements Runnable {

        private final Executor executor;
        private final Runnable runnable;
        @GuardedBy("lock")
        private boolean hasBeenExecuted = false;

        RunnableExecutorPair(Runnable runnable, Executor executor) {
            this.runnable = (Runnable) Preconditions.checkNotNull(runnable);
            this.executor = (Executor) Preconditions.checkNotNull(executor);
        }

        private void submit() {
            ExecutionQueue.this.lock.lock();

            try {
                if (!this.hasBeenExecuted) {
                    try {
                        this.executor.execute(this);
                    } catch (Exception exception) {
                        ExecutionQueue.logger.log(Level.SEVERE, "Exception while executing listener " + this.runnable + " with executor " + this.executor, exception);
                    }
                }
            } finally {
                if (ExecutionQueue.this.lock.isHeldByCurrentThread()) {
                    this.hasBeenExecuted = true;
                    ExecutionQueue.this.lock.unlock();
                }

            }

        }

        public final void run() {
            if (ExecutionQueue.this.lock.isHeldByCurrentThread()) {
                this.hasBeenExecuted = true;
                ExecutionQueue.this.lock.unlock();
            }

            this.runnable.run();
        }
    }
}
