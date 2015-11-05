package ru.sukharev.pathtracker.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Binder;
import android.os.IBinder;

public class TrackingService extends Service {

    private final IBinder mBinder = new LocalBinder();


    private TrackingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
        // TODO: Return the communication channel to the service.
    }

    public void setCallback(TrackingCallback callback){

    }


    public class LocalBinder extends Binder {

        public Service getService(){
            return TrackingService.this;
        }

    }

    public interface TrackingCallback{

        void onNewPoint(PointF point);

    }
}
