package net.minecraft.command.server;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

public class CommandAchievement extends CommandBase {

    public CommandAchievement() {}

    @Override
    public String func_71517_b() {
        return "achievement";
    }

    @Override
    public int func_82362_a() {
        return 2;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.achievement.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length >= 2) {
            StatBase statbase = StatList.func_151177_a(astring[1]);

            if (statbase == null && !astring[1].equals("*")) {
                throw new CommandException("commands.achievement.unknownAchievement", new Object[]{astring[1]});
            }

            EntityPlayerMP entityplayermp;

            if (astring.length >= 3) {
                entityplayermp = func_82359_c(icommandsender, astring[2]);
            } else {
                entityplayermp = func_71521_c(icommandsender);
            }

            if (astring[0].equalsIgnoreCase("give")) {
                if (statbase == null) {
                    Iterator iterator = AchievementList.field_76007_e.iterator();

                    while (iterator.hasNext()) {
                        Achievement achievement = (Achievement) iterator.next();

                        entityplayermp.func_71029_a(achievement);
                    }

                    func_152373_a(icommandsender, this, "commands.achievement.give.success.all", new Object[]{entityplayermp.func_70005_c_()});
                } else {
                    if (statbase instanceof Achievement) {
                        Achievement achievement1 = (Achievement) statbase;

                        ArrayList arraylist;

                        for (arraylist = Lists.newArrayList(); achievement1.field_75992_c != null && !entityplayermp.func_147099_x().func_77443_a(achievement1.field_75992_c); achievement1 = achievement1.field_75992_c) {
                            arraylist.add(achievement1.field_75992_c);
                        }

                        Iterator iterator1 = Lists.reverse(arraylist).iterator();

                        while (iterator1.hasNext()) {
                            Achievement achievement2 = (Achievement) iterator1.next();

                            entityplayermp.func_71029_a(achievement2);
                        }
                    }

                    entityplayermp.func_71029_a(statbase);
                    func_152373_a(icommandsender, this, "commands.achievement.give.success.one", new Object[]{entityplayermp.func_70005_c_(), statbase.func_150955_j()});
                }

                return;
            }
        }

        throw new WrongUsageException("commands.achievement.usage", new Object[0]);
    }

    @Override
    public List func_71516_a(ICommandSender icommandsender, String[] astring) {
        if (astring.length == 1) {
            return func_71530_a(astring, new String[]{"give"});
        } else if (astring.length != 2) {
            return astring.length == 3 ? func_71530_a(astring, MinecraftServer.func_71276_C().func_71213_z()) : null;
        } else {
            ArrayList arraylist = Lists.newArrayList();
            Iterator iterator = StatList.field_75940_b.iterator();

            while (iterator.hasNext()) {
                StatBase statbase = (StatBase) iterator.next();

                arraylist.add(statbase.field_75975_e);
            }

            return func_71531_a(astring, arraylist);
        }
    }

    @Override
    public boolean func_82358_a(String[] astring, int i) {
        return i == 2;
    }
}
