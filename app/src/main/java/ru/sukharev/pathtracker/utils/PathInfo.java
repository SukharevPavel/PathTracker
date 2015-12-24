package ru.sukharev.pathtracker.utils;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ru.sukharev.pathtracker.utils.orm.MapPoint;

/**
 * Class that store info about current showing path
 */
public class PathInfo {

    private long mStartTime;
    private long mCurTime;
    private double mDistance;
    private double curSpeed;
    private double avgSpeed;
    private List<MapPoint> mPoints;


    public PathInfo(List<MapPoint> points) {
        this(points.get(0));
        for (int i=1; i<points.size(); i++) {
            addPointInfo(points.get(i));
        }
    }

    public PathInfo(MapPoint point) {
        mPoints = new ArrayList<>();
        mPoints.add(point);
        mStartTime = point.getTime();
        mCurTime = point.getTime();
        curSpeed = point.getSpeed();
        avgSpeed = point.getSpeed();
        mDistance = 0d;
    }

    private double findDistance(MapPoint point1, MapPoint point2) {
        //Because of documentation of Location.distanceBetween function
        float[] result = new float[1];
        Location.distanceBetween(point1.getLattitude(),
                point1.getLongitude(),
                point2.getLattitude(),
                point2.getLongitude(),
                result);
        return result[0];
    }

    public void addPointInfo(List<MapPoint> list){
        for (MapPoint point: list)
            addPointInfo(point);
    }

    public void addPointInfo(MapPoint point){
        double curDist = findDistance(mPoints.get(mPoints.size() - 1), point);
        mDistance += curDist;

        curSpeed = (point.isHasSpeed() ? point.getSpeed() :
                findPreciseAvgSpeed(curDist, TimeUnit.MILLISECONDS.toSeconds(point.getTime() - getCurTime())));
        findAvgSpeed(point.getTime(), curSpeed);
        mCurTime = point.getTime();

        mPoints.add(point);
    }

    private double findPreciseAvgSpeed(double dist, long time) {
        if (time == 0d) return 0d;
        return dist / time;
    }

    private void findAvgSpeed(long time, double speed) {
        avgSpeed = (getAvgSpeed() * (getCurTime() - getStartTime()) +
                (time - getCurTime()) * speed)
                / (time - getStartTime());
    }


    public long getStartTime() {
        return mStartTime;
    }

    public long getCurTime() {
        return mCurTime;
    }

    public double getDistance() {
        return mDistance;
    }

    public double getCurSpeed() {
        return curSpeed;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }
}
