package ru.sukharev.pathtracker.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import ru.sukharev.pathtracker.provider.DatabaseHelper;
import ru.sukharev.pathtracker.service.TrackingService;

/**
 * Presenter element which handle interaction between View {@link ru.sukharev.pathtracker.ui.MapActivity}
 * and model: Service (@link ru,sukharev.pathtracker.service.TrackingService} and Content Provider
 */
public class MapHelper implements TrackingService.TrackingListener {

    private static final String TAG = "MapHelper.java";
    private Context mContext;
    private List<Location> mPoints;
    private boolean isServiceStarted = false;
    private TrackingService mService;
    private MapHelperListener mListener;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "service connected");
            isServiceStarted = true;
            mService = ((TrackingService) ((TrackingService.LocalBinder) service).getService());
            mService.setListener(MapHelper.this);
            mListener.onServiceStart();
        }


        //never called
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "service disconnected");
            isServiceStarted = false;
            mService = null;
            mListener.onServiceStop();
        }
    };
    private DatabaseHelper mDatabaseHelper;

    public MapHelper(@NonNull Context ctx) {
        mContext = ctx;
        mPoints = new LinkedList<>();
        mDatabaseHelper = DatabaseHelper.getInstance(ctx);
    }

    public void setListeners(MapHelperListener listener) {
        mListener = listener;
    }

    public void startService() {
        Log.i(TAG, "startService()");
        Intent intent = new Intent(mContext, TrackingService.class);
        mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void stopService() {
        mContext.unbindService(connection);
        isServiceStarted = false;
        mService = null;
        mListener.onServiceStop();
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
        if (mListener != null) {
            if (mPoints.isEmpty()) {
                Log.i(TAG, "startPoint");
                mListener.onStartPoint(point);
            } else {
                Log.i(TAG, "newPoint");
                mListener.onNewPoint(mPoints.get(mPoints.size() - 1), point);
            }
        }
        mPoints.add(point);

    }

    public interface MapHelperListener {

        void onServiceStart();

        void onServiceStop();

        void onNewPoint(Location last, Location newPoint);

        void onNewPointList(List<Location> list);

        void onStartPoint(Location startPoint);

        void onEndPoint(Location endPoint);

    }


}
