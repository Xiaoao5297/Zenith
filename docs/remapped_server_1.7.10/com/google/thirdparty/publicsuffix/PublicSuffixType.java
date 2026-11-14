package com.google.thirdparty.publicsuffix;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
enum PublicSuffixType {

    PRIVATE(':', ','), ICANN('!', '?');

    private final char innerNodeCode;
    private final char leafNodeCode;

    private PublicSuffixType(char c0, char c1) {
        this.innerNodeCode = c0;
        this.leafNodeCode = c1;
    }

    char getLeafNodeCode() {
        return this.leafNodeCode;
    }

    char getInnerNodeCode() {
        return this.innerNodeCode;
    }

    static PublicSuffixType fromCode(char c0) {
        PublicSuffixType[] apublicsuffixtype = values();
        int i = apublicsuffixtype.length;

        for (int j = 0; j < i; ++j) {
            PublicSuffixType publicsuffixtype = apublicsuffixtype[j];

            if (publicsuffixtype.getInnerNodeCode() == c0 || publicsuffixtype.getLeafNodeCode() == c0) {
                return publicsuffixtype;
            }
        }

        throw new IllegalArgumentException("No enum corresponding to given code: " + c0);
    }

    static PublicSuffixType fromIsPrivate(boolean flag) {
        return flag ? PublicSuffixType.PRIVATE : PublicSuffixType.ICANN;
    }
}
