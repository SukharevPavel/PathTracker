package ru.sukharev.pathtracker.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.sukharev.pathtracker.R;
import ru.sukharev.pathtracker.utils.Measurement;


public class InfoFragment extends Fragment {

    public final static String TAG = "InfoFragment.java";

    public final static String ARG_START_TIME = "start_time";
    public final static String ARG_CUR_TIME = "cur_time";
    public final static String ARG_AVG_SPEED = "avg_speed";
    public final static String ARG_CUR_SPEED = "cur_speed";
    public final static String ARG_DISTANCE = "distance";

    private DateFormat format = SimpleDateFormat.getTimeInstance();


    private TextView mTimeText;
    private TextView mDistText;
    private TextView mSpeedText;
    private TextView mAvgSpeedText;

    private Measurement mUnits;

    private long startTime;
    private long curTime;
    private double dist;
    private double curSpeed;
    private double avgSpeed;

    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUnits = new Measurement(getContext());
        initFields();
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info, container, false);


        mTimeText = (TextView) v.findViewById(R.id.info_timer);
        mDistText = (TextView) v.findViewById(R.id.info_dist);
        mSpeedText = (TextView) v.findViewById(R.id.info_cur_speed);
        mAvgSpeedText = (TextView) v.findViewById(R.id.info_avg_speed);
        setTextFields();
        return v;
    }


    private void initFields(){
        Bundle bundle = getArguments();
        if (bundle != null) {
            Log.i(TAG, "init");
            startTime = bundle.getLong(ARG_START_TIME);
            curTime = bundle.getLong(ARG_CUR_TIME);
            dist = bundle.getDouble(ARG_DISTANCE);
            curSpeed = bundle.getDouble(ARG_CUR_SPEED);
            avgSpeed = bundle.getDouble(ARG_AVG_SPEED);
        }
    }

    private void setTextFields(){
        mTimeText.setText(String.valueOf(format.format(new Date(curTime - startTime))));
        mDistText.setText(mUnits.formatMeters(dist));
        mSpeedText.setText(mUnits.formatSpeed(curSpeed));
        mAvgSpeedText.setText(mUnits.formatSpeed(avgSpeed));

    }

    public void updateFields(long startTime, long curTime, double dist, double curSpeed,
                              double avgSpeed){
        Log.i(TAG, "update");
        Log.i(TAG, "start time = " + startTime + "\n curTime = " + curTime);
        Log.i(TAG, "start time = " + format.format(new Date(startTime)) + "\n curTime = " + format.format(new Date(curTime)));
        this.startTime = startTime;
        this.curTime = curTime;
        this.dist = dist;
        this.curSpeed = curSpeed;
        this.avgSpeed = avgSpeed;
        setTextFields();
    }


}
