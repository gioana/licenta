package com.diploma.android.iruntracking.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.diploma.android.iruntracking.R;
import com.diploma.android.iruntracking.RunManager;
import com.diploma.android.iruntracking.activities.RunMapActivity;
import com.diploma.android.iruntracking.loaders.RunListCursorLoader;
import com.diploma.android.iruntracking.loaders.SQLiteCursorLoader;
import com.diploma.android.iruntracking.helpers.RunDatabaseHelper.RunCursor;
import com.diploma.android.iruntracking.model.Run;
import com.diploma.android.iruntracking.utils.Formatter;
import com.diploma.android.iruntracking.utils.SwipeDismissListViewTouchListener;

public class RunListFragment extends ListFragment implements LoaderCallbacks<Cursor> {
    private RunManager mRunManager;
    private RunListFragment mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize the loader to load the list of runs
        getLoaderManager().initLoader(0, null, this);
        mContext = this;
        mRunManager = RunManager.get(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_run_list, container, false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // we only ever load the runs, so assume this is the case
        return new RunListCursorLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // create an adapter to point at this cursor
        RunCursorAdapter adapter = new RunCursorAdapter(getActivity(), (RunCursor) cursor);
        setListAdapter(adapter);

        ListView listView = getListView();
        // Create a ListView-specific touch listener. ListViews are given special treatment because
        // by default they handle touches for their list items... i.e. they're in charge of drawing
        // the pressed state (the list selector), handling list item clicks, etc.
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    long runId = listView.getAdapter().getItemId(position);
                                    mRunManager.deleteRun(runId);
                                }
                                getLoaderManager().restartLoader(0, null, mContext);
                            }
                        });
        listView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listView.setOnScrollListener(touchListener.makeScrollListener());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // stop using the cursor (via the adapter)
        setListAdapter(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // the id argument will be the Run ID; CursorAdapter gives us this for free
        Intent i = new Intent(getActivity(), RunMapActivity.class);
        i.putExtra(RunMapActivity.EXTRA_RUN_ID, id);
        startActivity(i);
    }

    private class RunCursorAdapter extends CursorAdapter {
        private RunCursor mRunCursor;

        public RunCursorAdapter(Context context, RunCursor cursor) {
            super(context, cursor, 0);
            mRunCursor = cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // use a layout inflater to get a row view
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.row_run, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // get the run for the current row
            Run run = mRunCursor.getRun();

            TextView runStartDateTextView = (TextView) view.findViewById(R.id.run_start_date_textView);
            TextView distanceTextView = (TextView) view.findViewById(R.id.distance_textView);
            TextView duration = (TextView) view.findViewById(R.id.duration_textView);
            TextView minKm = (TextView) view.findViewById(R.id.min_km_textView);
            TextView calories = (TextView) view.findViewById(R.id.calories_textView);

            runStartDateTextView.setText(Formatter.formatDate(run.getStartDate()));
            distanceTextView.setText(Formatter.formatDistance(run.getDistance()));
            duration.setText(Formatter.formatDuration(run.getDuration()));
            minKm.setText(Formatter.formatPace(run.getAvgPace()));
            calories.setText(String.valueOf(run.getCalories()));
        }
    }
}
