package com.diploma.android.iruntracking.loaders;

import android.content.Context;
import android.location.Location;

import com.diploma.android.iruntracking.RunManager;

public class LastLocationLoader extends DataLoader<Location> {
    private long mRunId;
    
    public LastLocationLoader(Context context, long runId) {
        super(context);
        mRunId = runId;
    }

    @Override
    public Location loadInBackground() {
        return RunManager.get(getContext()).queryLastLocationForRun(mRunId);
    }
}