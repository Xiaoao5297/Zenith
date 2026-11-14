package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.thirdparty.publicsuffix.PublicSuffixPatterns;
import java.util.List;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
public final class InternetDomainName {

    private static final CharMatcher DOTS_MATCHER = CharMatcher.anyOf(".\u3002\uff0e\uff61");
    private static final Splitter DOT_SPLITTER = Splitter.on('.');
    private static final Joiner DOT_JOINER = Joiner.on('.');
    private static final int NO_PUBLIC_SUFFIX_FOUND = -1;
    private static final String DOT_REGEX = "\\.";
    private static final int MAX_PARTS = 127;
    private static final int MAX_LENGTH = 253;
    private static final int MAX_DOMAIN_PART_LENGTH = 63;
    private final String name;
    private final ImmutableList<String> parts;
    private final int publicSuffixIndex;
    private static final CharMatcher DASH_MATCHER = CharMatcher.anyOf("-_");
    private static final CharMatcher PART_CHAR_MATCHER = CharMatcher.JAVA_LETTER_OR_DIGIT.or(InternetDomainName.DASH_MATCHER);

    InternetDomainName(String s) {
        s = Ascii.toLowerCase(InternetDomainName.DOTS_MATCHER.replaceFrom(s, '.'));
        if (s.endsWith(".")) {
            s = s.substring(0, s.length() - 1);
        }

        Preconditions.checkArgument(s.length() <= 253, "Domain name too long: '%s':", s);
        this.name = s;
        this.parts = ImmutableList.copyOf(InternetDomainName.DOT_SPLITTER.split(s));
        Preconditions.checkArgument(this.parts.size() <= 127, "Domain has too many parts: '%s'", s);
        Preconditions.checkArgument(validateSyntax(this.parts), "Not a valid domain name: '%s'", s);
        this.publicSuffixIndex = this.findPublicSuffix();
    }

    private int findPublicSuffix() {
        int i = this.parts.size();

        for (int j = 0; j < i; ++j) {
            String s = InternetDomainName.DOT_JOINER.join((Iterable) this.parts.subList(j, i));

            if (PublicSuffixPatterns.EXACT.containsKey(s)) {
                return j;
            }

            if (PublicSuffixPatterns.EXCLUDED.containsKey(s)) {
                return j + 1;
            }

            if (matchesWildcardPublicSuffix(s)) {
                return j;
            }
        }

        return -1;
    }

    public static InternetDomainName from(String s) {
        return new InternetDomainName((String) Preconditions.checkNotNull(s));
    }

    private static boolean validateSyntax(List<String> list) {
        int i = list.size() - 1;

        if (!validatePart((String) list.get(i), true)) {
            return false;
        } else {
            for (int j = 0; j < i; ++j) {
                String s = (String) list.get(j);

                if (!validatePart(s, false)) {
                    return false;
                }
            }

            return true;
        }
    }

    private static boolean validatePart(String s, boolean flag) {
        if (s.length() >= 1 && s.length() <= 63) {
            String s1 = CharMatcher.ASCII.retainFrom(s);

            return !InternetDomainName.PART_CHAR_MATCHER.matchesAllOf(s1) ? false : (!InternetDomainName.DASH_MATCHER.matches(s.charAt(0)) && !InternetDomainName.DASH_MATCHER.matches(s.charAt(s.length() - 1)) ? !flag || !CharMatcher.DIGIT.matches(s.charAt(0)) : false);
        } else {
            return false;
        }
    }

    public ImmutableList<String> parts() {
        return this.parts;
    }

    public boolean isPublicSuffix() {
        return this.publicSuffixIndex == 0;
    }

    public boolean hasPublicSuffix() {
        return this.publicSuffixIndex != -1;
    }

    public InternetDomainName publicSuffix() {
        return this.hasPublicSuffix() ? this.ancestor(this.publicSuffixIndex) : null;
    }

    public boolean isUnderPublicSuffix() {
        return this.publicSuffixIndex > 0;
    }

    public boolean isTopPrivateDomain() {
        return this.publicSuffixIndex == 1;
    }

    public InternetDomainName topPrivateDomain() {
        if (this.isTopPrivateDomain()) {
            return this;
        } else {
            Preconditions.checkState(this.isUnderPublicSuffix(), "Not under a public suffix: %s", this.name);
            return this.ancestor(this.publicSuffixIndex - 1);
        }
    }

    public boolean hasParent() {
        return this.parts.size() > 1;
    }

    public InternetDomainName parent() {
        Preconditions.checkState(this.hasParent(), "Domain '%s' has no parent", this.name);
        return this.ancestor(1);
    }

    private InternetDomainName ancestor(int i) {
        return from(InternetDomainName.DOT_JOINER.join((Iterable) this.parts.subList(i, this.parts.size())));
    }

    public InternetDomainName child(String s) {
        return from((String) Preconditions.checkNotNull(s) + "." + this.name);
    }

    public static boolean isValid(String s) {
        try {
            from(s);
            return true;
        } catch (IllegalArgumentException illegalargumentexception) {
            return false;
        }
    }

    private static boolean matchesWildcardPublicSuffix(String s) {
        String[] astring = s.split("\\.", 2);

        return astring.length == 2 && PublicSuffixPatterns.UNDER.containsKey(astring[1]);
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        } else if (object instanceof InternetDomainName) {
            InternetDomainName internetdomainname = (InternetDomainName) object;

            return this.name.equals(internetdomainname.name);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.name.hashCode();
    }
}
