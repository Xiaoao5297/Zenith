package net.minecraft.scoreboard;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class ScoreHealthCriteria extends ScoreDummyCriteria {

    public ScoreHealthCriteria(String s) {
        super(s);
    }

    @Override
    public int func_96635_a(List list) {
        float f = 0.0F;

        EntityPlayer entityplayer;

        for (Iterator iterator = list.iterator(); iterator.hasNext(); f += entityplayer.func_110143_aJ() + entityplayer.func_110139_bj()) {
            entityplayer = (EntityPlayer) iterator.next();
        }

        if (list.size() > 0) {
            f /= (float) list.size();
        }

        return MathHelper.func_76123_f(f);
    }

    @Override
    public boolean func_96637_b() {
        return true;
    }
}
