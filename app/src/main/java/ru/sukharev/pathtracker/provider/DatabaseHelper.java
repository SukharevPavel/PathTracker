package ru.sukharev.pathtracker.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

import ru.sukharev.pathtracker.utils.orm.Path;
import ru.sukharev.pathtracker.utils.orm.Point;

/**
 * Helper for working with DAO
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "pathtracker.db";
    private final static int VERSION = 1;
    private static DatabaseHelper mDatabaseHelper;
    private PathDAO mPathDAO;
    private PointDAO mPointDAO;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    public static DatabaseHelper getInstance(Context ctx) {
        if (mDatabaseHelper == null) mDatabaseHelper = new DatabaseHelper(ctx);
        return mDatabaseHelper;
    }

    public PathDAO getPathDAO() throws SQLException {
        if (mPathDAO == null) mPathDAO = new PathDAO(getConnectionSource(), Path.class);
        return mPathDAO;
    }

    public PointDAO getPointDAO() throws SQLException {
        if (mPointDAO == null) mPointDAO = new PointDAO(getConnectionSource(), Point.class);
        return mPointDAO;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Path.class);
            TableUtils.createTable(connectionSource, Point.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Path.class, true);
            TableUtils.dropTable(connectionSource, Point.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        onCreate(database, connectionSource);
    }

    public static class PathDAO extends BaseDaoImpl<Path, String> {

        protected PathDAO(ConnectionSource connectionSource, Class<Path> dataClass) throws SQLException {
            super(connectionSource, dataClass);
        }

        public List<Path> getAllPaths() throws SQLException {
            return this.queryForAll();
        }
    }

    public static class PointDAO extends BaseDaoImpl<Point, Integer> {

        protected PointDAO(ConnectionSource connectionSource, Class<Point> dataClass) throws SQLException {
            super(connectionSource, dataClass);
        }

        public List<Point> getPointByPath(String name) throws SQLException {
            QueryBuilder<Point, Integer> queryBuilder = queryBuilder();
            queryBuilder.where().eq(Path.COLUMN_NAME, name);
            PreparedQuery<Point> preparedQuery = queryBuilder.prepare();
            return query(preparedQuery);
        }
    }
}
