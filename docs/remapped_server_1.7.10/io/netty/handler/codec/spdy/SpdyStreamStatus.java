package io.netty.handler.codec.spdy;

public class SpdyStreamStatus implements Comparable<SpdyStreamStatus> {

    public static final SpdyStreamStatus PROTOCOL_ERROR = new SpdyStreamStatus(1, "PROTOCOL_ERROR");
    public static final SpdyStreamStatus INVALID_STREAM = new SpdyStreamStatus(2, "INVALID_STREAM");
    public static final SpdyStreamStatus REFUSED_STREAM = new SpdyStreamStatus(3, "REFUSED_STREAM");
    public static final SpdyStreamStatus UNSUPPORTED_VERSION = new SpdyStreamStatus(4, "UNSUPPORTED_VERSION");
    public static final SpdyStreamStatus CANCEL = new SpdyStreamStatus(5, "CANCEL");
    public static final SpdyStreamStatus INTERNAL_ERROR = new SpdyStreamStatus(6, "INTERNAL_ERROR");
    public static final SpdyStreamStatus FLOW_CONTROL_ERROR = new SpdyStreamStatus(7, "FLOW_CONTROL_ERROR");
    public static final SpdyStreamStatus STREAM_IN_USE = new SpdyStreamStatus(8, "STREAM_IN_USE");
    public static final SpdyStreamStatus STREAM_ALREADY_CLOSED = new SpdyStreamStatus(9, "STREAM_ALREADY_CLOSED");
    public static final SpdyStreamStatus INVALID_CREDENTIALS = new SpdyStreamStatus(10, "INVALID_CREDENTIALS");
    public static final SpdyStreamStatus FRAME_TOO_LARGE = new SpdyStreamStatus(11, "FRAME_TOO_LARGE");
    private final int code;
    private final String statusPhrase;

    public static SpdyStreamStatus valueOf(int i) {
        if (i == 0) {
            throw new IllegalArgumentException("0 is not a valid status code for a RST_STREAM");
        } else {
            switch (i) {
                case 1:
                    return SpdyStreamStatus.PROTOCOL_ERROR;
                case 2:
                    return SpdyStreamStatus.INVALID_STREAM;
                case 3:
                    return SpdyStreamStatus.REFUSED_STREAM;
                case 4:
                    return SpdyStreamStatus.UNSUPPORTED_VERSION;
                case 5:
                    return SpdyStreamStatus.CANCEL;
                case 6:
                    return SpdyStreamStatus.INTERNAL_ERROR;
                case 7:
                    return SpdyStreamStatus.FLOW_CONTROL_ERROR;
                case 8:
                    return SpdyStreamStatus.STREAM_IN_USE;
                case 9:
                    return SpdyStreamStatus.STREAM_ALREADY_CLOSED;
                case 10:
                    return SpdyStreamStatus.INVALID_CREDENTIALS;
                case 11:
                    return SpdyStreamStatus.FRAME_TOO_LARGE;
                default:
                    return new SpdyStreamStatus(i, "UNKNOWN (" + i + ')');
            }
        }
    }

    public SpdyStreamStatus(int i, String s) {
        if (i == 0) {
            throw new IllegalArgumentException("0 is not a valid status code for a RST_STREAM");
        } else if (s == null) {
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
        return !(object instanceof SpdyStreamStatus) ? false : this.getCode() == ((SpdyStreamStatus) object).getCode();
    }

    public String toString() {
        return this.getStatusPhrase();
    }

    public int compareTo(SpdyStreamStatus spdystreamstatus) {
        return this.getCode() - spdystreamstatus.getCode();
    }
}
