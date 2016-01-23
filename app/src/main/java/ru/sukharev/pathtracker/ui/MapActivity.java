package ru.sukharev.pathtracker.ui;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.ForeignCollection;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ru.sukharev.pathtracker.R;
import ru.sukharev.pathtracker.ui.dialog.ClearDialogFragment;
import ru.sukharev.pathtracker.ui.dialog.EnableGPSDialogFragment;
import ru.sukharev.pathtracker.ui.dialog.ErrorDialogFragment;
import ru.sukharev.pathtracker.ui.dialog.PathNamingFragment;
import ru.sukharev.pathtracker.utils.MapHelper;
import ru.sukharev.pathtracker.utils.PathInfo;
import ru.sukharev.pathtracker.utils.orm.MapPath;
import ru.sukharev.pathtracker.utils.orm.MapPoint;

public class MapActivity extends AppCompatActivity implements MapHelper.MapHelperListener,
        MapHelper.SQLInteractionListener, PathNamingFragment.DialogPathNamingListener,
        NavigationDrawerListFragment.NavigationDrawerListener, ControlFragment.ControlFragmentListener,
        ClearDialogFragment.DialogClearListener, GoogleMap.OnMyLocationChangeListener,
        InfoFragment.InfoFragmentListener {

    private final static String TAG = "MapActivity.java";

    private final static DateFormat markerFormat = SimpleDateFormat.getTimeInstance();

    private final static float STANDARD_ZOOM = 12;

    private final static String PATH_NAMING_FRAGMENT_TAG = "path_naming_fragment";
    private final static String CLEAR_FRAGMENT_TAG = "clear_fragment";
    private final static String INFO_FRAGMENT_TAG = "info_fragment";
    private final static String ENABLE_GPS_FRAGMENT_TAG = "enable_gps_fragment";
    private final static String ERROR_DIALOG_FRAGMENT_TAG = "error_fragment";
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ControlFragment mControlFragment;
    private NavigationDrawerListFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    private List<Polyline> mPolylines;
    private PathInfo mPathInfo;

    private boolean isShowingSaved;
    private InfoFragment mInfoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Log.i(TAG, "activity create start");


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        setUpFragments();

        //Map needs info about control fragment to pad from bottom
        setUpMapIfNeeded();


        checkPermission();


        //check GPS and center user location only if first time launch
        if (savedInstanceState == null) {
            mMap.setOnMyLocationChangeListener(this);
            checkGPS();
        }
        Log.i(TAG, "activity create end");

    }

    @Override
    public void onStart() {
        super.onStart();
        padMapElements(mMap);
        Log.i(TAG, "activity start");
        if (mMap != null) setMapType();
        updateInfoFragmentIfExists();
        mNavigationDrawerFragment.reloadList();
        mMap.setMyLocationEnabled(true);


    }


    @Override
    public void onStop() {
        super.onStop();
        mMap.setMyLocationEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            Intent intent = new Intent(MapActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void checkGPS() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                getSupportFragmentManager().findFragmentByTag(ENABLE_GPS_FRAGMENT_TAG) == null) {
            EnableGPSDialogFragment fragment = new EnableGPSDialogFragment();
            fragment.show(getSupportFragmentManager(), ENABLE_GPS_FRAGMENT_TAG);
        }
    }


    private void setUpFragments() {
        mNavigationDrawerFragment = (NavigationDrawerListFragment) getSupportFragmentManager().
                findFragmentById(R.id.navigation_drawer_list_fragment);
        mNavigationDrawerFragment.setUp((DrawerLayout) findViewById(R.id.drawer_layout),
                mToolbar, R.id.navigation_drawer_list_fragment);

        mControlFragment = (ControlFragment) getSupportFragmentManager().
                findFragmentById(R.id.control_fragment);

        mInfoFragment = (InfoFragment) getSupportFragmentManager().findFragmentByTag(INFO_FRAGMENT_TAG);
    }

    private void padMapElements(final GoogleMap map) {
        ViewTreeObserver observer = mControlFragment.getView().getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                map.setPadding(0, 0, 0, mControlFragment.getView().getHeight());
                ViewTreeObserver observer = mControlFragment.getView().getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    observer.removeOnGlobalLayoutListener(this);
                } else {
                    observer.removeGlobalOnLayoutListener(this);
                }
            }

        });
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
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
                ErrorDialogFragment fragment = new ErrorDialogFragment();
                fragment.show(getSupportFragmentManager(), ERROR_DIALOG_FRAGMENT_TAG);
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
            setMapType();
            mPolylines = new ArrayList<>();
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.setMyLocationEnabled(true);

        }
    }

    private void setMapType() {
        int mapType = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.pref_key_map_type),
                        getString(R.string.pref_key_map_type_def_result)));
        mMap.setMapType(mapType);

    }

    private void showPathNamingFragment() {
        PathNamingFragment fragment = new PathNamingFragment();
        fragment.show(getSupportFragmentManager(), PATH_NAMING_FRAGMENT_TAG);
    }

    private void showClearFragment() {
        ClearDialogFragment fragment = new ClearDialogFragment();
        fragment.show(getSupportFragmentManager(), CLEAR_FRAGMENT_TAG);
    }

    private void clearMap() {
        if (mMap != null) {
            mMap.clear();
            for (Polyline polyline : mPolylines)
                removePolyline(polyline);
            mPolylines.clear();
        }
        if (mPathInfo != null)
            mPathInfo = null;
    }


    @Override
    public void onServiceStart() {
        mControlFragment.changeButtonIcon(true);
    }

    @Override
    public void onServiceStop() {
        mControlFragment.changeButtonIcon(false);
        //askForNameToSave();
    }

    @Override
    public void onNewPoint(MapPoint newPoint) {
        if (!isShowingSaved) setNewPoint(newPoint);
    }


    private void removePolyline(Polyline polyline) {
        if (polyline != null) {
            polyline.remove();
        }
    }

    private void addPolyline(Iterable<LatLng> list) {
        PolylineOptions options = new PolylineOptions().
                geodesic(true).
                color(ContextCompat.getColor(this, R.color.red_lt))
                .addAll(list);
        Polyline polyline = mMap.addPolyline(options);
        mPolylines.add(polyline);

    }

    private void updatePolyline(Iterable<LatLng> list) {
        Polyline polyline = mPolylines.get(mPolylines.size() - 1);
        Iterable<LatLng> resultList = null;
        if (polyline != null) {
            resultList = polyline.getPoints();
            removePolyline(polyline);
        }

        PolylineOptions options = new PolylineOptions().
                geodesic(true).
                color(ContextCompat.getColor(this, R.color.red_lt));
        if (resultList != null) options.addAll(resultList);
        if (list != null) options.addAll(list);
        mPolylines.set(mPolylines.size() - 1, mMap.addPolyline(options));

    }

    private void initPathInfo(List<MapPoint> list) {
        mPathInfo = new PathInfo(list);
    }


    private void addOnePoint(MapPoint newPoint) {
        mPathInfo.addPointInfo(newPoint);
        updateInfoFragmentIfExists();
        updatePolyline(Collections.singletonList(newPoint.toLatLng()));
    }

    private void updateInfoFragmentIfExists(){
        if (mInfoFragment != null)
            if (mPathInfo != null) mInfoFragment.updateFields(mPathInfo.getTotalTime(),
                    mPathInfo.getDistance(),
                    mPathInfo.getCurSpeed(),
                mPathInfo.getAvgSpeed());
            else mInfoFragment.dropAllFields();
    }

    private void addListOfPoints(List<MapPoint> pointList) {
        List<List<MapPoint>> pathPointsList = new ArrayList<>();

        List<MapPoint> polylineList = new ArrayList<>();
        for (MapPoint point : pointList) {
            polylineList.add(point);
            if (point.isEndPoint()) {
                pathPointsList.add(polylineList);
                polylineList = new ArrayList<>();
            }
        }
        if (!polylineList.isEmpty()) pathPointsList.add(polylineList);

        for (List<MapPoint> list : pathPointsList) {
            addPolyline(MapPoint.convertListToLatLng(list));
            for (MapPoint point : list) {
                if (point.isStartPoint()) setStartPoint(point);
                if (point.isEndPoint()) setEndPoint(point);
            }
        }
    }

    private void setNewPoint(MapPoint newPoint) {
        setUpMapIfNeeded();
        addOnePoint(newPoint);
    }

    @Override
    public void onNewPointList(List<MapPoint> list) {
        if (!isShowingSaved)
            setNewMapPoints(list.iterator());
    }

    private void setNewMapPoints(Iterator<MapPoint> iterator) {
        setUpMapIfNeeded();
        clearMap();
        List<MapPoint> points = new ArrayList<>();
        while (iterator.hasNext()) {
            points.add(iterator.next());
        }
        if (!points.isEmpty()) {
            initPathInfo(points);
            addListOfPoints(points);
            moveCameraToPosition(points.get(points.size() - 1).toLatLng());
        }
        updateInfoFragmentIfExists();
    }

    @Override
    public void onStartPoint(MapPoint startPoint) {
        if (!isShowingSaved) {
            setUpMapIfNeeded();
            if (mPathInfo == null) initPathInfo(Collections.singletonList(startPoint));
            else mPathInfo.addPointInfo(startPoint);
            setStartPoint(startPoint);
            addPolyline(Collections.singletonList(startPoint.toLatLng()));
        }
    }

    private void setStartPoint(MapPoint startPoint) {
        String title = markerFormat.format(startPoint.getTime());
        mMap.addMarker(new MarkerOptions()
                .position(startPoint.toLatLng())
                .title(title));
    }

    public void moveCameraToPosition(LatLng loc) {

        CameraPosition.Builder builder = new CameraPosition.Builder()
                .target(loc);
        if (mMap.getCameraPosition().zoom < STANDARD_ZOOM) builder.zoom(STANDARD_ZOOM);
        else builder.zoom(mMap.getCameraPosition().zoom);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
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


    private void switchInfoFragment(){
        if (getSupportFragmentManager().findFragmentByTag(INFO_FRAGMENT_TAG) == null) {
            setInfoFragment();
        }
        else {
            removeInfoFragment();
        }
    }

    public void setInfoFragment() {
        mInfoFragment = new InfoFragment();
        if (mPathInfo != null) {
            Bundle bundle = new Bundle();
            bundle.putLong(InfoFragment.ARG_TOTAL_TIME, mPathInfo.getTotalTime());
            bundle.putDouble(InfoFragment.ARG_CUR_SPEED, mPathInfo.getCurSpeed());
            bundle.putDouble(InfoFragment.ARG_AVG_SPEED, mPathInfo.getAvgSpeed());
            bundle.putDouble(InfoFragment.ARG_DISTANCE, mPathInfo.getDistance());
            mInfoFragment.setArguments(bundle);
        }
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .setCustomAnimations(R.anim.anim_info_in,
                        R.anim.anim_info_out)
                .add(R.id.info_fragment, mInfoFragment, INFO_FRAGMENT_TAG)
                .commit();
    }

    public void removeInfoFragment() {
        FragmentManager manager = getSupportFragmentManager();

        manager.beginTransaction()
                .remove(mInfoFragment)
                .commit();

        mInfoFragment = null;


    }

    @Override
    public void onEndPoint(MapPoint endPoint) {
        if (!isShowingSaved) {
            mPathInfo.pauseAndSetLastPointAsEnd();
            setEndPoint(endPoint);
        }
    }

    private void setEndPoint(MapPoint endPoint) {
        String title = markerFormat.format(endPoint.getTime());
        mMap.addMarker(new MarkerOptions()
                .position(endPoint.toLatLng())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .title(title));
    }

    @Override
    public void onGetName(String name) {
        if (mPathInfo != null)
            mControlFragment.saveToDatabase(name, mPathInfo.getDistance(), mPathInfo.getAvgSpeed());
        else Toast.makeText(this, R.string.error_no_path_info, Toast.LENGTH_SHORT).show();
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
    public void onRenamingPath(MapPath path, String newName) {
        mControlFragment.updatePath(path, newName);
    }

    @Override
    public void onCurrentButtonClick() {
        disableWatchingSavedPathMode();

    }

    @Override
    public void onSaveButtonClick() {
        showPathNamingFragment();
    }

    @Override
    public void onClearButtonClick() {
        showClearFragment();
    }

    @Override
    public void onInfoButtonListener() {
        switchInfoFragment();
    }

    @Override
    public void onClear() {
        clearMap();
        disableWatchingSavedPathMode();
        mControlFragment.clearData();
        updateInfoFragmentIfExists();
    }

    @Override
    public void onError() {
        Log.i(TAG, "error!");
        Toast.makeText(this, getString(R.string.error_saving_to_db), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess() {
        Log.i(TAG, "success!");
        mNavigationDrawerFragment.reloadList();
    }

    @Override
    public void onFail() {
        Toast.makeText(this, getString(R.string.error_saving_list_is_empty), Toast.LENGTH_SHORT).show();
    }


    //This callback works only first time when user location got
    @Override
    public void onMyLocationChange(Location location) {
        moveCameraToPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        mMap.setOnMyLocationChangeListener(null);
    }

    @Override
    public void onInfoFragmentClick() {
        removeInfoFragment();
    }

}
