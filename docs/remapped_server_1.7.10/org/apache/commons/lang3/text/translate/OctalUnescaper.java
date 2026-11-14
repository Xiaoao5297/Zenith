package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;

public class OctalUnescaper extends CharSequenceTranslator {

    public OctalUnescaper() {}

    @Override
    public int translate(CharSequence charsequence, int i, Writer writer) throws IOException {
        int j = charsequence.length() - i - 1;
        StringBuilder stringbuilder = new StringBuilder();

        if (charsequence.charAt(i) == '\\' && j > 0 && this.isOctalDigit(charsequence.charAt(i + 1))) {
            int k = i + 1;
            int l = i + 2;
            int i1 = i + 3;

            stringbuilder.append(charsequence.charAt(k));
            if (j > 1 && this.isOctalDigit(charsequence.charAt(l))) {
                stringbuilder.append(charsequence.charAt(l));
                if (j > 2 && this.isZeroToThree(charsequence.charAt(k)) && this.isOctalDigit(charsequence.charAt(i1))) {
                    stringbuilder.append(charsequence.charAt(i1));
                }
            }

            writer.write(Integer.parseInt(stringbuilder.toString(), 8));
            return 1 + stringbuilder.length();
        } else {
            return 0;
        }
    }

    private boolean isOctalDigit(char c0) {
        return c0 >= '0' && c0 <= '7';
    }

    private boolean isZeroToThree(char c0) {
        return c0 >= '0' && c0 <= '3';
    }
}
