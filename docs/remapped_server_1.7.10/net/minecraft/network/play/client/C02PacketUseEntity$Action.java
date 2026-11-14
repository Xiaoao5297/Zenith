package net.minecraft.network.play.client;

public enum C02PacketUseEntity$Action {

    INTERACT(0), ATTACK(1);

    private static final C02PacketUseEntity$Action[] field_151421_c = new C02PacketUseEntity$Action[values().length];
    private final int field_151418_d;

    private C02PacketUseEntity$Action(int i) {
        this.field_151418_d = i;
    }

    static {
        C02PacketUseEntity$Action[] ac02packetuseentity$action = values();
        int i = ac02packetuseentity$action.length;

        for (int j = 0; j < i; ++j) {
            C02PacketUseEntity$Action c02packetuseentity$action = ac02packetuseentity$action[j];

            C02PacketUseEntity$Action.field_151421_c[c02packetuseentity$action.field_151418_d] = c02packetuseentity$action;
        }

    }
}
