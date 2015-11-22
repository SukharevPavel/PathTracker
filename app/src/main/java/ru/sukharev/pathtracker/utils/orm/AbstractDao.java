package ru.sukharev.pathtracker.utils.orm;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

/**
 * Abstract implementation of DAO, including primitive query methods
 */
public abstract class AbstractDao<T, ID> extends BaseDaoImpl<T, ID> {

    protected AbstractDao(ConnectionSource connectionSource, Class<T> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<T> getList(String where, String[] whereArgs, String groupBy) throws SQLException {
        if (where == null) return getAll();
        QueryBuilder<T, ID> queryBuilder = queryBuilder();
        queryBuilder.where().in(where, whereArgs);
        if (groupBy != null) queryBuilder.groupBy(groupBy);
        PreparedQuery<T> preparedQuery = queryBuilder.prepare();
        return query(preparedQuery);
    }

    public List<T> getAll() throws SQLException {
        return this.queryForAll();
    }
}
