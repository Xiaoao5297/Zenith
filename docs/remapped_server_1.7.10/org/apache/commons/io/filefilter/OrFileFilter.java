package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class OrFileFilter extends AbstractFileFilter implements ConditionalFileFilter, Serializable {

    private final List<IOFileFilter> fileFilters;

    public OrFileFilter() {
        this.fileFilters = new ArrayList();
    }

    public OrFileFilter(List<IOFileFilter> list) {
        if (list == null) {
            this.fileFilters = new ArrayList();
        } else {
            this.fileFilters = new ArrayList(list);
        }

    }

    public OrFileFilter(IOFileFilter iofilefilter, IOFileFilter iofilefilter1) {
        if (iofilefilter != null && iofilefilter1 != null) {
            this.fileFilters = new ArrayList(2);
            this.addFileFilter(iofilefilter);
            this.addFileFilter(iofilefilter1);
        } else {
            throw new IllegalArgumentException("The filters must not be null");
        }
    }

    @Override
    public void addFileFilter(IOFileFilter iofilefilter) {
        this.fileFilters.add(iofilefilter);
    }

    @Override
    public List<IOFileFilter> getFileFilters() {
        return Collections.unmodifiableList(this.fileFilters);
    }

    @Override
    public boolean removeFileFilter(IOFileFilter iofilefilter) {
        return this.fileFilters.remove(iofilefilter);
    }

    @Override
    public void setFileFilters(List<IOFileFilter> list) {
        this.fileFilters.clear();
        this.fileFilters.addAll(list);
    }

    @Override
    public boolean accept(File file) {
        Iterator iterator = this.fileFilters.iterator();

        IOFileFilter iofilefilter;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            iofilefilter = (IOFileFilter) iterator.next();
        } while (!iofilefilter.accept(file));

        return true;
    }

    @Override
    public boolean accept(File file, String s) {
        Iterator iterator = this.fileFilters.iterator();

        IOFileFilter iofilefilter;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            iofilefilter = (IOFileFilter) iterator.next();
        } while (!iofilefilter.accept(file, s));

        return true;
    }

    @Override
    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append(super.toString());
        stringbuilder.append("(");
        if (this.fileFilters != null) {
            for (int i = 0; i < this.fileFilters.size(); ++i) {
                if (i > 0) {
                    stringbuilder.append(",");
                }

                Object object = this.fileFilters.get(i);

                stringbuilder.append(object == null ? "null" : object.toString());
            }
        }

        stringbuilder.append(")");
        return stringbuilder.toString();
    }
}
