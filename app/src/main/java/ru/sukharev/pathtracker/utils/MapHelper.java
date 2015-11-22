package ru.sukharev.pathtracker.utils;

import android.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import ru.sukharev.pathtracker.provider.DatabaseHelper;
import ru.sukharev.pathtracker.service.TrackingService;
import ru.sukharev.pathtracker.utils.orm.MapPath;
import ru.sukharev.pathtracker.utils.orm.MapPoint;
import ru.sukharev.pathtracker.utils.orm.OrmLoader;

/**
 * Presenter element which handle interaction between View {@link ru.sukharev.pathtracker.ui.MapActivity}
 * and model: Service (@link ru,sukharev.pathtracker.service.TrackingService} and Content Provider
 */
public class MapHelper implements TrackingService.TrackingListener, LoaderCallbacks {

    private static final String TAG = "MapHelper.java";
    private Context mContext;
    private List<MapPoint> mPoints;
    private boolean isServiceStarted = false;
    private TrackingService mService;
    private MapHelperListener mListener;

    private final static int PATH_LOADER_ID = 1;
    private final static int POINT_LOADER_ID = 2;

    public final static String EXTRA_POINT_LOADER_PATH_NAME = "pathName";

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "service connected");
            initTracking(service);
        }


        //never called
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "service disconnected");
            stopTracking();
        }
    };
    private DatabaseHelper mDatabaseHelper;

    public MapHelper(@NonNull Context ctx) {
        mContext = ctx;
        mPoints = new LinkedList<>();
        mDatabaseHelper = DatabaseHelper.getInstance(ctx);
    }

    private void initTracking(IBinder service){
        isServiceStarted = true;
        mService = ((TrackingService) ((TrackingService.LocalBinder) service).getService());
        mService.setListener(MapHelper.this);
        mListener.onServiceStart();
    }

    private void stopTracking(){
        isServiceStarted = false;
        mService = null;
        mListener.onServiceStop();
    }

    public void setListeners(MapHelperListener listener) {
        mListener = listener;
    }

    public void startService() {
        Log.i(TAG, "startService()");
        clearData();
        Intent intent = new Intent(mContext, TrackingService.class);
        mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void stopService() {
        mContext.unbindService(connection);
        stopTracking();
    }

    public void clearData(){
        mPoints.clear();
    }

    public boolean saveToDatabase(String name) throws SQLException {
        if (!mPoints.isEmpty()) {
            MapPath mapPath = new MapPath(name,
                    mPoints.get(0).getTime(),
                    mPoints.get(mPoints.size()-1).getTime());
            mDatabaseHelper.getPathDAO().create(mapPath);
            setPathToPoints(mapPath);
            saveAllPoints();
            return true;

        }
        return false;
    }

    private void setPathToPoints(MapPath mapPath){
        for (MapPoint mapPoint : mPoints)
                mapPoint.setPath(mapPath);
    }

    private void saveAllPoints() throws SQLException {
        DatabaseHelper.PointDAO dao = mDatabaseHelper.getPointDAO();
        for (MapPoint mapPoint : mPoints)
            dao.create(mapPoint);
    }

    public boolean isServiceStarted() {
        return isServiceStarted;
    }

    public void getList() {
        if (mPoints != null && !mPoints.isEmpty() && mListener != null)
            mListener.onNewPointList(mPoints);
    }

    @Override
    public void onNewPoint(Location point) {
        MapPoint newMapPoint = new MapPoint(point.getLatitude(), point.getLongitude(), point.getTime());
        if (mListener != null) {
            if (mPoints.isEmpty()) {
                Log.i(TAG, "startPoint");
                mListener.onStartPoint(newMapPoint);
            } else {
                Log.i(TAG, "newPoint");
                mListener.onNewPoint(mPoints.get(mPoints.size() - 1), newMapPoint);
            }
        }
        mPoints.add(newMapPoint);

    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case PATH_LOADER_ID:
                return new OrmLoader(mContext,
                        mDatabaseHelper,
                        MapPath.TABLE_NAME,
                        null,
                        null,
                        null);

            case POINT_LOADER_ID:
                String pathName = null;
                if (args != null && args.containsKey(EXTRA_POINT_LOADER_PATH_NAME))
                    pathName = args.getString(EXTRA_POINT_LOADER_PATH_NAME);

                return new OrmLoader(mContext,
                        mDatabaseHelper,
                        MapPoint.TABLE_NAME,
                        MapPath.COLUMN_NAME,
                        new String[]{pathName},
                        MapPoint.COLUMN_DATE);

            default: return null;
        }
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader loader, Object data) {

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader loader) {

    }



    public interface MapHelperListener {

        void onServiceStart();

        void onServiceStop();

        void onNewPoint(MapPoint last, MapPoint newPoint);

        void onNewPointList(List<MapPoint> list);

        void onStartPoint(MapPoint startPoint);

        void onEndPoint(MapPoint endPoint);

    }


}
