package ru.sukharev.pathtracker.ui;


import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import ru.sukharev.pathtracker.R;
import ru.sukharev.pathtracker.provider.DatabaseHelper;
import ru.sukharev.pathtracker.ui.dialog.PathRenamingFragment;
import ru.sukharev.pathtracker.utils.Measurement;
import ru.sukharev.pathtracker.utils.orm.MapPath;
import ru.sukharev.pathtracker.utils.orm.OrmLoader;

interface OnRecyclerViewClickListener {

    void onRecyclerViewClick(MapPath path);

}

interface ViewHolderContextMenuItemListener {

    void onContextMenuSelect(MapPath path);

    void onCheckedChangeListener(MapPath path, boolean flag);

}

/**
 * NavigationDrawer that shows the list of saved path and allows to interact with them
 */
public class NavigationDrawerListFragment extends Fragment implements LoaderManager.LoaderCallbacks,
        OnRecyclerViewClickListener, PathRenamingFragment.DialogPathRenamingListener {

    private final static int PATH_LOADER_ID = 1;
    private final static String TAG = "NavigationDrawer.java";
    private final static String PATH_RENAMING_FRAGMENT_TAG = "path_renaming_tag";
    private RecyclerView mRecyclerView;
    private PathAdapter mAdapter;
    private View mDrawerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationDrawerListener mPathListener;
    private MapPath mSelectedItem = null;
    private Toolbar mDrawerToolbar;
    private PathRenamingFragment mPathRenamingFragment;
    private SearchView mSearchView;



    public NavigationDrawerListFragment() {
        // Required empty public constructor
    }

    public void reloadList() {
        final Loader loader = getLoaderManager().getLoader(PATH_LOADER_ID);
        if (loader != null && loader.isReset()) {
            getLoaderManager().restartLoader(PATH_LOADER_ID, null, this);
        }
        getLoaderManager().getLoader(PATH_LOADER_ID).forceLoad();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new PathAdapter(getContext(),
                new ArrayList<MapPath>(), this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        getLoaderManager().initLoader(PATH_LOADER_ID, null, this);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new PathAdapter.SimpleDividerItemDecoration(
                getActivity()
        ));

        mRecyclerView.setAdapter(mAdapter);

        mDrawerToolbar = (Toolbar) v.findViewById(R.id.drawer_toolbar);
        setUpDrawerToolbar();
        //setListAdapter(mAdapter);
        return v;
    }

    private void setUpDrawerToolbar() {

        mDrawerToolbar.setTitle(getString(R.string.navigation_drawer_toolbar));
        mDrawerToolbar.inflateMenu(R.menu.menu_navigation_drawer);
        Menu menu = mDrawerToolbar.getMenu();

        setUpSearchView(menu);

        mDrawerToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_delete_selected: {
                        deleteSelected();
                        return true;
                    }
                    default:
                        return false;

                }
            }
        });

    }


    private void setUpSearchView(Menu menu) {
        SearchManager searchManager =
                (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        mSearchView =
                (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applyRegex(query);
                hideKeyboard();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applyRegex(newText);
                return true;
            }
        });

    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void applyRegex(String text) {
        mAdapter.applyRegexToList(text);
    }


    private void deleteSelected() {
        Map<MapPath, Boolean> map = mAdapter.getCheckedMap();
        try {
            for (MapPath path : map.keySet()) {
                if (map.get(path)) {
                    deletePathFromDatabase(path);
                    mAdapter.remove(path);
                }
            }
        } catch (SQLException e) {
            Toast.makeText(getContext(), getString(R.string.error_delete_path), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        //reloadList();
        mPathListener = (NavigationDrawerListener) getActivity();
        selectItem(mSelectedItem);

    }

    private void selectItem(MapPath path) {
        if (path != null) {
            mPathListener.onPathClick(path);
            mAdapter.setSelected(path);
        }
    }

    public void invalidateSelection() {
        mSelectedItem = null;
        mAdapter.setSelected(null);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
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
            case R.id.rename_item:
                showPathRenamingFragment(mAdapter.getSelected().getName());
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void showPathRenamingFragment(String oldName) {
        mPathRenamingFragment = new PathRenamingFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PathRenamingFragment.ARG_NAME, oldName);
        mPathRenamingFragment.setArguments(bundle);
        mPathRenamingFragment.setTargetFragment(this, 0);
        mPathRenamingFragment.show(getActivity().getSupportFragmentManager(), PATH_RENAMING_FRAGMENT_TAG);
    }

    private void deletePathFromDatabase(MapPath path) throws SQLException {
        mPathListener.onPathDelete(path);
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
        if (mAdapter != null) {
            mAdapter.replaceList((List<MapPath>) data);
        }
        applyRegex(String.valueOf(mSearchView.getQuery()));
    }


    @Override
    public void onLoaderReset(Loader loader) {

    }

    public void setUp(DrawerLayout drawerLayout, Toolbar toolbar, int fragmentId) {
        mDrawerLayout = drawerLayout;
        mDrawerView = getActivity().findViewById(R.id.navigation_view);
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

    @Override
    public void onNewName(String name) {
        mPathListener.onRenamingPath(mAdapter.getSelected(), name);
    }


    public interface NavigationDrawerListener {

        void onPathClick(MapPath path);

        void onRenamingPath(MapPath path, String newName);

        void onPathDelete(MapPath path);

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
        private Map<MapPath, Boolean> mapOfCheckedPaths;
        private List<MapPath> mSavedObjects;
        private MapPath selectedPath;

        public PathAdapter(Context ctx, List<MapPath> list, OnRecyclerViewClickListener listener) {
            mObjects = list;
            mContext = ctx;
            mListener = listener;
            getListSettings(getContext());
            mMenuInflater = new MenuInflater(getContext());
            mSavedObjects = new ArrayList<>(mObjects);
            mapOfCheckedPaths = new HashMap<>(mObjects.size());
        }

        public void replaceList(List<MapPath> newList) {
            mObjects = newList;
            if (mObjects != null)
                mSavedObjects = new ArrayList<>(mObjects);
            else mSavedObjects = new ArrayList<>();
            mUnits = new Measurement(getContext());
            getListSettings(getContext());
            notifyDataSetChanged();
        }

        public void applyRegexToList(String regex) {
            mObjects = new ArrayList<>(mSavedObjects);
            if (!regex.isEmpty()) {
                Pattern p = Pattern.compile(regex + ".*");
                List<MapPath> deletePath = new ArrayList<>(mObjects);
                for (MapPath path : deletePath)
                    if (!p.matcher(path.getName()).matches()) {
                        mObjects.remove(path);
                    }

            }
            notifyDataSetChanged();

        }

        public Map<MapPath, Boolean> getCheckedMap() {
            return mapOfCheckedPaths;
        }

        public boolean isPathChecked(MapPath path) {
            if (mapOfCheckedPaths.containsKey(path))
                return mapOfCheckedPaths.get(path);
            else return false;
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

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).
                    inflate(R.layout.navigation_drawer_list_item, parent, false);
            view.setFocusable(true);
            return new ViewHolder(view, mMenuInflater, this);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            final MapPath path = getItem(position);
            holder.path = path;
            holder.name.setText(holder.path.getName());
            holder.startTime.setText(format.format(new Date(holder.path.getStartTime())));
            holder.endTime.setText(format.format(new Date(holder.path.getEndTime())));
            holder.distance.setText(mUnits.formatMeters(holder.path.getDistance()));
            holder.velocity.setText(mUnits.formatSpeed(holder.path.getAvgSpeed()));
            holder.checkbox.setChecked(isPathChecked(path));
            if (path.equals(selectedPath)) holder.setSelected(true);
            else holder.setSelected(false);
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
            else holder.startTime.setVisibility(View.VISIBLE);
            if (!doShowEndTime) holder.endTime.setVisibility(View.GONE);
            else holder.endTime.setVisibility(View.VISIBLE);
            if (!doShowDistance) holder.distance.setVisibility(View.GONE);
            else holder.distance.setVisibility(View.VISIBLE);
            if (!doShowVelocity) holder.velocity.setVisibility(View.GONE);
            else holder.velocity.setVisibility(View.VISIBLE);
        }

        public MapPath getItem(int position) {
            return mObjects.get(position);
        }

        public void remove(MapPath path) {
            int pos = mObjects.indexOf(path);
            if (mObjects.contains(path)) {
                mObjects.remove(path);
                notifyItemRemoved(pos);
            }
            mSavedObjects.remove(path);
        }

        @Override
        public int getItemCount() {
            return mObjects.size();
        }

        @Override
        public void onContextMenuSelect(MapPath path) {
            mSelectedPath = path;
        }

        @Override
        public void onCheckedChangeListener(MapPath path, boolean flag) {
            mapOfCheckedPaths.put(path, flag);
        }

        public MapPath getSelected() {
            return mSelectedPath;
        }

        public void setSelected(MapPath path) {
            if (selectedPath != null && mObjects.contains(selectedPath))
                notifyItemChanged(mObjects.indexOf(selectedPath));
            selectedPath = path;
            if (selectedPath != null && mObjects.contains(selectedPath))
                notifyItemChanged(mObjects.indexOf(selectedPath));
        }

        public static class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
            private Drawable mDivider;

            public SimpleDividerItemDecoration(Context context) {
                mDivider = ContextCompat.getDrawable(context, R.drawable.recycler_view_divider);
            }

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                int left = parent.getPaddingLeft();
                int right = parent.getWidth() - parent.getPaddingRight();

                int childCount = parent.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = parent.getChildAt(i);

                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                    int top = child.getBottom() + params.bottomMargin;
                    int bottom = top + mDivider.getIntrinsicHeight();

                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(c);
                }
            }
        }

        public static class ViewHolder extends RecyclerView.ViewHolder
                implements View.OnCreateContextMenuListener {

            final MenuInflater mMenuInflater;
            TextView name;
            TextView startTime;
            TextView endTime;
            TextView distance;
            TextView velocity;
            CheckBox checkbox;
            View mView;
            MapPath path;
            private ViewHolderContextMenuItemListener mListener;
            private CheckBox.OnCheckedChangeListener mCheckboxListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mListener.onCheckedChangeListener(path, isChecked);
                }
            };


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
                checkbox = (CheckBox) itemView.findViewById(R.id.list_checkbox);
                checkbox.setOnCheckedChangeListener(mCheckboxListener);
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

            public void setSelected(boolean isSelected) {
                if (isSelected) mView.setBackgroundResource(R.drawable.recycler_item_focused);
                else mView.setBackgroundResource(R.drawable.recycler_item_idle);
            }
        }
    }

}
