package net.minecraft.server.management;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.IOUtils;

public class PlayerProfileCache {

    public static final SimpleDateFormat field_152659_a = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private final Map field_152661_c = Maps.newHashMap();
    private final Map field_152662_d = Maps.newHashMap();
    private final LinkedList field_152663_e = Lists.newLinkedList();
    private final MinecraftServer field_152664_f;
    protected final Gson field_152660_b;
    private final File field_152665_g;
    private static final ParameterizedType field_152666_h = new PlayerProfileCache$2();

    public PlayerProfileCache(MinecraftServer minecraftserver, File file) {
        this.field_152664_f = minecraftserver;
        this.field_152665_g = file;
        GsonBuilder gsonbuilder = new GsonBuilder();

        gsonbuilder.registerTypeHierarchyAdapter(PlayerProfileCache$ProfileEntry.class, new PlayerProfileCache$Serializer(this, (PlayerProfileCache$1) null));
        this.field_152660_b = gsonbuilder.create();
        this.func_152657_b();
    }

    private static GameProfile func_152650_a(MinecraftServer minecraftserver, String s) {
        GameProfile[] agameprofile = new GameProfile[1];
        PlayerProfileCache$1 playerprofilecache$1 = new PlayerProfileCache$1(agameprofile);

        minecraftserver.func_152359_aw().findProfilesByNames(new String[]{s}, Agent.MINECRAFT, playerprofilecache$1);
        if (!minecraftserver.func_71266_T() && agameprofile[0] == null) {
            UUID uuid = EntityPlayer.func_146094_a(new GameProfile((UUID) null, s));
            GameProfile gameprofile = new GameProfile(uuid, s);

            playerprofilecache$1.onProfileLookupSucceeded(gameprofile);
        }

        return agameprofile[0];
    }

    public void func_152649_a(GameProfile gameprofile) {
        this.func_152651_a(gameprofile, (Date) null);
    }

    private void func_152651_a(GameProfile gameprofile, Date date) {
        UUID uuid = gameprofile.getId();

        if (date == null) {
            Calendar calendar = Calendar.getInstance();

            calendar.setTime(new Date());
            calendar.add(2, 1);
            date = calendar.getTime();
        }

        String s = gameprofile.getName().toLowerCase(Locale.ROOT);
        PlayerProfileCache$ProfileEntry playerprofilecache$profileentry = new PlayerProfileCache$ProfileEntry(this, gameprofile, date, (PlayerProfileCache$1) null);
        LinkedList linkedlist = this.field_152663_e;

        synchronized (this.field_152663_e) {
            if (this.field_152662_d.containsKey(uuid)) {
                PlayerProfileCache$ProfileEntry playerprofilecache$profileentry1 = (PlayerProfileCache$ProfileEntry) this.field_152662_d.get(uuid);

                this.field_152661_c.remove(playerprofilecache$profileentry1.func_152668_a().getName().toLowerCase(Locale.ROOT));
                this.field_152661_c.put(gameprofile.getName().toLowerCase(Locale.ROOT), playerprofilecache$profileentry);
                this.field_152663_e.remove(gameprofile);
            } else {
                this.field_152662_d.put(uuid, playerprofilecache$profileentry);
                this.field_152661_c.put(s, playerprofilecache$profileentry);
            }

            this.field_152663_e.addFirst(gameprofile);
        }
    }

    public GameProfile func_152655_a(String s) {
        String s1 = s.toLowerCase(Locale.ROOT);
        PlayerProfileCache$ProfileEntry playerprofilecache$profileentry = (PlayerProfileCache$ProfileEntry) this.field_152661_c.get(s1);

        if (playerprofilecache$profileentry != null && (new Date()).getTime() >= PlayerProfileCache$ProfileEntry.access$200(playerprofilecache$profileentry).getTime()) {
            this.field_152662_d.remove(playerprofilecache$profileentry.func_152668_a().getId());
            this.field_152661_c.remove(playerprofilecache$profileentry.func_152668_a().getName().toLowerCase(Locale.ROOT));
            LinkedList linkedlist = this.field_152663_e;

            synchronized (this.field_152663_e) {
                this.field_152663_e.remove(playerprofilecache$profileentry.func_152668_a());
            }

            playerprofilecache$profileentry = null;
        }

        GameProfile gameprofile;

        if (playerprofilecache$profileentry != null) {
            gameprofile = playerprofilecache$profileentry.func_152668_a();
            LinkedList linkedlist1 = this.field_152663_e;

            synchronized (this.field_152663_e) {
                this.field_152663_e.remove(gameprofile);
                this.field_152663_e.addFirst(gameprofile);
            }
        } else {
            gameprofile = func_152650_a(this.field_152664_f, s1);
            if (gameprofile != null) {
                this.func_152649_a(gameprofile);
                playerprofilecache$profileentry = (PlayerProfileCache$ProfileEntry) this.field_152661_c.get(s1);
            }
        }

        this.func_152658_c();
        return playerprofilecache$profileentry == null ? null : playerprofilecache$profileentry.func_152668_a();
    }

    public String[] func_152654_a() {
        ArrayList arraylist = Lists.newArrayList((Iterable) this.field_152661_c.keySet());

        return (String[]) arraylist.toArray(new String[arraylist.size()]);
    }

    public GameProfile func_152652_a(UUID uuid) {
        PlayerProfileCache$ProfileEntry playerprofilecache$profileentry = (PlayerProfileCache$ProfileEntry) this.field_152662_d.get(uuid);

        return playerprofilecache$profileentry == null ? null : playerprofilecache$profileentry.func_152668_a();
    }

    private PlayerProfileCache$ProfileEntry func_152653_b(UUID uuid) {
        PlayerProfileCache$ProfileEntry playerprofilecache$profileentry = (PlayerProfileCache$ProfileEntry) this.field_152662_d.get(uuid);

        if (playerprofilecache$profileentry != null) {
            GameProfile gameprofile = playerprofilecache$profileentry.func_152668_a();
            LinkedList linkedlist = this.field_152663_e;

            synchronized (this.field_152663_e) {
                this.field_152663_e.remove(gameprofile);
                this.field_152663_e.addFirst(gameprofile);
            }
        }

        return playerprofilecache$profileentry;
    }

    public void func_152657_b() {
        List list = null;
        BufferedReader bufferedreader = null;

        label81:
        {
            try {
                bufferedreader = Files.newReader(this.field_152665_g, Charsets.UTF_8);
                list = (List) this.field_152660_b.fromJson((Reader) bufferedreader, (Type) PlayerProfileCache.field_152666_h);
                break label81;
            } catch (FileNotFoundException filenotfoundexception) {
                ;
            } finally {
                IOUtils.closeQuietly((Reader) bufferedreader);
            }

            return;
        }

        if (list != null) {
            this.field_152661_c.clear();
            this.field_152662_d.clear();
            LinkedList linkedlist = this.field_152663_e;

            synchronized (this.field_152663_e) {
                this.field_152663_e.clear();
            }

            list = Lists.reverse(list);
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                PlayerProfileCache$ProfileEntry playerprofilecache$profileentry = (PlayerProfileCache$ProfileEntry) iterator.next();

                if (playerprofilecache$profileentry != null) {
                    this.func_152651_a(playerprofilecache$profileentry.func_152668_a(), playerprofilecache$profileentry.func_152670_b());
                }
            }
        }

    }

    public void func_152658_c() {
        String s = this.field_152660_b.toJson((Object) this.func_152656_a(1000));
        BufferedWriter bufferedwriter = null;

        try {
            bufferedwriter = Files.newWriter(this.field_152665_g, Charsets.UTF_8);
            bufferedwriter.write(s);
            return;
        } catch (FileNotFoundException filenotfoundexception) {
            return;
        } catch (IOException ioexception) {
            ;
        } finally {
            IOUtils.closeQuietly((Writer) bufferedwriter);
        }

    }

    private List func_152656_a(int i) {
        ArrayList arraylist = Lists.newArrayList();
        LinkedList linkedlist = this.field_152663_e;
        ArrayList arraylist1;

        synchronized (this.field_152663_e) {
            arraylist1 = Lists.newArrayList(Iterators.limit(this.field_152663_e.iterator(), i));
        }

        Iterator iterator = arraylist1.iterator();

        while (iterator.hasNext()) {
            GameProfile gameprofile = (GameProfile) iterator.next();
            PlayerProfileCache$ProfileEntry playerprofilecache$profileentry = this.func_152653_b(gameprofile.getId());

            if (playerprofilecache$profileentry != null) {
                arraylist.add(playerprofilecache$profileentry);
            }
        }

        return arraylist;
    }
}
