package ru.sukharev.pathtracker.ui;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.sukharev.pathtracker.R;

/**
 * NavigationDrawer taht shows the list of saved path and allows to interact with them
 */
public class NavigationDrawerListFragment extends ListFragment {


    public NavigationDrawerListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
    }


}
