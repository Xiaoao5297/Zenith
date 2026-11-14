package com.mojang.authlib.minecraft;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MinecraftProfileTexture {

    private final String url;

    public MinecraftProfileTexture(String s) {
        this.url = s;
    }

    public String getUrl() {
        return this.url;
    }

    public String getHash() {
        return FilenameUtils.getBaseName(this.url);
    }

    public String toString() {
        return (new ToStringBuilder(this)).append("url", (Object) this.url).append("hash", (Object) this.getHash()).toString();
    }

    public static enum Type {

        SKIN, CAPE;

        private Type() {}
    }
}
