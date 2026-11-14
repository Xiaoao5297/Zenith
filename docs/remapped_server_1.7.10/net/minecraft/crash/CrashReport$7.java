package net.minecraft.crash;

import java.util.concurrent.Callable;

class CrashReport$7 implements Callable {

    CrashReport$7(CrashReport crashreport) {
        this.field_83004_a = crashreport;
    }

    public String call() {
        byte b0 = 0;
        int i = 56 * b0;
        int j = i / 1024 / 1024;
        byte b1 = 0;
        int k = 56 * b1;
        int l = k / 1024 / 1024;

        return b0 + " (" + i + " bytes; " + j + " MB) allocated, " + b1 + " (" + k + " bytes; " + l + " MB) used";
    }
}
