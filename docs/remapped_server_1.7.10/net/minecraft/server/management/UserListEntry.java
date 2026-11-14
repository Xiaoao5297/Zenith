package net.minecraft.server.management;

import com.google.gson.JsonObject;

public class UserListEntry {

    private final Object field_152642_a;

    public UserListEntry(Object object) {
        this.field_152642_a = object;
    }

    protected UserListEntry(Object object, JsonObject jsonobject) {
        this.field_152642_a = object;
    }

    Object func_152640_f() {
        return this.field_152642_a;
    }

    boolean func_73682_e() {
        return false;
    }

    protected void func_152641_a(JsonObject jsonobject) {}
}
