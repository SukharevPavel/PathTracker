package ru.sukharev.pathtracker.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ru.sukharev.pathtracker.R;
import ru.sukharev.pathtracker.utils.MapHelper;

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
    private Button mControlButton;
    private Button mCurrentPathButton;
    private Button mSaveButton;
    private Button mClearButton;
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
        }
    };

    public ControlFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstancceState){
        super.onCreate(savedInstancceState);
        mHelper = new MapHelper(getActivity().getApplicationContext());
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control, container, false);
        mListener = (ControlFragmentListener) getActivity();
        mHelper.setListeners(getActivity());

        mControlButton = (Button) view.findViewById(R.id.button_control_service);
        mControlButton.setOnClickListener(mButtonListener);

        mCurrentPathButton = (Button) view.findViewById(R.id.button_show_current);
        mCurrentPathButton.setOnClickListener(mCurrentButtonListener);

        mClearButton = (Button) view.findViewById(R.id.button_clear);
        mClearButton.setOnClickListener(mClearButtonListener);

        mSaveButton = (Button) view.findViewById(R.id.button_save);
        mSaveButton.setOnClickListener(mSaveButtonListener);


        if (mHelper.isServiceStarted()) changeButtonText(getString(R.string.button_stop_service));
        mHelper.getList();

        return view;
    }

    public void showCurrentPathButton(boolean visibility) {
        if (visibility) mCurrentPathButton.setVisibility(View.VISIBLE);
        else mCurrentPathButton.setVisibility(View.INVISIBLE);
    }

    public void changeButtonText(String text){
        if (mControlButton!= null) mControlButton.setText(text);
    }

    public void saveToDatabase(String name) {
        mHelper.saveToDatabase(name);
    }

    public void clearData(){
        mHelper.clearData();
    }

    public interface ControlFragmentListener {

        void onCurrentButtonClick();

        void onSaveButtonClick();

        void onClearButtonClick();

    }


}
