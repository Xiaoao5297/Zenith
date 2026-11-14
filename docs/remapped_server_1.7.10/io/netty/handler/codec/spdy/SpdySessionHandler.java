package io.netty.handler.codec.spdy;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.EmptyArrays;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class SpdySessionHandler extends ChannelDuplexHandler {

    private static final SpdyProtocolException PROTOCOL_EXCEPTION = new SpdyProtocolException();
    private static final SpdyProtocolException STREAM_CLOSED = new SpdyProtocolException("Stream closed");
    private final SpdySession spdySession = new SpdySession();
    private int lastGoodStreamId;
    private static final int DEFAULT_MAX_CONCURRENT_STREAMS = Integer.MAX_VALUE;
    private int remoteConcurrentStreams = Integer.MAX_VALUE;
    private int localConcurrentStreams = Integer.MAX_VALUE;
    private int maxConcurrentStreams = Integer.MAX_VALUE;
    private static final int DEFAULT_WINDOW_SIZE = 65536;
    private int initialSendWindowSize = 65536;
    private int initialReceiveWindowSize = 65536;
    private final Object flowControlLock = new Object();
    private final AtomicInteger pings = new AtomicInteger();
    private boolean sentGoAwayFrame;
    private boolean receivedGoAwayFrame;
    private ChannelFutureListener closeSessionFutureListener;
    private final boolean server;
    private final boolean flowControl;

    public SpdySessionHandler(int i, boolean flag) {
        if (i >= 2 && i <= 3) {
            this.server = flag;
            this.flowControl = i >= 3;
        } else {
            throw new IllegalArgumentException("unsupported version: " + i);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext channelhandlercontext, Object object) throws Exception {
        int i;
        int j;

        if (object instanceof SpdyDataFrame) {
            SpdyDataFrame spdydataframe = (SpdyDataFrame) object;

            i = spdydataframe.getStreamId();
            if (!this.spdySession.isActiveStream(i)) {
                spdydataframe.release();
                if (i <= this.lastGoodStreamId) {
                    this.issueStreamError(channelhandlercontext, i, SpdyStreamStatus.PROTOCOL_ERROR);
                } else if (!this.sentGoAwayFrame) {
                    this.issueStreamError(channelhandlercontext, i, SpdyStreamStatus.INVALID_STREAM);
                }

                return;
            }

            if (this.spdySession.isRemoteSideClosed(i)) {
                spdydataframe.release();
                this.issueStreamError(channelhandlercontext, i, SpdyStreamStatus.STREAM_ALREADY_CLOSED);
                return;
            }

            if (!this.isRemoteInitiatedID(i) && !this.spdySession.hasReceivedReply(i)) {
                spdydataframe.release();
                this.issueStreamError(channelhandlercontext, i, SpdyStreamStatus.PROTOCOL_ERROR);
                return;
            }

            if (this.flowControl) {
                j = -1 * spdydataframe.content().readableBytes();
                int k = this.spdySession.updateReceiveWindowSize(i, j);

                if (k < this.spdySession.getReceiveWindowSizeLowerBound(i)) {
                    spdydataframe.release();
                    this.issueStreamError(channelhandlercontext, i, SpdyStreamStatus.FLOW_CONTROL_ERROR);
                    return;
                }

                if (k < 0) {
                    while (spdydataframe.content().readableBytes() > this.initialReceiveWindowSize) {
                        DefaultSpdyDataFrame defaultspdydataframe = new DefaultSpdyDataFrame(i, spdydataframe.content().readSlice(this.initialReceiveWindowSize).retain());

                        channelhandlercontext.writeAndFlush(defaultspdydataframe);
                    }
                }

                if (k <= this.initialReceiveWindowSize / 2 && !spdydataframe.isLast()) {
                    j = this.initialReceiveWindowSize - k;
                    this.spdySession.updateReceiveWindowSize(i, j);
                    DefaultSpdyWindowUpdateFrame defaultspdywindowupdateframe = new DefaultSpdyWindowUpdateFrame(i, j);

                    channelhandlercontext.writeAndFlush(defaultspdywindowupdateframe);
                }
            }

            if (spdydataframe.isLast()) {
                this.halfCloseStream(i, true, channelhandlercontext.newSucceededFuture());
            }
        } else if (object instanceof SpdySynStreamFrame) {
            SpdySynStreamFrame spdysynstreamframe = (SpdySynStreamFrame) object;

            i = spdysynstreamframe.getStreamId();
            if (spdysynstreamframe.isInvalid() || !this.isRemoteInitiatedID(i) || this.spdySession.isActiveStream(i)) {
                this.issueStreamError(channelhandlercontext, i, SpdyStreamStatus.PROTOCOL_ERROR);
                return;
            }

            if (i <= this.lastGoodStreamId) {
                this.issueSessionError(channelhandlercontext, SpdySessionStatus.PROTOCOL_ERROR);
                return;
            }

            byte b0 = spdysynstreamframe.getPriority();
            boolean flag = spdysynstreamframe.isLast();
            boolean flag1 = spdysynstreamframe.isUnidirectional();

            if (!this.acceptStream(i, b0, flag, flag1)) {
                this.issueStreamError(channelhandlercontext, i, SpdyStreamStatus.REFUSED_STREAM);
                return;
            }
        } else if (object instanceof SpdySynReplyFrame) {
            SpdySynReplyFrame spdysynreplyframe = (SpdySynReplyFrame) object;

            i = spdysynreplyframe.getStreamId();
            if (spdysynreplyframe.isInvalid() || this.isRemoteInitiatedID(i) || this.spdySession.isRemoteSideClosed(i)) {
                this.issueStreamError(channelhandlercontext, i, SpdyStreamStatus.INVALID_STREAM);
                return;
            }

            if (this.spdySession.hasReceivedReply(i)) {
                this.issueStreamError(channelhandlercontext, i, SpdyStreamStatus.STREAM_IN_USE);
                return;
            }

            this.spdySession.receivedReply(i);
            if (spdysynreplyframe.isLast()) {
                this.halfCloseStream(i, true, channelhandlercontext.newSucceededFuture());
            }
        } else if (object instanceof SpdyRstStreamFrame) {
            SpdyRstStreamFrame spdyrststreamframe = (SpdyRstStreamFrame) object;

            this.removeStream(spdyrststreamframe.getStreamId(), channelhandlercontext.newSucceededFuture());
        } else if (object instanceof SpdySettingsFrame) {
            SpdySettingsFrame spdysettingsframe = (SpdySettingsFrame) object;

            i = spdysettingsframe.getValue(4);
            if (i >= 0) {
                this.updateConcurrentStreams(i, true);
            }

            if (spdysettingsframe.isPersisted(7)) {
                spdysettingsframe.removeValue(7);
            }

            spdysettingsframe.setPersistValue(7, false);
            if (this.flowControl) {
                j = spdysettingsframe.getValue(7);
                if (j >= 0) {
                    this.updateInitialSendWindowSize(j);
                }
            }
        } else if (object instanceof SpdyPingFrame) {
            SpdyPingFrame spdypingframe = (SpdyPingFrame) object;

            if (this.isRemoteInitiatedID(spdypingframe.getId())) {
                channelhandlercontext.writeAndFlush(spdypingframe);
                return;
            }

            if (this.pings.get() == 0) {
                return;
            }

            this.pings.getAndDecrement();
        } else if (object instanceof SpdyGoAwayFrame) {
            this.receivedGoAwayFrame = true;
        } else if (object instanceof SpdyHeadersFrame) {
            SpdyHeadersFrame spdyheadersframe = (SpdyHeadersFrame) object;

            i = spdyheadersframe.getStreamId();
            if (spdyheadersframe.isInvalid()) {
                this.issueStreamError(channelhandlercontext, i, SpdyStreamStatus.PROTOCOL_ERROR);
                return;
            }

            if (this.spdySession.isRemoteSideClosed(i)) {
                this.issueStreamError(channelhandlercontext, i, SpdyStreamStatus.INVALID_STREAM);
                return;
            }

            if (spdyheadersframe.isLast()) {
                this.halfCloseStream(i, true, channelhandlercontext.newSucceededFuture());
            }
        } else if (object instanceof SpdyWindowUpdateFrame && this.flowControl) {
            SpdyWindowUpdateFrame spdywindowupdateframe = (SpdyWindowUpdateFrame) object;

            i = spdywindowupdateframe.getStreamId();
            j = spdywindowupdateframe.getDeltaWindowSize();
            if (this.spdySession.isLocalSideClosed(i)) {
                return;
            }

            if (this.spdySession.getSendWindowSize(i) > Integer.MAX_VALUE - j) {
                this.issueStreamError(channelhandlercontext, i, SpdyStreamStatus.FLOW_CONTROL_ERROR);
                return;
            }

            this.updateSendWindowSize(channelhandlercontext, i, j);
        }

        channelhandlercontext.fireChannelRead(object);
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelhandlercontext) throws Exception {
        Iterator iterator = this.spdySession.getActiveStreams().iterator();

        while (iterator.hasNext()) {
            Integer integer = (Integer) iterator.next();

            this.removeStream(integer, channelhandlercontext.newSucceededFuture());
        }

        channelhandlercontext.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) throws Exception {
        if (throwable instanceof SpdyProtocolException) {
            this.issueSessionError(channelhandlercontext, SpdySessionStatus.PROTOCOL_ERROR);
        }

        channelhandlercontext.fireExceptionCaught(throwable);
    }

    @Override
    public void close(ChannelHandlerContext channelhandlercontext, ChannelPromise channelpromise) throws Exception {
        this.sendGoAwayFrame(channelhandlercontext, channelpromise);
    }

    @Override
    public void write(ChannelHandlerContext channelhandlercontext, Object object, ChannelPromise channelpromise) throws Exception {
        if (!(object instanceof SpdyDataFrame) && !(object instanceof SpdySynStreamFrame) && !(object instanceof SpdySynReplyFrame) && !(object instanceof SpdyRstStreamFrame) && !(object instanceof SpdySettingsFrame) && !(object instanceof SpdyPingFrame) && !(object instanceof SpdyGoAwayFrame) && !(object instanceof SpdyHeadersFrame) && !(object instanceof SpdyWindowUpdateFrame)) {
            channelhandlercontext.write(object, channelpromise);
        } else {
            this.handleOutboundMessage(channelhandlercontext, object, channelpromise);
        }

    }

    private void handleOutboundMessage(final ChannelHandlerContext channelhandlercontext, Object object, ChannelPromise channelpromise) throws Exception {
        final int i;

        if (object instanceof SpdyDataFrame) {
            SpdyDataFrame spdydataframe = (SpdyDataFrame) object;

            i = spdydataframe.getStreamId();
            if (this.spdySession.isLocalSideClosed(i)) {
                spdydataframe.release();
                channelpromise.setFailure(SpdySessionHandler.PROTOCOL_EXCEPTION);
                return;
            }

            if (this.flowControl) {
                Object object1 = this.flowControlLock;

                synchronized (this.flowControlLock) {
                    int j = spdydataframe.content().readableBytes();
                    int k = this.spdySession.getSendWindowSize(i);

                    if (k <= 0) {
                        this.spdySession.putPendingWrite(i, new SpdySession.PendingWrite(spdydataframe, channelpromise));
                        return;
                    }

                    if (k < j) {
                        this.spdySession.updateSendWindowSize(i, -1 * k);
                        DefaultSpdyDataFrame defaultspdydataframe = new DefaultSpdyDataFrame(i, spdydataframe.content().readSlice(k).retain());

                        this.spdySession.putPendingWrite(i, new SpdySession.PendingWrite(spdydataframe, channelpromise));
                        channelhandlercontext.write(defaultspdydataframe).addListener(new ChannelFutureListener() {
                            public void operationComplete(ChannelFuture channelfuture) throws Exception {
                                if (!channelfuture.isSuccess()) {
                                    SpdySessionHandler.this.issueStreamError(channelhandlercontext, i, SpdyStreamStatus.INTERNAL_ERROR);
                                }

                            }
                        });
                        return;
                    }

                    this.spdySession.updateSendWindowSize(i, -1 * j);
                    channelpromise.addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture channelfuture) throws Exception {
                            if (!channelfuture.isSuccess()) {
                                SpdySessionHandler.this.issueStreamError(channelhandlercontext, i, SpdyStreamStatus.INTERNAL_ERROR);
                            }

                        }
                    });
                }
            }

            if (spdydataframe.isLast()) {
                this.halfCloseStream(i, false, channelpromise);
            }
        } else {
            int l;

            if (object instanceof SpdySynStreamFrame) {
                SpdySynStreamFrame spdysynstreamframe = (SpdySynStreamFrame) object;

                i = spdysynstreamframe.getStreamId();
                if (this.isRemoteInitiatedID(i)) {
                    channelpromise.setFailure(SpdySessionHandler.PROTOCOL_EXCEPTION);
                    return;
                }

                l = spdysynstreamframe.getPriority();
                boolean flag = spdysynstreamframe.isUnidirectional();
                boolean flag1 = spdysynstreamframe.isLast();

                if (!this.acceptStream(i, (byte) l, flag, flag1)) {
                    channelpromise.setFailure(SpdySessionHandler.PROTOCOL_EXCEPTION);
                    return;
                }
            } else if (object instanceof SpdySynReplyFrame) {
                SpdySynReplyFrame spdysynreplyframe = (SpdySynReplyFrame) object;

                i = spdysynreplyframe.getStreamId();
                if (!this.isRemoteInitiatedID(i) || this.spdySession.isLocalSideClosed(i)) {
                    channelpromise.setFailure(SpdySessionHandler.PROTOCOL_EXCEPTION);
                    return;
                }

                if (spdysynreplyframe.isLast()) {
                    this.halfCloseStream(i, false, channelpromise);
                }
            } else if (object instanceof SpdyRstStreamFrame) {
                SpdyRstStreamFrame spdyrststreamframe = (SpdyRstStreamFrame) object;

                this.removeStream(spdyrststreamframe.getStreamId(), channelpromise);
            } else if (object instanceof SpdySettingsFrame) {
                SpdySettingsFrame spdysettingsframe = (SpdySettingsFrame) object;

                i = spdysettingsframe.getValue(4);
                if (i >= 0) {
                    this.updateConcurrentStreams(i, false);
                }

                if (spdysettingsframe.isPersisted(7)) {
                    spdysettingsframe.removeValue(7);
                }

                spdysettingsframe.setPersistValue(7, false);
                if (this.flowControl) {
                    l = spdysettingsframe.getValue(7);
                    if (l >= 0) {
                        this.updateInitialReceiveWindowSize(l);
                    }
                }
            } else if (object instanceof SpdyPingFrame) {
                SpdyPingFrame spdypingframe = (SpdyPingFrame) object;

                if (this.isRemoteInitiatedID(spdypingframe.getId())) {
                    channelhandlercontext.fireExceptionCaught(new IllegalArgumentException("invalid PING ID: " + spdypingframe.getId()));
                    return;
                }

                this.pings.getAndIncrement();
            } else {
                if (object instanceof SpdyGoAwayFrame) {
                    channelpromise.setFailure(SpdySessionHandler.PROTOCOL_EXCEPTION);
                    return;
                }

                if (object instanceof SpdyHeadersFrame) {
                    SpdyHeadersFrame spdyheadersframe = (SpdyHeadersFrame) object;

                    i = spdyheadersframe.getStreamId();
                    if (this.spdySession.isLocalSideClosed(i)) {
                        channelpromise.setFailure(SpdySessionHandler.PROTOCOL_EXCEPTION);
                        return;
                    }

                    if (spdyheadersframe.isLast()) {
                        this.halfCloseStream(i, false, channelpromise);
                    }
                } else if (object instanceof SpdyWindowUpdateFrame) {
                    channelpromise.setFailure(SpdySessionHandler.PROTOCOL_EXCEPTION);
                    return;
                }
            }
        }

        channelhandlercontext.write(object, channelpromise);
    }

    private void issueSessionError(ChannelHandlerContext channelhandlercontext, SpdySessionStatus spdysessionstatus) {
        this.sendGoAwayFrame(channelhandlercontext, spdysessionstatus).addListener(new SpdySessionHandler.ClosingChannelFutureListener(channelhandlercontext, channelhandlercontext.newPromise()));
    }

    private void issueStreamError(ChannelHandlerContext channelhandlercontext, int i, SpdyStreamStatus spdystreamstatus) {
        boolean flag = !this.spdySession.isRemoteSideClosed(i);
        ChannelPromise channelpromise = channelhandlercontext.newPromise();

        this.removeStream(i, channelpromise);
        DefaultSpdyRstStreamFrame defaultspdyrststreamframe = new DefaultSpdyRstStreamFrame(i, spdystreamstatus);

        channelhandlercontext.writeAndFlush(defaultspdyrststreamframe, channelpromise);
        if (flag) {
            channelhandlercontext.fireChannelRead(defaultspdyrststreamframe);
        }

    }

    private boolean isRemoteInitiatedID(int i) {
        boolean flag = SpdyCodecUtil.isServerId(i);

        return this.server && !flag || !this.server && flag;
    }

    private void updateConcurrentStreams(int i, boolean flag) {
        if (flag) {
            this.remoteConcurrentStreams = i;
        } else {
            this.localConcurrentStreams = i;
        }

        this.maxConcurrentStreams = Math.min(this.localConcurrentStreams, this.remoteConcurrentStreams);
    }

    private synchronized void updateInitialSendWindowSize(int i) {
        int j = i - this.initialSendWindowSize;

        this.initialSendWindowSize = i;
        this.spdySession.updateAllSendWindowSizes(j);
    }

    private synchronized void updateInitialReceiveWindowSize(int i) {
        int j = i - this.initialReceiveWindowSize;

        this.initialReceiveWindowSize = i;
        this.spdySession.updateAllReceiveWindowSizes(j);
    }

    private synchronized boolean acceptStream(int i, byte b0, boolean flag, boolean flag1) {
        if (!this.receivedGoAwayFrame && !this.sentGoAwayFrame) {
            if (this.spdySession.numActiveStreams() >= this.maxConcurrentStreams) {
                return false;
            } else {
                this.spdySession.acceptStream(i, b0, flag, flag1, this.initialSendWindowSize, this.initialReceiveWindowSize);
                if (this.isRemoteInitiatedID(i)) {
                    this.lastGoodStreamId = i;
                }

                return true;
            }
        } else {
            return false;
        }
    }

    private void halfCloseStream(int i, boolean flag, ChannelFuture channelfuture) {
        if (flag) {
            this.spdySession.closeRemoteSide(i);
        } else {
            this.spdySession.closeLocalSide(i);
        }

        if (this.closeSessionFutureListener != null && this.spdySession.noActiveStreams()) {
            channelfuture.addListener(this.closeSessionFutureListener);
        }

    }

    private void removeStream(int i, ChannelFuture channelfuture) {
        this.spdySession.removeStream(i, SpdySessionHandler.STREAM_CLOSED);
        if (this.closeSessionFutureListener != null && this.spdySession.noActiveStreams()) {
            channelfuture.addListener(this.closeSessionFutureListener);
        }

    }

    private void updateSendWindowSize(final ChannelHandlerContext channelhandlercontext, final int i, int j) {
        Object object = this.flowControlLock;

        synchronized (this.flowControlLock) {
            int k = this.spdySession.updateSendWindowSize(i, j);

            while (k > 0) {
                SpdySession.PendingWrite spdysession_pendingwrite = this.spdySession.getPendingWrite(i);

                if (spdysession_pendingwrite == null) {
                    break;
                }

                SpdyDataFrame spdydataframe = spdysession_pendingwrite.spdyDataFrame;
                int l = spdydataframe.content().readableBytes();

                if (k >= l) {
                    this.spdySession.removePendingWrite(i);
                    k = this.spdySession.updateSendWindowSize(i, -1 * l);
                    if (spdydataframe.isLast()) {
                        this.halfCloseStream(i, false, spdysession_pendingwrite.promise);
                    }

                    channelhandlercontext.writeAndFlush(spdydataframe, spdysession_pendingwrite.promise).addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture channelfuture) throws Exception {
                            if (!channelfuture.isSuccess()) {
                                SpdySessionHandler.this.issueStreamError(channelhandlercontext, i, SpdyStreamStatus.INTERNAL_ERROR);
                            }

                        }
                    });
                } else {
                    this.spdySession.updateSendWindowSize(i, -1 * k);
                    DefaultSpdyDataFrame defaultspdydataframe = new DefaultSpdyDataFrame(i, spdydataframe.content().readSlice(k).retain());

                    channelhandlercontext.writeAndFlush(defaultspdydataframe).addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture channelfuture) throws Exception {
                            if (!channelfuture.isSuccess()) {
                                SpdySessionHandler.this.issueStreamError(channelhandlercontext, i, SpdyStreamStatus.INTERNAL_ERROR);
                            }

                        }
                    });
                    k = 0;
                }
            }

        }
    }

    private void sendGoAwayFrame(ChannelHandlerContext channelhandlercontext, ChannelPromise channelpromise) {
        if (!channelhandlercontext.channel().isActive()) {
            channelhandlercontext.close(channelpromise);
        } else {
            ChannelFuture channelfuture = this.sendGoAwayFrame(channelhandlercontext, SpdySessionStatus.OK);

            if (this.spdySession.noActiveStreams()) {
                channelfuture.addListener(new SpdySessionHandler.ClosingChannelFutureListener(channelhandlercontext, channelpromise));
            } else {
                this.closeSessionFutureListener = new SpdySessionHandler.ClosingChannelFutureListener(channelhandlercontext, channelpromise);
            }

        }
    }

    private synchronized ChannelFuture sendGoAwayFrame(ChannelHandlerContext channelhandlercontext, SpdySessionStatus spdysessionstatus) {
        if (!this.sentGoAwayFrame) {
            this.sentGoAwayFrame = true;
            DefaultSpdyGoAwayFrame defaultspdygoawayframe = new DefaultSpdyGoAwayFrame(this.lastGoodStreamId, spdysessionstatus);

            return channelhandlercontext.writeAndFlush(defaultspdygoawayframe);
        } else {
            return channelhandlercontext.newSucceededFuture();
        }
    }

    static {
        SpdySessionHandler.PROTOCOL_EXCEPTION.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
        SpdySessionHandler.STREAM_CLOSED.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
    }

    private static final class ClosingChannelFutureListener implements ChannelFutureListener {

        private final ChannelHandlerContext ctx;
        private final ChannelPromise promise;

        ClosingChannelFutureListener(ChannelHandlerContext channelhandlercontext, ChannelPromise channelpromise) {
            this.ctx = channelhandlercontext;
            this.promise = channelpromise;
        }

        public void operationComplete(ChannelFuture channelfuture) throws Exception {
            this.ctx.close(this.promise);
        }
    }
}
