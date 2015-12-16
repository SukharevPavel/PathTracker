package ru.sukharev.pathtracker.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import ru.sukharev.pathtracker.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static String TAG = "SettingsFragment.java";

    private static String[] mapTypes;
    private ListPreference mMapType;
    private ListPreference mMeasureInterval;
    private CheckBoxPreference mStartTime;
    private CheckBoxPreference mEndTime;
    private CheckBoxPreference mDistance;
    private CheckBoxPreference mAvgSpeed;


    public SettingsFragment() {
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }


    private void setSummary() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setSummaryMapType(prefs);
        setSummaryMeasureInterval(prefs);
    }

    @Override
    public void onResume() {
        super.onResume();
        setSummary();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    private void setSummaryMapType(SharedPreferences prefs) {
        String key = getString(R.string.pref_key_map_type);

        if (mMapType == null)
            mMapType = (ListPreference) findPreference(key);

        int mapType = Integer.parseInt(prefs.getString(key,
                getString(R.string.pref_key_map_type_def_result)));
        if (mapTypes == null)
            mapTypes = getResources().getStringArray(R.array.array_map_types);
        try {
            Log.i(TAG, "mapType[0] = " + mapTypes[0]);
            Log.i(TAG, "mapType " + mapType);
            if (mMapType == null) Log.i(TAG, " mMapType is null");
            if (getPreferenceManager() == null) Log.i(TAG, "preference manager is null");
            if (getPreferenceManager().getPreferenceScreen() == null)
                Log.i(TAG, "preference screen is null");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        mMapType.setSummary(mapTypes[mapType - 1]);
    }


    private void setSummaryMeasureInterval(SharedPreferences prefs) {
        String key = getString(R.string.pref_key_measure_interval);

        if (mMeasureInterval == null)
            mMeasureInterval = (ListPreference) findPreference(key);

        String interval = prefs.getString(key,
                getString(R.string.pref_key_measure_interval_def));
        mMeasureInterval.setSummary(interval);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_key_map_type)))
            setSummaryMapType(sharedPreferences);
        else if (key.equals(getString(R.string.pref_key_measure_interval)))
            setSummaryMeasureInterval(sharedPreferences);
    }
}
