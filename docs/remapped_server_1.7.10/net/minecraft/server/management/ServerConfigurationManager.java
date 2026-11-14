package net.minecraft.server.management;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings$GameType;
import net.minecraft.world.demo.DemoWorldManager;
import net.minecraft.world.storage.IPlayerFileData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ServerConfigurationManager {

    public static final File field_152613_a = new File("banned-players.json");
    public static final File field_152614_b = new File("banned-ips.json");
    public static final File field_152615_c = new File("ops.json");
    public static final File field_152616_d = new File("whitelist.json");
    private static final Logger field_148546_d = LogManager.getLogger();
    private static final SimpleDateFormat field_72403_e = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
    private final MinecraftServer field_72400_f;
    public final List field_72404_b = new ArrayList();
    private final UserListBans field_72401_g;
    private final BanList field_72413_h;
    private final UserListOps field_72414_i;
    private final UserListWhitelist field_72411_j;
    private final Map field_148547_k;
    private IPlayerFileData field_72412_k;
    private boolean field_72409_l;
    protected int field_72405_c;
    private int field_72402_d;
    private WorldSettings$GameType field_72410_m;
    private boolean field_72407_n;
    private int field_72408_o;

    public ServerConfigurationManager(MinecraftServer minecraftserver) {
        this.field_72401_g = new UserListBans(ServerConfigurationManager.field_152613_a);
        this.field_72413_h = new BanList(ServerConfigurationManager.field_152614_b);
        this.field_72414_i = new UserListOps(ServerConfigurationManager.field_152615_c);
        this.field_72411_j = new UserListWhitelist(ServerConfigurationManager.field_152616_d);
        this.field_148547_k = Maps.newHashMap();
        this.field_72400_f = minecraftserver;
        this.field_72401_g.func_152686_a(false);
        this.field_72413_h.func_152686_a(false);
        this.field_72405_c = 8;
    }

    public void func_72355_a(NetworkManager networkmanager, EntityPlayerMP entityplayermp) {
        GameProfile gameprofile = entityplayermp.func_146103_bH();
        PlayerProfileCache playerprofilecache = this.field_72400_f.func_152358_ax();
        GameProfile gameprofile1 = playerprofilecache.func_152652_a(gameprofile.getId());
        String s = gameprofile1 == null ? gameprofile.getName() : gameprofile1.getName();

        playerprofilecache.func_152649_a(gameprofile);
        NBTTagCompound nbttagcompound = this.func_72380_a(entityplayermp);

        entityplayermp.func_70029_a(this.field_72400_f.func_71218_a(entityplayermp.field_71093_bK));
        entityplayermp.field_71134_c.func_73080_a((WorldServer) entityplayermp.field_70170_p);
        String s1 = "local";

        if (networkmanager.func_74430_c() != null) {
            s1 = networkmanager.func_74430_c().toString();
        }

        ServerConfigurationManager.field_148546_d.info(entityplayermp.func_70005_c_() + "[" + s1 + "] logged in with entity id " + entityplayermp.func_145782_y() + " at (" + entityplayermp.field_70165_t + ", " + entityplayermp.field_70163_u + ", " + entityplayermp.field_70161_v + ")");
        WorldServer worldserver = this.field_72400_f.func_71218_a(entityplayermp.field_71093_bK);
        ChunkCoordinates chunkcoordinates = worldserver.func_72861_E();

        this.func_72381_a(entityplayermp, (EntityPlayerMP) null, worldserver);
        NetHandlerPlayServer nethandlerplayserver = new NetHandlerPlayServer(this.field_72400_f, networkmanager, entityplayermp);

        nethandlerplayserver.func_147359_a(new S01PacketJoinGame(entityplayermp.func_145782_y(), entityplayermp.field_71134_c.func_73081_b(), worldserver.func_72912_H().func_76093_s(), worldserver.field_73011_w.field_76574_g, worldserver.field_73013_u, this.func_72352_l(), worldserver.func_72912_H().func_76067_t()));
        nethandlerplayserver.func_147359_a(new S3FPacketCustomPayload("MC|Brand", this.func_72365_p().getServerModName().getBytes(Charsets.UTF_8)));
        nethandlerplayserver.func_147359_a(new S05PacketSpawnPosition(chunkcoordinates.field_71574_a, chunkcoordinates.field_71572_b, chunkcoordinates.field_71573_c));
        nethandlerplayserver.func_147359_a(new S39PacketPlayerAbilities(entityplayermp.field_71075_bZ));
        nethandlerplayserver.func_147359_a(new S09PacketHeldItemChange(entityplayermp.field_71071_by.field_70461_c));
        entityplayermp.func_147099_x().func_150877_d();
        entityplayermp.func_147099_x().func_150884_b(entityplayermp);
        this.func_96456_a((ServerScoreboard) worldserver.func_96441_U(), entityplayermp);
        this.field_72400_f.func_147132_au();
        ChatComponentTranslation chatcomponenttranslation;

        if (!entityplayermp.func_70005_c_().equalsIgnoreCase(s)) {
            chatcomponenttranslation = new ChatComponentTranslation("multiplayer.player.joined.renamed", new Object[]{entityplayermp.func_145748_c_(), s});
        } else {
            chatcomponenttranslation = new ChatComponentTranslation("multiplayer.player.joined", new Object[]{entityplayermp.func_145748_c_()});
        }

        chatcomponenttranslation.func_150256_b().func_150238_a(EnumChatFormatting.YELLOW);
        this.func_148539_a(chatcomponenttranslation);
        this.func_72377_c(entityplayermp);
        nethandlerplayserver.func_147364_a(entityplayermp.field_70165_t, entityplayermp.field_70163_u, entityplayermp.field_70161_v, entityplayermp.field_70177_z, entityplayermp.field_70125_A);
        this.func_72354_b(entityplayermp, worldserver);
        if (this.field_72400_f.func_147133_T().length() > 0) {
            entityplayermp.func_147095_a(this.field_72400_f.func_147133_T());
        }

        Iterator iterator = entityplayermp.func_70651_bq().iterator();

        while (iterator.hasNext()) {
            PotionEffect potioneffect = (PotionEffect) iterator.next();

            nethandlerplayserver.func_147359_a(new S1DPacketEntityEffect(entityplayermp.func_145782_y(), potioneffect));
        }

        entityplayermp.func_71116_b();
        if (nbttagcompound != null && nbttagcompound.func_150297_b("Riding", 10)) {
            Entity entity = EntityList.func_75615_a(nbttagcompound.func_74775_l("Riding"), worldserver);

            if (entity != null) {
                entity.field_98038_p = true;
                worldserver.func_72838_d(entity);
                entityplayermp.func_70078_a(entity);
                entity.field_98038_p = false;
            }
        }

    }

    protected void func_96456_a(ServerScoreboard serverscoreboard, EntityPlayerMP entityplayermp) {
        HashSet hashset = new HashSet();
        Iterator iterator = serverscoreboard.func_96525_g().iterator();

        while (iterator.hasNext()) {
            ScorePlayerTeam scoreplayerteam = (ScorePlayerTeam) iterator.next();

            entityplayermp.field_71135_a.func_147359_a(new S3EPacketTeams(scoreplayerteam, 0));
        }

        for (int i = 0; i < 3; ++i) {
            ScoreObjective scoreobjective = serverscoreboard.func_96539_a(i);

            if (scoreobjective != null && !hashset.contains(scoreobjective)) {
                List list = serverscoreboard.func_96550_d(scoreobjective);
                Iterator iterator1 = list.iterator();

                while (iterator1.hasNext()) {
                    Packet packet = (Packet) iterator1.next();

                    entityplayermp.field_71135_a.func_147359_a(packet);
                }

                hashset.add(scoreobjective);
            }
        }

    }

    public void func_72364_a(WorldServer[] aworldserver) {
        this.field_72412_k = aworldserver[0].func_72860_G().func_75756_e();
    }

    public void func_72375_a(EntityPlayerMP entityplayermp, WorldServer worldserver) {
        WorldServer worldserver1 = entityplayermp.func_71121_q();

        if (worldserver != null) {
            worldserver.func_73040_p().func_72695_c(entityplayermp);
        }

        worldserver1.func_73040_p().func_72683_a(entityplayermp);
        worldserver1.field_73059_b.func_73158_c((int) entityplayermp.field_70165_t >> 4, (int) entityplayermp.field_70161_v >> 4);
    }

    public int func_72372_a() {
        return PlayerManager.func_72686_a(this.func_72395_o());
    }

    public NBTTagCompound func_72380_a(EntityPlayerMP entityplayermp) {
        NBTTagCompound nbttagcompound = this.field_72400_f.field_71305_c[0].func_72912_H().func_76072_h();
        NBTTagCompound nbttagcompound1;

        if (entityplayermp.func_70005_c_().equals(this.field_72400_f.func_71214_G()) && nbttagcompound != null) {
            entityplayermp.func_70020_e(nbttagcompound);
            nbttagcompound1 = nbttagcompound;
            ServerConfigurationManager.field_148546_d.debug("loading single player");
        } else {
            nbttagcompound1 = this.field_72412_k.func_75752_b(entityplayermp);
        }

        return nbttagcompound1;
    }

    protected void func_72391_b(EntityPlayerMP entityplayermp) {
        this.field_72412_k.func_75753_a(entityplayermp);
        StatisticsFile statisticsfile = (StatisticsFile) this.field_148547_k.get(entityplayermp.func_110124_au());

        if (statisticsfile != null) {
            statisticsfile.func_150883_b();
        }

    }

    public void func_72377_c(EntityPlayerMP entityplayermp) {
        this.func_148540_a(new S38PacketPlayerListItem(entityplayermp.func_70005_c_(), true, 1000));
        this.field_72404_b.add(entityplayermp);
        WorldServer worldserver = this.field_72400_f.func_71218_a(entityplayermp.field_71093_bK);

        worldserver.func_72838_d(entityplayermp);
        this.func_72375_a(entityplayermp, (WorldServer) null);

        for (int i = 0; i < this.field_72404_b.size(); ++i) {
            EntityPlayerMP entityplayermp1 = (EntityPlayerMP) this.field_72404_b.get(i);

            entityplayermp.field_71135_a.func_147359_a(new S38PacketPlayerListItem(entityplayermp1.func_70005_c_(), true, entityplayermp1.field_71138_i));
        }

    }

    public void func_72358_d(EntityPlayerMP entityplayermp) {
        entityplayermp.func_71121_q().func_73040_p().func_72685_d(entityplayermp);
    }

    public void func_72367_e(EntityPlayerMP entityplayermp) {
        entityplayermp.func_71029_a(StatList.field_75947_j);
        this.func_72391_b(entityplayermp);
        WorldServer worldserver = entityplayermp.func_71121_q();

        if (entityplayermp.field_70154_o != null) {
            worldserver.func_72973_f(entityplayermp.field_70154_o);
            ServerConfigurationManager.field_148546_d.debug("removing player mount");
        }

        worldserver.func_72900_e(entityplayermp);
        worldserver.func_73040_p().func_72695_c(entityplayermp);
        this.field_72404_b.remove(entityplayermp);
        this.field_148547_k.remove(entityplayermp.func_110124_au());
        this.func_148540_a(new S38PacketPlayerListItem(entityplayermp.func_70005_c_(), false, 9999));
    }

    public String func_148542_a(SocketAddress socketaddress, GameProfile gameprofile) {
        String s;

        if (this.field_72401_g.func_152702_a(gameprofile)) {
            UserListBansEntry userlistbansentry = (UserListBansEntry) this.field_72401_g.func_152683_b(gameprofile);

            s = "You are banned from this server!\nReason: " + userlistbansentry.func_73686_f();
            if (userlistbansentry.func_73680_d() != null) {
                s = s + "\nYour ban will be removed on " + ServerConfigurationManager.field_72403_e.format(userlistbansentry.func_73680_d());
            }

            return s;
        } else if (!this.func_152607_e(gameprofile)) {
            return "You are not white-listed on this server!";
        } else if (this.field_72413_h.func_152708_a(socketaddress)) {
            IPBanEntry ipbanentry = this.field_72413_h.func_152709_b(socketaddress);

            s = "Your IP address is banned from this server!\nReason: " + ipbanentry.func_73686_f();
            if (ipbanentry.func_73680_d() != null) {
                s = s + "\nYour ban will be removed on " + ServerConfigurationManager.field_72403_e.format(ipbanentry.func_73680_d());
            }

            return s;
        } else {
            return this.field_72404_b.size() >= this.field_72405_c ? "The server is full!" : null;
        }
    }

    public EntityPlayerMP func_148545_a(GameProfile gameprofile) {
        UUID uuid = EntityPlayer.func_146094_a(gameprofile);
        ArrayList arraylist = Lists.newArrayList();

        EntityPlayerMP entityplayermp;

        for (int i = 0; i < this.field_72404_b.size(); ++i) {
            entityplayermp = (EntityPlayerMP) this.field_72404_b.get(i);
            if (entityplayermp.func_110124_au().equals(uuid)) {
                arraylist.add(entityplayermp);
            }
        }

        Iterator iterator = arraylist.iterator();

        while (iterator.hasNext()) {
            entityplayermp = (EntityPlayerMP) iterator.next();
            entityplayermp.field_71135_a.func_147360_c("You logged in from another location");
        }

        Object object;

        if (this.field_72400_f.func_71242_L()) {
            object = new DemoWorldManager(this.field_72400_f.func_71218_a(0));
        } else {
            object = new ItemInWorldManager(this.field_72400_f.func_71218_a(0));
        }

        return new EntityPlayerMP(this.field_72400_f, this.field_72400_f.func_71218_a(0), gameprofile, (ItemInWorldManager) object);
    }

    public EntityPlayerMP func_72368_a(EntityPlayerMP entityplayermp, int i, boolean flag) {
        entityplayermp.func_71121_q().func_73039_n().func_72787_a(entityplayermp);
        entityplayermp.func_71121_q().func_73039_n().func_72790_b(entityplayermp);
        entityplayermp.func_71121_q().func_73040_p().func_72695_c(entityplayermp);
        this.field_72404_b.remove(entityplayermp);
        this.field_72400_f.func_71218_a(entityplayermp.field_71093_bK).func_72973_f(entityplayermp);
        ChunkCoordinates chunkcoordinates = entityplayermp.func_70997_bJ();
        boolean flag1 = entityplayermp.func_82245_bX();

        entityplayermp.field_71093_bK = i;
        Object object;

        if (this.field_72400_f.func_71242_L()) {
            object = new DemoWorldManager(this.field_72400_f.func_71218_a(entityplayermp.field_71093_bK));
        } else {
            object = new ItemInWorldManager(this.field_72400_f.func_71218_a(entityplayermp.field_71093_bK));
        }

        EntityPlayerMP entityplayermp1 = new EntityPlayerMP(this.field_72400_f, this.field_72400_f.func_71218_a(entityplayermp.field_71093_bK), entityplayermp.func_146103_bH(), (ItemInWorldManager) object);

        entityplayermp1.field_71135_a = entityplayermp.field_71135_a;
        entityplayermp1.func_71049_a(entityplayermp, flag);
        entityplayermp1.func_145769_d(entityplayermp.func_145782_y());
        WorldServer worldserver = this.field_72400_f.func_71218_a(entityplayermp.field_71093_bK);

        this.func_72381_a(entityplayermp1, entityplayermp, worldserver);
        ChunkCoordinates chunkcoordinates1;

        if (chunkcoordinates != null) {
            chunkcoordinates1 = EntityPlayer.func_71056_a(this.field_72400_f.func_71218_a(entityplayermp.field_71093_bK), chunkcoordinates, flag1);
            if (chunkcoordinates1 != null) {
                entityplayermp1.func_70012_b((double) ((float) chunkcoordinates1.field_71574_a + 0.5F), (double) ((float) chunkcoordinates1.field_71572_b + 0.1F), (double) ((float) chunkcoordinates1.field_71573_c + 0.5F), 0.0F, 0.0F);
                entityplayermp1.func_71063_a(chunkcoordinates, flag1);
            } else {
                entityplayermp1.field_71135_a.func_147359_a(new S2BPacketChangeGameState(0, 0.0F));
            }
        }

        worldserver.field_73059_b.func_73158_c((int) entityplayermp1.field_70165_t >> 4, (int) entityplayermp1.field_70161_v >> 4);

        while (!worldserver.func_72945_a(entityplayermp1, entityplayermp1.field_70121_D).isEmpty()) {
            entityplayermp1.func_70107_b(entityplayermp1.field_70165_t, entityplayermp1.field_70163_u + 1.0D, entityplayermp1.field_70161_v);
        }

        entityplayermp1.field_71135_a.func_147359_a(new S07PacketRespawn(entityplayermp1.field_71093_bK, entityplayermp1.field_70170_p.field_73013_u, entityplayermp1.field_70170_p.func_72912_H().func_76067_t(), entityplayermp1.field_71134_c.func_73081_b()));
        chunkcoordinates1 = worldserver.func_72861_E();
        entityplayermp1.field_71135_a.func_147364_a(entityplayermp1.field_70165_t, entityplayermp1.field_70163_u, entityplayermp1.field_70161_v, entityplayermp1.field_70177_z, entityplayermp1.field_70125_A);
        entityplayermp1.field_71135_a.func_147359_a(new S05PacketSpawnPosition(chunkcoordinates1.field_71574_a, chunkcoordinates1.field_71572_b, chunkcoordinates1.field_71573_c));
        entityplayermp1.field_71135_a.func_147359_a(new S1FPacketSetExperience(entityplayermp1.field_71106_cc, entityplayermp1.field_71067_cb, entityplayermp1.field_71068_ca));
        this.func_72354_b(entityplayermp1, worldserver);
        worldserver.func_73040_p().func_72683_a(entityplayermp1);
        worldserver.func_72838_d(entityplayermp1);
        this.field_72404_b.add(entityplayermp1);
        entityplayermp1.func_71116_b();
        entityplayermp1.func_70606_j(entityplayermp1.func_110143_aJ());
        return entityplayermp1;
    }

    public void func_72356_a(EntityPlayerMP entityplayermp, int i) {
        int j = entityplayermp.field_71093_bK;
        WorldServer worldserver = this.field_72400_f.func_71218_a(entityplayermp.field_71093_bK);

        entityplayermp.field_71093_bK = i;
        WorldServer worldserver1 = this.field_72400_f.func_71218_a(entityplayermp.field_71093_bK);

        entityplayermp.field_71135_a.func_147359_a(new S07PacketRespawn(entityplayermp.field_71093_bK, entityplayermp.field_70170_p.field_73013_u, entityplayermp.field_70170_p.func_72912_H().func_76067_t(), entityplayermp.field_71134_c.func_73081_b()));
        worldserver.func_72973_f(entityplayermp);
        entityplayermp.field_70128_L = false;
        this.func_82448_a(entityplayermp, j, worldserver, worldserver1);
        this.func_72375_a(entityplayermp, worldserver);
        entityplayermp.field_71135_a.func_147364_a(entityplayermp.field_70165_t, entityplayermp.field_70163_u, entityplayermp.field_70161_v, entityplayermp.field_70177_z, entityplayermp.field_70125_A);
        entityplayermp.field_71134_c.func_73080_a(worldserver1);
        this.func_72354_b(entityplayermp, worldserver1);
        this.func_72385_f(entityplayermp);
        Iterator iterator = entityplayermp.func_70651_bq().iterator();

        while (iterator.hasNext()) {
            PotionEffect potioneffect = (PotionEffect) iterator.next();

            entityplayermp.field_71135_a.func_147359_a(new S1DPacketEntityEffect(entityplayermp.func_145782_y(), potioneffect));
        }

    }

    public void func_82448_a(Entity entity, int i, WorldServer worldserver, WorldServer worldserver1) {
        double d0 = entity.field_70165_t;
        double d1 = entity.field_70161_v;
        double d2 = 8.0D;
        double d3 = entity.field_70165_t;
        double d4 = entity.field_70163_u;
        double d5 = entity.field_70161_v;
        float f = entity.field_70177_z;

        worldserver.field_72984_F.func_76320_a("moving");
        if (entity.field_71093_bK == -1) {
            d0 /= d2;
            d1 /= d2;
            entity.func_70012_b(d0, entity.field_70163_u, d1, entity.field_70177_z, entity.field_70125_A);
            if (entity.func_70089_S()) {
                worldserver.func_72866_a(entity, false);
            }
        } else if (entity.field_71093_bK == 0) {
            d0 *= d2;
            d1 *= d2;
            entity.func_70012_b(d0, entity.field_70163_u, d1, entity.field_70177_z, entity.field_70125_A);
            if (entity.func_70089_S()) {
                worldserver.func_72866_a(entity, false);
            }
        } else {
            ChunkCoordinates chunkcoordinates;

            if (i == 1) {
                chunkcoordinates = worldserver1.func_72861_E();
            } else {
                chunkcoordinates = worldserver1.func_73054_j();
            }

            d0 = (double) chunkcoordinates.field_71574_a;
            entity.field_70163_u = (double) chunkcoordinates.field_71572_b;
            d1 = (double) chunkcoordinates.field_71573_c;
            entity.func_70012_b(d0, entity.field_70163_u, d1, 90.0F, 0.0F);
            if (entity.func_70089_S()) {
                worldserver.func_72866_a(entity, false);
            }
        }

        worldserver.field_72984_F.func_76319_b();
        if (i != 1) {
            worldserver.field_72984_F.func_76320_a("placing");
            d0 = (double) MathHelper.func_76125_a((int) d0, -29999872, 29999872);
            d1 = (double) MathHelper.func_76125_a((int) d1, -29999872, 29999872);
            if (entity.func_70089_S()) {
                entity.func_70012_b(d0, entity.field_70163_u, d1, entity.field_70177_z, entity.field_70125_A);
                worldserver1.func_85176_s().func_77185_a(entity, d3, d4, d5, f);
                worldserver1.func_72838_d(entity);
                worldserver1.func_72866_a(entity, false);
            }

            worldserver.field_72984_F.func_76319_b();
        }

        entity.func_70029_a(worldserver1);
    }

    public void func_72374_b() {
        if (++this.field_72408_o > 600) {
            this.field_72408_o = 0;
        }

        if (this.field_72408_o < this.field_72404_b.size()) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) this.field_72404_b.get(this.field_72408_o);

            this.func_148540_a(new S38PacketPlayerListItem(entityplayermp.func_70005_c_(), true, entityplayermp.field_71138_i));
        }

    }

    public void func_148540_a(Packet packet) {
        for (int i = 0; i < this.field_72404_b.size(); ++i) {
            ((EntityPlayerMP) this.field_72404_b.get(i)).field_71135_a.func_147359_a(packet);
        }

    }

    public void func_148537_a(Packet packet, int i) {
        for (int j = 0; j < this.field_72404_b.size(); ++j) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) this.field_72404_b.get(j);

            if (entityplayermp.field_71093_bK == i) {
                entityplayermp.field_71135_a.func_147359_a(packet);
            }
        }

    }

    public String func_152609_b(boolean flag) {
        String s = "";
        ArrayList arraylist = Lists.newArrayList((Iterable) this.field_72404_b);

        for (int i = 0; i < arraylist.size(); ++i) {
            if (i > 0) {
                s = s + ", ";
            }

            s = s + ((EntityPlayerMP) arraylist.get(i)).func_70005_c_();
            if (flag) {
                s = s + " (" + ((EntityPlayerMP) arraylist.get(i)).func_110124_au().toString() + ")";
            }
        }

        return s;
    }

    public String[] func_72369_d() {
        String[] astring = new String[this.field_72404_b.size()];

        for (int i = 0; i < this.field_72404_b.size(); ++i) {
            astring[i] = ((EntityPlayerMP) this.field_72404_b.get(i)).func_70005_c_();
        }

        return astring;
    }

    public GameProfile[] func_152600_g() {
        GameProfile[] agameprofile = new GameProfile[this.field_72404_b.size()];

        for (int i = 0; i < this.field_72404_b.size(); ++i) {
            agameprofile[i] = ((EntityPlayerMP) this.field_72404_b.get(i)).func_146103_bH();
        }

        return agameprofile;
    }

    public UserListBans func_152608_h() {
        return this.field_72401_g;
    }

    public BanList func_72363_f() {
        return this.field_72413_h;
    }

    public void func_152605_a(GameProfile gameprofile) {
        this.field_72414_i.func_152687_a(new UserListOpsEntry(gameprofile, this.field_72400_f.func_110455_j()));
    }

    public void func_152610_b(GameProfile gameprofile) {
        this.field_72414_i.func_152684_c(gameprofile);
    }

    public boolean func_152607_e(GameProfile gameprofile) {
        return !this.field_72409_l || this.field_72414_i.func_152692_d(gameprofile) || this.field_72411_j.func_152692_d(gameprofile);
    }

    public boolean func_152596_g(GameProfile gameprofile) {
        return this.field_72414_i.func_152692_d(gameprofile) || this.field_72400_f.func_71264_H() && this.field_72400_f.field_71305_c[0].func_72912_H().func_76086_u() && this.field_72400_f.func_71214_G().equalsIgnoreCase(gameprofile.getName()) || this.field_72407_n;
    }

    public EntityPlayerMP func_152612_a(String s) {
        Iterator iterator = this.field_72404_b.iterator();

        EntityPlayerMP entityplayermp;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            entityplayermp = (EntityPlayerMP) iterator.next();
        } while (!entityplayermp.func_70005_c_().equalsIgnoreCase(s));

        return entityplayermp;
    }

    public List func_82449_a(ChunkCoordinates chunkcoordinates, int i, int j, int k, int l, int i1, int j1, Map map, String s, String s1, World world) {
        if (this.field_72404_b.isEmpty()) {
            return Collections.emptyList();
        } else {
            Object object = new ArrayList();
            boolean flag = k < 0;
            boolean flag1 = s != null && s.startsWith("!");
            boolean flag2 = s1 != null && s1.startsWith("!");
            int k1 = i * i;
            int l1 = j * j;

            k = MathHelper.func_76130_a(k);
            if (flag1) {
                s = s.substring(1);
            }

            if (flag2) {
                s1 = s1.substring(1);
            }

            for (int i2 = 0; i2 < this.field_72404_b.size(); ++i2) {
                EntityPlayerMP entityplayermp = (EntityPlayerMP) this.field_72404_b.get(i2);

                if ((world == null || entityplayermp.field_70170_p == world) && (s == null || flag1 != s.equalsIgnoreCase(entityplayermp.func_70005_c_()))) {
                    if (s1 != null) {
                        Team team = entityplayermp.func_96124_cp();
                        String s2 = team == null ? "" : team.func_96661_b();

                        if (flag2 == s1.equalsIgnoreCase(s2)) {
                            continue;
                        }
                    }

                    if (chunkcoordinates != null && (i > 0 || j > 0)) {
                        float f = chunkcoordinates.func_82371_e(entityplayermp.func_82114_b());

                        if (i > 0 && f < (float) k1 || j > 0 && f > (float) l1) {
                            continue;
                        }
                    }

                    if (this.func_96457_a(entityplayermp, map) && (l == WorldSettings$GameType.NOT_SET.func_77148_a() || l == entityplayermp.field_71134_c.func_73081_b().func_77148_a()) && (i1 <= 0 || entityplayermp.field_71068_ca >= i1) && entityplayermp.field_71068_ca <= j1) {
                        ((List) object).add(entityplayermp);
                    }
                }
            }

            if (chunkcoordinates != null) {
                Collections.sort((List) object, new PlayerPositionComparator(chunkcoordinates));
            }

            if (flag) {
                Collections.reverse((List) object);
            }

            if (k > 0) {
                object = ((List) object).subList(0, Math.min(k, ((List) object).size()));
            }

            return (List) object;
        }
    }

    private boolean func_96457_a(EntityPlayer entityplayer, Map map) {
        if (map != null && map.size() != 0) {
            Iterator iterator = map.entrySet().iterator();

            Entry entry;
            boolean flag;
            int i;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                entry = (Entry) iterator.next();
                String s = (String) entry.getKey();

                flag = false;
                if (s.endsWith("_min") && s.length() > 4) {
                    flag = true;
                    s = s.substring(0, s.length() - 4);
                }

                Scoreboard scoreboard = entityplayer.func_96123_co();
                ScoreObjective scoreobjective = scoreboard.func_96518_b(s);

                if (scoreobjective == null) {
                    return false;
                }

                Score score = entityplayer.func_96123_co().func_96529_a(entityplayer.func_70005_c_(), scoreobjective);

                i = score.func_96652_c();
                if (i < (Integer) entry.getValue() && flag) {
                    return false;
                }
            } while (i <= (Integer) entry.getValue() || flag);

            return false;
        } else {
            return true;
        }
    }

    public void func_148541_a(double d0, double d1, double d2, double d3, int i, Packet packet) {
        this.func_148543_a((EntityPlayer) null, d0, d1, d2, d3, i, packet);
    }

    public void func_148543_a(EntityPlayer entityplayer, double d0, double d1, double d2, double d3, int i, Packet packet) {
        for (int j = 0; j < this.field_72404_b.size(); ++j) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) this.field_72404_b.get(j);

            if (entityplayermp != entityplayer && entityplayermp.field_71093_bK == i) {
                double d4 = d0 - entityplayermp.field_70165_t;
                double d5 = d1 - entityplayermp.field_70163_u;
                double d6 = d2 - entityplayermp.field_70161_v;

                if (d4 * d4 + d5 * d5 + d6 * d6 < d3 * d3) {
                    entityplayermp.field_71135_a.func_147359_a(packet);
                }
            }
        }

    }

    public void func_72389_g() {
        for (int i = 0; i < this.field_72404_b.size(); ++i) {
            this.func_72391_b((EntityPlayerMP) this.field_72404_b.get(i));
        }

    }

    public void func_152601_d(GameProfile gameprofile) {
        this.field_72411_j.func_152687_a(new UserListWhitelistEntry(gameprofile));
    }

    public void func_152597_c(GameProfile gameprofile) {
        this.field_72411_j.func_152684_c(gameprofile);
    }

    public UserListWhitelist func_152599_k() {
        return this.field_72411_j;
    }

    public String[] func_152598_l() {
        return this.field_72411_j.func_152685_a();
    }

    public UserListOps func_152603_m() {
        return this.field_72414_i;
    }

    public String[] func_152606_n() {
        return this.field_72414_i.func_152685_a();
    }

    public void func_72362_j() {}

    public void func_72354_b(EntityPlayerMP entityplayermp, WorldServer worldserver) {
        entityplayermp.field_71135_a.func_147359_a(new S03PacketTimeUpdate(worldserver.func_82737_E(), worldserver.func_72820_D(), worldserver.func_82736_K().func_82766_b("doDaylightCycle")));
        if (worldserver.func_72896_J()) {
            entityplayermp.field_71135_a.func_147359_a(new S2BPacketChangeGameState(1, 0.0F));
            entityplayermp.field_71135_a.func_147359_a(new S2BPacketChangeGameState(7, worldserver.func_72867_j(1.0F)));
            entityplayermp.field_71135_a.func_147359_a(new S2BPacketChangeGameState(8, worldserver.func_72819_i(1.0F)));
        }

    }

    public void func_72385_f(EntityPlayerMP entityplayermp) {
        entityplayermp.func_71120_a(entityplayermp.field_71069_bz);
        entityplayermp.func_71118_n();
        entityplayermp.field_71135_a.func_147359_a(new S09PacketHeldItemChange(entityplayermp.field_71071_by.field_70461_c));
    }

    public int func_72394_k() {
        return this.field_72404_b.size();
    }

    public int func_72352_l() {
        return this.field_72405_c;
    }

    public String[] func_72373_m() {
        return this.field_72400_f.field_71305_c[0].func_72860_G().func_75756_e().func_75754_f();
    }

    public boolean func_72383_n() {
        return this.field_72409_l;
    }

    public void func_72371_a(boolean flag) {
        this.field_72409_l = flag;
    }

    public List func_72382_j(String s) {
        ArrayList arraylist = new ArrayList();
        Iterator iterator = this.field_72404_b.iterator();

        while (iterator.hasNext()) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) iterator.next();

            if (entityplayermp.func_71114_r().equals(s)) {
                arraylist.add(entityplayermp);
            }
        }

        return arraylist;
    }

    public int func_72395_o() {
        return this.field_72402_d;
    }

    public MinecraftServer func_72365_p() {
        return this.field_72400_f;
    }

    public NBTTagCompound func_72378_q() {
        return null;
    }

    private void func_72381_a(EntityPlayerMP entityplayermp, EntityPlayerMP entityplayermp1, World world) {
        if (entityplayermp1 != null) {
            entityplayermp.field_71134_c.func_73076_a(entityplayermp1.field_71134_c.func_73081_b());
        } else if (this.field_72410_m != null) {
            entityplayermp.field_71134_c.func_73076_a(this.field_72410_m);
        }

        entityplayermp.field_71134_c.func_73077_b(world.func_72912_H().func_76077_q());
    }

    public void func_72392_r() {
        for (int i = 0; i < this.field_72404_b.size(); ++i) {
            ((EntityPlayerMP) this.field_72404_b.get(i)).field_71135_a.func_147360_c("Server closed");
        }

    }

    public void func_148544_a(IChatComponent ichatcomponent, boolean flag) {
        this.field_72400_f.func_145747_a(ichatcomponent);
        this.func_148540_a(new S02PacketChat(ichatcomponent, flag));
    }

    public void func_148539_a(IChatComponent ichatcomponent) {
        this.func_148544_a(ichatcomponent, true);
    }

    public StatisticsFile func_152602_a(EntityPlayer entityplayer) {
        UUID uuid = entityplayer.func_110124_au();
        StatisticsFile statisticsfile = uuid == null ? null : (StatisticsFile) this.field_148547_k.get(uuid);

        if (statisticsfile == null) {
            File file = new File(this.field_72400_f.func_71218_a(0).func_72860_G().func_75765_b(), "stats");
            File file1 = new File(file, uuid.toString() + ".json");

            if (!file1.exists()) {
                File file2 = new File(file, entityplayer.func_70005_c_() + ".json");

                if (file2.exists() && file2.isFile()) {
                    file2.renameTo(file1);
                }
            }

            statisticsfile = new StatisticsFile(this.field_72400_f, file1);
            statisticsfile.func_150882_a();
            this.field_148547_k.put(uuid, statisticsfile);
        }

        return statisticsfile;
    }

    public void func_152611_a(int i) {
        this.field_72402_d = i;
        if (this.field_72400_f.field_71305_c != null) {
            WorldServer[] aworldserver = this.field_72400_f.field_71305_c;
            int j = aworldserver.length;

            for (int k = 0; k < j; ++k) {
                WorldServer worldserver = aworldserver[k];

                if (worldserver != null) {
                    worldserver.func_73040_p().func_152622_a(i);
                }
            }

        }
    }
}
