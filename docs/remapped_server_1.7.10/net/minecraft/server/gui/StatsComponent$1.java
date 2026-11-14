package net.minecraft.server.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class StatsComponent$1 implements ActionListener {

    StatsComponent$1(StatsComponent statscomponent) {
        this.field_120030_a = statscomponent;
    }

    public void actionPerformed(ActionEvent actionevent) {
        StatsComponent.access$000(this.field_120030_a);
    }
}
