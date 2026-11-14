package net.minecraft.stats;

import net.minecraft.scoreboard.ScoreDummyCriteria;

public class ObjectiveStat extends ScoreDummyCriteria {

    private final StatBase field_151459_g;

    public ObjectiveStat(StatBase statbase) {
        super(statbase.field_75975_e);
        this.field_151459_g = statbase;
    }
}
