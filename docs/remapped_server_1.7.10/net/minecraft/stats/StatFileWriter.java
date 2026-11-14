package net.minecraft.stats;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.TupleIntJsonSerializable;

public class StatFileWriter {

    protected final Map field_150875_a = Maps.newConcurrentMap();

    public StatFileWriter() {}

    public boolean func_77443_a(Achievement achievement) {
        return this.func_77444_a(achievement) > 0;
    }

    public boolean func_77442_b(Achievement achievement) {
        return achievement.field_75992_c == null || this.func_77443_a(achievement.field_75992_c);
    }

    public void func_150871_b(EntityPlayer entityplayer, StatBase statbase, int i) {
        if (!statbase.func_75967_d() || this.func_77442_b((Achievement) statbase)) {
            this.func_150873_a(entityplayer, statbase, this.func_77444_a(statbase) + i);
        }
    }

    public void func_150873_a(EntityPlayer entityplayer, StatBase statbase, int i) {
        TupleIntJsonSerializable tupleintjsonserializable = (TupleIntJsonSerializable) this.field_150875_a.get(statbase);

        if (tupleintjsonserializable == null) {
            tupleintjsonserializable = new TupleIntJsonSerializable();
            this.field_150875_a.put(statbase, tupleintjsonserializable);
        }

        tupleintjsonserializable.func_151188_a(i);
    }

    public int func_77444_a(StatBase statbase) {
        TupleIntJsonSerializable tupleintjsonserializable = (TupleIntJsonSerializable) this.field_150875_a.get(statbase);

        return tupleintjsonserializable == null ? 0 : tupleintjsonserializable.func_151189_a();
    }

    public IJsonSerializable func_150870_b(StatBase statbase) {
        TupleIntJsonSerializable tupleintjsonserializable = (TupleIntJsonSerializable) this.field_150875_a.get(statbase);

        return tupleintjsonserializable != null ? tupleintjsonserializable.func_151187_b() : null;
    }

    public IJsonSerializable func_150872_a(StatBase statbase, IJsonSerializable ijsonserializable) {
        TupleIntJsonSerializable tupleintjsonserializable = (TupleIntJsonSerializable) this.field_150875_a.get(statbase);

        if (tupleintjsonserializable == null) {
            tupleintjsonserializable = new TupleIntJsonSerializable();
            this.field_150875_a.put(statbase, tupleintjsonserializable);
        }

        tupleintjsonserializable.func_151190_a(ijsonserializable);
        return ijsonserializable;
    }
}
