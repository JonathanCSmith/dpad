package jonathansmith.dpad.server.database;

import java.util.HashMap;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.server.ServerEngine;
import jonathansmith.dpad.server.network.session.ServerNetworkSession;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Central point for database management
 */
public class DatabaseManager {

    private static DatabaseManager instance;

    private final HashMap<UUID, DatabaseConnection> connections = new HashMap<UUID, DatabaseConnection>();

    private final IEngine        engine;
    private final SessionFactory sessionFactory;

    private DatabaseConnection serverConnection;

    private boolean isShuttingDown = false;

    public DatabaseManager(IEngine engine, SessionFactory sessionFactory) {
        if (instance != null) {
            throw new RuntimeException("Cannot create multiple Database Managers!");
        }

        this.engine = engine;
        this.sessionFactory = sessionFactory;

        instance = this;
    }

    public static void buildServerNetworkSessionDatabaseConnection(ServerNetworkSession serverNetworkSession) {
        if (instance.isShuttingDown) {
            return;
        }

        if (instance.connections.containsKey(serverNetworkSession.getEngineAssignedUUID())) {
            instance.engine.error("The session " + serverNetworkSession.getEngineAssignedUUID() + "already has a reserved database connection", null);
        }

        else {
            Session session = instance.sessionFactory.openSession();
            DatabaseConnection connection = new DatabaseConnection(session);
            instance.connections.put(serverNetworkSession.getEngineAssignedUUID(), connection);
            serverNetworkSession.assignDatabaseConnection(connection);
        }
    }

    public void buildServerConnection(ServerEngine engine) {
        this.serverConnection = new DatabaseConnection(this.sessionFactory.openSession());
        engine.assignServerConnection(new ServerDatabaseConnection(engine, this.serverConnection));
    }

    public void shutdown(boolean force) {
        this.isShuttingDown = true;

        for (DatabaseConnection connection : this.connections.values()) {
            try {
                connection.closeSession(force);
            }

            catch (HibernateException ex) {
                this.engine.error("Could not close database connection", ex);
            }
        }

        try {
            this.serverConnection.closeSession(force);
        }

        catch (HibernateException ex) {
            this.engine.error("Error closing server side connection on shutdown!", ex);
        }
    }
}
