package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ImmediateExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PendingWrite;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.Status;
import javax.net.ssl.SSLException;

public class SslHandler extends ByteToMessageDecoder implements ChannelOutboundHandler {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SslHandler.class);
    private static final Pattern IGNORABLE_CLASS_IN_STACK = Pattern.compile("^.*(?:Socket|Datagram|Sctp|Udt)Channel.*$");
    private static final Pattern IGNORABLE_ERROR_MESSAGE = Pattern.compile("^.*(?:connection.*(?:reset|closed|abort|broken)|broken.*pipe).*$", 2);
    private static final SSLException SSLENGINE_CLOSED = new SSLException("SSLEngine closed already");
    private static final SSLException HANDSHAKE_TIMED_OUT = new SSLException("handshake timed out");
    private static final ClosedChannelException CHANNEL_CLOSED = new ClosedChannelException();
    private volatile ChannelHandlerContext ctx;
    private final SSLEngine engine;
    private final Executor delegatedTaskExecutor;
    private final boolean startTls;
    private boolean sentFirstMessage;
    private final SslHandler.LazyChannelPromise handshakePromise;
    private final SslHandler.LazyChannelPromise sslCloseFuture;
    private final Deque<PendingWrite> pendingUnencryptedWrites;
    private int packetLength;
    private ByteBuf decodeOut;
    private volatile long handshakeTimeoutMillis;
    private volatile long closeNotifyTimeoutMillis;

    public SslHandler(SSLEngine sslengine) {
        this(sslengine, ImmediateExecutor.INSTANCE);
    }

    public SslHandler(SSLEngine sslengine, boolean flag) {
        this(sslengine, flag, ImmediateExecutor.INSTANCE);
    }

    public SslHandler(SSLEngine sslengine, Executor executor) {
        this(sslengine, false, executor);
    }

    public SslHandler(SSLEngine sslengine, boolean flag, Executor executor) {
        this.handshakePromise = new SslHandler.LazyChannelPromise();
        this.sslCloseFuture = new SslHandler.LazyChannelPromise();
        this.pendingUnencryptedWrites = new ArrayDeque();
        this.handshakeTimeoutMillis = 10000L;
        this.closeNotifyTimeoutMillis = 3000L;
        if (sslengine == null) {
            throw new NullPointerException("engine");
        } else if (executor == null) {
            throw new NullPointerException("delegatedTaskExecutor");
        } else {
            this.engine = sslengine;
            this.delegatedTaskExecutor = executor;
            this.startTls = flag;
        }
    }

    public long getHandshakeTimeoutMillis() {
        return this.handshakeTimeoutMillis;
    }

    public void setHandshakeTimeout(long i, TimeUnit timeunit) {
        if (timeunit == null) {
            throw new NullPointerException("unit");
        } else {
            this.setHandshakeTimeoutMillis(timeunit.toMillis(i));
        }
    }

    public void setHandshakeTimeoutMillis(long i) {
        if (i < 0L) {
            throw new IllegalArgumentException("handshakeTimeoutMillis: " + i + " (expected: >= 0)");
        } else {
            this.handshakeTimeoutMillis = i;
        }
    }

    public long getCloseNotifyTimeoutMillis() {
        return this.closeNotifyTimeoutMillis;
    }

    public void setCloseNotifyTimeout(long i, TimeUnit timeunit) {
        if (timeunit == null) {
            throw new NullPointerException("unit");
        } else {
            this.setCloseNotifyTimeoutMillis(timeunit.toMillis(i));
        }
    }

    public void setCloseNotifyTimeoutMillis(long i) {
        if (i < 0L) {
            throw new IllegalArgumentException("closeNotifyTimeoutMillis: " + i + " (expected: >= 0)");
        } else {
            this.closeNotifyTimeoutMillis = i;
        }
    }

    public SSLEngine engine() {
        return this.engine;
    }

    public Future<Channel> handshakeFuture() {
        return this.handshakePromise;
    }

    public ChannelFuture close() {
        return this.close(this.ctx.newPromise());
    }

    public ChannelFuture close(final ChannelPromise channelpromise) {
        final ChannelHandlerContext channelhandlercontext = this.ctx;

        channelhandlercontext.executor().execute(new Runnable() {
            public void run() {
                SslHandler.this.engine.closeOutbound();

                try {
                    SslHandler.this.write(channelhandlercontext, Unpooled.EMPTY_BUFFER, channelpromise);
                    SslHandler.this.flush(channelhandlercontext);
                } catch (Exception exception) {
                    if (!channelpromise.tryFailure(exception)) {
                        SslHandler.logger.warn("flush() raised a masked exception.", (Throwable) exception);
                    }
                }

            }
        });
        return channelpromise;
    }

    public Future<Channel> sslCloseFuture() {
        return this.sslCloseFuture;
    }

    @Override
    public void handlerRemoved0(ChannelHandlerContext channelhandlercontext) throws Exception {
        if (this.decodeOut != null) {
            this.decodeOut.release();
            this.decodeOut = null;
        }

        while (true) {
            PendingWrite pendingwrite = (PendingWrite) this.pendingUnencryptedWrites.poll();

            if (pendingwrite == null) {
                return;
            }

            pendingwrite.failAndRecycle(new ChannelException("Pending write on removal of SslHandler"));
        }
    }

    @Override
    public void bind(ChannelHandlerContext channelhandlercontext, SocketAddress socketaddress, ChannelPromise channelpromise) throws Exception {
        channelhandlercontext.bind(socketaddress, channelpromise);
    }

    @Override
    public void connect(ChannelHandlerContext channelhandlercontext, SocketAddress socketaddress, SocketAddress socketaddress1, ChannelPromise channelpromise) throws Exception {
        channelhandlercontext.connect(socketaddress, socketaddress1, channelpromise);
    }

    @Override
    public void deregister(ChannelHandlerContext channelhandlercontext, ChannelPromise channelpromise) throws Exception {
        channelhandlercontext.deregister(channelpromise);
    }

    @Override
    public void disconnect(ChannelHandlerContext channelhandlercontext, ChannelPromise channelpromise) throws Exception {
        this.closeOutboundAndChannel(channelhandlercontext, channelpromise, true);
    }

    @Override
    public void close(ChannelHandlerContext channelhandlercontext, ChannelPromise channelpromise) throws Exception {
        this.closeOutboundAndChannel(channelhandlercontext, channelpromise, false);
    }

    @Override
    public void read(ChannelHandlerContext channelhandlercontext) {
        channelhandlercontext.read();
    }

    @Override
    public void write(ChannelHandlerContext channelhandlercontext, Object object, ChannelPromise channelpromise) throws Exception {
        this.pendingUnencryptedWrites.add(PendingWrite.newInstance((ByteBuf) object, channelpromise));
    }

    @Override
    public void flush(ChannelHandlerContext channelhandlercontext) throws Exception {
        if (this.startTls && !this.sentFirstMessage) {
            this.sentFirstMessage = true;

            while (true) {
                PendingWrite pendingwrite = (PendingWrite) this.pendingUnencryptedWrites.poll();

                if (pendingwrite == null) {
                    channelhandlercontext.flush();
                    return;
                }

                channelhandlercontext.write(pendingwrite.msg(), (ChannelPromise) pendingwrite.recycleAndGet());
            }
        } else {
            if (this.pendingUnencryptedWrites.isEmpty()) {
                this.pendingUnencryptedWrites.add(PendingWrite.newInstance(Unpooled.EMPTY_BUFFER, (Promise) null));
            }

            this.flush0(channelhandlercontext);
        }
    }

    private void flush0(ChannelHandlerContext channelhandlercontext) throws SSLException {
        ByteBuf bytebuf = null;
        ChannelPromise channelpromise = null;

        while (true) {
            try {
                PendingWrite pendingwrite = (PendingWrite) this.pendingUnencryptedWrites.peek();

                if (pendingwrite == null) {
                    return;
                }

                if (bytebuf == null) {
                    bytebuf = channelhandlercontext.alloc().buffer();
                }

                ByteBuf bytebuf1 = (ByteBuf) pendingwrite.msg();
                SSLEngineResult sslengineresult = wrap(this.engine, bytebuf1, bytebuf);

                if (!bytebuf1.isReadable()) {
                    bytebuf1.release();
                    channelpromise = (ChannelPromise) pendingwrite.recycleAndGet();
                    this.pendingUnencryptedWrites.remove();
                } else {
                    channelpromise = null;
                }

                if (sslengineresult.getStatus() == Status.CLOSED) {
                    while (true) {
                        PendingWrite pendingwrite1 = (PendingWrite) this.pendingUnencryptedWrites.poll();

                        if (pendingwrite1 == null) {
                            return;
                        }

                        pendingwrite1.failAndRecycle(SslHandler.SSLENGINE_CLOSED);
                    }
                }

                switch (sslengineresult.getHandshakeStatus()) {
                    case NEED_TASK:
                        this.runDelegatedTasks();
                        break;
                    case FINISHED:
                        this.setHandshakeSuccess();
                    case NOT_HANDSHAKING:
                    case NEED_WRAP:
                        if (channelpromise != null) {
                            channelhandlercontext.writeAndFlush(bytebuf, channelpromise);
                            channelpromise = null;
                        } else {
                            channelhandlercontext.writeAndFlush(bytebuf);
                        }

                        bytebuf = channelhandlercontext.alloc().buffer();
                        break;
                    case NEED_UNWRAP:
                        return;
                    default:
                        throw new IllegalStateException("Unknown handshake status: " + sslengineresult.getHandshakeStatus());
                }
            } catch (SSLException sslexception) {
                this.setHandshakeFailure(sslexception);
                throw sslexception;
            } finally {
                if (bytebuf != null && bytebuf.isReadable()) {
                    if (channelpromise != null) {
                        channelhandlercontext.writeAndFlush(bytebuf, channelpromise);
                    } else {
                        channelhandlercontext.writeAndFlush(bytebuf);
                    }

                    bytebuf = null;
                } else if (channelpromise != null) {
                    channelpromise.trySuccess();
                }

                if (bytebuf != null) {
                    bytebuf.release();
                }

            }
        }
    }

    private void flushNonAppData0(ChannelHandlerContext channelhandlercontext, boolean flag) throws SSLException {
        ByteBuf bytebuf = null;

        try {
            SSLEngineResult sslengineresult;

            try {
                do {
                    if (bytebuf == null) {
                        bytebuf = channelhandlercontext.alloc().buffer();
                    }

                    sslengineresult = wrap(this.engine, Unpooled.EMPTY_BUFFER, bytebuf);
                    if (sslengineresult.bytesProduced() > 0) {
                        channelhandlercontext.writeAndFlush(bytebuf);
                        bytebuf = null;
                    }

                    switch (sslengineresult.getHandshakeStatus()) {
                        case NEED_TASK:
                            this.runDelegatedTasks();
                            break;
                        case FINISHED:
                            this.setHandshakeSuccess();
                            break;
                        case NOT_HANDSHAKING:
                            if (!flag) {
                                this.unwrap(channelhandlercontext);
                            }
                        case NEED_WRAP:
                            break;
                        case NEED_UNWRAP:
                            if (!flag) {
                                this.unwrap(channelhandlercontext);
                            }
                            break;
                        default:
                            throw new IllegalStateException("Unknown handshake status: " + sslengineresult.getHandshakeStatus());
                    }
                } while (sslengineresult.bytesProduced() != 0);
            } catch (SSLException sslexception) {
                this.setHandshakeFailure(sslexception);
                throw sslexception;
            }
        } finally {
            if (bytebuf != null) {
                bytebuf.release();
            }

        }

    }

    private static SSLEngineResult wrap(SSLEngine sslengine, ByteBuf bytebuf, ByteBuf bytebuf1) throws SSLException {
        ByteBuffer bytebuffer = bytebuf.nioBuffer();

        while (true) {
            ByteBuffer bytebuffer1 = bytebuf1.nioBuffer(bytebuf1.writerIndex(), bytebuf1.writableBytes());
            SSLEngineResult sslengineresult = sslengine.wrap(bytebuffer, bytebuffer1);

            bytebuf.skipBytes(sslengineresult.bytesConsumed());
            bytebuf1.writerIndex(bytebuf1.writerIndex() + sslengineresult.bytesProduced());
            if (sslengineresult.getStatus() != Status.BUFFER_OVERFLOW) {
                return sslengineresult;
            }

            bytebuf1.ensureWritable(sslengine.getSession().getPacketBufferSize());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.setHandshakeFailure(SslHandler.CHANNEL_CLOSED);
        super.channelInactive(channelhandlercontext);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) throws Exception {
        if (this.ignoreException(throwable)) {
            if (SslHandler.logger.isDebugEnabled()) {
                SslHandler.logger.debug("Swallowing a harmless 'connection reset by peer / broken pipe' error that occurred while writing close_notify in response to the peer's close_notify", throwable);
            }

            if (channelhandlercontext.channel().isActive()) {
                channelhandlercontext.close();
            }
        } else {
            channelhandlercontext.fireExceptionCaught(throwable);
        }

    }

    private boolean ignoreException(Throwable throwable) {
        if (!(throwable instanceof SSLException) && throwable instanceof IOException && this.sslCloseFuture.isDone()) {
            String s = String.valueOf(throwable.getMessage()).toLowerCase();

            if (SslHandler.IGNORABLE_ERROR_MESSAGE.matcher(s).matches()) {
                return true;
            }

            StackTraceElement[] astacktraceelement = throwable.getStackTrace();
            StackTraceElement[] astacktraceelement1 = astacktraceelement;
            int i = astacktraceelement.length;

            for (int j = 0; j < i; ++j) {
                StackTraceElement stacktraceelement = astacktraceelement1[j];
                String s1 = stacktraceelement.getClassName();
                String s2 = stacktraceelement.getMethodName();

                if (!s1.startsWith("io.netty.") && "read".equals(s2)) {
                    if (SslHandler.IGNORABLE_CLASS_IN_STACK.matcher(s1).matches()) {
                        return true;
                    }

                    try {
                        Class oclass = this.getClass().getClassLoader().loadClass(s1);

                        if (SocketChannel.class.isAssignableFrom(oclass) || DatagramChannel.class.isAssignableFrom(oclass)) {
                            return true;
                        }

                        if (PlatformDependent.javaVersion() >= 7 && "com.sun.nio.sctp.SctpChannel".equals(oclass.getSuperclass().getName())) {
                            return true;
                        }
                    } catch (ClassNotFoundException classnotfoundexception) {
                        ;
                    }
                }
            }
        }

        return false;
    }

    public static boolean isEncrypted(ByteBuf bytebuf) {
        if (bytebuf.readableBytes() < 5) {
            throw new IllegalArgumentException("buffer must have at least 5 readable bytes");
        } else {
            return getEncryptedPacketLength(bytebuf) != -1;
        }
    }

    private static int getEncryptedPacketLength(ByteBuf bytebuf) {
        int i = bytebuf.readerIndex();
        int j = 0;
        boolean flag;

        switch (bytebuf.getUnsignedByte(i)) {
            case 20:
            case 21:
            case 22:
            case 23:
                flag = true;
                break;
            default:
                flag = false;
        }

        if (flag) {
            short short0 = bytebuf.getUnsignedByte(i + 1);

            if (short0 == 3) {
                j = bytebuf.getUnsignedShort(i + 3) + 5;
                if (j <= 5) {
                    flag = false;
                }
            } else {
                flag = false;
            }
        }

        if (!flag) {
            boolean flag1 = true;
            int k = (bytebuf.getUnsignedByte(i) & 128) != 0 ? 2 : 3;
            short short1 = bytebuf.getUnsignedByte(i + k + 1);

            if (short1 != 2 && short1 != 3) {
                flag1 = false;
            } else {
                if (k == 2) {
                    j = (bytebuf.getShort(i) & 32767) + 2;
                } else {
                    j = (bytebuf.getShort(i) & 16383) + 3;
                }

                if (j <= k) {
                    flag1 = false;
                }
            }

            if (!flag1) {
                return -1;
            }
        }

        return j;
    }

    @Override
    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws SSLException {
        int i = this.packetLength;
        int j = bytebuf.readableBytes();

        if (i == 0) {
            if (j < 5) {
                return;
            }

            i = getEncryptedPacketLength(bytebuf);
            if (i == -1) {
                NotSslRecordException notsslrecordexception = new NotSslRecordException("not an SSL/TLS record: " + ByteBufUtil.hexDump(bytebuf));

                bytebuf.skipBytes(j);
                channelhandlercontext.fireExceptionCaught(notsslrecordexception);
                this.setHandshakeFailure(notsslrecordexception);
                return;
            }

            assert i > 0;

            this.packetLength = i;
        }

        if (j >= i) {
            int k = bytebuf.readerIndex();

            bytebuf.skipBytes(i);
            ByteBuffer bytebuffer = bytebuf.nioBuffer(k, i);

            this.packetLength = 0;
            this.unwrap(channelhandlercontext, bytebuffer, list);
        }
    }

    private void unwrap(ChannelHandlerContext channelhandlercontext) throws SSLException {
        RecyclableArrayList recyclablearraylist = RecyclableArrayList.newInstance();

        try {
            this.unwrap(channelhandlercontext, Unpooled.EMPTY_BUFFER.nioBuffer(), (List) recyclablearraylist);
            int i = recyclablearraylist.size();

            for (int j = 0; j < i; ++j) {
                channelhandlercontext.fireChannelRead(recyclablearraylist.get(j));
            }
        } finally {
            recyclablearraylist.recycle();
        }

    }

    private void unwrap(ChannelHandlerContext channelhandlercontext, ByteBuffer bytebuffer, List<Object> list) throws SSLException {
        boolean flag = false;
        int i = 0;

        while (true) {
            boolean flag1 = false;

            try {
                flag1 = true;
                if (this.decodeOut == null) {
                    this.decodeOut = channelhandlercontext.alloc().buffer();
                }

                SSLEngineResult sslengineresult = unwrap(this.engine, bytebuffer, this.decodeOut);

                i += sslengineresult.bytesProduced();
                switch (sslengineresult.getStatus()) {
                    case CLOSED:
                        this.sslCloseFuture.trySuccess(channelhandlercontext.channel());
                    default:
                        switch (sslengineresult.getHandshakeStatus()) {
                            case NEED_TASK:
                                this.runDelegatedTasks();
                                break;
                            case FINISHED:
                                this.setHandshakeSuccess();
                                flag = true;
                                continue;
                            case NOT_HANDSHAKING:
                            case NEED_UNWRAP:
                                break;
                            case NEED_WRAP:
                                this.flushNonAppData0(channelhandlercontext, true);
                                break;
                            default:
                                throw new IllegalStateException("Unknown handshake status: " + sslengineresult.getHandshakeStatus());
                        }

                        if (sslengineresult.bytesConsumed() != 0 || sslengineresult.bytesProduced() != 0) {
                            continue;
                        }
                    case BUFFER_UNDERFLOW:
                }

                if (flag) {
                    this.flush0(channelhandlercontext);
                    flag1 = false;
                } else {
                    flag1 = false;
                }
            } catch (SSLException sslexception) {
                this.setHandshakeFailure(sslexception);
                throw sslexception;
            } finally {
                if (flag1) {
                    if (i > 0) {
                        ByteBuf bytebuf = this.decodeOut;

                        this.decodeOut = null;
                        list.add(bytebuf);
                    }

                }
            }

            if (i > 0) {
                ByteBuf bytebuf1 = this.decodeOut;

                this.decodeOut = null;
                list.add(bytebuf1);
            }

            return;
        }
    }

    private static SSLEngineResult unwrap(SSLEngine sslengine, ByteBuffer bytebuffer, ByteBuf bytebuf) throws SSLException {
        while (true) {
            ByteBuffer bytebuffer1 = bytebuf.nioBuffer(bytebuf.writerIndex(), bytebuf.writableBytes());
            SSLEngineResult sslengineresult = sslengine.unwrap(bytebuffer, bytebuffer1);

            bytebuf.writerIndex(bytebuf.writerIndex() + sslengineresult.bytesProduced());
            switch (sslengineresult.getStatus()) {
                case BUFFER_OVERFLOW:
                    bytebuf.ensureWritable(sslengine.getSession().getApplicationBufferSize());
                    break;
                default:
                    return sslengineresult;
            }
        }
    }

    private void runDelegatedTasks() {
        while (true) {
            Runnable runnable = this.engine.getDelegatedTask();

            if (runnable == null) {
                return;
            }

            this.delegatedTaskExecutor.execute(runnable);
        }
    }

    private void setHandshakeSuccess() {
        if (this.handshakePromise.trySuccess(this.ctx.channel())) {
            this.ctx.fireUserEventTriggered(SslHandshakeCompletionEvent.SUCCESS);
        }

    }

    private void setHandshakeFailure(Throwable throwable) {
        this.engine.closeOutbound();

        try {
            this.engine.closeInbound();
        } catch (SSLException sslexception) {
            String s = sslexception.getMessage();

            if (s == null || !s.contains("possible truncation attack")) {
                SslHandler.logger.debug("SSLEngine.closeInbound() raised an exception.", (Throwable) sslexception);
            }
        }

        this.notifyHandshakeFailure(throwable);

        while (true) {
            PendingWrite pendingwrite = (PendingWrite) this.pendingUnencryptedWrites.poll();

            if (pendingwrite == null) {
                return;
            }

            pendingwrite.failAndRecycle(throwable);
        }
    }

    private void notifyHandshakeFailure(Throwable throwable) {
        if (this.handshakePromise.tryFailure(throwable)) {
            this.ctx.fireUserEventTriggered(new SslHandshakeCompletionEvent(throwable));
            this.ctx.close();
        }

    }

    private void closeOutboundAndChannel(ChannelHandlerContext channelhandlercontext, ChannelPromise channelpromise, boolean flag) throws Exception {
        if (!channelhandlercontext.channel().isActive()) {
            if (flag) {
                channelhandlercontext.disconnect(channelpromise);
            } else {
                channelhandlercontext.close(channelpromise);
            }

        } else {
            this.engine.closeOutbound();
            ChannelPromise channelpromise1 = channelhandlercontext.newPromise();

            this.write(channelhandlercontext, Unpooled.EMPTY_BUFFER, channelpromise1);
            this.flush(channelhandlercontext);
            this.safeClose(channelhandlercontext, channelpromise1, channelpromise);
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.ctx = channelhandlercontext;
        if (channelhandlercontext.channel().isActive()) {
            this.handshake0();
        }

    }

    private Future<Channel> handshake0() {
        final ScheduledFuture scheduledfuture;

        if (this.handshakeTimeoutMillis > 0L) {
            scheduledfuture = this.ctx.executor().schedule(new Runnable() {
                public void run() {
                    if (!SslHandler.this.handshakePromise.isDone()) {
                        SslHandler.this.notifyHandshakeFailure(SslHandler.HANDSHAKE_TIMED_OUT);
                    }
                }
            }, this.handshakeTimeoutMillis, TimeUnit.MILLISECONDS);
        } else {
            scheduledfuture = null;
        }

        this.handshakePromise.addListener(new GenericFutureListener<Future<Channel>>() {
            @Override
            public void operationComplete(Future<Channel> future) throws Exception {
                if (scheduledfuture != null) {
                    scheduledfuture.cancel(false);
                }

            }
        });

        try {
            this.engine.beginHandshake();
            this.flushNonAppData0(this.ctx, false);
        } catch (Exception exception) {
            this.notifyHandshakeFailure(exception);
        }

        return this.handshakePromise;
    }

    @Override
    public void channelActive(final ChannelHandlerContext channelhandlercontext) throws Exception {
        if (!this.startTls && this.engine.getUseClientMode()) {
            this.handshake0().addListener(new GenericFutureListener<Future<Channel>>() {
                @Override
                public void operationComplete(Future<Channel> future) throws Exception {
                    if (!future.isSuccess()) {
                        future.cause().printStackTrace();
                        channelhandlercontext.close();
                    }

                }
            });
        }

        channelhandlercontext.fireChannelActive();
    }

    private void safeClose(final ChannelHandlerContext channelhandlercontext, ChannelFuture channelfuture, final ChannelPromise channelpromise) {
        if (!channelhandlercontext.channel().isActive()) {
            channelhandlercontext.close(channelpromise);
        } else {
            final ScheduledFuture scheduledfuture;

            if (this.closeNotifyTimeoutMillis > 0L) {
                scheduledfuture = channelhandlercontext.executor().schedule(new Runnable() {
                    public void run() {
                        SslHandler.logger.warn(channelhandlercontext.channel() + " last write attempt timed out." + " Force-closing the connection.");
                        channelhandlercontext.close(channelpromise);
                    }
                }, this.closeNotifyTimeoutMillis, TimeUnit.MILLISECONDS);
            } else {
                scheduledfuture = null;
            }

            channelfuture.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture channelfuture1) throws Exception {
                    if (scheduledfuture != null) {
                        scheduledfuture.cancel(false);
                    }

                    if (channelhandlercontext.channel().isActive()) {
                        channelhandlercontext.close(channelpromise);
                    }

                }
            });
        }
    }

    static {
        SslHandler.SSLENGINE_CLOSED.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
        SslHandler.HANDSHAKE_TIMED_OUT.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
        SslHandler.CHANNEL_CLOSED.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
    }

    private final class LazyChannelPromise extends DefaultPromise<Channel> {

        private LazyChannelPromise() {}

        @Override
        protected EventExecutor executor() {
            if (SslHandler.this.ctx == null) {
                throw new IllegalStateException();
            } else {
                return SslHandler.this.ctx.executor();
            }
        }
    }
}
