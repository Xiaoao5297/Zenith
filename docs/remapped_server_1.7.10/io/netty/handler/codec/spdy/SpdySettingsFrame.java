package io.netty.handler.codec.spdy;

import java.util.Set;

public interface SpdySettingsFrame extends SpdyFrame {

    int SETTINGS_UPLOAD_BANDWIDTH = 1;
    int SETTINGS_DOWNLOAD_BANDWIDTH = 2;
    int SETTINGS_ROUND_TRIP_TIME = 3;
    int SETTINGS_MAX_CONCURRENT_STREAMS = 4;
    int SETTINGS_CURRENT_CWND = 5;
    int SETTINGS_DOWNLOAD_RETRANS_RATE = 6;
    int SETTINGS_INITIAL_WINDOW_SIZE = 7;
    int SETTINGS_CLIENT_CERTIFICATE_VECTOR_SIZE = 8;

    Set<Integer> getIds();

    boolean isSet(int i);

    int getValue(int i);

    SpdySettingsFrame setValue(int i, int j);

    SpdySettingsFrame setValue(int i, int j, boolean flag, boolean flag1);

    SpdySettingsFrame removeValue(int i);

    boolean isPersistValue(int i);

    SpdySettingsFrame setPersistValue(int i, boolean flag);

    boolean isPersisted(int i);

    SpdySettingsFrame setPersisted(int i, boolean flag);

    boolean clearPreviouslyPersistedSettings();

    SpdySettingsFrame setClearPreviouslyPersistedSettings(boolean flag);
}
