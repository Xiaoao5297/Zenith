package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
public interface Service {

    /** @deprecated */
    @Deprecated
    ListenableFuture<Service.State> start();

    /** @deprecated */
    @Deprecated
    Service.State startAndWait();

    Service startAsync();

    boolean isRunning();

    Service.State state();

    /** @deprecated */
    @Deprecated
    ListenableFuture<Service.State> stop();

    /** @deprecated */
    @Deprecated
    Service.State stopAndWait();

    Service stopAsync();

    void awaitRunning();

    void awaitRunning(long i, TimeUnit timeunit) throws TimeoutException;

    void awaitTerminated();

    void awaitTerminated(long i, TimeUnit timeunit) throws TimeoutException;

    Throwable failureCause();

    void addListener(Service.Listener service_listener, Executor executor);

    @Beta
    public abstract static class Listener {

        public Listener() {}

        public void starting() {}

        public void running() {}

        public void stopping(Service.State service_state) {}

        public void terminated(Service.State service_state) {}

        public void failed(Service.State service_state, Throwable throwable) {}
    }

    @Beta
    public static enum State {

        NEW {
            @Override
            boolean isTerminal() {
                return false;
            }
        },
        STARTING {
            @Override
            boolean isTerminal() {
                return false;
            }
        },
        RUNNING {
            @Override
            boolean isTerminal() {
                return false;
            }
        },
        STOPPING {
            @Override
            boolean isTerminal() {
                return false;
            }
        },
        TERMINATED {
            @Override
            boolean isTerminal() {
                return true;
            }
        },
        FAILED {
            @Override
            boolean isTerminal() {
                return true;
            }
        };

        private State() {}

        abstract boolean isTerminal();
    }
}
