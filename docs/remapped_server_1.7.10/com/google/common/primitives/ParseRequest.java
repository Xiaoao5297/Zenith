package com.google.common.primitives;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
final class ParseRequest {

    final String rawValue;
    final int radix;

    private ParseRequest(String s, int i) {
        this.rawValue = s;
        this.radix = i;
    }

    static ParseRequest fromString(String s) {
        if (s.length() == 0) {
            throw new NumberFormatException("empty string");
        } else {
            char c0 = s.charAt(0);
            String s1;
            byte b0;

            if (!s.startsWith("0x") && !s.startsWith("0X")) {
                if (c0 == '#') {
                    s1 = s.substring(1);
                    b0 = 16;
                } else if (c0 == '0' && s.length() > 1) {
                    s1 = s.substring(1);
                    b0 = 8;
                } else {
                    s1 = s;
                    b0 = 10;
                }
            } else {
                s1 = s.substring(2);
                b0 = 16;
            }

            return new ParseRequest(s1, b0);
        }
    }
}
