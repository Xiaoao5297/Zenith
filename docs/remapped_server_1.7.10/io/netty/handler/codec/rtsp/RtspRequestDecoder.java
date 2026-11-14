package io.netty.handler.codec.rtsp;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpMessage;

public class RtspRequestDecoder extends RtspObjectDecoder {

    public RtspRequestDecoder() {}

    public RtspRequestDecoder(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected HttpMessage createMessage(String[] astring) throws Exception {
        return new DefaultHttpRequest(RtspVersions.valueOf(astring[2]), RtspMethods.valueOf(astring[0]), astring[1]);
    }

    @Override
    protected HttpMessage createInvalidMessage() {
        return new DefaultHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.OPTIONS, "/bad-request");
    }

    @Override
    protected boolean isDecodingRequest() {
        return true;
    }
}
