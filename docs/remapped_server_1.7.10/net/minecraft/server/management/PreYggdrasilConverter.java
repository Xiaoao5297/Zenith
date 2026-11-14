package net.minecraft.server.management;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.PropertyManager;
import net.minecraft.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PreYggdrasilConverter {

    private static final Logger field_152732_e = LogManager.getLogger();
    public static final File field_152728_a = new File("banned-ips.txt");
    public static final File field_152729_b = new File("banned-players.txt");
    public static final File field_152730_c = new File("ops.txt");
    public static final File field_152731_d = new File("white-list.txt");

    static List func_152721_a(File file, Map map) {
        List list = Files.readLines(file, Charsets.UTF_8);
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            s = s.trim();
            if (!s.startsWith("#") && s.length() >= 1) {
                String[] astring = s.split("\\|");

                map.put(astring[0].toLowerCase(Locale.ROOT), astring);
            }
        }

        return list;
    }

    private static void func_152717_a(MinecraftServer minecraftserver, Collection collection, ProfileLookupCallback profilelookupcallback) {
        String[] astring = (String[]) Iterators.toArray(Iterators.filter(collection.iterator(), (Predicate) (new PreYggdrasilConverter$1())), String.class);

        if (minecraftserver.func_71266_T()) {
            minecraftserver.func_152359_aw().findProfilesByNames(astring, Agent.MINECRAFT, profilelookupcallback);
        } else {
            String[] astring1 = astring;
            int i = astring.length;

            for (int j = 0; j < i; ++j) {
                String s = astring1[j];
                UUID uuid = EntityPlayer.func_146094_a(new GameProfile((UUID) null, s));
                GameProfile gameprofile = new GameProfile(uuid, s);

                profilelookupcallback.onProfileLookupSucceeded(gameprofile);
            }
        }

    }

    public static boolean func_152724_a(MinecraftServer minecraftserver) {
        UserListBans userlistbans = new UserListBans(ServerConfigurationManager.field_152613_a);

        if (PreYggdrasilConverter.field_152729_b.exists() && PreYggdrasilConverter.field_152729_b.isFile()) {
            if (userlistbans.func_152691_c().exists()) {
                try {
                    userlistbans.func_152679_g();
                } catch (FileNotFoundException filenotfoundexception) {
                    PreYggdrasilConverter.field_152732_e.warn("Could not load existing file " + userlistbans.func_152691_c().getName(), (Throwable) filenotfoundexception);
                }
            }

            try {
                HashMap hashmap = Maps.newHashMap();

                func_152721_a(PreYggdrasilConverter.field_152729_b, hashmap);
                PreYggdrasilConverter$2 preyggdrasilconverter$2 = new PreYggdrasilConverter$2(minecraftserver, hashmap, userlistbans);

                func_152717_a(minecraftserver, hashmap.keySet(), preyggdrasilconverter$2);
                userlistbans.func_152678_f();
                func_152727_c(PreYggdrasilConverter.field_152729_b);
                return true;
            } catch (IOException ioexception) {
                PreYggdrasilConverter.field_152732_e.warn("Could not read old user banlist to convert it!", (Throwable) ioexception);
                return false;
            } catch (PreYggdrasilConverter$ConversionError preyggdrasilconverter$conversionerror) {
                PreYggdrasilConverter.field_152732_e.error("Conversion failed, please try again later", (Throwable) preyggdrasilconverter$conversionerror);
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean func_152722_b(MinecraftServer minecraftserver) {
        BanList banlist = new BanList(ServerConfigurationManager.field_152614_b);

        if (PreYggdrasilConverter.field_152728_a.exists() && PreYggdrasilConverter.field_152728_a.isFile()) {
            if (banlist.func_152691_c().exists()) {
                try {
                    banlist.func_152679_g();
                } catch (FileNotFoundException filenotfoundexception) {
                    PreYggdrasilConverter.field_152732_e.warn("Could not load existing file " + banlist.func_152691_c().getName(), (Throwable) filenotfoundexception);
                }
            }

            try {
                HashMap hashmap = Maps.newHashMap();

                func_152721_a(PreYggdrasilConverter.field_152728_a, hashmap);
                Iterator iterator = hashmap.keySet().iterator();

                while (iterator.hasNext()) {
                    String s = (String) iterator.next();
                    String[] astring = (String[]) hashmap.get(s);
                    Date date = astring.length > 1 ? func_152713_b(astring[1], (Date) null) : null;
                    String s1 = astring.length > 2 ? astring[2] : null;
                    Date date1 = astring.length > 3 ? func_152713_b(astring[3], (Date) null) : null;
                    String s2 = astring.length > 4 ? astring[4] : null;

                    banlist.func_152687_a(new IPBanEntry(s, date, s1, date1, s2));
                }

                banlist.func_152678_f();
                func_152727_c(PreYggdrasilConverter.field_152728_a);
                return true;
            } catch (IOException ioexception) {
                PreYggdrasilConverter.field_152732_e.warn("Could not parse old ip banlist to convert it!", (Throwable) ioexception);
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean func_152718_c(MinecraftServer minecraftserver) {
        UserListOps userlistops = new UserListOps(ServerConfigurationManager.field_152615_c);

        if (PreYggdrasilConverter.field_152730_c.exists() && PreYggdrasilConverter.field_152730_c.isFile()) {
            if (userlistops.func_152691_c().exists()) {
                try {
                    userlistops.func_152679_g();
                } catch (FileNotFoundException filenotfoundexception) {
                    PreYggdrasilConverter.field_152732_e.warn("Could not load existing file " + userlistops.func_152691_c().getName(), (Throwable) filenotfoundexception);
                }
            }

            try {
                List list = Files.readLines(PreYggdrasilConverter.field_152730_c, Charsets.UTF_8);
                PreYggdrasilConverter$3 preyggdrasilconverter$3 = new PreYggdrasilConverter$3(minecraftserver, userlistops);

                func_152717_a(minecraftserver, list, preyggdrasilconverter$3);
                userlistops.func_152678_f();
                func_152727_c(PreYggdrasilConverter.field_152730_c);
                return true;
            } catch (IOException ioexception) {
                PreYggdrasilConverter.field_152732_e.warn("Could not read old oplist to convert it!", (Throwable) ioexception);
                return false;
            } catch (PreYggdrasilConverter$ConversionError preyggdrasilconverter$conversionerror) {
                PreYggdrasilConverter.field_152732_e.error("Conversion failed, please try again later", (Throwable) preyggdrasilconverter$conversionerror);
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean func_152710_d(MinecraftServer minecraftserver) {
        UserListWhitelist userlistwhitelist = new UserListWhitelist(ServerConfigurationManager.field_152616_d);

        if (PreYggdrasilConverter.field_152731_d.exists() && PreYggdrasilConverter.field_152731_d.isFile()) {
            if (userlistwhitelist.func_152691_c().exists()) {
                try {
                    userlistwhitelist.func_152679_g();
                } catch (FileNotFoundException filenotfoundexception) {
                    PreYggdrasilConverter.field_152732_e.warn("Could not load existing file " + userlistwhitelist.func_152691_c().getName(), (Throwable) filenotfoundexception);
                }
            }

            try {
                List list = Files.readLines(PreYggdrasilConverter.field_152731_d, Charsets.UTF_8);
                PreYggdrasilConverter$4 preyggdrasilconverter$4 = new PreYggdrasilConverter$4(minecraftserver, userlistwhitelist);

                func_152717_a(minecraftserver, list, preyggdrasilconverter$4);
                userlistwhitelist.func_152678_f();
                func_152727_c(PreYggdrasilConverter.field_152731_d);
                return true;
            } catch (IOException ioexception) {
                PreYggdrasilConverter.field_152732_e.warn("Could not read old whitelist to convert it!", (Throwable) ioexception);
                return false;
            } catch (PreYggdrasilConverter$ConversionError preyggdrasilconverter$conversionerror) {
                PreYggdrasilConverter.field_152732_e.error("Conversion failed, please try again later", (Throwable) preyggdrasilconverter$conversionerror);
                return false;
            }
        } else {
            return true;
        }
    }

    public static String func_152719_a(String s) {
        if (!StringUtils.func_151246_b(s) && s.length() <= 16) {
            MinecraftServer minecraftserver = MinecraftServer.func_71276_C();
            GameProfile gameprofile = minecraftserver.func_152358_ax().func_152655_a(s);

            if (gameprofile != null && gameprofile.getId() != null) {
                return gameprofile.getId().toString();
            } else if (!minecraftserver.func_71264_H() && minecraftserver.func_71266_T()) {
                ArrayList arraylist = Lists.newArrayList();
                PreYggdrasilConverter$5 preyggdrasilconverter$5 = new PreYggdrasilConverter$5(minecraftserver, arraylist);

                func_152717_a(minecraftserver, Lists.newArrayList((Object[]) (s)), preyggdrasilconverter$5);
                return arraylist.size() > 0 && ((GameProfile) arraylist.get(0)).getId() != null ? ((GameProfile) arraylist.get(0)).getId().toString() : "";
            } else {
                return EntityPlayer.func_146094_a(new GameProfile((UUID) null, s)).toString();
            }
        } else {
            return s;
        }
    }

    public static boolean func_152723_a(DedicatedServer dedicatedserver, PropertyManager propertymanager) {
        File file = func_152725_d(propertymanager);
        File file1 = new File(file.getParentFile(), "playerdata");
        File file2 = new File(file.getParentFile(), "unknownplayers");

        if (file.exists() && file.isDirectory()) {
            File[] afile = file.listFiles();
            ArrayList arraylist = Lists.newArrayList();
            File[] afile1 = afile;
            int i = afile.length;

            for (int j = 0; j < i; ++j) {
                File file3 = afile1[j];
                String s = file3.getName();

                if (s.toLowerCase(Locale.ROOT).endsWith(".dat")) {
                    String s1 = s.substring(0, s.length() - ".dat".length());

                    if (s1.length() > 0) {
                        arraylist.add(s1);
                    }
                }
            }

            try {
                String[] astring = (String[]) arraylist.toArray(new String[arraylist.size()]);
                PreYggdrasilConverter$6 preyggdrasilconverter$6 = new PreYggdrasilConverter$6(dedicatedserver, file1, file2, file, astring);

                func_152717_a(dedicatedserver, Lists.newArrayList((Object[]) astring), preyggdrasilconverter$6);
                return true;
            } catch (PreYggdrasilConverter$ConversionError preyggdrasilconverter$conversionerror) {
                PreYggdrasilConverter.field_152732_e.error("Conversion failed, please try again later", (Throwable) preyggdrasilconverter$conversionerror);
                return false;
            }
        } else {
            return true;
        }
    }

    private static void func_152711_b(File file) {
        if (file.exists()) {
            if (!file.isDirectory()) {
                throw new PreYggdrasilConverter$ConversionError("Can't create directory " + file.getName() + " in world save directory.", (PreYggdrasilConverter$1) null);
            }
        } else if (!file.mkdirs()) {
            throw new PreYggdrasilConverter$ConversionError("Can't create directory " + file.getName() + " in world save directory.", (PreYggdrasilConverter$1) null);
        }
    }

    public static boolean func_152714_a(PropertyManager propertymanager) {
        boolean flag = func_152712_b(propertymanager);

        flag = flag && func_152715_c(propertymanager);
        return flag;
    }

    private static boolean func_152712_b(PropertyManager propertymanager) {
        boolean flag = false;

        if (PreYggdrasilConverter.field_152729_b.exists() && PreYggdrasilConverter.field_152729_b.isFile()) {
            flag = true;
        }

        boolean flag1 = false;

        if (PreYggdrasilConverter.field_152728_a.exists() && PreYggdrasilConverter.field_152728_a.isFile()) {
            flag1 = true;
        }

        boolean flag2 = false;

        if (PreYggdrasilConverter.field_152730_c.exists() && PreYggdrasilConverter.field_152730_c.isFile()) {
            flag2 = true;
        }

        boolean flag3 = false;

        if (PreYggdrasilConverter.field_152731_d.exists() && PreYggdrasilConverter.field_152731_d.isFile()) {
            flag3 = true;
        }

        if (!flag && !flag1 && !flag2 && !flag3) {
            return true;
        } else {
            PreYggdrasilConverter.field_152732_e.warn("**** FAILED TO START THE SERVER AFTER ACCOUNT CONVERSION!");
            PreYggdrasilConverter.field_152732_e.warn("** please remove the following files and restart the server:");
            if (flag) {
                PreYggdrasilConverter.field_152732_e.warn("* " + PreYggdrasilConverter.field_152729_b.getName());
            }

            if (flag1) {
                PreYggdrasilConverter.field_152732_e.warn("* " + PreYggdrasilConverter.field_152728_a.getName());
            }

            if (flag2) {
                PreYggdrasilConverter.field_152732_e.warn("* " + PreYggdrasilConverter.field_152730_c.getName());
            }

            if (flag3) {
                PreYggdrasilConverter.field_152732_e.warn("* " + PreYggdrasilConverter.field_152731_d.getName());
            }

            return false;
        }
    }

    private static boolean func_152715_c(PropertyManager propertymanager) {
        File file = func_152725_d(propertymanager);

        if (file.exists() && file.isDirectory()) {
            String[] astring = file.list(new PreYggdrasilConverter$7());

            if (astring.length > 0) {
                PreYggdrasilConverter.field_152732_e.warn("**** DETECTED OLD PLAYER FILES IN THE WORLD SAVE");
                PreYggdrasilConverter.field_152732_e.warn("**** THIS USUALLY HAPPENS WHEN THE AUTOMATIC CONVERSION FAILED IN SOME WAY");
                PreYggdrasilConverter.field_152732_e.warn("** please restart the server and if the problem persists, remove the directory '{}'", file.getPath());
                return false;
            }
        }

        return true;
    }

    private static File func_152725_d(PropertyManager propertymanager) {
        String s = propertymanager.func_73671_a("level-name", "world");
        File file = new File(s);

        return new File(file, "players");
    }

    private static void func_152727_c(File file) {
        File file1 = new File(file.getName() + ".converted");

        file.renameTo(file1);
    }

    private static Date func_152713_b(String s, Date date) {
        Date date1;

        try {
            date1 = BanEntry.field_73698_a.parse(s);
        } catch (ParseException parseexception) {
            date1 = date;
        }

        return date1;
    }
}
