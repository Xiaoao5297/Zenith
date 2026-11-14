package io.netty.handler.codec.http;

import java.util.Set;

public interface Cookie extends Comparable<Cookie> {

    String getName();

    String getValue();

    void setValue(String s);

    String getDomain();

    void setDomain(String s);

    String getPath();

    void setPath(String s);

    String getComment();

    void setComment(String s);

    long getMaxAge();

    void setMaxAge(long i);

    int getVersion();

    void setVersion(int i);

    boolean isSecure();

    void setSecure(boolean flag);

    boolean isHttpOnly();

    void setHttpOnly(boolean flag);

    String getCommentUrl();

    void setCommentUrl(String s);

    boolean isDiscard();

    void setDiscard(boolean flag);

    Set<Integer> getPorts();

    void setPorts(int... aint);

    void setPorts(Iterable<Integer> iterable);
}
