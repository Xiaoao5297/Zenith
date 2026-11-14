package io.netty.util.internal.chmv8;

public class ForkJoinWorkerThread extends Thread {

    final ForkJoinPool pool;
    final ForkJoinPool.WorkQueue workQueue;

    protected ForkJoinWorkerThread(ForkJoinPool forkjoinpool) {
        super("aForkJoinWorkerThread");
        this.pool = forkjoinpool;
        this.workQueue = forkjoinpool.registerWorker(this);
    }

    public ForkJoinPool getPool() {
        return this.pool;
    }

    public int getPoolIndex() {
        return this.workQueue.poolIndex >>> 1;
    }

    protected void onStart() {}

    protected void onTermination(Throwable throwable) {}

    public void run() {
        Throwable throwable = null;

        try {
            this.onStart();
            this.pool.runWorker(this.workQueue);
        } catch (Throwable throwable1) {
            throwable = throwable1;
        } finally {
            try {
                this.onTermination(throwable);
            } catch (Throwable throwable2) {
                if (throwable == null) {
                    throwable = throwable2;
                }
            } finally {
                this.pool.deregisterWorker(this, throwable);
            }

        }

    }
}
