package net.minecraft.network;

import net.minecraft.network.play.client.C16PacketClientStatus$EnumState;

class NetHandlerPlayServer$SwitchEnumState {

    static {
        try {
            NetHandlerPlayServer$SwitchEnumState.field_151290_a[C16PacketClientStatus$EnumState.PERFORM_RESPAWN.ordinal()] = 1;
        } catch (NoSuchFieldError nosuchfielderror) {
            ;
        }

        try {
            NetHandlerPlayServer$SwitchEnumState.field_151290_a[C16PacketClientStatus$EnumState.REQUEST_STATS.ordinal()] = 2;
        } catch (NoSuchFieldError nosuchfielderror1) {
            ;
        }

        try {
            NetHandlerPlayServer$SwitchEnumState.field_151290_a[C16PacketClientStatus$EnumState.OPEN_INVENTORY_ACHIEVEMENT.ordinal()] = 3;
        } catch (NoSuchFieldError nosuchfielderror2) {
            ;
        }

    }
}
