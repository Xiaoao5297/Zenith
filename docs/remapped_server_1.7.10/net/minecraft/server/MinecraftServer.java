package net.minecraft.server;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.Proxy;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.imageio.ImageIO;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Bootstrap;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.ServerStatusResponse$MinecraftProtocolVersionIdentifier;
import net.minecraft.network.ServerStatusResponse$PlayerCountData;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings$GameType;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer implements ICommandSender, Runnable, IPlayerUsage {

    private static final Logger field_147145_h = LogManager.getLogger();
    public static final File field_152367_a = new File("usercache.json");
    private static MinecraftServer field_71309_l;
    private final ISaveFormat field_71310_m;
    private final PlayerUsageSnooper field_71307_n = new PlayerUsageSnooper("server", this, func_130071_aq());
    private final File field_71308_o;
    private final List field_71322_p = new ArrayList();
    private final ICommandManager field_71321_q;
    public final Profiler field_71304_b = new Profiler();
    private final NetworkSystem field_147144_o;
    private final ServerStatusResponse field_147147_p = new ServerStatusResponse();
    private final Random field_147146_q = new Random();
    private String field_71320_r;
    private int field_71319_s = -1;
    public WorldServer[] field_71305_c;
    private ServerConfigurationManager field_71318_t;
    private boolean field_71317_u = true;
    private boolean field_71316_v;
    private int field_71315_w;
    protected final Proxy field_110456_c;
    public String field_71302_d;
    public int field_71303_e;
    private boolean field_71325_x;
    private boolean field_71324_y;
    private boolean field_71323_z;
    private boolean field_71284_A;
    private boolean field_71285_B;
    private String field_71286_C;
    private int field_71280_D;
    private int field_143008_E = 0;
    public final long[] field_71311_j = new long[100];
    public long[][] field_71312_k;
    private KeyPair field_71292_I;
    private String field_71293_J;
    private String field_71294_K;
    private boolean field_71288_M;
    private boolean field_71289_N;
    private boolean field_71290_O;
    private String field_147141_M = "";
    private boolean field_71296_Q;
    private long field_71299_R;
    private String field_71298_S;
    private boolean field_71295_T;
    private boolean field_104057_T;
    private final YggdrasilAuthenticationService field_152364_T;
    private final MinecraftSessionService field_147143_S;
    private long field_147142_T = 0L;
    private final GameProfileRepository field_152365_W;
    private final PlayerProfileCache field_152366_X;

    public MinecraftServer(File file, Proxy proxy) {
        this.field_152366_X = new PlayerProfileCache(this, MinecraftServer.field_152367_a);
        MinecraftServer.field_71309_l = this;
        this.field_110456_c = proxy;
        this.field_71308_o = file;
        this.field_147144_o = new NetworkSystem(this);
        this.field_71321_q = new ServerCommandManager();
        this.field_71310_m = new AnvilSaveConverter(file);
        this.field_152364_T = new YggdrasilAuthenticationService(proxy, UUID.randomUUID().toString());
        this.field_147143_S = this.field_152364_T.createMinecraftSessionService();
        this.field_152365_W = this.field_152364_T.createProfileRepository();
    }

    protected abstract boolean func_71197_b();

    protected void func_71237_c(String s) {
        if (this.func_71254_M().func_75801_b(s)) {
            MinecraftServer.field_147145_h.info("Converting map!");
            this.func_71192_d("menu.convertingLevel");
            this.func_71254_M().func_75805_a(s, new MinecraftServer$1(this));
        }

    }

    protected synchronized void func_71192_d(String s) {
        this.field_71298_S = s;
    }

    protected void func_71247_a(String s, String s1, long i, WorldType worldtype, String s2) {
        this.func_71237_c(s);
        this.func_71192_d("menu.loadingLevel");
        this.field_71305_c = new WorldServer[3];
        this.field_71312_k = new long[this.field_71305_c.length][100];
        ISaveHandler isavehandler = this.field_71310_m.func_75804_a(s, true);
        WorldInfo worldinfo = isavehandler.func_75757_d();
        WorldSettings worldsettings;

        if (worldinfo == null) {
            worldsettings = new WorldSettings(i, this.func_71265_f(), this.func_71225_e(), this.func_71199_h(), worldtype);
            worldsettings.func_82750_a(s2);
        } else {
            worldsettings = new WorldSettings(worldinfo);
        }

        if (this.field_71289_N) {
            worldsettings.func_77159_a();
        }

        for (int j = 0; j < this.field_71305_c.length; ++j) {
            byte b0 = 0;

            if (j == 1) {
                b0 = -1;
            }

            if (j == 2) {
                b0 = 1;
            }

            if (j == 0) {
                if (this.func_71242_L()) {
                    this.field_71305_c[j] = new DemoWorldServer(this, isavehandler, s1, b0, this.field_71304_b);
                } else {
                    this.field_71305_c[j] = new WorldServer(this, isavehandler, s1, b0, worldsettings, this.field_71304_b);
                }
            } else {
                this.field_71305_c[j] = new WorldServerMulti(this, isavehandler, s1, b0, worldsettings, this.field_71305_c[0], this.field_71304_b);
            }

            this.field_71305_c[j].func_72954_a(new WorldManager(this, this.field_71305_c[j]));
            if (!this.func_71264_H()) {
                this.field_71305_c[j].func_72912_H().func_76060_a(this.func_71265_f());
            }

            this.field_71318_t.func_72364_a(this.field_71305_c);
        }

        this.func_147139_a(this.func_147135_j());
        this.func_71222_d();
    }

    protected void func_71222_d() {
        boolean flag = true;
        boolean flag1 = true;
        boolean flag2 = true;
        boolean flag3 = true;
        int i = 0;

        this.func_71192_d("menu.generatingTerrain");
        byte b0 = 0;

        MinecraftServer.field_147145_h.info("Preparing start region for level " + b0);
        WorldServer worldserver = this.field_71305_c[b0];
        ChunkCoordinates chunkcoordinates = worldserver.func_72861_E();
        long j = func_130071_aq();

        for (int k = -192; k <= 192 && this.func_71278_l(); k += 16) {
            for (int l = -192; l <= 192 && this.func_71278_l(); l += 16) {
                long i1 = func_130071_aq();

                if (i1 - j > 1000L) {
                    this.func_71216_a_("Preparing spawn area", i * 100 / 625);
                    j = i1;
                }

                ++i;
                worldserver.field_73059_b.func_73158_c(chunkcoordinates.field_71574_a + k >> 4, chunkcoordinates.field_71573_c + l >> 4);
            }
        }

        this.func_71243_i();
    }

    public abstract boolean func_71225_e();

    public abstract WorldSettings$GameType func_71265_f();

    public abstract EnumDifficulty func_147135_j();

    public abstract boolean func_71199_h();

    public abstract int func_110455_j();

    public abstract boolean func_152363_m();

    protected void func_71216_a_(String s, int i) {
        this.field_71302_d = s;
        this.field_71303_e = i;
        MinecraftServer.field_147145_h.info(s + ": " + i + "%");
    }

    protected void func_71243_i() {
        this.field_71302_d = null;
        this.field_71303_e = 0;
    }

    protected void func_71267_a(boolean flag) {
        if (!this.field_71290_O) {
            WorldServer[] aworldserver = this.field_71305_c;
            int i = aworldserver.length;

            for (int j = 0; j < i; ++j) {
                WorldServer worldserver = aworldserver[j];

                if (worldserver != null) {
                    if (!flag) {
                        MinecraftServer.field_147145_h.info("Saving chunks for level '" + worldserver.func_72912_H().func_76065_j() + "'/" + worldserver.field_73011_w.func_80007_l());
                    }

                    try {
                        worldserver.func_73044_a(true, (IProgressUpdate) null);
                    } catch (MinecraftException minecraftexception) {
                        MinecraftServer.field_147145_h.warn(minecraftexception.getMessage());
                    }
                }
            }

        }
    }

    public void func_71260_j() {
        if (!this.field_71290_O) {
            MinecraftServer.field_147145_h.info("Stopping server");
            if (this.func_147137_ag() != null) {
                this.func_147137_ag().func_151268_b();
            }

            if (this.field_71318_t != null) {
                MinecraftServer.field_147145_h.info("Saving players");
                this.field_71318_t.func_72389_g();
                this.field_71318_t.func_72392_r();
            }

            if (this.field_71305_c != null) {
                MinecraftServer.field_147145_h.info("Saving worlds");
                this.func_71267_a(false);

                for (int i = 0; i < this.field_71305_c.length; ++i) {
                    WorldServer worldserver = this.field_71305_c[i];

                    worldserver.func_73041_k();
                }
            }

            if (this.field_71307_n.func_76468_d()) {
                this.field_71307_n.func_76470_e();
            }

        }
    }

    public String func_71211_k() {
        return this.field_71320_r;
    }

    public void func_71189_e(String s) {
        this.field_71320_r = s;
    }

    public boolean func_71278_l() {
        return this.field_71317_u;
    }

    public void func_71263_m() {
        this.field_71317_u = false;
    }

    public void run() {
        try {
            if (this.func_71197_b()) {
                long i = func_130071_aq();
                long j = 0L;

                this.field_147147_p.func_151315_a(new ChatComponentText(this.field_71286_C));
                this.field_147147_p.func_151321_a(new ServerStatusResponse$MinecraftProtocolVersionIdentifier("1.7.10", 5));
                this.func_147138_a(this.field_147147_p);

                while (this.field_71317_u) {
                    long k = func_130071_aq();
                    long l = k - i;

                    if (l > 2000L && i - this.field_71299_R >= 15000L) {
                        MinecraftServer.field_147145_h.warn("Can't keep up! Did the system time change, or is the server overloaded? Running {}ms behind, skipping {} tick(s)", l, l / 50L);
                        l = 2000L;
                        this.field_71299_R = i;
                    }

                    if (l < 0L) {
                        MinecraftServer.field_147145_h.warn("Time ran backwards! Did the system time change?");
                        l = 0L;
                    }

                    j += l;
                    i = k;
                    if (this.field_71305_c[0].func_73056_e()) {
                        this.func_71217_p();
                        j = 0L;
                    } else {
                        while (j > 50L) {
                            j -= 50L;
                            this.func_71217_p();
                        }
                    }

                    Thread.sleep(Math.max(1L, 50L - j));
                    this.field_71296_Q = true;
                }
            } else {
                this.func_71228_a((CrashReport) null);
            }
        } catch (Throwable throwable) {
            MinecraftServer.field_147145_h.error("Encountered an unexpected exception", throwable);
            CrashReport crashreport = null;

            if (throwable instanceof ReportedException) {
                crashreport = this.func_71230_b(((ReportedException) throwable).func_71575_a());
            } else {
                crashreport = this.func_71230_b(new CrashReport("Exception in server tick loop", throwable));
            }

            File file = new File(new File(this.func_71238_n(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");

            if (crashreport.func_147149_a(file)) {
                MinecraftServer.field_147145_h.error("This crash report has been saved to: " + file.getAbsolutePath());
            } else {
                MinecraftServer.field_147145_h.error("We were unable to save this crash report to disk.");
            }

            this.func_71228_a(crashreport);
        } finally {
            try {
                this.func_71260_j();
                this.field_71316_v = true;
            } catch (Throwable throwable1) {
                MinecraftServer.field_147145_h.error("Exception stopping the server", throwable1);
            } finally {
                this.func_71240_o();
            }

        }

    }

    private void func_147138_a(ServerStatusResponse serverstatusresponse) {
        File file = this.func_71209_f("server-icon.png");

        if (file.isFile()) {
            ByteBuf bytebuf = Unpooled.buffer();

            try {
                BufferedImage bufferedimage = ImageIO.read(file);

                Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide");
                Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high");
                ImageIO.write(bufferedimage, "PNG", new ByteBufOutputStream(bytebuf));
                ByteBuf bytebuf1 = Base64.encode(bytebuf);

                serverstatusresponse.func_151320_a("data:image/png;base64," + bytebuf1.toString(Charsets.UTF_8));
            } catch (Exception exception) {
                MinecraftServer.field_147145_h.error("Couldn't load server icon", (Throwable) exception);
            } finally {
                bytebuf.release();
            }
        }

    }

    protected File func_71238_n() {
        return new File(".");
    }

    protected void func_71228_a(CrashReport crashreport) {}

    protected void func_71240_o() {}

    protected void func_71217_p() {
        long i = System.nanoTime();

        ++this.field_71315_w;
        if (this.field_71295_T) {
            this.field_71295_T = false;
            this.field_71304_b.field_76327_a = true;
            this.field_71304_b.func_76317_a();
        }

        this.field_71304_b.func_76320_a("root");
        this.func_71190_q();
        if (i - this.field_147142_T >= 5000000000L) {
            this.field_147142_T = i;
            this.field_147147_p.func_151319_a(new ServerStatusResponse$PlayerCountData(this.func_71275_y(), this.func_71233_x()));
            GameProfile[] agameprofile = new GameProfile[Math.min(this.func_71233_x(), 12)];
            int j = MathHelper.func_76136_a(this.field_147146_q, 0, this.func_71233_x() - agameprofile.length);

            for (int k = 0; k < agameprofile.length; ++k) {
                agameprofile[k] = ((EntityPlayerMP) this.field_71318_t.field_72404_b.get(j + k)).func_146103_bH();
            }

            Collections.shuffle(Arrays.asList(agameprofile));
            this.field_147147_p.func_151318_b().func_151330_a(agameprofile);
        }

        if (this.field_71315_w % 900 == 0) {
            this.field_71304_b.func_76320_a("save");
            this.field_71318_t.func_72389_g();
            this.func_71267_a(true);
            this.field_71304_b.func_76319_b();
        }

        this.field_71304_b.func_76320_a("tallying");
        this.field_71311_j[this.field_71315_w % 100] = System.nanoTime() - i;
        this.field_71304_b.func_76319_b();
        this.field_71304_b.func_76320_a("snooper");
        if (!this.field_71307_n.func_76468_d() && this.field_71315_w > 100) {
            this.field_71307_n.func_76463_a();
        }

        if (this.field_71315_w % 6000 == 0) {
            this.field_71307_n.func_76471_b();
        }

        this.field_71304_b.func_76319_b();
        this.field_71304_b.func_76319_b();
    }

    public void func_71190_q() {
        this.field_71304_b.func_76320_a("levels");

        int i;

        for (i = 0; i < this.field_71305_c.length; ++i) {
            long j = System.nanoTime();

            if (i == 0 || this.func_71255_r()) {
                WorldServer worldserver = this.field_71305_c[i];

                this.field_71304_b.func_76320_a(worldserver.func_72912_H().func_76065_j());
                this.field_71304_b.func_76320_a("pools");
                this.field_71304_b.func_76319_b();
                if (this.field_71315_w % 20 == 0) {
                    this.field_71304_b.func_76320_a("timeSync");
                    this.field_71318_t.func_148537_a(new S03PacketTimeUpdate(worldserver.func_82737_E(), worldserver.func_72820_D(), worldserver.func_82736_K().func_82766_b("doDaylightCycle")), worldserver.field_73011_w.field_76574_g);
                    this.field_71304_b.func_76319_b();
                }

                this.field_71304_b.func_76320_a("tick");

                CrashReport crashreport;

                try {
                    worldserver.func_72835_b();
                } catch (Throwable throwable) {
                    crashreport = CrashReport.func_85055_a(throwable, "Exception ticking world");
                    worldserver.func_72914_a(crashreport);
                    throw new ReportedException(crashreport);
                }

                try {
                    worldserver.func_72939_s();
                } catch (Throwable throwable1) {
                    crashreport = CrashReport.func_85055_a(throwable1, "Exception ticking world entities");
                    worldserver.func_72914_a(crashreport);
                    throw new ReportedException(crashreport);
                }

                this.field_71304_b.func_76319_b();
                this.field_71304_b.func_76320_a("tracker");
                worldserver.func_73039_n().func_72788_a();
                this.field_71304_b.func_76319_b();
                this.field_71304_b.func_76319_b();
            }

            this.field_71312_k[i][this.field_71315_w % 100] = System.nanoTime() - j;
        }

        this.field_71304_b.func_76318_c("connection");
        this.func_147137_ag().func_151269_c();
        this.field_71304_b.func_76318_c("players");
        this.field_71318_t.func_72374_b();
        this.field_71304_b.func_76318_c("tickables");

        for (i = 0; i < this.field_71322_p.size(); ++i) {
            ((IUpdatePlayerListBox) this.field_71322_p.get(i)).func_73660_a();
        }

        this.field_71304_b.func_76319_b();
    }

    public boolean func_71255_r() {
        return true;
    }

    public void func_82010_a(IUpdatePlayerListBox iupdateplayerlistbox) {
        this.field_71322_p.add(iupdateplayerlistbox);
    }

    public static void main(String[] astring) {
        Bootstrap.func_151354_b();

        try {
            boolean flag = true;
            String s = null;
            String s1 = ".";
            String s2 = null;
            boolean flag1 = false;
            boolean flag2 = false;
            int i = -1;

            for (int j = 0; j < astring.length; ++j) {
                String s3 = astring[j];
                String s4 = j == astring.length - 1 ? null : astring[j + 1];
                boolean flag3 = false;

                if (!s3.equals("nogui") && !s3.equals("--nogui")) {
                    if (s3.equals("--port") && s4 != null) {
                        flag3 = true;

                        try {
                            i = Integer.parseInt(s4);
                        } catch (NumberFormatException numberformatexception) {
                            ;
                        }
                    } else if (s3.equals("--singleplayer") && s4 != null) {
                        flag3 = true;
                        s = s4;
                    } else if (s3.equals("--universe") && s4 != null) {
                        flag3 = true;
                        s1 = s4;
                    } else if (s3.equals("--world") && s4 != null) {
                        flag3 = true;
                        s2 = s4;
                    } else if (s3.equals("--demo")) {
                        flag1 = true;
                    } else if (s3.equals("--bonusChest")) {
                        flag2 = true;
                    }
                } else {
                    flag = false;
                }

                if (flag3) {
                    ++j;
                }
            }

            DedicatedServer dedicatedserver = new DedicatedServer(new File(s1));

            if (s != null) {
                dedicatedserver.func_71224_l(s);
            }

            if (s2 != null) {
                dedicatedserver.func_71261_m(s2);
            }

            if (i >= 0) {
                dedicatedserver.func_71208_b(i);
            }

            if (flag1) {
                dedicatedserver.func_71204_b(true);
            }

            if (flag2) {
                dedicatedserver.func_71194_c(true);
            }

            if (flag && !GraphicsEnvironment.isHeadless()) {
                dedicatedserver.func_120011_ar();
            }

            dedicatedserver.func_71256_s();
            Runtime.getRuntime().addShutdownHook(new MinecraftServer$2("Server Shutdown Thread", dedicatedserver));
        } catch (Exception exception) {
            MinecraftServer.field_147145_h.fatal("Failed to start the minecraft server", (Throwable) exception);
        }

    }

    public void func_71256_s() {
        (new MinecraftServer$3(this, "Server thread")).start();
    }

    public File func_71209_f(String s) {
        return new File(this.func_71238_n(), s);
    }

    public void func_71244_g(String s) {
        MinecraftServer.field_147145_h.info(s);
    }

    public void func_71236_h(String s) {
        MinecraftServer.field_147145_h.warn(s);
    }

    public WorldServer func_71218_a(int i) {
        return i == -1 ? this.field_71305_c[1] : (i == 1 ? this.field_71305_c[2] : this.field_71305_c[0]);
    }

    public String func_71277_t() {
        return this.field_71320_r;
    }

    public int func_71234_u() {
        return this.field_71319_s;
    }

    public String func_71274_v() {
        return this.field_71286_C;
    }

    public String func_71249_w() {
        return "1.7.10";
    }

    public int func_71233_x() {
        return this.field_71318_t.func_72394_k();
    }

    public int func_71275_y() {
        return this.field_71318_t.func_72352_l();
    }

    public String[] func_71213_z() {
        return this.field_71318_t.func_72369_d();
    }

    public GameProfile[] func_152357_F() {
        return this.field_71318_t.func_152600_g();
    }

    public String func_71258_A() {
        return "";
    }

    public String func_71252_i(String s) {
        RConConsoleSource.field_70010_a.func_70007_b();
        this.field_71321_q.func_71556_a(RConConsoleSource.field_70010_a, s);
        return RConConsoleSource.field_70010_a.func_70008_c();
    }

    public boolean func_71239_B() {
        return false;
    }

    public void func_71201_j(String s) {
        MinecraftServer.field_147145_h.error(s);
    }

    public void func_71198_k(String s) {
        if (this.func_71239_B()) {
            MinecraftServer.field_147145_h.info(s);
        }

    }

    public String getServerModName() {
        return "vanilla";
    }

    public CrashReport func_71230_b(CrashReport crashreport) {
        crashreport.func_85056_g().func_71500_a("Profiler Position", new MinecraftServer$4(this));
        if (this.field_71305_c != null && this.field_71305_c.length > 0 && this.field_71305_c[0] != null) {
            crashreport.func_85056_g().func_71500_a("Vec3 Pool Size", new MinecraftServer$5(this));
        }

        if (this.field_71318_t != null) {
            crashreport.func_85056_g().func_71500_a("Player Count", new MinecraftServer$6(this));
        }

        return crashreport;
    }

    public List func_71248_a(ICommandSender icommandsender, String s) {
        ArrayList arraylist = new ArrayList();

        if (s.startsWith("/")) {
            s = s.substring(1);
            boolean flag = !s.contains(" ");
            List list = this.field_71321_q.func_71558_b(icommandsender, s);

            if (list != null) {
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    String s1 = (String) iterator.next();

                    if (flag) {
                        arraylist.add("/" + s1);
                    } else {
                        arraylist.add(s1);
                    }
                }
            }

            return arraylist;
        } else {
            String[] astring = s.split(" ", -1);
            String s2 = astring[astring.length - 1];
            String[] astring1 = this.field_71318_t.func_72369_d();
            int i = astring1.length;

            for (int j = 0; j < i; ++j) {
                String s3 = astring1[j];

                if (CommandBase.func_71523_a(s2, s3)) {
                    arraylist.add(s3);
                }
            }

            return arraylist;
        }
    }

    public static MinecraftServer func_71276_C() {
        return MinecraftServer.field_71309_l;
    }

    @Override
    public String func_70005_c_() {
        return "Server";
    }

    @Override
    public void func_145747_a(IChatComponent ichatcomponent) {
        MinecraftServer.field_147145_h.info(ichatcomponent.func_150260_c());
    }

    @Override
    public boolean func_70003_b(int i, String s) {
        return true;
    }

    public ICommandManager func_71187_D() {
        return this.field_71321_q;
    }

    public KeyPair func_71250_E() {
        return this.field_71292_I;
    }

    public int func_71215_F() {
        return this.field_71319_s;
    }

    public void func_71208_b(int i) {
        this.field_71319_s = i;
    }

    public String func_71214_G() {
        return this.field_71293_J;
    }

    public void func_71224_l(String s) {
        this.field_71293_J = s;
    }

    public boolean func_71264_H() {
        return this.field_71293_J != null;
    }

    public String func_71270_I() {
        return this.field_71294_K;
    }

    public void func_71261_m(String s) {
        this.field_71294_K = s;
    }

    public void func_71253_a(KeyPair keypair) {
        this.field_71292_I = keypair;
    }

    public void func_147139_a(EnumDifficulty enumdifficulty) {
        for (int i = 0; i < this.field_71305_c.length; ++i) {
            WorldServer worldserver = this.field_71305_c[i];

            if (worldserver != null) {
                if (worldserver.func_72912_H().func_76093_s()) {
                    worldserver.field_73013_u = EnumDifficulty.HARD;
                    worldserver.func_72891_a(true, true);
                } else if (this.func_71264_H()) {
                    worldserver.field_73013_u = enumdifficulty;
                    worldserver.func_72891_a(worldserver.field_73013_u != EnumDifficulty.PEACEFUL, true);
                } else {
                    worldserver.field_73013_u = enumdifficulty;
                    worldserver.func_72891_a(this.func_71193_K(), this.field_71324_y);
                }
            }
        }

    }

    protected boolean func_71193_K() {
        return true;
    }

    public boolean func_71242_L() {
        return this.field_71288_M;
    }

    public void func_71204_b(boolean flag) {
        this.field_71288_M = flag;
    }

    public void func_71194_c(boolean flag) {
        this.field_71289_N = flag;
    }

    public ISaveFormat func_71254_M() {
        return this.field_71310_m;
    }

    public void func_71272_O() {
        this.field_71290_O = true;
        this.func_71254_M().func_75800_d();

        for (int i = 0; i < this.field_71305_c.length; ++i) {
            WorldServer worldserver = this.field_71305_c[i];

            if (worldserver != null) {
                worldserver.func_73041_k();
            }
        }

        this.func_71254_M().func_75802_e(this.field_71305_c[0].func_72860_G().func_75760_g());
        this.func_71263_m();
    }

    public String func_147133_T() {
        return this.field_147141_M;
    }

    public void func_155759_m(String s) {
        this.field_147141_M = s;
    }

    @Override
    public void func_70000_a(PlayerUsageSnooper playerusagesnooper) {
        playerusagesnooper.func_152768_a("whitelist_enabled", false);
        playerusagesnooper.func_152768_a("whitelist_count", 0);
        playerusagesnooper.func_152768_a("players_current", this.func_71233_x());
        playerusagesnooper.func_152768_a("players_max", this.func_71275_y());
        playerusagesnooper.func_152768_a("players_seen", this.field_71318_t.func_72373_m().length);
        playerusagesnooper.func_152768_a("uses_auth", this.field_71325_x);
        playerusagesnooper.func_152768_a("gui_state", this.func_71279_ae() ? "enabled" : "disabled");
        playerusagesnooper.func_152768_a("run_time", (func_130071_aq() - playerusagesnooper.func_130105_g()) / 60L * 1000L);
        playerusagesnooper.func_152768_a("avg_tick_ms", (int) (MathHelper.func_76127_a(this.field_71311_j) * 1.0E-6D));
        int i = 0;

        for (int j = 0; j < this.field_71305_c.length; ++j) {
            if (this.field_71305_c[j] != null) {
                WorldServer worldserver = this.field_71305_c[j];
                WorldInfo worldinfo = worldserver.func_72912_H();

                playerusagesnooper.func_152768_a("world[" + i + "][dimension]", worldserver.field_73011_w.field_76574_g);
                playerusagesnooper.func_152768_a("world[" + i + "][mode]", worldinfo.func_76077_q());
                playerusagesnooper.func_152768_a("world[" + i + "][difficulty]", worldserver.field_73013_u);
                playerusagesnooper.func_152768_a("world[" + i + "][hardcore]", worldinfo.func_76093_s());
                playerusagesnooper.func_152768_a("world[" + i + "][generator_name]", worldinfo.func_76067_t().func_77127_a());
                playerusagesnooper.func_152768_a("world[" + i + "][generator_version]", worldinfo.func_76067_t().func_77131_c());
                playerusagesnooper.func_152768_a("world[" + i + "][height]", this.field_71280_D);
                playerusagesnooper.func_152768_a("world[" + i + "][chunks_loaded]", worldserver.func_72863_F().func_73152_e());
                ++i;
            }
        }

        playerusagesnooper.func_152768_a("worlds", i);
    }

    @Override
    public void func_70001_b(PlayerUsageSnooper playerusagesnooper) {
        playerusagesnooper.func_152767_b("singleplayer", this.func_71264_H());
        playerusagesnooper.func_152767_b("server_brand", this.getServerModName());
        playerusagesnooper.func_152767_b("gui_supported", GraphicsEnvironment.isHeadless() ? "headless" : "supported");
        playerusagesnooper.func_152767_b("dedicated", this.func_71262_S());
    }

    @Override
    public boolean func_70002_Q() {
        return true;
    }

    public abstract boolean func_71262_S();

    public boolean func_71266_T() {
        return this.field_71325_x;
    }

    public void func_71229_d(boolean flag) {
        this.field_71325_x = flag;
    }

    public boolean func_71268_U() {
        return this.field_71324_y;
    }

    public void func_71251_e(boolean flag) {
        this.field_71324_y = flag;
    }

    public boolean func_71220_V() {
        return this.field_71323_z;
    }

    public void func_71257_f(boolean flag) {
        this.field_71323_z = flag;
    }

    public boolean func_71219_W() {
        return this.field_71284_A;
    }

    public void func_71188_g(boolean flag) {
        this.field_71284_A = flag;
    }

    public boolean func_71231_X() {
        return this.field_71285_B;
    }

    public void func_71245_h(boolean flag) {
        this.field_71285_B = flag;
    }

    public abstract boolean func_82356_Z();

    public String func_71273_Y() {
        return this.field_71286_C;
    }

    public void func_71205_p(String s) {
        this.field_71286_C = s;
    }

    public int func_71207_Z() {
        return this.field_71280_D;
    }

    public void func_71191_d(int i) {
        this.field_71280_D = i;
    }

    public boolean func_71241_aa() {
        return this.field_71316_v;
    }

    public ServerConfigurationManager func_71203_ab() {
        return this.field_71318_t;
    }

    public void func_152361_a(ServerConfigurationManager serverconfigurationmanager) {
        this.field_71318_t = serverconfigurationmanager;
    }

    public void func_71235_a(WorldSettings$GameType worldsettings$gametype) {
        for (int i = 0; i < this.field_71305_c.length; ++i) {
            func_71276_C().field_71305_c[i].func_72912_H().func_76060_a(worldsettings$gametype);
        }

    }

    public NetworkSystem func_147137_ag() {
        return this.field_147144_o;
    }

    public boolean func_71279_ae() {
        return false;
    }

    public abstract String func_71206_a(WorldSettings$GameType worldsettings$gametype, boolean flag);

    public int func_71259_af() {
        return this.field_71315_w;
    }

    public void func_71223_ag() {
        this.field_71295_T = true;
    }

    @Override
    public ChunkCoordinates func_82114_b() {
        return new ChunkCoordinates(0, 0, 0);
    }

    @Override
    public World func_130014_f_() {
        return this.field_71305_c[0];
    }

    public int func_82357_ak() {
        return 16;
    }

    public boolean func_96290_a(World world, int i, int j, int k, EntityPlayer entityplayer) {
        return false;
    }

    public void func_104055_i(boolean flag) {
        this.field_104057_T = flag;
    }

    public boolean func_104056_am() {
        return this.field_104057_T;
    }

    public Proxy func_110454_ao() {
        return this.field_110456_c;
    }

    public static long func_130071_aq() {
        return System.currentTimeMillis();
    }

    public int func_143007_ar() {
        return this.field_143008_E;
    }

    public void func_143006_e(int i) {
        this.field_143008_E = i;
    }

    @Override
    public IChatComponent func_145748_c_() {
        return new ChatComponentText(this.func_70005_c_());
    }

    public boolean func_147136_ar() {
        return true;
    }

    public MinecraftSessionService func_147130_as() {
        return this.field_147143_S;
    }

    public GameProfileRepository func_152359_aw() {
        return this.field_152365_W;
    }

    public PlayerProfileCache func_152358_ax() {
        return this.field_152366_X;
    }

    public ServerStatusResponse func_147134_at() {
        return this.field_147147_p;
    }

    public void func_147132_au() {
        this.field_147142_T = 0L;
    }
}
