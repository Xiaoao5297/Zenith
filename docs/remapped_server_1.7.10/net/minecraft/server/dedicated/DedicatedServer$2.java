package net.minecraft.server.dedicated;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class DedicatedServer$2 extends Thread {

    DedicatedServer$2(DedicatedServer dedicatedserver, String s) {
        super(s);
        this.field_72428_a = dedicatedserver;
    }

    public void run() {
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(System.in));

        String s;

        try {
            while (!this.field_72428_a.func_71241_aa() && this.field_72428_a.func_71278_l() && (s = bufferedreader.readLine()) != null) {
                this.field_72428_a.func_71331_a(s, this.field_72428_a);
            }
        } catch (IOException ioexception) {
            DedicatedServer.access$000().error("Exception handling console input", (Throwable) ioexception);
        }

    }
}
