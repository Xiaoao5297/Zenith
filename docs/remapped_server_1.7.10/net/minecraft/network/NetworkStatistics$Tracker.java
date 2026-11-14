package net.minecraft.network;

import java.util.concurrent.atomic.AtomicReference;

class NetworkStatistics$Tracker {

    private AtomicReference[] field_152490_a = new AtomicReference[100];

    public NetworkStatistics$Tracker() {
        for (int i = 0; i < 100; ++i) {
            this.field_152490_a[i] = new AtomicReference(new NetworkStatistics$PacketStatData(0L, 0, 0.0D, (NetworkStatistics$1) null));
        }

    }

    public void func_152488_a(int i, long j) {
        try {
            if (i < 0 || i >= 100) {
                return;
            }

            NetworkStatistics$PacketStatData networkstatistics$packetstatdata;
            NetworkStatistics$PacketStatData networkstatistics$packetstatdata1;

            do {
                networkstatistics$packetstatdata = (NetworkStatistics$PacketStatData) this.field_152490_a[i].get();
                networkstatistics$packetstatdata1 = networkstatistics$packetstatdata.func_152494_a(j);
            } while (!this.field_152490_a[i].compareAndSet(networkstatistics$packetstatdata, networkstatistics$packetstatdata1));
        } catch (Exception exception) {
            if (NetworkStatistics.access$100().isDebugEnabled()) {
                NetworkStatistics.access$100().debug(NetworkStatistics.access$200(), "NetStat failed with packetId: " + i, (Throwable) exception);
            }
        }

    }

    public long func_152485_a() {
        long i = 0L;

        for (int j = 0; j < 100; ++j) {
            i += ((NetworkStatistics$PacketStatData) this.field_152490_a[j].get()).func_152493_a();
        }

        return i;
    }

    public long func_152489_b() {
        long i = 0L;

        for (int j = 0; j < 100; ++j) {
            i += (long) ((NetworkStatistics$PacketStatData) this.field_152490_a[j].get()).func_152495_b();
        }

        return i;
    }

    public NetworkStatistics$PacketStat func_152484_c() {
        int i = -1;
        NetworkStatistics$PacketStatData networkstatistics$packetstatdata = new NetworkStatistics$PacketStatData(-1L, -1, 0.0D, (NetworkStatistics$1) null);

        for (int j = 0; j < 100; ++j) {
            NetworkStatistics$PacketStatData networkstatistics$packetstatdata1 = (NetworkStatistics$PacketStatData) this.field_152490_a[j].get();

            if (NetworkStatistics$PacketStatData.access$300(networkstatistics$packetstatdata1) > NetworkStatistics$PacketStatData.access$300(networkstatistics$packetstatdata)) {
                i = j;
                networkstatistics$packetstatdata = networkstatistics$packetstatdata1;
            }
        }

        return new NetworkStatistics$PacketStat(i, networkstatistics$packetstatdata);
    }

    public NetworkStatistics$PacketStat func_152486_d() {
        int i = -1;
        NetworkStatistics$PacketStatData networkstatistics$packetstatdata = new NetworkStatistics$PacketStatData(-1L, -1, 0.0D, (NetworkStatistics$1) null);

        for (int j = 0; j < 100; ++j) {
            NetworkStatistics$PacketStatData networkstatistics$packetstatdata1 = (NetworkStatistics$PacketStatData) this.field_152490_a[j].get();

            if (NetworkStatistics$PacketStatData.access$400(networkstatistics$packetstatdata1) > NetworkStatistics$PacketStatData.access$400(networkstatistics$packetstatdata)) {
                i = j;
                networkstatistics$packetstatdata = networkstatistics$packetstatdata1;
            }
        }

        return new NetworkStatistics$PacketStat(i, networkstatistics$packetstatdata);
    }

    public NetworkStatistics$PacketStat func_152487_a(int i) {
        return i >= 0 && i < 100 ? new NetworkStatistics$PacketStat(i, (NetworkStatistics$PacketStatData) this.field_152490_a[i].get()) : null;
    }
}
