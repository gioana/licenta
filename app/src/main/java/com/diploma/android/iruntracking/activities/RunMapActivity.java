package com.diploma.android.iruntracking.activities;

import android.support.v4.app.Fragment;

import com.diploma.android.iruntracking.fragments.RunMapFragment;

public class RunMapActivity extends SingleFragmentActivity {
    /** A key for passing a run ID as a long */
    public static final String EXTRA_RUN_ID = "RUN_ID";
    
    @Override
    protected Fragment createFragment() {
        long runId = getIntent().getLongExtra(EXTRA_RUN_ID, -1);
        if (runId != -1) {
            return RunMapFragment.newInstance(runId);
        } else {
            return new RunMapFragment();
        }
    }

}
