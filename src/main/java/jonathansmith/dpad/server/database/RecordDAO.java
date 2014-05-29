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
@SuppressWarnings("unchecked")
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
        return (List<T>) query.list();
    }

    public T findOne(Query query) {
        return (T) query.uniqueResult();
    }

    public T findByID(Session sess, Class clazz, String uuid) {
        return (T) sess.get(clazz, uuid);
    }

    public RecordList<T> findAll(Session sess, Class clazz) {
        Query query = sess.createQuery("from " + clazz.getName());

        ArrayList<T> array = (ArrayList<T>) query.list();
        RecordList<T> recordList = new RecordList<T>();
        for (T arrayEntry : array) {
            recordList.add(arrayEntry);
        }

        return recordList;
    }
}
