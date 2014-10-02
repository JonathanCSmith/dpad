package jonathansmith.dpad.server.database.record.dataset;

import javax.persistence.NonUniqueResultException;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Property;

import jonathansmith.dpad.api.database.DatasetRecord;

import jonathansmith.dpad.server.database.DatabaseConnection;
import jonathansmith.dpad.server.database.RecordManager;

/**
 * Created by Jon on 29/09/2014.
 */
public class DatasetRecordManager extends RecordManager<DatasetRecord> {

    private static DatasetRecordManager instance;

    private DatasetRecordManager() {
        super(new DatasetRecordDAO(), DatasetRecord.class);
    }

    public static DatasetRecordManager getInstance() {
        if (instance == null) {
            instance = new DatasetRecordManager();
        }

        return instance;
    }

    public DatasetRecordDAO getDAO() {
        return (DatasetRecordDAO) this.database_access_object;
    }

    public DatasetRecord fetchFull(DatabaseConnection connection, DatasetRecord record) {
        DatasetRecord match = null;
        try {
            connection.beginTransaction();
            final Criteria criteria = connection.getSession().createCriteria(DatasetRecord.class);
            criteria.setFetchMode("Data", FetchMode.JOIN);
            criteria.add(Property.forName("UUID").eq(record.getUUID()));
            match = (DatasetRecord) criteria.uniqueResult();
            connection.commitTransaction();
        }

        catch (NonUniqueResultException ex) {
            this.engine.error("Query resulted in a non-unique answer", ex);
        }

        catch (HibernateException ex) {
            this.engine.error("Database access error", ex);
        }

        return record;
    }
}
