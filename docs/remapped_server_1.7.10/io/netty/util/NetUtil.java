package io.netty.util;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;

public final class NetUtil {

    public static final Inet4Address LOCALHOST4;
    public static final Inet6Address LOCALHOST6;
    public static final InetAddress LOCALHOST;
    public static final NetworkInterface LOOPBACK_IF;
    public static final int SOMAXCONN;
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(NetUtil.class);

    public static byte[] createByteArrayFromIpAddressString(String s) {
        StringTokenizer stringtokenizer;

        if (isValidIpV4Address(s)) {
            stringtokenizer = new StringTokenizer(s, ".");
            byte[] abyte = new byte[4];

            for (int i = 0; i < 4; ++i) {
                String s1 = stringtokenizer.nextToken();
                int j = Integer.parseInt(s1);

                abyte[i] = (byte) j;
            }

            return abyte;
        } else if (!isValidIpV6Address(s)) {
            return null;
        } else {
            if (s.charAt(0) == '[') {
                s = s.substring(1, s.length() - 1);
            }

            stringtokenizer = new StringTokenizer(s, ":.", true);
            ArrayList arraylist = new ArrayList();
            ArrayList arraylist1 = new ArrayList();
            String s2 = "";
            String s3 = "";
            int k = -1;

            while (stringtokenizer.hasMoreTokens()) {
                s3 = s2;
                s2 = stringtokenizer.nextToken();
                if (":".equals(s2)) {
                    if (":".equals(s3)) {
                        k = arraylist.size();
                    } else if (!s3.isEmpty()) {
                        arraylist.add(s3);
                    }
                } else if (".".equals(s2)) {
                    arraylist1.add(s3);
                }
            }

            if (":".equals(s3)) {
                if (":".equals(s2)) {
                    k = arraylist.size();
                } else {
                    arraylist.add(s2);
                }
            } else if (".".equals(s3)) {
                arraylist1.add(s2);
            }

            int l = 8;

            if (!arraylist1.isEmpty()) {
                l -= 2;
            }

            int i1;

            if (k != -1) {
                int j1 = l - arraylist.size();

                for (i1 = 0; i1 < j1; ++i1) {
                    arraylist.add(k, "0");
                }
            }

            byte[] abyte1 = new byte[16];

            for (i1 = 0; i1 < arraylist.size(); ++i1) {
                convertToBytes((String) arraylist.get(i1), abyte1, i1 * 2);
            }

            for (i1 = 0; i1 < arraylist1.size(); ++i1) {
                abyte1[i1 + 12] = (byte) (Integer.parseInt((String) arraylist1.get(i1)) & 255);
            }

            return abyte1;
        }
    }

    private static void convertToBytes(String s, byte[] abyte, int i) {
        int j = s.length();
        int k = 0;

        abyte[i] = 0;
        abyte[i + 1] = 0;
        int l;

        if (j > 3) {
            l = getIntValue(s.charAt(k++));
            abyte[i] = (byte) (abyte[i] | l << 4);
        }

        if (j > 2) {
            l = getIntValue(s.charAt(k++));
            abyte[i] = (byte) (abyte[i] | l);
        }

        if (j > 1) {
            l = getIntValue(s.charAt(k++));
            abyte[i + 1] = (byte) (abyte[i + 1] | l << 4);
        }

        l = getIntValue(s.charAt(k));
        abyte[i + 1] = (byte) (abyte[i + 1] | l & 15);
    }

    static int getIntValue(char c0) {
        switch (c0) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            default:
                c0 = Character.toLowerCase(c0);
                switch (c0) {
                    case 'a':
                        return 10;
                    case 'b':
                        return 11;
                    case 'c':
                        return 12;
                    case 'd':
                        return 13;
                    case 'e':
                        return 14;
                    case 'f':
                        return 15;
                    default:
                        return 0;
                }
        }
    }

    public static boolean isValidIpV6Address(String s) {
        int i = s.length();
        boolean flag = false;
        int j = 0;
        int k = 0;
        int l = 0;
        StringBuilder stringbuilder = new StringBuilder();
        char c0 = 0;
        byte b0 = 0;

        if (i < 2) {
            return false;
        } else {
            for (int i1 = 0; i1 < i; ++i1) {
                char c1 = c0;

                c0 = s.charAt(i1);
                switch (c0) {
                    case '%':
                        if (j == 0) {
                            return false;
                        }

                        ++l;
                        if (i1 + 1 >= i) {
                            return false;
                        }

                        try {
                            Integer.parseInt(s.substring(i1 + 1));
                            break;
                        } catch (NumberFormatException numberformatexception) {
                            return false;
                        }
                    case '.':
                        ++k;
                        if (k > 3) {
                            return false;
                        }

                        if (!isValidIp4Word(stringbuilder.toString())) {
                            return false;
                        }

                        if (j != 6 && !flag) {
                            return false;
                        }

                        if (j == 7 && s.charAt(b0) != ':' && s.charAt(1 + b0) != ':') {
                            return false;
                        }

                        stringbuilder.delete(0, stringbuilder.length());
                        break;
                    case ':':
                        if (i1 == b0 && (s.length() <= i1 || s.charAt(i1 + 1) != ':')) {
                            return false;
                        }

                        ++j;
                        if (j > 7) {
                            return false;
                        }

                        if (k > 0) {
                            return false;
                        }

                        if (c1 == ':') {
                            if (flag) {
                                return false;
                            }

                            flag = true;
                        }

                        stringbuilder.delete(0, stringbuilder.length());
                        break;
                    case '[':
                        if (i1 != 0) {
                            return false;
                        }

                        if (s.charAt(i - 1) != ']') {
                            return false;
                        }

                        b0 = 1;
                        if (i < 4) {
                            return false;
                        }
                        break;
                    case ']':
                        if (i1 != i - 1) {
                            return false;
                        }

                        if (s.charAt(0) != '[') {
                            return false;
                        }
                        break;
                    default:
                        if (l == 0) {
                            if (stringbuilder != null && stringbuilder.length() > 3) {
                                return false;
                            }

                            if (!isValidHexChar(c0)) {
                                return false;
                            }
                        }

                        stringbuilder.append(c0);
                }
            }

            if (k > 0) {
                if (k != 3 || !isValidIp4Word(stringbuilder.toString()) || j >= 7) {
                    return false;
                }
            } else {
                if (j != 7 && !flag) {
                    return false;
                }

                if (l == 0 && stringbuilder.length() == 0 && s.charAt(i - 1 - b0) == ':' && s.charAt(i - 2 - b0) != ':') {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean isValidIp4Word(String s) {
        if (s.length() >= 1 && s.length() <= 3) {
            for (int i = 0; i < s.length(); ++i) {
                char c0 = s.charAt(i);

                if (c0 < '0' || c0 > '9') {
                    return false;
                }
            }

            if (Integer.parseInt(s) > 255) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    static boolean isValidHexChar(char c0) {
        return c0 >= '0' && c0 <= '9' || c0 >= 'A' && c0 <= 'F' || c0 >= 'a' && c0 <= 'f';
    }

    public static boolean isValidIpV4Address(String s) {
        int i = 0;
        int j = s.length();

        if (j > 15) {
            return false;
        } else {
            StringBuilder stringbuilder = new StringBuilder();

            for (int k = 0; k < j; ++k) {
                char c0 = s.charAt(k);

                if (c0 == '.') {
                    ++i;
                    if (i > 3) {
                        return false;
                    }

                    if (stringbuilder.length() == 0) {
                        return false;
                    }

                    if (Integer.parseInt(stringbuilder.toString()) > 255) {
                        return false;
                    }

                    stringbuilder.delete(0, stringbuilder.length());
                } else {
                    if (!Character.isDigit(c0)) {
                        return false;
                    }

                    if (stringbuilder.length() > 2) {
                        return false;
                    }

                    stringbuilder.append(c0);
                }
            }

            if (stringbuilder.length() != 0 && Integer.parseInt(stringbuilder.toString()) <= 255) {
                if (i != 3) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    private NetUtil() {}

    static {
        NetworkInterface networkinterface = null;

        try {
            Enumeration enumeration = NetworkInterface.getNetworkInterfaces();

            while (enumeration.hasMoreElements()) {
                NetworkInterface networkinterface1 = (NetworkInterface) enumeration.nextElement();

                if (networkinterface1.isLoopback()) {
                    networkinterface = networkinterface1;
                    break;
                }
            }

            if (networkinterface == null) {
                NetUtil.logger.warn("Failed to find the loopback interface");
            }
        } catch (SocketException socketexception) {
            NetUtil.logger.warn("Failed to find the loopback interface", (Throwable) socketexception);
        }

        LOOPBACK_IF = networkinterface;
        Object object = null;

        if (NetUtil.LOOPBACK_IF != null) {
            NetUtil.logger.debug("Loopback interface: {}", (Object) NetUtil.LOOPBACK_IF.getDisplayName());
            Enumeration enumeration1 = NetUtil.LOOPBACK_IF.getInetAddresses();

            while (enumeration1.hasMoreElements()) {
                InetAddress inetaddress = (InetAddress) enumeration1.nextElement();

                if (object == null) {
                    NetUtil.logger.debug("Loopback address: {} (primary)", (Object) inetaddress);
                    object = inetaddress;
                } else {
                    NetUtil.logger.debug("Loopback address: {}", (Object) inetaddress);
                }
            }
        }

        Inet4Address inet4address = null;

        try {
            inet4address = (Inet4Address) InetAddress.getByAddress(new byte[]{127, 0, 0, 1});
        } catch (Exception exception) {
            PlatformDependent.throwException(exception);
        }

        LOCALHOST4 = inet4address;
        Inet6Address inet6address = null;

        try {
            inet6address = (Inet6Address) InetAddress.getByAddress(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1});
        } catch (Exception exception1) {
            PlatformDependent.throwException(exception1);
        }

        LOCALHOST6 = inet6address;
        if (object == null) {
            try {
                if (NetworkInterface.getByInetAddress(NetUtil.LOCALHOST6) != null) {
                    NetUtil.logger.debug("Using hard-coded IPv6 localhost address: {}", (Object) inet6address);
                    object = inet6address;
                }
            } catch (Exception exception2) {
                ;
            } finally {
                if (object == null) {
                    NetUtil.logger.debug("Using hard-coded IPv4 localhost address: {}", (Object) inet4address);
                    object = inet4address;
                }

            }
        }

        LOCALHOST = (InetAddress) object;
        int i = 3072;
        BufferedReader bufferedreader = null;

        try {
            bufferedreader = new BufferedReader(new FileReader("/proc/sys/net/core/somaxconn"));
            i = Integer.parseInt(bufferedreader.readLine());
            NetUtil.logger.debug("/proc/sys/net/core/somaxconn: {}", (Object) i);
        } catch (Exception exception3) {
            ;
        } finally {
            if (bufferedreader != null) {
                try {
                    bufferedreader.close();
                } catch (Exception exception4) {
                    ;
                }
            }

        }

        SOMAXCONN = i;
    }
}
