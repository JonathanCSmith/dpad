package jonathansmith.dpad.server.engine.executor;

import java.io.File;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import jonathansmith.dpad.common.database.DatabaseManager;
import jonathansmith.dpad.common.database.record.Record;
import jonathansmith.dpad.common.engine.executor.Task;

import jonathansmith.dpad.server.ServerEngine;

/**
 * Created by Jon on 20/05/2014.
 * <p/>
 * Server database setup
 */
public class SetupHibernateTask extends Task {

    private static final String TASK_NAME = "Server Hibernate Setup";

    private final ServerEngine engine;

    private boolean isNewDatabase;

    public SetupHibernateTask(ServerEngine engine) {
        super(TASK_NAME, engine);

        this.engine = engine;
    }

    @Override
    public void runTask() {
        // Build the hibernate Configuration
        this.loggingEngine.info("Beginning database initialisation", null);
        this.loggingEngine.trace("Building hibernate configuration", null);
        Configuration cfg = this.buildHibernateSessioncfguration();

        this.loggingEngine.trace("Building hibernate service registry", null);
        ServiceRegistry registry = new ServiceRegistryBuilder().applySettings(cfg.getProperties()).buildServiceRegistry();

        // Initialise the hibernate session
        SessionFactory sessionFactory;
        try {
            this.loggingEngine.trace("Building hibernate session factory", null);
            sessionFactory = cfg.buildSessionFactory(registry);
            sessionFactory.openSession();

            if (this.isNewDatabase) {
                this.loggingEngine.trace("Rebuilding database as it is new... I am not sure why I have to do this though...", null);
                this.loggingEngine.warn("", null);
                this.loggingEngine.warn("==========================================================", null);
                this.loggingEngine.warn("Retrying database creation for a second time as it is new!", null);
                this.loggingEngine.warn("==========================================================", null);
                this.loggingEngine.warn("", null);

                sessionFactory.close();
                sessionFactory = cfg.buildSessionFactory(registry);
                sessionFactory.openSession();
            }
        }

        catch (HibernateException ex) {
            this.loggingEngine.handleError("Connection to: " + this.engine.getFileSystem().getDatabaseDirectory() + " was rejected or unavailable", ex, true);
            return;
        }

        this.loggingEngine.trace("Assigning hibernate information to the database manager", null);
        DatabaseManager dbm = new DatabaseManager(sessionFactory);
        this.engine.setDatabaseManager(dbm);
        this.loggingEngine.info("Database initialisation complete", null);
    }

    private Configuration buildHibernateSessioncfguration() {
        Configuration cfg = new Configuration();

        // Use h2 as that is our database type
        cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        cfg.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        cfg.setProperty("hibernate.connection.url", "jdbc:h2:file:" + this.engine.getFileSystem().getDatabaseDirectory() + "/DPADDatabase");

        // Use custom connection manager for better IO to the database
        cfg.setProperty("connection.provider.provider_class", "com.jolbox.bonecp.provider.BoneCPConnectionProvider");
        cfg.setProperty("bonecp.partitionCount", "3");
        cfg.setProperty("bonecp.maxConnectionsPerPartition", "15");
        cfg.setProperty("bonecp.minConnectionsPerPartition", "2");
        cfg.setProperty("bonecp.acquireIncrement", "3");

        cfg.setProperty("hibernate.connection.username", "sa");
        cfg.setProperty("hibernate.connection.password", "");
        cfg.setProperty("hibernate.current_session_context_class", "thread");

        // Change configuration based on whether this is a new database or not
        File file = new File(this.engine.getFileSystem().getDatabaseDirectory() + "/DPADDatabase.h2.db");
        if (!file.exists()) {
            cfg.setProperty("hibernate.hbm2ddl.auto", "create");
            this.isNewDatabase = true;

        }
        else {
            cfg.setProperty("hibernate.hbm2ddl.auto", "validate");
            this.isNewDatabase = false;
        }

        // Build records for the database programatically
        List<Class<? extends Record>> annotatedClasses = DatabaseManager.getAnnotatedClasses();
        for (Class<? extends Record> clazz : annotatedClasses) {
            cfg.addAnnotatedClass(clazz);
        }

        return cfg;
    }
}
