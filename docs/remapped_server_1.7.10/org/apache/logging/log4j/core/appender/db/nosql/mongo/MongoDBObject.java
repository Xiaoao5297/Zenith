package org.apache.logging.log4j.core.appender.db.nosql.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import java.util.Collections;
import org.apache.logging.log4j.core.appender.db.nosql.NoSQLObject;

public final class MongoDBObject implements NoSQLObject<BasicDBObject> {

    private final BasicDBObject mongoObject = new BasicDBObject();

    public MongoDBObject() {}

    @Override
    public void set(String s, Object object) {
        this.mongoObject.append(s, object);
    }

    @Override
    public void set(String s, NoSQLObject<BasicDBObject> nosqlobject) {
        this.mongoObject.append(s, nosqlobject.unwrap());
    }

    @Override
    public void set(String s, Object[] aobject) {
        BasicDBList basicdblist = new BasicDBList();

        Collections.addAll(basicdblist, aobject);
        this.mongoObject.append(s, basicdblist);
    }

    @Override
    public void set(String s, NoSQLObject<BasicDBObject>[] anosqlobject) {
        BasicDBList basicdblist = new BasicDBList();
        NoSQLObject[] anosqlobject1 = anosqlobject;
        int i = anosqlobject.length;

        for (int j = 0; j < i; ++j) {
            NoSQLObject nosqlobject = anosqlobject1[j];

            basicdblist.add(nosqlobject.unwrap());
        }

        this.mongoObject.append(s, basicdblist);
    }

    @Override
    public BasicDBObject unwrap() {
        return this.mongoObject;
    }
}
