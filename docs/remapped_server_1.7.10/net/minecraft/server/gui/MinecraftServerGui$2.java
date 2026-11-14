package net.minecraft.server.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextField;
import net.minecraft.server.MinecraftServer;

class MinecraftServerGui$2 implements ActionListener {

    MinecraftServerGui$2(MinecraftServerGui minecraftservergui, JTextField jtextfield) {
        this.field_120024_b = minecraftservergui;
        this.field_120025_a = jtextfield;
    }

    public void actionPerformed(ActionEvent actionevent) {
        String s = this.field_120025_a.getText().trim();

        if (s.length() > 0) {
            MinecraftServerGui.access$000(this.field_120024_b).func_71331_a(s, MinecraftServer.func_71276_C());
        }

        this.field_120025_a.setText("");
    }
}
