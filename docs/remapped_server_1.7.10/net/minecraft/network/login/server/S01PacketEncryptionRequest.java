package net.minecraft.network.login.server;

import java.security.PublicKey;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.CryptManager;

public class S01PacketEncryptionRequest extends Packet {

    private String field_149612_a;
    private PublicKey field_149610_b;
    private byte[] field_149611_c;

    public S01PacketEncryptionRequest() {}

    public S01PacketEncryptionRequest(String s, PublicKey publickey, byte[] abyte) {
        this.field_149612_a = s;
        this.field_149610_b = publickey;
        this.field_149611_c = abyte;
    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149612_a = packetbuffer.func_150789_c(20);
        this.field_149610_b = CryptManager.func_75896_a(func_148834_a(packetbuffer));
        this.field_149611_c = func_148834_a(packetbuffer);
    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.func_150785_a(this.field_149612_a);
        func_148838_a(packetbuffer, this.field_149610_b.getEncoded());
        func_148838_a(packetbuffer, this.field_149611_c);
    }

    public void func_148833_a(INetHandlerLoginClient inethandlerloginclient) {
        inethandlerloginclient.func_147389_a(this);
    }
}
