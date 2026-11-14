package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;

@Beta
public abstract class AbstractService implements Service {

    private final Monitor monitor = new Monitor();
    private final AbstractService.Transition startup = new AbstractService.Transition();
    private final AbstractService.Transition shutdown = new AbstractService.Transition();
    private final Monitor.Guard isStartable;
    private final Monitor.Guard isStoppable;
    private final Monitor.Guard hasReachedRunning;
    private final Monitor.Guard isStopped;
    @GuardedBy("monitor")
    private final List<AbstractService.ListenerExecutorPair> listeners;
    private final ExecutionQueue queuedListeners;
    @GuardedBy("monitor")
    private volatile AbstractService.StateSnapshot snapshot;

    protected AbstractService() {
        this.isStartable = new Monitor.Guard(this.monitor) {
            @Override
            public boolean isSatisfied() {
                return AbstractService.this.state() == Service.State.NEW;
            }
        };
        this.isStoppable = new Monitor.Guard(this.monitor) {
            @Override
            public boolean isSatisfied() {
                return AbstractService.this.state().compareTo(Service.State.RUNNING) <= 0;
            }
        };
        this.hasReachedRunning = new Monitor.Guard(this.monitor) {
            @Override
            public boolean isSatisfied() {
                return AbstractService.this.state().compareTo(Service.State.RUNNING) >= 0;
            }
        };
        this.isStopped = new Monitor.Guard(this.monitor) {
            @Override
            public boolean isSatisfied() {
                return AbstractService.this.state().isTerminal();
            }
        };
        this.listeners = Lists.newArrayList();
        this.queuedListeners = new ExecutionQueue();
        this.snapshot = new AbstractService.StateSnapshot(Service.State.NEW);
        this.addListener(new Service.Listener() {
            @Override
            public void running() {
                AbstractService.this.startup.set(Service.State.RUNNING);
            }

            @Override
            public void stopping(Service.State service_state) {
                if (service_state == Service.State.STARTING) {
                    AbstractService.this.startup.set(Service.State.STOPPING);
                }

            }

            @Override
            public void terminated(Service.State service_state) {
                if (service_state == Service.State.NEW) {
                    AbstractService.this.startup.set(Service.State.TERMINATED);
                }

                AbstractService.this.shutdown.set(Service.State.TERMINATED);
            }

            @Override
            public void failed(Service.State service_state, Throwable throwable) {
                switch (service_state) {
                    case STARTING:
                        AbstractService.this.startup.setException(throwable);
                        AbstractService.this.shutdown.setException(new Exception("Service failed to start.", throwable));
                        break;
                    case RUNNING:
                        AbstractService.this.shutdown.setException(new Exception("Service failed while running", throwable));
                        break;
                    case STOPPING:
                        AbstractService.this.shutdown.setException(throwable);
                        break;
                    case TERMINATED:
                    case FAILED:
                    case NEW:
                    default:
                        throw new AssertionError("Unexpected from state: " + service_state);
                }

            }
        }, MoreExecutors.sameThreadExecutor());
    }

    protected abstract void doStart();

    protected abstract void doStop();

    @Override
    public final Service startAsync() {
        if (this.monitor.enterIf(this.isStartable)) {
            try {
                this.snapshot = new AbstractService.StateSnapshot(Service.State.STARTING);
                this.starting();
                this.doStart();
            } catch (Throwable throwable) {
                this.notifyFailed(throwable);
            } finally {
                this.monitor.leave();
                this.executeListeners();
            }

            return this;
        } else {
            throw new IllegalStateException("Service " + this + " has already been started");
        }
    }

    /** @deprecated */
    @Deprecated
    @Override
    public final ListenableFuture<Service.State> start() {
        if (this.monitor.enterIf(this.isStartable)) {
            try {
                this.snapshot = new AbstractService.StateSnapshot(Service.State.STARTING);
                this.starting();
                this.doStart();
            } catch (Throwable throwable) {
                this.notifyFailed(throwable);
            } finally {
                this.monitor.leave();
                this.executeListeners();
            }
        }

        return this.startup;
    }

    @Override
    public final Service stopAsync() {
        this.stop();
        return this;
    }

    /** @deprecated */
    @Deprecated
    @Override
    public final ListenableFuture<Service.State> stop() {
        if (this.monitor.enterIf(this.isStoppable)) {
            try {
                Service.State service_state = this.state();

                switch (service_state) {
                    case STARTING:
                        this.snapshot = new AbstractService.StateSnapshot(Service.State.STARTING, true, (Throwable) null);
                        this.stopping(Service.State.STARTING);
                        break;
                    case RUNNING:
                        this.snapshot = new AbstractService.StateSnapshot(Service.State.STOPPING);
                        this.stopping(Service.State.RUNNING);
                        this.doStop();
                        break;
                    case STOPPING:
                    case TERMINATED:
                    case FAILED:
                        throw new AssertionError("isStoppable is incorrectly implemented, saw: " + service_state);
                    case NEW:
                        this.snapshot = new AbstractService.StateSnapshot(Service.State.TERMINATED);
                        this.terminated(Service.State.NEW);
                        break;
                    default:
                        throw new AssertionError("Unexpected state: " + service_state);
                }
            } catch (Throwable throwable) {
                this.notifyFailed(throwable);
            } finally {
                this.monitor.leave();
                this.executeListeners();
            }
        }

        return this.shutdown;
    }

    /** @deprecated */
    @Deprecated
    @Override
    public Service.State startAndWait() {
        return (Service.State) Futures.getUnchecked(this.start());
    }

    /** @deprecated */
    @Deprecated
    @Override
    public Service.State stopAndWait() {
        return (Service.State) Futures.getUnchecked(this.stop());
    }

    @Override
    public final void awaitRunning() {
        this.monitor.enterWhenUninterruptibly(this.hasReachedRunning);

        try {
            this.checkCurrentState(Service.State.RUNNING);
        } finally {
            this.monitor.leave();
        }

    }

    @Override
    public final void awaitRunning(long i, TimeUnit timeunit) throws TimeoutException {
        if (this.monitor.enterWhenUninterruptibly(this.hasReachedRunning, i, timeunit)) {
            try {
                this.checkCurrentState(Service.State.RUNNING);
            } finally {
                this.monitor.leave();
            }

        } else {
            throw new TimeoutException("Timed out waiting for " + this + " to reach the RUNNING state. " + "Current state: " + this.state());
        }
    }

    @Override
    public final void awaitTerminated() {
        this.monitor.enterWhenUninterruptibly(this.isStopped);

        try {
            this.checkCurrentState(Service.State.TERMINATED);
        } finally {
            this.monitor.leave();
        }

    }

    @Override
    public final void awaitTerminated(long i, TimeUnit timeunit) throws TimeoutException {
        if (this.monitor.enterWhenUninterruptibly(this.isStopped, i, timeunit)) {
            try {
                Service.State service_state = this.state();

                this.checkCurrentState(Service.State.TERMINATED);
            } finally {
                this.monitor.leave();
            }

        } else {
            throw new TimeoutException("Timed out waiting for " + this + " to reach a terminal state. " + "Current state: " + this.state());
        }
    }

    @GuardedBy("monitor")
    private void checkCurrentState(Service.State service_state) {
        Service.State service_state1 = this.state();

        if (service_state1 != service_state) {
            if (service_state1 == Service.State.FAILED) {
                throw new IllegalStateException("Expected the service to be " + service_state + ", but the service has FAILED", this.failureCause());
            } else {
                throw new IllegalStateException("Expected the service to be " + service_state + ", but was " + service_state1);
            }
        }
    }

    protected final void notifyStarted() {
        this.monitor.enter();

        try {
            if (this.snapshot.state != Service.State.STARTING) {
                IllegalStateException illegalstateexception = new IllegalStateException("Cannot notifyStarted() when the service is " + this.snapshot.state);

                this.notifyFailed(illegalstateexception);
                throw illegalstateexception;
            }

            if (this.snapshot.shutdownWhenStartupFinishes) {
                this.snapshot = new AbstractService.StateSnapshot(Service.State.STOPPING);
                this.doStop();
            } else {
                this.snapshot = new AbstractService.StateSnapshot(Service.State.RUNNING);
                this.running();
            }
        } finally {
            this.monitor.leave();
            this.executeListeners();
        }

    }

    protected final void notifyStopped() {
        this.monitor.enter();

        try {
            Service.State service_state = this.snapshot.state;

            if (service_state != Service.State.STOPPING && service_state != Service.State.RUNNING) {
                IllegalStateException illegalstateexception = new IllegalStateException("Cannot notifyStopped() when the service is " + service_state);

                this.notifyFailed(illegalstateexception);
                throw illegalstateexception;
            }

            this.snapshot = new AbstractService.StateSnapshot(Service.State.TERMINATED);
            this.terminated(service_state);
        } finally {
            this.monitor.leave();
            this.executeListeners();
        }

    }

    protected final void notifyFailed(Throwable throwable) {
        Preconditions.checkNotNull(throwable);
        this.monitor.enter();

        try {
            Service.State service_state = this.state();

            switch (service_state) {
                case STARTING:
                case RUNNING:
                case STOPPING:
                    this.snapshot = new AbstractService.StateSnapshot(Service.State.FAILED, false, throwable);
                    this.failed(service_state, throwable);
                    break;
                case TERMINATED:
                case NEW:
                    throw new IllegalStateException("Failed while in state:" + service_state, throwable);
                case FAILED:
                    break;
                default:
                    throw new AssertionError("Unexpected state: " + service_state);
            }
        } finally {
            this.monitor.leave();
            this.executeListeners();
        }

    }

    @Override
    public final boolean isRunning() {
        return this.state() == Service.State.RUNNING;
    }

    @Override
    public final Service.State state() {
        return this.snapshot.externalState();
    }

    @Override
    public final Throwable failureCause() {
        return this.snapshot.failureCause();
    }

    @Override
    public final void addListener(Service.Listener service_listener, Executor executor) {
        Preconditions.checkNotNull(service_listener, "listener");
        Preconditions.checkNotNull(executor, "executor");
        this.monitor.enter();

        try {
            Service.State service_state = this.state();

            if (service_state != Service.State.TERMINATED && service_state != Service.State.FAILED) {
                this.listeners.add(new AbstractService.ListenerExecutorPair(service_listener, executor));
            }
        } finally {
            this.monitor.leave();
        }

    }

    public String toString() {
        return this.getClass().getSimpleName() + " [" + this.state() + "]";
    }

    private void executeListeners() {
        if (!this.monitor.isOccupiedByCurrentThread()) {
            this.queuedListeners.execute();
        }

    }

    @GuardedBy("monitor")
    private void starting() {
        Iterator iterator = this.listeners.iterator();

        while (iterator.hasNext()) {
            final AbstractService.ListenerExecutorPair abstractservice_listenerexecutorpair = (AbstractService.ListenerExecutorPair) iterator.next();

            this.queuedListeners.add(new Runnable() {
                public void run() {
                    abstractservice_listenerexecutorpair.listener.starting();
                }
            }, abstractservice_listenerexecutorpair.executor);
        }

    }

    @GuardedBy("monitor")
    private void running() {
        Iterator iterator = this.listeners.iterator();

        while (iterator.hasNext()) {
            final AbstractService.ListenerExecutorPair abstractservice_listenerexecutorpair = (AbstractService.ListenerExecutorPair) iterator.next();

            this.queuedListeners.add(new Runnable() {
                public void run() {
                    abstractservice_listenerexecutorpair.listener.running();
                }
            }, abstractservice_listenerexecutorpair.executor);
        }

    }

    @GuardedBy("monitor")
    private void stopping(final Service.State service_state) {
        Iterator iterator = this.listeners.iterator();

        while (iterator.hasNext()) {
            final AbstractService.ListenerExecutorPair abstractservice_listenerexecutorpair = (AbstractService.ListenerExecutorPair) iterator.next();

            this.queuedListeners.add(new Runnable() {
                public void run() {
                    abstractservice_listenerexecutorpair.listener.stopping(service_state);
                }
            }, abstractservice_listenerexecutorpair.executor);
        }

    }

    @GuardedBy("monitor")
    private void terminated(final Service.State service_state) {
        Iterator iterator = this.listeners.iterator();

        while (iterator.hasNext()) {
            final AbstractService.ListenerExecutorPair abstractservice_listenerexecutorpair = (AbstractService.ListenerExecutorPair) iterator.next();

            this.queuedListeners.add(new Runnable() {
                public void run() {
                    abstractservice_listenerexecutorpair.listener.terminated(service_state);
                }
            }, abstractservice_listenerexecutorpair.executor);
        }

        this.listeners.clear();
    }

    @GuardedBy("monitor")
    private void failed(final Service.State service_state, final Throwable throwable) {
        Iterator iterator = this.listeners.iterator();

        while (iterator.hasNext()) {
            final AbstractService.ListenerExecutorPair abstractservice_listenerexecutorpair = (AbstractService.ListenerExecutorPair) iterator.next();

            this.queuedListeners.add(new Runnable() {
                public void run() {
                    abstractservice_listenerexecutorpair.listener.failed(service_state, throwable);
                }
            }, abstractservice_listenerexecutorpair.executor);
        }

        this.listeners.clear();
    }

    @Immutable
    private static final class StateSnapshot {

        final Service.State state;
        final boolean shutdownWhenStartupFinishes;
        @Nullable
        final Throwable failure;

        StateSnapshot(Service.State service_state) {
            this(service_state, false, (Throwable) null);
        }

        StateSnapshot(Service.State service_state, boolean flag, @Nullable Throwable throwable) {
            Preconditions.checkArgument(!flag || service_state == Service.State.STARTING, "shudownWhenStartupFinishes can only be set if state is STARTING. Got %s instead.", service_state);
            Preconditions.checkArgument(!(throwable != null ^ service_state == Service.State.FAILED), "A failure cause should be set if and only if the state is failed.  Got %s and %s instead.", service_state, throwable);
            this.state = service_state;
            this.shutdownWhenStartupFinishes = flag;
            this.failure = throwable;
        }

        Service.State externalState() {
            return this.shutdownWhenStartupFinishes && this.state == Service.State.STARTING ? Service.State.STOPPING : this.state;
        }

        Throwable failureCause() {
            Preconditions.checkState(this.state == Service.State.FAILED, "failureCause() is only valid if the service has failed, service is %s", this.state);
            return this.failure;
        }
    }

    private static class ListenerExecutorPair {

        final Service.Listener listener;
        final Executor executor;

        ListenerExecutorPair(Service.Listener service_listener, Executor executor) {
            this.listener = service_listener;
            this.executor = executor;
        }
    }

    private class Transition extends AbstractFuture<Service.State> {

        private Transition() {}

        @Override
        public Service.State get(long i, TimeUnit timeunit) throws InterruptedException, TimeoutException, ExecutionException {
            try {
                return (Service.State) super.get(i, timeunit);
            } catch (TimeoutException timeoutexception) {
                throw new TimeoutException(AbstractService.this.toString());
            }
        }
    }
}
