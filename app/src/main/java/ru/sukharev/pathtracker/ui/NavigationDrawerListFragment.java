package ru.sukharev.pathtracker.ui;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.sukharev.pathtracker.R;
import ru.sukharev.pathtracker.provider.DatabaseHelper;
import ru.sukharev.pathtracker.utils.Measurement;
import ru.sukharev.pathtracker.utils.orm.MapPath;
import ru.sukharev.pathtracker.utils.orm.OrmLoader;

interface OnRecyclerViewClickListener {

    void onRecyclerViewClick(MapPath path);

}

interface ViewHolderContextMenuItemListener {

    void onContextMenuSelect(MapPath path);

}

/**
 * NavigationDrawer that shows the list of saved path and allows to interact with them
 */
public class NavigationDrawerListFragment extends Fragment implements LoaderManager.LoaderCallbacks,
        OnRecyclerViewClickListener {

    public final static int NO_SELECTION = -1;
    private final static int PATH_LOADER_ID = 1;
    private final static String TAG = "NavigationDrawer.java";
    private RecyclerView mRecyclerView;
    private PathAdapter mAdapter;
    private View mDrawerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private PathItemClickListener mPathListener;
    private MapPath mSelectedItem = null;



    public NavigationDrawerListFragment() {
        // Required empty public constructor
    }

    public void reloadList() {
        Log.i(TAG, "reload");
        final Loader loader = getLoaderManager().getLoader(PATH_LOADER_ID);
        if (loader != null && loader.isReset()) {
            Log.i(TAG, "restart");
            getLoaderManager().restartLoader(PATH_LOADER_ID, null, this);
        }
        getLoaderManager().getLoader(PATH_LOADER_ID).forceLoad();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        getLoaderManager().initLoader(PATH_LOADER_ID, null, this);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new PathAdapter(getContext(),
                new ArrayList<MapPath>(), this);
        mRecyclerView.setAdapter(mAdapter);
        //setListAdapter(mAdapter);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        //  registerForContextMenu(getListView());
        getLoaderManager().getLoader(PATH_LOADER_ID).forceLoad();
        mPathListener = (PathItemClickListener) getActivity();
        // getListView().setOnItemClickListener(mItemListener);
        selectItem(mSelectedItem);
    }

    private void selectItem(MapPath path) {
        if (path != null)
            mPathListener.onPathClick(path);
    }

    public void invalidateSelection() {
        mSelectedItem = null;
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.i(TAG, "context menu item selected pos = " + mAdapter.getSelected());
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete_item:
                try {
                    deletePathFromDatabase(mAdapter.getSelected());
                    mAdapter.remove(mAdapter.getSelected());
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
        Log.i(TAG, "on load finished");
        if (mAdapter != null) {
            Log.i(TAG, "replace list");
            mAdapter.replaceList((List<MapPath>) data);
        }
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

    @Override
    public void onRecyclerViewClick(MapPath path) {
        if (mPathListener != null) {
            mSelectedItem = path;
            selectItem(mSelectedItem);
        }
        if (mDrawerLayout != null)
            mDrawerLayout.closeDrawer(mDrawerView);
    }


    public interface PathItemClickListener {

        void onPathClick(MapPath path);

    }

    public static class PathAdapter extends RecyclerView.Adapter<PathAdapter.ViewHolder>
            implements ViewHolderContextMenuItemListener {

        private final Context mContext;
        private final DateFormat format = SimpleDateFormat.getDateTimeInstance();
        private Measurement mUnits;
        private List<MapPath> mObjects;
        private boolean doShowStartTime;
        private boolean doShowEndTime;
        private boolean doShowDistance;
        private boolean doShowVelocity;
        private MenuInflater mMenuInflater;
        private OnRecyclerViewClickListener mListener;
        private MapPath mSelectedPath;

        public PathAdapter(Context ctx, List<MapPath> list, OnRecyclerViewClickListener listener) {
            mObjects = list;
            mContext = ctx;
            mListener = listener;
            getListSettings(getContext());
            mMenuInflater = new MenuInflater(getContext());
        }

        public void replaceList(List<MapPath> newList) {
            mObjects = newList;
            mUnits = new Measurement(getContext());
            getListSettings(getContext());
            notifyDataSetChanged();
        }


        public Context getContext() {
            return mContext;
        }

        private void getListSettings(Context ctx) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            doShowStartTime = prefs.getBoolean(ctx.getString(R.string.pref_key_list_starttime),
                    ctx.getResources().getBoolean(R.bool.prefs_key_list_starttime_def));
            doShowEndTime = prefs.getBoolean(ctx.getString(R.string.pref_key_list_endtime),
                    ctx.getResources().getBoolean(R.bool.prefs_key_list_endtime_def));
            doShowDistance = prefs.getBoolean(ctx.getString(R.string.pref_key_list_distance),
                    ctx.getResources().getBoolean(R.bool.prefs_key_list_distance_def));
            doShowVelocity = prefs.getBoolean(ctx.getString(R.string.pref_key_list_velocity),
                    ctx.getResources().getBoolean(R.bool.prefs_key_list_velocity_def));
            Log.i(TAG, doShowStartTime + "  + " + doShowEndTime + " " + doShowDistance + " " + doShowVelocity);

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.i(TAG, "create holder");
            View view = LayoutInflater.from(mContext).
                    inflate(R.layout.navigation_drawer_list_item, parent, false);
            return new ViewHolder(view, mMenuInflater, this);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            Log.i(TAG, "bind holder pos=" + position);
            final MapPath path = getItem(position);
            holder.path = path;
            holder.name.setText(holder.path.getName());
            holder.startTime.setText(format.format(new Date(holder.path.getStartTime())));
            holder.endTime.setText(format.format(new Date(holder.path.getEndTime())));
            holder.distance.setText(mUnits.formatMeters(holder.path.getDistance()));
            holder.velocity.setText(mUnits.formatSpeed(holder.path.getAvgSpeed()));
            holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onRecyclerViewClick(path);
                }
            });
            setHolderVisibility(holder);
        }

        private void setHolderVisibility(ViewHolder holder) {
            if (!doShowStartTime) holder.startTime.setVisibility(View.GONE);
            if (!doShowEndTime) holder.endTime.setVisibility(View.GONE);
            if (!doShowDistance) holder.distance.setVisibility(View.GONE);
            if (!doShowVelocity) holder.velocity.setVisibility(View.GONE);
        }

        public MapPath getItem(int position) {
            return mObjects.get(position);
        }

        public void remove(MapPath path) {
            int pos = mObjects.indexOf(path);
            mObjects.remove(path);
            notifyItemRemoved(pos);
        }

        @Override
        public int getItemCount() {
            return mObjects.size();
        }

        @Override
        public void onContextMenuSelect(MapPath path) {
            mSelectedPath = path;
        }

        public MapPath getSelected() {
            return mSelectedPath;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder
                implements View.OnCreateContextMenuListener {

            final MenuInflater mMenuInflater;
            TextView name;
            TextView startTime;
            TextView endTime;
            TextView distance;
            TextView velocity;
            View mView;
            MapPath path;
            private ViewHolderContextMenuItemListener mListener;


            public ViewHolder(View itemView, MenuInflater inflater,
                              ViewHolderContextMenuItemListener listener) {
                super(itemView);
                mView = itemView;
                mListener = listener;
                itemView.setOnCreateContextMenuListener(this);
                name = (TextView) itemView.findViewById(R.id.list_item_name);
                startTime = (TextView) itemView.findViewById(R.id.list_start_time);
                endTime = (TextView) itemView.findViewById(R.id.list_end_time);
                distance = (TextView) itemView.findViewById(R.id.list_distance);
                velocity = (TextView) itemView.findViewById(R.id.list_velocity);
                mMenuInflater = inflater;
            }

            public void setOnClickListener(View.OnClickListener listener) {
                mView.setOnClickListener(listener);
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                mMenuInflater.inflate(R.menu.context_list_menu, menu);
                mListener.onContextMenuSelect(path);
            }
        }
    }

}
