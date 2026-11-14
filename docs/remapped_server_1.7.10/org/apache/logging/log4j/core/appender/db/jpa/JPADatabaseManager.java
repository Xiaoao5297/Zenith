package org.apache.logging.log4j.core.appender.db.jpa;

import java.lang.reflect.Constructor;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseManager;

public final class JPADatabaseManager extends AbstractDatabaseManager {

    private static final JPADatabaseManager.JPADatabaseManagerFactory FACTORY = new JPADatabaseManager.JPADatabaseManagerFactory();
    private final String entityClassName;
    private final Constructor<? extends AbstractLogEventWrapperEntity> entityConstructor;
    private final String persistenceUnitName;
    private EntityManagerFactory entityManagerFactory;

    private JPADatabaseManager(String s, int i, Class<? extends AbstractLogEventWrapperEntity> oclass, Constructor<? extends AbstractLogEventWrapperEntity> constructor, String s1) {
        super(s, i);
        this.entityClassName = oclass.getName();
        this.entityConstructor = constructor;
        this.persistenceUnitName = s1;
    }

    @Override
    protected void connectInternal() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory(this.persistenceUnitName);
    }

    @Override
    protected void disconnectInternal() {
        if (this.entityManagerFactory != null && this.entityManagerFactory.isOpen()) {
            this.entityManagerFactory.close();
        }

    }

    @Override
    protected void writeInternal(LogEvent logevent) {
        if (this.isConnected() && this.entityManagerFactory != null) {
            AbstractLogEventWrapperEntity abstractlogeventwrapperentity;

            try {
                abstractlogeventwrapperentity = (AbstractLogEventWrapperEntity) this.entityConstructor.newInstance(logevent);
            } catch (Exception exception) {
                throw new AppenderLoggingException("Failed to instantiate entity class [" + this.entityClassName + "].", exception);
            }

            EntityManager entitymanager = null;
            EntityTransaction entitytransaction = null;

            try {
                entitymanager = this.entityManagerFactory.createEntityManager();
                entitytransaction = entitymanager.getTransaction();
                entitytransaction.begin();
                entitymanager.persist(abstractlogeventwrapperentity);
                entitytransaction.commit();
            } catch (Exception exception1) {
                if (entitytransaction != null && entitytransaction.isActive()) {
                    entitytransaction.rollback();
                }

                throw new AppenderLoggingException("Failed to insert record for log event in JDBC manager: " + exception1.getMessage(), exception1);
            } finally {
                if (entitymanager != null && entitymanager.isOpen()) {
                    entitymanager.close();
                }

            }

        } else {
            throw new AppenderLoggingException("Cannot write logging event; JPA manager not connected to the database.");
        }
    }

    public static JPADatabaseManager getJPADatabaseManager(String s, int i, Class<? extends AbstractLogEventWrapperEntity> oclass, Constructor<? extends AbstractLogEventWrapperEntity> constructor, String s1) {
        return (JPADatabaseManager) AbstractDatabaseManager.getManager(s, (AbstractDatabaseManager.AbstractFactoryData) (new JPADatabaseManager.FactoryData(i, oclass, constructor, s1)), (ManagerFactory) JPADatabaseManager.FACTORY);
    }

    private static final class JPADatabaseManagerFactory implements ManagerFactory<JPADatabaseManager, JPADatabaseManager.FactoryData> {

        private JPADatabaseManagerFactory() {}

        public JPADatabaseManager createManager(String s, JPADatabaseManager.FactoryData jpadatabasemanager_factorydata) {
            return new JPADatabaseManager(s, jpadatabasemanager_factorydata.getBufferSize(), jpadatabasemanager_factorydata.entityClass, jpadatabasemanager_factorydata.entityConstructor, jpadatabasemanager_factorydata.persistenceUnitName);
        }
    }

    private static final class FactoryData extends AbstractDatabaseManager.AbstractFactoryData {

        private final Class<? extends AbstractLogEventWrapperEntity> entityClass;
        private final Constructor<? extends AbstractLogEventWrapperEntity> entityConstructor;
        private final String persistenceUnitName;

        protected FactoryData(int i, Class<? extends AbstractLogEventWrapperEntity> oclass, Constructor<? extends AbstractLogEventWrapperEntity> constructor, String s) {
            super(i);
            this.entityClass = oclass;
            this.entityConstructor = constructor;
            this.persistenceUnitName = s;
        }
    }
}
