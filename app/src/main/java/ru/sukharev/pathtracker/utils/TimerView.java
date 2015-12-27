package ru.sukharev.pathtracker.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by hpc on 12/27/15.
 */
public class TimerView extends TextView {

    private final static int ONE_HOUR = 3600000;
    private DateFormat shortFormat = new SimpleDateFormat("mm:ss");
    private DateFormat longFormat = new SimpleDateFormat("hh:mm:ss");
    private long mTime;

    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setNewTime(0L);
    }

    public void setNewTime(long time) {
        mTime = time;
        Date date = new Date(time);
        if (time < ONE_HOUR)
            setText(shortFormat.format(date));
        else
            setText(longFormat.format(date));
    }

    public long getTime() {
        return mTime;
    }


}
