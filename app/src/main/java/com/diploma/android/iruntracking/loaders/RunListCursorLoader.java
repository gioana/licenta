package com.diploma.android.iruntracking.loaders;

import android.content.Context;
import android.database.Cursor;

import com.diploma.android.iruntracking.RunManager;

public class RunListCursorLoader extends SQLiteCursorLoader {

    public RunListCursorLoader(Context context) {
        super(context);
    }

    @Override
    protected Cursor loadCursor() {
        // query the list of runs
        return RunManager.get(getContext()).queryRuns();
    }
}