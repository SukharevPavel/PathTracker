package ru.sukharev.pathtracker.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.graphics.PointF;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ru.sukharev.pathtracker.service.TrackingService;

/**
 * Presenter element which handle interaction between View {@link ru.sukharev.pathtracker.ui.MapActivity}
 * and model: Service (@link ru,sukharev.pathtracker.service.TrackingService} and Content Provider
 */
public class MapHelper implements TrackingService.TrackingListener {

    private Context mContext;
    private List<Location> mPoints;
    private boolean isServiceStarted = false;
    private TrackingService mService;
    private MapHelperListener mListener;

    public MapHelper(@NonNull Context ctx){
        mContext = ctx;
        mPoints = new LinkedList<>();
    }

    public void setListeners(MapHelperListener listener){
        mListener = listener;
    }

    public void startService(){
        Intent intent = new Intent(mContext, TrackingService.class);
        mContext.bindService(intent, connection, 0);

    }

    public void stopService(){
        mContext.unbindService(connection);
    }

    public boolean isServiceStarted(){
        return isServiceStarted;
    }



    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isServiceStarted = true;
            mService = ((TrackingService) ((TrackingService.LocalBinder) service).getService());
            mService.setListener(MapHelper.this);
            mListener.onServiceStart();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceStarted = false;
            mService = null;
            mListener.onServiceStop();
        }
    };

    @Override
    public void onNewPoint(Location point) {
            if (mListener != null) {
                if (mPoints.isEmpty()) mListener.onStartPoint(point);
                else mListener.onNewPoint(mPoints.get(mPoints.size()), point);
            }
        mPoints.add(point);

    }

    public interface MapHelperListener{

        void onServiceStart();

        void onServiceStop();

        void onNewPoint(Location last, Location newPoint);

        void onNewPointList(List<Location> list);

        void onStartPoint(Location startPoint);

        void onEndPoint(Location endPoint);

    }



}
