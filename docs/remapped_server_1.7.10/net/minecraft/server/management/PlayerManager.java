package net.minecraft.server.management;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerManager {

    private static final Logger field_152627_a = LogManager.getLogger();
    private final WorldServer field_72701_a;
    private final List field_72699_b = new ArrayList();
    private final LongHashMap field_72700_c = new LongHashMap();
    private final List field_72697_d = new ArrayList();
    private final List field_111193_e = new ArrayList();
    private int field_72698_e;
    private long field_111192_g;
    private final int[][] field_72696_f = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

    public PlayerManager(WorldServer worldserver) {
        this.field_72701_a = worldserver;
        this.func_152622_a(worldserver.func_73046_m().func_71203_ab().func_72395_o());
    }

    public WorldServer func_72688_a() {
        return this.field_72701_a;
    }

    public void func_72693_b() {
        long i = this.field_72701_a.func_82737_E();
        PlayerManager$PlayerInstance playermanager$playerinstance;
        int j;

        if (i - this.field_111192_g > 8000L) {
            this.field_111192_g = i;

            for (j = 0; j < this.field_111193_e.size(); ++j) {
                playermanager$playerinstance = (PlayerManager$PlayerInstance) this.field_111193_e.get(j);
                playermanager$playerinstance.func_73254_a();
                playermanager$playerinstance.func_111194_a();
            }
        } else {
            for (j = 0; j < this.field_72697_d.size(); ++j) {
                playermanager$playerinstance = (PlayerManager$PlayerInstance) this.field_72697_d.get(j);
                playermanager$playerinstance.func_73254_a();
            }
        }

        this.field_72697_d.clear();
        if (this.field_72699_b.isEmpty()) {
            WorldProvider worldprovider = this.field_72701_a.field_73011_w;

            if (!worldprovider.func_76567_e()) {
                this.field_72701_a.field_73059_b.func_73240_a();
            }
        }

    }

    public boolean func_152621_a(int i, int j) {
        long k = (long) i + 2147483647L | (long) j + 2147483647L << 32;

        return this.field_72700_c.func_76164_a(k) != null;
    }

    private PlayerManager$PlayerInstance func_72690_a(int i, int j, boolean flag) {
        long k = (long) i + 2147483647L | (long) j + 2147483647L << 32;
        PlayerManager$PlayerInstance playermanager$playerinstance = (PlayerManager$PlayerInstance) this.field_72700_c.func_76164_a(k);

        if (playermanager$playerinstance == null && flag) {
            playermanager$playerinstance = new PlayerManager$PlayerInstance(this, i, j);
            this.field_72700_c.func_76163_a(k, playermanager$playerinstance);
            this.field_111193_e.add(playermanager$playerinstance);
        }

        return playermanager$playerinstance;
    }

    public void func_151250_a(int i, int j, int k) {
        int l = i >> 4;
        int i1 = k >> 4;
        PlayerManager$PlayerInstance playermanager$playerinstance = this.func_72690_a(l, i1, false);

        if (playermanager$playerinstance != null) {
            playermanager$playerinstance.func_151253_a(i & 15, j, k & 15);
        }

    }

    public void func_72683_a(EntityPlayerMP entityplayermp) {
        int i = (int) entityplayermp.field_70165_t >> 4;
        int j = (int) entityplayermp.field_70161_v >> 4;

        entityplayermp.field_71131_d = entityplayermp.field_70165_t;
        entityplayermp.field_71132_e = entityplayermp.field_70161_v;

        for (int k = i - this.field_72698_e; k <= i + this.field_72698_e; ++k) {
            for (int l = j - this.field_72698_e; l <= j + this.field_72698_e; ++l) {
                this.func_72690_a(k, l, true).func_73255_a(entityplayermp);
            }
        }

        this.field_72699_b.add(entityplayermp);
        this.func_72691_b(entityplayermp);
    }

    public void func_72691_b(EntityPlayerMP entityplayermp) {
        ArrayList arraylist = new ArrayList(entityplayermp.field_71129_f);
        int i = 0;
        int j = this.field_72698_e;
        int k = (int) entityplayermp.field_70165_t >> 4;
        int l = (int) entityplayermp.field_70161_v >> 4;
        int i1 = 0;
        int j1 = 0;
        ChunkCoordIntPair chunkcoordintpair = PlayerManager$PlayerInstance.access$500(this.func_72690_a(k, l, true));

        entityplayermp.field_71129_f.clear();
        if (arraylist.contains(chunkcoordintpair)) {
            entityplayermp.field_71129_f.add(chunkcoordintpair);
        }

        int k1;

        for (k1 = 1; k1 <= j * 2; ++k1) {
            for (int l1 = 0; l1 < 2; ++l1) {
                int[] aint = this.field_72696_f[i++ % 4];

                for (int i2 = 0; i2 < k1; ++i2) {
                    i1 += aint[0];
                    j1 += aint[1];
                    chunkcoordintpair = PlayerManager$PlayerInstance.access$500(this.func_72690_a(k + i1, l + j1, true));
                    if (arraylist.contains(chunkcoordintpair)) {
                        entityplayermp.field_71129_f.add(chunkcoordintpair);
                    }
                }
            }
        }

        i %= 4;

        for (k1 = 0; k1 < j * 2; ++k1) {
            i1 += this.field_72696_f[i][0];
            j1 += this.field_72696_f[i][1];
            chunkcoordintpair = PlayerManager$PlayerInstance.access$500(this.func_72690_a(k + i1, l + j1, true));
            if (arraylist.contains(chunkcoordintpair)) {
                entityplayermp.field_71129_f.add(chunkcoordintpair);
            }
        }

    }

    public void func_72695_c(EntityPlayerMP entityplayermp) {
        int i = (int) entityplayermp.field_71131_d >> 4;
        int j = (int) entityplayermp.field_71132_e >> 4;

        for (int k = i - this.field_72698_e; k <= i + this.field_72698_e; ++k) {
            for (int l = j - this.field_72698_e; l <= j + this.field_72698_e; ++l) {
                PlayerManager$PlayerInstance playermanager$playerinstance = this.func_72690_a(k, l, false);

                if (playermanager$playerinstance != null) {
                    playermanager$playerinstance.func_73252_b(entityplayermp);
                }
            }
        }

        this.field_72699_b.remove(entityplayermp);
    }

    private boolean func_72684_a(int i, int j, int k, int l, int i1) {
        int j1 = i - k;
        int k1 = j - l;

        return j1 >= -i1 && j1 <= i1 ? k1 >= -i1 && k1 <= i1 : false;
    }

    public void func_72685_d(EntityPlayerMP entityplayermp) {
        int i = (int) entityplayermp.field_70165_t >> 4;
        int j = (int) entityplayermp.field_70161_v >> 4;
        double d0 = entityplayermp.field_71131_d - entityplayermp.field_70165_t;
        double d1 = entityplayermp.field_71132_e - entityplayermp.field_70161_v;
        double d2 = d0 * d0 + d1 * d1;

        if (d2 >= 64.0D) {
            int k = (int) entityplayermp.field_71131_d >> 4;
            int l = (int) entityplayermp.field_71132_e >> 4;
            int i1 = this.field_72698_e;
            int j1 = i - k;
            int k1 = j - l;

            if (j1 != 0 || k1 != 0) {
                for (int l1 = i - i1; l1 <= i + i1; ++l1) {
                    for (int i2 = j - i1; i2 <= j + i1; ++i2) {
                        if (!this.func_72684_a(l1, i2, k, l, i1)) {
                            this.func_72690_a(l1, i2, true).func_73255_a(entityplayermp);
                        }

                        if (!this.func_72684_a(l1 - j1, i2 - k1, i, j, i1)) {
                            PlayerManager$PlayerInstance playermanager$playerinstance = this.func_72690_a(l1 - j1, i2 - k1, false);

                            if (playermanager$playerinstance != null) {
                                playermanager$playerinstance.func_73252_b(entityplayermp);
                            }
                        }
                    }
                }

                this.func_72691_b(entityplayermp);
                entityplayermp.field_71131_d = entityplayermp.field_70165_t;
                entityplayermp.field_71132_e = entityplayermp.field_70161_v;
            }
        }
    }

    public boolean func_72694_a(EntityPlayerMP entityplayermp, int i, int j) {
        PlayerManager$PlayerInstance playermanager$playerinstance = this.func_72690_a(i, j, false);

        return playermanager$playerinstance != null && PlayerManager$PlayerInstance.access$600(playermanager$playerinstance).contains(entityplayermp) && !entityplayermp.field_71129_f.contains(PlayerManager$PlayerInstance.access$500(playermanager$playerinstance));
    }

    public void func_152622_a(int i) {
        i = MathHelper.func_76125_a(i, 3, 20);
        if (i != this.field_72698_e) {
            int j = i - this.field_72698_e;
            Iterator iterator = this.field_72699_b.iterator();

            while (iterator.hasNext()) {
                EntityPlayerMP entityplayermp = (EntityPlayerMP) iterator.next();
                int k = (int) entityplayermp.field_70165_t >> 4;
                int l = (int) entityplayermp.field_70161_v >> 4;
                int i1;
                int j1;

                if (j > 0) {
                    for (i1 = k - i; i1 <= k + i; ++i1) {
                        for (j1 = l - i; j1 <= l + i; ++j1) {
                            PlayerManager$PlayerInstance playermanager$playerinstance = this.func_72690_a(i1, j1, true);

                            if (!PlayerManager$PlayerInstance.access$600(playermanager$playerinstance).contains(entityplayermp)) {
                                playermanager$playerinstance.func_73255_a(entityplayermp);
                            }
                        }
                    }
                } else {
                    for (i1 = k - this.field_72698_e; i1 <= k + this.field_72698_e; ++i1) {
                        for (j1 = l - this.field_72698_e; j1 <= l + this.field_72698_e; ++j1) {
                            if (!this.func_72684_a(i1, j1, k, l, i)) {
                                this.func_72690_a(i1, j1, true).func_73252_b(entityplayermp);
                            }
                        }
                    }
                }
            }

            this.field_72698_e = i;
        }
    }

    public static int func_72686_a(int i) {
        return i * 16 - 16;
    }
}
