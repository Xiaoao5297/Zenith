package net.minecraft.server.management;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;

class PlayerManager$PlayerInstance {

    private final List field_73263_b;
    private final ChunkCoordIntPair field_73264_c;
    private short[] field_151254_d;
    private int field_73262_e;
    private int field_73260_f;
    private long field_111198_g;

    public PlayerManager$PlayerInstance(PlayerManager playermanager, int i, int j) {
        this.field_73265_a = playermanager;
        this.field_73263_b = new ArrayList();
        this.field_151254_d = new short[64];
        this.field_73264_c = new ChunkCoordIntPair(i, j);
        playermanager.func_72688_a().field_73059_b.func_73158_c(i, j);
    }

    public void func_73255_a(EntityPlayerMP entityplayermp) {
        if (this.field_73263_b.contains(entityplayermp)) {
            PlayerManager.access$000().debug("Failed to add player. {} already is in chunk {}, {}", entityplayermp, this.field_73264_c.field_77276_a, this.field_73264_c.field_77275_b);
        } else {
            if (this.field_73263_b.isEmpty()) {
                this.field_111198_g = PlayerManager.access$100(this.field_73265_a).func_82737_E();
            }

            this.field_73263_b.add(entityplayermp);
            entityplayermp.field_71129_f.add(this.field_73264_c);
        }
    }

    public void func_73252_b(EntityPlayerMP entityplayermp) {
        if (this.field_73263_b.contains(entityplayermp)) {
            Chunk chunk = PlayerManager.access$100(this.field_73265_a).func_72964_e(this.field_73264_c.field_77276_a, this.field_73264_c.field_77275_b);

            if (chunk.func_150802_k()) {
                entityplayermp.field_71135_a.func_147359_a(new S21PacketChunkData(chunk, true, 0));
            }

            this.field_73263_b.remove(entityplayermp);
            entityplayermp.field_71129_f.remove(this.field_73264_c);
            if (this.field_73263_b.isEmpty()) {
                long i = (long) this.field_73264_c.field_77276_a + 2147483647L | (long) this.field_73264_c.field_77275_b + 2147483647L << 32;

                this.func_111196_a(chunk);
                PlayerManager.access$200(this.field_73265_a).func_76159_d(i);
                PlayerManager.access$300(this.field_73265_a).remove(this);
                if (this.field_73262_e > 0) {
                    PlayerManager.access$400(this.field_73265_a).remove(this);
                }

                this.field_73265_a.func_72688_a().field_73059_b.func_73241_b(this.field_73264_c.field_77276_a, this.field_73264_c.field_77275_b);
            }

        }
    }

    public void func_111194_a() {
        this.func_111196_a(PlayerManager.access$100(this.field_73265_a).func_72964_e(this.field_73264_c.field_77276_a, this.field_73264_c.field_77275_b));
    }

    private void func_111196_a(Chunk chunk) {
        chunk.field_111204_q += PlayerManager.access$100(this.field_73265_a).func_82737_E() - this.field_111198_g;
        this.field_111198_g = PlayerManager.access$100(this.field_73265_a).func_82737_E();
    }

    public void func_151253_a(int i, int j, int k) {
        if (this.field_73262_e == 0) {
            PlayerManager.access$400(this.field_73265_a).add(this);
        }

        this.field_73260_f |= 1 << (j >> 4);
        if (this.field_73262_e < 64) {
            short short0 = (short) (i << 12 | k << 8 | j);

            for (int l = 0; l < this.field_73262_e; ++l) {
                if (this.field_151254_d[l] == short0) {
                    return;
                }
            }

            this.field_151254_d[this.field_73262_e++] = short0;
        }

    }

    public void func_151251_a(Packet packet) {
        for (int i = 0; i < this.field_73263_b.size(); ++i) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) this.field_73263_b.get(i);

            if (!entityplayermp.field_71129_f.contains(this.field_73264_c)) {
                entityplayermp.field_71135_a.func_147359_a(packet);
            }
        }

    }

    public void func_73254_a() {
        if (this.field_73262_e != 0) {
            int i;
            int j;
            int k;

            if (this.field_73262_e == 1) {
                i = this.field_73264_c.field_77276_a * 16 + (this.field_151254_d[0] >> 12 & 15);
                j = this.field_151254_d[0] & 255;
                k = this.field_73264_c.field_77275_b * 16 + (this.field_151254_d[0] >> 8 & 15);
                this.func_151251_a(new S23PacketBlockChange(i, j, k, PlayerManager.access$100(this.field_73265_a)));
                if (PlayerManager.access$100(this.field_73265_a).func_147439_a(i, j, k).func_149716_u()) {
                    this.func_151252_a(PlayerManager.access$100(this.field_73265_a).func_147438_o(i, j, k));
                }
            } else {
                int l;

                if (this.field_73262_e == 64) {
                    i = this.field_73264_c.field_77276_a * 16;
                    j = this.field_73264_c.field_77275_b * 16;
                    this.func_151251_a(new S21PacketChunkData(PlayerManager.access$100(this.field_73265_a).func_72964_e(this.field_73264_c.field_77276_a, this.field_73264_c.field_77275_b), false, this.field_73260_f));

                    for (k = 0; k < 16; ++k) {
                        if ((this.field_73260_f & 1 << k) != 0) {
                            l = k << 4;
                            List list = PlayerManager.access$100(this.field_73265_a).func_147486_a(i, l, j, i + 16, l + 16, j + 16);

                            for (int i1 = 0; i1 < list.size(); ++i1) {
                                this.func_151252_a((TileEntity) list.get(i1));
                            }
                        }
                    }
                } else {
                    this.func_151251_a(new S22PacketMultiBlockChange(this.field_73262_e, this.field_151254_d, PlayerManager.access$100(this.field_73265_a).func_72964_e(this.field_73264_c.field_77276_a, this.field_73264_c.field_77275_b)));

                    for (i = 0; i < this.field_73262_e; ++i) {
                        j = this.field_73264_c.field_77276_a * 16 + (this.field_151254_d[i] >> 12 & 15);
                        k = this.field_151254_d[i] & 255;
                        l = this.field_73264_c.field_77275_b * 16 + (this.field_151254_d[i] >> 8 & 15);
                        if (PlayerManager.access$100(this.field_73265_a).func_147439_a(j, k, l).func_149716_u()) {
                            this.func_151252_a(PlayerManager.access$100(this.field_73265_a).func_147438_o(j, k, l));
                        }
                    }
                }
            }

            this.field_73262_e = 0;
            this.field_73260_f = 0;
        }
    }

    private void func_151252_a(TileEntity tileentity) {
        if (tileentity != null) {
            Packet packet = tileentity.func_145844_m();

            if (packet != null) {
                this.func_151251_a(packet);
            }
        }

    }
}
