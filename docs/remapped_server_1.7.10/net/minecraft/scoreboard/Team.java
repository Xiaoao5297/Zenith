package net.minecraft.scoreboard;

public abstract class Team {

    public Team() {}

    public boolean func_142054_a(Team team) {
        return team == null ? false : this == team;
    }

    public abstract String func_96661_b();

    public abstract String func_142053_d(String s);

    public abstract boolean func_96665_g();
}
