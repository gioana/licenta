package com.diploma.android.iruntracking.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.diploma.android.iruntracking.R;
import com.diploma.android.iruntracking.RunManager;
import com.diploma.android.iruntracking.model.Run;
import com.diploma.android.iruntracking.utils.Formatter;
import com.diploma.android.iruntracking.utils.RecordType;

public class RecordsActivity extends Activity {

    private RunManager mRunManager;

    private TextView mDistance;
    private TextView mDistanceDate;
    private TextView mDuration;
    private TextView mDurationDate;
    private TextView mPace;
    private TextView mPaceDate;
    private TextView mCalories;
    private TextView mCaloriesDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        mRunManager = RunManager.get(this);

        mDistance = (TextView) findViewById(R.id.farthest_distance_textView);
        mDistanceDate = (TextView) findViewById(R.id.farthest_distance_date_textView);
        mDuration = (TextView) findViewById(R.id.longest_duration_textView);
        mDurationDate = (TextView) findViewById(R.id.longest_duration_date_textView);
        mPace = (TextView) findViewById(R.id.fastest_pace_textView);
        mPaceDate = (TextView) findViewById(R.id.fastest_pace_date_textView);
        mCalories = (TextView) findViewById(R.id.max_calories_textView);
        mCaloriesDate = (TextView) findViewById(R.id.max_calories_date_textView);

        setRecords();
    }

    private void setRecords() {
        Run run = mRunManager.queryRun(RecordType.DISTANCE);
        if (run != null) {
            mDistance.setText(Formatter.formatDistance(run.getDistance()));
            mDistanceDate.setText(String.valueOf(Formatter.formatDate(run.getStartDate())));
        }

        run = mRunManager.queryRun(RecordType.DURATION);
        if (run != null) {
            mDuration.setText(Formatter.formatDuration(run.getDuration()));
            mDurationDate.setText(Formatter.formatDate(run.getStartDate()));
        }

        run = mRunManager.queryRun(RecordType.PACE);
        if (run != null) {
            mPace.setText(Formatter.formatPace(run.getAvgPace()));
            mPaceDate.setText(Formatter.formatDate(run.getStartDate()));
        }

        run = mRunManager.queryRun(RecordType.CALORIES);
        if (run != null) {
            mCalories.setText(String.valueOf(run.getCalories()));
            mCaloriesDate.setText(Formatter.formatDate(run.getStartDate()));
        }
    }
}
