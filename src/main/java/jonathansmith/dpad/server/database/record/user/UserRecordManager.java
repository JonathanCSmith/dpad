package jonathansmith.dpad.server.database.record.user;

import javax.persistence.NonUniqueResultException;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Property;

import jonathansmith.dpad.api.database.UserRecord;

import jonathansmith.dpad.server.database.DatabaseConnection;
import jonathansmith.dpad.server.database.RecordManager;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Manager for User Records
 */
public class UserRecordManager extends RecordManager<UserRecord> {

    private static UserRecordManager instance;

    private UserRecordManager() {
        super(new UserRecordDAO(), UserRecord.class);
    }

    public static UserRecordManager getInstance() {
        if (instance == null) {
            instance = new UserRecordManager();
        }

        return instance;
    }

    @Override
    public UserRecordDAO getDAO() {
        return (UserRecordDAO) this.database_access_object;
    }

    public UserRecord findByUsername(DatabaseConnection connection, String username) {
        UserRecord record = null;
        try {
            connection.beginTransaction();
            record = this.getDAO().findByName(connection.getSession(), username);
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

    public UserRecord fetchExperiments(DatabaseConnection connection, UserRecord record) {
        UserRecord result = null;
        try {
            connection.beginTransaction();
            final Criteria criteria = connection.getSession().createCriteria(UserRecord.class);
            criteria.setFetchMode("Experiments", FetchMode.JOIN);
            criteria.add(Property.forName("UUID").eq(record.getUUID()));
            result = (UserRecord) criteria.uniqueResult();
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
