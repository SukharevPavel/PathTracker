package ru.sukharev.pathtracker.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import ru.sukharev.pathtracker.R;
import ru.sukharev.pathtracker.utils.MapHelper;
import ru.sukharev.pathtracker.utils.orm.MapPath;

/**
 * A simple {@link Fragment} subclass.
 */
public class ControlFragment extends Fragment {

    private final static String TAG = "ControlFragment.java";
    private MapHelper mHelper;
    private final View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick " + mHelper.isServiceStarted());
            if (mHelper.isServiceStarted()) mHelper.stopService();
            else mHelper.startService();
        }
    };
    private ImageButton mControlButton;
    private ImageButton mCurrentPathButton;
    private ImageButton mSaveButton;
    private ImageButton mClearButton;
    private ImageButton mInfoButton;
    private ControlFragmentListener mListener;
    private final View.OnClickListener mCurrentButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListener.onCurrentButtonClick();
            mHelper.getList();
        }
    };

    private final View.OnClickListener mSaveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListener.onSaveButtonClick();
        }
    };
    private final View.OnClickListener mClearButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListener.onClearButtonClick();
            if (mHelper.isServiceStarted()) mHelper.clearData();
        }
    };
    private final View.OnClickListener mInfoButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListener.onInfoButtonListener();
        }
    };

    public ControlFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstancceState){
        super.onCreate(savedInstancceState);
        mHelper = MapHelper.getInstance(getActivity().getApplicationContext());
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control, container, false);
        mListener = (ControlFragmentListener) getActivity();
        mHelper.setListeners(getActivity());

        mControlButton = (ImageButton) view.findViewById(R.id.button_control_service);
        mControlButton.setOnClickListener(mButtonListener);

        mCurrentPathButton = (ImageButton) view.findViewById(R.id.button_show_current);
        mCurrentPathButton.setOnClickListener(mCurrentButtonListener);

        mClearButton = (ImageButton) view.findViewById(R.id.button_clear);
        mClearButton.setOnClickListener(mClearButtonListener);

        mSaveButton = (ImageButton) view.findViewById(R.id.button_save);
        mSaveButton.setOnClickListener(mSaveButtonListener);

        mInfoButton = (ImageButton) view.findViewById(R.id.button_info);
        mInfoButton.setOnClickListener(mInfoButtonListener);

        changeButtonIcon(mHelper.isServiceStarted());
        mHelper.getList();
        return view;
    }


    public void showCurrentPathButton(boolean visibility) {
        if (visibility) mCurrentPathButton.setVisibility(View.VISIBLE);
        else mCurrentPathButton.setVisibility(View.INVISIBLE);
    }

    public void changeButtonIcon(boolean isStart) {
        if (mControlButton != null) {
            if (isStart) mControlButton.setImageResource(R.drawable.pause_circle);
            else mControlButton.setImageResource(R.drawable.play_circle);
        }
    }


    public void saveToDatabase(String name, double distance, double avgSpeed) {
        mHelper.saveToDatabase(name, distance, avgSpeed);
    }

    public void clearData(){
        mHelper.clearData();
    }

    public void updatePath(MapPath path, String newName) {
        mHelper.updatePath(path, newName);
    }

    public void deletePath(MapPath path) {
        mHelper.deletePath(path);
    }

    public interface ControlFragmentListener {

        void onCurrentButtonClick();

        void onSaveButtonClick();

        void onClearButtonClick();

        void onInfoButtonListener();

    }


}
