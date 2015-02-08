package jonathansmith.dpad.server.database;

import org.hibernate.HibernateException;

import jonathansmith.dpad.api.database.DatabaseRecord;
import jonathansmith.dpad.api.database.Record;

import jonathansmith.dpad.common.database.util.RecordList;
import jonathansmith.dpad.common.platform.Platform;

import jonathansmith.dpad.server.ServerEngine;

import jonathansmith.dpad.DPAD;

/**
 * Created by Jon on 28/05/2014.
 * <p/>
 * Record manager parents. Implements common queries for database access
 */
public abstract class RecordManager<T extends Record> {

    protected final ServerEngine engine;
    protected final RecordDAO    database_access_object;

    private final Class<T> clazz;

    public RecordManager(RecordDAO dao, Class<T> clazz) {
        this.engine = (ServerEngine) DPAD.getInstance().getEngine(Platform.SERVER);
        this.database_access_object = dao;
        this.clazz = clazz;
    }

    public RecordList<T> loadAll(DatabaseConnection connection) {
        RecordList<T> all = new RecordList<T>();
        try {
            connection.beginTransaction();
            all = this.getDAO().findAll(connection.getSession(), this.clazz);
            connection.commitTransaction();

        }
        catch (HibernateException ex) {
            this.engine.error("Database access error", ex);
            connection.rollbackTransaction();
        }

        return all;
    }

    public boolean save(DatabaseConnection connection, T input) {
        boolean success = false;
        try {
            connection.beginTransaction();
            input = this.getDAO().merge(connection.getSession(), input);
            this.getDAO().save(connection.getSession(), input);
            connection.commitTransaction();
            success = true;

        }
        catch (HibernateException ex) {
            this.engine.error("Database access error", ex);
            connection.rollbackTransaction();
        }

        return success;
    }

    public boolean saveNew(DatabaseConnection connection, T input) {
        boolean success = false;
        try {
            connection.beginTransaction();
            this.getDAO().save(connection.getSession(), input);
            connection.commitTransaction();
            success = true;

        }
        catch (HibernateException ex) {
            this.engine.error("Database access error", ex);
            connection.rollbackTransaction();
        }

        return success;
    }

    public T findByID(DatabaseConnection connection, String uuid) {
        T out = null;
        try {
            connection.beginTransaction();
            out = this.getDAO().findByID(connection.getSession(), this.clazz, uuid);
            connection.commitTransaction();

        }

        catch (HibernateException ex) {
            this.engine.error("Database access error", ex);
            connection.rollbackTransaction();
        }

        return out;
    }

    public boolean deleteExisting(DatabaseConnection connection, T input) {
        boolean success = false;
        try {
            connection.beginTransaction();
            this.getDAO().delete(connection.getSession(), input);
            connection.commitTransaction();
            success = true;

        }
        catch (HibernateException ex) {
            this.engine.error("Database access error", ex);
            connection.rollbackTransaction();
        }

        return success;
    }

    public T loadChildrenForUpdate(DatabaseConnection connection, T record, DatabaseRecord childType) {
        return null;
    }

    public abstract <L extends RecordDAO<T, String>> L getDAO();
}
