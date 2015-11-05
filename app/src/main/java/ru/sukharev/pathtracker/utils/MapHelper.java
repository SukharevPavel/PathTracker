package ru.sukharev.pathtracker.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import java.util.LinkedList;
import java.util.List;

import ru.sukharev.pathtracker.service.TrackingService;

/**
 * Presenter element which handle interaction between View {@link ru.sukharev.pathtracker.ui.MapActivity}
 * and model: Service (@link ru,sukharev.pathtracker.service.TrackingService} and Content Provider
 */
public class MapHelper implements TrackingService.TrackingCallback {

    private Context mContext;
    private List<PointF> mPoints;
    private boolean isServiceStarted = false;
    private TrackingService mService;
    private MapHelperCallbacks mCallbacks;

    public MapHelper(@NonNull Context ctx){
        mContext = ctx;
        mPoints = new LinkedList<>();
    }

    public void setCallbacks(MapHelperCallbacks callbacks){
        mCallbacks = callbacks;
    }

    public void startService(){
        Intent intent = new Intent(mContext, TrackingService.class);
        mContext.bindService(intent, connection, 0);

    }

    public void stopService(){
        Intent intent = new Intent(mContext, TrackingService.class);
        mContext.stopService(intent);
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isServiceStarted = true;
            mService = ((TrackingService) ((TrackingService.LocalBinder) service).getService());
            mService.setCallback(MapHelper.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceStarted = false;
            mService = null;
        }
    };

    @Override
    public void onNewPoint(PointF point) {
            mPoints.add(point);

    }


    public interface MapHelperCallbacks{

        void onNewPoint(PointF last, PointF newPoint);

        void onNewPointList(List<PointF> list);

        void onStartPoint(PointF startPoint);

        void onEndPoint(PointF endPoint);

    }

}
