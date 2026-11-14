package com.mojang.authlib.minecraft;

import com.mojang.authlib.GameProfile;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class InsecureTextureException extends RuntimeException {

    public InsecureTextureException(String s) {
        super(s);
    }

    public static class MissingTextureException extends InsecureTextureException {

        public MissingTextureException() {
            super("No texture information found");
        }
    }

    public static class WrongTextureOwnerException extends InsecureTextureException {

        private final GameProfile expected;
        private final UUID resultId;
        private final String resultName;

        public WrongTextureOwnerException(GameProfile gameprofile, UUID uuid, String s) {
            super("Decrypted textures payload was for another user (expected " + gameprofile.getId() + "/" + gameprofile.getName() + " but was for " + uuid + "/" + s + ")");
            this.expected = gameprofile;
            this.resultId = uuid;
            this.resultName = s;
        }
    }

    public static class OutdatedTextureException extends InsecureTextureException {

        private final Date validFrom;
        private final Calendar limit;

        public OutdatedTextureException(Date date, Calendar calendar) {
            super("Decrypted textures payload is too old (" + date + ", but we need it to be at least " + calendar + ")");
            this.validFrom = date;
            this.limit = calendar;
        }
    }
}
