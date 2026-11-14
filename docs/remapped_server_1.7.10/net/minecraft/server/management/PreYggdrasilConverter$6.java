package net.minecraft.server.management;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import java.io.File;
import java.util.UUID;
import net.minecraft.server.dedicated.DedicatedServer;

final class PreYggdrasilConverter$6 implements ProfileLookupCallback {

    PreYggdrasilConverter$6(DedicatedServer dedicatedserver, File file, File file1, File file2, String[] astring) {
        this.field_152745_a = dedicatedserver;
        this.field_152746_b = file;
        this.field_152747_c = file1;
        this.field_152748_d = file2;
        this.field_152749_e = astring;
    }

    @Override
    public void onProfileLookupSucceeded(GameProfile gameprofile) {
        this.field_152745_a.func_152358_ax().func_152649_a(gameprofile);
        UUID uuid = gameprofile.getId();

        if (uuid == null) {
            throw new PreYggdrasilConverter$ConversionError("Missing UUID for user profile " + gameprofile.getName(), (PreYggdrasilConverter$1) null);
        } else {
            this.func_152743_a(this.field_152746_b, this.func_152744_a(gameprofile), uuid.toString());
        }
    }

    @Override
    public void onProfileLookupFailed(GameProfile gameprofile, Exception exception) {
        PreYggdrasilConverter.access$000().warn("Could not lookup user uuid for " + gameprofile.getName(), (Throwable) exception);
        if (exception instanceof ProfileNotFoundException) {
            String s = this.func_152744_a(gameprofile);

            this.func_152743_a(this.field_152747_c, s, s);
        } else {
            throw new PreYggdrasilConverter$ConversionError("Could not request user " + gameprofile.getName() + " from backend systems", exception, (PreYggdrasilConverter$1) null);
        }
    }

    private void func_152743_a(File file, String s, String s1) {
        File file1 = new File(this.field_152748_d, s + ".dat");
        File file2 = new File(file, s1 + ".dat");

        PreYggdrasilConverter.access$400(file);
        if (!file1.renameTo(file2)) {
            throw new PreYggdrasilConverter$ConversionError("Could not convert file for " + s, (PreYggdrasilConverter$1) null);
        }
    }

    private String func_152744_a(GameProfile gameprofile) {
        String s = null;

        for (int i = 0; i < this.field_152749_e.length; ++i) {
            if (this.field_152749_e[i] != null && this.field_152749_e[i].equalsIgnoreCase(gameprofile.getName())) {
                s = this.field_152749_e[i];
                break;
            }
        }

        if (s == null) {
            throw new PreYggdrasilConverter$ConversionError("Could not find the filename for " + gameprofile.getName() + " anymore", (PreYggdrasilConverter$1) null);
        } else {
            return s;
        }
    }
}
