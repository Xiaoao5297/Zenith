package net.minecraft.network.play.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;

public class S27PacketExplosion extends Packet {

    private double field_149158_a;
    private double field_149156_b;
    private double field_149157_c;
    private float field_149154_d;
    private List field_149155_e;
    private float field_149152_f;
    private float field_149153_g;
    private float field_149159_h;

    public S27PacketExplosion() {}

    public S27PacketExplosion(double d0, double d1, double d2, float f, List list, Vec3 vec3) {
        this.field_149158_a = d0;
        this.field_149156_b = d1;
        this.field_149157_c = d2;
        this.field_149154_d = f;
        this.field_149155_e = new ArrayList(list);
        if (vec3 != null) {
            this.field_149152_f = (float) vec3.field_72450_a;
            this.field_149153_g = (float) vec3.field_72448_b;
            this.field_149159_h = (float) vec3.field_72449_c;
        }

    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149158_a = (double) packetbuffer.readFloat();
        this.field_149156_b = (double) packetbuffer.readFloat();
        this.field_149157_c = (double) packetbuffer.readFloat();
        this.field_149154_d = packetbuffer.readFloat();
        int i = packetbuffer.readInt();

        this.field_149155_e = new ArrayList(i);
        int j = (int) this.field_149158_a;
        int k = (int) this.field_149156_b;
        int l = (int) this.field_149157_c;

        for (int i1 = 0; i1 < i; ++i1) {
            int j1 = packetbuffer.readByte() + j;
            int k1 = packetbuffer.readByte() + k;
            int l1 = packetbuffer.readByte() + l;

            this.field_149155_e.add(new ChunkPosition(j1, k1, l1));
        }

        this.field_149152_f = packetbuffer.readFloat();
        this.field_149153_g = packetbuffer.readFloat();
        this.field_149159_h = packetbuffer.readFloat();
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeFloat((float) this.field_149158_a);
        packetbuffer.writeFloat((float) this.field_149156_b);
        packetbuffer.writeFloat((float) this.field_149157_c);
        packetbuffer.writeFloat(this.field_149154_d);
        packetbuffer.writeInt(this.field_149155_e.size());
        int i = (int) this.field_149158_a;
        int j = (int) this.field_149156_b;
        int k = (int) this.field_149157_c;
        Iterator iterator = this.field_149155_e.iterator();

        while (iterator.hasNext()) {
            ChunkPosition chunkposition = (ChunkPosition) iterator.next();
            int l = chunkposition.field_151329_a - i;
            int i1 = chunkposition.field_151327_b - j;
            int j1 = chunkposition.field_151328_c - k;

            packetbuffer.writeByte(l);
            packetbuffer.writeByte(i1);
            packetbuffer.writeByte(j1);
        }

        packetbuffer.writeFloat(this.field_149152_f);
        packetbuffer.writeFloat(this.field_149153_g);
        packetbuffer.writeFloat(this.field_149159_h);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147283_a(this);
    }
}
