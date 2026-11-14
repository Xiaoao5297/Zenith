package net.minecraft.network.play.client;

public enum C16PacketClientStatus$EnumState {

    PERFORM_RESPAWN(0), REQUEST_STATS(1), OPEN_INVENTORY_ACHIEVEMENT(2);

    private final int field_151403_d;
    private static final C16PacketClientStatus$EnumState[] field_151404_e = new C16PacketClientStatus$EnumState[values().length];

    private C16PacketClientStatus$EnumState(int i) {
        this.field_151403_d = i;
    }

    static {
        C16PacketClientStatus$EnumState[] ac16packetclientstatus$enumstate = values();
        int i = ac16packetclientstatus$enumstate.length;

        for (int j = 0; j < i; ++j) {
            C16PacketClientStatus$EnumState c16packetclientstatus$enumstate = ac16packetclientstatus$enumstate[j];

            C16PacketClientStatus$EnumState.field_151404_e[c16packetclientstatus$enumstate.field_151403_d] = c16packetclientstatus$enumstate;
        }

    }
}
