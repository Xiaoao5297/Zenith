package net.minecraft.scoreboard;

import java.util.Comparator;

final class Score$1 implements Comparator {

    Score$1() {}

    public int compare(Score score, Score score1) {
        return score.func_96652_c() > score1.func_96652_c() ? 1 : (score.func_96652_c() < score1.func_96652_c() ? -1 : 0);
    }
}
