package io.netty.handler.codec.rtsp;

import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpResponseStatus;

public class RtspResponseDecoder extends RtspObjectDecoder {

    private static final HttpResponseStatus UNKNOWN_STATUS = new HttpResponseStatus(999, "Unknown");

    public RtspResponseDecoder() {}

    public RtspResponseDecoder(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected HttpMessage createMessage(String[] astring) throws Exception {
        return new DefaultHttpResponse(RtspVersions.valueOf(astring[0]), new HttpResponseStatus(Integer.valueOf(astring[1]), astring[2]));
    }

    @Override
    protected HttpMessage createInvalidMessage() {
        return new DefaultHttpResponse(RtspVersions.RTSP_1_0, RtspResponseDecoder.UNKNOWN_STATUS);
    }

    @Override
    protected boolean isDecodingRequest() {
        return false;
    }
}
