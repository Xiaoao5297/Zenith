package com.google.common.io;

import com.google.common.annotations.Beta;
import java.io.IOException;

@Beta
public interface LineProcessor<T> {

    boolean processLine(String s) throws IOException;

    T getResult();
}
