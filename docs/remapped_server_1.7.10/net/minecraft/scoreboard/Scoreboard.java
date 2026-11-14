package net.minecraft.scoreboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Scoreboard {

    private final Map field_96545_a = new HashMap();
    private final Map field_96543_b = new HashMap();
    private final Map field_96544_c = new HashMap();
    private final ScoreObjective[] field_96541_d = new ScoreObjective[3];
    private final Map field_96542_e = new HashMap();
    private final Map field_96540_f = new HashMap();

    public Scoreboard() {}

    public ScoreObjective func_96518_b(String s) {
        return (ScoreObjective) this.field_96545_a.get(s);
    }

    public ScoreObjective func_96535_a(String s, IScoreObjectiveCriteria iscoreobjectivecriteria) {
        ScoreObjective scoreobjective = this.func_96518_b(s);

        if (scoreobjective != null) {
            throw new IllegalArgumentException("An objective with the name '" + s + "' already exists!");
        } else {
            scoreobjective = new ScoreObjective(this, s, iscoreobjectivecriteria);
            Object object = (List) this.field_96543_b.get(iscoreobjectivecriteria);

            if (object == null) {
                object = new ArrayList();
                this.field_96543_b.put(iscoreobjectivecriteria, object);
            }

            ((List) object).add(scoreobjective);
            this.field_96545_a.put(s, scoreobjective);
            this.func_96522_a(scoreobjective);
            return scoreobjective;
        }
    }

    public Collection func_96520_a(IScoreObjectiveCriteria iscoreobjectivecriteria) {
        Collection collection = (Collection) this.field_96543_b.get(iscoreobjectivecriteria);

        return collection == null ? new ArrayList() : new ArrayList(collection);
    }

    public Score func_96529_a(String s, ScoreObjective scoreobjective) {
        Object object = (Map) this.field_96544_c.get(s);

        if (object == null) {
            object = new HashMap();
            this.field_96544_c.put(s, object);
        }

        Score score = (Score) ((Map) object).get(scoreobjective);

        if (score == null) {
            score = new Score(this, scoreobjective, s);
            ((Map) object).put(scoreobjective, score);
        }

        return score;
    }

    public Collection func_96534_i(ScoreObjective scoreobjective) {
        ArrayList arraylist = new ArrayList();
        Iterator iterator = this.field_96544_c.values().iterator();

        while (iterator.hasNext()) {
            Map map = (Map) iterator.next();
            Score score = (Score) map.get(scoreobjective);

            if (score != null) {
                arraylist.add(score);
            }
        }

        Collections.sort(arraylist, Score.field_96658_a);
        return arraylist;
    }

    public Collection func_96514_c() {
        return this.field_96545_a.values();
    }

    public Collection func_96526_d() {
        return this.field_96544_c.keySet();
    }

    public void func_96515_c(String s) {
        Map map = (Map) this.field_96544_c.remove(s);

        if (map != null) {
            this.func_96516_a(s);
        }

    }

    public Collection func_96528_e() {
        Collection collection = this.field_96544_c.values();
        ArrayList arraylist = new ArrayList();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Map map = (Map) iterator.next();

            arraylist.addAll(map.values());
        }

        return arraylist;
    }

    public Map func_96510_d(String s) {
        Object object = (Map) this.field_96544_c.get(s);

        if (object == null) {
            object = new HashMap();
        }

        return (Map) object;
    }

    public void func_96519_k(ScoreObjective scoreobjective) {
        this.field_96545_a.remove(scoreobjective.func_96679_b());

        for (int i = 0; i < 3; ++i) {
            if (this.func_96539_a(i) == scoreobjective) {
                this.func_96530_a(i, (ScoreObjective) null);
            }
        }

        List list = (List) this.field_96543_b.get(scoreobjective.func_96680_c());

        if (list != null) {
            list.remove(scoreobjective);
        }

        Iterator iterator = this.field_96544_c.values().iterator();

        while (iterator.hasNext()) {
            Map map = (Map) iterator.next();

            map.remove(scoreobjective);
        }

        this.func_96533_c(scoreobjective);
    }

    public void func_96530_a(int i, ScoreObjective scoreobjective) {
        this.field_96541_d[i] = scoreobjective;
    }

    public ScoreObjective func_96539_a(int i) {
        return this.field_96541_d[i];
    }

    public ScorePlayerTeam func_96508_e(String s) {
        return (ScorePlayerTeam) this.field_96542_e.get(s);
    }

    public ScorePlayerTeam func_96527_f(String s) {
        ScorePlayerTeam scoreplayerteam = this.func_96508_e(s);

        if (scoreplayerteam != null) {
            throw new IllegalArgumentException("A team with the name '" + s + "' already exists!");
        } else {
            scoreplayerteam = new ScorePlayerTeam(this, s);
            this.field_96542_e.put(s, scoreplayerteam);
            this.func_96523_a(scoreplayerteam);
            return scoreplayerteam;
        }
    }

    public void func_96511_d(ScorePlayerTeam scoreplayerteam) {
        this.field_96542_e.remove(scoreplayerteam.func_96661_b());
        Iterator iterator = scoreplayerteam.func_96670_d().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            this.field_96540_f.remove(s);
        }

        this.func_96513_c(scoreplayerteam);
    }

    public boolean func_151392_a(String s, String s1) {
        if (!this.field_96542_e.containsKey(s1)) {
            return false;
        } else {
            ScorePlayerTeam scoreplayerteam = this.func_96508_e(s1);

            if (this.func_96509_i(s) != null) {
                this.func_96524_g(s);
            }

            this.field_96540_f.put(s, scoreplayerteam);
            scoreplayerteam.func_96670_d().add(s);
            return true;
        }
    }

    public boolean func_96524_g(String s) {
        ScorePlayerTeam scoreplayerteam = this.func_96509_i(s);

        if (scoreplayerteam != null) {
            this.func_96512_b(s, scoreplayerteam);
            return true;
        } else {
            return false;
        }
    }

    public void func_96512_b(String s, ScorePlayerTeam scoreplayerteam) {
        if (this.func_96509_i(s) != scoreplayerteam) {
            throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + scoreplayerteam.func_96661_b() + "'.");
        } else {
            this.field_96540_f.remove(s);
            scoreplayerteam.func_96670_d().remove(s);
        }
    }

    public Collection func_96531_f() {
        return this.field_96542_e.keySet();
    }

    public Collection func_96525_g() {
        return this.field_96542_e.values();
    }

    public ScorePlayerTeam func_96509_i(String s) {
        return (ScorePlayerTeam) this.field_96540_f.get(s);
    }

    public void func_96522_a(ScoreObjective scoreobjective) {}

    public void func_96532_b(ScoreObjective scoreobjective) {}

    public void func_96533_c(ScoreObjective scoreobjective) {}

    public void func_96536_a(Score score) {}

    public void func_96516_a(String s) {}

    public void func_96523_a(ScorePlayerTeam scoreplayerteam) {}

    public void func_96538_b(ScorePlayerTeam scoreplayerteam) {}

    public void func_96513_c(ScorePlayerTeam scoreplayerteam) {}

    public static String func_96517_b(int i) {
        switch (i) {
            case 0:
                return "list";
            case 1:
                return "sidebar";
            case 2:
                return "belowName";
            default:
                return null;
        }
    }

    public static int func_96537_j(String s) {
        return s.equalsIgnoreCase("list") ? 0 : (s.equalsIgnoreCase("sidebar") ? 1 : (s.equalsIgnoreCase("belowName") ? 2 : -1));
    }
}
