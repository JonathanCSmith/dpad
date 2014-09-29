package jonathansmith.dpad.server.database.record.experiment;

import org.hibernate.Query;
import org.hibernate.Session;

import jonathansmith.dpad.api.database.ExperimentRecord;

import jonathansmith.dpad.server.database.RecordDAO;

/**
 * Created by Jon on 28/08/2014.
 * <p/>
 * Experiment database access object
 */
public class ExperimentRecordDAO extends RecordDAO<ExperimentRecord, String> {

    public ExperimentRecord findByName(Session session, String experimentName) {
        String sql = "SELECT p FROM Experiment p WHERE p.experimentName = :name";
        Query query = session.createQuery(sql).setParameter("name", experimentName);
        return this.findOne(query);
    }
}
