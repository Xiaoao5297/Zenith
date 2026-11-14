package net.minecraft.world;

import net.minecraft.util.ChunkCoordinates;

public class Teleporter$PortalPosition extends ChunkCoordinates {

    public long field_85087_d;

    public Teleporter$PortalPosition(Teleporter teleporter, int i, int j, int k, long l) {
        super(i, j, k);
        this.field_85088_e = teleporter;
        this.field_85087_d = l;
    }
}
