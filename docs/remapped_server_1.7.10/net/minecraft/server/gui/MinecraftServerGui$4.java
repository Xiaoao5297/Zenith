package net.minecraft.server.gui;

import com.mojang.util.QueueLogAppender;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

class MinecraftServerGui$4 implements Runnable {

    MinecraftServerGui$4(MinecraftServerGui minecraftservergui, JTextArea jtextarea, JScrollPane jscrollpane) {
        this.field_164251_c = minecraftservergui;
        this.field_164252_a = jtextarea;
        this.field_164250_b = jscrollpane;
    }

    public void run() {
        String s;

        while ((s = QueueLogAppender.getNextLogEvent("ServerGuiConsole")) != null) {
            this.field_164251_c.func_164247_a(this.field_164252_a, this.field_164250_b, s);
        }

    }
}
