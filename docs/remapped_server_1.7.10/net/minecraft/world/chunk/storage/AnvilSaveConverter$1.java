package net.minecraft.world.chunk.storage;

import java.io.File;
import java.io.FilenameFilter;

class AnvilSaveConverter$1 implements FilenameFilter {

    AnvilSaveConverter$1(AnvilSaveConverter anvilsaveconverter) {
        this.field_76172_a = anvilsaveconverter;
    }

    public boolean accept(File file, String s) {
        return s.endsWith(".mcr");
    }
}
