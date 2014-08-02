package jonathansmith.dpad.server.engine.executor.startup;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import jonathansmith.dpad.common.database.record.DatabaseRecord;
import jonathansmith.dpad.common.database.record.Record;
import jonathansmith.dpad.common.engine.event.gui.ProgressBarUpdateEvent;
import jonathansmith.dpad.common.engine.executor.Task;

import jonathansmith.dpad.server.ServerEngine;
import jonathansmith.dpad.server.database.DatabaseManager;

/**
 * Created by Jon on 20/05/2014.
 * <p/>
 * Server database setup.
 */
public class SetupHibernateTask extends Task {

    private static final String TASK_NAME = "Server Hibernate Setup";

    private final ServerEngine engine;

    private GatherServerStartupPropertiesTask configTask;

    public SetupHibernateTask(ServerEngine engine, GatherServerStartupPropertiesTask configTask) {
        super(TASK_NAME, engine);

        this.engine = engine;
        this.configTask = configTask;
    }

    @Override
    public void runTask() {
        this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent(TASK_NAME, 0, 3, 0));

        // Build the hibernate Configuration
        this.loggingEngine.info("Beginning database initialisation", null);
        this.loggingEngine.trace("Building hibernate configuration", null);
        Configuration cfg = this.buildHibernateSessionConfiguration();

        this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent(TASK_NAME, 0, 3, 1));

        if (this.configTask.getServerSetupConfiguration().isNewServer()) {
            UUID suuuid = UUID.nameUUIDFromBytes(this.configTask.getServerSetupConfiguration().getSuperUsername().getBytes());
            if (new File(this.engine.getFileSystem().getDatabaseDirectory() + "/DPADDatabase_" + suuuid.toString()).exists()) {
                throw new UnsupportedOperationException(); // TODO MAYBE: Move to a better error
            }
        }

        this.loggingEngine.trace("Building hibernate service registry", null);
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties());

        // Initialise the hibernate session
        SessionFactory sessionFactory;
        try {
            this.loggingEngine.trace("Building hibernate session factory", null);
            sessionFactory = cfg.buildSessionFactory(builder.build());
            Session session = sessionFactory.openSession();
            session.close();
            this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent(TASK_NAME, 0, 3, 2));
        }

        catch (HibernateException ex) {
            this.loggingEngine.handleError("Connection to: " + this.engine.getFileSystem().getDatabaseDirectory() + " was rejected (i.e. wrong password) or unavailable", ex);
            return;
        }

        this.loggingEngine.trace("Assigning hibernate information to the database manager", null);
        DatabaseManager dbm = new DatabaseManager(this.engine, sessionFactory);
        this.engine.setDatabaseManager(dbm);
        this.loggingEngine.getEventThread().postEvent(new ProgressBarUpdateEvent(TASK_NAME, 0, 3, 3));
        this.loggingEngine.info("Database initialisation complete", null);
    }

    private Configuration buildHibernateSessionConfiguration() {
        Configuration cfg = new Configuration();

        // Always use this, we check for existence elsewhere
        cfg.setProperty("hibernate.hbm2ddl.auto", "update");

        // Build the Super User UUID
        UUID suuuid = UUID.nameUUIDFromBytes(this.configTask.getServerSetupConfiguration().getSuperUsername().getBytes());

        // Use h2 as that is our database type
        cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        cfg.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        cfg.setProperty("hibernate.connection.url", "jdbc:h2:file:" + this.engine.getFileSystem().getDatabaseDirectory() + "/DPADDatabase_" + suuuid.toString());

        // Use custom connection manager for better IO to the database
        cfg.setProperty("hibernate.connection.provider_class", "org.hibernate.c3p0.internal.C3P0ConnectionProvider");
        cfg.setProperty("hibernate.c3p0.acquire_increment", "1");
        cfg.setProperty("hibernate.c3p0.idle_test_period", "100");
        cfg.setProperty("hibernate.c3p0.max_size", "100");
        cfg.setProperty("hibernate.c3p0.max_statements", "0");
        cfg.setProperty("hibernate.c3p0.min_size", "10");
        cfg.setProperty("hibernate.c3p0.timeout", "10");

        // Super user properties
        cfg.setProperty("hibernate.connection.username", this.configTask.getServerSetupConfiguration().getSuperUsername());
        cfg.setProperty("hibernate.connection.password", this.configTask.getServerSetupConfiguration().getSuperUserPassword());
        cfg.setProperty("hibernate.current_session_context_class", "thread");

        // Build records for the database
        List<Class<? extends Record>> annotatedClasses = DatabaseRecord.getRecordClasses();
        for (Class<? extends Record> clazz : annotatedClasses) {
            cfg.addAnnotatedClass(clazz);
        }

        return cfg;
    }
}
