package net.minecraft.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CommandSpreadPlayers extends CommandBase {

    public CommandSpreadPlayers() {}

    @Override
    public String func_71517_b() {
        return "spreadplayers";
    }

    @Override
    public int func_82362_a() {
        return 2;
    }

    @Override
    public String func_71518_a(ICommandSender icommandsender) {
        return "commands.spreadplayers.usage";
    }

    @Override
    public void func_71515_b(ICommandSender icommandsender, String[] astring) {
        if (astring.length < 6) {
            throw new WrongUsageException("commands.spreadplayers.usage", new Object[0]);
        } else {
            int i = 0;
            double d0 = func_110666_a(icommandsender, Double.NaN, astring[i++]);
            double d1 = func_110666_a(icommandsender, Double.NaN, astring[i++]);
            double d2 = func_110664_a(icommandsender, astring[i++], 0.0D);
            double d3 = func_110664_a(icommandsender, astring[i++], d2 + 1.0D);
            boolean flag = func_110662_c(icommandsender, astring[i++]);
            ArrayList arraylist = Lists.newArrayList();

            while (true) {
                while (i < astring.length) {
                    String s = astring[i++];

                    if (PlayerSelector.func_82378_b(s)) {
                        EntityPlayerMP[] aentityplayermp = PlayerSelector.func_82380_c(icommandsender, s);

                        if (aentityplayermp == null || aentityplayermp.length == 0) {
                            throw new PlayerNotFoundException();
                        }

                        Collections.addAll(arraylist, aentityplayermp);
                    } else {
                        EntityPlayerMP entityplayermp = MinecraftServer.func_71276_C().func_71203_ab().func_152612_a(s);

                        if (entityplayermp == null) {
                            throw new PlayerNotFoundException();
                        }

                        arraylist.add(entityplayermp);
                    }
                }

                if (arraylist.isEmpty()) {
                    throw new PlayerNotFoundException();
                }

                icommandsender.func_145747_a(new ChatComponentTranslation("commands.spreadplayers.spreading." + (flag ? "teams" : "players"), new Object[]{arraylist.size(), d3, d0, d1, d2}));
                this.func_110669_a(icommandsender, arraylist, new CommandSpreadPlayers$Position(d0, d1), d2, d3, ((EntityLivingBase) arraylist.get(0)).field_70170_p, flag);
                return;
            }
        }
    }

    private void func_110669_a(ICommandSender icommandsender, List list, CommandSpreadPlayers$Position commandspreadplayers$position, double d0, double d1, World world, boolean flag) {
        Random random = new Random();
        double d2 = commandspreadplayers$position.field_111101_a - d1;
        double d3 = commandspreadplayers$position.field_111100_b - d1;
        double d4 = commandspreadplayers$position.field_111101_a + d1;
        double d5 = commandspreadplayers$position.field_111100_b + d1;
        CommandSpreadPlayers$Position[] acommandspreadplayers$position = this.func_110670_a(random, flag ? this.func_110667_a(list) : list.size(), d2, d3, d4, d5);
        int i = this.func_110668_a(commandspreadplayers$position, d0, world, random, d2, d3, d4, d5, acommandspreadplayers$position, flag);
        double d6 = this.func_110671_a(list, world, acommandspreadplayers$position, flag);

        func_152373_a(icommandsender, this, "commands.spreadplayers.success." + (flag ? "teams" : "players"), new Object[]{acommandspreadplayers$position.length, commandspreadplayers$position.field_111101_a, commandspreadplayers$position.field_111100_b});
        if (acommandspreadplayers$position.length > 1) {
            icommandsender.func_145747_a(new ChatComponentTranslation("commands.spreadplayers.info." + (flag ? "teams" : "players"), new Object[]{String.format("%.2f", d6), i}));
        }

    }

    private int func_110667_a(List list) {
        HashSet hashset = Sets.newHashSet();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            EntityLivingBase entitylivingbase = (EntityLivingBase) iterator.next();

            if (entitylivingbase instanceof EntityPlayer) {
                hashset.add(entitylivingbase.func_96124_cp());
            } else {
                hashset.add((Object) null);
            }
        }

        return hashset.size();
    }

    private int func_110668_a(CommandSpreadPlayers$Position commandspreadplayers$position, double d0, World world, Random random, double d1, double d2, double d3, double d4, CommandSpreadPlayers$Position[] acommandspreadplayers$position, boolean flag) {
        boolean flag1 = true;
        double d5 = 3.4028234663852886E38D;

        int i;

        for (i = 0; i < 10000 && flag1; ++i) {
            flag1 = false;
            d5 = 3.4028234663852886E38D;

            int j;
            CommandSpreadPlayers$Position commandspreadplayers$position1;

            for (int k = 0; k < acommandspreadplayers$position.length; ++k) {
                CommandSpreadPlayers$Position commandspreadplayers$position2 = acommandspreadplayers$position[k];

                j = 0;
                commandspreadplayers$position1 = new CommandSpreadPlayers$Position();

                for (int l = 0; l < acommandspreadplayers$position.length; ++l) {
                    if (k != l) {
                        CommandSpreadPlayers$Position commandspreadplayers$position3 = acommandspreadplayers$position[l];
                        double d6 = commandspreadplayers$position2.func_111099_a(commandspreadplayers$position3);

                        d5 = Math.min(d6, d5);
                        if (d6 < d0) {
                            ++j;
                            commandspreadplayers$position1.field_111101_a += commandspreadplayers$position3.field_111101_a - commandspreadplayers$position2.field_111101_a;
                            commandspreadplayers$position1.field_111100_b += commandspreadplayers$position3.field_111100_b - commandspreadplayers$position2.field_111100_b;
                        }
                    }
                }

                if (j > 0) {
                    commandspreadplayers$position1.field_111101_a /= (double) j;
                    commandspreadplayers$position1.field_111100_b /= (double) j;
                    double d7 = (double) commandspreadplayers$position1.func_111096_b();

                    if (d7 > 0.0D) {
                        commandspreadplayers$position1.func_111095_a();
                        commandspreadplayers$position2.func_111094_b(commandspreadplayers$position1);
                    } else {
                        commandspreadplayers$position2.func_111097_a(random, d1, d2, d3, d4);
                    }

                    flag1 = true;
                }

                if (commandspreadplayers$position2.func_111093_a(d1, d2, d3, d4)) {
                    flag1 = true;
                }
            }

            if (!flag1) {
                CommandSpreadPlayers$Position[] acommandspreadplayers$position1 = acommandspreadplayers$position;
                int i1 = acommandspreadplayers$position.length;

                for (j = 0; j < i1; ++j) {
                    commandspreadplayers$position1 = acommandspreadplayers$position1[j];
                    if (!commandspreadplayers$position1.func_111098_b(world)) {
                        commandspreadplayers$position1.func_111097_a(random, d1, d2, d3, d4);
                        flag1 = true;
                    }
                }
            }
        }

        if (i >= 10000) {
            throw new CommandException("commands.spreadplayers.failure." + (flag ? "teams" : "players"), new Object[]{acommandspreadplayers$position.length, commandspreadplayers$position.field_111101_a, commandspreadplayers$position.field_111100_b, String.format("%.2f", d5)});
        } else {
            return i;
        }
    }

    private double func_110671_a(List list, World world, CommandSpreadPlayers$Position[] acommandspreadplayers$position, boolean flag) {
        double d0 = 0.0D;
        int i = 0;
        HashMap hashmap = Maps.newHashMap();

        for (int j = 0; j < list.size(); ++j) {
            EntityLivingBase entitylivingbase = (EntityLivingBase) list.get(j);
            CommandSpreadPlayers$Position commandspreadplayers$position;

            if (flag) {
                Team team = entitylivingbase instanceof EntityPlayer ? entitylivingbase.func_96124_cp() : null;

                if (!hashmap.containsKey(team)) {
                    hashmap.put(team, acommandspreadplayers$position[i++]);
                }

                commandspreadplayers$position = (CommandSpreadPlayers$Position) hashmap.get(team);
            } else {
                commandspreadplayers$position = acommandspreadplayers$position[i++];
            }

            entitylivingbase.func_70634_a((double) ((float) MathHelper.func_76128_c(commandspreadplayers$position.field_111101_a) + 0.5F), (double) commandspreadplayers$position.func_111092_a(world), (double) MathHelper.func_76128_c(commandspreadplayers$position.field_111100_b) + 0.5D);
            double d1 = Double.MAX_VALUE;

            for (int k = 0; k < acommandspreadplayers$position.length; ++k) {
                if (commandspreadplayers$position != acommandspreadplayers$position[k]) {
                    double d2 = commandspreadplayers$position.func_111099_a(acommandspreadplayers$position[k]);

                    d1 = Math.min(d2, d1);
                }
            }

            d0 += d1;
        }

        d0 /= (double) list.size();
        return d0;
    }

    private CommandSpreadPlayers$Position[] func_110670_a(Random random, int i, double d0, double d1, double d2, double d3) {
        CommandSpreadPlayers$Position[] acommandspreadplayers$position = new CommandSpreadPlayers$Position[i];

        for (int j = 0; j < acommandspreadplayers$position.length; ++j) {
            CommandSpreadPlayers$Position commandspreadplayers$position = new CommandSpreadPlayers$Position();

            commandspreadplayers$position.func_111097_a(random, d0, d1, d2, d3);
            acommandspreadplayers$position[j] = commandspreadplayers$position;
        }

        return acommandspreadplayers$position;
    }
}
