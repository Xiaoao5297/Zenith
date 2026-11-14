package net.minecraft.scoreboard;

public class ScoreObjective {

    private final Scoreboard field_96686_a;
    private final String field_96684_b;
    private final IScoreObjectiveCriteria field_96685_c;
    private String field_96683_d;

    public ScoreObjective(Scoreboard scoreboard, String s, IScoreObjectiveCriteria iscoreobjectivecriteria) {
        this.field_96686_a = scoreboard;
        this.field_96684_b = s;
        this.field_96685_c = iscoreobjectivecriteria;
        this.field_96683_d = s;
    }

    public String func_96679_b() {
        return this.field_96684_b;
    }

    public IScoreObjectiveCriteria func_96680_c() {
        return this.field_96685_c;
    }

    public String func_96678_d() {
        return this.field_96683_d;
    }

    public void func_96681_a(String s) {
        this.field_96683_d = s;
        this.field_96686_a.func_96532_b(this);
    }
}
