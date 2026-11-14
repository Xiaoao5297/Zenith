package io.netty.channel.nio;

import io.netty.channel.ChannelException;
import io.netty.channel.EventLoopException;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

public final class NioEventLoop extends SingleThreadEventLoop {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioEventLoop.class);
    private static final int CLEANUP_INTERVAL = 256;
    private static final boolean DISABLE_KEYSET_OPTIMIZATION = SystemPropertyUtil.getBoolean("io.netty.noKeySetOptimization", false);
    private static final int MIN_PREMATURE_SELECTOR_RETURNS = 3;
    private static final int SELECTOR_AUTO_REBUILD_THRESHOLD;
    Selector selector;
    private SelectedSelectionKeySet selectedKeys;
    private final SelectorProvider provider;
    private final AtomicBoolean wakenUp = new AtomicBoolean();
    private boolean oldWakenUp;
    private volatile int ioRatio = 50;
    private int cancelledKeys;
    private boolean needsToSelectAgain;

    NioEventLoop(NioEventLoopGroup nioeventloopgroup, ThreadFactory threadfactory, SelectorProvider selectorprovider) {
        super((EventLoopGroup) nioeventloopgroup, threadfactory, false);
        if (selectorprovider == null) {
            throw new NullPointerException("selectorProvider");
        } else {
            this.provider = selectorprovider;
            this.selector = this.openSelector();
        }
    }

    private Selector openSelector() {
        AbstractSelector abstractselector;

        try {
            abstractselector = this.provider.openSelector();
        } catch (IOException ioexception) {
            throw new ChannelException("failed to open a new selector", ioexception);
        }

        if (NioEventLoop.DISABLE_KEYSET_OPTIMIZATION) {
            return abstractselector;
        } else {
            try {
                SelectedSelectionKeySet selectedselectionkeyset = new SelectedSelectionKeySet();
                Class oclass = Class.forName("sun.nio.ch.SelectorImpl", false, ClassLoader.getSystemClassLoader());

                oclass.isAssignableFrom(abstractselector.getClass());
                Field field = oclass.getDeclaredField("selectedKeys");
                Field field1 = oclass.getDeclaredField("publicSelectedKeys");

                field.setAccessible(true);
                field1.setAccessible(true);
                field.set(abstractselector, selectedselectionkeyset);
                field1.set(abstractselector, selectedselectionkeyset);
                this.selectedKeys = selectedselectionkeyset;
                NioEventLoop.logger.trace("Instrumented an optimized java.util.Set into: {}", (Object) abstractselector);
            } catch (Throwable throwable) {
                this.selectedKeys = null;
                NioEventLoop.logger.trace("Failed to instrument an optimized java.util.Set into: {}", abstractselector, throwable);
            }

            return abstractselector;
        }
    }

    @Override
    protected Queue<Runnable> newTaskQueue() {
        return new ConcurrentLinkedQueue();
    }

    public void register(SelectableChannel selectablechannel, int i, NioTask<?> niotask) {
        if (selectablechannel == null) {
            throw new NullPointerException("ch");
        } else if (i == 0) {
            throw new IllegalArgumentException("interestOps must be non-zero.");
        } else if ((i & ~selectablechannel.validOps()) != 0) {
            throw new IllegalArgumentException("invalid interestOps: " + i + "(validOps: " + selectablechannel.validOps() + ')');
        } else if (niotask == null) {
            throw new NullPointerException("task");
        } else if (this.isShutdown()) {
            throw new IllegalStateException("event loop shut down");
        } else {
            try {
                selectablechannel.register(this.selector, i, niotask);
            } catch (Exception exception) {
                throw new EventLoopException("failed to register a channel", exception);
            }
        }
    }

    public int getIoRatio() {
        return this.ioRatio;
    }

    public void setIoRatio(int i) {
        if (i > 0 && i < 100) {
            this.ioRatio = i;
        } else {
            throw new IllegalArgumentException("ioRatio: " + i + " (expected: 0 < ioRatio < 100)");
        }
    }

    public void rebuildSelector() {
        if (!this.inEventLoop()) {
            this.execute(new Runnable() {
                public void run() {
                    NioEventLoop.this.rebuildSelector();
                }
            });
        } else {
            Selector selector = this.selector;

            if (selector != null) {
                Selector selector1;

                try {
                    selector1 = this.openSelector();
                } catch (Exception exception) {
                    NioEventLoop.logger.warn("Failed to create a new Selector.", (Throwable) exception);
                    return;
                }

                int i = 0;

                label62:
                while (true) {
                    try {
                        Iterator iterator = selector.keys().iterator();

                        while (true) {
                            if (!iterator.hasNext()) {
                                break label62;
                            }

                            SelectionKey selectionkey = (SelectionKey) iterator.next();
                            Object object = selectionkey.attachment();

                            try {
                                if (selectionkey.channel().keyFor(selector1) == null) {
                                    int j = selectionkey.interestOps();

                                    selectionkey.cancel();
                                    selectionkey.channel().register(selector1, j, object);
                                    ++i;
                                }
                            } catch (Exception exception1) {
                                NioEventLoop.logger.warn("Failed to re-register a Channel to the new Selector.", (Throwable) exception1);
                                if (object instanceof AbstractNioChannel) {
                                    AbstractNioChannel abstractniochannel = (AbstractNioChannel) object;

                                    abstractniochannel.unsafe().close(abstractniochannel.unsafe().voidPromise());
                                } else {
                                    NioTask niotask = (NioTask) object;

                                    invokeChannelUnregistered(niotask, selectionkey, exception1);
                                }
                            }
                        }
                    } catch (ConcurrentModificationException concurrentmodificationexception) {
                        ;
                    }
                }

                this.selector = selector1;

                try {
                    selector.close();
                } catch (Throwable throwable) {
                    if (NioEventLoop.logger.isWarnEnabled()) {
                        NioEventLoop.logger.warn("Failed to close the old Selector.", throwable);
                    }
                }

                NioEventLoop.logger.info("Migrated " + i + " channel(s) to the new Selector.");
            }
        }
    }

    @Override
    protected void run() {
        while (true) {
            this.oldWakenUp = this.wakenUp.getAndSet(false);

            try {
                if (this.hasTasks()) {
                    this.selectNow();
                } else {
                    this.select();
                    if (this.wakenUp.get()) {
                        this.selector.wakeup();
                    }
                }

                this.cancelledKeys = 0;
                long i = System.nanoTime();

                this.needsToSelectAgain = false;
                if (this.selectedKeys != null) {
                    this.processSelectedKeysOptimized(this.selectedKeys.flip());
                } else {
                    this.processSelectedKeysPlain(this.selector.selectedKeys());
                }

                long j = System.nanoTime() - i;
                int k = this.ioRatio;

                this.runAllTasks(j * (long) (100 - k) / (long) k);
                if (this.isShuttingDown()) {
                    this.closeAll();
                    if (this.confirmShutdown()) {
                        return;
                    }
                }
            } catch (Throwable throwable) {
                NioEventLoop.logger.warn("Unexpected exception in the selector loop.", throwable);

                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException interruptedexception) {
                    ;
                }
            }
        }
    }

    @Override
    protected void cleanup() {
        try {
            this.selector.close();
        } catch (IOException ioexception) {
            NioEventLoop.logger.warn("Failed to close a selector.", (Throwable) ioexception);
        }

    }

    void cancel(SelectionKey selectionkey) {
        selectionkey.cancel();
        ++this.cancelledKeys;
        if (this.cancelledKeys >= 256) {
            this.cancelledKeys = 0;
            this.needsToSelectAgain = true;
        }

    }

    @Override
    protected Runnable pollTask() {
        Runnable runnable = super.pollTask();

        if (this.needsToSelectAgain) {
            this.selectAgain();
        }

        return runnable;
    }

    private void processSelectedKeysPlain(Set<SelectionKey> set) {
        if (!set.isEmpty()) {
            Iterator iterator = set.iterator();

            while (true) {
                SelectionKey selectionkey = (SelectionKey) iterator.next();
                Object object = selectionkey.attachment();

                iterator.remove();
                if (object instanceof AbstractNioChannel) {
                    processSelectedKey(selectionkey, (AbstractNioChannel) object);
                } else {
                    NioTask niotask = (NioTask) object;

                    processSelectedKey(selectionkey, niotask);
                }

                if (!iterator.hasNext()) {
                    break;
                }

                if (this.needsToSelectAgain) {
                    this.selectAgain();
                    set = this.selector.selectedKeys();
                    if (set.isEmpty()) {
                        break;
                    }

                    iterator = set.iterator();
                }
            }

        }
    }

    private void processSelectedKeysOptimized(SelectionKey[] aselectionkey) {
        int i = 0;

        while (true) {
            SelectionKey selectionkey = aselectionkey[i];

            if (selectionkey == null) {
                return;
            }

            Object object = selectionkey.attachment();

            if (object instanceof AbstractNioChannel) {
                processSelectedKey(selectionkey, (AbstractNioChannel) object);
            } else {
                NioTask niotask = (NioTask) object;

                processSelectedKey(selectionkey, niotask);
            }

            if (this.needsToSelectAgain) {
                this.selectAgain();
                aselectionkey = this.selectedKeys.flip();
                i = -1;
            }

            ++i;
        }
    }

    private static void processSelectedKey(SelectionKey selectionkey, AbstractNioChannel abstractniochannel) {
        AbstractNioChannel.NioUnsafe abstractniochannel_niounsafe = abstractniochannel.unsafe();

        if (!selectionkey.isValid()) {
            abstractniochannel_niounsafe.close(abstractniochannel_niounsafe.voidPromise());
        } else {
            try {
                int i = selectionkey.readyOps();

                if ((i & 17) != 0 || i == 0) {
                    abstractniochannel_niounsafe.read();
                    if (!abstractniochannel.isOpen()) {
                        return;
                    }
                }

                if ((i & 4) != 0) {
                    abstractniochannel.unsafe().forceFlush();
                }

                if ((i & 8) != 0) {
                    int j = selectionkey.interestOps();

                    j &= -9;
                    selectionkey.interestOps(j);
                    abstractniochannel_niounsafe.finishConnect();
                }
            } catch (CancelledKeyException cancelledkeyexception) {
                abstractniochannel_niounsafe.close(abstractniochannel_niounsafe.voidPromise());
            }

        }
    }

    private static void processSelectedKey(SelectionKey selectionkey, NioTask<SelectableChannel> niotask) {
        byte b0 = 0;
        boolean flag = false;

        label91:
        {
            try {
                flag = true;
                niotask.channelReady(selectionkey.channel(), selectionkey);
                b0 = 1;
                flag = false;
                break label91;
            } catch (Exception exception) {
                selectionkey.cancel();
                invokeChannelUnregistered(niotask, selectionkey, exception);
                b0 = 2;
                flag = false;
            } finally {
                if (flag) {
                    switch (b0) {
                        case 0:
                            selectionkey.cancel();
                            invokeChannelUnregistered(niotask, selectionkey, (Throwable) null);
                            break;
                        case 1:
                            if (!selectionkey.isValid()) {
                                invokeChannelUnregistered(niotask, selectionkey, (Throwable) null);
                            }
                    }

                }
            }

            switch (b0) {
                case 0:
                    selectionkey.cancel();
                    invokeChannelUnregistered(niotask, selectionkey, (Throwable) null);
                    return;
                case 1:
                    if (!selectionkey.isValid()) {
                        invokeChannelUnregistered(niotask, selectionkey, (Throwable) null);
                    }

                    return;
                default:
                    return;
            }
        }

        switch (b0) {
            case 0:
                selectionkey.cancel();
                invokeChannelUnregistered(niotask, selectionkey, (Throwable) null);
                break;
            case 1:
                if (!selectionkey.isValid()) {
                    invokeChannelUnregistered(niotask, selectionkey, (Throwable) null);
                }
        }

    }

    private void closeAll() {
        this.selectAgain();
        Set set = this.selector.keys();
        ArrayList arraylist = new ArrayList(set.size());
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            SelectionKey selectionkey = (SelectionKey) iterator.next();
            Object object = selectionkey.attachment();

            if (object instanceof AbstractNioChannel) {
                arraylist.add((AbstractNioChannel) object);
            } else {
                selectionkey.cancel();
                NioTask niotask = (NioTask) object;

                invokeChannelUnregistered(niotask, selectionkey, (Throwable) null);
            }
        }

        iterator = arraylist.iterator();

        while (iterator.hasNext()) {
            AbstractNioChannel abstractniochannel = (AbstractNioChannel) iterator.next();

            abstractniochannel.unsafe().close(abstractniochannel.unsafe().voidPromise());
        }

    }

    private static void invokeChannelUnregistered(NioTask<SelectableChannel> niotask, SelectionKey selectionkey, Throwable throwable) {
        try {
            niotask.channelUnregistered(selectionkey.channel(), throwable);
        } catch (Exception exception) {
            NioEventLoop.logger.warn("Unexpected exception while running NioTask.channelUnregistered()", (Throwable) exception);
        }

    }

    @Override
    protected void wakeup(boolean flag) {
        if (!flag && this.wakenUp.compareAndSet(false, true)) {
            this.selector.wakeup();
        }

    }

    void selectNow() throws IOException {
        try {
            this.selector.selectNow();
        } finally {
            if (this.wakenUp.get()) {
                this.selector.wakeup();
            }

        }

    }

    private void select() throws IOException {
        Selector selector = this.selector;

        try {
            int i = 0;
            long j = System.nanoTime();
            long k = j + this.delayNanos(j);

            while (true) {
                long l = (k - j + 500000L) / 1000000L;

                if (l <= 0L) {
                    if (i == 0) {
                        selector.selectNow();
                        i = 1;
                    }
                    break;
                }

                int i1 = selector.select(l);

                ++i;
                if (i1 != 0 || this.oldWakenUp || this.wakenUp.get() || this.hasTasks()) {
                    break;
                }

                if (NioEventLoop.SELECTOR_AUTO_REBUILD_THRESHOLD > 0 && i >= NioEventLoop.SELECTOR_AUTO_REBUILD_THRESHOLD) {
                    NioEventLoop.logger.warn("Selector.select() returned prematurely {} times in a row; rebuilding selector.", (Object) i);
                    this.rebuildSelector();
                    selector = this.selector;
                    selector.selectNow();
                    i = 1;
                    break;
                }

                j = System.nanoTime();
            }

            if (i > 3 && NioEventLoop.logger.isDebugEnabled()) {
                NioEventLoop.logger.debug("Selector.select() returned prematurely {} times in a row.", (Object) (i - 1));
            }
        } catch (CancelledKeyException cancelledkeyexception) {
            if (NioEventLoop.logger.isDebugEnabled()) {
                NioEventLoop.logger.debug(CancelledKeyException.class.getSimpleName() + " raised by a Selector - JDK bug?", (Throwable) cancelledkeyexception);
            }
        }

    }

    private void selectAgain() {
        this.needsToSelectAgain = false;

        try {
            this.selector.selectNow();
        } catch (Throwable throwable) {
            NioEventLoop.logger.warn("Failed to update SelectionKeys.", throwable);
        }

    }

    static {
        String s = "sun.nio.ch.bugLevel";

        try {
            String s1 = System.getProperty(s);

            if (s1 == null) {
                System.setProperty(s, "");
            }
        } catch (SecurityException securityexception) {
            if (NioEventLoop.logger.isDebugEnabled()) {
                NioEventLoop.logger.debug("Unable to get/set System Property: {}", s, securityexception);
            }
        }

        int i = SystemPropertyUtil.getInt("io.netty.selectorAutoRebuildThreshold", 512);

        if (i < 3) {
            i = 0;
        }

        SELECTOR_AUTO_REBUILD_THRESHOLD = i;
        if (NioEventLoop.logger.isDebugEnabled()) {
            NioEventLoop.logger.debug("-Dio.netty.noKeySetOptimization: {}", (Object) NioEventLoop.DISABLE_KEYSET_OPTIMIZATION);
            NioEventLoop.logger.debug("-Dio.netty.selectorAutoRebuildThreshold: {}", (Object) NioEventLoop.SELECTOR_AUTO_REBUILD_THRESHOLD);
        }

    }
}
