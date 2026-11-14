package net.minecraft.network.play.client;

import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C13PacketPlayerAbilities extends Packet {

    private boolean field_149500_a;
    private boolean field_149498_b;
    private boolean field_149499_c;
    private boolean field_149496_d;
    private float field_149497_e;
    private float field_149495_f;

    public C13PacketPlayerAbilities() {}

    public C13PacketPlayerAbilities(PlayerCapabilities playercapabilities) {
        this.func_149490_a(playercapabilities.field_75102_a);
        this.func_149483_b(playercapabilities.field_75100_b);
        this.func_149491_c(playercapabilities.field_75101_c);
        this.func_149493_d(playercapabilities.field_75098_d);
        this.func_149485_a(playercapabilities.func_75093_a());
        this.func_149492_b(playercapabilities.func_75094_b());
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        byte b0 = packetbuffer.readByte();

        this.func_149490_a((b0 & 1) > 0);
        this.func_149483_b((b0 & 2) > 0);
        this.func_149491_c((b0 & 4) > 0);
        this.func_149493_d((b0 & 8) > 0);
        this.func_149485_a(packetbuffer.readFloat());
        this.func_149492_b(packetbuffer.readFloat());
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        byte b0 = 0;

        if (this.func_149494_c()) {
            b0 = (byte) (b0 | 1);
        }

        if (this.func_149488_d()) {
            b0 = (byte) (b0 | 2);
        }

        if (this.func_149486_e()) {
            b0 = (byte) (b0 | 4);
        }

        if (this.func_149484_f()) {
            b0 = (byte) (b0 | 8);
        }

        packetbuffer.writeByte(b0);
        packetbuffer.writeFloat(this.field_149497_e);
        packetbuffer.writeFloat(this.field_149495_f);
    }

    public void func_148833_a(INetHandlerPlayServer inethandlerplayserver) {
        inethandlerplayserver.func_147348_a(this);
    }

    @Override
    public String func_148835_b() {
        return String.format("invuln=%b, flying=%b, canfly=%b, instabuild=%b, flyspeed=%.4f, walkspped=%.4f", this.func_149494_c(), this.func_149488_d(), this.func_149486_e(), this.func_149484_f(), this.func_149482_g(), this.func_149489_h());
    }

    public boolean func_149494_c() {
        return this.field_149500_a;
    }

    public void func_149490_a(boolean flag) {
        this.field_149500_a = flag;
    }

    public boolean func_149488_d() {
        return this.field_149498_b;
    }

    public void func_149483_b(boolean flag) {
        this.field_149498_b = flag;
    }

    public boolean func_149486_e() {
        return this.field_149499_c;
    }

    public void func_149491_c(boolean flag) {
        this.field_149499_c = flag;
    }

    public boolean func_149484_f() {
        return this.field_149496_d;
    }

    public void func_149493_d(boolean flag) {
        this.field_149496_d = flag;
    }

    public float func_149482_g() {
        return this.field_149497_e;
    }

    public void func_149485_a(float f) {
        this.field_149497_e = f;
    }

    public float func_149489_h() {
        return this.field_149495_f;
    }

    public void func_149492_b(float f) {
        this.field_149495_f = f;
    }
}
