package jonathansmith.dpad.server.database;

import java.util.HashMap;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.common.database.record.DatabaseRecord;
import jonathansmith.dpad.common.database.record.Record;

import jonathansmith.dpad.server.database.record.analysingplugin.AnalysingPluginRecordManager;
import jonathansmith.dpad.server.database.record.loadingplugin.LoadingPluginRecordManager;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Central point for database management
 */
public class DatabaseManager {

    private final HashMap<String, DatabaseConnection> connections = new HashMap<String, DatabaseConnection>();

    private final IEngine        engine;
    private final SessionFactory sessionFactory;

    private boolean isShuttingDown = false;

    public DatabaseManager(IEngine engine, SessionFactory sessionFactory) {
        this.engine = engine;
        this.sessionFactory = sessionFactory;
    }

    public void shutdown(boolean force) {
        this.isShuttingDown = true;

        for (DatabaseConnection connection : this.connections.values()) {
            try {
                if (force) {
                    connection.getSession().close();
                }

                else {
                    if (connection.getSession().isDirty()) {
                        connection.getSession().flush();
                        connection.commitTransaction();
                    }

                    connection.getSession().close();
                }
            }

            catch (HibernateException ex) {
                this.engine.error("Could not close database connection", ex);
            }
        }
    }

    public RecordManager getRecordManager(Class<? extends Record> clazz, String uuid) {
        if (this.isShuttingDown) {
            return null;
        }

        DatabaseRecord record = DatabaseRecord.getRecordTypeFromClass(clazz);

        RecordManager manager;
        switch (record) {
            case LOADING_PLUGIN:
                manager = LoadingPluginRecordManager.getInstance();
                manager.bindConnection(this.getConnectionFromUUID(uuid));
                break;

            case ANALYSING_PLUGIN:
                manager = AnalysingPluginRecordManager.getInstance();
                manager.bindConnection(this.getConnectionFromUUID(uuid));
                break;

            default:
                manager = null;
        }

        return manager;
    }

    private DatabaseConnection getConnectionFromUUID(String uuid) {
        if (this.isShuttingDown) {
            return null;
        }

        if (this.connections.containsKey(uuid)) {
            return this.connections.get(uuid);
        }

        else {
            Session session = this.sessionFactory.openSession();
            DatabaseConnection connection = new DatabaseConnection(session);
            this.connections.put(uuid, connection);
            return connection;
        }
    }
}
