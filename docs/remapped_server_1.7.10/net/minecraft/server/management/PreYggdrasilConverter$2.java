package net.minecraft.server.management;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import net.minecraft.server.MinecraftServer;

final class PreYggdrasilConverter$2 implements ProfileLookupCallback {

    PreYggdrasilConverter$2(MinecraftServer minecraftserver, Map map, UserListBans userlistbans) {
        this.field_152734_a = minecraftserver;
        this.field_152735_b = map;
        this.field_152736_c = userlistbans;
    }

    @Override
    public void onProfileLookupSucceeded(GameProfile gameprofile) {
        this.field_152734_a.func_152358_ax().func_152649_a(gameprofile);
        String[] astring = (String[]) this.field_152735_b.get(gameprofile.getName().toLowerCase(Locale.ROOT));

        if (astring == null) {
            PreYggdrasilConverter.access$000().warn("Could not convert user banlist entry for " + gameprofile.getName());
            throw new PreYggdrasilConverter$ConversionError("Profile not in the conversionlist", (PreYggdrasilConverter$1) null);
        } else {
            Date date = astring.length > 1 ? PreYggdrasilConverter.access$200(astring[1], (Date) null) : null;
            String s = astring.length > 2 ? astring[2] : null;
            Date date1 = astring.length > 3 ? PreYggdrasilConverter.access$200(astring[3], (Date) null) : null;
            String s1 = astring.length > 4 ? astring[4] : null;

            this.field_152736_c.func_152687_a(new UserListBansEntry(gameprofile, date, s, date1, s1));
        }
    }

    @Override
    public void onProfileLookupFailed(GameProfile gameprofile, Exception exception) {
        PreYggdrasilConverter.access$000().warn("Could not lookup user banlist entry for " + gameprofile.getName(), (Throwable) exception);
        if (!(exception instanceof ProfileNotFoundException)) {
            throw new PreYggdrasilConverter$ConversionError("Could not request user " + gameprofile.getName() + " from backend systems", exception, (PreYggdrasilConverter$1) null);
        }
    }
}
