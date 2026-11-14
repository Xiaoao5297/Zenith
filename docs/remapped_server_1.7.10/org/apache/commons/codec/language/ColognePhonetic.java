package org.apache.commons.codec.language;

import java.util.Locale;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

public class ColognePhonetic implements StringEncoder {

    private static final char[] AEIJOUY = new char[]{'A', 'E', 'I', 'J', 'O', 'U', 'Y'};
    private static final char[] SCZ = new char[]{'S', 'C', 'Z'};
    private static final char[] WFPV = new char[]{'W', 'F', 'P', 'V'};
    private static final char[] GKQ = new char[]{'G', 'K', 'Q'};
    private static final char[] CKQ = new char[]{'C', 'K', 'Q'};
    private static final char[] AHKLOQRUX = new char[]{'A', 'H', 'K', 'L', 'O', 'Q', 'R', 'U', 'X'};
    private static final char[] SZ = new char[]{'S', 'Z'};
    private static final char[] AHOUKQX = new char[]{'A', 'H', 'O', 'U', 'K', 'Q', 'X'};
    private static final char[] TDX = new char[]{'T', 'D', 'X'};
    private static final char[][] PREPROCESS_MAP = new char[][]{{'\u00c4', 'A'}, {'\u00dc', 'U'}, {'\u00d6', 'O'}, {'\u00df', 'S'}};

    public ColognePhonetic() {}

    private static boolean arrayContains(char[] achar, char c0) {
        char[] achar1 = achar;
        int i = achar.length;

        for (int j = 0; j < i; ++j) {
            char c1 = achar1[j];

            if (c1 == c0) {
                return true;
            }
        }

        return false;
    }

    public String colognePhonetic(String s) {
        if (s == null) {
            return null;
        } else {
            s = this.preprocess(s);
            ColognePhonetic.CologneOutputBuffer colognephonetic_cologneoutputbuffer = new ColognePhonetic.CologneOutputBuffer(s.length() * 2);
            ColognePhonetic.CologneInputBuffer colognephonetic_cologneinputbuffer = new ColognePhonetic.CologneInputBuffer(s.toCharArray());
            char c0 = '-';
            char c1 = '/';
            int i = colognephonetic_cologneinputbuffer.length();

            while (i > 0) {
                char c2 = colognephonetic_cologneinputbuffer.removeNext();
                char c3;

                if ((i = colognephonetic_cologneinputbuffer.length()) > 0) {
                    c3 = colognephonetic_cologneinputbuffer.getNextChar();
                } else {
                    c3 = '-';
                }

                char c4;

                if (arrayContains(ColognePhonetic.AEIJOUY, c2)) {
                    c4 = '0';
                } else if (c2 != 'H' && c2 >= 'A' && c2 <= 'Z') {
                    if (c2 != 'B' && (c2 != 'P' || c3 == 'H')) {
                        if ((c2 == 'D' || c2 == 'T') && !arrayContains(ColognePhonetic.SCZ, c3)) {
                            c4 = '2';
                        } else if (arrayContains(ColognePhonetic.WFPV, c2)) {
                            c4 = '3';
                        } else if (arrayContains(ColognePhonetic.GKQ, c2)) {
                            c4 = '4';
                        } else if (c2 == 'X' && !arrayContains(ColognePhonetic.CKQ, c0)) {
                            c4 = '4';
                            colognephonetic_cologneinputbuffer.addLeft('S');
                            ++i;
                        } else if (c2 != 'S' && c2 != 'Z') {
                            if (c2 == 'C') {
                                if (c1 == '/') {
                                    if (arrayContains(ColognePhonetic.AHKLOQRUX, c3)) {
                                        c4 = '4';
                                    } else {
                                        c4 = '8';
                                    }
                                } else if (!arrayContains(ColognePhonetic.SZ, c0) && arrayContains(ColognePhonetic.AHOUKQX, c3)) {
                                    c4 = '4';
                                } else {
                                    c4 = '8';
                                }
                            } else if (arrayContains(ColognePhonetic.TDX, c2)) {
                                c4 = '8';
                            } else if (c2 == 'R') {
                                c4 = '7';
                            } else if (c2 == 'L') {
                                c4 = '5';
                            } else if (c2 != 'M' && c2 != 'N') {
                                c4 = c2;
                            } else {
                                c4 = '6';
                            }
                        } else {
                            c4 = '8';
                        }
                    } else {
                        c4 = '1';
                    }
                } else {
                    if (c1 == '/') {
                        continue;
                    }

                    c4 = '-';
                }

                if (c4 != '-' && (c1 != c4 && (c4 != '0' || c1 == '/') || c4 < '0' || c4 > '8')) {
                    colognephonetic_cologneoutputbuffer.addRight(c4);
                }

                c0 = c2;
                c1 = c4;
            }

            return colognephonetic_cologneoutputbuffer.toString();
        }
    }

    @Override
    public Object encode(Object object) throws EncoderException {
        if (!(object instanceof String)) {
            throw new EncoderException("This method's parameter was expected to be of the type " + String.class.getName() + ". But actually it was of the type " + object.getClass().getName() + ".");
        } else {
            return this.encode((String) object);
        }
    }

    @Override
    public String encode(String s) {
        return this.colognePhonetic(s);
    }

    public boolean isEncodeEqual(String s, String s1) {
        return this.colognePhonetic(s).equals(this.colognePhonetic(s1));
    }

    private String preprocess(String s) {
        s = s.toUpperCase(Locale.GERMAN);
        char[] achar = s.toCharArray();

        for (int i = 0; i < achar.length; ++i) {
            if (achar[i] > 'Z') {
                char[][] achar1 = ColognePhonetic.PREPROCESS_MAP;
                int j = achar1.length;

                for (int k = 0; k < j; ++k) {
                    char[] achar2 = achar1[k];

                    if (achar[i] == achar2[0]) {
                        achar[i] = achar2[1];
                        break;
                    }
                }
            }
        }

        return new String(achar);
    }

    private class CologneInputBuffer extends ColognePhonetic.CologneBuffer {

        public CologneInputBuffer(char[] achar) {
            super(achar);
        }

        public void addLeft(char c0) {
            ++this.length;
            this.data[this.getNextPos()] = c0;
        }

        @Override
        protected char[] copyData(int i, int j) {
            char[] achar = new char[j];

            System.arraycopy(this.data, this.data.length - this.length + i, achar, 0, j);
            return achar;
        }

        public char getNextChar() {
            return this.data[this.getNextPos()];
        }

        protected int getNextPos() {
            return this.data.length - this.length;
        }

        public char removeNext() {
            char c0 = this.getNextChar();

            --this.length;
            return c0;
        }
    }

    private class CologneOutputBuffer extends ColognePhonetic.CologneBuffer {

        public CologneOutputBuffer(int i) {
            super(i);
        }

        public void addRight(char c0) {
            this.data[this.length] = c0;
            ++this.length;
        }

        @Override
        protected char[] copyData(int i, int j) {
            char[] achar = new char[j];

            System.arraycopy(this.data, i, achar, 0, j);
            return achar;
        }
    }

    private abstract class CologneBuffer {

        protected final char[] data;
        protected int length = 0;

        public CologneBuffer(char[] achar) {
            this.data = achar;
            this.length = achar.length;
        }

        public CologneBuffer(int i) {
            this.data = new char[i];
            this.length = 0;
        }

        protected abstract char[] copyData(int i, int j);

        public int length() {
            return this.length;
        }

        public String toString() {
            return new String(this.copyData(0, this.length));
        }
    }
}
