package net.minecraft.server.management;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;

final class PlayerProfileCache$1 implements ProfileLookupCallback {

    PlayerProfileCache$1(GameProfile[] agameprofile) {
        this.field_152667_a = agameprofile;
    }

    @Override
    public void onProfileLookupSucceeded(GameProfile gameprofile) {
        this.field_152667_a[0] = gameprofile;
    }

    @Override
    public void onProfileLookupFailed(GameProfile gameprofile, Exception exception) {
        this.field_152667_a[0] = null;
    }
}
