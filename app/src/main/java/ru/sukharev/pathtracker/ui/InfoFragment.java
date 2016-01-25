package ru.sukharev.pathtracker.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.sukharev.pathtracker.R;
import ru.sukharev.pathtracker.utils.Measurement;
import ru.sukharev.pathtracker.utils.TimerView;

/**
 * Info fragment (CardView) that uses for showing info about current path - speed, time, distance
 */
public class InfoFragment extends Fragment {

    public final static String TAG = "InfoFragment.java";


    public final static String ARG_TOTAL_TIME = "cur_time";
    public final static String ARG_AVG_SPEED = "avg_speed";
    public final static String ARG_CUR_SPEED = "cur_speed";
    public final static String ARG_DISTANCE = "distance";

    private final static int NO_DATA = 0;


    private TimerView mTimeText;
    private TextView mDistText;
    private TextView mSpeedText;
    private TextView mAvgSpeedText;

    private Measurement mUnits;


    private long totalTime;
    private double dist;
    private double curSpeed;
    private double avgSpeed;
    private InfoFragmentListener mListener;

    public InfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info, container, false);

        mListener = (InfoFragmentListener) getActivity();

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onInfoFragmentClick();
            }
        });
        mTimeText = (TimerView) v.findViewById(R.id.info_timer);
        mDistText = (TextView) v.findViewById(R.id.info_dist);
        mSpeedText = (TextView) v.findViewById(R.id.info_cur_speed);
        mAvgSpeedText = (TextView) v.findViewById(R.id.info_avg_speed);
        setTextFields();
        return v;
    }


    private void initFields(){
        Bundle bundle = getArguments();
        if (bundle != null) {
            totalTime = bundle.getLong(ARG_TOTAL_TIME);
            dist = bundle.getDouble(ARG_DISTANCE);
            curSpeed = bundle.getDouble(ARG_CUR_SPEED);
            avgSpeed = bundle.getDouble(ARG_AVG_SPEED);
        }
    }


    private void setTextFields(){
        mUnits = new Measurement(getContext());
        mTimeText.setNewTime(totalTime);
        mDistText.setText(mUnits.formatMeters(dist));
        mSpeedText.setText(mUnits.formatSpeed(curSpeed));
        mAvgSpeedText.setText(mUnits.formatSpeed(avgSpeed));

    }

    public void updateFields(long totalTime, double dist, double curSpeed,
                              double avgSpeed){
        Log.i(TAG, "update");
        this.totalTime = totalTime;
        this.dist = dist;
        this.curSpeed = curSpeed;
        this.avgSpeed = avgSpeed;
        setTextFields();
    }

    public void dropAllFields() {
        this.avgSpeed = this.curSpeed = this.dist = this.totalTime = NO_DATA;
        setTextFields();
    }


    public interface InfoFragmentListener {

        void onInfoFragmentClick();


    }


}
