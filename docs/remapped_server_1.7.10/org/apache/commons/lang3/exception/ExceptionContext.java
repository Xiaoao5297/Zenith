package org.apache.commons.lang3.exception;

import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;

public interface ExceptionContext {

    ExceptionContext addContextValue(String s, Object object);

    ExceptionContext setContextValue(String s, Object object);

    List<Object> getContextValues(String s);

    Object getFirstContextValue(String s);

    Set<String> getContextLabels();

    List<Pair<String, Object>> getContextEntries();

    String getFormattedExceptionMessage(String s);
}
