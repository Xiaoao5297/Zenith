package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.concurrent.GuardedBy;

@Beta
public abstract class AbstractScheduledService implements Service {

    private static final Logger logger = Logger.getLogger(AbstractScheduledService.class.getName());
    private final AbstractService delegate = new AbstractService() {
        private volatile Future<?> runningTask;
        private volatile ScheduledExecutorService executorService;
        private final ReentrantLock lock = new ReentrantLock();
        private final Runnable task = new Runnable() {
            public void run() {
                lock.lock();

                try {
                    AbstractScheduledService.this.runOneIteration();
                } catch (Throwable throwable) {
                    try {
                        AbstractScheduledService.this.shutDown();
                    } catch (Exception exception) {
                        AbstractScheduledService.logger.log(Level.WARNING, "Error while attempting to shut down the service after failure.", exception);
                    }

                    notifyFailed(throwable);
                    throw Throwables.propagate(throwable);
                } finally {
                    lock.unlock();
                }

            }
        };

        @Override
        protected final void doStart() {
            this.executorService = MoreExecutors.renamingDecorator(AbstractScheduledService.this.executor(), new Supplier<String>() {
                @Override
                public String get() {
                    return AbstractScheduledService.this.serviceName() + " " + state();
                }
            });
            this.executorService.execute(new Runnable() {
                public void run() {
                    lock.lock();

                    try {
                        AbstractScheduledService.this.startUp();
                        runningTask = AbstractScheduledService.this.scheduler().schedule(AbstractScheduledService.this.delegate, executorService, task);
                        notifyStarted();
                    } catch (Throwable throwable) {
                        notifyFailed(throwable);
                        throw Throwables.propagate(throwable);
                    } finally {
                        lock.unlock();
                    }

                }
            });
        }

        @Override
        protected final void doStop() {
            this.runningTask.cancel(false);
            this.executorService.execute(new Runnable() {
                public void run() {
                    try {
                        lock.lock();

                        try {
                            if (state() != Service.State.STOPPING) {
                                return;
                            }

                            AbstractScheduledService.this.shutDown();
                        } finally {
                            lock.unlock();
                        }

                        notifyStopped();
                    } catch (Throwable throwable) {
                        notifyFailed(throwable);
                        throw Throwables.propagate(throwable);
                    }
                }
            });
        }
    };

    protected AbstractScheduledService() {}

    protected abstract void runOneIteration() throws Exception;

    protected void startUp() throws Exception {}

    protected void shutDown() throws Exception {}

    protected abstract AbstractScheduledService.Scheduler scheduler();

    protected ScheduledExecutorService executor() {
        final ScheduledExecutorService scheduledexecutorservice = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            public Thread newThread(Runnable runnable) {
                return MoreExecutors.newThread(AbstractScheduledService.this.serviceName(), runnable);
            }
        });

        this.addListener(new Service.Listener() {
            @Override
            public void terminated(Service.State service_state) {
                scheduledexecutorservice.shutdown();
            }

            @Override
            public void failed(Service.State service_state, Throwable throwable) {
                scheduledexecutorservice.shutdown();
            }
        }, MoreExecutors.sameThreadExecutor());
        return scheduledexecutorservice;
    }

    protected String serviceName() {
        return this.getClass().getSimpleName();
    }

    public String toString() {
        return this.serviceName() + " [" + this.state() + "]";
    }

    /** @deprecated */
    @Deprecated
    @Override
    public final ListenableFuture<Service.State> start() {
        return this.delegate.start();
    }

    /** @deprecated */
    @Deprecated
    @Override
    public final Service.State startAndWait() {
        return this.delegate.startAndWait();
    }

    @Override
    public final boolean isRunning() {
        return this.delegate.isRunning();
    }

    @Override
    public final Service.State state() {
        return this.delegate.state();
    }

    /** @deprecated */
    @Deprecated
    @Override
    public final ListenableFuture<Service.State> stop() {
        return this.delegate.stop();
    }

    /** @deprecated */
    @Deprecated
    @Override
    public final Service.State stopAndWait() {
        return this.delegate.stopAndWait();
    }

    @Override
    public final void addListener(Service.Listener service_listener, Executor executor) {
        this.delegate.addListener(service_listener, executor);
    }

    @Override
    public final Throwable failureCause() {
        return this.delegate.failureCause();
    }

    @Override
    public final Service startAsync() {
        this.delegate.startAsync();
        return this;
    }

    @Override
    public final Service stopAsync() {
        this.delegate.stopAsync();
        return this;
    }

    @Override
    public final void awaitRunning() {
        this.delegate.awaitRunning();
    }

    @Override
    public final void awaitRunning(long i, TimeUnit timeunit) throws TimeoutException {
        this.delegate.awaitRunning(i, timeunit);
    }

    @Override
    public final void awaitTerminated() {
        this.delegate.awaitTerminated();
    }

    @Override
    public final void awaitTerminated(long i, TimeUnit timeunit) throws TimeoutException {
        this.delegate.awaitTerminated(i, timeunit);
    }

    @Beta
    public abstract static class CustomScheduler extends AbstractScheduledService.Scheduler {

        public CustomScheduler() {
            super(null);
        }

        @Override
        final Future<?> schedule(AbstractService abstractservice, ScheduledExecutorService scheduledexecutorservice, Runnable runnable) {
            AbstractScheduledService.CustomScheduler.ReschedulableCallable abstractscheduledservice_customscheduler_reschedulablecallable = new AbstractScheduledService.CustomScheduler.ReschedulableCallable(abstractservice, scheduledexecutorservice, runnable);

            abstractscheduledservice_customscheduler_reschedulablecallable.reschedule();
            return abstractscheduledservice_customscheduler_reschedulablecallable;
        }

        protected abstract AbstractScheduledService.CustomScheduler.Schedule getNextSchedule() throws Exception;

        @Beta
        protected static final class Schedule {

            private final long delay;
            private final TimeUnit unit;

            public Schedule(long i, TimeUnit timeunit) {
                this.delay = i;
                this.unit = (TimeUnit) Preconditions.checkNotNull(timeunit);
            }
        }

        private class ReschedulableCallable extends ForwardingFuture<Void> implements Callable<Void> {

            private final Runnable wrappedRunnable;
            private final ScheduledExecutorService executor;
            private final AbstractService service;
            private final ReentrantLock lock = new ReentrantLock();
            @GuardedBy("lock")
            private Future<Void> currentFuture;

            ReschedulableCallable(AbstractService abstractservice, ScheduledExecutorService scheduledexecutorservice, Runnable runnable) {
                this.wrappedRunnable = runnable;
                this.executor = scheduledexecutorservice;
                this.service = abstractservice;
            }

            public Void call() throws Exception {
                this.wrappedRunnable.run();
                this.reschedule();
                return null;
            }

            public void reschedule() {
                this.lock.lock();

                try {
                    if (this.currentFuture == null || !this.currentFuture.isCancelled()) {
                        AbstractScheduledService.CustomScheduler.Schedule abstractscheduledservice_customscheduler_schedule = CustomScheduler.this.getNextSchedule();

                        this.currentFuture = this.executor.schedule(this, abstractscheduledservice_customscheduler_schedule.delay, abstractscheduledservice_customscheduler_schedule.unit);
                    }
                } catch (Throwable throwable) {
                    this.service.notifyFailed(throwable);
                } finally {
                    this.lock.unlock();
                }

            }

            @Override
            public boolean cancel(boolean flag) {
                this.lock.lock();

                boolean flag1;

                try {
                    flag1 = this.currentFuture.cancel(flag);
                } finally {
                    this.lock.unlock();
                }

                return flag1;
            }

            @Override
            protected Future<Void> delegate() {
                throw new UnsupportedOperationException("Only cancel is supported by this future");
            }
        }
    }

    public abstract static class Scheduler {

        public static AbstractScheduledService.Scheduler newFixedDelaySchedule(final long i, final long j, final TimeUnit timeunit) {
            return new AbstractScheduledService.Scheduler() {
                @Override
                public Future<?> schedule(AbstractService abstractservice, ScheduledExecutorService scheduledexecutorservice, Runnable runnable) {
                    return scheduledexecutorservice.scheduleWithFixedDelay(runnable, i, j, timeunit);
                }
            };
        }

        public static AbstractScheduledService.Scheduler newFixedRateSchedule(final long i, final long j, final TimeUnit timeunit) {
            return new AbstractScheduledService.Scheduler() {
                @Override
                public Future<?> schedule(AbstractService abstractservice, ScheduledExecutorService scheduledexecutorservice, Runnable runnable) {
                    return scheduledexecutorservice.scheduleAtFixedRate(runnable, i, j, timeunit);
                }
            };
        }

        abstract Future<?> schedule(AbstractService abstractservice, ScheduledExecutorService scheduledexecutorservice, Runnable runnable);

        private Scheduler() {}
    }
}
