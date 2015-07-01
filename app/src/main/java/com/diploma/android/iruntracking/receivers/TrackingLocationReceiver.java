package com.diploma.android.iruntracking.receivers;

import android.content.Context;
import android.location.Location;

import com.diploma.android.iruntracking.RunManager;

public class TrackingLocationReceiver extends LocationReceiver {
    
    @Override
    protected void onLocationReceived(Context c, Location loc) {
        RunManager.get(c).insertLocation(loc);
    }

}
