package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import com.google.common.primitives.Ints;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Nullable;

@Beta
public final class InetAddresses {

    private static final int IPV4_PART_COUNT = 4;
    private static final int IPV6_PART_COUNT = 8;
    private static final Inet4Address LOOPBACK4 = (Inet4Address) forString("127.0.0.1");
    private static final Inet4Address ANY4 = (Inet4Address) forString("0.0.0.0");

    private InetAddresses() {}

    private static Inet4Address getInet4Address(byte[] abyte) {
        Preconditions.checkArgument(abyte.length == 4, "Byte array has invalid length for an IPv4 address: %s != 4.", abyte.length);
        return (Inet4Address) bytesToInetAddress(abyte);
    }

    public static InetAddress forString(String s) {
        byte[] abyte = ipStringToBytes(s);

        if (abyte == null) {
            throw new IllegalArgumentException(String.format("'%s' is not an IP string literal.", s));
        } else {
            return bytesToInetAddress(abyte);
        }
    }

    public static boolean isInetAddress(String s) {
        return ipStringToBytes(s) != null;
    }

    private static byte[] ipStringToBytes(String s) {
        boolean flag = false;
        boolean flag1 = false;

        for (int i = 0; i < s.length(); ++i) {
            char c0 = s.charAt(i);

            if (c0 == '.') {
                flag1 = true;
            } else if (c0 == ':') {
                if (flag1) {
                    return null;
                }

                flag = true;
            } else if (Character.digit(c0, 16) == -1) {
                return null;
            }
        }

        if (flag) {
            if (flag1) {
                s = convertDottedQuadToHex(s);
                if (s == null) {
                    return null;
                }
            }

            return textToNumericFormatV6(s);
        } else if (flag1) {
            return textToNumericFormatV4(s);
        } else {
            return null;
        }
    }

    private static byte[] textToNumericFormatV4(String s) {
        String[] astring = s.split("\\.", 5);

        if (astring.length != 4) {
            return null;
        } else {
            byte[] abyte = new byte[4];

            try {
                for (int i = 0; i < abyte.length; ++i) {
                    abyte[i] = parseOctet(astring[i]);
                }

                return abyte;
            } catch (NumberFormatException numberformatexception) {
                return null;
            }
        }
    }

    private static byte[] textToNumericFormatV6(String s) {
        String[] astring = s.split(":", 10);

        if (astring.length >= 3 && astring.length <= 9) {
            int i = -1;

            int j;

            for (j = 1; j < astring.length - 1; ++j) {
                if (astring[j].length() == 0) {
                    if (i >= 0) {
                        return null;
                    }

                    i = j;
                }
            }

            int k;

            if (i >= 0) {
                j = i;
                k = astring.length - i - 1;
                if (astring[0].length() == 0) {
                    j = i - 1;
                    if (j != 0) {
                        return null;
                    }
                }

                if (astring[astring.length - 1].length() == 0) {
                    --k;
                    if (k != 0) {
                        return null;
                    }
                }
            } else {
                j = astring.length;
                k = 0;
            }

            int l;
            label77:
            {
                l = 8 - (j + k);
                if (i >= 0) {
                    if (l >= 1) {
                        break label77;
                    }
                } else if (l == 0) {
                    break label77;
                }

                return null;
            }

            ByteBuffer bytebuffer = ByteBuffer.allocate(16);

            try {
                int i1;

                for (i1 = 0; i1 < j; ++i1) {
                    bytebuffer.putShort(parseHextet(astring[i1]));
                }

                for (i1 = 0; i1 < l; ++i1) {
                    bytebuffer.putShort((short) 0);
                }

                for (i1 = k; i1 > 0; --i1) {
                    bytebuffer.putShort(parseHextet(astring[astring.length - i1]));
                }
            } catch (NumberFormatException numberformatexception) {
                return null;
            }

            return bytebuffer.array();
        } else {
            return null;
        }
    }

    private static String convertDottedQuadToHex(String s) {
        int i = s.lastIndexOf(58);
        String s1 = s.substring(0, i + 1);
        String s2 = s.substring(i + 1);
        byte[] abyte = textToNumericFormatV4(s2);

        if (abyte == null) {
            return null;
        } else {
            String s3 = Integer.toHexString((abyte[0] & 255) << 8 | abyte[1] & 255);
            String s4 = Integer.toHexString((abyte[2] & 255) << 8 | abyte[3] & 255);

            return s1 + s3 + ":" + s4;
        }
    }

    private static byte parseOctet(String s) {
        int i = Integer.parseInt(s);

        if (i <= 255 && (!s.startsWith("0") || s.length() <= 1)) {
            return (byte) i;
        } else {
            throw new NumberFormatException();
        }
    }

    private static short parseHextet(String s) {
        int i = Integer.parseInt(s, 16);

        if (i > 65535) {
            throw new NumberFormatException();
        } else {
            return (short) i;
        }
    }

    private static InetAddress bytesToInetAddress(byte[] abyte) {
        try {
            return InetAddress.getByAddress(abyte);
        } catch (UnknownHostException unknownhostexception) {
            throw new AssertionError(unknownhostexception);
        }
    }

    public static String toAddrString(InetAddress inetaddress) {
        Preconditions.checkNotNull(inetaddress);
        if (inetaddress instanceof Inet4Address) {
            return inetaddress.getHostAddress();
        } else {
            Preconditions.checkArgument(inetaddress instanceof Inet6Address);
            byte[] abyte = inetaddress.getAddress();
            int[] aint = new int[8];

            for (int i = 0; i < aint.length; ++i) {
                aint[i] = Ints.fromBytes((byte) 0, (byte) 0, abyte[2 * i], abyte[2 * i + 1]);
            }

            compressLongestRunOfZeroes(aint);
            return hextetsToIPv6String(aint);
        }
    }

    private static void compressLongestRunOfZeroes(int[] aint) {
        int i = -1;
        int j = -1;
        int k = -1;

        for (int l = 0; l < aint.length + 1; ++l) {
            if (l < aint.length && aint[l] == 0) {
                if (k < 0) {
                    k = l;
                }
            } else if (k >= 0) {
                int i1 = l - k;

                if (i1 > j) {
                    i = k;
                    j = i1;
                }

                k = -1;
            }
        }

        if (j >= 2) {
            Arrays.fill(aint, i, i + j, -1);
        }

    }

    private static String hextetsToIPv6String(int[] aint) {
        StringBuilder stringbuilder = new StringBuilder(39);
        boolean flag = false;

        for (int i = 0; i < aint.length; ++i) {
            boolean flag1 = aint[i] >= 0;

            if (flag1) {
                if (flag) {
                    stringbuilder.append(':');
                }

                stringbuilder.append(Integer.toHexString(aint[i]));
            } else if (i == 0 || flag) {
                stringbuilder.append("::");
            }

            flag = flag1;
        }

        return stringbuilder.toString();
    }

    public static String toUriString(InetAddress inetaddress) {
        return inetaddress instanceof Inet6Address ? "[" + toAddrString(inetaddress) + "]" : toAddrString(inetaddress);
    }

    public static InetAddress forUriString(String s) {
        Preconditions.checkNotNull(s);
        String s1;
        byte b0;

        if (s.startsWith("[") && s.endsWith("]")) {
            s1 = s.substring(1, s.length() - 1);
            b0 = 16;
        } else {
            s1 = s;
            b0 = 4;
        }

        byte[] abyte = ipStringToBytes(s1);

        if (abyte != null && abyte.length == b0) {
            return bytesToInetAddress(abyte);
        } else {
            throw new IllegalArgumentException(String.format("Not a valid URI IP literal: '%s'", s));
        }
    }

    public static boolean isUriInetAddress(String s) {
        try {
            forUriString(s);
            return true;
        } catch (IllegalArgumentException illegalargumentexception) {
            return false;
        }
    }

    public static boolean isCompatIPv4Address(Inet6Address inet6address) {
        if (!inet6address.isIPv4CompatibleAddress()) {
            return false;
        } else {
            byte[] abyte = inet6address.getAddress();

            return abyte[12] != 0 || abyte[13] != 0 || abyte[14] != 0 || abyte[15] != 0 && abyte[15] != 1;
        }
    }

    public static Inet4Address getCompatIPv4Address(Inet6Address inet6address) {
        Preconditions.checkArgument(isCompatIPv4Address(inet6address), "Address '%s' is not IPv4-compatible.", toAddrString(inet6address));
        return getInet4Address(Arrays.copyOfRange(inet6address.getAddress(), 12, 16));
    }

    public static boolean is6to4Address(Inet6Address inet6address) {
        byte[] abyte = inet6address.getAddress();

        return abyte[0] == 32 && abyte[1] == 2;
    }

    public static Inet4Address get6to4IPv4Address(Inet6Address inet6address) {
        Preconditions.checkArgument(is6to4Address(inet6address), "Address '%s' is not a 6to4 address.", toAddrString(inet6address));
        return getInet4Address(Arrays.copyOfRange(inet6address.getAddress(), 2, 6));
    }

    public static boolean isTeredoAddress(Inet6Address inet6address) {
        byte[] abyte = inet6address.getAddress();

        return abyte[0] == 32 && abyte[1] == 1 && abyte[2] == 0 && abyte[3] == 0;
    }

    public static InetAddresses.TeredoInfo getTeredoInfo(Inet6Address inet6address) {
        Preconditions.checkArgument(isTeredoAddress(inet6address), "Address '%s' is not a Teredo address.", toAddrString(inet6address));
        byte[] abyte = inet6address.getAddress();
        Inet4Address inet4address = getInet4Address(Arrays.copyOfRange(abyte, 4, 8));
        int i = ByteStreams.newDataInput(abyte, 8).readShort() & '\uffff';
        int j = ~ByteStreams.newDataInput(abyte, 10).readShort() & '\uffff';
        byte[] abyte1 = Arrays.copyOfRange(abyte, 12, 16);

        for (int k = 0; k < abyte1.length; ++k) {
            abyte1[k] = (byte) (~abyte1[k]);
        }

        Inet4Address inet4address1 = getInet4Address(abyte1);

        return new InetAddresses.TeredoInfo(inet4address, inet4address1, j, i);
    }

    public static boolean isIsatapAddress(Inet6Address inet6address) {
        if (isTeredoAddress(inet6address)) {
            return false;
        } else {
            byte[] abyte = inet6address.getAddress();

            return (abyte[8] | 3) != 3 ? false : abyte[9] == 0 && abyte[10] == 94 && abyte[11] == -2;
        }
    }

    public static Inet4Address getIsatapIPv4Address(Inet6Address inet6address) {
        Preconditions.checkArgument(isIsatapAddress(inet6address), "Address '%s' is not an ISATAP address.", toAddrString(inet6address));
        return getInet4Address(Arrays.copyOfRange(inet6address.getAddress(), 12, 16));
    }

    public static boolean hasEmbeddedIPv4ClientAddress(Inet6Address inet6address) {
        return isCompatIPv4Address(inet6address) || is6to4Address(inet6address) || isTeredoAddress(inet6address);
    }

    public static Inet4Address getEmbeddedIPv4ClientAddress(Inet6Address inet6address) {
        if (isCompatIPv4Address(inet6address)) {
            return getCompatIPv4Address(inet6address);
        } else if (is6to4Address(inet6address)) {
            return get6to4IPv4Address(inet6address);
        } else if (isTeredoAddress(inet6address)) {
            return getTeredoInfo(inet6address).getClient();
        } else {
            throw new IllegalArgumentException(String.format("'%s' has no embedded IPv4 address.", toAddrString(inet6address)));
        }
    }

    public static boolean isMappedIPv4Address(String s) {
        byte[] abyte = ipStringToBytes(s);

        if (abyte != null && abyte.length == 16) {
            int i;

            for (i = 0; i < 10; ++i) {
                if (abyte[i] != 0) {
                    return false;
                }
            }

            for (i = 10; i < 12; ++i) {
                if (abyte[i] != -1) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static Inet4Address getCoercedIPv4Address(InetAddress inetaddress) {
        if (inetaddress instanceof Inet4Address) {
            return (Inet4Address) inetaddress;
        } else {
            byte[] abyte = inetaddress.getAddress();
            boolean flag = true;

            for (int i = 0; i < 15; ++i) {
                if (abyte[i] != 0) {
                    flag = false;
                    break;
                }
            }

            if (flag && abyte[15] == 1) {
                return InetAddresses.LOOPBACK4;
            } else if (flag && abyte[15] == 0) {
                return InetAddresses.ANY4;
            } else {
                Inet6Address inet6address = (Inet6Address) inetaddress;
                long j = 0L;

                if (hasEmbeddedIPv4ClientAddress(inet6address)) {
                    j = (long) getEmbeddedIPv4ClientAddress(inet6address).hashCode();
                } else {
                    j = ByteBuffer.wrap(inet6address.getAddress(), 0, 8).getLong();
                }

                int k = Hashing.murmur3_32().hashLong(j).asInt();

                k |= -536870912;
                if (k == -1) {
                    k = -2;
                }

                return getInet4Address(Ints.toByteArray(k));
            }
        }
    }

    public static int coerceToInteger(InetAddress inetaddress) {
        return ByteStreams.newDataInput(getCoercedIPv4Address(inetaddress).getAddress()).readInt();
    }

    public static Inet4Address fromInteger(int i) {
        return getInet4Address(Ints.toByteArray(i));
    }

    public static InetAddress fromLittleEndianByteArray(byte[] abyte) throws UnknownHostException {
        byte[] abyte1 = new byte[abyte.length];

        for (int i = 0; i < abyte.length; ++i) {
            abyte1[i] = abyte[abyte.length - i - 1];
        }

        return InetAddress.getByAddress(abyte1);
    }

    public static InetAddress increment(InetAddress inetaddress) {
        byte[] abyte = inetaddress.getAddress();

        int i;

        for (i = abyte.length - 1; i >= 0 && abyte[i] == -1; --i) {
            abyte[i] = 0;
        }

        Preconditions.checkArgument(i >= 0, "Incrementing %s would wrap.", inetaddress);
        ++abyte[i];
        return bytesToInetAddress(abyte);
    }

    public static boolean isMaximum(InetAddress inetaddress) {
        byte[] abyte = inetaddress.getAddress();

        for (int i = 0; i < abyte.length; ++i) {
            if (abyte[i] != -1) {
                return false;
            }
        }

        return true;
    }

    @Beta
    public static final class TeredoInfo {

        private final Inet4Address server;
        private final Inet4Address client;
        private final int port;
        private final int flags;

        public TeredoInfo(@Nullable Inet4Address inet4address, @Nullable Inet4Address inet4address1, int i, int j) {
            Preconditions.checkArgument(i >= 0 && i <= 65535, "port '%s' is out of range (0 <= port <= 0xffff)", i);
            Preconditions.checkArgument(j >= 0 && j <= 65535, "flags '%s' is out of range (0 <= flags <= 0xffff)", j);
            this.server = (Inet4Address) Objects.firstNonNull(inet4address, InetAddresses.ANY4);
            this.client = (Inet4Address) Objects.firstNonNull(inet4address1, InetAddresses.ANY4);
            this.port = i;
            this.flags = j;
        }

        public Inet4Address getServer() {
            return this.server;
        }

        public Inet4Address getClient() {
            return this.client;
        }

        public int getPort() {
            return this.port;
        }

        public int getFlags() {
            return this.flags;
        }
    }
}
