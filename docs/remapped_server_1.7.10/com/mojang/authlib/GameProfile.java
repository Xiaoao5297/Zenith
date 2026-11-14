package com.mojang.authlib;

import com.mojang.authlib.properties.PropertyMap;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GameProfile {

    private final UUID id;
    private final String name;
    private final PropertyMap properties = new PropertyMap();
    private boolean legacy;

    public GameProfile(UUID uuid, String s) {
        if (uuid == null && StringUtils.isBlank(s)) {
            throw new IllegalArgumentException("Name and ID cannot both be blank");
        } else {
            this.id = uuid;
            this.name = s;
        }
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public PropertyMap getProperties() {
        return this.properties;
    }

    public boolean isComplete() {
        return this.id != null && StringUtils.isNotBlank(this.getName());
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            GameProfile gameprofile;
            label33:
            {
                gameprofile = (GameProfile) object;
                if (this.id != null) {
                    if (this.id.equals(gameprofile.id)) {
                        break label33;
                    }
                } else if (gameprofile.id == null) {
                    break label33;
                }

                return false;
            }

            if (this.name != null) {
                if (this.name.equals(gameprofile.name)) {
                    return true;
                }
            } else if (gameprofile.name == null) {
                return true;
            }

            return false;
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = this.id != null ? this.id.hashCode() : 0;

        i = 31 * i + (this.name != null ? this.name.hashCode() : 0);
        return i;
    }

    public String toString() {
        return (new ToStringBuilder(this)).append("id", (Object) this.id).append("name", (Object) this.name).append("properties", (Object) this.properties).append("legacy", this.legacy).toString();
    }

    public boolean isLegacy() {
        return this.legacy;
    }
}
