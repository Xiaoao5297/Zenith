package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Beta
public abstract class AbstractExecutionThreadService implements Service {

    private static final Logger logger = Logger.getLogger(AbstractExecutionThreadService.class.getName());
    private final Service delegate = new AbstractService() {
        @Override
        protected final void doStart() {
            Executor executor = MoreExecutors.renamingDecorator(AbstractExecutionThreadService.this.executor(), new Supplier<String>() {
                @Override
                public String get() {
                    return AbstractExecutionThreadService.this.serviceName();
                }
            });

            executor.execute(new Runnable() {
                public void run() {
                    try {
                        AbstractExecutionThreadService.this.startUp();
                        notifyStarted();
                        if (isRunning()) {
                            try {
                                AbstractExecutionThreadService.this.run();
                            } catch (Throwable throwable) {
                                try {
                                    AbstractExecutionThreadService.this.shutDown();
                                } catch (Exception exception) {
                                    AbstractExecutionThreadService.logger.log(Level.WARNING, "Error while attempting to shut down the service after failure.", exception);
                                }

                                throw throwable;
                            }
                        }

                        AbstractExecutionThreadService.this.shutDown();
                        notifyStopped();
                    } catch (Throwable throwable1) {
                        notifyFailed(throwable1);
                        throw Throwables.propagate(throwable1);
                    }
                }
            });
        }

        @Override
        protected void doStop() {
            AbstractExecutionThreadService.this.triggerShutdown();
        }
    };

    protected AbstractExecutionThreadService() {}

    protected void startUp() throws Exception {}

    protected abstract void run() throws Exception;

    protected void shutDown() throws Exception {}

    protected void triggerShutdown() {}

    protected Executor executor() {
        return new Executor() {
            public void execute(Runnable runnable) {
                MoreExecutors.newThread(AbstractExecutionThreadService.this.serviceName(), runnable).start();
            }
        };
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

    protected String serviceName() {
        return this.getClass().getSimpleName();
    }
}
