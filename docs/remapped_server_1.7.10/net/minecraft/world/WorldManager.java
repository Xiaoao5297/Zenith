package net.minecraft.world;

import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.server.MinecraftServer;

public class WorldManager implements IWorldAccess {

    private MinecraftServer field_72783_a;
    private WorldServer field_72782_b;

    public WorldManager(MinecraftServer minecraftserver, WorldServer worldserver) {
        this.field_72783_a = minecraftserver;
        this.field_72782_b = worldserver;
    }

    @Override
    public void func_72708_a(String s, double d0, double d1, double d2, double d3, double d4, double d5) {}

    @Override
    public void func_72703_a(Entity entity) {
        this.field_72782_b.func_73039_n().func_72786_a(entity);
    }

    @Override
    public void func_72709_b(Entity entity) {
        this.field_72782_b.func_73039_n().func_72790_b(entity);
    }

    @Override
    public void func_72704_a(String s, double d0, double d1, double d2, float f, float f1) {
        this.field_72783_a.func_71203_ab().func_148541_a(d0, d1, d2, f > 1.0F ? (double) (16.0F * f) : 16.0D, this.field_72782_b.field_73011_w.field_76574_g, new S29PacketSoundEffect(s, d0, d1, d2, f, f1));
    }

    @Override
    public void func_85102_a(EntityPlayer entityplayer, String s, double d0, double d1, double d2, float f, float f1) {
        this.field_72783_a.func_71203_ab().func_148543_a(entityplayer, d0, d1, d2, f > 1.0F ? (double) (16.0F * f) : 16.0D, this.field_72782_b.field_73011_w.field_76574_g, new S29PacketSoundEffect(s, d0, d1, d2, f, f1));
    }

    @Override
    public void func_147585_a(int i, int j, int k, int l, int i1, int j1) {}

    @Override
    public void func_147586_a(int i, int j, int k) {
        this.field_72782_b.func_73040_p().func_151250_a(i, j, k);
    }

    @Override
    public void func_147588_b(int i, int j, int k) {}

    @Override
    public void func_72702_a(String s, int i, int j, int k) {}

    @Override
    public void func_72706_a(EntityPlayer entityplayer, int i, int j, int k, int l, int i1) {
        this.field_72783_a.func_71203_ab().func_148543_a(entityplayer, (double) j, (double) k, (double) l, 64.0D, this.field_72782_b.field_73011_w.field_76574_g, new S28PacketEffect(i, j, k, l, i1, false));
    }

    @Override
    public void func_82746_a(int i, int j, int k, int l, int i1) {
        this.field_72783_a.func_71203_ab().func_148540_a(new S28PacketEffect(i, j, k, l, i1, true));
    }

    @Override
    public void func_147587_b(int i, int j, int k, int l, int i1) {
        Iterator iterator = this.field_72783_a.func_71203_ab().field_72404_b.iterator();

        while (iterator.hasNext()) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) iterator.next();

            if (entityplayermp != null && entityplayermp.field_70170_p == this.field_72782_b && entityplayermp.func_145782_y() != i) {
                double d0 = (double) j - entityplayermp.field_70165_t;
                double d1 = (double) k - entityplayermp.field_70163_u;
                double d2 = (double) l - entityplayermp.field_70161_v;

                if (d0 * d0 + d1 * d1 + d2 * d2 < 1024.0D) {
                    entityplayermp.field_71135_a.func_147359_a(new S25PacketBlockBreakAnim(i, j, k, l, i1));
                }
            }
        }

    }

    @Override
    public void func_147584_b() {}
}
