package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.util.CharsetUtil;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

final class WebSocketUtil {

    static byte[] md5(byte[] abyte) {
        try {
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");

            return messagedigest.digest(abyte);
        } catch (NoSuchAlgorithmException nosuchalgorithmexception) {
            throw new InternalError("MD5 not supported on this platform - Outdated?");
        }
    }

    static byte[] sha1(byte[] abyte) {
        try {
            MessageDigest messagedigest = MessageDigest.getInstance("SHA1");

            return messagedigest.digest(abyte);
        } catch (NoSuchAlgorithmException nosuchalgorithmexception) {
            throw new InternalError("SHA-1 is not supported on this platform - Outdated?");
        }
    }

    static String base64(byte[] abyte) {
        ByteBuf bytebuf = Unpooled.wrappedBuffer(abyte);
        ByteBuf bytebuf1 = Base64.encode(bytebuf);
        String s = bytebuf1.toString(CharsetUtil.UTF_8);

        bytebuf1.release();
        return s;
    }

    static byte[] randomBytes(int i) {
        byte[] abyte = new byte[i];

        for (int j = 0; j < i; ++j) {
            abyte[j] = (byte) randomNumber(0, 255);
        }

        return abyte;
    }

    static int randomNumber(int i, int j) {
        return (int) (Math.random() * (double) j + (double) i);
    }

    private WebSocketUtil() {}
}
