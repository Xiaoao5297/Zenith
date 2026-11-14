package net.minecraft.network;

public class NetworkStatistics$PacketStat {

    private final int field_152482_a;
    private final NetworkStatistics$PacketStatData field_152483_b;

    public NetworkStatistics$PacketStat(int i, NetworkStatistics$PacketStatData networkstatistics$packetstatdata) {
        this.field_152482_a = i;
        this.field_152483_b = networkstatistics$packetstatdata;
    }

    public String toString() {
        return "PacketStat(" + this.field_152482_a + ")" + this.field_152483_b;
    }
}
