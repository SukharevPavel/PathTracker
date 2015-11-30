package ru.sukharev.pathtracker.ui;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.ForeignCollection;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ru.sukharev.pathtracker.R;
import ru.sukharev.pathtracker.ui.dialog.ClearDialogFragment;
import ru.sukharev.pathtracker.ui.dialog.PathNamingFragment;
import ru.sukharev.pathtracker.utils.MapHelper;
import ru.sukharev.pathtracker.utils.orm.MapPath;
import ru.sukharev.pathtracker.utils.orm.MapPoint;

public class MapActivity extends AppCompatActivity implements MapHelper.MapHelperListener,
        MapHelper.SQLInteractionListener, PathNamingFragment.DialogPathNamingListener,
        NavigationDrawerListFragment.PathItemClickListener, ControlFragment.ControlFragmentListener,
        ClearDialogFragment.DialogClearListener {

    private final static String TAG = "MapActivity.java";

    private final static String PATH_NAMING_FRAGMENT_TAG = "path_naming_fragment";
    private final static String CLEAR_FRAGMENT_TAG = "clear_fragment";
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ControlFragment mControlFragment;
    private NavigationDrawerListFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    private Polyline mPolyline;

    private boolean isShowingSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mNavigationDrawerFragment = (NavigationDrawerListFragment) getSupportFragmentManager().
                findFragmentById(R.id.navigation_drawer_list_fragment);
        mNavigationDrawerFragment.setUp((DrawerLayout) findViewById(R.id.drawer_layout),
                mToolbar, R.id.navigation_drawer_list_fragment);
        mControlFragment = (ControlFragment) getSupportFragmentManager().
                findFragmentById(R.id.control_fragment);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            checkPermission();
    }

    @Override
    protected void onResume() {
        setUpMapIfNeeded();
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
     * installed) and the map has not already been instantiated..
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

    private void showPathNamingFragment(){
        PathNamingFragment fragment = new PathNamingFragment();
        fragment.show(getSupportFragmentManager(), PATH_NAMING_FRAGMENT_TAG);
    }

    private void showClearFragment(){
        ClearDialogFragment fragment = new ClearDialogFragment();
        fragment.show(getSupportFragmentManager(), CLEAR_FRAGMENT_TAG);
    }

    private void clearMap(){
        if (mMap != null) {
            mMap.clear();
            removePolyline();
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */

    @Override
    public void onServiceStart() {
        mControlFragment.changeButtonText(getString(R.string.button_stop_service));
    }

    @Override
    public void onServiceStop() {
        mControlFragment.changeButtonText(getString(R.string.button_start_service));
        //askForNameToSave();
    }

    @Override
    public void onNewPoint(MapPoint newPoint) {
        if (!isShowingSaved) setNewPoint(newPoint);
    }


    private void removePolyline() {
        if (mPolyline != null) {
            mPolyline.remove();
            mPolyline = null;
        }
    }

    private void updatePolyline(Iterable<LatLng> list) {
        Iterable<LatLng> resultList = null;
        if (mPolyline != null) {
            resultList = mPolyline.getPoints();
            removePolyline();
        }
        PolylineOptions options = new PolylineOptions().
                geodesic(true).
                color(ContextCompat.getColor(this, R.color.red_lt));
        if (resultList != null) options.addAll(resultList);
        if (list != null) options.addAll(list);
        mPolyline = mMap.addPolyline(options);

    }


    private void addOnePoint(MapPoint newPoint) {
        updatePolyline(Collections.singletonList(newPoint.toLatLng()));
    }

    private void addListOfPoints(Iterator<MapPoint> iterator) {
        LinkedList<LatLng> newList = new LinkedList<>();
        while (iterator.hasNext())
            newList.add(iterator.next().toLatLng());
        updatePolyline(newList);
    }

    private void setNewPoint(MapPoint newPoint) {
        setUpMapIfNeeded();
        addOnePoint(newPoint);
       /* mMap.addPolyline(new PolylineOptions().geodesic(true)
                .color(ContextCompat.getColor(this, R.color.red_lt))
                .add(new LatLng(last.getLattitude(), last.getLongitude()))
                .add(new LatLng(newPoint.getLattitude(), newPoint.getLongitude())));*/
    }

    @Override
    public void onNewPointList(List<MapPoint> list) {
        if (!isShowingSaved)
            setNewMapPoints(list.iterator());
    }

    private void setNewMapPoints(Iterator<MapPoint> iterator) {
        setUpMapIfNeeded();
        clearMap();
        MapPoint newPoint;
        if (iterator.hasNext()) {
            newPoint = iterator.next();
            setStartPoint(newPoint);
            addListOfPoints(iterator);
        }
    }

    @Override
    public void onStartPoint(MapPoint startPoint) {
        if (!isShowingSaved) setStartPoint(startPoint);
    }

    private void setStartPoint(MapPoint startPoint) {
        setUpMapIfNeeded();
        clearMap();
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(startPoint.getLattitude(), startPoint.getLongitude()))
                .title("Start"));
        addOnePoint(startPoint);
    }

    private void enableWatchingSavedPathMode() {
        isShowingSaved = true;
        mControlFragment.showCurrentPathButton(true);
    }

    private void disableWatchingSavedPathMode() {
        isShowingSaved = false;
        mControlFragment.showCurrentPathButton(false);
        mNavigationDrawerFragment.invalidateSelection();
    }

    @Override
    public void onEndPoint(MapPoint endPoint) {

    }

    @Override
    public void onGetName(String name) {
        mControlFragment.saveToDatabase(name);
    }

    @Override
    public void onPathClick(MapPath path) {
        ForeignCollection<MapPoint> points = path.getPoints();
        CloseableIterator<MapPoint> iterator = points.closeableIterator();
        try {
            setNewMapPoints(iterator);
        } finally {
            try {
                iterator.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        enableWatchingSavedPathMode();
    }

    @Override
    public void onCurrentButtonClick() {
        disableWatchingSavedPathMode();
    }

    @Override
    public void onSaveButtonClick(){
        showPathNamingFragment();
    }

    @Override
    public void onClearButtonClick() {
        showClearFragment();
    }

    @Override
    public void onClear() {
        clearMap();
        mControlFragment.clearData();
    }

    @Override
    public void onError(SQLException e) {
        Toast.makeText(this, getString(R.string.error_saving_to_db), Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }

    @Override
    public void onSuccess() {
        mNavigationDrawerFragment.reloadList();
    }

    @Override
    public void onFail() {
        Toast.makeText(this, getString(R.string.error_saving_list_is_empty), Toast.LENGTH_LONG).show();
    }
}
