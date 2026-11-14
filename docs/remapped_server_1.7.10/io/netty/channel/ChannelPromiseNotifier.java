package io.netty.channel;

public final class ChannelPromiseNotifier implements ChannelFutureListener {

    private final ChannelPromise[] promises;

    public ChannelPromiseNotifier(ChannelPromise... achannelpromise) {
        if (achannelpromise == null) {
            throw new NullPointerException("promises");
        } else {
            ChannelPromise[] achannelpromise1 = achannelpromise;
            int i = achannelpromise.length;

            for (int j = 0; j < i; ++j) {
                ChannelPromise channelpromise = achannelpromise1[j];

                if (channelpromise == null) {
                    throw new IllegalArgumentException("promises contains null ChannelPromise");
                }
            }

            this.promises = (ChannelPromise[]) achannelpromise.clone();
        }
    }

    public void operationComplete(ChannelFuture channelfuture) throws Exception {
        int i;

        if (channelfuture.isSuccess()) {
            ChannelPromise[] achannelpromise = this.promises;
            int j = achannelpromise.length;

            for (i = 0; i < j; ++i) {
                ChannelPromise channelpromise = achannelpromise[i];

                channelpromise.setSuccess();
            }

        } else {
            Throwable throwable = channelfuture.cause();
            ChannelPromise[] achannelpromise1 = this.promises;

            i = achannelpromise1.length;

            for (int k = 0; k < i; ++k) {
                ChannelPromise channelpromise1 = achannelpromise1[k];

                channelpromise1.setFailure(throwable);
            }

        }
    }
}
