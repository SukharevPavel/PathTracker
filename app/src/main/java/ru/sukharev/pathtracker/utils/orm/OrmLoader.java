package ru.sukharev.pathtracker.utils.orm;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.sql.SQLException;
import java.util.List;

import ru.sukharev.pathtracker.provider.DatabaseHelper;

/**
 * Loader for asynchronous processing database query.
 */
public class OrmLoader extends AsyncTaskLoader<List<?>> {

    DatabaseHelper mHelper;
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
