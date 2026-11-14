package io.netty.handler.codec.spdy;

import io.netty.util.internal.StringUtil;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class DefaultSpdySettingsFrame implements SpdySettingsFrame {

    private boolean clear;
    private final Map<Integer, DefaultSpdySettingsFrame.Setting> settingsMap = new TreeMap();

    public DefaultSpdySettingsFrame() {}

    @Override
    public Set<Integer> getIds() {
        return this.settingsMap.keySet();
    }

    @Override
    public boolean isSet(int i) {
        Integer integer = i;

        return this.settingsMap.containsKey(integer);
    }

    @Override
    public int getValue(int i) {
        Integer integer = i;

        return this.settingsMap.containsKey(integer) ? ((DefaultSpdySettingsFrame.Setting) this.settingsMap.get(integer)).getValue() : -1;
    }

    @Override
    public SpdySettingsFrame setValue(int i, int j) {
        return this.setValue(i, j, false, false);
    }

    @Override
    public SpdySettingsFrame setValue(int i, int j, boolean flag, boolean flag1) {
        if (i > 0 && i <= 16777215) {
            Integer integer = i;

            if (this.settingsMap.containsKey(integer)) {
                DefaultSpdySettingsFrame.Setting defaultspdysettingsframe_setting = (DefaultSpdySettingsFrame.Setting) this.settingsMap.get(integer);

                defaultspdysettingsframe_setting.setValue(j);
                defaultspdysettingsframe_setting.setPersist(flag);
                defaultspdysettingsframe_setting.setPersisted(flag1);
            } else {
                this.settingsMap.put(integer, new DefaultSpdySettingsFrame.Setting(j, flag, flag1));
            }

            return this;
        } else {
            throw new IllegalArgumentException("Setting ID is not valid: " + i);
        }
    }

    @Override
    public SpdySettingsFrame removeValue(int i) {
        Integer integer = i;

        if (this.settingsMap.containsKey(integer)) {
            this.settingsMap.remove(integer);
        }

        return this;
    }

    @Override
    public boolean isPersistValue(int i) {
        Integer integer = i;

        return this.settingsMap.containsKey(integer) ? ((DefaultSpdySettingsFrame.Setting) this.settingsMap.get(integer)).isPersist() : false;
    }

    @Override
    public SpdySettingsFrame setPersistValue(int i, boolean flag) {
        Integer integer = i;

        if (this.settingsMap.containsKey(integer)) {
            ((DefaultSpdySettingsFrame.Setting) this.settingsMap.get(integer)).setPersist(flag);
        }

        return this;
    }

    @Override
    public boolean isPersisted(int i) {
        Integer integer = i;

        return this.settingsMap.containsKey(integer) ? ((DefaultSpdySettingsFrame.Setting) this.settingsMap.get(integer)).isPersisted() : false;
    }

    @Override
    public SpdySettingsFrame setPersisted(int i, boolean flag) {
        Integer integer = i;

        if (this.settingsMap.containsKey(integer)) {
            ((DefaultSpdySettingsFrame.Setting) this.settingsMap.get(integer)).setPersisted(flag);
        }

        return this;
    }

    @Override
    public boolean clearPreviouslyPersistedSettings() {
        return this.clear;
    }

    @Override
    public SpdySettingsFrame setClearPreviouslyPersistedSettings(boolean flag) {
        this.clear = flag;
        return this;
    }

    private Set<Entry<Integer, DefaultSpdySettingsFrame.Setting>> getSettings() {
        return this.settingsMap.entrySet();
    }

    private void appendSettings(StringBuilder stringbuilder) {
        Iterator iterator = this.getSettings().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            DefaultSpdySettingsFrame.Setting defaultspdysettingsframe_setting = (DefaultSpdySettingsFrame.Setting) entry.getValue();

            stringbuilder.append("--> ");
            stringbuilder.append(((Integer) entry.getKey()).toString());
            stringbuilder.append(':');
            stringbuilder.append(defaultspdysettingsframe_setting.getValue());
            stringbuilder.append(" (persist value: ");
            stringbuilder.append(defaultspdysettingsframe_setting.isPersist());
            stringbuilder.append("; persisted: ");
            stringbuilder.append(defaultspdysettingsframe_setting.isPersisted());
            stringbuilder.append(')');
            stringbuilder.append(StringUtil.NEWLINE);
        }

    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append(this.getClass().getSimpleName());
        stringbuilder.append(StringUtil.NEWLINE);
        this.appendSettings(stringbuilder);
        stringbuilder.setLength(stringbuilder.length() - StringUtil.NEWLINE.length());
        return stringbuilder.toString();
    }

    private static final class Setting {

        private int value;
        private boolean persist;
        private boolean persisted;

        Setting(int i, boolean flag, boolean flag1) {
            this.value = i;
            this.persist = flag;
            this.persisted = flag1;
        }

        int getValue() {
            return this.value;
        }

        void setValue(int i) {
            this.value = i;
        }

        boolean isPersist() {
            return this.persist;
        }

        void setPersist(boolean flag) {
            this.persist = flag;
        }

        boolean isPersisted() {
            return this.persisted;
        }

        void setPersisted(boolean flag) {
            this.persisted = flag;
        }
    }
}
