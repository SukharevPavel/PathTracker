package ru.sukharev.pathtracker.utils;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.ForeignCollection;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import ru.sukharev.pathtracker.R;
import ru.sukharev.pathtracker.provider.DatabaseHelper;
import ru.sukharev.pathtracker.utils.orm.MapPath;
import ru.sukharev.pathtracker.utils.orm.MapPoint;

/**
 * Presenter element which handle interaction between View {@link ru.sukharev.pathtracker.ui.MapActivity}
 * and model: Service (@link ru,sukharev.pathtracker.service.TrackingService} and Content Provider
 */
public class MapHelper implements GoogleApiClient.ConnectionCallbacks,
        com.google.android.gms.location.LocationListener {

    private final static int SQL_SUCCESS = 0;
    private final static int SQL_FAIL = 1;
    private final static int SQL_ERROR = 2;

    private static final String TAG = "MapHelper.java";
    private final List<MapPoint> mPoints;
    private Context mContext;
    private boolean isServiceStarted = false;
    private MapHelperListener mListener;
    private SQLInteractionListener mSQLListener;
    private DatabaseHelper mDatabaseHelper;
    private LocationRequest mLocationRequest;

    private boolean isWaitingForStartPoint;

    private GoogleApiClient mGoogleApiClient;
    private static MapHelper sHelper;

    public static MapHelper getInstance(Context ctx){
        if (sHelper == null) sHelper = new MapHelper(ctx);
        return sHelper;
    }

    private MapHelper(@NonNull Context ctx) {
        mContext = ctx;
        mPoints = new CopyOnWriteArrayList<>();
        mDatabaseHelper = DatabaseHelper.getInstance(ctx);
        configureGoogleApiClient();
    }

    private void configureGoogleApiClient() {
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API);
        mGoogleApiClient = builder.build();
    }


    private void stopTracking() {
        isServiceStarted = false;
        if (!mPoints.isEmpty()) {
            mListener.onEndPoint(setEndPoint());
        }
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        mListener.onServiceStop();
    }

    public void setListeners(Activity activity) {
        try {
            mListener = (MapHelperListener) activity;
        } catch (ClassCastException e) {
            mListener = null;
        }
        try {
            mSQLListener = (SQLInteractionListener) activity;
        } catch (ClassCastException e) {
            mSQLListener = SQLInteractionListener.dummySQLListener;
        }
    }

    public void startService() {
        setUpLocationRequest();
        isWaitingForStartPoint = true;
        mGoogleApiClient.connect();

    }

    private void setUpLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(TimeUnit.SECONDS.toMillis(
                Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(mContext).
                        getString(mContext.getString(R.string.pref_key_measure_interval),
                                mContext.getString(R.string.pref_key_measure_interval_def)))));
        mLocationRequest.setFastestInterval(mLocationRequest.getInterval());
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void stopService() {
        stopTracking();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    public void clearData() {
        isWaitingForStartPoint = true;
        mPoints.clear();
    }

    public void saveToDatabase(String name, double distance, double avgSpeed) {
        if (!mPoints.isEmpty()) {

            MapPath mapPath = new MapPath(name,
                    mPoints.get(0).getTime(),
                    mPoints.get(mPoints.size() - 1).getTime(),
                    distance,
                    avgSpeed);
            new AsynkSaveToDatabaseTask().execute(mapPath);


        } else mSQLListener.onFail(SQLInteractionListener.FAIL_CODES.LIST_EMPTY);
    }

    public boolean isServiceStarted() {
        return isServiceStarted;
    }

    public void getList() {
        if (mListener != null)
            mListener.onNewPointList(mPoints);
    }


    private void addPointToList(MapPoint point) {
        mPoints.add(point);
    }


    private void processPoint(MapPoint newMapPoint) {
        newMapPoint.setIsStartPoint(isWaitingForStartPoint);
        notifyUI(newMapPoint);
        addPointToList(newMapPoint);
    }


    private MapPoint setEndPoint() {
        MapPoint point = mPoints.get(mPoints.size() - 1);
        point.setIsEndPoint(true);
        mPoints.set(mPoints.size() - 1, point);
        return point;
    }


    private void notifyUI(MapPoint newMapPoint) {
        if (isWaitingForStartPoint) {
            mListener.onStartPoint(newMapPoint);
            isWaitingForStartPoint = false;
        }
        else mListener.onNewPoint(newMapPoint);
    }

    @Override
    public void onConnected(Bundle bundle) {
        isServiceStarted = true;
        mListener.onServiceStart();

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        stopTracking();
    }


    @Override
    public void onLocationChanged(Location location) {
        MapPoint newMapPoint = new MapPoint(location);
        processPoint(newMapPoint);
    }

    public void updatePath(final MapPath path, final String newName) {
        try {
            if (path != null) {
                DatabaseHelper.PathDAO dao = mDatabaseHelper.getPathDAO();
                path.setName(newName);
                dao.update(path);
                mSQLListener.onSuccess();
            } else mSQLListener.onFail(SQLInteractionListener.FAIL_CODES.PATH_IS_NULL);
        } catch (SQLException e) {
            mSQLListener.onError(SQLInteractionListener.ERROR_CODES.UPDATE);
        }
    }

    public void deletePath(MapPath path) {
        new AsynkDeleteFromDatabaseTask().execute(path);
    }


    public interface MapHelperListener {

        void onServiceStart();

        void onServiceStop();

        void onNewPoint(MapPoint newPoint);

        void onNewPointList(List<MapPoint> list);

        void onStartPoint(MapPoint startPoint);

        void onEndPoint(MapPoint endPoint);

    }

    public interface SQLInteractionListener {

        SQLInteractionListener dummySQLListener = new SQLInteractionListener() {
            @Override
            public void onError(int code) {
            }

            @Override
            public void onSuccess() {
            }

            @Override
            public void onFail(int code) {
            }
        };

        void onError(int errorCode);

        void onSuccess();

        void onFail(int failCode);

        class ERROR_CODES {
            public static final int ADD = 1;
            public static final int UPDATE = 2;
            public static final int DELETE = 3;
        }

        class FAIL_CODES {
            public static final int ADD = 1;
            public static final int LIST_EMPTY = 3;
            public static final int PATH_IS_NULL = 4;
        }

    }


    private class AsynkDeleteFromDatabaseTask extends AsyncTask<MapPath, Void, Void> {

        @Override
        protected Void doInBackground(MapPath... params) {
            MapPath mapPath = params[0];
            ForeignCollection<MapPoint> points = mapPath.getPoints();
            CloseableIterator<MapPoint> iterator = points.closeableIterator();
            try {
                mDatabaseHelper.getPathDAO().delete(mapPath);
                publishProgress();
                while (iterator.hasNext()) {
                    MapPoint point = iterator.next();
                    mDatabaseHelper.getPointDAO().delete(point);
                }
            } catch (SQLException e) {
                mSQLListener.onError(SQLInteractionListener.ERROR_CODES.DELETE);
            } finally {
                try {
                    iterator.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        public void onProgressUpdate(Void... params) {
            mSQLListener.onSuccess();
        }
    }


    private class AsynkSaveToDatabaseTask extends AsyncTask<MapPath, Void, Integer> {


        @Override
        protected Integer doInBackground(MapPath... params) {
            if (!mPoints.isEmpty()) {
                try {
                    MapPath mapPath = params[0];
                    setPathToPoints(mapPath);
                    mDatabaseHelper.getPathDAO().create(mapPath);
                    saveAllPoints();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return SQL_ERROR;
                }
                return SQL_SUCCESS;
            }
            return SQL_FAIL;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            switch (result) {
                case SQL_SUCCESS:
                    mSQLListener.onSuccess();
                    break;
                case SQL_FAIL:
                    mSQLListener.onFail(SQLInteractionListener.FAIL_CODES.ADD);
                    break;
                case SQL_ERROR:
                    mSQLListener.onError(SQLInteractionListener.ERROR_CODES.ADD);
                    break;
                default:
                    break;
            }
        }

        private void setPathToPoints(MapPath mapPath) {
            for (MapPoint mapPoint : mPoints)
                mapPoint.setPath(mapPath);
        }

        private void saveAllPoints() throws SQLException {
            DatabaseHelper.PointDAO dao = mDatabaseHelper.getPointDAO();
            MapPoint endPoint = mPoints.get(mPoints.size() - 1);
            boolean flagOfLastPoint = false;
            for (MapPoint mapPoint : mPoints) {
                //to make a nice saved path with borders, where it end and where it stops even
                //if we save the path while tracking is not over
                if (mapPoint.equals(endPoint) && !mapPoint.isEndPoint()) {
                    flagOfLastPoint = true;
                    mapPoint.setIsEndPoint(true);
                }
                dao.create(mapPoint);
                if (flagOfLastPoint) {
                    flagOfLastPoint = false;
                    mapPoint.setIsEndPoint(false);
                }
            }
        }
    }


}
