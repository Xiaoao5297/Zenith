package io.netty.channel.local;

import io.netty.channel.Channel;
import java.net.SocketAddress;

public final class LocalAddress extends SocketAddress implements Comparable<LocalAddress> {

    private static final long serialVersionUID = 4644331421130916435L;
    public static final LocalAddress ANY = new LocalAddress("ANY");
    private final String id;
    private final String strVal;

    LocalAddress(Channel channel) {
        StringBuilder stringbuilder = new StringBuilder(16);

        stringbuilder.append("local:E");
        stringbuilder.append(Long.toHexString((long) channel.hashCode() & 4294967295L | 4294967296L));
        stringbuilder.setCharAt(7, ':');
        this.id = stringbuilder.substring(6);
        this.strVal = stringbuilder.toString();
    }

    public LocalAddress(String s) {
        if (s == null) {
            throw new NullPointerException("id");
        } else {
            s = s.trim().toLowerCase();
            if (s.isEmpty()) {
                throw new IllegalArgumentException("empty id");
            } else {
                this.id = s;
                this.strVal = "local:" + s;
            }
        }
    }

    public String id() {
        return this.id;
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public boolean equals(Object object) {
        return !(object instanceof LocalAddress) ? false : this.id.equals(((LocalAddress) object).id);
    }

    public int compareTo(LocalAddress localaddress) {
        return this.id.compareTo(localaddress.id);
    }

    public String toString() {
        return this.strVal;
    }
}
