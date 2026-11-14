package net.minecraft.network;

import net.minecraft.util.IChatComponent;

public class ServerStatusResponse {

    private IChatComponent field_151326_a;
    private ServerStatusResponse$PlayerCountData field_151324_b;
    private ServerStatusResponse$MinecraftProtocolVersionIdentifier field_151325_c;
    private String field_151323_d;

    public ServerStatusResponse() {}

    public IChatComponent func_151317_a() {
        return this.field_151326_a;
    }

    public void func_151315_a(IChatComponent ichatcomponent) {
        this.field_151326_a = ichatcomponent;
    }

    public ServerStatusResponse$PlayerCountData func_151318_b() {
        return this.field_151324_b;
    }

    public void func_151319_a(ServerStatusResponse$PlayerCountData serverstatusresponse$playercountdata) {
        this.field_151324_b = serverstatusresponse$playercountdata;
    }

    public ServerStatusResponse$MinecraftProtocolVersionIdentifier func_151322_c() {
        return this.field_151325_c;
    }

    public void func_151321_a(ServerStatusResponse$MinecraftProtocolVersionIdentifier serverstatusresponse$minecraftprotocolversionidentifier) {
        this.field_151325_c = serverstatusresponse$minecraftprotocolversionidentifier;
    }

    public void func_151320_a(String s) {
        this.field_151323_d = s;
    }

    public String func_151316_d() {
        return this.field_151323_d;
    }
}
