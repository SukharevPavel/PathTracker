package ru.sukharev.pathtracker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.DecimalFormat;

import ru.sukharev.pathtracker.R;

/**
 * File that used for changing displaying of default measurement units.
 */
public class Measurement {


    private final static int KM_POSITION_IN_STRING_ARRAY_RESOURCE = 0;
    private final static int MI_POSITION_IN_STRING_ARRAY_RESOURCE = 1;
    private final static int M_POSITION_IN_STRING_ARRAY_RESOURCE = 2;
    private final static int NM_POSITION_IN_STRING_ARRAY_RESOURCE = 3;
    private final static int HOUR_POSITION_IN_STRING_ARRAY_RESOURCE = 0;
    private final static int MINUTE_POSITION_IN_STRING_ARRAY_RESOURCE = 1;
    private final static int SECOND_POSITION_IN_STRING_ARRAY_RESOURCE = 2;
    private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    SharedPreferences mPreferences;
    Context mContext;


    public Measurement(Context ctx) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        mContext = ctx;
    }

    public String formatMeters(double metre) {
        StringBuilder builder = new StringBuilder();
        builder.append(DECIMAL_FORMAT.format(convertMeters(metre)));
        builder.append(Commons.SPACE);
        appendDistanceSuffix(builder);
        return builder.toString();

    }

    public void appendDistanceSuffix(StringBuilder builder) {

        String unit = mPreferences.getString(mContext.getString(R.string.pref_key_units_distance),
                mContext.getString(R.string.pref_key_units_distance_default));
        String[] unitArray = mContext.getResources().getStringArray(R.array.array_distance_units);

        if (unit.equals(unitArray[KM_POSITION_IN_STRING_ARRAY_RESOURCE])) {
            builder.append(mContext.getString(R.string.unit_kilometre_suffix));
        }
        if (unit.equals(unitArray[MI_POSITION_IN_STRING_ARRAY_RESOURCE])) {
            builder.append(mContext.getString(R.string.unit_mile_suffix));
        }
        if (unit.equals(unitArray[M_POSITION_IN_STRING_ARRAY_RESOURCE])) {
            builder.append(mContext.getString(R.string.unit_metre_suffix));
        }
        if (unit.equals(unitArray[NM_POSITION_IN_STRING_ARRAY_RESOURCE])) {
            builder.append(mContext.getString(R.string.unit_nautical_mile_suffix));
        }

    }

    public void appendTimeSuffix(StringBuilder builder) {

        String unit = mPreferences.getString(mContext.getString(R.string.pref_key_units_speed),
                mContext.getString(R.string.pref_key_units_speed_default));
        String[] unitArray = mContext.getResources().getStringArray(R.array.array_speed_units);

        if (unit.equals(unitArray[HOUR_POSITION_IN_STRING_ARRAY_RESOURCE])) {
            builder.append(mContext.getString(R.string.unit_hour_suffix));
        }
        if (unit.equals(unitArray[MINUTE_POSITION_IN_STRING_ARRAY_RESOURCE])) {
            builder.append(mContext.getString(R.string.unit_minute_suffix));

        }
        if (unit.equals(unitArray[SECOND_POSITION_IN_STRING_ARRAY_RESOURCE])) {
            builder.append(mContext.getString(R.string.unit_second_suffix));
        }

    }

    public double convertMeters(double metre) {
        String unit = mPreferences.getString(mContext.getString(R.string.pref_key_units_distance),
                mContext.getString(R.string.pref_key_units_distance_default));
        String[] unitArray = mContext.getResources().getStringArray(R.array.array_distance_units);
        //Kilometres
        //Miles
        //Metres
        //Nautical miles
        if (unit.equals(unitArray[KM_POSITION_IN_STRING_ARRAY_RESOURCE])) {
            return metre / Commons.METRES_IN_KILOMETRE;
        }
        if (unit.equals(unitArray[MI_POSITION_IN_STRING_ARRAY_RESOURCE])) {
            return metre / Commons.METRES_IN_MILE;

        }
        if (unit.equals(unitArray[M_POSITION_IN_STRING_ARRAY_RESOURCE])) {
            return metre;
        }
        if (unit.equals(unitArray[NM_POSITION_IN_STRING_ARRAY_RESOURCE])) {
            return metre / Commons.METRES_IN_NAUTICAL_MILE;
        }
        return metre;
    }

    public double convertPerSeconds(double unitPerSec) {
        String unit = mPreferences.getString(mContext.getString(R.string.pref_key_units_speed),
                mContext.getString(R.string.pref_key_units_speed_default));
        String[] unitArray = mContext.getResources().getStringArray(R.array.array_speed_units);

        if (unit.equals(unitArray[HOUR_POSITION_IN_STRING_ARRAY_RESOURCE])) {
            return unitPerSec * Commons.SECONDS_IN_HOUR;
        }
        if (unit.equals(unitArray[MINUTE_POSITION_IN_STRING_ARRAY_RESOURCE])) {
            return unitPerSec * Commons.SECONDS_IN_MINUTE;

        }
        if (unit.equals(unitArray[SECOND_POSITION_IN_STRING_ARRAY_RESOURCE])) {
            return unitPerSec;
        }

        return unitPerSec;
    }

    public double convertSeconds(double seconds) {
        String unit = mPreferences.getString(mContext.getString(R.string.pref_key_units_speed),
                mContext.getString(R.string.pref_key_units_speed_default));
        String[] unitArray = mContext.getResources().getStringArray(R.array.array_speed_units);

        if (unit.equals(unitArray[HOUR_POSITION_IN_STRING_ARRAY_RESOURCE])) {
            return seconds / Commons.SECONDS_IN_HOUR;
        }
        if (unit.equals(unitArray[MINUTE_POSITION_IN_STRING_ARRAY_RESOURCE])) {
            return seconds / Commons.SECONDS_IN_MINUTE;

        }
        if (unit.equals(unitArray[SECOND_POSITION_IN_STRING_ARRAY_RESOURCE])) {
            return Math.round(seconds);
        }

        return seconds;
    }

    public String formatTime(double seconds) {
        StringBuilder builder = new StringBuilder();
        builder.append(convertSeconds(seconds));
        builder.append(Commons.SPACE);
        appendTimeSuffix(builder);
        return builder.toString();
    }


    public String formatSpeed(double metrePerSec) {


        //Hours
        //Minutes
        //Seconds
        StringBuilder builder = new StringBuilder();
        builder.append(DECIMAL_FORMAT.format(convertPerSeconds(convertMeters(metrePerSec))));
        builder.append(Commons.SPACE);
        appendDistanceSuffix(builder);
        builder.append(Commons.SLASH);
        appendTimeSuffix(builder);
        return builder.toString();


    }

}
