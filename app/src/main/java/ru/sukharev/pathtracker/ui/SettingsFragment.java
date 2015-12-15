package ru.sukharev.pathtracker.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import ru.sukharev.pathtracker.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static String[] mapTypes;
    private ListPreference mMapType;
    private ListPreference mMeasureInterval;

    public SettingsFragment() {
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        setSummary();
    }

    private void setSummary() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setSummaryMapType(prefs);
        setSummaryMeasureInterval(prefs);
    }

    @Override
    public void onResume() {
        super.onResume();
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
        if (mMapType == null)
            mMapType = (ListPreference) findPreference(getString(R.string.pref_key_map_type));

        int mapType = Integer.parseInt(prefs.getString(getString(R.string.pref_key_map_type),
                getString(R.string.pref_key_map_type_def_result)));
        if (mapTypes == null)
            mapTypes = getResources().getStringArray(R.array.array_map_types);
        mMapType.setSummary(mapTypes[mapType - 1]);
    }

    private void setSummaryMeasureInterval(SharedPreferences prefs) {
        if (mMeasureInterval == null)
            mMeasureInterval = (ListPreference) findPreference(getString(R.string.pref_key_measure_interval));

        String interval = prefs.getString(getString(R.string.pref_key_measure_interval),
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
