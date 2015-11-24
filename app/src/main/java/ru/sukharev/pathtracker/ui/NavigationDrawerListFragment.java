package ru.sukharev.pathtracker.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ru.sukharev.pathtracker.R;
import ru.sukharev.pathtracker.provider.DatabaseHelper;
import ru.sukharev.pathtracker.utils.orm.MapPath;
import ru.sukharev.pathtracker.utils.orm.OrmLoader;

/**
 * NavigationDrawer taht shows the list of saved path and allows to interact with them
 */
public class NavigationDrawerListFragment extends ListFragment implements LoaderManager.LoaderCallbacks {

    private final static int PATH_LOADER_ID = 1;
    private PathAdapter mAdapter;

    private View mDrawerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private PathItemClickListener mPathListener;
    private AdapterView.OnItemClickListener mItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mPathListener != null)
                mPathListener.onPathClick(mAdapter.getItem(position));
            if (mDrawerLayout != null)
                mDrawerLayout.closeDrawer(mDrawerView);
        }
    };



    public NavigationDrawerListFragment() {
        // Required empty public constructor
    }

    public void reloadList() {
        getLoaderManager().getLoader(PATH_LOADER_ID).forceLoad();
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(PATH_LOADER_ID, null, this);
        mAdapter = new PathAdapter(getContext(),
                R.layout.navigation_drawer_list_item,
                new ArrayList<MapPath>());
        setListAdapter(mAdapter);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
        getLoaderManager().getLoader(PATH_LOADER_ID).forceLoad();
        mPathListener = (PathItemClickListener) getActivity();
        getListView().setOnItemClickListener(mItemListener);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.context_list_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete_item:
                try {
                    deletePathFromDatabase(mAdapter.getItem(info.position));
                    mAdapter.remove(mAdapter.getItem(info.position));
                } catch (SQLException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), getString(R.string.error_delete_path), Toast.LENGTH_LONG).show();
                    return super.onContextItemSelected(item);
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void deletePathFromDatabase(MapPath path) throws SQLException {
        DatabaseHelper.getInstance(getContext()).getPathDAO().delete(path);
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new OrmLoader(getContext(),
                DatabaseHelper.getInstance(getContext()),
                MapPath.TABLE_NAME,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if (mAdapter != null)
            mAdapter.replaceList((List<MapPath>) data);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    public void setUp(DrawerLayout drawerLayout, Toolbar toolbar, int fragmentId) {
        mDrawerLayout = drawerLayout;
        mDrawerView = getActivity().findViewById(R.id.navigation_drawer_list_fragment);
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                toolbar,             /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }


    public interface PathItemClickListener {

        void onPathClick(MapPath path);

    }


    public static class PathAdapter extends ArrayAdapter<MapPath>{

        private int mResource;


        public PathAdapter(Context context, int resource,  List<MapPath> objects) {
            super(context, resource, objects);
            mResource = resource;
        }

        public void replaceList(List<MapPath> newList){
            //T0D0 can be done faster, if check equality of lists
            clear();
            for (MapPath path : newList)
                add(path);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //return super.getView(position, convertView, parent);
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(mResource, parent, false);
                holder.name = (TextView) convertView.findViewById(R.id.list_item_name);
                holder.startTime = (TextView) convertView.findViewById(R.id.list_start_time);
                holder.endTime = (TextView) convertView.findViewById(R.id.list_end_time);
                convertView.setTag(holder);
            } else holder = (ViewHolder) convertView.getTag();
            holder.name.setText(getItem(position).getName());
            holder.startTime.setText(String.valueOf(getItem(position).getStartTime()));
            holder.endTime.setText(String.valueOf(getItem(position).getEndTime()));
            return convertView;
        }

        public static class ViewHolder{
            TextView name;
            TextView startTime;
            TextView endTime;
        }
    }
}
