package net.minecraft.server.management;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import net.minecraft.server.MinecraftServer;

final class PreYggdrasilConverter$4 implements ProfileLookupCallback {

    PreYggdrasilConverter$4(MinecraftServer minecraftserver, UserListWhitelist userlistwhitelist) {
        this.field_152739_a = minecraftserver;
        this.field_152740_b = userlistwhitelist;
    }

    @Override
    public void onProfileLookupSucceeded(GameProfile gameprofile) {
        this.field_152739_a.func_152358_ax().func_152649_a(gameprofile);
        this.field_152740_b.func_152687_a(new UserListWhitelistEntry(gameprofile));
    }

    @Override
    public void onProfileLookupFailed(GameProfile gameprofile, Exception exception) {
        PreYggdrasilConverter.access$000().warn("Could not lookup user whitelist entry for " + gameprofile.getName(), (Throwable) exception);
        if (!(exception instanceof ProfileNotFoundException)) {
            throw new PreYggdrasilConverter$ConversionError("Could not request user " + gameprofile.getName() + " from backend systems", exception, (PreYggdrasilConverter$1) null);
        }
    }
}
