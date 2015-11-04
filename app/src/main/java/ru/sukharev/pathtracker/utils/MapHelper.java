package ru.sukharev.pathtracker.utils;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

import ru.sukharev.pathtracker.service.TrackingService;

/**
 * Created by pasha on 04.11.2015.
 */
public class MapHelper{

    private Context mContext;
    private List<PointF> mPoints;
    private boolean isServiceStarted = false;

    public MapHelper(@NonNull Context ctx){
        mContext = ctx;
        mPoints = new LinkedList<>();
    }

    public void startService(){
        Intent intent = new Intent(mContext, TrackingService.class);
        mContext.bindService(intent)

    }

    ServiceConnection


    public interface MapHelperListener{

        void onNewPoint(PointF last, PointF newPoint);

        void onNewPointList(List<PointF> list);

    }

}
