package net.minecraft.network.rcon;

import java.net.DatagramPacket;
import java.util.Date;
import java.util.Random;

class RConThreadQuery$Auth {

    private long field_72598_b;
    private int field_72599_c;
    private byte[] field_72596_d;
    private byte[] field_72597_e;
    private String field_72595_f;

    public RConThreadQuery$Auth(RConThreadQuery rconthreadquery, DatagramPacket datagrampacket) {
        this.field_72600_a = rconthreadquery;
        this.field_72598_b = (new Date()).getTime();
        byte[] abyte = datagrampacket.getData();

        this.field_72596_d = new byte[4];
        this.field_72596_d[0] = abyte[3];
        this.field_72596_d[1] = abyte[4];
        this.field_72596_d[2] = abyte[5];
        this.field_72596_d[3] = abyte[6];
        this.field_72595_f = new String(this.field_72596_d);
        this.field_72599_c = (new Random()).nextInt(16777216);
        this.field_72597_e = String.format("\t%s%d\u0000", this.field_72595_f, this.field_72599_c).getBytes();
    }

    public Boolean func_72593_a(long i) {
        return this.field_72598_b < i;
    }

    public int func_72592_a() {
        return this.field_72599_c;
    }

    public byte[] func_72594_b() {
        return this.field_72597_e;
    }

    public byte[] func_72591_c() {
        return this.field_72596_d;
    }
}
