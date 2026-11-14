package net.minecraft.server.dedicated;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommand;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.rcon.IServer;
import net.minecraft.network.rcon.RConThreadMain;
import net.minecraft.network.rcon.RConThreadQuery;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerEula;
import net.minecraft.server.gui.MinecraftServerGui;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.CryptManager;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings$GameType;
import net.minecraft.world.WorldType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DedicatedServer extends MinecraftServer implements IServer {

    private static final Logger field_155771_h = LogManager.getLogger();
    private final List field_71341_l = Collections.synchronizedList(new ArrayList());
    private RConThreadQuery field_71342_m;
    private RConThreadMain field_71339_n;
    private PropertyManager field_71340_o;
    private ServerEula field_154332_n;
    private boolean field_71338_p;
    private WorldSettings$GameType field_71337_q;
    private boolean field_71335_s;

    public DedicatedServer(File file) {
        super(file, Proxy.NO_PROXY);
        new DedicatedServer$1(this, "Server Infinisleeper");
    }

    @Override
    protected boolean func_71197_b() {
        DedicatedServer$2 dedicatedserver$2 = new DedicatedServer$2(this, "Server console handler");

        dedicatedserver$2.setDaemon(true);
        dedicatedserver$2.start();
        DedicatedServer.field_155771_h.info("Starting minecraft server version 1.7.10");
        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
            DedicatedServer.field_155771_h.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }

        DedicatedServer.field_155771_h.info("Loading properties");
        this.field_71340_o = new PropertyManager(new File("server.properties"));
        this.field_154332_n = new ServerEula(new File("eula.txt"));
        if (!this.field_154332_n.func_154346_a()) {
            DedicatedServer.field_155771_h.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
            this.field_154332_n.func_154348_b();
            return false;
        } else {
            if (this.func_71264_H()) {
                this.func_71189_e("127.0.0.1");
            } else {
                this.func_71229_d(this.field_71340_o.func_73670_a("online-mode", true));
                this.func_71189_e(this.field_71340_o.func_73671_a("server-ip", ""));
            }

            this.func_71251_e(this.field_71340_o.func_73670_a("spawn-animals", true));
            this.func_71257_f(this.field_71340_o.func_73670_a("spawn-npcs", true));
            this.func_71188_g(this.field_71340_o.func_73670_a("pvp", true));
            this.func_71245_h(this.field_71340_o.func_73670_a("allow-flight", false));
            this.func_155759_m(this.field_71340_o.func_73671_a("resource-pack", ""));
            this.func_71205_p(this.field_71340_o.func_73671_a("motd", "A Minecraft Server"));
            this.func_104055_i(this.field_71340_o.func_73670_a("force-gamemode", false));
            this.func_143006_e(this.field_71340_o.func_73669_a("player-idle-timeout", 0));
            if (this.field_71340_o.func_73669_a("difficulty", 1) < 0) {
                this.field_71340_o.func_73667_a("difficulty", 0);
            } else if (this.field_71340_o.func_73669_a("difficulty", 1) > 3) {
                this.field_71340_o.func_73667_a("difficulty", 3);
            }

            this.field_71338_p = this.field_71340_o.func_73670_a("generate-structures", true);
            int i = this.field_71340_o.func_73669_a("gamemode", WorldSettings$GameType.SURVIVAL.func_77148_a());

            this.field_71337_q = WorldSettings.func_77161_a(i);
            DedicatedServer.field_155771_h.info("Default game type: " + this.field_71337_q);
            InetAddress inetaddress = null;

            if (this.func_71211_k().length() > 0) {
                inetaddress = InetAddress.getByName(this.func_71211_k());
            }

            if (this.func_71215_F() < 0) {
                this.func_71208_b(this.field_71340_o.func_73669_a("server-port", 25565));
            }

            DedicatedServer.field_155771_h.info("Generating keypair");
            this.func_71253_a(CryptManager.func_75891_b());
            DedicatedServer.field_155771_h.info("Starting Minecraft server on " + (this.func_71211_k().length() == 0 ? "*" : this.func_71211_k()) + ":" + this.func_71215_F());

            try {
                this.func_147137_ag().func_151265_a(inetaddress, this.func_71215_F());
            } catch (IOException ioexception) {
                DedicatedServer.field_155771_h.warn("**** FAILED TO BIND TO PORT!");
                DedicatedServer.field_155771_h.warn("The exception was: {}", ioexception.toString());
                DedicatedServer.field_155771_h.warn("Perhaps a server is already running on that port?");
                return false;
            }

            if (!this.func_71266_T()) {
                DedicatedServer.field_155771_h.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
                DedicatedServer.field_155771_h.warn("The server will make no attempt to authenticate usernames. Beware.");
                DedicatedServer.field_155771_h.warn("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
                DedicatedServer.field_155771_h.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
            }

            if (this.func_152368_aE()) {
                this.func_152358_ax().func_152658_c();
            }

            if (!PreYggdrasilConverter.func_152714_a(this.field_71340_o)) {
                return false;
            } else {
                this.func_152361_a(new DedicatedPlayerList(this));
                long j = System.nanoTime();

                if (this.func_71270_I() == null) {
                    this.func_71261_m(this.field_71340_o.func_73671_a("level-name", "world"));
                }

                String s = this.field_71340_o.func_73671_a("level-seed", "");
                String s1 = this.field_71340_o.func_73671_a("level-type", "DEFAULT");
                String s2 = this.field_71340_o.func_73671_a("generator-settings", "");
                long k = (new Random()).nextLong();

                if (s.length() > 0) {
                    try {
                        long l = Long.parseLong(s);

                        if (l != 0L) {
                            k = l;
                        }
                    } catch (NumberFormatException numberformatexception) {
                        k = (long) s.hashCode();
                    }
                }

                WorldType worldtype = WorldType.func_77130_a(s1);

                if (worldtype == null) {
                    worldtype = WorldType.field_77137_b;
                }

                this.func_147136_ar();
                this.func_82356_Z();
                this.func_110455_j();
                this.func_70002_Q();
                this.func_71191_d(this.field_71340_o.func_73669_a("max-build-height", 256));
                this.func_71191_d((this.func_71207_Z() + 8) / 16 * 16);
                this.func_71191_d(MathHelper.func_76125_a(this.func_71207_Z(), 64, 256));
                this.field_71340_o.func_73667_a("max-build-height", this.func_71207_Z());
                DedicatedServer.field_155771_h.info("Preparing level \"" + this.func_71270_I() + "\"");
                this.func_71247_a(this.func_71270_I(), this.func_71270_I(), k, worldtype, s2);
                long i1 = System.nanoTime() - j;
                String s3 = String.format("%.3fs", (double) i1 / 1.0E9D);

                DedicatedServer.field_155771_h.info("Done (" + s3 + ")! For help, type \"help\" or \"?\"");
                if (this.field_71340_o.func_73670_a("enable-query", false)) {
                    DedicatedServer.field_155771_h.info("Starting GS4 status listener");
                    this.field_71342_m = new RConThreadQuery(this);
                    this.field_71342_m.func_72602_a();
                }

                if (this.field_71340_o.func_73670_a("enable-rcon", false)) {
                    DedicatedServer.field_155771_h.info("Starting remote control listener");
                    this.field_71339_n = new RConThreadMain(this);
                    this.field_71339_n.func_72602_a();
                }

                return true;
            }
        }
    }

    @Override
    public boolean func_71225_e() {
        return this.field_71338_p;
    }

    @Override
    public WorldSettings$GameType func_71265_f() {
        return this.field_71337_q;
    }

    @Override
    public EnumDifficulty func_147135_j() {
        return EnumDifficulty.func_151523_a(this.field_71340_o.func_73669_a("difficulty", 1));
    }

    @Override
    public boolean func_71199_h() {
        return this.field_71340_o.func_73670_a("hardcore", false);
    }

    @Override
    protected void func_71228_a(CrashReport crashreport) {}

    @Override
    public CrashReport func_71230_b(CrashReport crashreport) {
        crashreport = super.func_71230_b(crashreport);
        crashreport.func_85056_g().func_71500_a("Is Modded", new DedicatedServer$3(this));
        crashreport.func_85056_g().func_71500_a("Type", new DedicatedServer$4(this));
        return crashreport;
    }

    @Override
    protected void func_71240_o() {
        System.exit(0);
    }

    @Override
    protected void func_71190_q() {
        super.func_71190_q();
        this.func_71333_ah();
    }

    @Override
    public boolean func_71255_r() {
        return this.field_71340_o.func_73670_a("allow-nether", true);
    }

    @Override
    public boolean func_71193_K() {
        return this.field_71340_o.func_73670_a("spawn-monsters", true);
    }

    @Override
    public void func_70000_a(PlayerUsageSnooper playerusagesnooper) {
        playerusagesnooper.func_152768_a("whitelist_enabled", this.func_71203_ab().func_72383_n());
        playerusagesnooper.func_152768_a("whitelist_count", this.func_71203_ab().func_152598_l().length);
        super.func_70000_a(playerusagesnooper);
    }

    @Override
    public boolean func_70002_Q() {
        return this.field_71340_o.func_73670_a("snooper-enabled", true);
    }

    public void func_71331_a(String s, ICommandSender icommandsender) {
        this.field_71341_l.add(new ServerCommand(s, icommandsender));
    }

    public void func_71333_ah() {
        while (!this.field_71341_l.isEmpty()) {
            ServerCommand servercommand = (ServerCommand) this.field_71341_l.remove(0);

            this.func_71187_D().func_71556_a(servercommand.field_73701_b, servercommand.field_73702_a);
        }

    }

    @Override
    public boolean func_71262_S() {
        return true;
    }

    @Override
    public DedicatedPlayerList func_71203_ab() {
        return (DedicatedPlayerList) super.func_71203_ab();
    }

    @Override
    public int func_71327_a(String s, int i) {
        return this.field_71340_o.func_73669_a(s, i);
    }

    @Override
    public String func_71330_a(String s, String s1) {
        return this.field_71340_o.func_73671_a(s, s1);
    }

    public boolean func_71332_a(String s, boolean flag) {
        return this.field_71340_o.func_73670_a(s, flag);
    }

    @Override
    public void func_71328_a(String s, Object object) {
        this.field_71340_o.func_73667_a(s, object);
    }

    @Override
    public void func_71326_a() {
        this.field_71340_o.func_73668_b();
    }

    @Override
    public String func_71329_c() {
        File file = this.field_71340_o.func_73665_c();

        return file != null ? file.getAbsolutePath() : "No settings file";
    }

    public void func_120011_ar() {
        MinecraftServerGui.func_120016_a(this);
        this.field_71335_s = true;
    }

    @Override
    public boolean func_71279_ae() {
        return this.field_71335_s;
    }

    @Override
    public String func_71206_a(WorldSettings$GameType worldsettings$gametype, boolean flag) {
        return "";
    }

    @Override
    public boolean func_82356_Z() {
        return this.field_71340_o.func_73670_a("enable-command-block", false);
    }

    @Override
    public int func_82357_ak() {
        return this.field_71340_o.func_73669_a("spawn-protection", super.func_82357_ak());
    }

    @Override
    public boolean func_96290_a(World world, int i, int j, int k, EntityPlayer entityplayer) {
        if (world.field_73011_w.field_76574_g != 0) {
            return false;
        } else if (this.func_71203_ab().func_152603_m().func_152690_d()) {
            return false;
        } else if (this.func_71203_ab().func_152596_g(entityplayer.func_146103_bH())) {
            return false;
        } else if (this.func_82357_ak() <= 0) {
            return false;
        } else {
            ChunkCoordinates chunkcoordinates = world.func_72861_E();
            int l = MathHelper.func_76130_a(i - chunkcoordinates.field_71574_a);
            int i1 = MathHelper.func_76130_a(k - chunkcoordinates.field_71573_c);
            int j1 = Math.max(l, i1);

            return j1 <= this.func_82357_ak();
        }
    }

    @Override
    public int func_110455_j() {
        return this.field_71340_o.func_73669_a("op-permission-level", 4);
    }

    @Override
    public void func_143006_e(int i) {
        super.func_143006_e(i);
        this.field_71340_o.func_73667_a("player-idle-timeout", i);
        this.func_71326_a();
    }

    @Override
    public boolean func_152363_m() {
        return this.field_71340_o.func_73670_a("broadcast-rcon-to-ops", true);
    }

    @Override
    public boolean func_147136_ar() {
        return this.field_71340_o.func_73670_a("announce-player-achievements", true);
    }

    protected boolean func_152368_aE() {
        boolean flag = false;

        int i;

        for (i = 0; !flag && i <= 2; ++i) {
            if (i > 0) {
                DedicatedServer.field_155771_h.warn("Encountered a problem while converting the user banlist, retrying in a few seconds");
                this.func_152369_aG();
            }

            flag = PreYggdrasilConverter.func_152724_a(this);
        }

        boolean flag1 = false;

        for (i = 0; !flag1 && i <= 2; ++i) {
            if (i > 0) {
                DedicatedServer.field_155771_h.warn("Encountered a problem while converting the ip banlist, retrying in a few seconds");
                this.func_152369_aG();
            }

            flag1 = PreYggdrasilConverter.func_152722_b(this);
        }

        boolean flag2 = false;

        for (i = 0; !flag2 && i <= 2; ++i) {
            if (i > 0) {
                DedicatedServer.field_155771_h.warn("Encountered a problem while converting the op list, retrying in a few seconds");
                this.func_152369_aG();
            }

            flag2 = PreYggdrasilConverter.func_152718_c(this);
        }

        boolean flag3 = false;

        for (i = 0; !flag3 && i <= 2; ++i) {
            if (i > 0) {
                DedicatedServer.field_155771_h.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
                this.func_152369_aG();
            }

            flag3 = PreYggdrasilConverter.func_152710_d(this);
        }

        boolean flag4 = false;

        for (i = 0; !flag4 && i <= 2; ++i) {
            if (i > 0) {
                DedicatedServer.field_155771_h.warn("Encountered a problem while converting the player save files, retrying in a few seconds");
                this.func_152369_aG();
            }

            flag4 = PreYggdrasilConverter.func_152723_a(this, this.field_71340_o);
        }

        return flag || flag1 || flag2 || flag3 || flag4;
    }

    private void func_152369_aG() {
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException interruptedexception) {
            ;
        }
    }
}
