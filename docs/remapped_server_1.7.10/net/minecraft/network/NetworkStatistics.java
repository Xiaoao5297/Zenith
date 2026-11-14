package net.minecraft.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class NetworkStatistics {

    private static final Logger field_152478_a = LogManager.getLogger();
    private static final Marker field_152479_b = MarkerManager.getMarker("NETSTAT_MARKER", NetworkManager.field_152461_c);
    private NetworkStatistics$Tracker field_152480_c = new NetworkStatistics$Tracker();
    private NetworkStatistics$Tracker field_152481_d = new NetworkStatistics$Tracker();

    public NetworkStatistics() {}

    public void func_152469_a(int i, long j) {
        this.field_152480_c.func_152488_a(i, j);
    }

    public void func_152464_b(int i, long j) {
        this.field_152481_d.func_152488_a(i, j);
    }

    public long func_152465_a() {
        return this.field_152480_c.func_152485_a();
    }

    public long func_152471_b() {
        return this.field_152481_d.func_152485_a();
    }

    public long func_152472_c() {
        return this.field_152480_c.func_152489_b();
    }

    public long func_152473_d() {
        return this.field_152481_d.func_152489_b();
    }

    public NetworkStatistics$PacketStat func_152477_e() {
        return this.field_152480_c.func_152484_c();
    }

    public NetworkStatistics$PacketStat func_152467_f() {
        return this.field_152480_c.func_152486_d();
    }

    public NetworkStatistics$PacketStat func_152475_g() {
        return this.field_152481_d.func_152484_c();
    }

    public NetworkStatistics$PacketStat func_152470_h() {
        return this.field_152481_d.func_152486_d();
    }

    public NetworkStatistics$PacketStat func_152466_a(int i) {
        return this.field_152480_c.func_152487_a(i);
    }

    public NetworkStatistics$PacketStat func_152468_b(int i) {
        return this.field_152481_d.func_152487_a(i);
    }
}
