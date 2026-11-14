package net.minecraft.nbt;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.util.StringUtils;

public final class NBTUtil {

    public static GameProfile func_152459_a(NBTTagCompound nbttagcompound) {
        String s = null;
        String s1 = null;

        if (nbttagcompound.func_150297_b("Name", 8)) {
            s = nbttagcompound.func_74779_i("Name");
        }

        if (nbttagcompound.func_150297_b("Id", 8)) {
            s1 = nbttagcompound.func_74779_i("Id");
        }

        if (StringUtils.func_151246_b(s) && StringUtils.func_151246_b(s1)) {
            return null;
        } else {
            UUID uuid;

            try {
                uuid = UUID.fromString(s1);
            } catch (Throwable throwable) {
                uuid = null;
            }

            GameProfile gameprofile = new GameProfile(uuid, s);

            if (nbttagcompound.func_150297_b("Properties", 10)) {
                NBTTagCompound nbttagcompound1 = nbttagcompound.func_74775_l("Properties");
                Iterator iterator = nbttagcompound1.func_150296_c().iterator();

                while (iterator.hasNext()) {
                    String s2 = (String) iterator.next();
                    NBTTagList nbttaglist = nbttagcompound1.func_150295_c(s2, 10);

                    for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
                        NBTTagCompound nbttagcompound2 = nbttaglist.func_150305_b(i);
                        String s3 = nbttagcompound2.func_74779_i("Value");

                        if (nbttagcompound2.func_150297_b("Signature", 8)) {
                            gameprofile.getProperties().put(s2, new Property(s2, s3, nbttagcompound2.func_74779_i("Signature")));
                        } else {
                            gameprofile.getProperties().put(s2, new Property(s2, s3));
                        }
                    }
                }
            }

            return gameprofile;
        }
    }

    public static void func_152460_a(NBTTagCompound nbttagcompound, GameProfile gameprofile) {
        if (!StringUtils.func_151246_b(gameprofile.getName())) {
            nbttagcompound.func_74778_a("Name", gameprofile.getName());
        }

        if (gameprofile.getId() != null) {
            nbttagcompound.func_74778_a("Id", gameprofile.getId().toString());
        }

        if (!gameprofile.getProperties().isEmpty()) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            Iterator iterator = gameprofile.getProperties().keySet().iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();
                NBTTagList nbttaglist = new NBTTagList();

                NBTTagCompound nbttagcompound2;

                for (Iterator iterator1 = gameprofile.getProperties().get(s).iterator(); iterator1.hasNext(); nbttaglist.func_74742_a(nbttagcompound2)) {
                    Property property = (Property) iterator1.next();

                    nbttagcompound2 = new NBTTagCompound();
                    nbttagcompound2.func_74778_a("Value", property.getValue());
                    if (property.hasSignature()) {
                        nbttagcompound2.func_74778_a("Signature", property.getSignature());
                    }
                }

                nbttagcompound1.func_74782_a(s, nbttaglist);
            }

            nbttagcompound.func_74782_a("Properties", nbttagcompound1);
        }

    }
}
