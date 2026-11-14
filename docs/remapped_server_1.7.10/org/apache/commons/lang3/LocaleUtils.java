package org.apache.commons.lang3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LocaleUtils {

    private static final ConcurrentMap<String, List<Locale>> cLanguagesByCountry = new ConcurrentHashMap();
    private static final ConcurrentMap<String, List<Locale>> cCountriesByLanguage = new ConcurrentHashMap();

    public LocaleUtils() {}

    public static Locale toLocale(String s) {
        if (s == null) {
            return null;
        } else if (s.isEmpty()) {
            return new Locale("", "");
        } else if (s.contains("#")) {
            throw new IllegalArgumentException("Invalid locale format: " + s);
        } else {
            int i = s.length();

            if (i < 2) {
                throw new IllegalArgumentException("Invalid locale format: " + s);
            } else {
                char c0 = s.charAt(0);
                char c1;
                char c2;

                if (c0 == '_') {
                    if (i < 3) {
                        throw new IllegalArgumentException("Invalid locale format: " + s);
                    } else {
                        c1 = s.charAt(1);
                        c2 = s.charAt(2);
                        if (Character.isUpperCase(c1) && Character.isUpperCase(c2)) {
                            if (i == 3) {
                                return new Locale("", s.substring(1, 3));
                            } else if (i < 5) {
                                throw new IllegalArgumentException("Invalid locale format: " + s);
                            } else if (s.charAt(3) != '_') {
                                throw new IllegalArgumentException("Invalid locale format: " + s);
                            } else {
                                return new Locale("", s.substring(1, 3), s.substring(4));
                            }
                        } else {
                            throw new IllegalArgumentException("Invalid locale format: " + s);
                        }
                    }
                } else {
                    c1 = s.charAt(1);
                    if (Character.isLowerCase(c0) && Character.isLowerCase(c1)) {
                        if (i == 2) {
                            return new Locale(s);
                        } else if (i < 5) {
                            throw new IllegalArgumentException("Invalid locale format: " + s);
                        } else if (s.charAt(2) != '_') {
                            throw new IllegalArgumentException("Invalid locale format: " + s);
                        } else {
                            c2 = s.charAt(3);
                            if (c2 == '_') {
                                return new Locale(s.substring(0, 2), "", s.substring(4));
                            } else {
                                char c3 = s.charAt(4);

                                if (Character.isUpperCase(c2) && Character.isUpperCase(c3)) {
                                    if (i == 5) {
                                        return new Locale(s.substring(0, 2), s.substring(3, 5));
                                    } else if (i < 7) {
                                        throw new IllegalArgumentException("Invalid locale format: " + s);
                                    } else if (s.charAt(5) != '_') {
                                        throw new IllegalArgumentException("Invalid locale format: " + s);
                                    } else {
                                        return new Locale(s.substring(0, 2), s.substring(3, 5), s.substring(6));
                                    }
                                } else {
                                    throw new IllegalArgumentException("Invalid locale format: " + s);
                                }
                            }
                        }
                    } else {
                        throw new IllegalArgumentException("Invalid locale format: " + s);
                    }
                }
            }
        }
    }

    public static List<Locale> localeLookupList(Locale locale) {
        return localeLookupList(locale, locale);
    }

    public static List<Locale> localeLookupList(Locale locale, Locale locale1) {
        ArrayList arraylist = new ArrayList(4);

        if (locale != null) {
            arraylist.add(locale);
            if (locale.getVariant().length() > 0) {
                arraylist.add(new Locale(locale.getLanguage(), locale.getCountry()));
            }

            if (locale.getCountry().length() > 0) {
                arraylist.add(new Locale(locale.getLanguage(), ""));
            }

            if (!arraylist.contains(locale1)) {
                arraylist.add(locale1);
            }
        }

        return Collections.unmodifiableList(arraylist);
    }

    public static List<Locale> availableLocaleList() {
        return LocaleUtils.SyncAvoid.AVAILABLE_LOCALE_LIST;
    }

    public static Set<Locale> availableLocaleSet() {
        return LocaleUtils.SyncAvoid.AVAILABLE_LOCALE_SET;
    }

    public static boolean isAvailableLocale(Locale locale) {
        return availableLocaleList().contains(locale);
    }

    public static List<Locale> languagesByCountry(String s) {
        if (s == null) {
            return Collections.emptyList();
        } else {
            List list = (List) LocaleUtils.cLanguagesByCountry.get(s);

            if (list == null) {
                ArrayList arraylist = new ArrayList();
                List list1 = availableLocaleList();

                for (int i = 0; i < list1.size(); ++i) {
                    Locale locale = (Locale) list1.get(i);

                    if (s.equals(locale.getCountry()) && locale.getVariant().isEmpty()) {
                        arraylist.add(locale);
                    }
                }

                list = Collections.unmodifiableList(arraylist);
                LocaleUtils.cLanguagesByCountry.putIfAbsent(s, list);
                list = (List) LocaleUtils.cLanguagesByCountry.get(s);
            }

            return list;
        }
    }

    public static List<Locale> countriesByLanguage(String s) {
        if (s == null) {
            return Collections.emptyList();
        } else {
            List list = (List) LocaleUtils.cCountriesByLanguage.get(s);

            if (list == null) {
                ArrayList arraylist = new ArrayList();
                List list1 = availableLocaleList();

                for (int i = 0; i < list1.size(); ++i) {
                    Locale locale = (Locale) list1.get(i);

                    if (s.equals(locale.getLanguage()) && locale.getCountry().length() != 0 && locale.getVariant().isEmpty()) {
                        arraylist.add(locale);
                    }
                }

                list = Collections.unmodifiableList(arraylist);
                LocaleUtils.cCountriesByLanguage.putIfAbsent(s, list);
                list = (List) LocaleUtils.cCountriesByLanguage.get(s);
            }

            return list;
        }
    }

    static class SyncAvoid {

        private static final List<Locale> AVAILABLE_LOCALE_LIST;
        private static final Set<Locale> AVAILABLE_LOCALE_SET;

        SyncAvoid() {}

        static {
            ArrayList arraylist = new ArrayList(Arrays.asList(Locale.getAvailableLocales()));

            AVAILABLE_LOCALE_LIST = Collections.unmodifiableList(arraylist);
            AVAILABLE_LOCALE_SET = Collections.unmodifiableSet(new HashSet(arraylist));
        }
    }
}
