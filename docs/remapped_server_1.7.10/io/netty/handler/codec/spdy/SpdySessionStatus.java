package io.netty.handler.codec.spdy;

public class SpdySessionStatus implements Comparable<SpdySessionStatus> {

    public static final SpdySessionStatus OK = new SpdySessionStatus(0, "OK");
    public static final SpdySessionStatus PROTOCOL_ERROR = new SpdySessionStatus(1, "PROTOCOL_ERROR");
    public static final SpdySessionStatus INTERNAL_ERROR = new SpdySessionStatus(2, "INTERNAL_ERROR");
    private final int code;
    private final String statusPhrase;

    public static SpdySessionStatus valueOf(int i) {
        switch (i) {
            case 0:
                return SpdySessionStatus.OK;
            case 1:
                return SpdySessionStatus.PROTOCOL_ERROR;
            case 2:
                return SpdySessionStatus.INTERNAL_ERROR;
            default:
                return new SpdySessionStatus(i, "UNKNOWN (" + i + ')');
        }
    }

    public SpdySessionStatus(int i, String s) {
        if (s == null) {
            throw new NullPointerException("statusPhrase");
        } else {
            this.code = i;
            this.statusPhrase = s;
        }
    }

    public int getCode() {
        return this.code;
    }

    public String getStatusPhrase() {
        return this.statusPhrase;
    }

    public int hashCode() {
        return this.getCode();
    }

    public boolean equals(Object object) {
        return !(object instanceof SpdySessionStatus) ? false : this.getCode() == ((SpdySessionStatus) object).getCode();
    }

    public String toString() {
        return this.getStatusPhrase();
    }

    public int compareTo(SpdySessionStatus spdysessionstatus) {
        return this.getCode() - spdysessionstatus.getCode();
    }
}
