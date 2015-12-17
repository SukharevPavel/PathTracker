package ru.sukharev.pathtracker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ru.sukharev.pathtracker.R;

/**
 * File that used for changing displaying of default measurement units.
 */
public class Measurement {

    public final static String SPACE = " ";
    public final static double METRES_IN_KILOMETRE = 1000;
    public final static double METRES_IN_MILE = 1609.344;
    public final static double METRES_IN_NAUTICAL_MILE = 1852;
    SharedPreferences mPreferences;
    Context mContext;

    public Measurement(Context ctx) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        mContext = ctx;
    }

    public String formatMeters(double metre) {
        String unit = mPreferences.getString(mContext.getString(R.string.pref_key_units_distance),
                mContext.getString(R.string.pref_key_units_distance_default));
        String[] unitArray = mContext.getResources().getStringArray(R.array.array_distance_units);
        //Kilometres
        //Miles
        //Metres
        //Nautical miles
        StringBuilder builder = new StringBuilder();
        if (unit.equals(unitArray[0])) {
            builder.append(String.valueOf(metre / METRES_IN_KILOMETRE));
            builder.append(SPACE);
            builder.append(mContext.getString(R.string.unit_kilometre_suffix));
        }
        if (unit.equals(unitArray[1])) {
            builder.append(String.valueOf(metre / METRES_IN_MILE));
            builder.append(SPACE);
            builder.append(mContext.getString(R.string.unit_mile_suffix));
        }
        if (unit.equals(unitArray[2])) {
            builder.append(String.valueOf(metre));
            builder.append(SPACE);
            builder.append(mContext.getString(R.string.unit_metre_suffix));
        }
        if (unit.equals(unitArray[3])) {
            builder.append(String.valueOf(metre / METRES_IN_NAUTICAL_MILE));
            builder.append(SPACE);
            builder.append(mContext.getString(R.string.unit_nautical_mile_suffix));
        }
        return builder.toString();

    }

}
