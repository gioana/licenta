package com.diploma.android.iruntracking.activities;

import android.support.v4.app.Fragment;

import com.diploma.android.iruntracking.fragments.RunFragment;

public class RunActivity extends SingleFragmentActivity {
    private RunFragment mFragment;

    public RunFragment getFragment() {
        return mFragment;
    }

    @Override
    protected Fragment createFragment() {
        mFragment = new RunFragment();
        return mFragment;
    }

    @Override
    public void onBackPressed() {
        mFragment.onBackPressed();
    }
}
