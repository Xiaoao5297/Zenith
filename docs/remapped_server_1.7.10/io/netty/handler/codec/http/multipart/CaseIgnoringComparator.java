package io.netty.handler.codec.http.multipart;

import java.io.Serializable;
import java.util.Comparator;

final class CaseIgnoringComparator implements Comparator<String>, Serializable {

    private static final long serialVersionUID = 4582133183775373862L;
    static final CaseIgnoringComparator INSTANCE = new CaseIgnoringComparator();

    private CaseIgnoringComparator() {}

    public int compare(String s, String s1) {
        return s.compareToIgnoreCase(s1);
    }

    private Object readResolve() {
        return CaseIgnoringComparator.INSTANCE;
    }
}
