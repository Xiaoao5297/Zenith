package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@Beta
@ThreadSafe
public class CycleDetectingLockFactory {

    private static final ConcurrentMap<Class<? extends Enum>, Map<? extends Enum, CycleDetectingLockFactory.LockGraphNode>> lockGraphNodesPerType = (new MapMaker()).weakKeys().makeMap();
    private static final Logger logger = Logger.getLogger(CycleDetectingLockFactory.class.getName());
    final CycleDetectingLockFactory.Policy policy;
    private static final ThreadLocal<ArrayList<CycleDetectingLockFactory.LockGraphNode>> acquiredLocks = new ThreadLocal<ArrayList<CycleDetectingLockFactory.LockGraphNode>>() {
        protected ArrayList<CycleDetectingLockFactory.LockGraphNode> initialValue() {
            return Lists.newArrayListWithCapacity(3);
        }
    };

    public static CycleDetectingLockFactory newInstance(CycleDetectingLockFactory.Policy cycledetectinglockfactory_policy) {
        return new CycleDetectingLockFactory(cycledetectinglockfactory_policy);
    }

    public ReentrantLock newReentrantLock(String s) {
        return this.newReentrantLock(s, false);
    }

    public ReentrantLock newReentrantLock(String s, boolean flag) {
        return (ReentrantLock) (this.policy == CycleDetectingLockFactory.Policies.DISABLED ? new ReentrantLock(flag) : new CycleDetectingLockFactory.CycleDetectingReentrantLock(new CycleDetectingLockFactory.LockGraphNode(s), flag));
    }

    public ReentrantReadWriteLock newReentrantReadWriteLock(String s) {
        return this.newReentrantReadWriteLock(s, false);
    }

    public ReentrantReadWriteLock newReentrantReadWriteLock(String s, boolean flag) {
        return (ReentrantReadWriteLock) (this.policy == CycleDetectingLockFactory.Policies.DISABLED ? new ReentrantReadWriteLock(flag) : new CycleDetectingLockFactory.CycleDetectingReentrantReadWriteLock(new CycleDetectingLockFactory.LockGraphNode(s), flag));
    }

    public static <E extends Enum<E>> CycleDetectingLockFactory.WithExplicitOrdering<E> newInstanceWithExplicitOrdering(Class<E> oclass, CycleDetectingLockFactory.Policy cycledetectinglockfactory_policy) {
        Preconditions.checkNotNull(oclass);
        Preconditions.checkNotNull(cycledetectinglockfactory_policy);
        Map map = getOrCreateNodes(oclass);

        return new CycleDetectingLockFactory.WithExplicitOrdering<>(cycledetectinglockfactory_policy, map);
    }

    private static Map<? extends Enum, CycleDetectingLockFactory.LockGraphNode> getOrCreateNodes(Class<? extends Enum> oclass) {
        Map map = (Map) CycleDetectingLockFactory.lockGraphNodesPerType.get(oclass);

        if (map != null) {
            return map;
        } else {
            Map map1 = createNodes(oclass);

            map = (Map) CycleDetectingLockFactory.lockGraphNodesPerType.putIfAbsent(oclass, map1);
            return (Map) Objects.firstNonNull(map, map1);
        }
    }

    @VisibleForTesting
    static <E extends Enum<E>> Map<E, CycleDetectingLockFactory.LockGraphNode> createNodes(Class<E> oclass) {
        EnumMap enummap = Maps.newEnumMap(oclass);
        Enum[] aenum = (Enum[]) oclass.getEnumConstants();
        int i = aenum.length;
        ArrayList arraylist = Lists.newArrayListWithCapacity(i);
        Enum[] aenum1 = aenum;
        int j = aenum.length;

        for (int k = 0; k < j; ++k) {
            Enum oenum = aenum1[k];
            CycleDetectingLockFactory.LockGraphNode cycledetectinglockfactory_lockgraphnode = new CycleDetectingLockFactory.LockGraphNode(getLockName(oenum));

            arraylist.add(cycledetectinglockfactory_lockgraphnode);
            enummap.put(oenum, cycledetectinglockfactory_lockgraphnode);
        }

        int l;

        for (l = 1; l < i; ++l) {
            ((CycleDetectingLockFactory.LockGraphNode) arraylist.get(l)).checkAcquiredLocks(CycleDetectingLockFactory.Policies.THROW, arraylist.subList(0, l));
        }

        for (l = 0; l < i - 1; ++l) {
            ((CycleDetectingLockFactory.LockGraphNode) arraylist.get(l)).checkAcquiredLocks(CycleDetectingLockFactory.Policies.DISABLED, arraylist.subList(l + 1, i));
        }

        return Collections.unmodifiableMap(enummap);
    }

    private static String getLockName(Enum<?> oenum) {
        return oenum.getDeclaringClass().getSimpleName() + "." + oenum.name();
    }

    private CycleDetectingLockFactory(CycleDetectingLockFactory.Policy cycledetectinglockfactory_policy) {
        this.policy = (CycleDetectingLockFactory.Policy) Preconditions.checkNotNull(cycledetectinglockfactory_policy);
    }

    private void aboutToAcquire(CycleDetectingLockFactory.CycleDetectingLock cycledetectinglockfactory_cycledetectinglock) {
        if (!cycledetectinglockfactory_cycledetectinglock.isAcquiredByCurrentThread()) {
            ArrayList arraylist = (ArrayList) CycleDetectingLockFactory.acquiredLocks.get();
            CycleDetectingLockFactory.LockGraphNode cycledetectinglockfactory_lockgraphnode = cycledetectinglockfactory_cycledetectinglock.getLockGraphNode();

            cycledetectinglockfactory_lockgraphnode.checkAcquiredLocks(this.policy, arraylist);
            arraylist.add(cycledetectinglockfactory_lockgraphnode);
        }

    }

    private void lockStateChanged(CycleDetectingLockFactory.CycleDetectingLock cycledetectinglockfactory_cycledetectinglock) {
        if (!cycledetectinglockfactory_cycledetectinglock.isAcquiredByCurrentThread()) {
            ArrayList arraylist = (ArrayList) CycleDetectingLockFactory.acquiredLocks.get();
            CycleDetectingLockFactory.LockGraphNode cycledetectinglockfactory_lockgraphnode = cycledetectinglockfactory_cycledetectinglock.getLockGraphNode();

            for (int i = arraylist.size() - 1; i >= 0; --i) {
                if (arraylist.get(i) == cycledetectinglockfactory_lockgraphnode) {
                    arraylist.remove(i);
                    break;
                }
            }
        }

    }

    private class CycleDetectingReentrantWriteLock extends WriteLock {

        final CycleDetectingLockFactory.CycleDetectingReentrantReadWriteLock readWriteLock;

        CycleDetectingReentrantWriteLock(CycleDetectingLockFactory.CycleDetectingReentrantReadWriteLock cycledetectinglockfactory_cycledetectingreentrantreadwritelock) {
            super(cycledetectinglockfactory_cycledetectingreentrantreadwritelock);
            this.readWriteLock = cycledetectinglockfactory_cycledetectingreentrantreadwritelock;
        }

        public void lock() {
            CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);

            try {
                super.lock();
            } finally {
                CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
            }

        }

        public void lockInterruptibly() throws InterruptedException {
            CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);

            try {
                super.lockInterruptibly();
            } finally {
                CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
            }

        }

        public boolean tryLock() {
            CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);

            boolean flag;

            try {
                flag = super.tryLock();
            } finally {
                CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
            }

            return flag;
        }

        public boolean tryLock(long i, TimeUnit timeunit) throws InterruptedException {
            CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);

            boolean flag;

            try {
                flag = super.tryLock(i, timeunit);
            } finally {
                CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
            }

            return flag;
        }

        public void unlock() {
            try {
                super.unlock();
            } finally {
                CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
            }

        }
    }

    private class CycleDetectingReentrantReadLock extends ReadLock {

        final CycleDetectingLockFactory.CycleDetectingReentrantReadWriteLock readWriteLock;

        CycleDetectingReentrantReadLock(CycleDetectingLockFactory.CycleDetectingReentrantReadWriteLock cycledetectinglockfactory_cycledetectingreentrantreadwritelock) {
            super(cycledetectinglockfactory_cycledetectingreentrantreadwritelock);
            this.readWriteLock = cycledetectinglockfactory_cycledetectingreentrantreadwritelock;
        }

        public void lock() {
            CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);

            try {
                super.lock();
            } finally {
                CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
            }

        }

        public void lockInterruptibly() throws InterruptedException {
            CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);

            try {
                super.lockInterruptibly();
            } finally {
                CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
            }

        }

        public boolean tryLock() {
            CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);

            boolean flag;

            try {
                flag = super.tryLock();
            } finally {
                CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
            }

            return flag;
        }

        public boolean tryLock(long i, TimeUnit timeunit) throws InterruptedException {
            CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);

            boolean flag;

            try {
                flag = super.tryLock(i, timeunit);
            } finally {
                CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
            }

            return flag;
        }

        public void unlock() {
            try {
                super.unlock();
            } finally {
                CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
            }

        }
    }

    final class CycleDetectingReentrantReadWriteLock extends ReentrantReadWriteLock implements CycleDetectingLockFactory.CycleDetectingLock {

        private final CycleDetectingLockFactory.CycleDetectingReentrantReadLock readLock;
        private final CycleDetectingLockFactory.CycleDetectingReentrantWriteLock writeLock;
        private final CycleDetectingLockFactory.LockGraphNode lockGraphNode;

        private CycleDetectingReentrantReadWriteLock(CycleDetectingLockFactory.LockGraphNode cycledetectinglockfactory_lockgraphnode, boolean flag) {
            super(flag);
            this.readLock = CycleDetectingLockFactory.this.new CycleDetectingReentrantReadLock(this);
            this.writeLock = CycleDetectingLockFactory.this.new CycleDetectingReentrantWriteLock(this);
            this.lockGraphNode = (CycleDetectingLockFactory.LockGraphNode) Preconditions.checkNotNull(cycledetectinglockfactory_lockgraphnode);
        }

        public ReadLock readLock() {
            return this.readLock;
        }

        public WriteLock writeLock() {
            return this.writeLock;
        }

        @Override
        public CycleDetectingLockFactory.LockGraphNode getLockGraphNode() {
            return this.lockGraphNode;
        }

        @Override
        public boolean isAcquiredByCurrentThread() {
            return this.isWriteLockedByCurrentThread() || this.getReadHoldCount() > 0;
        }
    }

    final class CycleDetectingReentrantLock extends ReentrantLock implements CycleDetectingLockFactory.CycleDetectingLock {

        private final CycleDetectingLockFactory.LockGraphNode lockGraphNode;

        private CycleDetectingReentrantLock(CycleDetectingLockFactory.LockGraphNode cycledetectinglockfactory_lockgraphnode, boolean flag) {
            super(flag);
            this.lockGraphNode = (CycleDetectingLockFactory.LockGraphNode) Preconditions.checkNotNull(cycledetectinglockfactory_lockgraphnode);
        }

        @Override
        public CycleDetectingLockFactory.LockGraphNode getLockGraphNode() {
            return this.lockGraphNode;
        }

        @Override
        public boolean isAcquiredByCurrentThread() {
            return this.isHeldByCurrentThread();
        }

        public void lock() {
            CycleDetectingLockFactory.this.aboutToAcquire(this);

            try {
                super.lock();
            } finally {
                CycleDetectingLockFactory.this.lockStateChanged(this);
            }

        }

        public void lockInterruptibly() throws InterruptedException {
            CycleDetectingLockFactory.this.aboutToAcquire(this);

            try {
                super.lockInterruptibly();
            } finally {
                CycleDetectingLockFactory.this.lockStateChanged(this);
            }

        }

        public boolean tryLock() {
            CycleDetectingLockFactory.this.aboutToAcquire(this);

            boolean flag;

            try {
                flag = super.tryLock();
            } finally {
                CycleDetectingLockFactory.this.lockStateChanged(this);
            }

            return flag;
        }

        public boolean tryLock(long i, TimeUnit timeunit) throws InterruptedException {
            CycleDetectingLockFactory.this.aboutToAcquire(this);

            boolean flag;

            try {
                flag = super.tryLock(i, timeunit);
            } finally {
                CycleDetectingLockFactory.this.lockStateChanged(this);
            }

            return flag;
        }

        public void unlock() {
            try {
                super.unlock();
            } finally {
                CycleDetectingLockFactory.this.lockStateChanged(this);
            }

        }
    }

    private static class LockGraphNode {

        final Map<CycleDetectingLockFactory.LockGraphNode, CycleDetectingLockFactory.ExampleStackTrace> allowedPriorLocks = (new MapMaker()).weakKeys().makeMap();
        final Map<CycleDetectingLockFactory.LockGraphNode, CycleDetectingLockFactory.PotentialDeadlockException> disallowedPriorLocks = (new MapMaker()).weakKeys().makeMap();
        final String lockName;

        LockGraphNode(String s) {
            this.lockName = (String) Preconditions.checkNotNull(s);
        }

        String getLockName() {
            return this.lockName;
        }

        void checkAcquiredLocks(CycleDetectingLockFactory.Policy cycledetectinglockfactory_policy, List<CycleDetectingLockFactory.LockGraphNode> list) {
            int i = 0;

            for (int j = list.size(); i < j; ++i) {
                this.checkAcquiredLock(cycledetectinglockfactory_policy, (CycleDetectingLockFactory.LockGraphNode) list.get(i));
            }

        }

        void checkAcquiredLock(CycleDetectingLockFactory.Policy cycledetectinglockfactory_policy, CycleDetectingLockFactory.LockGraphNode cycledetectinglockfactory_lockgraphnode) {
            Preconditions.checkState(this != cycledetectinglockfactory_lockgraphnode, "Attempted to acquire multiple locks with the same rank " + cycledetectinglockfactory_lockgraphnode.getLockName());
            if (!this.allowedPriorLocks.containsKey(cycledetectinglockfactory_lockgraphnode)) {
                CycleDetectingLockFactory.PotentialDeadlockException cycledetectinglockfactory_potentialdeadlockexception = (CycleDetectingLockFactory.PotentialDeadlockException) this.disallowedPriorLocks.get(cycledetectinglockfactory_lockgraphnode);

                if (cycledetectinglockfactory_potentialdeadlockexception != null) {
                    CycleDetectingLockFactory.PotentialDeadlockException cycledetectinglockfactory_potentialdeadlockexception1 = new CycleDetectingLockFactory.PotentialDeadlockException(cycledetectinglockfactory_lockgraphnode, this, cycledetectinglockfactory_potentialdeadlockexception.getConflictingStackTrace());

                    cycledetectinglockfactory_policy.handlePotentialDeadlock(cycledetectinglockfactory_potentialdeadlockexception1);
                } else {
                    Set set = Sets.newIdentityHashSet();
                    CycleDetectingLockFactory.ExampleStackTrace cycledetectinglockfactory_examplestacktrace = cycledetectinglockfactory_lockgraphnode.findPathTo(this, set);

                    if (cycledetectinglockfactory_examplestacktrace == null) {
                        this.allowedPriorLocks.put(cycledetectinglockfactory_lockgraphnode, new CycleDetectingLockFactory.ExampleStackTrace(cycledetectinglockfactory_lockgraphnode, this));
                    } else {
                        CycleDetectingLockFactory.PotentialDeadlockException cycledetectinglockfactory_potentialdeadlockexception2 = new CycleDetectingLockFactory.PotentialDeadlockException(cycledetectinglockfactory_lockgraphnode, this, cycledetectinglockfactory_examplestacktrace);

                        this.disallowedPriorLocks.put(cycledetectinglockfactory_lockgraphnode, cycledetectinglockfactory_potentialdeadlockexception2);
                        cycledetectinglockfactory_policy.handlePotentialDeadlock(cycledetectinglockfactory_potentialdeadlockexception2);
                    }

                }
            }
        }

        @Nullable
        private CycleDetectingLockFactory.ExampleStackTrace findPathTo(CycleDetectingLockFactory.LockGraphNode cycledetectinglockfactory_lockgraphnode, Set<CycleDetectingLockFactory.LockGraphNode> set) {
            if (!set.add(this)) {
                return null;
            } else {
                CycleDetectingLockFactory.ExampleStackTrace cycledetectinglockfactory_examplestacktrace = (CycleDetectingLockFactory.ExampleStackTrace) this.allowedPriorLocks.get(cycledetectinglockfactory_lockgraphnode);

                if (cycledetectinglockfactory_examplestacktrace != null) {
                    return cycledetectinglockfactory_examplestacktrace;
                } else {
                    Iterator iterator = this.allowedPriorLocks.entrySet().iterator();

                    Entry entry;
                    CycleDetectingLockFactory.LockGraphNode cycledetectinglockfactory_lockgraphnode1;

                    do {
                        if (!iterator.hasNext()) {
                            return null;
                        }

                        entry = (Entry) iterator.next();
                        cycledetectinglockfactory_lockgraphnode1 = (CycleDetectingLockFactory.LockGraphNode) entry.getKey();
                        cycledetectinglockfactory_examplestacktrace = cycledetectinglockfactory_lockgraphnode1.findPathTo(cycledetectinglockfactory_lockgraphnode, set);
                    } while (cycledetectinglockfactory_examplestacktrace == null);

                    CycleDetectingLockFactory.ExampleStackTrace cycledetectinglockfactory_examplestacktrace1 = new CycleDetectingLockFactory.ExampleStackTrace(cycledetectinglockfactory_lockgraphnode1, this);

                    cycledetectinglockfactory_examplestacktrace1.setStackTrace(((CycleDetectingLockFactory.ExampleStackTrace) entry.getValue()).getStackTrace());
                    cycledetectinglockfactory_examplestacktrace1.initCause(cycledetectinglockfactory_examplestacktrace);
                    return cycledetectinglockfactory_examplestacktrace1;
                }
            }
        }
    }

    private interface CycleDetectingLock {

        CycleDetectingLockFactory.LockGraphNode getLockGraphNode();

        boolean isAcquiredByCurrentThread();
    }

    @Beta
    public static final class PotentialDeadlockException extends CycleDetectingLockFactory.ExampleStackTrace {

        private final CycleDetectingLockFactory.ExampleStackTrace conflictingStackTrace;

        private PotentialDeadlockException(CycleDetectingLockFactory.LockGraphNode cycledetectinglockfactory_lockgraphnode, CycleDetectingLockFactory.LockGraphNode cycledetectinglockfactory_lockgraphnode1, CycleDetectingLockFactory.ExampleStackTrace cycledetectinglockfactory_examplestacktrace) {
            super(cycledetectinglockfactory_lockgraphnode, cycledetectinglockfactory_lockgraphnode1);
            this.conflictingStackTrace = cycledetectinglockfactory_examplestacktrace;
            this.initCause(cycledetectinglockfactory_examplestacktrace);
        }

        public CycleDetectingLockFactory.ExampleStackTrace getConflictingStackTrace() {
            return this.conflictingStackTrace;
        }

        public String getMessage() {
            StringBuilder stringbuilder = new StringBuilder(super.getMessage());

            for (Object object = this.conflictingStackTrace; object != null; object = ((Throwable) object).getCause()) {
                stringbuilder.append(", ").append(((Throwable) object).getMessage());
            }

            return stringbuilder.toString();
        }
    }

    private static class ExampleStackTrace extends IllegalStateException {

        static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];
        static Set<String> EXCLUDED_CLASS_NAMES = ImmutableSet.of(CycleDetectingLockFactory.class.getName(), CycleDetectingLockFactory.ExampleStackTrace.class.getName(), CycleDetectingLockFactory.LockGraphNode.class.getName());

        ExampleStackTrace(CycleDetectingLockFactory.LockGraphNode cycledetectinglockfactory_lockgraphnode, CycleDetectingLockFactory.LockGraphNode cycledetectinglockfactory_lockgraphnode1) {
            super(cycledetectinglockfactory_lockgraphnode.getLockName() + " -> " + cycledetectinglockfactory_lockgraphnode1.getLockName());
            StackTraceElement[] astacktraceelement = this.getStackTrace();
            int i = 0;

            for (int j = astacktraceelement.length; i < j; ++i) {
                if (CycleDetectingLockFactory.WithExplicitOrdering.class.getName().equals(astacktraceelement[i].getClassName())) {
                    this.setStackTrace(CycleDetectingLockFactory.ExampleStackTrace.EMPTY_STACK_TRACE);
                    break;
                }

                if (!CycleDetectingLockFactory.ExampleStackTrace.EXCLUDED_CLASS_NAMES.contains(astacktraceelement[i].getClassName())) {
                    this.setStackTrace((StackTraceElement[]) Arrays.copyOfRange(astacktraceelement, i, j));
                    break;
                }
            }

        }
    }

    @Beta
    public static final class WithExplicitOrdering<E extends Enum<E>> extends CycleDetectingLockFactory {

        private final Map<E, CycleDetectingLockFactory.LockGraphNode> lockGraphNodes;

        @VisibleForTesting
        WithExplicitOrdering(CycleDetectingLockFactory.Policy cycledetectinglockfactory_policy, Map<E, CycleDetectingLockFactory.LockGraphNode> map) {
            super(cycledetectinglockfactory_policy, null);
            this.lockGraphNodes = map;
        }

        public ReentrantLock newReentrantLock(E e0) {
            return this.newReentrantLock(e0, false);
        }

        public ReentrantLock newReentrantLock(E e0, boolean flag) {
            return (ReentrantLock) (this.policy == CycleDetectingLockFactory.Policies.DISABLED ? new ReentrantLock(flag) : new CycleDetectingLockFactory.CycleDetectingReentrantLock((CycleDetectingLockFactory.LockGraphNode) this.lockGraphNodes.get(e0), flag));
        }

        public ReentrantReadWriteLock newReentrantReadWriteLock(E e0) {
            return this.newReentrantReadWriteLock(e0, false);
        }

        public ReentrantReadWriteLock newReentrantReadWriteLock(E e0, boolean flag) {
            return (ReentrantReadWriteLock) (this.policy == CycleDetectingLockFactory.Policies.DISABLED ? new ReentrantReadWriteLock(flag) : new CycleDetectingLockFactory.CycleDetectingReentrantReadWriteLock((CycleDetectingLockFactory.LockGraphNode) this.lockGraphNodes.get(e0), flag));
        }
    }

    @Beta
    public static enum Policies implements CycleDetectingLockFactory.Policy {

        THROW {
            @Override
            public void handlePotentialDeadlock(CycleDetectingLockFactory.PotentialDeadlockException cycledetectinglockfactory_potentialdeadlockexception) {
                throw cycledetectinglockfactory_potentialdeadlockexception;
            }
        },
        WARN {
            @Override
            public void handlePotentialDeadlock(CycleDetectingLockFactory.PotentialDeadlockException cycledetectinglockfactory_potentialdeadlockexception) {
                CycleDetectingLockFactory.logger.log(Level.SEVERE, "Detected potential deadlock", cycledetectinglockfactory_potentialdeadlockexception);
            }
        },
        DISABLED {
            @Override
            public void handlePotentialDeadlock(CycleDetectingLockFactory.PotentialDeadlockException cycledetectinglockfactory_potentialdeadlockexception) {}
        };

        private Policies() {}
    }

    @Beta
    @ThreadSafe
    public interface Policy {

        void handlePotentialDeadlock(CycleDetectingLockFactory.PotentialDeadlockException cycledetectinglockfactory_potentialdeadlockexception);
    }
}
