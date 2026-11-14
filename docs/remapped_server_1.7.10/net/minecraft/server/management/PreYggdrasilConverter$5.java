package net.minecraft.server.management;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import java.util.List;
import net.minecraft.server.MinecraftServer;

final class PreYggdrasilConverter$5 implements ProfileLookupCallback {

    PreYggdrasilConverter$5(MinecraftServer minecraftserver, List list) {
        this.field_152741_a = minecraftserver;
        this.field_152742_b = list;
    }

    @Override
    public void onProfileLookupSucceeded(GameProfile gameprofile) {
        this.field_152741_a.func_152358_ax().func_152649_a(gameprofile);
        this.field_152742_b.add(gameprofile);
    }

    @Override
    public void onProfileLookupFailed(GameProfile gameprofile, Exception exception) {
        PreYggdrasilConverter.access$000().warn("Could not lookup user whitelist entry for " + gameprofile.getName(), (Throwable) exception);
    }
}
