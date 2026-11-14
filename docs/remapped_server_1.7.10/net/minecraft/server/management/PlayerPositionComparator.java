package net.minecraft.server.management;

import java.util.Comparator;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;

public class PlayerPositionComparator implements Comparator {

    private final ChunkCoordinates field_82548_a;

    public PlayerPositionComparator(ChunkCoordinates chunkcoordinates) {
        this.field_82548_a = chunkcoordinates;
    }

    public int compare(EntityPlayerMP entityplayermp, EntityPlayerMP entityplayermp1) {
        double d0 = entityplayermp.func_70092_e((double) this.field_82548_a.field_71574_a, (double) this.field_82548_a.field_71572_b, (double) this.field_82548_a.field_71573_c);
        double d1 = entityplayermp1.func_70092_e((double) this.field_82548_a.field_71574_a, (double) this.field_82548_a.field_71572_b, (double) this.field_82548_a.field_71573_c);

        return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
    }
}
