package jonathansmith.jdpad.common.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.SessionFactory;

import jonathansmith.jdpad.common.database.record.Record;

/**
 * Created by Jon on 26/03/14.
 * <p/>
 * Central point for database management
 */
public class DatabaseManager {

    private static final List<Class<? extends Record>> ANNOTATED_CLASSES = new ArrayList<Class<? extends Record>>(
            Arrays.asList(
                    Record.class // TODO: Do not register this :P
            )
    );

    public static List<Class<? extends Record>> getAnnotatedClasses() {
        return ANNOTATED_CLASSES;
    }

    private final SessionFactory sessionFactory;

    public DatabaseManager(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void shutdown(boolean force) {

    }
}
