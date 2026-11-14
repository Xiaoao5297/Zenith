package io.netty.handler.codec.spdy;

import io.netty.channel.ChannelPromise;
import io.netty.util.internal.PlatformDependent;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

final class SpdySession {

    private final Map<Integer, SpdySession.StreamState> activeStreams = PlatformDependent.newConcurrentHashMap();

    SpdySession() {}

    int numActiveStreams() {
        return this.activeStreams.size();
    }

    boolean noActiveStreams() {
        return this.activeStreams.isEmpty();
    }

    boolean isActiveStream(int i) {
        return this.activeStreams.containsKey(i);
    }

    Set<Integer> getActiveStreams() {
        TreeSet treeset = new TreeSet(new SpdySession.PriorityComparator());

        treeset.addAll(this.activeStreams.keySet());
        return treeset;
    }

    void acceptStream(int i, byte b0, boolean flag, boolean flag1, int j, int k) {
        if (!flag || !flag1) {
            this.activeStreams.put(i, new SpdySession.StreamState(b0, flag, flag1, j, k));
        }

    }

    void removeStream(int i, Throwable throwable) {
        SpdySession.StreamState spdysession_streamstate = (SpdySession.StreamState) this.activeStreams.remove(i);

        if (spdysession_streamstate != null) {
            spdysession_streamstate.clearPendingWrites(throwable);
        }

    }

    boolean isRemoteSideClosed(int i) {
        SpdySession.StreamState spdysession_streamstate = (SpdySession.StreamState) this.activeStreams.get(i);

        return spdysession_streamstate == null || spdysession_streamstate.isRemoteSideClosed();
    }

    void closeRemoteSide(int i) {
        SpdySession.StreamState spdysession_streamstate = (SpdySession.StreamState) this.activeStreams.get(i);

        if (spdysession_streamstate != null) {
            spdysession_streamstate.closeRemoteSide();
            if (spdysession_streamstate.isLocalSideClosed()) {
                this.activeStreams.remove(i);
            }
        }

    }

    boolean isLocalSideClosed(int i) {
        SpdySession.StreamState spdysession_streamstate = (SpdySession.StreamState) this.activeStreams.get(i);

        return spdysession_streamstate == null || spdysession_streamstate.isLocalSideClosed();
    }

    void closeLocalSide(int i) {
        SpdySession.StreamState spdysession_streamstate = (SpdySession.StreamState) this.activeStreams.get(i);

        if (spdysession_streamstate != null) {
            spdysession_streamstate.closeLocalSide();
            if (spdysession_streamstate.isRemoteSideClosed()) {
                this.activeStreams.remove(i);
            }
        }

    }

    boolean hasReceivedReply(int i) {
        SpdySession.StreamState spdysession_streamstate = (SpdySession.StreamState) this.activeStreams.get(i);

        return spdysession_streamstate != null && spdysession_streamstate.hasReceivedReply();
    }

    void receivedReply(int i) {
        SpdySession.StreamState spdysession_streamstate = (SpdySession.StreamState) this.activeStreams.get(i);

        if (spdysession_streamstate != null) {
            spdysession_streamstate.receivedReply();
        }

    }

    int getSendWindowSize(int i) {
        SpdySession.StreamState spdysession_streamstate = (SpdySession.StreamState) this.activeStreams.get(i);

        return spdysession_streamstate != null ? spdysession_streamstate.getSendWindowSize() : -1;
    }

    int updateSendWindowSize(int i, int j) {
        SpdySession.StreamState spdysession_streamstate = (SpdySession.StreamState) this.activeStreams.get(i);

        return spdysession_streamstate != null ? spdysession_streamstate.updateSendWindowSize(j) : -1;
    }

    int updateReceiveWindowSize(int i, int j) {
        SpdySession.StreamState spdysession_streamstate = (SpdySession.StreamState) this.activeStreams.get(i);

        if (j > 0) {
            spdysession_streamstate.setReceiveWindowSizeLowerBound(0);
        }

        return spdysession_streamstate != null ? spdysession_streamstate.updateReceiveWindowSize(j) : -1;
    }

    int getReceiveWindowSizeLowerBound(int i) {
        SpdySession.StreamState spdysession_streamstate = (SpdySession.StreamState) this.activeStreams.get(i);

        return spdysession_streamstate != null ? spdysession_streamstate.getReceiveWindowSizeLowerBound() : 0;
    }

    void updateAllSendWindowSizes(int i) {
        Iterator iterator = this.activeStreams.values().iterator();

        while (iterator.hasNext()) {
            SpdySession.StreamState spdysession_streamstate = (SpdySession.StreamState) iterator.next();

            spdysession_streamstate.updateSendWindowSize(i);
        }

    }

    void updateAllReceiveWindowSizes(int i) {
        Iterator iterator = this.activeStreams.values().iterator();

        while (iterator.hasNext()) {
            SpdySession.StreamState spdysession_streamstate = (SpdySession.StreamState) iterator.next();

            spdysession_streamstate.updateReceiveWindowSize(i);
            if (i < 0) {
                spdysession_streamstate.setReceiveWindowSizeLowerBound(i);
            }
        }

    }

    boolean putPendingWrite(int i, SpdySession.PendingWrite spdysession_pendingwrite) {
        SpdySession.StreamState spdysession_streamstate = (SpdySession.StreamState) this.activeStreams.get(i);

        return spdysession_streamstate != null && spdysession_streamstate.putPendingWrite(spdysession_pendingwrite);
    }

    SpdySession.PendingWrite getPendingWrite(int i) {
        SpdySession.StreamState spdysession_streamstate = (SpdySession.StreamState) this.activeStreams.get(i);

        return spdysession_streamstate != null ? spdysession_streamstate.getPendingWrite() : null;
    }

    SpdySession.PendingWrite removePendingWrite(int i) {
        SpdySession.StreamState spdysession_streamstate = (SpdySession.StreamState) this.activeStreams.get(i);

        return spdysession_streamstate != null ? spdysession_streamstate.removePendingWrite() : null;
    }

    public static final class PendingWrite {

        final SpdyDataFrame spdyDataFrame;
        final ChannelPromise promise;

        PendingWrite(SpdyDataFrame spdydataframe, ChannelPromise channelpromise) {
            this.spdyDataFrame = spdydataframe;
            this.promise = channelpromise;
        }

        void fail(Throwable throwable) {
            this.spdyDataFrame.release();
            this.promise.setFailure(throwable);
        }
    }

    private final class PriorityComparator implements Comparator<Integer> {

        private PriorityComparator() {}

        public int compare(Integer integer, Integer integer1) {
            SpdySession.StreamState spdysession_streamstate = (SpdySession.StreamState) SpdySession.this.activeStreams.get(integer);
            SpdySession.StreamState spdysession_streamstate1 = (SpdySession.StreamState) SpdySession.this.activeStreams.get(integer1);

            return spdysession_streamstate.getPriority() - spdysession_streamstate1.getPriority();
        }
    }

    private static final class StreamState {

        private final byte priority;
        private boolean remoteSideClosed;
        private boolean localSideClosed;
        private boolean receivedReply;
        private final AtomicInteger sendWindowSize;
        private final AtomicInteger receiveWindowSize;
        private int receiveWindowSizeLowerBound;
        private final Queue<SpdySession.PendingWrite> pendingWriteQueue = new ConcurrentLinkedQueue();

        StreamState(byte b0, boolean flag, boolean flag1, int i, int j) {
            this.priority = b0;
            this.remoteSideClosed = flag;
            this.localSideClosed = flag1;
            this.sendWindowSize = new AtomicInteger(i);
            this.receiveWindowSize = new AtomicInteger(j);
        }

        byte getPriority() {
            return this.priority;
        }

        boolean isRemoteSideClosed() {
            return this.remoteSideClosed;
        }

        void closeRemoteSide() {
            this.remoteSideClosed = true;
        }

        boolean isLocalSideClosed() {
            return this.localSideClosed;
        }

        void closeLocalSide() {
            this.localSideClosed = true;
        }

        boolean hasReceivedReply() {
            return this.receivedReply;
        }

        void receivedReply() {
            this.receivedReply = true;
        }

        int getSendWindowSize() {
            return this.sendWindowSize.get();
        }

        int updateSendWindowSize(int i) {
            return this.sendWindowSize.addAndGet(i);
        }

        int updateReceiveWindowSize(int i) {
            return this.receiveWindowSize.addAndGet(i);
        }

        int getReceiveWindowSizeLowerBound() {
            return this.receiveWindowSizeLowerBound;
        }

        void setReceiveWindowSizeLowerBound(int i) {
            this.receiveWindowSizeLowerBound = i;
        }

        boolean putPendingWrite(SpdySession.PendingWrite spdysession_pendingwrite) {
            return this.pendingWriteQueue.offer(spdysession_pendingwrite);
        }

        SpdySession.PendingWrite getPendingWrite() {
            return (SpdySession.PendingWrite) this.pendingWriteQueue.peek();
        }

        SpdySession.PendingWrite removePendingWrite() {
            return (SpdySession.PendingWrite) this.pendingWriteQueue.poll();
        }

        void clearPendingWrites(Throwable throwable) {
            while (true) {
                SpdySession.PendingWrite spdysession_pendingwrite = (SpdySession.PendingWrite) this.pendingWriteQueue.poll();

                if (spdysession_pendingwrite == null) {
                    return;
                }

                spdysession_pendingwrite.fail(throwable);
            }
        }
    }
}
