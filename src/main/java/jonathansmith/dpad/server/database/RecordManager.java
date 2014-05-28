package jonathansmith.dpad.server.database;

import org.hibernate.HibernateException;

import jonathansmith.dpad.api.APIAccess;
import jonathansmith.dpad.api.common.engine.IEngine;

import jonathansmith.dpad.common.database.record.DatabaseRecord;
import jonathansmith.dpad.common.database.record.Record;
import jonathansmith.dpad.common.database.util.RecordList;

/**
 * Created by Jon on 28/05/2014.
 * <p/>
 * Record manager parents. Implements common queries for database access
 */
public abstract class RecordManager<T extends Record> {

    protected final IEngine   engine;
    protected final RecordDAO database_access_object;

    private final Class<T> clazz;

    protected DatabaseConnection connection;

    public RecordManager(RecordDAO dao, Class clazz) {
        this.engine = APIAccess.getAPI().getServer();
        this.database_access_object = dao;
        this.clazz = clazz;
    }

    public void bindConnection(DatabaseConnection connectionFromUUID) {
        this.connection = connectionFromUUID;
    }

    public RecordList<Record> loadAll() {
        RecordList<Record> all = new RecordList<Record>();
        try {
            this.connection.beginTransaction();
            all = this.database_access_object.findAll(this.connection.getSession(), this.clazz);
            this.connection.commitTransaction();

        }
        catch (HibernateException ex) {
            this.engine.error("Database access error", ex);
        }

        return all;
    }

    public boolean save(T input) {
        boolean success = false;
        try {
            this.connection.beginTransaction();
            input = (T) this.database_access_object.merge(this.connection.getSession(), input);
            this.database_access_object.save(this.connection.getSession(), input);
            this.connection.commitTransaction();
            success = true;

        }
        catch (HibernateException ex) {
            this.engine.error("Database access error", ex);
        }

        return success;
    }

    public boolean saveNew(T input) {
        boolean success = false;
        try {
            this.connection.beginTransaction();
            this.database_access_object.save(this.connection.getSession(), input);
            this.connection.commitTransaction();
            success = true;

        }
        catch (HibernateException ex) {
            this.engine.error("Database access error", ex);
        }

        return success;
    }

    public T findByID(String uuid) {
        T out = null;
        try {
            this.connection.beginTransaction();
            out = (T) this.database_access_object.findByID(this.connection.getSession(), this.clazz, uuid);
            this.connection.commitTransaction();

        }
        catch (HibernateException ex) {
            this.engine.error("Database access error", ex);
        }

        return out;
    }

    public boolean deleteExisting(T input) {
        boolean success = false;
        try {
            this.connection.beginTransaction();
            this.database_access_object.delete(this.connection.getSession(), input);
            this.connection.commitTransaction();
            success = true;

        }
        catch (HibernateException ex) {
            this.engine.error("Database access error", ex);
            this.connection.rollbackTransaction();
        }

        return success;
    }

    public abstract <L extends RecordDAO> L getDAO();

    public abstract T loadChildrenForUpdate(T record, DatabaseRecord childType);
}
