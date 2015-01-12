package jonathansmith.dpad.server.database.record.user;

import org.hibernate.Query;
import org.hibernate.Session;

import jonathansmith.dpad.api.database.UserRecord;

import jonathansmith.dpad.server.database.RecordDAO;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * DAO for User records
 */
public class UserRecordDAO extends RecordDAO<UserRecord, String> {

    public UserRecord findByName(Session session, String username) {
        String sql = "SELECT p FROM UserRecord p WHERE p.username = :name";
        Query query = session.createQuery(sql).setParameter("name", username);
        return this.findOne(query);
    }
}
