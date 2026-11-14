package io.netty.channel.sctp;

import com.sun.nio.sctp.MessageInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.DefaultByteBufHolder;

public final class SctpMessage extends DefaultByteBufHolder {

    private final int streamIdentifier;
    private final int protocolIdentifier;
    private final MessageInfo msgInfo;

    public SctpMessage(int i, int j, ByteBuf bytebuf) {
        super(bytebuf);
        this.protocolIdentifier = i;
        this.streamIdentifier = j;
        this.msgInfo = null;
    }

    public SctpMessage(MessageInfo messageinfo, ByteBuf bytebuf) {
        super(bytebuf);
        if (messageinfo == null) {
            throw new NullPointerException("msgInfo");
        } else {
            this.msgInfo = messageinfo;
            this.streamIdentifier = messageinfo.streamNumber();
            this.protocolIdentifier = messageinfo.payloadProtocolID();
        }
    }

    public int streamIdentifier() {
        return this.streamIdentifier;
    }

    public int protocolIdentifier() {
        return this.protocolIdentifier;
    }

    public MessageInfo messageInfo() {
        return this.msgInfo;
    }

    public boolean isComplete() {
        return this.msgInfo != null ? this.msgInfo.isComplete() : true;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            SctpMessage sctpmessage = (SctpMessage) object;

            return this.protocolIdentifier != sctpmessage.protocolIdentifier ? false : (this.streamIdentifier != sctpmessage.streamIdentifier ? false : this.content().equals(sctpmessage.content()));
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = this.streamIdentifier;

        i = 31 * i + this.protocolIdentifier;
        i = 31 * i + this.content().hashCode();
        return i;
    }

    @Override
    public SctpMessage copy() {
        return this.msgInfo == null ? new SctpMessage(this.protocolIdentifier, this.streamIdentifier, this.content().copy()) : new SctpMessage(this.msgInfo, this.content().copy());
    }

    @Override
    public SctpMessage duplicate() {
        return this.msgInfo == null ? new SctpMessage(this.protocolIdentifier, this.streamIdentifier, this.content().duplicate()) : new SctpMessage(this.msgInfo, this.content().copy());
    }

    @Override
    public SctpMessage retain() {
        super.retain();
        return this;
    }

    @Override
    public SctpMessage retain(int i) {
        super.retain(i);
        return this;
    }

    @Override
    public String toString() {
        return this.refCnt() == 0 ? "SctpFrame{streamIdentifier=" + this.streamIdentifier + ", protocolIdentifier=" + this.protocolIdentifier + ", data=(FREED)}" : "SctpFrame{streamIdentifier=" + this.streamIdentifier + ", protocolIdentifier=" + this.protocolIdentifier + ", data=" + ByteBufUtil.hexDump(this.content()) + '}';
    }
}
