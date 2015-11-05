package ru.sukharev.pathtracker.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    private MapHelper mHelper;
    private Button mControlButton;

    private final View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mHelper.isServiceStarted()) mHelper.stopService();
            else mHelper.startService();
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
        mHelper.setListeners((MapHelper.MapHelperListener) getActivity());
        mControlButton = (Button) view.findViewById(R.id.button_control_service);
        mControlButton.setOnClickListener(mButtonListener);
        return view;
    }

    public void changeButtonText(String text){
        if (mControlButton!= null) mControlButton.setText(text);
    }



}
