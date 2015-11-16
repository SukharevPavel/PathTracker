package ru.sukharev.pathtracker.ui;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.sql.SQLException;
import java.util.List;

import ru.sukharev.pathtracker.R;
import ru.sukharev.pathtracker.utils.MapHelper;
import ru.sukharev.pathtracker.utils.orm.MapPoint;

public class MapActivity extends AppCompatActivity implements MapHelper.MapHelperListener,
        PathNamingFragment.DialogPathNamingListener{

    private final static String TAG = "MapActivity.java";

    private final static String PATH_NAMING_FRAGMENT_TAG = "path_naming_fragment";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private ControlFragment mControlFragment;

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();
        mControlFragment = (ControlFragment) getSupportFragmentManager().
                findFragmentById(R.id.control_fragment);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void checkPermission(){
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                //T0D0 show error and close
            }
        }
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
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment))
                    .getMap();
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
        PathNamingFragment fragment = new PathNamingFragment();
        fragment.show(getSupportFragmentManager(), PATH_NAMING_FRAGMENT_TAG);
    }


    int i=0;
    @Override
    public void onNewPoint(MapPoint last, MapPoint newPoint) {
      //  setUpMapIfNeeded();
        Log.i(TAG, "new point = " + last.getLatitude() + " " + last.getLongitude() );
        Log.i(TAG, "new point = " + newPoint.getLatitude() + " " + newPoint.getLongitude() );
        Toast.makeText(this,"new point = " + newPoint.getLatitude() + " " + newPoint.getLongitude(),Toast.LENGTH_LONG).show();
        /*mMap.addMarker(new MarkerOptions()
                .position(new LatLng(newPoint.getLatitude(), newPoint.getLongitude()))
                .title(String.valueOf(i++)));*/
            mMap.addPolyline(new PolylineOptions().geodesic(true)
                    .add(new LatLng(last.getLatitude(), last.getLongitude()))
                    .add(new LatLng(newPoint.getLatitude(), newPoint.getLongitude())));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(newPoint.getLatitude(), newPoint.getLongitude()), 10));
    }

    @Override
    public void onNewPointList(List<MapPoint> list) {
        onStartPoint(list.get(0));
        for (int i=1;i<list.size(); i++)
            onNewPoint(list.get(i-1), list.get(i));
    }

    @Override
    public void onStartPoint(MapPoint startPoint) {
        setUpMapIfNeeded();
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(startPoint.getLatitude(), startPoint.getLongitude()))
                .title("Start"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(startPoint.getLatitude(), startPoint.getLongitude()),10));
    }

    @Override
    public void onEndPoint(MapPoint endPoint) {

    }

    @Override
    public void onGetName(String name) {
        try {
            if (!mControlFragment.saveToDatabase(name)) Toast.makeText(this, getString(R.string.error_saving_list_is_empty), Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            Toast.makeText(this, getString(R.string.error_saving_to_db), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

}
