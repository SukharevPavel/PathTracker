package ru.sukharev.pathtracker.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;


/**
 * View to show tracking time in info fragment.
 */
public class TimerView extends TextView {


    private long mTime;

    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setNewTime(0L);
    }

    public void setNewTime(long time) {
        mTime = time;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time) % Commons.SECONDS_IN_MINUTE;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time) % Commons.MINUTES_IN_HOUR;
        long hours = TimeUnit.MILLISECONDS.toHours(time);
        StringBuilder builder = new StringBuilder();
        if (time < Commons.MILLISECONDS_IN_ONE_HOUR) {
            builder.append(formatToTwoDigits(minutes));
            builder.append(Commons.COLON);
            builder.append(formatToTwoDigits(seconds));
        } else {
            builder.append(hours);
            builder.append(Commons.COLON);
            builder.append(formatToTwoDigits(minutes));
            builder.append(Commons.COLON);
            builder.append(formatToTwoDigits(seconds));
        }
        setText(builder.toString());
    }

    public String formatToTwoDigits(long value) {
        if (value < 10) return String.valueOf(0) + String.valueOf(value);
        else return String.valueOf(value);
    }


    public long getTime() {
        return mTime;
    }


}
