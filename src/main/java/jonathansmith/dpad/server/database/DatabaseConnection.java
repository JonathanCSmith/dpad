package jonathansmith.dpad.server.database;

import org.hibernate.Session;

/**
 * Created by Jon on 27/05/2014.
 * <p/>
 * Represents an individual connection to the database. Usually locked to sessions or threads.
 */
public class DatabaseConnection {

    private final Session session;

    public DatabaseConnection(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return this.session;
    }

    public void beginTransaction() {
        this.session.beginTransaction();
    }

    public void commitTransaction() {
        this.session.getTransaction().commit();
    }

    public void rollbackTransaction() {
        this.session.getTransaction().rollback();
    }

    public void closeSession(boolean force) {
        if (force) {
            this.session.close();
        }

        else {
            if (this.session.isDirty()) {
                this.session.flush();
                this.session.getTransaction().commit();
            }

            this.session.close();
        }
    }
}
