package io.netty.handler.codec.socks;

public enum SocksCmdStatus {

    SUCCESS((byte) 0), FAILURE((byte) 1), FORBIDDEN((byte) 2), NETWORK_UNREACHABLE((byte) 3), HOST_UNREACHABLE((byte) 4), REFUSED((byte) 5), TTL_EXPIRED((byte) 6), COMMAND_NOT_SUPPORTED((byte) 7), ADDRESS_NOT_SUPPORTED((byte) 8), UNASSIGNED((byte) -1);

    private final byte b;

    private SocksCmdStatus(byte b0) {
        this.b = b0;
    }

    public static SocksCmdStatus fromByte(byte b0) {
        SocksCmdStatus[] asockscmdstatus = values();
        int i = asockscmdstatus.length;

        for (int j = 0; j < i; ++j) {
            SocksCmdStatus sockscmdstatus = asockscmdstatus[j];

            if (sockscmdstatus.b == b0) {
                return sockscmdstatus;
            }
        }

        return SocksCmdStatus.UNASSIGNED;
    }

    public byte byteValue() {
        return this.b;
    }
}
