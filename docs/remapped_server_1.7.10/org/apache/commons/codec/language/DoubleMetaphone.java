package org.apache.commons.codec.language;

import java.util.Locale;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

public class DoubleMetaphone implements StringEncoder {

    private static final String VOWELS = "AEIOUY";
    private static final String[] SILENT_START = new String[]{"GN", "KN", "PN", "WR", "PS"};
    private static final String[] L_R_N_M_B_H_F_V_W_SPACE = new String[]{"L", "R", "N", "M", "B", "H", "F", "V", "W", " "};
    private static final String[] ES_EP_EB_EL_EY_IB_IL_IN_IE_EI_ER = new String[]{"ES", "EP", "EB", "EL", "EY", "IB", "IL", "IN", "IE", "EI", "ER"};
    private static final String[] L_T_K_S_N_M_B_Z = new String[]{"L", "T", "K", "S", "N", "M", "B", "Z"};
    private int maxCodeLen = 4;

    public DoubleMetaphone() {}

    public String doubleMetaphone(String s) {
        return this.doubleMetaphone(s, false);
    }

    public String doubleMetaphone(String s, boolean flag) {
        s = this.cleanInput(s);
        if (s == null) {
            return null;
        } else {
            boolean flag1 = this.isSlavoGermanic(s);
            int i = this.isSilentStart(s) ? 1 : 0;
            DoubleMetaphone.DoubleMetaphoneResult doublemetaphone_doublemetaphoneresult = new DoubleMetaphone.DoubleMetaphoneResult(this.getMaxCodeLen());

            while (!doublemetaphone_doublemetaphoneresult.isComplete() && i <= s.length() - 1) {
                switch (s.charAt(i)) {
                    case 'A':
                    case 'E':
                    case 'I':
                    case 'O':
                    case 'U':
                    case 'Y':
                        i = this.handleAEIOUY(doublemetaphone_doublemetaphoneresult, i);
                        break;
                    case 'B':
                        doublemetaphone_doublemetaphoneresult.append('P');
                        i = this.charAt(s, i + 1) == 'B' ? i + 2 : i + 1;
                        break;
                    case 'C':
                        i = this.handleC(s, doublemetaphone_doublemetaphoneresult, i);
                        break;
                    case 'D':
                        i = this.handleD(s, doublemetaphone_doublemetaphoneresult, i);
                        break;
                    case 'F':
                        doublemetaphone_doublemetaphoneresult.append('F');
                        i = this.charAt(s, i + 1) == 'F' ? i + 2 : i + 1;
                        break;
                    case 'G':
                        i = this.handleG(s, doublemetaphone_doublemetaphoneresult, i, flag1);
                        break;
                    case 'H':
                        i = this.handleH(s, doublemetaphone_doublemetaphoneresult, i);
                        break;
                    case 'J':
                        i = this.handleJ(s, doublemetaphone_doublemetaphoneresult, i, flag1);
                        break;
                    case 'K':
                        doublemetaphone_doublemetaphoneresult.append('K');
                        i = this.charAt(s, i + 1) == 'K' ? i + 2 : i + 1;
                        break;
                    case 'L':
                        i = this.handleL(s, doublemetaphone_doublemetaphoneresult, i);
                        break;
                    case 'M':
                        doublemetaphone_doublemetaphoneresult.append('M');
                        i = this.conditionM0(s, i) ? i + 2 : i + 1;
                        break;
                    case 'N':
                        doublemetaphone_doublemetaphoneresult.append('N');
                        i = this.charAt(s, i + 1) == 'N' ? i + 2 : i + 1;
                        break;
                    case 'P':
                        i = this.handleP(s, doublemetaphone_doublemetaphoneresult, i);
                        break;
                    case 'Q':
                        doublemetaphone_doublemetaphoneresult.append('K');
                        i = this.charAt(s, i + 1) == 'Q' ? i + 2 : i + 1;
                        break;
                    case 'R':
                        i = this.handleR(s, doublemetaphone_doublemetaphoneresult, i, flag1);
                        break;
                    case 'S':
                        i = this.handleS(s, doublemetaphone_doublemetaphoneresult, i, flag1);
                        break;
                    case 'T':
                        i = this.handleT(s, doublemetaphone_doublemetaphoneresult, i);
                        break;
                    case 'V':
                        doublemetaphone_doublemetaphoneresult.append('F');
                        i = this.charAt(s, i + 1) == 'V' ? i + 2 : i + 1;
                        break;
                    case 'W':
                        i = this.handleW(s, doublemetaphone_doublemetaphoneresult, i);
                        break;
                    case 'X':
                        i = this.handleX(s, doublemetaphone_doublemetaphoneresult, i);
                        break;
                    case 'Z':
                        i = this.handleZ(s, doublemetaphone_doublemetaphoneresult, i, flag1);
                        break;
                    case '\u00c7':
                        doublemetaphone_doublemetaphoneresult.append('S');
                        ++i;
                        break;
                    case '\u00d1':
                        doublemetaphone_doublemetaphoneresult.append('N');
                        ++i;
                        break;
                    default:
                        ++i;
                }
            }

            return flag ? doublemetaphone_doublemetaphoneresult.getAlternate() : doublemetaphone_doublemetaphoneresult.getPrimary();
        }
    }

    @Override
    public Object encode(Object object) throws EncoderException {
        if (!(object instanceof String)) {
            throw new EncoderException("DoubleMetaphone encode parameter is not of type String");
        } else {
            return this.doubleMetaphone((String) object);
        }
    }

    @Override
    public String encode(String s) {
        return this.doubleMetaphone(s);
    }

    public boolean isDoubleMetaphoneEqual(String s, String s1) {
        return this.isDoubleMetaphoneEqual(s, s1, false);
    }

    public boolean isDoubleMetaphoneEqual(String s, String s1, boolean flag) {
        return this.doubleMetaphone(s, flag).equals(this.doubleMetaphone(s1, flag));
    }

    public int getMaxCodeLen() {
        return this.maxCodeLen;
    }

    public void setMaxCodeLen(int i) {
        this.maxCodeLen = i;
    }

    private int handleAEIOUY(DoubleMetaphone.DoubleMetaphoneResult doublemetaphone_doublemetaphoneresult, int i) {
        if (i == 0) {
            doublemetaphone_doublemetaphoneresult.append('A');
        }

        return i + 1;
    }

    private int handleC(String s, DoubleMetaphone.DoubleMetaphoneResult doublemetaphone_doublemetaphoneresult, int i) {
        if (this.conditionC0(s, i)) {
            doublemetaphone_doublemetaphoneresult.append('K');
            i += 2;
        } else if (i == 0 && contains(s, i, 6, "CAESAR")) {
            doublemetaphone_doublemetaphoneresult.append('S');
            i += 2;
        } else if (contains(s, i, 2, "CH")) {
            i = this.handleCH(s, doublemetaphone_doublemetaphoneresult, i);
        } else if (contains(s, i, 2, "CZ") && !contains(s, i - 2, 4, "WICZ")) {
            doublemetaphone_doublemetaphoneresult.append('S', 'X');
            i += 2;
        } else if (contains(s, i + 1, 3, "CIA")) {
            doublemetaphone_doublemetaphoneresult.append('X');
            i += 3;
        } else {
            if (contains(s, i, 2, "CC") && (i != 1 || this.charAt(s, 0) != 'M')) {
                return this.handleCC(s, doublemetaphone_doublemetaphoneresult, i);
            }

            if (contains(s, i, 2, "CK", "CG", "CQ")) {
                doublemetaphone_doublemetaphoneresult.append('K');
                i += 2;
            } else if (contains(s, i, 2, "CI", "CE", "CY")) {
                if (contains(s, i, 3, "CIO", "CIE", "CIA")) {
                    doublemetaphone_doublemetaphoneresult.append('S', 'X');
                } else {
                    doublemetaphone_doublemetaphoneresult.append('S');
                }

                i += 2;
            } else {
                doublemetaphone_doublemetaphoneresult.append('K');
                if (contains(s, i + 1, 2, " C", " Q", " G")) {
                    i += 3;
                } else if (contains(s, i + 1, 1, "C", "K", "Q") && !contains(s, i + 1, 2, "CE", "CI")) {
                    i += 2;
                } else {
                    ++i;
                }
            }
        }

        return i;
    }

    private int handleCC(String s, DoubleMetaphone.DoubleMetaphoneResult doublemetaphone_doublemetaphoneresult, int i) {
        if (contains(s, i + 2, 1, "I", "E", "H") && !contains(s, i + 2, 2, "HU")) {
            if ((i != 1 || this.charAt(s, i - 1) != 'A') && !contains(s, i - 1, 5, "UCCEE", "UCCES")) {
                doublemetaphone_doublemetaphoneresult.append('X');
            } else {
                doublemetaphone_doublemetaphoneresult.append("KS");
            }

            i += 3;
        } else {
            doublemetaphone_doublemetaphoneresult.append('K');
            i += 2;
        }

        return i;
    }

    private int handleCH(String s, DoubleMetaphone.DoubleMetaphoneResult doublemetaphone_doublemetaphoneresult, int i) {
        if (i > 0 && contains(s, i, 4, "CHAE")) {
            doublemetaphone_doublemetaphoneresult.append('K', 'X');
            return i + 2;
        } else if (this.conditionCH0(s, i)) {
            doublemetaphone_doublemetaphoneresult.append('K');
            return i + 2;
        } else if (this.conditionCH1(s, i)) {
            doublemetaphone_doublemetaphoneresult.append('K');
            return i + 2;
        } else {
            if (i > 0) {
                if (contains(s, 0, 2, "MC")) {
                    doublemetaphone_doublemetaphoneresult.append('K');
                } else {
                    doublemetaphone_doublemetaphoneresult.append('X', 'K');
                }
            } else {
                doublemetaphone_doublemetaphoneresult.append('X');
            }

            return i + 2;
        }
    }

    private int handleD(String s, DoubleMetaphone.DoubleMetaphoneResult doublemetaphone_doublemetaphoneresult, int i) {
        if (contains(s, i, 2, "DG")) {
            if (contains(s, i + 2, 1, "I", "E", "Y")) {
                doublemetaphone_doublemetaphoneresult.append('J');
                i += 3;
            } else {
                doublemetaphone_doublemetaphoneresult.append("TK");
                i += 2;
            }
        } else if (contains(s, i, 2, "DT", "DD")) {
            doublemetaphone_doublemetaphoneresult.append('T');
            i += 2;
        } else {
            doublemetaphone_doublemetaphoneresult.append('T');
            ++i;
        }

        return i;
    }

    private int handleG(String s, DoubleMetaphone.DoubleMetaphoneResult doublemetaphone_doublemetaphoneresult, int i, boolean flag) {
        if (this.charAt(s, i + 1) == 'H') {
            i = this.handleGH(s, doublemetaphone_doublemetaphoneresult, i);
        } else if (this.charAt(s, i + 1) == 'N') {
            if (i == 1 && this.isVowel(this.charAt(s, 0)) && !flag) {
                doublemetaphone_doublemetaphoneresult.append("KN", "N");
            } else if (!contains(s, i + 2, 2, "EY") && this.charAt(s, i + 1) != 'Y' && !flag) {
                doublemetaphone_doublemetaphoneresult.append("N", "KN");
            } else {
                doublemetaphone_doublemetaphoneresult.append("KN");
            }

            i += 2;
        } else if (contains(s, i + 1, 2, "LI") && !flag) {
            doublemetaphone_doublemetaphoneresult.append("KL", "L");
            i += 2;
        } else if (i == 0 && (this.charAt(s, i + 1) == 'Y' || contains(s, i + 1, 2, DoubleMetaphone.ES_EP_EB_EL_EY_IB_IL_IN_IE_EI_ER))) {
            doublemetaphone_doublemetaphoneresult.append('K', 'J');
            i += 2;
        } else if ((contains(s, i + 1, 2, "ER") || this.charAt(s, i + 1) == 'Y') && !contains(s, 0, 6, "DANGER", "RANGER", "MANGER") && !contains(s, i - 1, 1, "E", "I") && !contains(s, i - 1, 3, "RGY", "OGY")) {
            doublemetaphone_doublemetaphoneresult.append('K', 'J');
            i += 2;
        } else if (!contains(s, i + 1, 1, "E", "I", "Y") && !contains(s, i - 1, 4, "AGGI", "OGGI")) {
            if (this.charAt(s, i + 1) == 'G') {
                i += 2;
                doublemetaphone_doublemetaphoneresult.append('K');
            } else {
                ++i;
                doublemetaphone_doublemetaphoneresult.append('K');
            }
        } else {
            if (!contains(s, 0, 4, "VAN ", "VON ") && !contains(s, 0, 3, "SCH") && !contains(s, i + 1, 2, "ET")) {
                if (contains(s, i + 1, 3, "IER")) {
                    doublemetaphone_doublemetaphoneresult.append('J');
                } else {
                    doublemetaphone_doublemetaphoneresult.append('J', 'K');
                }
            } else {
                doublemetaphone_doublemetaphoneresult.append('K');
            }

            i += 2;
        }

        return i;
    }

    private int handleGH(String s, DoubleMetaphone.DoubleMetaphoneResult doublemetaphone_doublemetaphoneresult, int i) {
        if (i > 0 && !this.isVowel(this.charAt(s, i - 1))) {
            doublemetaphone_doublemetaphoneresult.append('K');
            i += 2;
        } else if (i == 0) {
            if (this.charAt(s, i + 2) == 'I') {
                doublemetaphone_doublemetaphoneresult.append('J');
            } else {
                doublemetaphone_doublemetaphoneresult.append('K');
            }

            i += 2;
        } else if ((i <= 1 || !contains(s, i - 2, 1, "B", "H", "D")) && (i <= 2 || !contains(s, i - 3, 1, "B", "H", "D")) && (i <= 3 || !contains(s, i - 4, 1, "B", "H"))) {
            if (i > 2 && this.charAt(s, i - 1) == 'U' && contains(s, i - 3, 1, "C", "G", "L", "R", "T")) {
                doublemetaphone_doublemetaphoneresult.append('F');
            } else if (i > 0 && this.charAt(s, i - 1) != 'I') {
                doublemetaphone_doublemetaphoneresult.append('K');
            }

            i += 2;
        } else {
            i += 2;
        }

        return i;
    }

    private int handleH(String s, DoubleMetaphone.DoubleMetaphoneResult doublemetaphone_doublemetaphoneresult, int i) {
        if ((i == 0 || this.isVowel(this.charAt(s, i - 1))) && this.isVowel(this.charAt(s, i + 1))) {
            doublemetaphone_doublemetaphoneresult.append('H');
            i += 2;
        } else {
            ++i;
        }

        return i;
    }

    private int handleJ(String s, DoubleMetaphone.DoubleMetaphoneResult doublemetaphone_doublemetaphoneresult, int i, boolean flag) {
        if (!contains(s, i, 4, "JOSE") && !contains(s, 0, 4, "SAN ")) {
            if (i == 0 && !contains(s, i, 4, "JOSE")) {
                doublemetaphone_doublemetaphoneresult.append('J', 'A');
            } else if (this.isVowel(this.charAt(s, i - 1)) && !flag && (this.charAt(s, i + 1) == 'A' || this.charAt(s, i + 1) == 'O')) {
                doublemetaphone_doublemetaphoneresult.append('J', 'H');
            } else if (i == s.length() - 1) {
                doublemetaphone_doublemetaphoneresult.append('J', ' ');
            } else if (!contains(s, i + 1, 1, DoubleMetaphone.L_T_K_S_N_M_B_Z) && !contains(s, i - 1, 1, "S", "K", "L")) {
                doublemetaphone_doublemetaphoneresult.append('J');
            }

            if (this.charAt(s, i + 1) == 'J') {
                i += 2;
            } else {
                ++i;
            }
        } else {
            if ((i != 0 || this.charAt(s, i + 4) != ' ') && s.length() != 4 && !contains(s, 0, 4, "SAN ")) {
                doublemetaphone_doublemetaphoneresult.append('J', 'H');
            } else {
                doublemetaphone_doublemetaphoneresult.append('H');
            }

            ++i;
        }

        return i;
    }

    private int handleL(String s, DoubleMetaphone.DoubleMetaphoneResult doublemetaphone_doublemetaphoneresult, int i) {
        if (this.charAt(s, i + 1) == 'L') {
            if (this.conditionL0(s, i)) {
                doublemetaphone_doublemetaphoneresult.appendPrimary('L');
            } else {
                doublemetaphone_doublemetaphoneresult.append('L');
            }

            i += 2;
        } else {
            ++i;
            doublemetaphone_doublemetaphoneresult.append('L');
        }

        return i;
    }

    private int handleP(String s, DoubleMetaphone.DoubleMetaphoneResult doublemetaphone_doublemetaphoneresult, int i) {
        if (this.charAt(s, i + 1) == 'H') {
            doublemetaphone_doublemetaphoneresult.append('F');
            i += 2;
        } else {
            doublemetaphone_doublemetaphoneresult.append('P');
            i = contains(s, i + 1, 1, "P", "B") ? i + 2 : i + 1;
        }

        return i;
    }

    private int handleR(String s, DoubleMetaphone.DoubleMetaphoneResult doublemetaphone_doublemetaphoneresult, int i, boolean flag) {
        if (i == s.length() - 1 && !flag && contains(s, i - 2, 2, "IE") && !contains(s, i - 4, 2, "ME", "MA")) {
            doublemetaphone_doublemetaphoneresult.appendAlternate('R');
        } else {
            doublemetaphone_doublemetaphoneresult.append('R');
        }

        return this.charAt(s, i + 1) == 'R' ? i + 2 : i + 1;
    }

    private int handleS(String s, DoubleMetaphone.DoubleMetaphoneResult doublemetaphone_doublemetaphoneresult, int i, boolean flag) {
        if (contains(s, i - 1, 3, "ISL", "YSL")) {
            ++i;
        } else if (i == 0 && contains(s, i, 5, "SUGAR")) {
            doublemetaphone_doublemetaphoneresult.append('X', 'S');
            ++i;
        } else if (contains(s, i, 2, "SH")) {
            if (contains(s, i + 1, 4, "HEIM", "HOEK", "HOLM", "HOLZ")) {
                doublemetaphone_doublemetaphoneresult.append('S');
            } else {
                doublemetaphone_doublemetaphoneresult.append('X');
            }

            i += 2;
        } else if (!contains(s, i, 3, "SIO", "SIA") && !contains(s, i, 4, "SIAN")) {
            if ((i != 0 || !contains(s, i + 1, 1, "M", "N", "L", "W")) && !contains(s, i + 1, 1, "Z")) {
                if (contains(s, i, 2, "SC")) {
                    i = this.handleSC(s, doublemetaphone_doublemetaphoneresult, i);
                } else {
                    if (i == s.length() - 1 && contains(s, i - 2, 2, "AI", "OI")) {
                        doublemetaphone_doublemetaphoneresult.appendAlternate('S');
                    } else {
                        doublemetaphone_doublemetaphoneresult.append('S');
                    }

                    i = contains(s, i + 1, 1, "S", "Z") ? i + 2 : i + 1;
                }
            } else {
                doublemetaphone_doublemetaphoneresult.append('S', 'X');
                i = contains(s, i + 1, 1, "Z") ? i + 2 : i + 1;
            }
        } else {
            if (flag) {
                doublemetaphone_doublemetaphoneresult.append('S');
            } else {
                doublemetaphone_doublemetaphoneresult.append('S', 'X');
            }

            i += 3;
        }

        return i;
    }

    private int handleSC(String s, DoubleMetaphone.DoubleMetaphoneResult doublemetaphone_doublemetaphoneresult, int i) {
        if (this.charAt(s, i + 2) == 'H') {
            if (contains(s, i + 3, 2, "OO", "ER", "EN", "UY", "ED", "EM")) {
                if (contains(s, i + 3, 2, "ER", "EN")) {
                    doublemetaphone_doublemetaphoneresult.append("X", "SK");
                } else {
                    doublemetaphone_doublemetaphoneresult.append("SK");
                }
            } else if (i == 0 && !this.isVowel(this.charAt(s, 3)) && this.charAt(s, 3) != 'W') {
                doublemetaphone_doublemetaphoneresult.append('X', 'S');
            } else {
                doublemetaphone_doublemetaphoneresult.append('X');
            }
        } else if (contains(s, i + 2, 1, "I", "E", "Y")) {
            doublemetaphone_doublemetaphoneresult.append('S');
        } else {
            doublemetaphone_doublemetaphoneresult.append("SK");
        }

        return i + 3;
    }

    private int handleT(String s, DoubleMetaphone.DoubleMetaphoneResult doublemetaphone_doublemetaphoneresult, int i) {
        if (contains(s, i, 4, "TION")) {
            doublemetaphone_doublemetaphoneresult.append('X');
            i += 3;
        } else if (contains(s, i, 3, "TIA", "TCH")) {
            doublemetaphone_doublemetaphoneresult.append('X');
            i += 3;
        } else if (!contains(s, i, 2, "TH") && !contains(s, i, 3, "TTH")) {
            doublemetaphone_doublemetaphoneresult.append('T');
            i = contains(s, i + 1, 1, "T", "D") ? i + 2 : i + 1;
        } else {
            if (!contains(s, i + 2, 2, "OM", "AM") && !contains(s, 0, 4, "VAN ", "VON ") && !contains(s, 0, 3, "SCH")) {
                doublemetaphone_doublemetaphoneresult.append('0', 'T');
            } else {
                doublemetaphone_doublemetaphoneresult.append('T');
            }

            i += 2;
        }

        return i;
    }

    private int handleW(String s, DoubleMetaphone.DoubleMetaphoneResult doublemetaphone_doublemetaphoneresult, int i) {
        if (contains(s, i, 2, "WR")) {
            doublemetaphone_doublemetaphoneresult.append('R');
            i += 2;
        } else if (i == 0 && (this.isVowel(this.charAt(s, i + 1)) || contains(s, i, 2, "WH"))) {
            if (this.isVowel(this.charAt(s, i + 1))) {
                doublemetaphone_doublemetaphoneresult.append('A', 'F');
            } else {
                doublemetaphone_doublemetaphoneresult.append('A');
            }

            ++i;
        } else if ((i != s.length() - 1 || !this.isVowel(this.charAt(s, i - 1))) && !contains(s, i - 1, 5, "EWSKI", "EWSKY", "OWSKI", "OWSKY") && !contains(s, 0, 3, "SCH")) {
            if (contains(s, i, 4, "WICZ", "WITZ")) {
                doublemetaphone_doublemetaphoneresult.append("TS", "FX");
                i += 4;
            } else {
                ++i;
            }
        } else {
            doublemetaphone_doublemetaphoneresult.appendAlternate('F');
            ++i;
        }

        return i;
    }

    private int handleX(String s, DoubleMetaphone.DoubleMetaphoneResult doublemetaphone_doublemetaphoneresult, int i) {
        if (i == 0) {
            doublemetaphone_doublemetaphoneresult.append('S');
            ++i;
        } else {
            if (i != s.length() - 1 || !contains(s, i - 3, 3, "IAU", "EAU") && !contains(s, i - 2, 2, "AU", "OU")) {
                doublemetaphone_doublemetaphoneresult.append("KS");
            }

            i = contains(s, i + 1, 1, "C", "X") ? i + 2 : i + 1;
        }

        return i;
    }

    private int handleZ(String s, DoubleMetaphone.DoubleMetaphoneResult doublemetaphone_doublemetaphoneresult, int i, boolean flag) {
        if (this.charAt(s, i + 1) == 'H') {
            doublemetaphone_doublemetaphoneresult.append('J');
            i += 2;
        } else {
            if (!contains(s, i + 1, 2, "ZO", "ZI", "ZA") && (!flag || i <= 0 || this.charAt(s, i - 1) == 'T')) {
                doublemetaphone_doublemetaphoneresult.append('S');
            } else {
                doublemetaphone_doublemetaphoneresult.append("S", "TS");
            }

            i = this.charAt(s, i + 1) == 'Z' ? i + 2 : i + 1;
        }

        return i;
    }

    private boolean conditionC0(String s, int i) {
        if (contains(s, i, 4, "CHIA")) {
            return true;
        } else if (i <= 1) {
            return false;
        } else if (this.isVowel(this.charAt(s, i - 2))) {
            return false;
        } else if (!contains(s, i - 1, 3, "ACH")) {
            return false;
        } else {
            char c0 = this.charAt(s, i + 2);

            return c0 != 'I' && c0 != 'E' || contains(s, i - 2, 6, "BACHER", "MACHER");
        }
    }

    private boolean conditionCH0(String s, int i) {
        return i != 0 ? false : (!contains(s, i + 1, 5, "HARAC", "HARIS") && !contains(s, i + 1, 3, "HOR", "HYM", "HIA", "HEM") ? false : !contains(s, 0, 5, "CHORE"));
    }

    private boolean conditionCH1(String s, int i) {
        return contains(s, 0, 4, "VAN ", "VON ") || contains(s, 0, 3, "SCH") || contains(s, i - 2, 6, "ORCHES", "ARCHIT", "ORCHID") || contains(s, i + 2, 1, "T", "S") || (contains(s, i - 1, 1, "A", "O", "U", "E") || i == 0) && (contains(s, i + 2, 1, DoubleMetaphone.L_R_N_M_B_H_F_V_W_SPACE) || i + 1 == s.length() - 1);
    }

    private boolean conditionL0(String s, int i) {
        return i == s.length() - 3 && contains(s, i - 1, 4, "ILLO", "ILLA", "ALLE") ? true : (contains(s, s.length() - 2, 2, "AS", "OS") || contains(s, s.length() - 1, 1, "A", "O")) && contains(s, i - 1, 4, "ALLE");
    }

    private boolean conditionM0(String s, int i) {
        return this.charAt(s, i + 1) == 'M' ? true : contains(s, i - 1, 3, "UMB") && (i + 1 == s.length() - 1 || contains(s, i + 2, 2, "ER"));
    }

    private boolean isSlavoGermanic(String s) {
        return s.indexOf(87) > -1 || s.indexOf(75) > -1 || s.indexOf("CZ") > -1 || s.indexOf("WITZ") > -1;
    }

    private boolean isVowel(char c0) {
        return "AEIOUY".indexOf(c0) != -1;
    }

    private boolean isSilentStart(String s) {
        boolean flag = false;
        String[] astring = DoubleMetaphone.SILENT_START;
        int i = astring.length;

        for (int j = 0; j < i; ++j) {
            String s1 = astring[j];

            if (s.startsWith(s1)) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    private String cleanInput(String s) {
        if (s == null) {
            return null;
        } else {
            s = s.trim();
            return s.length() == 0 ? null : s.toUpperCase(Locale.ENGLISH);
        }
    }

    protected char charAt(String s, int i) {
        return i >= 0 && i < s.length() ? s.charAt(i) : '\u0000';
    }

    protected static boolean contains(String s, int i, int j, String... astring) {
        boolean flag = false;

        if (i >= 0 && i + j <= s.length()) {
            String s1 = s.substring(i, i + j);
            String[] astring1 = astring;
            int k = astring.length;

            for (int l = 0; l < k; ++l) {
                String s2 = astring1[l];

                if (s1.equals(s2)) {
                    flag = true;
                    break;
                }
            }
        }

        return flag;
    }

    public class DoubleMetaphoneResult {

        private final StringBuilder primary = new StringBuilder(DoubleMetaphone.this.getMaxCodeLen());
        private final StringBuilder alternate = new StringBuilder(DoubleMetaphone.this.getMaxCodeLen());
        private final int maxLength;

        public DoubleMetaphoneResult(int i) {
            this.maxLength = i;
        }

        public void append(char c0) {
            this.appendPrimary(c0);
            this.appendAlternate(c0);
        }

        public void append(char c0, char c1) {
            this.appendPrimary(c0);
            this.appendAlternate(c1);
        }

        public void appendPrimary(char c0) {
            if (this.primary.length() < this.maxLength) {
                this.primary.append(c0);
            }

        }

        public void appendAlternate(char c0) {
            if (this.alternate.length() < this.maxLength) {
                this.alternate.append(c0);
            }

        }

        public void append(String s) {
            this.appendPrimary(s);
            this.appendAlternate(s);
        }

        public void append(String s, String s1) {
            this.appendPrimary(s);
            this.appendAlternate(s1);
        }

        public void appendPrimary(String s) {
            int i = this.maxLength - this.primary.length();

            if (s.length() <= i) {
                this.primary.append(s);
            } else {
                this.primary.append(s.substring(0, i));
            }

        }

        public void appendAlternate(String s) {
            int i = this.maxLength - this.alternate.length();

            if (s.length() <= i) {
                this.alternate.append(s);
            } else {
                this.alternate.append(s.substring(0, i));
            }

        }

        public String getPrimary() {
            return this.primary.toString();
        }

        public String getAlternate() {
            return this.alternate.toString();
        }

        public boolean isComplete() {
            return this.primary.length() >= this.maxLength && this.alternate.length() >= this.maxLength;
        }
    }
}
