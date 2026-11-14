package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;

public class TrueFileFilter implements IOFileFilter, Serializable {

    public static final IOFileFilter TRUE = new TrueFileFilter();
    public static final IOFileFilter INSTANCE = TrueFileFilter.TRUE;

    protected TrueFileFilter() {}

    @Override
    public boolean accept(File file) {
        return true;
    }

    @Override
    public boolean accept(File file, String s) {
        return true;
    }
}
