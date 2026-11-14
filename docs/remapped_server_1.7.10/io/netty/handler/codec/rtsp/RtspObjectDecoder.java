package io.netty.handler.codec.rtsp;

import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectDecoder;

public abstract class RtspObjectDecoder extends HttpObjectDecoder {

    protected RtspObjectDecoder() {
        this(4096, 8192, 8192);
    }

    protected RtspObjectDecoder(int i, int j, int k) {
        super(i, j, k * 2, false);
    }

    @Override
    protected boolean isContentAlwaysEmpty(HttpMessage httpmessage) {
        boolean flag = super.isContentAlwaysEmpty(httpmessage);

        return flag ? true : (!httpmessage.headers().contains("Content-Length") ? true : flag);
    }
}
