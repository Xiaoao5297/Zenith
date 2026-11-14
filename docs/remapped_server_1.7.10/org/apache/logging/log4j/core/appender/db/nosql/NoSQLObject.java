package org.apache.logging.log4j.core.appender.db.nosql;

public interface NoSQLObject<W> {

    void set(String s, Object object);

    void set(String s, NoSQLObject<W> nosqlobject);

    void set(String s, Object[] aobject);

    void set(String s, NoSQLObject<W>[] anosqlobject);

    W unwrap();
}
