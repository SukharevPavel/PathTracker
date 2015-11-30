package ru.sukharev.pathtracker.utils;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import ru.sukharev.pathtracker.provider.DatabaseHelper;
import ru.sukharev.pathtracker.utils.orm.MapPath;
import ru.sukharev.pathtracker.utils.orm.MapPoint;

/**
 * Presenter element which handle interaction between View {@link ru.sukharev.pathtracker.ui.MapActivity}
 * and model: Service (@link ru,sukharev.pathtracker.service.TrackingService} and Content Provider
 */
public class MapHelper implements GoogleApiClient.ConnectionCallbacks,
        com.google.android.gms.location.LocationListener {

    private static final String TAG = "MapHelper.java";
    private Context mContext;
    private List<MapPoint> mPoints;
    private boolean isServiceStarted = false;
    private MapHelperListener mListener;
    private SQLInteractionListener mSQLListener;
    private DatabaseHelper mDatabaseHelper;
    private LocationRequest mLocationRequest;

    private GoogleApiClient mGoogleApiClient;



    public MapHelper(@NonNull Context ctx) {
        mContext = ctx;
        mPoints = new LinkedList<>();
        mDatabaseHelper = DatabaseHelper.getInstance(ctx);
        configureGoogleApiClient();
    }

    private void configureGoogleApiClient() {
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API);
        mGoogleApiClient = builder.build();
    }


    private void stopTracking(){
        isServiceStarted = false;
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
            mSQLListener = null;
        }
    }

    public void startService() {
        setUpLocationRequest();
        mGoogleApiClient.connect();
        /*Log.i(TAG, "startService()");
        Intent intent = new Intent(mContext, TrackingService.class);
        mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);*/

    }

    private void setUpLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    public void stopService() {
        stopTracking();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    public void clearData(){
        mPoints.clear();
    }

    public void saveToDatabase(String name) {
        new AsynkSaveToDatabaseTask().execute(name);
    }

    public boolean isServiceStarted() {
        return isServiceStarted;
    }

    public void getList() {
        if (mPoints != null && mListener != null)
            mListener.onNewPointList(mPoints);
    }


    @Override
    public void onConnected(Bundle bundle) {
        isServiceStarted = true;
        mListener.onServiceStart();
        Location point = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        MapPoint newMapPoint = new MapPoint(point);

        //Checking if we start a new tracking on just a resume previous
        if (mPoints.isEmpty()) mListener.onStartPoint(newMapPoint);
        else mListener.onNewPoint(newMapPoint);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        mPoints.add(newMapPoint);
    }

    @Override
    public void onConnectionSuspended(int i) {
        stopTracking();
    }


    @Override
    public void onLocationChanged(Location location) {
        MapPoint newMapPoint = new MapPoint(location);
        mListener.onNewPoint(newMapPoint);
        mPoints.add(newMapPoint);
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

        void onError(SQLException e);

        void onSuccess();

        void onFail();

    }

    private class AsynkSaveToDatabaseTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String name = params[0];
            if (!mPoints.isEmpty()) {
                try {
                    MapPath mapPath = new MapPath(name,
                            mPoints.get(0).getTime(),
                            mPoints.get(mPoints.size() - 1).getTime());
                    setPathToPoints(mapPath);
                    mDatabaseHelper.getPathDAO().create(mapPath);
                    saveAllPoints();
                } catch (SQLException e) {
                    mSQLListener.onError(e);
                }
                mSQLListener.onSuccess();
                return null;
            }
            mSQLListener.onFail();
            return null;
        }


        private void setPathToPoints(MapPath mapPath) {
            for (MapPoint mapPoint : mPoints)
                mapPoint.setPath(mapPath);
        }

        private void saveAllPoints() throws SQLException {
            DatabaseHelper.PointDAO dao = mDatabaseHelper.getPointDAO();
            for (MapPoint mapPoint : mPoints)
                dao.create(mapPoint);
        }
    }


}
