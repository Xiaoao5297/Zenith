package io.netty.handler.codec.socks;

final class SocksCommonUtils {

    public static final SocksRequest UNKNOWN_SOCKS_REQUEST = new UnknownSocksRequest();
    public static final SocksResponse UNKNOWN_SOCKS_RESPONSE = new UnknownSocksResponse();
    private static final int SECOND_ADDRESS_OCTET_SHIFT = 16;
    private static final int FIRST_ADDRESS_OCTET_SHIFT = 24;
    private static final int THIRD_ADDRESS_OCTET_SHIFT = 8;
    private static final int XOR_DEFAULT_VALUE = 255;
    private static final char[] ipv6conseqZeroFiller = new char[]{':', ':'};
    private static final char ipv6hextetSeparator = ':';

    private SocksCommonUtils() {}

    public static String intToIp(int i) {
        return String.valueOf(i >> 24 & 255) + '.' + (i >> 16 & 255) + '.' + (i >> 8 & 255) + '.' + (i & 255);
    }

    public static String ipv6toCompressedForm(byte[] abyte) {
        assert abyte.length == 16;

        int i = -1;
        int j = 0;

        int k;

        for (int l = 0; l < 8; l = k / 2 + 1) {
            k = l * 2;

            int i1;

            for (i1 = 0; k < abyte.length && abyte[k] == 0 && abyte[k + 1] == 0; ++i1) {
                k += 2;
            }

            if (i1 > j) {
                i = l;
                j = i1;
            }
        }

        if (i != -1 && j >= 2) {
            StringBuilder stringbuilder = new StringBuilder(39);

            ipv6toStr(stringbuilder, abyte, 0, i);
            stringbuilder.append(SocksCommonUtils.ipv6conseqZeroFiller);
            ipv6toStr(stringbuilder, abyte, i + j, 8);
            return stringbuilder.toString();
        } else {
            return ipv6toStr(abyte);
        }
    }

    public static String ipv6toStr(byte[] abyte) {
        assert abyte.length == 16;

        StringBuilder stringbuilder = new StringBuilder(39);

        ipv6toStr(stringbuilder, abyte, 0, 8);
        return stringbuilder.toString();
    }

    private static void ipv6toStr(StringBuilder stringbuilder, byte[] abyte, int i, int j) {
        for (int k = i; k < j; ++k) {
            stringbuilder.append(Integer.toHexString(abyte[k << 1] << 8 & '\uff00' | abyte[(k << 1) + 1] & 255));
            if (k < j - 1) {
                stringbuilder.append(':');
            }
        }

    }
}
