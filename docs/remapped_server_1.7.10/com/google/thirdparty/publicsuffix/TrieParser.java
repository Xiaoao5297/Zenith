package com.google.thirdparty.publicsuffix;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;

@GwtCompatible
class TrieParser {

    private static final Joiner PREFIX_JOINER = Joiner.on("");

    TrieParser() {}

    static ImmutableMap<String, PublicSuffixType> parseTrie(CharSequence charsequence) {
        ImmutableMap.Builder immutablemap_builder = ImmutableMap.builder();
        int i = charsequence.length();

        for (int j = 0; j < i; j += doParseTrieToBuilder(Lists.newLinkedList(), charsequence.subSequence(j, i), immutablemap_builder)) {
            ;
        }

        return immutablemap_builder.build();
    }

    private static int doParseTrieToBuilder(List<CharSequence> list, CharSequence charsequence, ImmutableMap.Builder<String, PublicSuffixType> immutablemap_builder) {
        int i = charsequence.length();
        int j = 0;

        char c0;

        for (c0 = 0; j < i; ++j) {
            c0 = charsequence.charAt(j);
            if (c0 == '&' || c0 == '?' || c0 == '!' || c0 == ':' || c0 == ',') {
                break;
            }
        }

        list.add(0, reverse(charsequence.subSequence(0, j)));
        if (c0 == '!' || c0 == '?' || c0 == ':' || c0 == ',') {
            String s = TrieParser.PREFIX_JOINER.join((Iterable) list);

            if (s.length() > 0) {
                immutablemap_builder.put(s, PublicSuffixType.fromCode(c0));
            }
        }

        ++j;
        if (c0 != '?' && c0 != ',') {
            while (j < i) {
                j += doParseTrieToBuilder(list, charsequence.subSequence(j, i), immutablemap_builder);
                if (charsequence.charAt(j) == '?' || charsequence.charAt(j) == ',') {
                    ++j;
                    break;
                }
            }
        }

        list.remove(0);
        return j;
    }

    private static CharSequence reverse(CharSequence charsequence) {
        int i = charsequence.length();

        if (i <= 1) {
            return charsequence;
        } else {
            char[] achar = new char[i];

            achar[0] = charsequence.charAt(i - 1);

            for (int j = 1; j < i; ++j) {
                achar[j] = charsequence.charAt(i - 1 - j);
                if (Character.isSurrogatePair(achar[j], achar[j - 1])) {
                    swap(achar, j - 1, j);
                }
            }

            return new String(achar);
        }
    }

    private static void swap(char[] achar, int i, int j) {
        char c0 = achar[i];

        achar[i] = achar[j];
        achar[j] = c0;
    }
}
