package ru.sukharev.pathtracker.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class TrackingService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private static final String TAG = "TrackingService.java";

    private TrackingListener mListener;
    LocationManager mLocationManager;
    LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG,"tracked");
            if (mListener!=null)
                mListener.onNewPoint(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG,"onStatusChanged " + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG,"onproviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i(TAG,"onproviderDisabled");
        }
    };


    public TrackingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return new LocalBinder();
    }

    private void setUpTracking() {
        Log.i(TAG, "setTracking");
        mLocationManager = (LocationManager) getApplicationContext().
                getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                registerLocationListeners();
        } else {
            registerLocationListeners();
        }
    }

    public void registerLocationListeners(){
        Log.i(TAG, "registerListeners");
        //T0D0 check if provider enabled
      //  mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
    }

    private void abandonTracking(){
        if (mLocationManager != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    mLocationManager.removeUpdates(mLocationListener);
            } else mLocationManager.removeUpdates(mLocationListener);
    }

    public void setListener(TrackingListener listener){
        Log.i(TAG, "setListener");
        mListener = listener;
        setUpTracking();
    }

    @Override
    public boolean onUnbind(Intent itent){
        Log.i(TAG,"unbind");
        abandonTracking();
        return false;
    }


    public class LocalBinder extends Binder {

        public Service getService(){
            return TrackingService.this;
        }

    }

    public interface TrackingListener{

        void onNewPoint(Location point);

    }
}
