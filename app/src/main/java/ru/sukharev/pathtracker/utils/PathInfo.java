package ru.sukharev.pathtracker.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.sukharev.pathtracker.utils.orm.MapPoint;

/**
 * Class that store info about current showing path
 */
public class PathInfo {

    private final static String TAG = "PathInfo.java";
    private long mStartTime;
    private long mEndTime;
    private Speed mSpeed;


    public PathInfo(List<MapPoint> points) {
        mStartTime = points.get(0).getTime();
        mEndTime = points.get(points.size() - 1).getTime();
        mSpeed = new Speed(getSpeedList(points));
    }

    public PathInfo(MapPoint point) {
        mStartTime = point.getTime();
        mEndTime = point.getTime();
        mSpeed = new Speed(point.getSpeed());
    }

    public void addPointInfo(List<MapPoint> points) {
        for (MapPoint point : points)
            mSpeed.addValue(point.getSpeed());
        mEndTime = points.get(points.size() - 1).getTime();
        Log.i(TAG, "start = " + mStartTime + " end = " + mEndTime + " curSpeed = " +
                mSpeed.getCurrentSpeed() + " avgSpeed = " + mSpeed.getAverageSpeed());
    }

    private List<Double> getSpeedList(List<MapPoint> list) {
        List<Double> speedList = new ArrayList<>(list.size());
        for (MapPoint point : list)
            speedList.add(point.getSpeed());
        return speedList;
    }


}
