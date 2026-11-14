package net.minecraft.server.network;

import net.minecraft.network.EnumConnectionState;

class NetHandlerHandshakeTCP$SwitchEnumConnectionState {

    static {
        try {
            NetHandlerHandshakeTCP$SwitchEnumConnectionState.field_151291_a[EnumConnectionState.LOGIN.ordinal()] = 1;
        } catch (NoSuchFieldError nosuchfielderror) {
            ;
        }

        try {
            NetHandlerHandshakeTCP$SwitchEnumConnectionState.field_151291_a[EnumConnectionState.STATUS.ordinal()] = 2;
        } catch (NoSuchFieldError nosuchfielderror1) {
            ;
        }

    }
}
