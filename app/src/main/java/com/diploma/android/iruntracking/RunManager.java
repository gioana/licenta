package com.diploma.android.iruntracking;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.diploma.android.iruntracking.helpers.RunDatabaseHelper.LocationCursor;
import com.diploma.android.iruntracking.helpers.RunDatabaseHelper.RunCursor;
import com.diploma.android.iruntracking.helpers.RunDatabaseHelper.UserCursor;
import com.diploma.android.iruntracking.model.Run;
import com.diploma.android.iruntracking.helpers.RunDatabaseHelper;
import com.diploma.android.iruntracking.model.User;
import com.diploma.android.iruntracking.utils.Constants;
import com.diploma.android.iruntracking.utils.RecordType;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class RunManager {
    private static final String TAG = "RunManager";
    public static final String ACTION_LOCATION = "com.diploma.android.iruntracking.ACTION_LOCATION";

    private static RunManager sRunManager;
    private Context mAppContext;
    private LocationManager mLocationManager;
    private RunDatabaseHelper mHelper;
    private SharedPreferences mPrefs;
    private long mCurrentRunId;
    private User mCurrentUser;

    public User getCurrentUser() {
        return mCurrentUser;
    }

    public void setCurrentUser(User user) {
        this.mCurrentUser = user;
    }

    private RunManager(Context appContext) {
        mAppContext = appContext;
        mLocationManager = (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);
        mHelper = new RunDatabaseHelper(mAppContext);
        mPrefs = mAppContext.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        mCurrentRunId = mPrefs.getLong(Constants.PREF_CURRENT_RUN_ID, -1);
    }

    public static RunManager get(Context c) {
        if (sRunManager == null) {
            // we use the application context to avoid leaking activities
            sRunManager = new RunManager(c.getApplicationContext());
        }
        return sRunManager;
    }

    public static void deleteRunManagerInstance() {
        sRunManager = null;
    }

    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
    }

    public void startLocationUpdates() {
        String provider = LocationManager.GPS_PROVIDER;
        // if we have the test provider and it's enabled, use it
        Log.d(TAG, "Using provider " + provider);

        // get the last known location and broadcast it if we have one
        Location lastKnown = mLocationManager.getLastKnownLocation(provider);
        if (lastKnown != null) {
            // reset the time to now
            lastKnown.setTime(System.currentTimeMillis());
            broadcastLocation(lastKnown);
        }
        // start updates from the location manager
        PendingIntent pi = getLocationPendingIntent(true);
        mLocationManager.requestLocationUpdates(provider, 0, 0, pi);
    }

    public void stopLocationUpdates() {
        PendingIntent pi = getLocationPendingIntent(false);
        if (pi != null) {
            mLocationManager.removeUpdates(pi);
            pi.cancel();
        }
    }

    private void broadcastLocation(Location location) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        mAppContext.sendBroadcast(broadcast);
    }

    public Run startNewRun() {
        // insert a run into the db
        Run run = insertRun();
        // start tracking the run
        startTrackingRun(run);
        return run;
    }

    public void stopRun() {
        stopLocationUpdates();
        mCurrentRunId = -1;
        mPrefs.edit().remove(Constants.PREF_CURRENT_RUN_ID).apply();
    }

    public void startTrackingRun(Run run) {
        // keep the ID
        mCurrentRunId = run.getId();
        // store it in shared preferences
        mPrefs.edit().putLong(Constants.PREF_CURRENT_RUN_ID, mCurrentRunId).apply();
        // start location updates
        startLocationUpdates();
    }

    public boolean isTrackingRun(Run run) {
        return run != null && run.getId() == mCurrentRunId;
    }


    /*Database operations*/

    //Create
    private Run insertRun() {
        Run run = new Run(mCurrentUser.getId());
        run.setId(mHelper.insertRun(run));
        return run;
    }

    public void insertLocation(Location loc) {
        if (mCurrentRunId != -1) {
            mHelper.insertLocation(mCurrentRunId, loc);
        } else {
            Log.e(TAG, "Location received with no tracking run; ignoring.");
        }
    }

    public long insertUser(User user) {
        return mHelper.insertUser(user);
    }

    // Read
    public Run queryRun(long id) {
        Run run = null;
        RunCursor cursor = mHelper.queryRun(id);
        cursor.moveToFirst();
        // if we got a row, get a run
        if (!cursor.isAfterLast())
            run = cursor.getRun();
        cursor.close();
        return run;
    }

    public RunCursor queryRuns() {
        return mHelper.queryRuns(mCurrentUser.getId());
    }

    public LocationCursor queryLocationsForRun(long runId) {
        return mHelper.queryLocationsForRun(runId);
    }

    public Location queryLastLocationForRun(long runId) {
        Location location = null;
        LocationCursor cursor = mHelper.queryLastLocationForRun(runId);
        cursor.moveToFirst();
        // if we got a row, get a location
        if (!cursor.isAfterLast()) {
            location = cursor.getLocation();
        }
        cursor.close();
        return location;
    }

    public User queryUser(String email) {
        User user = null;
        UserCursor cursor = mHelper.queryUser(email);
        cursor.moveToFirst();
        // if we got a row, get the user
        if (!cursor.isAfterLast()) {
            user = cursor.getUser();
        }
        cursor.close();

        return user;
    }

    public User queryUser(long userId) {
        User user = null;
        UserCursor cursor = mHelper.queryUser(userId);
        cursor.moveToFirst();
        // if we got a row, get the user
        if (!cursor.isAfterLast()) {
            user = cursor.getUser();
        }
        cursor.close();

        return user;
    }

    public Run queryRun(RecordType type) {
        Run run = null;
        RunCursor cursor = mHelper.queryRun(type, mCurrentUser.getId());

        cursor.moveToFirst();
        // if we got a row, get a run
        if (!cursor.isAfterLast())
            run = cursor.getRun();
        cursor.close();

        return run;
    }

    public float queryCurrentWeekDistance() {
        Calendar c = GregorianCalendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        c.clear(Calendar.MINUTE);
        c.clear(Calendar.SECOND);
        c.clear(Calendar.MILLISECOND);

        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        long startWeekDate = c.getTimeInMillis();
        c.add(Calendar.DATE, 7);
        long endWeekDate = c.getTimeInMillis();

        return mHelper.queryWeekDistance(startWeekDate, endWeekDate, mCurrentUser.getId());
    }

    public long queryCurrentWeekDuration() {
        Calendar c = GregorianCalendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        c.clear(Calendar.MINUTE);
        c.clear(Calendar.SECOND);
        c.clear(Calendar.MILLISECOND);

        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        long startWeekDate = c.getTimeInMillis();
        c.add(Calendar.DATE, 7);
        long endWeekDate = c.getTimeInMillis();

        return mHelper.queryWeekDuration(startWeekDate, endWeekDate, mCurrentUser.getId());
    }

    public long queryCurrentWeekCalories() {
        Calendar c = GregorianCalendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        c.clear(Calendar.MINUTE);
        c.clear(Calendar.SECOND);
        c.clear(Calendar.MILLISECOND);

        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        long startWeekDate = c.getTimeInMillis();
        c.add(Calendar.DATE, 7);
        long endWeekDate = c.getTimeInMillis();

        return mHelper.queryWeekCalories(startWeekDate, endWeekDate, mCurrentUser.getId());
    }

    // Update
    public void updateRun(Run run) {
        mHelper.updateRun(run);
    }

    public void updateUser(User user) {
        mHelper.updateUser(user);
    }

    // Delete
    public void deleteRun(long runId) {
        mHelper.deleteRun(runId);
    }
}
