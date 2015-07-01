package com.diploma.android.iruntracking.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.diploma.android.iruntracking.R;
import com.diploma.android.iruntracking.RunManager;
import com.diploma.android.iruntracking.activities.RunActivity;
import com.diploma.android.iruntracking.receivers.TrackingLocationReceiver;
import com.diploma.android.iruntracking.activities.RunMapActivity;
import com.diploma.android.iruntracking.model.Run;
import com.diploma.android.iruntracking.utils.Calculator;
import com.diploma.android.iruntracking.utils.Formatter;

public class RunFragment extends Fragment {
    private static final String TAG = "RunFragment";

    private BroadcastReceiver mLocationReceiver = new TrackingLocationReceiver() {

        @Override
        protected void onLocationReceived(Context context, Location loc) {

            if (!mRunManager.isTrackingRun(mRun)) {
                return;
            }
            if (mLastLocation != null) {
                mRun.setDistance(mRun.getDistance() + mLastLocation.distanceTo(loc));
            }
            mLastLocation = loc;
            if (isVisible()) {
                updateUI();
            }
        }

        @Override
        protected void onProviderEnabledChanged(boolean enabled) {
            int toastText = enabled ? R.string.gps_enabled : R.string.gps_disabled;
            Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
        }

    };

    private RunManager mRunManager;

    private Run mRun;
    private Location mLastLocation;
    private long mTime;

    private View mView;
    private Button mStartButton;
    private Button mPauseButton;
    private Button mStopButton;
    private Button mMapButton;
    private TextView mDurationTextView;
    private TextView mDistanceTextView;
    private TextView mAvgPaceTextView;
    private TextView mCurrentPaceTextView;
    private TextView mCaloriesTextView;
    private long mPausedSeconds;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mRunManager = RunManager.get(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_run, container, false);

        mDurationTextView = (TextView) mView.findViewById(R.id.run_duration_textView);
        mDistanceTextView = (TextView) mView.findViewById(R.id.run_distance_textView);
        mAvgPaceTextView = (TextView) mView.findViewById(R.id.run_pace_textView);
        mCurrentPaceTextView = (TextView) mView.findViewById(R.id.run_current_pace_textView);
        mCaloriesTextView = (TextView) mView.findViewById(R.id.run_calories_textView);

        mStartButton = (Button) mView.findViewById(R.id.run_start_button);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRun == null) {
                    mRun = mRunManager.startNewRun();
                } else {
                    mRunManager.startTrackingRun(mRun);
                }
                setButtonsVisibility(false);
                mTime = System.currentTimeMillis();
                updateUI();
            }
        });

        mPauseButton = (Button) mView.findViewById(R.id.run_pause_button);
        mPauseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mPauseButton.getText().equals(getString(R.string.pause))) {
                    mPauseButton.setText(getString(R.string.resume));
                    mRunManager.stopLocationUpdates();
                    mLastLocation = null;
                    mPausedSeconds = System.currentTimeMillis();
                } else {
                    mPauseButton.setText(getString(R.string.pause));
                    mRunManager.startLocationUpdates();
                    long elapsedMilliSeconds = System.currentTimeMillis() - mPausedSeconds;
                    mTime += elapsedMilliSeconds;
                }
            }
        });

        mStopButton = (Button) mView.findViewById(R.id.run_stop_button);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRunManager.stopRun();
                showSummary();
            }
        });

        mMapButton = (Button) mView.findViewById(R.id.run_map_button);
        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), RunMapActivity.class);
                i.putExtra(RunMapActivity.EXTRA_RUN_ID, mRun.getId());
                startActivity(i);
            }
        });

        setButtonsVisibility(true);
        return mView;
    }

    private void showSummary() {
        Toast.makeText(getActivity(), R.string.run_completed, Toast.LENGTH_LONG).show();

        // set Buttons
        mStartButton.setText(getString(R.string.save_activity));
        setButtonsVisibility(true);
        mMapButton.setVisibility(View.VISIBLE);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRunManager.updateRun(mRun);
                getActivity().finish();
            }
        });

        // change background
        mView.setBackgroundColor(getResources().getColor(R.color.light_grey));

        // remove current pace layout
        LinearLayout paceLinearLayout = (LinearLayout) mView.findViewById(R.id.run_current_pace_layout);
        paceLinearLayout.setVisibility(View.GONE);

        // change calories text
        TextView caloriesTextView = (TextView) mView.findViewById(R.id.run_calories_static_textView);
        caloriesTextView.setText(getString(R.string.calories));

        // set values
        float avgPace = mRun.getSumPaceMinPerKm() / mRun.getDuration();
        long calories = Calculator.getCaloriesPerMinute(mRunManager.getCurrentUser().getWeight(),
                avgPace, mRun.getDuration());
        mAvgPaceTextView.setText(Formatter.formatPace(avgPace));
        mCaloriesTextView.setText(String.valueOf(calories));

        mRun.setAvgPace(avgPace);
        mRun.setCalories(calories);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mLocationReceiver,
                new IntentFilter(RunManager.ACTION_LOCATION));
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(mLocationReceiver);
        super.onStop();
    }

    private void updateUI() {
        float weight = 65;

        if (mRun != null && mLastLocation != null) {
            long durationSeconds = (mLastLocation.getTime() - mTime) / 1000;
            float distanceM = mRun.getDistance();
            float currentPaceMinPerKm = Calculator.getPace(durationSeconds, distanceM);
            long caloriesPerMinute = Calculator.getCaloriesPerMinute(weight, currentPaceMinPerKm, durationSeconds);

            mRun.addPaceMinPerKm(currentPaceMinPerKm);
            mRun.setDuration(durationSeconds);

            mDurationTextView.setText(Formatter.formatDuration(durationSeconds));
            mDistanceTextView.setText(Formatter.formatDistance(distanceM));
            mCaloriesTextView.setText(String.valueOf(caloriesPerMinute));
            mCurrentPaceTextView.setText(Formatter.formatPace(currentPaceMinPerKm));
            mAvgPaceTextView.setText(Formatter.formatPace(mRun.getSumPaceMinPerKm() / durationSeconds));
        }
    }

    private void setButtonsVisibility(boolean isStartEndRun) {
        int visibility = View.VISIBLE;

        if (isStartEndRun) {
            visibility = View.GONE;
            mStartButton.setVisibility(View.VISIBLE);
        } else {
            mStartButton.setVisibility(View.GONE);
        }
        mPauseButton.setVisibility(visibility);
        mStopButton.setVisibility(visibility);
        mMapButton.setVisibility(visibility);
    }

    public void onBackPressed() {
        if (mRun != null) {
            new DiscardActivityDialogFragment().show(getActivity().getSupportFragmentManager(), TAG);
        } else {
            getActivity().supportFinishAfterTransition();
        }
    }

    public static class DiscardActivityDialogFragment extends DialogFragment {
        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.dialog_discard_activity)
                    .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    })
                    .setNegativeButton(R.string.discard, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            RunFragment runFragment = ((RunActivity) getActivity()).getFragment();
                            runFragment.mRunManager.deleteRun(runFragment.mRun.getId());
                            runFragment.getActivity().finish();
                        }
                    });

            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
