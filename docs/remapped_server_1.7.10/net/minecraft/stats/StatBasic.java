package net.minecraft.stats;

import net.minecraft.util.IChatComponent;

public class StatBasic extends StatBase {

    public StatBasic(String s, IChatComponent ichatcomponent, IStatType istattype) {
        super(s, ichatcomponent, istattype);
    }

    public StatBasic(String s, IChatComponent ichatcomponent) {
        super(s, ichatcomponent);
    }

    @Override
    public StatBase func_75971_g() {
        super.func_75971_g();
        StatList.field_75941_c.add(this);
        return this;
    }
}
