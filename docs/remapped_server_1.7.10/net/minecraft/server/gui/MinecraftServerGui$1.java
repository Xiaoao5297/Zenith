package net.minecraft.server.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import net.minecraft.server.dedicated.DedicatedServer;

final class MinecraftServerGui$1 extends WindowAdapter {

    MinecraftServerGui$1(DedicatedServer dedicatedserver) {
        this.field_120023_a = dedicatedserver;
    }

    public void windowClosing(WindowEvent windowevent) {
        this.field_120023_a.func_71263_m();

        while (!this.field_120023_a.func_71241_aa()) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException interruptedexception) {
                interruptedexception.printStackTrace();
            }
        }

        System.exit(0);
    }
}
