package com.diploma.android.iruntracking.loaders;

import android.content.Context;

import com.diploma.android.iruntracking.RunManager;
import com.diploma.android.iruntracking.model.Run;

public class RunLoader extends DataLoader<Run> {
    private long mRunId;
    
    public RunLoader(Context context, long runId) {
        super(context);
        mRunId = runId;
    }
    
    @Override
    public Run loadInBackground() {
        return RunManager.get(getContext()).queryRun(mRunId);
    }
}