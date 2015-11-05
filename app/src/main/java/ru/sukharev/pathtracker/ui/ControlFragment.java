package ru.sukharev.pathtracker.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.sukharev.pathtracker.R;
import ru.sukharev.pathtracker.utils.MapHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class ControlFragment extends Fragment {

    private MapHelper mHelper;

    private final View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

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
        mHelper.setCallbacks((MapHelper.MapHelperCallbacks) getActivity());
        view.findViewById(R.id.button_control_service).setOnClickListener(mButtonListener);
        return view;
    }



}
