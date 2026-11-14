package io.netty.channel;

import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutionException;

final class DefaultChannelPipeline implements ChannelPipeline {

    static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultChannelPipeline.class);
    private static final WeakHashMap<Class<?>, String>[] nameCaches = new WeakHashMap[Runtime.getRuntime().availableProcessors()];
    final AbstractChannel channel;
    final DefaultChannelHandlerContext head;
    final DefaultChannelHandlerContext tail;
    private final Map<String, DefaultChannelHandlerContext> name2ctx = new HashMap(4);
    final Map<EventExecutorGroup, EventExecutor> childExecutors = new IdentityHashMap();

    public DefaultChannelPipeline(AbstractChannel abstractchannel) {
        if (abstractchannel == null) {
            throw new NullPointerException("channel");
        } else {
            this.channel = abstractchannel;
            DefaultChannelPipeline.TailHandler defaultchannelpipeline_tailhandler = new DefaultChannelPipeline.TailHandler();

            this.tail = new DefaultChannelHandlerContext(this, (EventExecutorGroup) null, this.generateName(defaultchannelpipeline_tailhandler), defaultchannelpipeline_tailhandler);
            DefaultChannelPipeline.HeadHandler defaultchannelpipeline_headhandler = new DefaultChannelPipeline.HeadHandler(abstractchannel.unsafe());

            this.head = new DefaultChannelHandlerContext(this, (EventExecutorGroup) null, this.generateName(defaultchannelpipeline_headhandler), defaultchannelpipeline_headhandler);
            this.head.next = this.tail;
            this.tail.prev = this.head;
        }
    }

    @Override
    public Channel channel() {
        return this.channel;
    }

    @Override
    public ChannelPipeline addFirst(String s, ChannelHandler channelhandler) {
        return this.addFirst((EventExecutorGroup) null, s, channelhandler);
    }

    @Override
    public ChannelPipeline addFirst(EventExecutorGroup eventexecutorgroup, String s, ChannelHandler channelhandler) {
        synchronized (this) {
            this.checkDuplicateName(s);
            DefaultChannelHandlerContext defaultchannelhandlercontext = new DefaultChannelHandlerContext(this, eventexecutorgroup, s, channelhandler);

            this.addFirst0(s, defaultchannelhandlercontext);
            return this;
        }
    }

    private void addFirst0(String s, DefaultChannelHandlerContext defaultchannelhandlercontext) {
        checkMultiplicity(defaultchannelhandlercontext);
        DefaultChannelHandlerContext defaultchannelhandlercontext1 = this.head.next;

        defaultchannelhandlercontext.prev = this.head;
        defaultchannelhandlercontext.next = defaultchannelhandlercontext1;
        this.head.next = defaultchannelhandlercontext;
        defaultchannelhandlercontext1.prev = defaultchannelhandlercontext;
        this.name2ctx.put(s, defaultchannelhandlercontext);
        this.callHandlerAdded(defaultchannelhandlercontext);
    }

    @Override
    public ChannelPipeline addLast(String s, ChannelHandler channelhandler) {
        return this.addLast((EventExecutorGroup) null, s, channelhandler);
    }

    @Override
    public ChannelPipeline addLast(EventExecutorGroup eventexecutorgroup, String s, ChannelHandler channelhandler) {
        synchronized (this) {
            this.checkDuplicateName(s);
            DefaultChannelHandlerContext defaultchannelhandlercontext = new DefaultChannelHandlerContext(this, eventexecutorgroup, s, channelhandler);

            this.addLast0(s, defaultchannelhandlercontext);
            return this;
        }
    }

    private void addLast0(String s, DefaultChannelHandlerContext defaultchannelhandlercontext) {
        checkMultiplicity(defaultchannelhandlercontext);
        DefaultChannelHandlerContext defaultchannelhandlercontext1 = this.tail.prev;

        defaultchannelhandlercontext.prev = defaultchannelhandlercontext1;
        defaultchannelhandlercontext.next = this.tail;
        defaultchannelhandlercontext1.next = defaultchannelhandlercontext;
        this.tail.prev = defaultchannelhandlercontext;
        this.name2ctx.put(s, defaultchannelhandlercontext);
        this.callHandlerAdded(defaultchannelhandlercontext);
    }

    @Override
    public ChannelPipeline addBefore(String s, String s1, ChannelHandler channelhandler) {
        return this.addBefore((EventExecutorGroup) null, s, s1, channelhandler);
    }

    @Override
    public ChannelPipeline addBefore(EventExecutorGroup eventexecutorgroup, String s, String s1, ChannelHandler channelhandler) {
        synchronized (this) {
            DefaultChannelHandlerContext defaultchannelhandlercontext = this.getContextOrDie(s);

            this.checkDuplicateName(s1);
            DefaultChannelHandlerContext defaultchannelhandlercontext1 = new DefaultChannelHandlerContext(this, eventexecutorgroup, s1, channelhandler);

            this.addBefore0(s1, defaultchannelhandlercontext, defaultchannelhandlercontext1);
            return this;
        }
    }

    private void addBefore0(String s, DefaultChannelHandlerContext defaultchannelhandlercontext, DefaultChannelHandlerContext defaultchannelhandlercontext1) {
        checkMultiplicity(defaultchannelhandlercontext1);
        defaultchannelhandlercontext1.prev = defaultchannelhandlercontext.prev;
        defaultchannelhandlercontext1.next = defaultchannelhandlercontext;
        defaultchannelhandlercontext.prev.next = defaultchannelhandlercontext1;
        defaultchannelhandlercontext.prev = defaultchannelhandlercontext1;
        this.name2ctx.put(s, defaultchannelhandlercontext1);
        this.callHandlerAdded(defaultchannelhandlercontext1);
    }

    @Override
    public ChannelPipeline addAfter(String s, String s1, ChannelHandler channelhandler) {
        return this.addAfter((EventExecutorGroup) null, s, s1, channelhandler);
    }

    @Override
    public ChannelPipeline addAfter(EventExecutorGroup eventexecutorgroup, String s, String s1, ChannelHandler channelhandler) {
        synchronized (this) {
            DefaultChannelHandlerContext defaultchannelhandlercontext = this.getContextOrDie(s);

            this.checkDuplicateName(s1);
            DefaultChannelHandlerContext defaultchannelhandlercontext1 = new DefaultChannelHandlerContext(this, eventexecutorgroup, s1, channelhandler);

            this.addAfter0(s1, defaultchannelhandlercontext, defaultchannelhandlercontext1);
            return this;
        }
    }

    private void addAfter0(String s, DefaultChannelHandlerContext defaultchannelhandlercontext, DefaultChannelHandlerContext defaultchannelhandlercontext1) {
        this.checkDuplicateName(s);
        checkMultiplicity(defaultchannelhandlercontext1);
        defaultchannelhandlercontext1.prev = defaultchannelhandlercontext;
        defaultchannelhandlercontext1.next = defaultchannelhandlercontext.next;
        defaultchannelhandlercontext.next.prev = defaultchannelhandlercontext1;
        defaultchannelhandlercontext.next = defaultchannelhandlercontext1;
        this.name2ctx.put(s, defaultchannelhandlercontext1);
        this.callHandlerAdded(defaultchannelhandlercontext1);
    }

    @Override
    public ChannelPipeline addFirst(ChannelHandler... achannelhandler) {
        return this.addFirst((EventExecutorGroup) null, achannelhandler);
    }

    @Override
    public ChannelPipeline addFirst(EventExecutorGroup eventexecutorgroup, ChannelHandler... achannelhandler) {
        if (achannelhandler == null) {
            throw new NullPointerException("handlers");
        } else if (achannelhandler.length != 0 && achannelhandler[0] != null) {
            int i;

            for (i = 1; i < achannelhandler.length && achannelhandler[i] != null; ++i) {
                ;
            }

            for (int j = i - 1; j >= 0; --j) {
                ChannelHandler channelhandler = achannelhandler[j];

                this.addFirst(eventexecutorgroup, this.generateName(channelhandler), channelhandler);
            }

            return this;
        } else {
            return this;
        }
    }

    @Override
    public ChannelPipeline addLast(ChannelHandler... achannelhandler) {
        return this.addLast((EventExecutorGroup) null, achannelhandler);
    }

    @Override
    public ChannelPipeline addLast(EventExecutorGroup eventexecutorgroup, ChannelHandler... achannelhandler) {
        if (achannelhandler == null) {
            throw new NullPointerException("handlers");
        } else {
            ChannelHandler[] achannelhandler1 = achannelhandler;
            int i = achannelhandler.length;

            for (int j = 0; j < i; ++j) {
                ChannelHandler channelhandler = achannelhandler1[j];

                if (channelhandler == null) {
                    break;
                }

                this.addLast(eventexecutorgroup, this.generateName(channelhandler), channelhandler);
            }

            return this;
        }
    }

    private String generateName(ChannelHandler channelhandler) {
        WeakHashMap weakhashmap = DefaultChannelPipeline.nameCaches[(int) (Thread.currentThread().getId() % (long) DefaultChannelPipeline.nameCaches.length)];
        Class oclass = channelhandler.getClass();
        String s;

        synchronized (weakhashmap) {
            s = (String) weakhashmap.get(oclass);
            if (s == null) {
                s = StringUtil.simpleClassName(oclass) + "#0";
                weakhashmap.put(oclass, s);
            }
        }

        synchronized (this) {
            if (this.name2ctx.containsKey(s)) {
                String s1 = s.substring(0, s.length() - 1);
                int i = 1;

                while (true) {
                    String s2 = s1 + i;

                    if (!this.name2ctx.containsKey(s2)) {
                        s = s2;
                        break;
                    }

                    ++i;
                }
            }

            return s;
        }
    }

    @Override
    public ChannelPipeline remove(ChannelHandler channelhandler) {
        this.remove(this.getContextOrDie(channelhandler));
        return this;
    }

    @Override
    public ChannelHandler remove(String s) {
        return this.remove(this.getContextOrDie(s)).handler();
    }

    @Override
    public <T extends ChannelHandler> T remove(Class<T> oclass) {
        return this.remove(this.getContextOrDie(oclass)).handler();
    }

    private DefaultChannelHandlerContext remove(final DefaultChannelHandlerContext defaultchannelhandlercontext) {
        assert defaultchannelhandlercontext != this.head && defaultchannelhandlercontext != this.tail;

        Future future;
        DefaultChannelHandlerContext defaultchannelhandlercontext1;

        synchronized (this) {
            if (!defaultchannelhandlercontext.channel().isRegistered() || defaultchannelhandlercontext.executor().inEventLoop()) {
                this.remove0(defaultchannelhandlercontext);
                return defaultchannelhandlercontext;
            }

            future = defaultchannelhandlercontext.executor().submit(new Runnable() {
                public void run() {
                    DefaultChannelPipeline defaultchannelpipeline = DefaultChannelPipeline.this;

                    synchronized (DefaultChannelPipeline.this) {
                        DefaultChannelPipeline.this.remove0(defaultchannelhandlercontext);
                    }
                }
            });
            defaultchannelhandlercontext1 = defaultchannelhandlercontext;
        }

        waitForFuture(future);
        return defaultchannelhandlercontext1;
    }

    void remove0(DefaultChannelHandlerContext defaultchannelhandlercontext) {
        DefaultChannelHandlerContext defaultchannelhandlercontext1 = defaultchannelhandlercontext.prev;
        DefaultChannelHandlerContext defaultchannelhandlercontext2 = defaultchannelhandlercontext.next;

        defaultchannelhandlercontext1.next = defaultchannelhandlercontext2;
        defaultchannelhandlercontext2.prev = defaultchannelhandlercontext1;
        this.name2ctx.remove(defaultchannelhandlercontext.name());
        this.callHandlerRemoved(defaultchannelhandlercontext);
    }

    @Override
    public ChannelHandler removeFirst() {
        if (this.head.next == this.tail) {
            throw new NoSuchElementException();
        } else {
            return this.remove(this.head.next).handler();
        }
    }

    @Override
    public ChannelHandler removeLast() {
        if (this.head.next == this.tail) {
            throw new NoSuchElementException();
        } else {
            return this.remove(this.tail.prev).handler();
        }
    }

    @Override
    public ChannelPipeline replace(ChannelHandler channelhandler, String s, ChannelHandler channelhandler1) {
        this.replace(this.getContextOrDie(channelhandler), s, channelhandler1);
        return this;
    }

    @Override
    public ChannelHandler replace(String s, String s1, ChannelHandler channelhandler) {
        return this.replace(this.getContextOrDie(s), s1, channelhandler);
    }

    @Override
    public <T extends ChannelHandler> T replace(Class<T> oclass, String s, ChannelHandler channelhandler) {
        return this.replace(this.getContextOrDie(oclass), s, channelhandler);
    }

    private ChannelHandler replace(final DefaultChannelHandlerContext defaultchannelhandlercontext, final String s, ChannelHandler channelhandler) {
        assert defaultchannelhandlercontext != this.head && defaultchannelhandlercontext != this.tail;

        Future future;

        synchronized (this) {
            boolean flag = defaultchannelhandlercontext.name().equals(s);

            if (!flag) {
                this.checkDuplicateName(s);
            }

            final DefaultChannelHandlerContext defaultchannelhandlercontext1 = new DefaultChannelHandlerContext(this, defaultchannelhandlercontext.executor, s, channelhandler);

            if (!defaultchannelhandlercontext1.channel().isRegistered() || defaultchannelhandlercontext1.executor().inEventLoop()) {
                this.replace0(defaultchannelhandlercontext, s, defaultchannelhandlercontext1);
                return defaultchannelhandlercontext.handler();
            }

            future = defaultchannelhandlercontext1.executor().submit(new Runnable() {
                public void run() {
                    DefaultChannelPipeline defaultchannelpipeline = DefaultChannelPipeline.this;

                    synchronized (DefaultChannelPipeline.this) {
                        DefaultChannelPipeline.this.replace0(defaultchannelhandlercontext, s, defaultchannelhandlercontext1);
                    }
                }
            });
        }

        waitForFuture(future);
        return defaultchannelhandlercontext.handler();
    }

    private void replace0(DefaultChannelHandlerContext defaultchannelhandlercontext, String s, DefaultChannelHandlerContext defaultchannelhandlercontext1) {
        checkMultiplicity(defaultchannelhandlercontext1);
        DefaultChannelHandlerContext defaultchannelhandlercontext2 = defaultchannelhandlercontext.prev;
        DefaultChannelHandlerContext defaultchannelhandlercontext3 = defaultchannelhandlercontext.next;

        defaultchannelhandlercontext1.prev = defaultchannelhandlercontext2;
        defaultchannelhandlercontext1.next = defaultchannelhandlercontext3;
        defaultchannelhandlercontext2.next = defaultchannelhandlercontext1;
        defaultchannelhandlercontext3.prev = defaultchannelhandlercontext1;
        if (!defaultchannelhandlercontext.name().equals(s)) {
            this.name2ctx.remove(defaultchannelhandlercontext.name());
        }

        this.name2ctx.put(s, defaultchannelhandlercontext1);
        defaultchannelhandlercontext.prev = defaultchannelhandlercontext1;
        defaultchannelhandlercontext.next = defaultchannelhandlercontext1;
        this.callHandlerAdded(defaultchannelhandlercontext1);
        this.callHandlerRemoved(defaultchannelhandlercontext);
    }

    private static void checkMultiplicity(ChannelHandlerContext channelhandlercontext) {
        ChannelHandler channelhandler = channelhandlercontext.handler();

        if (channelhandler instanceof ChannelHandlerAdapter) {
            ChannelHandlerAdapter channelhandleradapter = (ChannelHandlerAdapter) channelhandler;

            if (!channelhandleradapter.isSharable() && channelhandleradapter.added) {
                throw new ChannelPipelineException(channelhandleradapter.getClass().getName() + " is not a @Sharable handler, so can't be added or removed multiple times.");
            }

            channelhandleradapter.added = true;
        }

    }

    private void callHandlerAdded(final ChannelHandlerContext channelhandlercontext) {
        if (channelhandlercontext.channel().isRegistered() && !channelhandlercontext.executor().inEventLoop()) {
            channelhandlercontext.executor().execute(new Runnable() {
                public void run() {
                    DefaultChannelPipeline.this.callHandlerAdded0(channelhandlercontext);
                }
            });
        } else {
            this.callHandlerAdded0(channelhandlercontext);
        }
    }

    private void callHandlerAdded0(ChannelHandlerContext channelhandlercontext) {
        try {
            channelhandlercontext.handler().handlerAdded(channelhandlercontext);
        } catch (Throwable throwable) {
            boolean flag = false;

            try {
                this.remove((DefaultChannelHandlerContext) channelhandlercontext);
                flag = true;
            } catch (Throwable throwable1) {
                if (DefaultChannelPipeline.logger.isWarnEnabled()) {
                    DefaultChannelPipeline.logger.warn("Failed to remove a handler: " + channelhandlercontext.name(), throwable1);
                }
            }

            if (flag) {
                this.fireExceptionCaught(new ChannelPipelineException(channelhandlercontext.handler().getClass().getName() + ".handlerAdded() has thrown an exception; removed.", throwable));
            } else {
                this.fireExceptionCaught(new ChannelPipelineException(channelhandlercontext.handler().getClass().getName() + ".handlerAdded() has thrown an exception; also failed to remove.", throwable));
            }
        }

    }

    private void callHandlerRemoved(final DefaultChannelHandlerContext defaultchannelhandlercontext) {
        if (defaultchannelhandlercontext.channel().isRegistered() && !defaultchannelhandlercontext.executor().inEventLoop()) {
            defaultchannelhandlercontext.executor().execute(new Runnable() {
                public void run() {
                    DefaultChannelPipeline.this.callHandlerRemoved0(defaultchannelhandlercontext);
                }
            });
        } else {
            this.callHandlerRemoved0(defaultchannelhandlercontext);
        }
    }

    private void callHandlerRemoved0(DefaultChannelHandlerContext defaultchannelhandlercontext) {
        try {
            defaultchannelhandlercontext.handler().handlerRemoved(defaultchannelhandlercontext);
            defaultchannelhandlercontext.setRemoved();
        } catch (Throwable throwable) {
            this.fireExceptionCaught(new ChannelPipelineException(defaultchannelhandlercontext.handler().getClass().getName() + ".handlerRemoved() has thrown an exception.", throwable));
        }

    }

    private static void waitForFuture(java.util.concurrent.Future<?> java_util_concurrent_future) {
        try {
            java_util_concurrent_future.get();
        } catch (ExecutionException executionexception) {
            PlatformDependent.throwException(executionexception.getCause());
        } catch (InterruptedException interruptedexception) {
            Thread.currentThread().interrupt();
        }

    }

    @Override
    public ChannelHandler first() {
        ChannelHandlerContext channelhandlercontext = this.firstContext();

        return channelhandlercontext == null ? null : channelhandlercontext.handler();
    }

    @Override
    public ChannelHandlerContext firstContext() {
        DefaultChannelHandlerContext defaultchannelhandlercontext = this.head.next;

        return defaultchannelhandlercontext == this.head ? null : this.head.next;
    }

    @Override
    public ChannelHandler last() {
        DefaultChannelHandlerContext defaultchannelhandlercontext = this.tail.prev;

        return defaultchannelhandlercontext == this.head ? null : defaultchannelhandlercontext.handler();
    }

    @Override
    public ChannelHandlerContext lastContext() {
        DefaultChannelHandlerContext defaultchannelhandlercontext = this.tail.prev;

        return defaultchannelhandlercontext == this.head ? null : defaultchannelhandlercontext;
    }

    @Override
    public ChannelHandler get(String s) {
        ChannelHandlerContext channelhandlercontext = this.context(s);

        return channelhandlercontext == null ? null : channelhandlercontext.handler();
    }

    @Override
    public <T extends ChannelHandler> T get(Class<T> oclass) {
        ChannelHandlerContext channelhandlercontext = this.context(oclass);

        return channelhandlercontext == null ? null : channelhandlercontext.handler();
    }

    @Override
    public ChannelHandlerContext context(String s) {
        if (s == null) {
            throw new NullPointerException("name");
        } else {
            synchronized (this) {
                return (ChannelHandlerContext) this.name2ctx.get(s);
            }
        }
    }

    @Override
    public ChannelHandlerContext context(ChannelHandler channelhandler) {
        if (channelhandler == null) {
            throw new NullPointerException("handler");
        } else {
            for (DefaultChannelHandlerContext defaultchannelhandlercontext = this.head.next; defaultchannelhandlercontext != null; defaultchannelhandlercontext = defaultchannelhandlercontext.next) {
                if (defaultchannelhandlercontext.handler() == channelhandler) {
                    return defaultchannelhandlercontext;
                }
            }

            return null;
        }
    }

    @Override
    public ChannelHandlerContext context(Class<? extends ChannelHandler> oclass) {
        if (oclass == null) {
            throw new NullPointerException("handlerType");
        } else {
            for (DefaultChannelHandlerContext defaultchannelhandlercontext = this.head.next; defaultchannelhandlercontext != null; defaultchannelhandlercontext = defaultchannelhandlercontext.next) {
                if (oclass.isAssignableFrom(defaultchannelhandlercontext.handler().getClass())) {
                    return defaultchannelhandlercontext;
                }
            }

            return null;
        }
    }

    @Override
    public List<String> names() {
        ArrayList arraylist = new ArrayList();

        for (DefaultChannelHandlerContext defaultchannelhandlercontext = this.head.next; defaultchannelhandlercontext != null; defaultchannelhandlercontext = defaultchannelhandlercontext.next) {
            arraylist.add(defaultchannelhandlercontext.name());
        }

        return arraylist;
    }

    @Override
    public Map<String, ChannelHandler> toMap() {
        LinkedHashMap linkedhashmap = new LinkedHashMap();

        for (DefaultChannelHandlerContext defaultchannelhandlercontext = this.head.next; defaultchannelhandlercontext != this.tail; defaultchannelhandlercontext = defaultchannelhandlercontext.next) {
            linkedhashmap.put(defaultchannelhandlercontext.name(), defaultchannelhandlercontext.handler());
        }

        return linkedhashmap;
    }

    public Iterator<Entry<String, ChannelHandler>> iterator() {
        return this.toMap().entrySet().iterator();
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append(this.getClass().getSimpleName());
        stringbuilder.append('{');
        DefaultChannelHandlerContext defaultchannelhandlercontext = this.head.next;

        while (defaultchannelhandlercontext != this.tail) {
            stringbuilder.append('(');
            stringbuilder.append(defaultchannelhandlercontext.name());
            stringbuilder.append(" = ");
            stringbuilder.append(defaultchannelhandlercontext.handler().getClass().getName());
            stringbuilder.append(')');
            defaultchannelhandlercontext = defaultchannelhandlercontext.next;
            if (defaultchannelhandlercontext == this.tail) {
                break;
            }

            stringbuilder.append(", ");
        }

        stringbuilder.append('}');
        return stringbuilder.toString();
    }

    @Override
    public ChannelPipeline fireChannelRegistered() {
        this.head.fireChannelRegistered();
        return this;
    }

    @Override
    public ChannelPipeline fireChannelUnregistered() {
        this.head.fireChannelUnregistered();
        if (!this.channel.isOpen()) {
            this.teardownAll();
        }

        return this;
    }

    private void teardownAll() {
        this.tail.prev.teardown();
    }

    @Override
    public ChannelPipeline fireChannelActive() {
        this.head.fireChannelActive();
        if (this.channel.config().isAutoRead()) {
            this.channel.read();
        }

        return this;
    }

    @Override
    public ChannelPipeline fireChannelInactive() {
        this.head.fireChannelInactive();
        return this;
    }

    @Override
    public ChannelPipeline fireExceptionCaught(Throwable throwable) {
        this.head.fireExceptionCaught(throwable);
        return this;
    }

    @Override
    public ChannelPipeline fireUserEventTriggered(Object object) {
        this.head.fireUserEventTriggered(object);
        return this;
    }

    @Override
    public ChannelPipeline fireChannelRead(Object object) {
        this.head.fireChannelRead(object);
        return this;
    }

    @Override
    public ChannelPipeline fireChannelReadComplete() {
        this.head.fireChannelReadComplete();
        if (this.channel.config().isAutoRead()) {
            this.read();
        }

        return this;
    }

    @Override
    public ChannelPipeline fireChannelWritabilityChanged() {
        this.head.fireChannelWritabilityChanged();
        return this;
    }

    @Override
    public ChannelFuture bind(SocketAddress socketaddress) {
        return this.tail.bind(socketaddress);
    }

    @Override
    public ChannelFuture connect(SocketAddress socketaddress) {
        return this.tail.connect(socketaddress);
    }

    @Override
    public ChannelFuture connect(SocketAddress socketaddress, SocketAddress socketaddress1) {
        return this.tail.connect(socketaddress, socketaddress1);
    }

    @Override
    public ChannelFuture disconnect() {
        return this.tail.disconnect();
    }

    @Override
    public ChannelFuture close() {
        return this.tail.close();
    }

    @Override
    public ChannelFuture deregister() {
        return this.tail.deregister();
    }

    @Override
    public ChannelPipeline flush() {
        this.tail.flush();
        return this;
    }

    @Override
    public ChannelFuture bind(SocketAddress socketaddress, ChannelPromise channelpromise) {
        return this.tail.bind(socketaddress, channelpromise);
    }

    @Override
    public ChannelFuture connect(SocketAddress socketaddress, ChannelPromise channelpromise) {
        return this.tail.connect(socketaddress, channelpromise);
    }

    @Override
    public ChannelFuture connect(SocketAddress socketaddress, SocketAddress socketaddress1, ChannelPromise channelpromise) {
        return this.tail.connect(socketaddress, socketaddress1, channelpromise);
    }

    @Override
    public ChannelFuture disconnect(ChannelPromise channelpromise) {
        return this.tail.disconnect(channelpromise);
    }

    @Override
    public ChannelFuture close(ChannelPromise channelpromise) {
        return this.tail.close(channelpromise);
    }

    @Override
    public ChannelFuture deregister(ChannelPromise channelpromise) {
        return this.tail.deregister(channelpromise);
    }

    @Override
    public ChannelPipeline read() {
        this.tail.read();
        return this;
    }

    @Override
    public ChannelFuture write(Object object) {
        return this.tail.write(object);
    }

    @Override
    public ChannelFuture write(Object object, ChannelPromise channelpromise) {
        return this.tail.write(object, channelpromise);
    }

    @Override
    public ChannelFuture writeAndFlush(Object object, ChannelPromise channelpromise) {
        return this.tail.writeAndFlush(object, channelpromise);
    }

    @Override
    public ChannelFuture writeAndFlush(Object object) {
        return this.tail.writeAndFlush(object);
    }

    private void checkDuplicateName(String s) {
        if (this.name2ctx.containsKey(s)) {
            throw new IllegalArgumentException("Duplicate handler name: " + s);
        }
    }

    private DefaultChannelHandlerContext getContextOrDie(String s) {
        DefaultChannelHandlerContext defaultchannelhandlercontext = (DefaultChannelHandlerContext) this.context(s);

        if (defaultchannelhandlercontext == null) {
            throw new NoSuchElementException(s);
        } else {
            return defaultchannelhandlercontext;
        }
    }

    private DefaultChannelHandlerContext getContextOrDie(ChannelHandler channelhandler) {
        DefaultChannelHandlerContext defaultchannelhandlercontext = (DefaultChannelHandlerContext) this.context(channelhandler);

        if (defaultchannelhandlercontext == null) {
            throw new NoSuchElementException(channelhandler.getClass().getName());
        } else {
            return defaultchannelhandlercontext;
        }
    }

    private DefaultChannelHandlerContext getContextOrDie(Class<? extends ChannelHandler> oclass) {
        DefaultChannelHandlerContext defaultchannelhandlercontext = (DefaultChannelHandlerContext) this.context(oclass);

        if (defaultchannelhandlercontext == null) {
            throw new NoSuchElementException(oclass.getName());
        } else {
            return defaultchannelhandlercontext;
        }
    }

    static {
        for (int i = 0; i < DefaultChannelPipeline.nameCaches.length; ++i) {
            DefaultChannelPipeline.nameCaches[i] = new WeakHashMap();
        }

    }

    static final class HeadHandler implements ChannelOutboundHandler {

        protected final Channel.Unsafe unsafe;

        protected HeadHandler(Channel.Unsafe channel_unsafe) {
            this.unsafe = channel_unsafe;
        }

        @Override
        public void handlerAdded(ChannelHandlerContext channelhandlercontext) throws Exception {}

        @Override
        public void handlerRemoved(ChannelHandlerContext channelhandlercontext) throws Exception {}

        @Override
        public void bind(ChannelHandlerContext channelhandlercontext, SocketAddress socketaddress, ChannelPromise channelpromise) throws Exception {
            this.unsafe.bind(socketaddress, channelpromise);
        }

        @Override
        public void connect(ChannelHandlerContext channelhandlercontext, SocketAddress socketaddress, SocketAddress socketaddress1, ChannelPromise channelpromise) throws Exception {
            this.unsafe.connect(socketaddress, socketaddress1, channelpromise);
        }

        @Override
        public void disconnect(ChannelHandlerContext channelhandlercontext, ChannelPromise channelpromise) throws Exception {
            this.unsafe.disconnect(channelpromise);
        }

        @Override
        public void close(ChannelHandlerContext channelhandlercontext, ChannelPromise channelpromise) throws Exception {
            this.unsafe.close(channelpromise);
        }

        @Override
        public void deregister(ChannelHandlerContext channelhandlercontext, ChannelPromise channelpromise) throws Exception {
            this.unsafe.deregister(channelpromise);
        }

        @Override
        public void read(ChannelHandlerContext channelhandlercontext) {
            this.unsafe.beginRead();
        }

        @Override
        public void write(ChannelHandlerContext channelhandlercontext, Object object, ChannelPromise channelpromise) throws Exception {
            this.unsafe.write(object, channelpromise);
        }

        @Override
        public void flush(ChannelHandlerContext channelhandlercontext) throws Exception {
            this.unsafe.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) throws Exception {
            channelhandlercontext.fireExceptionCaught(throwable);
        }
    }

    static final class TailHandler implements ChannelInboundHandler {

        TailHandler() {}

        @Override
        public void channelRegistered(ChannelHandlerContext channelhandlercontext) throws Exception {}

        @Override
        public void channelUnregistered(ChannelHandlerContext channelhandlercontext) throws Exception {}

        @Override
        public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception {}

        @Override
        public void channelInactive(ChannelHandlerContext channelhandlercontext) throws Exception {}

        @Override
        public void channelWritabilityChanged(ChannelHandlerContext channelhandlercontext) throws Exception {}

        @Override
        public void handlerAdded(ChannelHandlerContext channelhandlercontext) throws Exception {}

        @Override
        public void handlerRemoved(ChannelHandlerContext channelhandlercontext) throws Exception {}

        @Override
        public void userEventTriggered(ChannelHandlerContext channelhandlercontext, Object object) throws Exception {}

        @Override
        public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) throws Exception {
            DefaultChannelPipeline.logger.warn("An exceptionCaught() event was fired, and it reached at the tail of the pipeline. It usually means the last handler in the pipeline did not handle the exception.", throwable);
        }

        @Override
        public void channelRead(ChannelHandlerContext channelhandlercontext, Object object) throws Exception {
            try {
                DefaultChannelPipeline.logger.debug("Discarded inbound message {} that reached at the tail of the pipeline. Please check your pipeline configuration.", object);
            } finally {
                ReferenceCountUtil.release(object);
            }

        }

        @Override
        public void channelReadComplete(ChannelHandlerContext channelhandlercontext) throws Exception {}
    }
}
