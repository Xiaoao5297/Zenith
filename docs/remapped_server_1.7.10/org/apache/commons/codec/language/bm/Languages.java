package org.apache.commons.codec.language.bm;

import java.io.InputStream;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

public class Languages {

    public static final String ANY = "any";
    private static final Map<NameType, Languages> LANGUAGES = new EnumMap(NameType.class);
    private final Set<String> languages;
    public static final Languages.LanguageSet NO_LANGUAGES;
    public static final Languages.LanguageSet ANY_LANGUAGE;

    public static Languages getInstance(NameType nametype) {
        return (Languages) Languages.LANGUAGES.get(nametype);
    }

    public static Languages getInstance(String s) {
        HashSet hashset = new HashSet();
        InputStream inputstream = Languages.class.getClassLoader().getResourceAsStream(s);

        if (inputstream == null) {
            throw new IllegalArgumentException("Unable to resolve required resource: " + s);
        } else {
            Scanner scanner = new Scanner(inputstream, "UTF-8");

            try {
                boolean flag = false;

                while (scanner.hasNextLine()) {
                    String s1 = scanner.nextLine().trim();

                    if (flag) {
                        if (s1.endsWith("*/")) {
                            flag = false;
                        }
                    } else if (s1.startsWith("/*")) {
                        flag = true;
                    } else if (s1.length() > 0) {
                        hashset.add(s1);
                    }
                }
            } finally {
                scanner.close();
            }

            return new Languages(Collections.unmodifiableSet(hashset));
        }
    }

    private static String langResourceName(NameType nametype) {
        return String.format("org/apache/commons/codec/language/bm/%s_languages.txt", nametype.getName());
    }

    private Languages(Set<String> set) {
        this.languages = set;
    }

    public Set<String> getLanguages() {
        return this.languages;
    }

    static {
        NameType[] anametype = NameType.values();
        int i = anametype.length;

        for (int j = 0; j < i; ++j) {
            NameType nametype = anametype[j];

            Languages.LANGUAGES.put(nametype, getInstance(langResourceName(nametype)));
        }

        NO_LANGUAGES = new Languages.LanguageSet() {
            @Override
            public boolean contains(String s) {
                return false;
            }

            @Override
            public String getAny() {
                throw new NoSuchElementException("Can't fetch any language from the empty language set.");
            }

            @Override
            public boolean isEmpty() {
                return true;
            }

            @Override
            public boolean isSingleton() {
                return false;
            }

            @Override
            public Languages.LanguageSet restrictTo(Languages.LanguageSet languages_languageset) {
                return this;
            }

            public String toString() {
                return "NO_LANGUAGES";
            }
        };
        ANY_LANGUAGE = new Languages.LanguageSet() {
            @Override
            public boolean contains(String s) {
                return true;
            }

            @Override
            public String getAny() {
                throw new NoSuchElementException("Can't fetch any language from the any language set.");
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean isSingleton() {
                return false;
            }

            @Override
            public Languages.LanguageSet restrictTo(Languages.LanguageSet languages_languageset) {
                return languages_languageset;
            }

            public String toString() {
                return "ANY_LANGUAGE";
            }
        };
    }

    public static final class SomeLanguages extends Languages.LanguageSet {

        private final Set<String> languages;

        private SomeLanguages(Set<String> set) {
            this.languages = Collections.unmodifiableSet(set);
        }

        @Override
        public boolean contains(String s) {
            return this.languages.contains(s);
        }

        @Override
        public String getAny() {
            return (String) this.languages.iterator().next();
        }

        public Set<String> getLanguages() {
            return this.languages;
        }

        @Override
        public boolean isEmpty() {
            return this.languages.isEmpty();
        }

        @Override
        public boolean isSingleton() {
            return this.languages.size() == 1;
        }

        @Override
        public Languages.LanguageSet restrictTo(Languages.LanguageSet languages_languageset) {
            if (languages_languageset == Languages.NO_LANGUAGES) {
                return languages_languageset;
            } else if (languages_languageset == Languages.ANY_LANGUAGE) {
                return this;
            } else {
                Languages.SomeLanguages languages_somelanguages = (Languages.SomeLanguages) languages_languageset;
                HashSet hashset = new HashSet(Math.min(this.languages.size(), languages_somelanguages.languages.size()));
                Iterator iterator = this.languages.iterator();

                while (iterator.hasNext()) {
                    String s = (String) iterator.next();

                    if (languages_somelanguages.languages.contains(s)) {
                        hashset.add(s);
                    }
                }

                return from(hashset);
            }
        }

        public String toString() {
            return "Languages(" + this.languages.toString() + ")";
        }
    }

    public abstract static class LanguageSet {

        public LanguageSet() {}

        public static Languages.LanguageSet from(Set<String> set) {
            return (Languages.LanguageSet) (set.isEmpty() ? Languages.NO_LANGUAGES : new Languages.SomeLanguages(set));
        }

        public abstract boolean contains(String s);

        public abstract String getAny();

        public abstract boolean isEmpty();

        public abstract boolean isSingleton();

        public abstract Languages.LanguageSet restrictTo(Languages.LanguageSet languages_languageset);
    }
}
