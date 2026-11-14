package com.google.common.io;

import java.io.IOException;

abstract class LineBuffer {

    private StringBuilder line = new StringBuilder();
    private boolean sawReturn;

    LineBuffer() {}

    protected void add(char[] achar, int i, int j) throws IOException {
        int k = i;

        if (this.sawReturn && j > 0 && this.finishLine(achar[i] == '\n')) {
            k = i + 1;
        }

        int l = k;

        for (int i1 = i + j; k < i1; ++k) {
            switch (achar[k]) {
                case '\n':
                    this.line.append(achar, l, k - l);
                    this.finishLine(true);
                    l = k + 1;
                    break;
                case '\r':
                    this.line.append(achar, l, k - l);
                    this.sawReturn = true;
                    if (k + 1 < i1 && this.finishLine(achar[k + 1] == '\n')) {
                        ++k;
                    }

                    l = k + 1;
            }
        }

        this.line.append(achar, l, i + j - l);
    }

    private boolean finishLine(boolean flag) throws IOException {
        this.handleLine(this.line.toString(), this.sawReturn ? (flag ? "\r\n" : "\r") : (flag ? "\n" : ""));
        this.line = new StringBuilder();
        this.sawReturn = false;
        return flag;
    }

    protected void finish() throws IOException {
        if (this.sawReturn || this.line.length() > 0) {
            this.finishLine(false);
        }

    }

    protected abstract void handleLine(String s, String s1) throws IOException;
}
