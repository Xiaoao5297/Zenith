package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.base.Stopwatch;
import com.google.common.base.Supplier;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.Immutable;

@Beta
public final class ServiceManager {

    private static final Logger logger = Logger.getLogger(ServiceManager.class.getName());
    private final ServiceManager.ServiceManagerState state;
    private final ImmutableList<Service> services;

    public ServiceManager(Iterable<? extends Service> iterable) {
        ImmutableList immutablelist = ImmutableList.copyOf(iterable);

        if (immutablelist.isEmpty()) {
            ServiceManager.logger.log(Level.WARNING, "ServiceManager configured with no services.  Is your application configured properly?", new ServiceManager.EmptyServiceManagerWarning());
            immutablelist = ImmutableList.of(new ServiceManager.NoOpService());
        }

        this.state = new ServiceManager.ServiceManagerState(immutablelist);
        this.services = immutablelist;
        WeakReference weakreference = new WeakReference(this.state);
        Iterator iterator = immutablelist.iterator();

        while (iterator.hasNext()) {
            Service service = (Service) iterator.next();

            service.addListener(new ServiceManager.ServiceListener(service, weakreference), new ServiceManager.SynchronizedExecutor());
            Preconditions.checkArgument(service.state() == Service.State.NEW, "Can only manage NEW services, %s", service);
        }

        this.state.markReady();
    }

    public void addListener(ServiceManager.Listener servicemanager_listener, Executor executor) {
        this.state.addListener(servicemanager_listener, executor);
    }

    public void addListener(ServiceManager.Listener servicemanager_listener) {
        this.state.addListener(servicemanager_listener, MoreExecutors.sameThreadExecutor());
    }

    public ServiceManager startAsync() {
        Iterator iterator = this.services.iterator();

        Service service;

        while (iterator.hasNext()) {
            service = (Service) iterator.next();
            Service.State service_state = service.state();

            Preconditions.checkState(service_state == Service.State.NEW, "Service %s is %s, cannot start it.", service, service_state);
        }

        iterator = this.services.iterator();

        while (iterator.hasNext()) {
            service = (Service) iterator.next();

            try {
                service.startAsync();
            } catch (IllegalStateException illegalstateexception) {
                ServiceManager.logger.log(Level.WARNING, "Unable to start Service " + service, illegalstateexception);
            }
        }

        return this;
    }

    public void awaitHealthy() {
        this.state.awaitHealthy();
    }

    public void awaitHealthy(long i, TimeUnit timeunit) throws TimeoutException {
        this.state.awaitHealthy(i, timeunit);
    }

    public ServiceManager stopAsync() {
        Iterator iterator = this.services.iterator();

        while (iterator.hasNext()) {
            Service service = (Service) iterator.next();

            service.stopAsync();
        }

        return this;
    }

    public void awaitStopped() {
        this.state.awaitStopped();
    }

    public void awaitStopped(long i, TimeUnit timeunit) throws TimeoutException {
        this.state.awaitStopped(i, timeunit);
    }

    public boolean isHealthy() {
        Iterator iterator = this.services.iterator();

        Service service;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            service = (Service) iterator.next();
        } while (service.isRunning());

        return false;
    }

    public ImmutableMultimap<Service.State, Service> servicesByState() {
        return this.state.servicesByState();
    }

    public ImmutableMap<Service, Long> startupTimes() {
        return this.state.startupTimes();
    }

    public String toString() {
        return Objects.toStringHelper(ServiceManager.class).add("services", Collections2.filter(this.services, Predicates.not(Predicates.instanceOf(ServiceManager.NoOpService.class)))).toString();
    }

    private static final class SynchronizedExecutor implements Executor {

        private SynchronizedExecutor() {}

        public synchronized void execute(Runnable runnable) {
            runnable.run();
        }
    }

    private static final class EmptyServiceManagerWarning extends Throwable {

        private EmptyServiceManagerWarning() {}
    }

    private static final class NoOpService extends AbstractService {

        private NoOpService() {}

        @Override
        protected void doStart() {
            this.notifyStarted();
        }

        @Override
        protected void doStop() {
            this.notifyStopped();
        }
    }

    @Immutable
    private static final class ListenerExecutorPair {

        final ServiceManager.Listener listener;
        final Executor executor;

        ListenerExecutorPair(ServiceManager.Listener servicemanager_listener, Executor executor) {
            this.listener = servicemanager_listener;
            this.executor = executor;
        }
    }

    private static final class ServiceListener extends Service.Listener {

        final Service service;
        final WeakReference<ServiceManager.ServiceManagerState> state;

        ServiceListener(Service service, WeakReference<ServiceManager.ServiceManagerState> weakreference) {
            this.service = service;
            this.state = weakreference;
        }

        @Override
        public void starting() {
            ServiceManager.ServiceManagerState servicemanager_servicemanagerstate = (ServiceManager.ServiceManagerState) this.state.get();

            if (servicemanager_servicemanagerstate != null) {
                servicemanager_servicemanagerstate.transitionService(this.service, Service.State.NEW, Service.State.STARTING);
                if (!(this.service instanceof ServiceManager.NoOpService)) {
                    ServiceManager.logger.log(Level.FINE, "Starting {0}.", this.service);
                }
            }

        }

        @Override
        public void running() {
            ServiceManager.ServiceManagerState servicemanager_servicemanagerstate = (ServiceManager.ServiceManagerState) this.state.get();

            if (servicemanager_servicemanagerstate != null) {
                servicemanager_servicemanagerstate.transitionService(this.service, Service.State.STARTING, Service.State.RUNNING);
            }

        }

        @Override
        public void stopping(Service.State service_state) {
            ServiceManager.ServiceManagerState servicemanager_servicemanagerstate = (ServiceManager.ServiceManagerState) this.state.get();

            if (servicemanager_servicemanagerstate != null) {
                servicemanager_servicemanagerstate.transitionService(this.service, service_state, Service.State.STOPPING);
            }

        }

        @Override
        public void terminated(Service.State service_state) {
            ServiceManager.ServiceManagerState servicemanager_servicemanagerstate = (ServiceManager.ServiceManagerState) this.state.get();

            if (servicemanager_servicemanagerstate != null) {
                if (!(this.service instanceof ServiceManager.NoOpService)) {
                    ServiceManager.logger.log(Level.FINE, "Service {0} has terminated. Previous state was: {1}", new Object[]{this.service, service_state});
                }

                servicemanager_servicemanagerstate.transitionService(this.service, service_state, Service.State.TERMINATED);
            }

        }

        @Override
        public void failed(Service.State service_state, Throwable throwable) {
            ServiceManager.ServiceManagerState servicemanager_servicemanagerstate = (ServiceManager.ServiceManagerState) this.state.get();

            if (servicemanager_servicemanagerstate != null) {
                if (!(this.service instanceof ServiceManager.NoOpService)) {
                    ServiceManager.logger.log(Level.SEVERE, "Service " + this.service + " has failed in the " + service_state + " state.", throwable);
                }

                servicemanager_servicemanagerstate.transitionService(this.service, service_state, Service.State.FAILED);
            }

        }
    }

    private static final class ServiceManagerState {

        final Monitor monitor = new Monitor();
        @GuardedBy("monitor")
        final SetMultimap<Service.State, Service> servicesByState = Multimaps.newSetMultimap(new EnumMap(Service.State.class), new Supplier<Set<Service>>() {
            @Override
            public Set<Service> get() {
                return Sets.newLinkedHashSet();
            }
        });
        @GuardedBy("monitor")
        final Multiset<Service.State> states;
        @GuardedBy("monitor")
        final Map<Service, Stopwatch> startupTimers;
        @GuardedBy("monitor")
        boolean ready;
        @GuardedBy("monitor")
        boolean transitioned;
        final int numberOfServices;
        final Monitor.Guard awaitHealthGuard;
        final Monitor.Guard stoppedGuard;
        @GuardedBy("monitor")
        final List<ServiceManager.ListenerExecutorPair> listeners;
        @GuardedBy("monitor")
        final ExecutionQueue queuedListeners;

        ServiceManagerState(ImmutableCollection<Service> immutablecollection) {
            this.states = this.servicesByState.keys();
            this.startupTimers = Maps.newIdentityHashMap();
            this.awaitHealthGuard = new Monitor.Guard(this.monitor) {
                @Override
                public boolean isSatisfied() {
                    return ServiceManagerState.this.states.count(Service.State.RUNNING) == ServiceManagerState.this.numberOfServices || ServiceManagerState.this.states.contains(Service.State.STOPPING) || ServiceManagerState.this.states.contains(Service.State.TERMINATED) || ServiceManagerState.this.states.contains(Service.State.FAILED);
                }
            };
            this.stoppedGuard = new Monitor.Guard(this.monitor) {
                @Override
                public boolean isSatisfied() {
                    return ServiceManagerState.this.states.count(Service.State.TERMINATED) + ServiceManagerState.this.states.count(Service.State.FAILED) == ServiceManagerState.this.numberOfServices;
                }
            };
            this.listeners = Lists.newArrayList();
            this.queuedListeners = new ExecutionQueue();
            this.numberOfServices = immutablecollection.size();
            this.servicesByState.putAll(Service.State.NEW, immutablecollection);
            Iterator iterator = immutablecollection.iterator();

            while (iterator.hasNext()) {
                Service service = (Service) iterator.next();

                this.startupTimers.put(service, Stopwatch.createUnstarted());
            }

        }

        void markReady() {
            this.monitor.enter();

            try {
                if (this.transitioned) {
                    ArrayList arraylist = Lists.newArrayList();
                    Iterator iterator = this.servicesByState().values().iterator();

                    while (iterator.hasNext()) {
                        Service service = (Service) iterator.next();

                        if (service.state() != Service.State.NEW) {
                            arraylist.add(service);
                        }
                    }

                    throw new IllegalArgumentException("Services started transitioning asynchronously before the ServiceManager was constructed: " + arraylist);
                }

                this.ready = true;
            } finally {
                this.monitor.leave();
            }

        }

        void addListener(ServiceManager.Listener servicemanager_listener, Executor executor) {
            Preconditions.checkNotNull(servicemanager_listener, "listener");
            Preconditions.checkNotNull(executor, "executor");
            this.monitor.enter();

            try {
                if (!this.stoppedGuard.isSatisfied()) {
                    this.listeners.add(new ServiceManager.ListenerExecutorPair(servicemanager_listener, executor));
                }
            } finally {
                this.monitor.leave();
            }

        }

        void awaitHealthy() {
            this.monitor.enterWhenUninterruptibly(this.awaitHealthGuard);

            try {
                this.checkHealthy();
            } finally {
                this.monitor.leave();
            }

        }

        void awaitHealthy(long i, TimeUnit timeunit) throws TimeoutException {
            this.monitor.enter();

            try {
                if (!this.monitor.waitForUninterruptibly(this.awaitHealthGuard, i, timeunit)) {
                    throw new TimeoutException("Timeout waiting for the services to become healthy. The following services have not started: " + Multimaps.filterKeys(this.servicesByState, Predicates.in(ImmutableSet.of(Service.State.NEW, Service.State.STARTING))));
                }

                this.checkHealthy();
            } finally {
                this.monitor.leave();
            }

        }

        void awaitStopped() {
            this.monitor.enterWhenUninterruptibly(this.stoppedGuard);
            this.monitor.leave();
        }

        void awaitStopped(long i, TimeUnit timeunit) throws TimeoutException {
            this.monitor.enter();

            try {
                if (!this.monitor.waitForUninterruptibly(this.stoppedGuard, i, timeunit)) {
                    throw new TimeoutException("Timeout waiting for the services to stop. The following services have not stopped: " + Multimaps.filterKeys(this.servicesByState, Predicates.not(Predicates.in(ImmutableSet.of(Service.State.TERMINATED, Service.State.FAILED)))));
                }
            } finally {
                this.monitor.leave();
            }

        }

        ImmutableMultimap<Service.State, Service> servicesByState() {
            ImmutableSetMultimap.Builder immutablesetmultimap_builder = ImmutableSetMultimap.builder();

            this.monitor.enter();

            try {
                Iterator iterator = this.servicesByState.entries().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();

                    if (!(entry.getValue() instanceof ServiceManager.NoOpService)) {
                        immutablesetmultimap_builder.put(entry.getKey(), entry.getValue());
                    }
                }
            } finally {
                this.monitor.leave();
            }

            return immutablesetmultimap_builder.build();
        }

        ImmutableMap<Service, Long> startupTimes() {
            this.monitor.enter();

            ArrayList arraylist;

            try {
                arraylist = Lists.newArrayListWithCapacity(this.states.size() - this.states.count(Service.State.NEW) + this.states.count(Service.State.STARTING));
                Iterator iterator = this.startupTimers.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();
                    Service service = (Service) entry.getKey();
                    Stopwatch stopwatch = (Stopwatch) entry.getValue();

                    if (!stopwatch.isRunning() && !this.servicesByState.containsEntry(Service.State.NEW, service) && !(service instanceof ServiceManager.NoOpService)) {
                        arraylist.add(Maps.immutableEntry(service, stopwatch.elapsed(TimeUnit.MILLISECONDS)));
                    }
                }
            } finally {
                this.monitor.leave();
            }

            Collections.sort(arraylist, Ordering.natural().onResultOf(new Function<Entry<Service, Long>, Long>() {
                public Long apply(Entry<Service, Long> entry1) {
                    return (Long) entry1.getValue();
                }
            }));
            ImmutableMap.Builder immutablemap_builder = ImmutableMap.builder();
            Iterator iterator1 = arraylist.iterator();

            while (iterator1.hasNext()) {
                Entry entry1 = (Entry) iterator1.next();

                immutablemap_builder.put(entry1);
            }

            return immutablemap_builder.build();
        }

        void transitionService(Service service, Service.State service_state, Service.State service_state1) {
            Preconditions.checkNotNull(service);
            Preconditions.checkArgument(service_state != service_state1);
            this.monitor.enter();

            try {
                this.transitioned = true;
                if (!this.ready) {
                    return;
                }

                Preconditions.checkState(this.servicesByState.remove(service_state, service), "Service %s not at the expected location in the state map %s", service, service_state);
                Preconditions.checkState(this.servicesByState.put(service_state1, service), "Service %s in the state map unexpectedly at %s", service, service_state1);
                Stopwatch stopwatch = (Stopwatch) this.startupTimers.get(service);

                if (service_state == Service.State.NEW) {
                    stopwatch.start();
                }

                if (service_state1.compareTo(Service.State.RUNNING) >= 0 && stopwatch.isRunning()) {
                    stopwatch.stop();
                    if (!(service instanceof ServiceManager.NoOpService)) {
                        ServiceManager.logger.log(Level.FINE, "Started {0} in {1}.", new Object[]{service, stopwatch});
                    }
                }

                if (service_state1 == Service.State.FAILED) {
                    this.fireFailedListeners(service);
                }

                if (this.states.count(Service.State.RUNNING) == this.numberOfServices) {
                    this.fireHealthyListeners();
                } else if (this.states.count(Service.State.TERMINATED) + this.states.count(Service.State.FAILED) == this.numberOfServices) {
                    this.fireStoppedListeners();
                    this.listeners.clear();
                }
            } finally {
                this.monitor.leave();
                this.executeListeners();
            }

        }

        @GuardedBy("monitor")
        void fireStoppedListeners() {
            Iterator iterator = this.listeners.iterator();

            while (iterator.hasNext()) {
                final ServiceManager.ListenerExecutorPair servicemanager_listenerexecutorpair = (ServiceManager.ListenerExecutorPair) iterator.next();

                this.queuedListeners.add(new Runnable() {
                    public void run() {
                        servicemanager_listenerexecutorpair.listener.stopped();
                    }
                }, servicemanager_listenerexecutorpair.executor);
            }

        }

        @GuardedBy("monitor")
        void fireHealthyListeners() {
            Iterator iterator = this.listeners.iterator();

            while (iterator.hasNext()) {
                final ServiceManager.ListenerExecutorPair servicemanager_listenerexecutorpair = (ServiceManager.ListenerExecutorPair) iterator.next();

                this.queuedListeners.add(new Runnable() {
                    public void run() {
                        servicemanager_listenerexecutorpair.listener.healthy();
                    }
                }, servicemanager_listenerexecutorpair.executor);
            }

        }

        @GuardedBy("monitor")
        void fireFailedListeners(final Service service) {
            Iterator iterator = this.listeners.iterator();

            while (iterator.hasNext()) {
                final ServiceManager.ListenerExecutorPair servicemanager_listenerexecutorpair = (ServiceManager.ListenerExecutorPair) iterator.next();

                this.queuedListeners.add(new Runnable() {
                    public void run() {
                        servicemanager_listenerexecutorpair.listener.failure(service);
                    }
                }, servicemanager_listenerexecutorpair.executor);
            }

        }

        void executeListeners() {
            Preconditions.checkState(!this.monitor.isOccupiedByCurrentThread(), "It is incorrect to execute listeners with the monitor held.");
            this.queuedListeners.execute();
        }

        @GuardedBy("monitor")
        void checkHealthy() {
            if (this.states.count(Service.State.RUNNING) != this.numberOfServices) {
                throw new IllegalStateException("Expected to be healthy after starting. The following services are not running: " + Multimaps.filterKeys(this.servicesByState, Predicates.not(Predicates.equalTo(Service.State.RUNNING))));
            }
        }
    }

    @Beta
    public abstract static class Listener {

        public Listener() {}

        public void healthy() {}

        public void stopped() {}

        public void failure(Service service) {}
    }
}
