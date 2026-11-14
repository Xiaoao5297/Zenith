package net.minecraft.server.management;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import net.minecraft.server.MinecraftServer;

final class PreYggdrasilConverter$3 implements ProfileLookupCallback {

    PreYggdrasilConverter$3(MinecraftServer minecraftserver, UserListOps userlistops) {
        this.field_152737_a = minecraftserver;
        this.field_152738_b = userlistops;
    }

    @Override
    public void onProfileLookupSucceeded(GameProfile gameprofile) {
        this.field_152737_a.func_152358_ax().func_152649_a(gameprofile);
        this.field_152738_b.func_152687_a(new UserListOpsEntry(gameprofile, this.field_152737_a.func_110455_j()));
    }

    @Override
    public void onProfileLookupFailed(GameProfile gameprofile, Exception exception) {
        PreYggdrasilConverter.access$000().warn("Could not lookup oplist entry for " + gameprofile.getName(), (Throwable) exception);
        if (!(exception instanceof ProfileNotFoundException)) {
            throw new PreYggdrasilConverter$ConversionError("Could not request user " + gameprofile.getName() + " from backend systems", exception, (PreYggdrasilConverter$1) null);
        }
    }
}
