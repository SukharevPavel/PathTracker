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
import android.os.Bundle;
import android.os.IBinder;

public class TrackingService extends Service {

    private final IBinder mBinder = new LocalBinder();

    private TrackingListener mListener;
    LocationManager mLocationManager;
    LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (mListener!=null)
                mListener.onNewPoint(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    public TrackingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        setUpTracking();
        return new LocalBinder();
    }

    private void setUpTracking() {
        mLocationManager = (LocationManager) getApplicationContext().
                getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
    }

    private void abandonTracking(){
        if (mLocationManager != null)
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                mLocationManager.removeUpdates(mLocationListener);
    }

    public void setListener(TrackingListener listener){
        mListener = listener;
        setUpTracking();
    }

    @Override
    public boolean onUnbind(Intent itent){
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
