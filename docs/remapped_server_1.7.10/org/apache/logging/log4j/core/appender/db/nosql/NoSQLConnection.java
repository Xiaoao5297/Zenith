package org.apache.logging.log4j.core.appender.db.nosql;

import java.io.Closeable;

public interface NoSQLConnection<W, T extends NoSQLObject<W>> extends Closeable {

    T createObject();

    T[] createList(int i);

    void insertObject(NoSQLObject<W> nosqlobject);

    void close();

    boolean isClosed();
}
