package jonathansmith.dpad.server.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import jonathansmith.dpad.common.database.record.Record;
import jonathansmith.dpad.common.database.util.RecordList;

/**
 * Created by Jon on 28/05/2014.
 * <p/>
 * Parent class for all database access objects
 */
public abstract class RecordDAO<T extends Record, ID extends Serializable> {

    public void save(Session sess, T entity) {
        sess.saveOrUpdate(entity);
    }

    public T merge(Session sess, T entity) {
        return (T) sess.merge(entity);
    }

    public void delete(Session sess, T entity) {
        sess.delete(entity);
    }

    public List<T> findMany(Query query) {
        List<T> t = (List<T>) query.list();
        return t;
    }

    public T findOne(Query query) {
        T t = (T) query.uniqueResult();
        return t;
    }

    public T findByID(Session sess, Class clazz, String uuid) {
        T t = (T) sess.get(clazz, uuid);
        return t;
    }

    public RecordList<T> findAll(Session sess, Class clazz) {
        Query query = sess.createQuery("from " + clazz.getName());
        ArrayList<T> array = (ArrayList<T>) query.list();
        RecordList<T> T = new RecordList<T>();

        for (int i = 0; i < array.size(); i++) {
            T.add(array.get(i));
        }

        return T;
    }
}
