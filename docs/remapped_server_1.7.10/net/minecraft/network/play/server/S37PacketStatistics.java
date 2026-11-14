package net.minecraft.network.play.server;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

public class S37PacketStatistics extends Packet {

    private Map field_148976_a;

    public S37PacketStatistics() {}

    public S37PacketStatistics(Map map) {
        this.field_148976_a = map;
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147293_a(this);
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        int i = packetbuffer.func_150792_a();

        this.field_148976_a = Maps.newHashMap();

        for (int j = 0; j < i; ++j) {
            StatBase statbase = StatList.func_151177_a(packetbuffer.func_150789_c(32767));
            int k = packetbuffer.func_150792_a();

            if (statbase != null) {
                this.field_148976_a.put(statbase, k);
            }
        }

    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.func_150787_b(this.field_148976_a.size());
        Iterator iterator = this.field_148976_a.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            packetbuffer.func_150785_a(((StatBase) entry.getKey()).field_75975_e);
            packetbuffer.func_150787_b((Integer) entry.getValue());
        }

    }

    @Override
    public String func_148835_b() {
        return String.format("count=%d", this.field_148976_a.size());
    }
}
