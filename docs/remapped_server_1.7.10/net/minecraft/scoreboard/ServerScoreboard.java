package net.minecraft.scoreboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.server.MinecraftServer;

public class ServerScoreboard extends Scoreboard {

    private final MinecraftServer field_96555_a;
    private final Set field_96553_b = new HashSet();
    private ScoreboardSaveData field_96554_c;

    public ServerScoreboard(MinecraftServer minecraftserver) {
        this.field_96555_a = minecraftserver;
    }

    @Override
    public void func_96536_a(Score score) {
        super.func_96536_a(score);
        if (this.field_96553_b.contains(score.func_96645_d())) {
            this.field_96555_a.func_71203_ab().func_148540_a(new S3CPacketUpdateScore(score, 0));
        }

        this.func_96551_b();
    }

    @Override
    public void func_96516_a(String s) {
        super.func_96516_a(s);
        this.field_96555_a.func_71203_ab().func_148540_a(new S3CPacketUpdateScore(s));
        this.func_96551_b();
    }

    @Override
    public void func_96530_a(int i, ScoreObjective scoreobjective) {
        ScoreObjective scoreobjective1 = this.func_96539_a(i);

        super.func_96530_a(i, scoreobjective);
        if (scoreobjective1 != scoreobjective && scoreobjective1 != null) {
            if (this.func_96552_h(scoreobjective1) > 0) {
                this.field_96555_a.func_71203_ab().func_148540_a(new S3DPacketDisplayScoreboard(i, scoreobjective));
            } else {
                this.func_96546_g(scoreobjective1);
            }
        }

        if (scoreobjective != null) {
            if (this.field_96553_b.contains(scoreobjective)) {
                this.field_96555_a.func_71203_ab().func_148540_a(new S3DPacketDisplayScoreboard(i, scoreobjective));
            } else {
                this.func_96549_e(scoreobjective);
            }
        }

        this.func_96551_b();
    }

    @Override
    public boolean func_151392_a(String s, String s1) {
        if (super.func_151392_a(s, s1)) {
            ScorePlayerTeam scoreplayerteam = this.func_96508_e(s1);

            this.field_96555_a.func_71203_ab().func_148540_a(new S3EPacketTeams(scoreplayerteam, Arrays.asList(s), 3));
            this.func_96551_b();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void func_96512_b(String s, ScorePlayerTeam scoreplayerteam) {
        super.func_96512_b(s, scoreplayerteam);
        this.field_96555_a.func_71203_ab().func_148540_a(new S3EPacketTeams(scoreplayerteam, Arrays.asList(s), 4));
        this.func_96551_b();
    }

    @Override
    public void func_96522_a(ScoreObjective scoreobjective) {
        super.func_96522_a(scoreobjective);
        this.func_96551_b();
    }

    @Override
    public void func_96532_b(ScoreObjective scoreobjective) {
        super.func_96532_b(scoreobjective);
        if (this.field_96553_b.contains(scoreobjective)) {
            this.field_96555_a.func_71203_ab().func_148540_a(new S3BPacketScoreboardObjective(scoreobjective, 2));
        }

        this.func_96551_b();
    }

    @Override
    public void func_96533_c(ScoreObjective scoreobjective) {
        super.func_96533_c(scoreobjective);
        if (this.field_96553_b.contains(scoreobjective)) {
            this.func_96546_g(scoreobjective);
        }

        this.func_96551_b();
    }

    @Override
    public void func_96523_a(ScorePlayerTeam scoreplayerteam) {
        super.func_96523_a(scoreplayerteam);
        this.field_96555_a.func_71203_ab().func_148540_a(new S3EPacketTeams(scoreplayerteam, 0));
        this.func_96551_b();
    }

    @Override
    public void func_96538_b(ScorePlayerTeam scoreplayerteam) {
        super.func_96538_b(scoreplayerteam);
        this.field_96555_a.func_71203_ab().func_148540_a(new S3EPacketTeams(scoreplayerteam, 2));
        this.func_96551_b();
    }

    @Override
    public void func_96513_c(ScorePlayerTeam scoreplayerteam) {
        super.func_96513_c(scoreplayerteam);
        this.field_96555_a.func_71203_ab().func_148540_a(new S3EPacketTeams(scoreplayerteam, 1));
        this.func_96551_b();
    }

    public void func_96547_a(ScoreboardSaveData scoreboardsavedata) {
        this.field_96554_c = scoreboardsavedata;
    }

    protected void func_96551_b() {
        if (this.field_96554_c != null) {
            this.field_96554_c.func_76185_a();
        }

    }

    public List func_96550_d(ScoreObjective scoreobjective) {
        ArrayList arraylist = new ArrayList();

        arraylist.add(new S3BPacketScoreboardObjective(scoreobjective, 0));

        for (int i = 0; i < 3; ++i) {
            if (this.func_96539_a(i) == scoreobjective) {
                arraylist.add(new S3DPacketDisplayScoreboard(i, scoreobjective));
            }
        }

        Iterator iterator = this.func_96534_i(scoreobjective).iterator();

        while (iterator.hasNext()) {
            Score score = (Score) iterator.next();

            arraylist.add(new S3CPacketUpdateScore(score, 0));
        }

        return arraylist;
    }

    public void func_96549_e(ScoreObjective scoreobjective) {
        List list = this.func_96550_d(scoreobjective);
        Iterator iterator = this.field_96555_a.func_71203_ab().field_72404_b.iterator();

        while (iterator.hasNext()) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) iterator.next();
            Iterator iterator1 = list.iterator();

            while (iterator1.hasNext()) {
                Packet packet = (Packet) iterator1.next();

                entityplayermp.field_71135_a.func_147359_a(packet);
            }
        }

        this.field_96553_b.add(scoreobjective);
    }

    public List func_96548_f(ScoreObjective scoreobjective) {
        ArrayList arraylist = new ArrayList();

        arraylist.add(new S3BPacketScoreboardObjective(scoreobjective, 1));

        for (int i = 0; i < 3; ++i) {
            if (this.func_96539_a(i) == scoreobjective) {
                arraylist.add(new S3DPacketDisplayScoreboard(i, scoreobjective));
            }
        }

        return arraylist;
    }

    public void func_96546_g(ScoreObjective scoreobjective) {
        List list = this.func_96548_f(scoreobjective);
        Iterator iterator = this.field_96555_a.func_71203_ab().field_72404_b.iterator();

        while (iterator.hasNext()) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) iterator.next();
            Iterator iterator1 = list.iterator();

            while (iterator1.hasNext()) {
                Packet packet = (Packet) iterator1.next();

                entityplayermp.field_71135_a.func_147359_a(packet);
            }
        }

        this.field_96553_b.remove(scoreobjective);
    }

    public int func_96552_h(ScoreObjective scoreobjective) {
        int i = 0;

        for (int j = 0; j < 3; ++j) {
            if (this.func_96539_a(j) == scoreobjective) {
                ++i;
            }
        }

        return i;
    }
}
