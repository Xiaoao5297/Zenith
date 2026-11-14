package net.minecraft.network.play.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.ScorePlayerTeam;

public class S3EPacketTeams extends Packet {

    private String field_149320_a = "";
    private String field_149318_b = "";
    private String field_149319_c = "";
    private String field_149316_d = "";
    private Collection field_149317_e = new ArrayList();
    private int field_149314_f;
    private int field_149315_g;

    public S3EPacketTeams() {}

    public S3EPacketTeams(ScorePlayerTeam scoreplayerteam, int i) {
        this.field_149320_a = scoreplayerteam.func_96661_b();
        this.field_149314_f = i;
        if (i == 0 || i == 2) {
            this.field_149318_b = scoreplayerteam.func_96669_c();
            this.field_149319_c = scoreplayerteam.func_96668_e();
            this.field_149316_d = scoreplayerteam.func_96663_f();
            this.field_149315_g = scoreplayerteam.func_98299_i();
        }

        if (i == 0) {
            this.field_149317_e.addAll(scoreplayerteam.func_96670_d());
        }

    }

    public S3EPacketTeams(ScorePlayerTeam scoreplayerteam, Collection collection, int i) {
        if (i != 3 && i != 4) {
            throw new IllegalArgumentException("Method must be join or leave for player constructor");
        } else if (collection != null && !collection.isEmpty()) {
            this.field_149314_f = i;
            this.field_149320_a = scoreplayerteam.func_96661_b();
            this.field_149317_e.addAll(collection);
        } else {
            throw new IllegalArgumentException("Players cannot be null/empty");
        }
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149320_a = packetbuffer.func_150789_c(16);
        this.field_149314_f = packetbuffer.readByte();
        if (this.field_149314_f == 0 || this.field_149314_f == 2) {
            this.field_149318_b = packetbuffer.func_150789_c(32);
            this.field_149319_c = packetbuffer.func_150789_c(16);
            this.field_149316_d = packetbuffer.func_150789_c(16);
            this.field_149315_g = packetbuffer.readByte();
        }

        if (this.field_149314_f == 0 || this.field_149314_f == 3 || this.field_149314_f == 4) {
            short short0 = packetbuffer.readShort();

            for (int i = 0; i < short0; ++i) {
                this.field_149317_e.add(packetbuffer.func_150789_c(40));
            }
        }

    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.func_150785_a(this.field_149320_a);
        packetbuffer.writeByte(this.field_149314_f);
        if (this.field_149314_f == 0 || this.field_149314_f == 2) {
            packetbuffer.func_150785_a(this.field_149318_b);
            packetbuffer.func_150785_a(this.field_149319_c);
            packetbuffer.func_150785_a(this.field_149316_d);
            packetbuffer.writeByte(this.field_149315_g);
        }

        if (this.field_149314_f == 0 || this.field_149314_f == 3 || this.field_149314_f == 4) {
            packetbuffer.writeShort(this.field_149317_e.size());
            Iterator iterator = this.field_149317_e.iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();

                packetbuffer.func_150785_a(s);
            }
        }

    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147247_a(this);
    }
}
