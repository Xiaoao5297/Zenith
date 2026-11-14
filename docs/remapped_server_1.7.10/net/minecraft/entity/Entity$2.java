package net.minecraft.entity;

import java.util.concurrent.Callable;

class Entity$2 implements Callable {

    Entity$2(Entity entity) {
        this.field_96564_a = entity;
    }

    public String call() {
        return this.field_96564_a.func_70005_c_();
    }
}
