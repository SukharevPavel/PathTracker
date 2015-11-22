package ru.sukharev.pathtracker.utils.orm;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.util.List;

import ru.sukharev.pathtracker.provider.DatabaseHelper;
import ru.sukharev.pathtracker.utils.orm.MapPath;
import ru.sukharev.pathtracker.utils.orm.MapPoint;

/**
 * Loader for asynchronous processing database query.
 */
public class OrmLoader extends AsyncTaskLoader<List<?>> {

    DatabaseHelper mHelper;
    public final static int GET_ALL_PATHS = 1;
    public final static int GET_POINT_BY_PATH = 2;

    private String mTable;
    private String mSelection;
    private String[] mSelectionArgs;
    private String mGroupBy;


    public OrmLoader(Context context, DatabaseHelper helper, String table, String where,
                     String[] whereArgs, String groupBy) {
        super(context);
        mTable = table;
        mSelection = where;
        mSelectionArgs = whereArgs;
        mGroupBy = groupBy;
        mHelper = helper;
    }

    @Override
    public List<?> loadInBackground() {
        try {
            switch (mTable) {
                case MapPath.TABLE_NAME:
                    return mHelper.getPathDAO().getList(mSelection, mSelectionArgs, mGroupBy);
                case MapPoint.TABLE_NAME:
                    return mHelper.getPointDAO().getList(mSelection, mSelectionArgs, mGroupBy);
                default:
                    return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
