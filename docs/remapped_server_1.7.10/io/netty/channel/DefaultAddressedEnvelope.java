package io.netty.channel;

import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.StringUtil;
import java.net.SocketAddress;

public class DefaultAddressedEnvelope<M, A extends SocketAddress> implements AddressedEnvelope<M, A> {

    private final M message;
    private final A sender;
    private final A recipient;

    public DefaultAddressedEnvelope(M m0, A a0, A a1) {
        if (m0 == null) {
            throw new NullPointerException("message");
        } else {
            this.message = m0;
            this.sender = a1;
            this.recipient = a0;
        }
    }

    public DefaultAddressedEnvelope(M m0, A a0) {
        this(m0, a0, (SocketAddress) null);
    }

    @Override
    public M content() {
        return this.message;
    }

    @Override
    public A sender() {
        return this.sender;
    }

    @Override
    public A recipient() {
        return this.recipient;
    }

    @Override
    public int refCnt() {
        return this.message instanceof ReferenceCounted ? ((ReferenceCounted) this.message).refCnt() : 1;
    }

    @Override
    public AddressedEnvelope<M, A> retain() {
        ReferenceCountUtil.retain(this.message);
        return this;
    }

    @Override
    public AddressedEnvelope<M, A> retain(int i) {
        ReferenceCountUtil.retain(this.message, i);
        return this;
    }

    @Override
    public boolean release() {
        return ReferenceCountUtil.release(this.message);
    }

    @Override
    public boolean release(int i) {
        return ReferenceCountUtil.release(this.message, i);
    }

    public String toString() {
        return this.sender != null ? StringUtil.simpleClassName((Object) this) + '(' + this.sender + " => " + this.recipient + ", " + this.message + ')' : StringUtil.simpleClassName((Object) this) + "(=> " + this.recipient + ", " + this.message + ')';
    }
}
