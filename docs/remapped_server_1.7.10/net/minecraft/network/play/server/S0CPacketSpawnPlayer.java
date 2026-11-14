package net.minecraft.network.play.server;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S0CPacketSpawnPlayer extends Packet {

    private int field_148957_a;
    private GameProfile field_148955_b;
    private int field_148956_c;
    private int field_148953_d;
    private int field_148954_e;
    private byte field_148951_f;
    private byte field_148952_g;
    private int field_148959_h;
    private DataWatcher field_148960_i;
    private List field_148958_j;

    public S0CPacketSpawnPlayer() {}

    public S0CPacketSpawnPlayer(EntityPlayer entityplayer) {
        this.field_148957_a = entityplayer.func_145782_y();
        this.field_148955_b = entityplayer.func_146103_bH();
        this.field_148956_c = MathHelper.func_76128_c(entityplayer.field_70165_t * 32.0D);
        this.field_148953_d = MathHelper.func_76128_c(entityplayer.field_70163_u * 32.0D);
        this.field_148954_e = MathHelper.func_76128_c(entityplayer.field_70161_v * 32.0D);
        this.field_148951_f = (byte) ((int) (entityplayer.field_70177_z * 256.0F / 360.0F));
        this.field_148952_g = (byte) ((int) (entityplayer.field_70125_A * 256.0F / 360.0F));
        ItemStack itemstack = entityplayer.field_71071_by.func_70448_g();

        this.field_148959_h = itemstack == null ? 0 : Item.func_150891_b(itemstack.func_77973_b());
        this.field_148960_i = entityplayer.func_70096_w();
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_148957_a = packetbuffer.func_150792_a();
        UUID uuid = UUID.fromString(packetbuffer.func_150789_c(36));

        this.field_148955_b = new GameProfile(uuid, packetbuffer.func_150789_c(16));
        int i = packetbuffer.func_150792_a();

        for (int j = 0; j < i; ++j) {
            String s = packetbuffer.func_150789_c(32767);
            String s1 = packetbuffer.func_150789_c(32767);
            String s2 = packetbuffer.func_150789_c(32767);

            this.field_148955_b.getProperties().put(s, new Property(s, s1, s2));
        }

        this.field_148956_c = packetbuffer.readInt();
        this.field_148953_d = packetbuffer.readInt();
        this.field_148954_e = packetbuffer.readInt();
        this.field_148951_f = packetbuffer.readByte();
        this.field_148952_g = packetbuffer.readByte();
        this.field_148959_h = packetbuffer.readShort();
        this.field_148958_j = DataWatcher.func_151508_b(packetbuffer);
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.func_150787_b(this.field_148957_a);
        UUID uuid = this.field_148955_b.getId();

        packetbuffer.func_150785_a(uuid == null ? "" : uuid.toString());
        packetbuffer.func_150785_a(this.field_148955_b.getName());
        packetbuffer.func_150787_b(this.field_148955_b.getProperties().size());
        Iterator iterator = this.field_148955_b.getProperties().values().iterator();

        while (iterator.hasNext()) {
            Property property = (Property) iterator.next();

            packetbuffer.func_150785_a(property.getName());
            packetbuffer.func_150785_a(property.getValue());
            packetbuffer.func_150785_a(property.getSignature());
        }

        packetbuffer.writeInt(this.field_148956_c);
        packetbuffer.writeInt(this.field_148953_d);
        packetbuffer.writeInt(this.field_148954_e);
        packetbuffer.writeByte(this.field_148951_f);
        packetbuffer.writeByte(this.field_148952_g);
        packetbuffer.writeShort(this.field_148959_h);
        this.field_148960_i.func_151509_a(packetbuffer);
    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147237_a(this);
    }

    @Override
    public String func_148835_b() {
        return String.format("id=%d, gameProfile='%s', x=%.2f, y=%.2f, z=%.2f, carried=%d", this.field_148957_a, this.field_148955_b, (float) this.field_148956_c / 32.0F, (float) this.field_148953_d / 32.0F, (float) this.field_148954_e / 32.0F, this.field_148959_h);
    }
}
