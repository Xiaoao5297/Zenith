package io.netty.channel;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

public final class ChannelFlushPromiseNotifier {

    private long writeCounter;
    private final Queue<ChannelFlushPromiseNotifier.FlushCheckpoint> flushCheckpoints;
    private final boolean tryNotify;

    public ChannelFlushPromiseNotifier(boolean flag) {
        this.flushCheckpoints = new ArrayDeque();
        this.tryNotify = flag;
    }

    public ChannelFlushPromiseNotifier() {
        this(false);
    }

    public ChannelFlushPromiseNotifier add(ChannelPromise channelpromise, int i) {
        if (channelpromise == null) {
            throw new NullPointerException("promise");
        } else if (i < 0) {
            throw new IllegalArgumentException("pendingDataSize must be >= 0 but was" + i);
        } else {
            long j = this.writeCounter + (long) i;

            if (channelpromise instanceof ChannelFlushPromiseNotifier.FlushCheckpoint) {
                ChannelFlushPromiseNotifier.FlushCheckpoint channelflushpromisenotifier_flushcheckpoint = (ChannelFlushPromiseNotifier.FlushCheckpoint) channelpromise;

                channelflushpromisenotifier_flushcheckpoint.flushCheckpoint(j);
                this.flushCheckpoints.add(channelflushpromisenotifier_flushcheckpoint);
            } else {
                this.flushCheckpoints.add(new ChannelFlushPromiseNotifier.DefaultFlushCheckpoint(j, channelpromise));
            }

            return this;
        }
    }

    public ChannelFlushPromiseNotifier increaseWriteCounter(long i) {
        if (i < 0L) {
            throw new IllegalArgumentException("delta must be >= 0 but was" + i);
        } else {
            this.writeCounter += i;
            return this;
        }
    }

    public long writeCounter() {
        return this.writeCounter;
    }

    public ChannelFlushPromiseNotifier notifyFlushFutures() {
        this.notifyFlushFutures0((Throwable) null);
        return this;
    }

    public ChannelFlushPromiseNotifier notifyFlushFutures(Throwable throwable) {
        this.notifyFlushFutures();

        while (true) {
            ChannelFlushPromiseNotifier.FlushCheckpoint channelflushpromisenotifier_flushcheckpoint = (ChannelFlushPromiseNotifier.FlushCheckpoint) this.flushCheckpoints.poll();

            if (channelflushpromisenotifier_flushcheckpoint == null) {
                return this;
            }

            if (this.tryNotify) {
                channelflushpromisenotifier_flushcheckpoint.promise().tryFailure(throwable);
            } else {
                channelflushpromisenotifier_flushcheckpoint.promise().setFailure(throwable);
            }
        }
    }

    public ChannelFlushPromiseNotifier notifyFlushFutures(Throwable throwable, Throwable throwable1) {
        this.notifyFlushFutures0(throwable);

        while (true) {
            ChannelFlushPromiseNotifier.FlushCheckpoint channelflushpromisenotifier_flushcheckpoint = (ChannelFlushPromiseNotifier.FlushCheckpoint) this.flushCheckpoints.poll();

            if (channelflushpromisenotifier_flushcheckpoint == null) {
                return this;
            }

            if (this.tryNotify) {
                channelflushpromisenotifier_flushcheckpoint.promise().tryFailure(throwable1);
            } else {
                channelflushpromisenotifier_flushcheckpoint.promise().setFailure(throwable1);
            }
        }
    }

    private void notifyFlushFutures0(Throwable throwable) {
        if (this.flushCheckpoints.isEmpty()) {
            this.writeCounter = 0L;
        } else {
            long i = this.writeCounter;

            while (true) {
                ChannelFlushPromiseNotifier.FlushCheckpoint channelflushpromisenotifier_flushcheckpoint = (ChannelFlushPromiseNotifier.FlushCheckpoint) this.flushCheckpoints.peek();

                if (channelflushpromisenotifier_flushcheckpoint == null) {
                    this.writeCounter = 0L;
                    break;
                }

                if (channelflushpromisenotifier_flushcheckpoint.flushCheckpoint() > i) {
                    if (i > 0L && this.flushCheckpoints.size() == 1) {
                        this.writeCounter = 0L;
                        channelflushpromisenotifier_flushcheckpoint.flushCheckpoint(channelflushpromisenotifier_flushcheckpoint.flushCheckpoint() - i);
                    }
                    break;
                }

                this.flushCheckpoints.remove();
                if (throwable == null) {
                    if (this.tryNotify) {
                        channelflushpromisenotifier_flushcheckpoint.promise().trySuccess();
                    } else {
                        channelflushpromisenotifier_flushcheckpoint.promise().setSuccess();
                    }
                } else if (this.tryNotify) {
                    channelflushpromisenotifier_flushcheckpoint.promise().tryFailure(throwable);
                } else {
                    channelflushpromisenotifier_flushcheckpoint.promise().setFailure(throwable);
                }
            }

            long j = this.writeCounter;

            if (j >= 549755813888L) {
                this.writeCounter = 0L;
                Iterator iterator = this.flushCheckpoints.iterator();

                while (iterator.hasNext()) {
                    ChannelFlushPromiseNotifier.FlushCheckpoint channelflushpromisenotifier_flushcheckpoint1 = (ChannelFlushPromiseNotifier.FlushCheckpoint) iterator.next();

                    channelflushpromisenotifier_flushcheckpoint1.flushCheckpoint(channelflushpromisenotifier_flushcheckpoint1.flushCheckpoint() - j);
                }
            }

        }
    }

    private static class DefaultFlushCheckpoint implements ChannelFlushPromiseNotifier.FlushCheckpoint {

        private long checkpoint;
        private final ChannelPromise future;

        DefaultFlushCheckpoint(long i, ChannelPromise channelpromise) {
            this.checkpoint = i;
            this.future = channelpromise;
        }

        @Override
        public long flushCheckpoint() {
            return this.checkpoint;
        }

        @Override
        public void flushCheckpoint(long i) {
            this.checkpoint = i;
        }

        @Override
        public ChannelPromise promise() {
            return this.future;
        }
    }

    interface FlushCheckpoint {

        long flushCheckpoint();

        void flushCheckpoint(long i);

        ChannelPromise promise();
    }
}
