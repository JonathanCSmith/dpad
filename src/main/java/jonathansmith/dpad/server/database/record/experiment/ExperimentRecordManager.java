package jonathansmith.dpad.server.database.record.experiment;

import javax.persistence.NonUniqueResultException;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Property;

import jonathansmith.dpad.api.database.ExperimentRecord;

import jonathansmith.dpad.server.database.DatabaseConnection;
import jonathansmith.dpad.server.database.RecordManager;

/**
 * Created by Jon on 28/08/2014.
 * <p/>
 * Manager for experiment records
 */
public class ExperimentRecordManager extends RecordManager<ExperimentRecord> {

    private static ExperimentRecordManager instance;

    private ExperimentRecordManager() {
        super(new ExperimentRecordDAO(), ExperimentRecord.class);
    }

    public static ExperimentRecordManager getInstance() {
        if (instance == null) {
            instance = new ExperimentRecordManager();
        }

        return instance;
    }

    public ExperimentRecord findByExperimentName(DatabaseConnection connection, String experimentName) {
        ExperimentRecord record = null;
        try {
            connection.beginTransaction();
            ;
            record = this.getDAO().findByName(connection.getSession(), experimentName);
            connection.commitTransaction();
        }

        catch (NonUniqueResultException ex) {
            this.engine.error("Query resulted in a non unique answer", ex);
        }

        catch (HibernateException ex) {
            this.engine.error("Database access error", ex);
        }

        return record;
    }

    @Override
    public ExperimentRecordDAO getDAO() {
        return (ExperimentRecordDAO) this.database_access_object;
    }

    public ExperimentRecord fetchDatasets(DatabaseConnection connection, ExperimentRecord record) {
        ExperimentRecord result = null;
        try {
            connection.beginTransaction();
            final Criteria criteria = connection.getSession().createCriteria(ExperimentRecord.class);
            criteria.setFetchMode("datasets", FetchMode.JOIN);
            criteria.add(Property.forName("UUID").eq(record.getUUID()));
            result = (ExperimentRecord) criteria.uniqueResult();
            connection.commitTransaction();
        }

        catch (NonUniqueResultException ex) {
            this.engine.error("Query resulted in a non-unique answer", ex);
        }

        catch (HibernateException ex) {
            this.engine.error("Database access error", ex);
        }

        return result;
    }
}
