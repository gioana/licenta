package com.diploma.android.iruntracking.activities;

import android.support.v4.app.Fragment;

import com.diploma.android.iruntracking.fragments.MainFragment;

public class MainActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new MainFragment();
    }
}
