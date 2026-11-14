package net.minecraft.scoreboard;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ScorePlayerTeam extends Team {

    private final Scoreboard field_96677_a;
    private final String field_96675_b;
    private final Set field_96676_c = new HashSet();
    private String field_96673_d;
    private String field_96674_e = "";
    private String field_96671_f = "";
    private boolean field_96672_g = true;
    private boolean field_98301_h = true;

    public ScorePlayerTeam(Scoreboard scoreboard, String s) {
        this.field_96677_a = scoreboard;
        this.field_96675_b = s;
        this.field_96673_d = s;
    }

    @Override
    public String func_96661_b() {
        return this.field_96675_b;
    }

    public String func_96669_c() {
        return this.field_96673_d;
    }

    public void func_96664_a(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Name cannot be null");
        } else {
            this.field_96673_d = s;
            this.field_96677_a.func_96538_b(this);
        }
    }

    public Collection func_96670_d() {
        return this.field_96676_c;
    }

    public String func_96668_e() {
        return this.field_96674_e;
    }

    public void func_96666_b(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Prefix cannot be null");
        } else {
            this.field_96674_e = s;
            this.field_96677_a.func_96538_b(this);
        }
    }

    public String func_96663_f() {
        return this.field_96671_f;
    }

    public void func_96662_c(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Suffix cannot be null");
        } else {
            this.field_96671_f = s;
            this.field_96677_a.func_96538_b(this);
        }
    }

    @Override
    public String func_142053_d(String s) {
        return this.func_96668_e() + s + this.func_96663_f();
    }

    public static String func_96667_a(Team team, String s) {
        return team == null ? s : team.func_142053_d(s);
    }

    @Override
    public boolean func_96665_g() {
        return this.field_96672_g;
    }

    public void func_96660_a(boolean flag) {
        this.field_96672_g = flag;
        this.field_96677_a.func_96538_b(this);
    }

    public boolean func_98297_h() {
        return this.field_98301_h;
    }

    public void func_98300_b(boolean flag) {
        this.field_98301_h = flag;
        this.field_96677_a.func_96538_b(this);
    }

    public int func_98299_i() {
        int i = 0;

        if (this.func_96665_g()) {
            i |= 1;
        }

        if (this.func_98297_h()) {
            i |= 2;
        }

        return i;
    }
}
