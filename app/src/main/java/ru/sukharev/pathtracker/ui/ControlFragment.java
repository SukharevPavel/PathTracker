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


    private final View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    public ControlFragment() {
        // Required empty public constructor
        new MapHelper(getContext());
    }


    @Override
    public void onCreate(Bundle savedInstancceState){
        super.onCreate(savedInstancceState);
        setRetainInstance(true);
        //T0D0 init helper
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_control, container, false);
    }


}
