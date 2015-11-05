package ru.sukharev.pathtracker.ui;

import android.content.ContentValues;
import android.graphics.PointF;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import ru.sukharev.pathtracker.R;
import ru.sukharev.pathtracker.utils.MapHelper;

public class MapActivity extends FragmentActivity implements MapHelper.MapHelperListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private ControlFragment mControlFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();
        mControlFragment = (ControlFragment) getSupportFragmentManager().
                findFragmentById(R.id.control_fragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }


    @Override
    public void onServiceStart() {
        mControlFragment.changeButtonText(getString(R.string.button_stop_service));
    }

    @Override
    public void onServiceStop() {
        mControlFragment.changeButtonText(getString(R.string.button_start_service));
    }

    @Override
    public void onNewPoint(PointF last, PointF newPoint) {

    }

    @Override
    public void onNewPointList(List<PointF> list) {

    }

    @Override
    public void onStartPoint(PointF startPoint) {

    }

    @Override
    public void onEndPoint(PointF endPoint) {

    }
}
