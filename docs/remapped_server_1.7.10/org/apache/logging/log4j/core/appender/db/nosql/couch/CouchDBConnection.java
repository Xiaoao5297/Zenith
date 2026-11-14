package org.apache.logging.log4j.core.appender.db.nosql.couch;

import java.util.Map;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.db.nosql.NoSQLConnection;
import org.apache.logging.log4j.core.appender.db.nosql.NoSQLObject;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;

public final class CouchDBConnection implements NoSQLConnection<Map<String, Object>, CouchDBObject> {

    private final CouchDbClient client;
    private boolean closed = false;

    public CouchDBConnection(CouchDbClient couchdbclient) {
        this.client = couchdbclient;
    }

    @Override
    public CouchDBObject createObject() {
        return new CouchDBObject();
    }

    @Override
    public CouchDBObject[] createList(int i) {
        return new CouchDBObject[i];
    }

    @Override
    public void insertObject(NoSQLObject<Map<String, Object>> nosqlobject) {
        try {
            Response response = this.client.save(nosqlobject.unwrap());

            if (response.getError() != null && response.getError().length() > 0) {
                throw new AppenderLoggingException("Failed to write log event to CouchDB due to error: " + response.getError() + ".");
            }
        } catch (Exception exception) {
            throw new AppenderLoggingException("Failed to write log event to CouchDB due to error: " + exception.getMessage(), exception);
        }
    }

    @Override
    public synchronized void close() {
        this.closed = true;
        this.client.shutdown();
    }

    @Override
    public synchronized boolean isClosed() {
        return this.closed;
    }
}
