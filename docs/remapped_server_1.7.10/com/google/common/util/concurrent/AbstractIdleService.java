package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
public abstract class AbstractIdleService implements Service {

    private final Supplier<String> threadNameSupplier = new Supplier<String>() {
        @Override
        public String get() {
            return AbstractIdleService.this.serviceName() + " " + AbstractIdleService.this.state();
        }
    };
    private final Service delegate = new AbstractService() {
        @Override
        protected final void doStart() {
            MoreExecutors.renamingDecorator(AbstractIdleService.this.executor(), AbstractIdleService.this.threadNameSupplier).execute(new Runnable() {
                public void run() {
                    try {
                        AbstractIdleService.this.startUp();
                        notifyStarted();
                    } catch (Throwable throwable) {
                        notifyFailed(throwable);
                        throw Throwables.propagate(throwable);
                    }
                }
            });
        }

        @Override
        protected final void doStop() {
            MoreExecutors.renamingDecorator(AbstractIdleService.this.executor(), AbstractIdleService.this.threadNameSupplier).execute(new Runnable() {
                public void run() {
                    try {
                        AbstractIdleService.this.shutDown();
                        notifyStopped();
                    } catch (Throwable throwable) {
                        notifyFailed(throwable);
                        throw Throwables.propagate(throwable);
                    }
                }
            });
        }
    };

    protected AbstractIdleService() {}

    protected abstract void startUp() throws Exception;

    protected abstract void shutDown() throws Exception;

    protected Executor executor() {
        return new Executor() {
            public void execute(Runnable runnable) {
                MoreExecutors.newThread((String) AbstractIdleService.this.threadNameSupplier.get(), runnable).start();
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
