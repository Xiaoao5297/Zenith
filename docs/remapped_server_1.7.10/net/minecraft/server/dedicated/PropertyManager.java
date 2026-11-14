package net.minecraft.server.dedicated;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertyManager {

    private static final Logger field_164440_a = LogManager.getLogger();
    private final Properties field_73672_b = new Properties();
    private final File field_73673_c;

    public PropertyManager(File file) {
        this.field_73673_c = file;
        if (file.exists()) {
            FileInputStream fileinputstream = null;

            try {
                fileinputstream = new FileInputStream(file);
                this.field_73672_b.load(fileinputstream);
            } catch (Exception exception) {
                PropertyManager.field_164440_a.warn("Failed to load " + file, (Throwable) exception);
                this.func_73666_a();
            } finally {
                if (fileinputstream != null) {
                    try {
                        fileinputstream.close();
                    } catch (IOException ioexception) {
                        ;
                    }
                }

            }
        } else {
            PropertyManager.field_164440_a.warn(file + " does not exist");
            this.func_73666_a();
        }

    }

    public void func_73666_a() {
        PropertyManager.field_164440_a.info("Generating new properties file");
        this.func_73668_b();
    }

    public void func_73668_b() {
        FileOutputStream fileoutputstream = null;

        try {
            fileoutputstream = new FileOutputStream(this.field_73673_c);
            this.field_73672_b.store(fileoutputstream, "Minecraft server properties");
        } catch (Exception exception) {
            PropertyManager.field_164440_a.warn("Failed to save " + this.field_73673_c, (Throwable) exception);
            this.func_73666_a();
        } finally {
            if (fileoutputstream != null) {
                try {
                    fileoutputstream.close();
                } catch (IOException ioexception) {
                    ;
                }
            }

        }

    }

    public File func_73665_c() {
        return this.field_73673_c;
    }

    public String func_73671_a(String s, String s1) {
        if (!this.field_73672_b.containsKey(s)) {
            this.field_73672_b.setProperty(s, s1);
            this.func_73668_b();
            this.func_73668_b();
        }

        return this.field_73672_b.getProperty(s, s1);
    }

    public int func_73669_a(String s, int i) {
        try {
            return Integer.parseInt(this.func_73671_a(s, "" + i));
        } catch (Exception exception) {
            this.field_73672_b.setProperty(s, "" + i);
            this.func_73668_b();
            return i;
        }
    }

    public boolean func_73670_a(String s, boolean flag) {
        try {
            return Boolean.parseBoolean(this.func_73671_a(s, "" + flag));
        } catch (Exception exception) {
            this.field_73672_b.setProperty(s, "" + flag);
            this.func_73668_b();
            return flag;
        }
    }

    public void func_73667_a(String s, Object object) {
        this.field_73672_b.setProperty(s, "" + object);
    }
}
