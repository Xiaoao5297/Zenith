package net.minecraft.server.management;

import java.io.File;
import java.io.FilenameFilter;

final class PreYggdrasilConverter$7 implements FilenameFilter {

    PreYggdrasilConverter$7() {}

    public boolean accept(File file, String s) {
        return s.endsWith(".dat");
    }
}
