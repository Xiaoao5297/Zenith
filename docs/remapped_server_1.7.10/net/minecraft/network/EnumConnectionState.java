package net.minecraft.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.logging.log4j.LogManager;

public enum EnumConnectionState {

    HANDSHAKING(-1), PLAY(0), STATUS(1), LOGIN(2);

    private static final TIntObjectMap field_150764_e = new TIntObjectHashMap<>();
    private static final Map field_150761_f = Maps.newHashMap();
    private final int field_150762_g;
    private final BiMap field_150769_h;
    private final BiMap field_150770_i;

    private EnumConnectionState(int i) {
        this.field_150769_h = HashBiMap.create();
        this.field_150770_i = HashBiMap.create();
        this.field_150762_g = i;
    }

    protected EnumConnectionState func_150751_a(int i, Class oclass) {
        String s;

        if (this.field_150769_h.containsKey(i)) {
            s = "Serverbound packet ID " + i + " is already assigned to " + this.field_150769_h.get(i) + "; cannot re-assign to " + oclass;
            LogManager.getLogger().fatal(s);
            throw new IllegalArgumentException(s);
        } else if (this.field_150769_h.containsValue(oclass)) {
            s = "Serverbound packet " + oclass + " is already assigned to ID " + this.field_150769_h.inverse().get(oclass) + "; cannot re-assign to " + i;
            LogManager.getLogger().fatal(s);
            throw new IllegalArgumentException(s);
        } else {
            this.field_150769_h.put(i, oclass);
            return this;
        }
    }

    protected EnumConnectionState func_150756_b(int i, Class oclass) {
        String s;

        if (this.field_150770_i.containsKey(i)) {
            s = "Clientbound packet ID " + i + " is already assigned to " + this.field_150770_i.get(i) + "; cannot re-assign to " + oclass;
            LogManager.getLogger().fatal(s);
            throw new IllegalArgumentException(s);
        } else if (this.field_150770_i.containsValue(oclass)) {
            s = "Clientbound packet " + oclass + " is already assigned to ID " + this.field_150770_i.inverse().get(oclass) + "; cannot re-assign to " + i;
            LogManager.getLogger().fatal(s);
            throw new IllegalArgumentException(s);
        } else {
            this.field_150770_i.put(i, oclass);
            return this;
        }
    }

    public BiMap func_150753_a() {
        return this.field_150769_h;
    }

    public BiMap func_150755_b() {
        return this.field_150770_i;
    }

    public BiMap func_150757_a(boolean flag) {
        return flag ? this.func_150755_b() : this.func_150753_a();
    }

    public BiMap func_150754_b(boolean flag) {
        return flag ? this.func_150753_a() : this.func_150755_b();
    }

    public int func_150759_c() {
        return this.field_150762_g;
    }

    public static EnumConnectionState func_150760_a(int i) {
        return (EnumConnectionState) EnumConnectionState.field_150764_e.get(i);
    }

    public static EnumConnectionState func_150752_a(Packet packet) {
        return (EnumConnectionState) EnumConnectionState.field_150761_f.get(packet.getClass());
    }

    static {
        EnumConnectionState[] aenumconnectionstate = values();
        int i = aenumconnectionstate.length;

        for (int j = 0; j < i; ++j) {
            EnumConnectionState enumconnectionstate = aenumconnectionstate[j];

            EnumConnectionState.field_150764_e.put(enumconnectionstate.func_150759_c(), enumconnectionstate);
            Iterator iterator = Iterables.concat(enumconnectionstate.func_150755_b().values(), enumconnectionstate.func_150753_a().values()).iterator();

            while (iterator.hasNext()) {
                Class oclass = (Class) iterator.next();

                if (EnumConnectionState.field_150761_f.containsKey(oclass) && EnumConnectionState.field_150761_f.get(oclass) != enumconnectionstate) {
                    throw new Error("Packet " + oclass + " is already assigned to protocol " + EnumConnectionState.field_150761_f.get(oclass) + " - can't reassign to " + enumconnectionstate);
                }

                EnumConnectionState.field_150761_f.put(oclass, enumconnectionstate);
            }
        }

    }
}
