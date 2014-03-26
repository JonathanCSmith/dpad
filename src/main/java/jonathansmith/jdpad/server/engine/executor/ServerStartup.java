package jonathansmith.jdpad.server.engine.executor;

import java.io.File;
import java.net.SocketAddress;
import java.util.List;

import org.apache.log4j.Level;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import jonathansmith.jdpad.common.database.DatabaseManager;
import jonathansmith.jdpad.common.database.record.Record;
import jonathansmith.jdpad.common.engine.Engine;
import jonathansmith.jdpad.common.engine.executor.Executor;
import jonathansmith.jdpad.common.engine.util.log.LoggerFactory;
import jonathansmith.jdpad.common.engine.util.log.LoggingLevel;
import jonathansmith.jdpad.common.platform.Platform;

import jonathansmith.jdpad.server.ServerEngine;
import jonathansmith.jdpad.server.network.ServerNetworkManager;

import jonathansmith.jdpad.JDPAD;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Server startup executable
 */
public class ServerStartup extends Executor {

    private final SocketAddress address;

    public ServerStartup(Engine engine, SocketAddress address) {
        super(engine);

        this.address = address;
    }

    @Override
    public void execute() {
        // Bind the net logger to our loggers
        LoggerFactory.getInstance().getLogger(this.engine, "org.jboss.logging", new LoggingLevel(Level.DEBUG, Level.WARN, Level.DEBUG, Level.INFO));
        LoggerFactory.getInstance().getLogger(this.engine, "io.netty", new LoggingLevel(Level.DEBUG, Level.WARN, Level.DEBUG, Level.INFO));
        LoggerFactory.getInstance().getLogger(this.engine, "org.hibernate", new LoggingLevel(Level.DEBUG, Level.WARN, Level.DEBUG, Level.INFO));

        // Build the hibernate Configuration
        this.engine.info("Beginning database initialisation", null);
        Configuration cfg = this.buildHibernateSessioncfguration();
        boolean isNewDatabase = cfg.getProperty("hibernate.hbm2ddl.auto").contentEquals("create");
        ServiceRegistry registry = new ServiceRegistryBuilder().applySettings(cfg.getProperties()).buildServiceRegistry();

        // Initialise the hibernate session
        SessionFactory sessionFactory;
        try {
            sessionFactory = cfg.buildSessionFactory(registry);
            sessionFactory.openSession();

            if (isNewDatabase) {
                this.engine.warn("", null);
                this.engine.warn("==========================================================", null);
                this.engine.warn("Retrying database creation for a second time as it is new!", null);
                this.engine.warn("==========================================================", null);
                this.engine.warn("", null);

                sessionFactory.close();
                sessionFactory = cfg.buildSessionFactory(registry);
                sessionFactory.openSession();
            }
        }

        catch (HibernateException ex) {
            this.engine.handleError("Connection to: " + this.engine.getFileSystem().getDatabaseDirectory() + " was rejected or unavailable", ex, true);
            return;
        }

        DatabaseManager dbm = new DatabaseManager(sessionFactory);
        this.engine.info("Database initialisation complete", null);

        // Server Network Manager
        this.engine.info("Beginning network initialisation", null);
        ServerNetworkManager sNM = new ServerNetworkManager(this.engine, this.address, JDPAD.getInstance().getPlatformSelection() == Platform.LOCAL);

        try {
            sNM.buildBootstap();
        }

        catch (Exception ex) {
            this.engine.handleError("Could not build bootstrap during network initialisation.", ex, true);
            return;
        }
        // TODO: sNM.start();
        this.engine.info("Network initialisation complete", null);

        // Hand off information to engine
        ((ServerEngine) this.engine).setDatabaseManager(dbm);
        this.engine.setNetworkManager(sNM);

        // Notify that executor has finished
        ((ServerEngine) this.engine).setServerFinishedSetup();
        this.setFinished();
    }

    @Override
    public void shutdown(boolean forceShutdownFlag) {

    }

    private Configuration buildHibernateSessioncfguration() {
        Configuration cfg = new Configuration();
        cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        cfg.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        cfg.setProperty("hibernate.connection.url", "jdbc:h2:file:" + this.engine.getFileSystem().getDatabaseDirectory() + "/JDPADDatabase");

        // Use custom connection manager for better IO to the database
        cfg.setProperty("connection.provider.provider_class", "com.jolbox.bonecp.provider.BoneCPConnectionProvider");
        cfg.setProperty("bonecp.partitionCount", "3");
        cfg.setProperty("bonecp.maxConnectionsPerPartition", "15");
        cfg.setProperty("bonecp.minConnectionsPerPartition", "2");
        cfg.setProperty("bonecp.acquireIncrement", "3");

        cfg.setProperty("hibernate.connection.username", "sa");
        cfg.setProperty("hibernate.connection.password", "");
        cfg.setProperty("hibernate.current_session_context_class", "thread");

        File file = new File(this.engine.getFileSystem().getDatabaseDirectory() + "/JDPADDatabase.h2.db");
        if (!file.exists()) {
            cfg.setProperty("hibernate.hbm2ddl.auto", "create");

        }
        else {
            cfg.setProperty("hibernate.hbm2ddl.auto", "validate");
        }

        List<Class<? extends Record>> annotatedClasses = DatabaseManager.getAnnotatedClasses();
        for (Class<? extends Record> clazz : annotatedClasses) {
            cfg.addAnnotatedClass(clazz);
        }

        return cfg;
    }
}
