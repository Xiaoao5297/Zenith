package org.apache.logging.log4j.core.pattern;

import java.util.EnumMap;
import java.util.Locale;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(name = "LevelPatternConverter", category = "Converter")
@ConverterKeys({"p", "level"})
public final class LevelPatternConverter extends LogEventPatternConverter {

    private static final String OPTION_LENGTH = "length";
    private static final String OPTION_LOWER = "lowerCase";
    private static final LevelPatternConverter INSTANCE = new LevelPatternConverter((EnumMap) null);
    private final EnumMap<Level, String> levelMap;

    private LevelPatternConverter(EnumMap<Level, String> enummap) {
        super("Level", "level");
        this.levelMap = enummap;
    }

    public static LevelPatternConverter newInstance(String[] astring) {
        if (astring != null && astring.length != 0) {
            EnumMap enummap = new EnumMap(Level.class);
            int i = Integer.MAX_VALUE;
            boolean flag = false;
            String[] astring1 = astring[0].split(",");
            String[] astring2 = astring1;
            int j = astring1.length;

            int k;

            for (k = 0; k < j; ++k) {
                String s = astring2[k];
                String[] astring3 = s.split("=");

                if (astring3 != null && astring3.length == 2) {
                    String s1 = astring3[0].trim();
                    String s2 = astring3[1].trim();

                    if ("length".equalsIgnoreCase(s1)) {
                        i = Integer.parseInt(s2);
                    } else if ("lowerCase".equalsIgnoreCase(s1)) {
                        flag = Boolean.parseBoolean(s2);
                    } else {
                        Level level = Level.toLevel(s1, (Level) null);

                        if (level == null) {
                            LevelPatternConverter.LOGGER.error("Invalid Level {}", s1);
                        } else {
                            enummap.put(level, s2);
                        }
                    }
                } else {
                    LevelPatternConverter.LOGGER.error("Invalid option {}", s);
                }
            }

            if (enummap.size() == 0 && i == Integer.MAX_VALUE && !flag) {
                return LevelPatternConverter.INSTANCE;
            } else {
                Level[] alevel = Level.values();

                j = alevel.length;

                for (k = 0; k < j; ++k) {
                    Level level1 = alevel[k];

                    if (!enummap.containsKey(level1)) {
                        String s3 = left(level1, i);

                        enummap.put(level1, flag ? s3.toLowerCase(Locale.US) : s3);
                    }
                }

                return new LevelPatternConverter(enummap);
            }
        } else {
            return LevelPatternConverter.INSTANCE;
        }
    }

    private static String left(Level level, int i) {
        String s = level.toString();

        return i >= s.length() ? s : s.substring(0, i);
    }

    @Override
    public void format(LogEvent logevent, StringBuilder stringbuilder) {
        stringbuilder.append(this.levelMap == null ? logevent.getLevel().toString() : (String) this.levelMap.get(logevent.getLevel()));
    }

    @Override
    public String getStyleClass(Object object) {
        if (object instanceof LogEvent) {
            Level level = ((LogEvent) object).getLevel();

            switch (level) {
                case TRACE:
                    return "level trace";
                case DEBUG:
                    return "level debug";
                case INFO:
                    return "level info";
                case WARN:
                    return "level warn";
                case ERROR:
                    return "level error";
                case FATAL:
                    return "level fatal";
                default:
                    return "level " + ((LogEvent) object).getLevel().toString();
            }
        } else {
            return "level";
        }
    }
}
