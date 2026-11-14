package net.minecraft.network;

public class ServerStatusResponse$MinecraftProtocolVersionIdentifier {

    private final String field_151306_a;
    private final int field_151305_b;

    public ServerStatusResponse$MinecraftProtocolVersionIdentifier(String s, int i) {
        this.field_151306_a = s;
        this.field_151305_b = i;
    }

    public String func_151303_a() {
        return this.field_151306_a;
    }

    public int func_151304_b() {
        return this.field_151305_b;
    }
}
