package io.netty.channel;

public final class ChannelMetadata {

    private final boolean hasDisconnect;

    public ChannelMetadata(boolean flag) {
        this.hasDisconnect = flag;
    }

    public boolean hasDisconnect() {
        return this.hasDisconnect;
    }
}
