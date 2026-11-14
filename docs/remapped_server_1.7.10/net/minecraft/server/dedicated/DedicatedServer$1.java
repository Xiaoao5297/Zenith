package net.minecraft.server.dedicated;

class DedicatedServer$1 extends Thread {

    DedicatedServer$1(DedicatedServer dedicatedserver, String s) {
        super(s);
        this.field_72451_a = dedicatedserver;
        this.setDaemon(true);
        this.start();
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(2147483647L);
            } catch (InterruptedException interruptedexception) {
                ;
            }
        }
    }
}
