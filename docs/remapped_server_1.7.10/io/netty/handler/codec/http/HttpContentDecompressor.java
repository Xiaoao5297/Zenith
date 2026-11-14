package io.netty.handler.codec.http;

import io.netty.channel.ChannelHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;

public class HttpContentDecompressor extends HttpContentDecoder {

    public HttpContentDecompressor() {}

    @Override
    protected EmbeddedChannel newContentDecoder(String s) throws Exception {
        return !"gzip".equalsIgnoreCase(s) && !"x-gzip".equalsIgnoreCase(s) ? (!"deflate".equalsIgnoreCase(s) && !"x-deflate".equalsIgnoreCase(s) ? null : new EmbeddedChannel(new ChannelHandler[]{ZlibCodecFactory.newZlibDecoder(ZlibWrapper.ZLIB_OR_NONE)})) : new EmbeddedChannel(new ChannelHandler[]{ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP)});
    }
}
