package io.netty.handler.codec.spdy;

import io.netty.util.internal.StringUtil;

public class DefaultSpdyPingFrame implements SpdyPingFrame {

    private int id;

    public DefaultSpdyPingFrame(int i) {
        this.setId(i);
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public SpdyPingFrame setId(int i) {
        this.id = i;
        return this;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append(this.getClass().getSimpleName());
        stringbuilder.append(StringUtil.NEWLINE);
        stringbuilder.append("--> ID = ");
        stringbuilder.append(this.getId());
        return stringbuilder.toString();
    }
}
