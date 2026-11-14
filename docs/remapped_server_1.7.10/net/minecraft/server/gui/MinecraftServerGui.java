package net.minecraft.server.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import net.minecraft.server.dedicated.DedicatedServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MinecraftServerGui extends JComponent {

    private static final Font field_164249_a = new Font("Monospaced", 0, 12);
    private static final Logger field_164248_b = LogManager.getLogger();
    private DedicatedServer field_120021_b;

    public static void func_120016_a(DedicatedServer dedicatedserver) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exception) {
            ;
        }

        MinecraftServerGui minecraftservergui = new MinecraftServerGui(dedicatedserver);
        JFrame jframe = new JFrame("Minecraft server");

        jframe.add(minecraftservergui);
        jframe.pack();
        jframe.setLocationRelativeTo((Component) null);
        jframe.setVisible(true);
        jframe.addWindowListener(new MinecraftServerGui$1(dedicatedserver));
    }

    public MinecraftServerGui(DedicatedServer dedicatedserver) {
        this.field_120021_b = dedicatedserver;
        this.setPreferredSize(new Dimension(854, 480));
        this.setLayout(new BorderLayout());

        try {
            this.add(this.func_120018_d(), "Center");
            this.add(this.func_120019_b(), "West");
        } catch (Exception exception) {
            MinecraftServerGui.field_164248_b.error("Couldn't build server GUI", (Throwable) exception);
        }

    }

    private JComponent func_120019_b() {
        JPanel jpanel = new JPanel(new BorderLayout());

        jpanel.add(new StatsComponent(this.field_120021_b), "North");
        jpanel.add(this.func_120020_c(), "Center");
        jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
        return jpanel;
    }

    private JComponent func_120020_c() {
        PlayerListComponent playerlistcomponent = new PlayerListComponent(this.field_120021_b);
        JScrollPane jscrollpane = new JScrollPane(playerlistcomponent, 22, 30);

        jscrollpane.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
        return jscrollpane;
    }

    private JComponent func_120018_d() {
        JPanel jpanel = new JPanel(new BorderLayout());
        JTextArea jtextarea = new JTextArea();
        JScrollPane jscrollpane = new JScrollPane(jtextarea, 22, 30);

        jtextarea.setEditable(false);
        jtextarea.setFont(MinecraftServerGui.field_164249_a);
        JTextField jtextfield = new JTextField();

        jtextfield.addActionListener(new MinecraftServerGui$2(this, jtextfield));
        jtextarea.addFocusListener(new MinecraftServerGui$3(this));
        jpanel.add(jscrollpane, "Center");
        jpanel.add(jtextfield, "South");
        jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
        Thread thread = new Thread(new MinecraftServerGui$4(this, jtextarea, jscrollpane));

        thread.setDaemon(true);
        thread.start();
        return jpanel;
    }

    public void func_164247_a(JTextArea jtextarea, JScrollPane jscrollpane, String s) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new MinecraftServerGui$5(this, jtextarea, jscrollpane, s));
        } else {
            Document document = jtextarea.getDocument();
            JScrollBar jscrollbar = jscrollpane.getVerticalScrollBar();
            boolean flag = false;

            if (jscrollpane.getViewport().getView() == jtextarea) {
                flag = (double) jscrollbar.getValue() + jscrollbar.getSize().getHeight() + (double) (MinecraftServerGui.field_164249_a.getSize() * 4) > (double) jscrollbar.getMaximum();
            }

            try {
                document.insertString(document.getLength(), s, (AttributeSet) null);
            } catch (BadLocationException badlocationexception) {
                ;
            }

            if (flag) {
                jscrollbar.setValue(Integer.MAX_VALUE);
            }

        }
    }
}
