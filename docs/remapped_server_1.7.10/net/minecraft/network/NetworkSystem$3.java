package net.minecraft.network;

import java.util.concurrent.Callable;

class NetworkSystem$3 implements Callable {

    NetworkSystem$3(NetworkSystem networksystem, NetworkManager networkmanager) {
        this.field_151279_b = networksystem;
        this.field_151280_a = networkmanager;
    }

    public String call() {
        return this.field_151280_a.toString();
    }
}
