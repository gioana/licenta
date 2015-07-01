package com.diploma.android.iruntracking.loaders;

import android.content.Context;
import android.database.Cursor;

import com.diploma.android.iruntracking.RunManager;

public class LocationListCursorLoader extends SQLiteCursorLoader {
    private long mRunId;
    
    public LocationListCursorLoader(Context c, long runId) {
        super(c);
        mRunId = runId;
    }

    @Override
    protected Cursor loadCursor() {
        return RunManager.get(getContext()).queryLocationsForRun(mRunId);
    }
}