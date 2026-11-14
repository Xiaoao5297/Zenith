package net.minecraft.command.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class CommandScoreboard extends CommandBase {

    public CommandScoreboard() {}

    @Override
    public String func_71517_b() {
        return "scoreboard";
    }

    @Override
    public int func_82362_a() {
        return 2;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.scoreboard.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length >= 1) {
            if (astring[0].equalsIgnoreCase("objectives")) {
                if (astring.length == 1) {
                    throw new WrongUsageException("commands.scoreboard.objectives.usage", new Object[0]);
                }

                if (astring[1].equalsIgnoreCase("list")) {
                    this.func_147196_d(icommandsender);
                } else if (astring[1].equalsIgnoreCase("add")) {
                    if (astring.length < 4) {
                        throw new WrongUsageException("commands.scoreboard.objectives.add.usage", new Object[0]);
                    }

                    this.func_147193_c(icommandsender, astring, 2);
                } else if (astring[1].equalsIgnoreCase("remove")) {
                    if (astring.length != 3) {
                        throw new WrongUsageException("commands.scoreboard.objectives.remove.usage", new Object[0]);
                    }

                    this.func_147191_h(icommandsender, astring[2]);
                } else {
                    if (!astring[1].equalsIgnoreCase("setdisplay")) {
                        throw new WrongUsageException("commands.scoreboard.objectives.usage", new Object[0]);
                    }

                    if (astring.length != 3 && astring.length != 4) {
                        throw new WrongUsageException("commands.scoreboard.objectives.setdisplay.usage", new Object[0]);
                    }

                    this.func_147198_k(icommandsender, astring, 2);
                }

                return;
            }

            if (astring[0].equalsIgnoreCase("players")) {
                if (astring.length == 1) {
                    throw new WrongUsageException("commands.scoreboard.players.usage", new Object[0]);
                }

                if (astring[1].equalsIgnoreCase("list")) {
                    if (astring.length > 3) {
                        throw new WrongUsageException("commands.scoreboard.players.list.usage", new Object[0]);
                    }

                    this.func_147195_l(icommandsender, astring, 2);
                } else if (astring[1].equalsIgnoreCase("add")) {
                    if (astring.length != 5) {
                        throw new WrongUsageException("commands.scoreboard.players.add.usage", new Object[0]);
                    }

                    this.func_147197_m(icommandsender, astring, 2);
                } else if (astring[1].equalsIgnoreCase("remove")) {
                    if (astring.length != 5) {
                        throw new WrongUsageException("commands.scoreboard.players.remove.usage", new Object[0]);
                    }

                    this.func_147197_m(icommandsender, astring, 2);
                } else if (astring[1].equalsIgnoreCase("set")) {
                    if (astring.length != 5) {
                        throw new WrongUsageException("commands.scoreboard.players.set.usage", new Object[0]);
                    }

                    this.func_147197_m(icommandsender, astring, 2);
                } else {
                    if (!astring[1].equalsIgnoreCase("reset")) {
                        throw new WrongUsageException("commands.scoreboard.players.usage", new Object[0]);
                    }

                    if (astring.length != 3) {
                        throw new WrongUsageException("commands.scoreboard.players.reset.usage", new Object[0]);
                    }

                    this.func_147187_n(icommandsender, astring, 2);
                }

                return;
            }

            if (astring[0].equalsIgnoreCase("teams")) {
                if (astring.length == 1) {
                    throw new WrongUsageException("commands.scoreboard.teams.usage", new Object[0]);
                }

                if (astring[1].equalsIgnoreCase("list")) {
                    if (astring.length > 3) {
                        throw new WrongUsageException("commands.scoreboard.teams.list.usage", new Object[0]);
                    }

                    this.func_147186_g(icommandsender, astring, 2);
                } else if (astring[1].equalsIgnoreCase("add")) {
                    if (astring.length < 3) {
                        throw new WrongUsageException("commands.scoreboard.teams.add.usage", new Object[0]);
                    }

                    this.func_147185_d(icommandsender, astring, 2);
                } else if (astring[1].equalsIgnoreCase("remove")) {
                    if (astring.length != 3) {
                        throw new WrongUsageException("commands.scoreboard.teams.remove.usage", new Object[0]);
                    }

                    this.func_147194_f(icommandsender, astring, 2);
                } else if (astring[1].equalsIgnoreCase("empty")) {
                    if (astring.length != 3) {
                        throw new WrongUsageException("commands.scoreboard.teams.empty.usage", new Object[0]);
                    }

                    this.func_147188_j(icommandsender, astring, 2);
                } else if (astring[1].equalsIgnoreCase("join")) {
                    if (astring.length < 4 && (astring.length != 3 || !(icommandsender instanceof EntityPlayer))) {
                        throw new WrongUsageException("commands.scoreboard.teams.join.usage", new Object[0]);
                    }

                    this.func_147190_h(icommandsender, astring, 2);
                } else if (astring[1].equalsIgnoreCase("leave")) {
                    if (astring.length < 3 && !(icommandsender instanceof EntityPlayer)) {
                        throw new WrongUsageException("commands.scoreboard.teams.leave.usage", new Object[0]);
                    }

                    this.func_147199_i(icommandsender, astring, 2);
                } else {
                    if (!astring[1].equalsIgnoreCase("option")) {
                        throw new WrongUsageException("commands.scoreboard.teams.usage", new Object[0]);
                    }

                    if (astring.length != 4 && astring.length != 5) {
                        throw new WrongUsageException("commands.scoreboard.teams.option.usage", new Object[0]);
                    }

                    this.func_147200_e(icommandsender, astring, 2);
                }

                return;
            }
        }

        throw new WrongUsageException("commands.scoreboard.usage", new Object[0]);
    }

    protected Scoreboard func_147192_d() {
        return MinecraftServer.func_71276_C().func_71218_a(0).func_96441_U();
    }

    protected ScoreObjective func_147189_a(String s, boolean flag) {
        Scoreboard scoreboard = this.func_147192_d();
        ScoreObjective scoreobjective = scoreboard.func_96518_b(s);

        if (scoreobjective == null) {
            throw new CommandException("commands.scoreboard.objectiveNotFound", new Object[]{s});
        } else if (flag && scoreobjective.func_96680_c().func_96637_b()) {
            throw new CommandException("commands.scoreboard.objectiveReadOnly", new Object[]{s});
        } else {
            return scoreobjective;
        }
    }

    protected ScorePlayerTeam func_147183_a(String s) {
        Scoreboard scoreboard = this.func_147192_d();
        ScorePlayerTeam scoreplayerteam = scoreboard.func_96508_e(s);

        if (scoreplayerteam == null) {
            throw new CommandException("commands.scoreboard.teamNotFound", new Object[]{s});
        } else {
            return scoreplayerteam;
        }
    }

    protected void func_147193_c(ICommandSender icommandsender, String[] astring, int i) {
        String s = astring[i++];
        String s1 = astring[i++];
        Scoreboard scoreboard = this.func_147192_d();
        IScoreObjectiveCriteria iscoreobjectivecriteria = (IScoreObjectiveCriteria) IScoreObjectiveCriteria.field_96643_a.get(s1);

        if (iscoreobjectivecriteria == null) {
            throw new WrongUsageException("commands.scoreboard.objectives.add.wrongType", new Object[]{s1});
        } else if (scoreboard.func_96518_b(s) != null) {
            throw new CommandException("commands.scoreboard.objectives.add.alreadyExists", new Object[]{s});
        } else if (s.length() > 16) {
            throw new SyntaxErrorException("commands.scoreboard.objectives.add.tooLong", new Object[]{s, 16});
        } else if (s.length() == 0) {
            throw new WrongUsageException("commands.scoreboard.objectives.add.usage", new Object[0]);
        } else {
            if (astring.length > i) {
                String s2 = func_147178_a(icommandsender, astring, i).func_150260_c();

                if (s2.length() > 32) {
                    throw new SyntaxErrorException("commands.scoreboard.objectives.add.displayTooLong", new Object[]{s2, 32});
                }

                if (s2.length() > 0) {
                    scoreboard.func_96535_a(s, iscoreobjectivecriteria).func_96681_a(s2);
                } else {
                    scoreboard.func_96535_a(s, iscoreobjectivecriteria);
                }
            } else {
                scoreboard.func_96535_a(s, iscoreobjectivecriteria);
            }

            func_152373_a(icommandsender, this, "commands.scoreboard.objectives.add.success", new Object[]{s});
        }
    }

    protected void func_147185_d(ICommandSender icommandsender, String[] astring, int i) {
        String s = astring[i++];
        Scoreboard scoreboard = this.func_147192_d();

        if (scoreboard.func_96508_e(s) != null) {
            throw new CommandException("commands.scoreboard.teams.add.alreadyExists", new Object[]{s});
        } else if (s.length() > 16) {
            throw new SyntaxErrorException("commands.scoreboard.teams.add.tooLong", new Object[]{s, 16});
        } else if (s.length() == 0) {
            throw new WrongUsageException("commands.scoreboard.teams.add.usage", new Object[0]);
        } else {
            if (astring.length > i) {
                String s1 = func_147178_a(icommandsender, astring, i).func_150260_c();

                if (s1.length() > 32) {
                    throw new SyntaxErrorException("commands.scoreboard.teams.add.displayTooLong", new Object[]{s1, 32});
                }

                if (s1.length() > 0) {
                    scoreboard.func_96527_f(s).func_96664_a(s1);
                } else {
                    scoreboard.func_96527_f(s);
                }
            } else {
                scoreboard.func_96527_f(s);
            }

            func_152373_a(icommandsender, this, "commands.scoreboard.teams.add.success", new Object[]{s});
        }
    }

    protected void func_147200_e(ICommandSender icommandsender, String[] astring, int i) {
        ScorePlayerTeam scoreplayerteam = this.func_147183_a(astring[i++]);

        if (scoreplayerteam != null) {
            String s = astring[i++].toLowerCase();

            if (!s.equalsIgnoreCase("color") && !s.equalsIgnoreCase("friendlyfire") && !s.equalsIgnoreCase("seeFriendlyInvisibles")) {
                throw new WrongUsageException("commands.scoreboard.teams.option.usage", new Object[0]);
            } else if (astring.length == 4) {
                if (s.equalsIgnoreCase("color")) {
                    throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[]{s, func_96333_a(EnumChatFormatting.func_96296_a(true, false))});
                } else if (!s.equalsIgnoreCase("friendlyfire") && !s.equalsIgnoreCase("seeFriendlyInvisibles")) {
                    throw new WrongUsageException("commands.scoreboard.teams.option.usage", new Object[0]);
                } else {
                    throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[]{s, func_96333_a(Arrays.asList("true", "false"))});
                }
            } else {
                String s1 = astring[i++];

                if (s.equalsIgnoreCase("color")) {
                    EnumChatFormatting enumchatformatting = EnumChatFormatting.func_96300_b(s1);

                    if (enumchatformatting == null || enumchatformatting.func_96301_b()) {
                        throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[]{s, func_96333_a(EnumChatFormatting.func_96296_a(true, false))});
                    }

                    scoreplayerteam.func_96666_b(enumchatformatting.toString());
                    scoreplayerteam.func_96662_c(EnumChatFormatting.RESET.toString());
                } else if (s.equalsIgnoreCase("friendlyfire")) {
                    if (!s1.equalsIgnoreCase("true") && !s1.equalsIgnoreCase("false")) {
                        throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[]{s, func_96333_a(Arrays.asList("true", "false"))});
                    }

                    scoreplayerteam.func_96660_a(s1.equalsIgnoreCase("true"));
                } else if (s.equalsIgnoreCase("seeFriendlyInvisibles")) {
                    if (!s1.equalsIgnoreCase("true") && !s1.equalsIgnoreCase("false")) {
                        throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[]{s, func_96333_a(Arrays.asList("true", "false"))});
                    }

                    scoreplayerteam.func_98300_b(s1.equalsIgnoreCase("true"));
                }

                func_152373_a(icommandsender, this, "commands.scoreboard.teams.option.success", new Object[]{s, scoreplayerteam.func_96661_b(), s1});
            }
        }
    }

    protected void func_147194_f(ICommandSender icommandsender, String[] astring, int i) {
        Scoreboard scoreboard = this.func_147192_d();
        ScorePlayerTeam scoreplayerteam = this.func_147183_a(astring[i++]);

        if (scoreplayerteam != null) {
            scoreboard.func_96511_d(scoreplayerteam);
            func_152373_a(icommandsender, this, "commands.scoreboard.teams.remove.success", new Object[]{scoreplayerteam.func_96661_b()});
        }
    }

    protected void func_147186_g(ICommandSender icommandsender, String[] astring, int i) {
        Scoreboard scoreboard = this.func_147192_d();

        if (astring.length > i) {
            ScorePlayerTeam scoreplayerteam = this.func_147183_a(astring[i++]);

            if (scoreplayerteam == null) {
                return;
            }

            Collection collection = scoreplayerteam.func_96670_d();

            if (collection.size() <= 0) {
                throw new CommandException("commands.scoreboard.teams.list.player.empty", new Object[]{scoreplayerteam.func_96661_b()});
            }

            ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("commands.scoreboard.teams.list.player.count", new Object[]{collection.size(), scoreplayerteam.func_96661_b()});

            chatcomponenttranslation.func_150256_b().func_150238_a(EnumChatFormatting.DARK_GREEN);
            icommandsender.func_145747_a(chatcomponenttranslation);
            icommandsender.func_145747_a(new ChatComponentText(func_71527_a(collection.toArray())));
        } else {
            Collection collection1 = scoreboard.func_96525_g();

            if (collection1.size() <= 0) {
                throw new CommandException("commands.scoreboard.teams.list.empty", new Object[0]);
            }

            ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation("commands.scoreboard.teams.list.count", new Object[]{collection1.size()});

            chatcomponenttranslation1.func_150256_b().func_150238_a(EnumChatFormatting.DARK_GREEN);
            icommandsender.func_145747_a(chatcomponenttranslation1);
            Iterator iterator = collection1.iterator();

            while (iterator.hasNext()) {
                ScorePlayerTeam scoreplayerteam1 = (ScorePlayerTeam) iterator.next();

                icommandsender.func_145747_a(new ChatComponentTranslation("commands.scoreboard.teams.list.entry", new Object[]{scoreplayerteam1.func_96661_b(), scoreplayerteam1.func_96669_c(), scoreplayerteam1.func_96670_d().size()}));
            }
        }

    }

    protected void func_147190_h(ICommandSender icommandsender, String[] astring, int i) {
        Scoreboard scoreboard = this.func_147192_d();
        String s = astring[i++];
        HashSet hashset = new HashSet();
        HashSet hashset1 = new HashSet();
        String s1;

        if (icommandsender instanceof EntityPlayer && i == astring.length) {
            s1 = func_71521_c(icommandsender).func_70005_c_();
            if (scoreboard.func_151392_a(s1, s)) {
                hashset.add(s1);
            } else {
                hashset1.add(s1);
            }
        } else {
            while (i < astring.length) {
                s1 = func_96332_d(icommandsender, astring[i++]);
                if (scoreboard.func_151392_a(s1, s)) {
                    hashset.add(s1);
                } else {
                    hashset1.add(s1);
                }
            }
        }

        if (!hashset.isEmpty()) {
            func_152373_a(icommandsender, this, "commands.scoreboard.teams.join.success", new Object[]{hashset.size(), s, func_71527_a(hashset.toArray(new String[0]))});
        }

        if (!hashset1.isEmpty()) {
            throw new CommandException("commands.scoreboard.teams.join.failure", new Object[]{hashset1.size(), s, func_71527_a(hashset1.toArray(new String[0]))});
        }
    }

    protected void func_147199_i(ICommandSender icommandsender, String[] astring, int i) {
        Scoreboard scoreboard = this.func_147192_d();
        HashSet hashset = new HashSet();
        HashSet hashset1 = new HashSet();
        String s;

        if (icommandsender instanceof EntityPlayer && i == astring.length) {
            s = func_71521_c(icommandsender).func_70005_c_();
            if (scoreboard.func_96524_g(s)) {
                hashset.add(s);
            } else {
                hashset1.add(s);
            }
        } else {
            while (i < astring.length) {
                s = func_96332_d(icommandsender, astring[i++]);
                if (scoreboard.func_96524_g(s)) {
                    hashset.add(s);
                } else {
                    hashset1.add(s);
                }
            }
        }

        if (!hashset.isEmpty()) {
            func_152373_a(icommandsender, this, "commands.scoreboard.teams.leave.success", new Object[]{hashset.size(), func_71527_a(hashset.toArray(new String[0]))});
        }

        if (!hashset1.isEmpty()) {
            throw new CommandException("commands.scoreboard.teams.leave.failure", new Object[]{hashset1.size(), func_71527_a(hashset1.toArray(new String[0]))});
        }
    }

    protected void func_147188_j(ICommandSender icommandsender, String[] astring, int i) {
        Scoreboard scoreboard = this.func_147192_d();
        ScorePlayerTeam scoreplayerteam = this.func_147183_a(astring[i++]);

        if (scoreplayerteam != null) {
            ArrayList arraylist = new ArrayList(scoreplayerteam.func_96670_d());

            if (arraylist.isEmpty()) {
                throw new CommandException("commands.scoreboard.teams.empty.alreadyEmpty", new Object[]{scoreplayerteam.func_96661_b()});
            } else {
                Iterator iterator = arraylist.iterator();

                while (iterator.hasNext()) {
                    String s = (String) iterator.next();

                    scoreboard.func_96512_b(s, scoreplayerteam);
                }

                func_152373_a(icommandsender, this, "commands.scoreboard.teams.empty.success", new Object[]{arraylist.size(), scoreplayerteam.func_96661_b()});
            }
        }
    }

    protected void func_147191_h(ICommandSender icommandsender, String s) {
        Scoreboard scoreboard = this.func_147192_d();
        ScoreObjective scoreobjective = this.func_147189_a(s, false);

        scoreboard.func_96519_k(scoreobjective);
        func_152373_a(icommandsender, this, "commands.scoreboard.objectives.remove.success", new Object[]{s});
    }

    protected void func_147196_d(ICommandSender icommandsender) {
        Scoreboard scoreboard = this.func_147192_d();
        Collection collection = scoreboard.func_96514_c();

        if (collection.size() <= 0) {
            throw new CommandException("commands.scoreboard.objectives.list.empty", new Object[0]);
        } else {
            ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("commands.scoreboard.objectives.list.count", new Object[]{collection.size()});

            chatcomponenttranslation.func_150256_b().func_150238_a(EnumChatFormatting.DARK_GREEN);
            icommandsender.func_145747_a(chatcomponenttranslation);
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                ScoreObjective scoreobjective = (ScoreObjective) iterator.next();

                icommandsender.func_145747_a(new ChatComponentTranslation("commands.scoreboard.objectives.list.entry", new Object[]{scoreobjective.func_96679_b(), scoreobjective.func_96678_d(), scoreobjective.func_96680_c().func_96636_a()}));
            }

        }
    }

    protected void func_147198_k(ICommandSender icommandsender, String[] astring, int i) {
        Scoreboard scoreboard = this.func_147192_d();
        String s = astring[i++];
        int j = Scoreboard.func_96537_j(s);
        ScoreObjective scoreobjective = null;

        if (astring.length == 4) {
            scoreobjective = this.func_147189_a(astring[i++], false);
        }

        if (j < 0) {
            throw new CommandException("commands.scoreboard.objectives.setdisplay.invalidSlot", new Object[]{s});
        } else {
            scoreboard.func_96530_a(j, scoreobjective);
            if (scoreobjective != null) {
                func_152373_a(icommandsender, this, "commands.scoreboard.objectives.setdisplay.successSet", new Object[]{Scoreboard.func_96517_b(j), scoreobjective.func_96679_b()});
            } else {
                func_152373_a(icommandsender, this, "commands.scoreboard.objectives.setdisplay.successCleared", new Object[]{Scoreboard.func_96517_b(j)});
            }

        }
    }

    protected void func_147195_l(ICommandSender icommandsender, String[] astring, int i) {
        Scoreboard scoreboard = this.func_147192_d();

        if (astring.length > i) {
            String s = func_96332_d(icommandsender, astring[i++]);
            Map map = scoreboard.func_96510_d(s);

            if (map.size() <= 0) {
                throw new CommandException("commands.scoreboard.players.list.player.empty", new Object[]{s});
            }

            ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("commands.scoreboard.players.list.player.count", new Object[]{map.size(), s});

            chatcomponenttranslation.func_150256_b().func_150238_a(EnumChatFormatting.DARK_GREEN);
            icommandsender.func_145747_a(chatcomponenttranslation);
            Iterator iterator = map.values().iterator();

            while (iterator.hasNext()) {
                Score score = (Score) iterator.next();

                icommandsender.func_145747_a(new ChatComponentTranslation("commands.scoreboard.players.list.player.entry", new Object[]{score.func_96652_c(), score.func_96645_d().func_96678_d(), score.func_96645_d().func_96679_b()}));
            }
        } else {
            Collection collection = scoreboard.func_96526_d();

            if (collection.size() <= 0) {
                throw new CommandException("commands.scoreboard.players.list.empty", new Object[0]);
            }

            ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation("commands.scoreboard.players.list.count", new Object[]{collection.size()});

            chatcomponenttranslation1.func_150256_b().func_150238_a(EnumChatFormatting.DARK_GREEN);
            icommandsender.func_145747_a(chatcomponenttranslation1);
            icommandsender.func_145747_a(new ChatComponentText(func_71527_a(collection.toArray())));
        }

    }

    protected void func_147197_m(ICommandSender icommandsender, String[] astring, int i) {
        String s = astring[i - 1];
        String s1 = func_96332_d(icommandsender, astring[i++]);
        ScoreObjective scoreobjective = this.func_147189_a(astring[i++], true);
        int j = s.equalsIgnoreCase("set") ? func_71526_a(icommandsender, astring[i++]) : func_71528_a(icommandsender, astring[i++], 1);
        Scoreboard scoreboard = this.func_147192_d();
        Score score = scoreboard.func_96529_a(s1, scoreobjective);

        if (s.equalsIgnoreCase("set")) {
            score.func_96647_c(j);
        } else if (s.equalsIgnoreCase("add")) {
            score.func_96649_a(j);
        } else {
            score.func_96646_b(j);
        }

        func_152373_a(icommandsender, this, "commands.scoreboard.players.set.success", new Object[]{scoreobjective.func_96679_b(), s1, score.func_96652_c()});
    }

    protected void func_147187_n(ICommandSender icommandsender, String[] astring, int i) {
        Scoreboard scoreboard = this.func_147192_d();
        String s = func_96332_d(icommandsender, astring[i++]);

        scoreboard.func_96515_c(s);
        func_152373_a(icommandsender, this, "commands.scoreboard.players.reset.success", new Object[]{s});
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        if (astring.length == 1) {
            return func_71530_a(astring, new String[]{"objectives", "players", "teams"});
        } else {
            if (astring[0].equalsIgnoreCase("objectives")) {
                if (astring.length == 2) {
                    return func_71530_a(astring, new String[]{"list", "add", "remove", "setdisplay"});
                }

                if (astring[1].equalsIgnoreCase("add")) {
                    if (astring.length == 4) {
                        Set set = IScoreObjectiveCriteria.field_96643_a.keySet();

                        return func_71531_a(astring, set);
                    }
                } else if (astring[1].equalsIgnoreCase("remove")) {
                    if (astring.length == 3) {
                        return func_71531_a(astring, this.func_147184_a(false));
                    }
                } else if (astring[1].equalsIgnoreCase("setdisplay")) {
                    if (astring.length == 3) {
                        return func_71530_a(astring, new String[]{"list", "sidebar", "belowName"});
                    }

                    if (astring.length == 4) {
                        return func_71531_a(astring, this.func_147184_a(false));
                    }
                }
            } else if (astring[0].equalsIgnoreCase("players")) {
                if (astring.length == 2) {
                    return func_71530_a(astring, new String[]{"set", "add", "remove", "reset", "list"});
                }

                if (!astring[1].equalsIgnoreCase("set") && !astring[1].equalsIgnoreCase("add") && !astring[1].equalsIgnoreCase("remove")) {
                    if ((astring[1].equalsIgnoreCase("reset") || astring[1].equalsIgnoreCase("list")) && astring.length == 3) {
                        return func_71531_a(astring, this.func_147192_d().func_96526_d());
                    }
                } else {
                    if (astring.length == 3) {
                        return func_71530_a(astring, MinecraftServer.func_71276_C().func_71213_z());
                    }

                    if (astring.length == 4) {
                        return func_71531_a(astring, this.func_147184_a(true));
                    }
                }
            } else if (astring[0].equalsIgnoreCase("teams")) {
                if (astring.length == 2) {
                    return func_71530_a(astring, new String[]{"add", "remove", "join", "leave", "empty", "list", "option"});
                }

                if (astring[1].equalsIgnoreCase("join")) {
                    if (astring.length == 3) {
                        return func_71531_a(astring, this.func_147192_d().func_96531_f());
                    }

                    if (astring.length >= 4) {
                        return func_71530_a(astring, MinecraftServer.func_71276_C().func_71213_z());
                    }
                } else {
                    if (astring[1].equalsIgnoreCase("leave")) {
                        return func_71530_a(astring, MinecraftServer.func_71276_C().func_71213_z());
                    }

                    if (!astring[1].equalsIgnoreCase("empty") && !astring[1].equalsIgnoreCase("list") && !astring[1].equalsIgnoreCase("remove")) {
                        if (astring[1].equalsIgnoreCase("option")) {
                            if (astring.length == 3) {
                                return func_71531_a(astring, this.func_147192_d().func_96531_f());
                            }

                            if (astring.length == 4) {
                                return func_71530_a(astring, new String[]{"color", "friendlyfire", "seeFriendlyInvisibles"});
                            }

                            if (astring.length == 5) {
                                if (astring[3].equalsIgnoreCase("color")) {
                                    return func_71531_a(astring, EnumChatFormatting.func_96296_a(true, false));
                                }

                                if (astring[3].equalsIgnoreCase("friendlyfire") || astring[3].equalsIgnoreCase("seeFriendlyInvisibles")) {
                                    return func_71530_a(astring, new String[]{"true", "false"});
                                }
                            }
                        }
                    } else if (astring.length == 3) {
                        return func_71531_a(astring, this.func_147192_d().func_96531_f());
                    }
                }
            }

            return null;
        }
    }

    protected List func_147184_a(boolean flag) {
        Collection collection = this.func_147192_d().func_96514_c();
        ArrayList arraylist = new ArrayList();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            ScoreObjective scoreobjective = (ScoreObjective) iterator.next();

            if (!flag || !scoreobjective.func_96680_c().func_96637_b()) {
                arraylist.add(scoreobjective.func_96679_b());
            }
        }

        return arraylist;
    }

    @Override
    public boolean func_82358_a(String[] astring, int i) {
        return astring[0].equalsIgnoreCase("players") ? i == 2 : (!astring[0].equalsIgnoreCase("teams") ? false : i == 2 || i == 3);
    }
}
