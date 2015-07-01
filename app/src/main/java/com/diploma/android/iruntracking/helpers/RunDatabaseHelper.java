package com.diploma.android.iruntracking.helpers;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import com.diploma.android.iruntracking.model.Run;
import com.diploma.android.iruntracking.model.User;
import com.diploma.android.iruntracking.utils.RecordType;

public class RunDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "RunDataBaseHelper";

    private static final String DB_NAME = "runs.sqlite";
    private static final int VERSION = 1;
    
    private static final String TABLE_RUN = "run";
    private static final String COLUMN_RUN_ID = "_id";
    private static final String COLUMN_RUN_START_DATE = "start_date";
    private static final String COLUMN_RUN_DISTANCE = "distance";
    private static final String COLUMN_RUN_DURATION = "duration";
    private static final String COLUMN_RUN_USER_ID = "user_id";
    private static final String COLUMN_RUN_AVG_PACE = "avg_pace";
    private static final String COLUMN_RUN_CALORIES = "calories";

    private static final String TABLE_LOCATION = "location";
    private static final String COLUMN_LOCATION_LATITUDE = "latitude";
    private static final String COLUMN_LOCATION_LONGITUDE = "longitude";
    private static final String COLUMN_LOCATION_ALTITUDE = "altitude";
    private static final String COLUMN_LOCATION_TIMESTAMP = "timestamp";
    private static final String COLUMN_LOCATION_PROVIDER = "provider";
    private static final String COLUMN_LOCATION_RUN_ID = "run_id";

    private static final String TABLE_USER = "user";
    private static final String COLUMN_USER_ID = "_id";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";
    private static final String COLUMN_USER_FACEBOOK_ID = "facebook_id";
    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_WEIGHT = "weight";

    public RunDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create the "run" table
        db.execSQL("create table run (" +
                "_id integer primary key autoincrement, start_date integer," +
                " distance real, duration long, user_id integer, avg_pace real, calories long)");
        // create the "location" table
        db.execSQL("create table location (" +
                " timestamp integer, latitude real, longitude real, altitude real," +
                " provider varchar(100), run_id integer references run(_id))");
        // create the "user" table
        db.execSQL("create table user (" +
                "_id integer primary key autoincrement, email varchar(50)," +
                "password varchar(20), facebook_id long, name varchar(50), weight real)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // implement schema changes and data massage here when upgrading
    }
    
    public long insertRun(Run run) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RUN_START_DATE, run.getStartDate().getTime());
        cv.put(COLUMN_RUN_DISTANCE, run.getDistance());
        cv.put(COLUMN_RUN_DURATION, run.getDuration());
        cv.put(COLUMN_RUN_USER_ID, run.getUserId());
        cv.put(COLUMN_RUN_AVG_PACE, run.getAvgPace());
        cv.put(COLUMN_RUN_CALORIES, run.getCalories());

        return getWritableDatabase().insert(TABLE_RUN, null, cv);
    }

    public long insertLocation(long runId, Location location) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LOCATION_LATITUDE, location.getLatitude());
        cv.put(COLUMN_LOCATION_LONGITUDE, location.getLongitude());
        cv.put(COLUMN_LOCATION_ALTITUDE, location.getAltitude());
        cv.put(COLUMN_LOCATION_TIMESTAMP, location.getTime());
        cv.put(COLUMN_LOCATION_PROVIDER, location.getProvider());
        cv.put(COLUMN_LOCATION_RUN_ID, runId);

        return getWritableDatabase().insert(TABLE_LOCATION, null, cv);
    }

    public long insertUser(User user) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USER_EMAIL, user.getEmail());
        cv.put(COLUMN_USER_PASSWORD, user.getPassword());
        cv.put(COLUMN_USER_FACEBOOK_ID, user.getFacebookId());
        cv.put(COLUMN_USER_NAME, user.getName());
        cv.put(COLUMN_USER_WEIGHT, user.getWeight());

        return getWritableDatabase().insert(TABLE_USER, null, cv);
    }

    public void updateRun(Run run) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RUN_START_DATE, run.getStartDate().getTime());
        cv.put(COLUMN_RUN_DISTANCE, run.getDistance());
        cv.put(COLUMN_RUN_DURATION, run.getDuration());
        cv.put(COLUMN_RUN_USER_ID, run.getUserId());
        cv.put(COLUMN_RUN_AVG_PACE, run.getAvgPace());
        cv.put(COLUMN_RUN_CALORIES, run.getCalories());
        String whereClause = String.format("%s =? AND %s =?",
                COLUMN_RUN_ID, COLUMN_RUN_USER_ID);
        String[] whereArgs = new String[] {String.valueOf(run.getId()),
                String.valueOf(run.getUserId())};
        getWritableDatabase().update(TABLE_RUN, cv, whereClause, whereArgs);
    }

    public void deleteRun(long runId) {
        String whereClause = COLUMN_RUN_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(runId) };
        getWritableDatabase().delete(TABLE_RUN, whereClause, whereArgs);
        whereClause = COLUMN_LOCATION_RUN_ID + "=?";
        getWritableDatabase().delete(TABLE_LOCATION, whereClause, whereArgs);
    }

    public RunCursor queryRuns(long userId) {
        // equivalent to "select * from run order by start_date asc"
        Cursor wrapped = getReadableDatabase().query(TABLE_RUN,
                null, // all columns
                COLUMN_RUN_USER_ID + " = ?", // look for a user ID
                new String[] {String.valueOf(userId)}, // with this value
                null, // group by
                null, // order by
                COLUMN_RUN_START_DATE + " desc"); // having
        return new RunCursor(wrapped);
    }
    
    public RunCursor queryRun(long id) {
        Cursor wrapped = getReadableDatabase().query(TABLE_RUN, 
                null, // all columns 
                COLUMN_RUN_ID + " = ?", // look for a run ID
                new String[]{ String.valueOf(id) }, // with this value
                null, // group by
                null, // having
                null, // order by
                "1"); // limit 1 row
        return new RunCursor(wrapped);
    }

    public RunCursor queryRun(RecordType type, long userId) {
        String orderBy;
        switch (type) {
            case DISTANCE:
                orderBy = COLUMN_RUN_DISTANCE + " desc";
                break;
            case DURATION:
                orderBy = COLUMN_RUN_DURATION + " desc";
                break;
            case PACE:
                orderBy = COLUMN_RUN_AVG_PACE + " asc";
                break;
            case CALORIES:
                orderBy = COLUMN_RUN_CALORIES + " desc";
                break;
            default:
                orderBy = null;
                break;
        }

        Cursor wrapped = getReadableDatabase().query(TABLE_RUN,
                null, // all columns
                COLUMN_RUN_USER_ID + " = ?", // look for a user ID
                new String[] {String.valueOf(userId)}, // with this value
                null, // group by
                null, // having
                orderBy, // order by
                "1"); // limit 1 row

        return new RunCursor(wrapped);
    }

    public float queryWeekDistance(long startWeekDate, long endWeekDate, long userId) {
        float totalDistance = 0;

        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT SUM(" + COLUMN_RUN_DISTANCE + ") FROM " + TABLE_RUN + " WHERE " +
                        COLUMN_RUN_USER_ID + " = " + userId + " AND " +
                COLUMN_RUN_START_DATE + " > " + startWeekDate + " AND " + COLUMN_RUN_START_DATE +
                " < " + endWeekDate, null);
        if(cursor.moveToFirst()) {
            totalDistance = cursor.getFloat(0);
        }

        return totalDistance;
    }

    public long queryWeekCalories(long startWeekDate, long endWeekDate, long userId) {
        long totalCalories = 0;

        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT SUM(" + COLUMN_RUN_CALORIES + ") FROM " + TABLE_RUN + " WHERE " +
                        COLUMN_RUN_USER_ID + " = " + userId + " AND " +
                        COLUMN_RUN_START_DATE + " > " + startWeekDate + " AND " + COLUMN_RUN_START_DATE +
                        " < " + endWeekDate, null);
        if (cursor.moveToFirst()) {
            totalCalories = cursor.getLong(0);
        }

        return totalCalories;
    }

    public long queryWeekDuration(long startWeekDate, long endWeekDate, long userId) {
        long totalDuration = 0;

        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT SUM(" + COLUMN_RUN_DURATION + ") FROM " + TABLE_RUN + " WHERE " +
                        COLUMN_RUN_USER_ID + " = " + userId + " AND " +
                        COLUMN_RUN_START_DATE + " > " + startWeekDate + " AND " + COLUMN_RUN_START_DATE +
                        " < " + endWeekDate, null);
        if (cursor.moveToFirst()) {
            totalDuration = cursor.getLong(0);
        }

        return totalDuration;
    }

    public LocationCursor queryLastLocationForRun(long runId) {
        Cursor wrapped = getReadableDatabase().query(TABLE_LOCATION, 
                null, // all columns 
                COLUMN_LOCATION_RUN_ID + " = ?", // limit to the given run
                new String[]{ String.valueOf(runId) }, 
                null, // group by
                null, // having
                COLUMN_LOCATION_TIMESTAMP + " desc", // order by latest first
                "1"); // limit 1
        return new LocationCursor(wrapped);
    }

    public LocationCursor queryLocationsForRun(long runId) {
        Cursor wrapped = getReadableDatabase().query(TABLE_LOCATION,
                null,
                COLUMN_LOCATION_RUN_ID + " = ?", // limit to the given run
                new String[]{ String.valueOf(runId) }, 
                null, // group by
                null, // having
                COLUMN_LOCATION_TIMESTAMP + " asc"); // order by timestamp

        return new LocationCursor(wrapped);
    }

    public UserCursor queryUser(String email) {
        Cursor wrapped = getReadableDatabase().query(TABLE_USER,
                null,
                COLUMN_USER_EMAIL + " = ?",
                new String[]{String.valueOf(email)},
                null,
                null,
                null);

        return new UserCursor(wrapped);
    }

    public UserCursor queryUser(long userId) {
        Cursor wrapped = getReadableDatabase().query(TABLE_USER,
                null,
                COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                null);

        return new UserCursor(wrapped);
    }

    public void updateUser(User user) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USER_EMAIL, user.getEmail());
        cv.put(COLUMN_USER_PASSWORD, user.getPassword());
        cv.put(COLUMN_USER_FACEBOOK_ID, user.getFacebookId());
        cv.put(COLUMN_USER_NAME, user.getName());
        cv.put(COLUMN_USER_WEIGHT, user.getWeight());

        String whereClause = String.format("%s =?",COLUMN_USER_ID);
        String[] whereArgs = new String[] {String.valueOf(user.getId())};
        getWritableDatabase().update(TABLE_USER, cv, whereClause, whereArgs);
    }

    /**
     * A convenience class to wrap a cursor that returns rows from the "run" table.
     * The queryRun() method will give you a Run instance representing the current row.
     */
    public static class RunCursor extends CursorWrapper {
        
        public RunCursor(Cursor c) {
            super(c);
        }
        
        /**
         * Returns a Run object configured for the current row, or null if the current row is invalid.
         */
        public Run getRun() {
            if (isBeforeFirst() || isAfterLast()) {
                return null;
            }
            Run run = new Run();
            run.setId(getLong(getColumnIndex(COLUMN_RUN_ID)));
            run.setStartDate(new Date(getLong(getColumnIndex(COLUMN_RUN_START_DATE))));
            run.setDistance(getFloat(getColumnIndex(COLUMN_RUN_DISTANCE)));
            run.setDuration(getLong(getColumnIndex(COLUMN_RUN_DURATION)));
            run.setUserId(getInt(getColumnIndex(COLUMN_RUN_USER_ID)));
            run.setAvgPace(getFloat(getColumnIndex(COLUMN_RUN_AVG_PACE)));
            run.setCalories(getLong(getColumnIndex(COLUMN_RUN_CALORIES)));

            return run;
        }
    }
    
    public static class LocationCursor extends CursorWrapper {
        
        public LocationCursor(Cursor c) {
            super(c);
        }
        
        public Location getLocation() {
            if (isBeforeFirst() || isAfterLast()) {
                return null;
            }
            // first get the provider out so we can use the constructor
            String provider = getString(getColumnIndex(COLUMN_LOCATION_PROVIDER));
            Location loc = new Location(provider);
            // populate the remaining properties
            loc.setLongitude(getDouble(getColumnIndex(COLUMN_LOCATION_LONGITUDE)));
            loc.setLatitude(getDouble(getColumnIndex(COLUMN_LOCATION_LATITUDE)));
            loc.setAltitude(getDouble(getColumnIndex(COLUMN_LOCATION_ALTITUDE)));
            loc.setTime(getLong(getColumnIndex(COLUMN_LOCATION_TIMESTAMP)));

            return loc;
        }
    }

    public static class UserCursor extends CursorWrapper {

        /**
         * Creates a cursor wrapper.
         *
         * @param cursor The underlying cursor to wrap.
         */
        public UserCursor(Cursor cursor) {
            super(cursor);
        }

        public User getUser() {
            if (isBeforeFirst() || isAfterLast()) {
                return null;
            }
            User user = new User();
            user.setId(getInt(getColumnIndex(COLUMN_USER_ID)));
            user.setEmail(getString(getColumnIndex(COLUMN_USER_EMAIL)));
            user.setPassword(getString(getColumnIndex(COLUMN_USER_PASSWORD)));
            user.setFacebookId(getString(getColumnIndex(COLUMN_USER_FACEBOOK_ID)));
            user.setName(getString(getColumnIndex(COLUMN_USER_NAME)));
            user.setWeight(getFloat(getColumnIndex(COLUMN_USER_WEIGHT)));

            return user;
        }
    }
}
