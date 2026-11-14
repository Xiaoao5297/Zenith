package net.minecraft.server.gui;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

class MinecraftServerGui$5 implements Runnable {

    MinecraftServerGui$5(MinecraftServerGui minecraftservergui, JTextArea jtextarea, JScrollPane jscrollpane, String s) {
        this.field_164253_d = minecraftservergui;
        this.field_164256_a = jtextarea;
        this.field_164254_b = jscrollpane;
        this.field_164255_c = s;
    }

    public void run() {
        this.field_164253_d.func_164247_a(this.field_164256_a, this.field_164254_b, this.field_164255_c);
    }
}
