package ru.sukharev.pathtracker.utils;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.sukharev.pathtracker.utils.orm.MapPoint;

/**
 * Class that store info about current showing path
 */
public class PathInfo {

    /**
    * dummy providers needs for init Location objects and use distanceTo() method
    * @see #findDistance
     */
    private final static String DUMMY_PROVIDER_A = "A";
    private final static String DUMMY_PROVIDER_B = "B";

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

    private double findDistance(MapPoint point1, MapPoint point2){
        Location locationA = new Location(DUMMY_PROVIDER_A);
        locationA.setLatitude(point1.getLattitude());
        locationA.setLongitude(point1.getLongitude());
        Location locationB = new Location(DUMMY_PROVIDER_B);
        locationB.setLatitude(point2.getLattitude());
        locationB.setLongitude(point2.getLongitude());
        return locationA.distanceTo(locationB);
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

    public void addPointInfo(List<MapPoint> list){
        for (MapPoint point: list)
            addPointInfo(point);
    }

    public void addPointInfo(MapPoint point){
        mDistance += findDistance(mPoints.get(mPoints.size() - 1), point);
        mPoints.add(point);

        findAvgSpeed(point);

        mCurTime = point.getTime();
        curSpeed = point.getSpeed();
    }

    private void findAvgSpeed(MapPoint point){
        avgSpeed = (avgSpeed * (mCurTime-mStartTime) +
                (point.getTime() - mCurTime) * point.getSpeed())
                / (point.getTime() - mStartTime);
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
