package net.minecraft.entity;

import java.util.concurrent.Callable;

class EntityTracker$1 implements Callable {

    EntityTracker$1(EntityTracker entitytracker, int i) {
        this.field_96569_b = entitytracker;
        this.field_96570_a = i;
    }

    public String call() {
        String s = "Once per " + this.field_96570_a + " ticks";

        if (this.field_96570_a == Integer.MAX_VALUE) {
            s = "Maximum (" + s + ")";
        }

        return s;
    }
}
