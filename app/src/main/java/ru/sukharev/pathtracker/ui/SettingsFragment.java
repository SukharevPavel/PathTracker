package ru.sukharev.pathtracker.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

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
    private ListPreference mDistanceUnits;
    private ListPreference mSpeedUnits;


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
        setSummaryDistanceUnits(prefs);
        setSummarySpeedUnits(prefs);
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

    private void setSummaryDistanceUnits(SharedPreferences prefs) {
        String key = getString(R.string.pref_key_units_distance);

        if (mDistanceUnits == null)
            mDistanceUnits = (ListPreference) findPreference(key);

        String unit = prefs.getString(key, getString(R.string.pref_key_units_distance_default));

        mDistanceUnits.setSummary(unit);

    }

    private void setSummarySpeedUnits(SharedPreferences prefs) {
        String key = getString(R.string.pref_key_units_speed);

        if (mSpeedUnits == null)
            mSpeedUnits = (ListPreference) findPreference(key);

        String unit = prefs.getString(key, getString(R.string.pref_key_units_speed_default));

        mSpeedUnits.setSummary(unit);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_key_map_type)))
            setSummaryMapType(sharedPreferences);
        else if (key.equals(getString(R.string.pref_key_measure_interval)))
            setSummaryMeasureInterval(sharedPreferences);
        else if (key.equals(getString(R.string.pref_key_units_distance)))
            setSummaryDistanceUnits(sharedPreferences);
        else if (key.equals(getString(R.string.pref_key_units_speed)))
            setSummarySpeedUnits(sharedPreferences);
    }


}
