package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import java.math.BigInteger;
import java.util.UUID;
import net.minecraft.util.CryptManager;

class NetHandlerLoginServer$1 extends Thread {

    NetHandlerLoginServer$1(NetHandlerLoginServer nethandlerloginserver, String s) {
        super(s);
        this.field_151292_a = nethandlerloginserver;
    }

    public void run() {
        GameProfile gameprofile = NetHandlerLoginServer.access$000(this.field_151292_a);

        try {
            String s = (new BigInteger(CryptManager.func_75895_a(NetHandlerLoginServer.access$100(this.field_151292_a), NetHandlerLoginServer.access$200(this.field_151292_a).func_71250_E().getPublic(), NetHandlerLoginServer.access$300(this.field_151292_a)))).toString(16);

            NetHandlerLoginServer.access$002(this.field_151292_a, NetHandlerLoginServer.access$200(this.field_151292_a).func_147130_as().hasJoinedServer(new GameProfile((UUID) null, gameprofile.getName()), s));
            if (NetHandlerLoginServer.access$000(this.field_151292_a) != null) {
                NetHandlerLoginServer.access$400().info("UUID of player " + NetHandlerLoginServer.access$000(this.field_151292_a).getName() + " is " + NetHandlerLoginServer.access$000(this.field_151292_a).getId());
                NetHandlerLoginServer.access$502(this.field_151292_a, NetHandlerLoginServer$LoginState.READY_TO_ACCEPT);
            } else if (NetHandlerLoginServer.access$200(this.field_151292_a).func_71264_H()) {
                NetHandlerLoginServer.access$400().warn("Failed to verify username but will let them in anyway!");
                NetHandlerLoginServer.access$002(this.field_151292_a, this.field_151292_a.func_152506_a(gameprofile));
                NetHandlerLoginServer.access$502(this.field_151292_a, NetHandlerLoginServer$LoginState.READY_TO_ACCEPT);
            } else {
                this.field_151292_a.func_147322_a("Failed to verify username!");
                NetHandlerLoginServer.access$400().error("Username '" + NetHandlerLoginServer.access$000(this.field_151292_a).getName() + "' tried to join with an invalid session");
            }
        } catch (AuthenticationUnavailableException authenticationunavailableexception) {
            if (NetHandlerLoginServer.access$200(this.field_151292_a).func_71264_H()) {
                NetHandlerLoginServer.access$400().warn("Authentication servers are down but will let them in anyway!");
                NetHandlerLoginServer.access$002(this.field_151292_a, this.field_151292_a.func_152506_a(gameprofile));
                NetHandlerLoginServer.access$502(this.field_151292_a, NetHandlerLoginServer$LoginState.READY_TO_ACCEPT);
            } else {
                this.field_151292_a.func_147322_a("Authentication servers are down. Please try again later, sorry!");
                NetHandlerLoginServer.access$400().error("Couldn't verify username because servers are unavailable");
            }
        }

    }
}
