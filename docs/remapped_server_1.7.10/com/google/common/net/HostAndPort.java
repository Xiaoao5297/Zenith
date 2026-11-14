package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.io.Serializable;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Beta
@Immutable
@GwtCompatible
public final class HostAndPort implements Serializable {

    private static final int NO_PORT = -1;
    private final String host;
    private final int port;
    private final boolean hasBracketlessColons;
    private static final long serialVersionUID = 0L;

    private HostAndPort(String s, int i, boolean flag) {
        this.host = s;
        this.port = i;
        this.hasBracketlessColons = flag;
    }

    public String getHostText() {
        return this.host;
    }

    public boolean hasPort() {
        return this.port >= 0;
    }

    public int getPort() {
        Preconditions.checkState(this.hasPort());
        return this.port;
    }

    public int getPortOrDefault(int i) {
        return this.hasPort() ? this.port : i;
    }

    public static HostAndPort fromParts(String s, int i) {
        Preconditions.checkArgument(isValidPort(i), "Port out of range: %s", i);
        HostAndPort hostandport = fromString(s);

        Preconditions.checkArgument(!hostandport.hasPort(), "Host has a port: %s", s);
        return new HostAndPort(hostandport.host, i, hostandport.hasBracketlessColons);
    }

    public static HostAndPort fromString(String s) {
        Preconditions.checkNotNull(s);
        String s1 = null;
        boolean flag = false;
        String s2;
        int i;

        if (s.startsWith("[")) {
            String[] astring = getHostAndPortFromBracketedHost(s);

            s2 = astring[0];
            s1 = astring[1];
        } else {
            i = s.indexOf(58);
            if (i >= 0 && s.indexOf(58, i + 1) == -1) {
                s2 = s.substring(0, i);
                s1 = s.substring(i + 1);
            } else {
                s2 = s;
                flag = i >= 0;
            }
        }

        i = -1;
        if (!Strings.isNullOrEmpty(s1)) {
            Preconditions.checkArgument(!s1.startsWith("+"), "Unparseable port number: %s", s);

            try {
                i = Integer.parseInt(s1);
            } catch (NumberFormatException numberformatexception) {
                throw new IllegalArgumentException("Unparseable port number: " + s);
            }

            Preconditions.checkArgument(isValidPort(i), "Port number out of range: %s", s);
        }

        return new HostAndPort(s2, i, flag);
    }

    private static String[] getHostAndPortFromBracketedHost(String s) {
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;

        Preconditions.checkArgument(s.charAt(0) == '[', "Bracketed host-port string must start with a bracket: %s", s);
        int i = s.indexOf(58);
        int j = s.lastIndexOf(93);

        Preconditions.checkArgument(i > -1 && j > i, "Invalid bracketed host/port: %s", s);
        String s1 = s.substring(1, j);

        if (j + 1 == s.length()) {
            return new String[]{s1, ""};
        } else {
            Preconditions.checkArgument(s.charAt(j + 1) == ':', "Only a colon may follow a close bracket: %s", s);

            for (int k = j + 2; k < s.length(); ++k) {
                Preconditions.checkArgument(Character.isDigit(s.charAt(k)), "Port must be numeric: %s", s);
            }

            return new String[]{s1, s.substring(j + 2)};
        }
    }

    public HostAndPort withDefaultPort(int i) {
        Preconditions.checkArgument(isValidPort(i));
        return !this.hasPort() && this.port != i ? new HostAndPort(this.host, i, this.hasBracketlessColons) : this;
    }

    public HostAndPort requireBracketsForIPv6() {
        Preconditions.checkArgument(!this.hasBracketlessColons, "Possible bracketless IPv6 literal: %s", this.host);
        return this;
    }

    public boolean equals(@Nullable Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof HostAndPort)) {
            return false;
        } else {
            HostAndPort hostandport = (HostAndPort) object;

            return Objects.equal(this.host, hostandport.host) && this.port == hostandport.port && this.hasBracketlessColons == hostandport.hasBracketlessColons;
        }
    }

    public int hashCode() {
        return Objects.hashCode(this.host, this.port, this.hasBracketlessColons);
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder(this.host.length() + 7);

        if (this.host.indexOf(58) >= 0) {
            stringbuilder.append('[').append(this.host).append(']');
        } else {
            stringbuilder.append(this.host);
        }

        if (this.hasPort()) {
            stringbuilder.append(':').append(this.port);
        }

        return stringbuilder.toString();
    }

    private static boolean isValidPort(int i) {
        return i >= 0 && i <= 65535;
    }
}
