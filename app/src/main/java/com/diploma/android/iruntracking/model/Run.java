package com.diploma.android.iruntracking.model;

import java.util.Date;

public class Run {
    private long mId;
    private Date mStartDate;
    private float mDistance;
    private long mDuration;
    private float mSumPaceMinPerKm;
    private long mUserId;
    private float mAvgPace;
    private long mCalories;

    public Run() {}

    public Run(long userId) {
        mId = -1;
        mStartDate = new Date();
        mDistance = 0;
        mDuration = 0;
        mSumPaceMinPerKm = 0;
        mUserId = userId;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Date startDate) {
        mStartDate = startDate;
    }

    public float getDistance() {
        return mDistance;
    }

    public void setDistance(float distance) {
        this.mDistance = distance;
    }

    public long getDuration() {
        return mDuration;
    }

    public void setDuration(long duration) {
        this.mDuration = duration;
    }

    public long getUserId() {
        return mUserId;
    }

    public void setUserId(long userId) {
        this.mUserId = userId;
    }

    public long getCalories() {
        return mCalories;
    }

    public void setCalories(long calories) {
        this.mCalories = calories;
    }

    public float getAvgPace() {
        return mAvgPace;
    }

    public void setAvgPace(float avgPace) {
        this.mAvgPace = avgPace;
    }

    public void addPaceMinPerKm(float currentPaceMinPerKm) {
        if (this.mSumPaceMinPerKm == 0) {
            this.mSumPaceMinPerKm = currentPaceMinPerKm;
        } else {
            mSumPaceMinPerKm += currentPaceMinPerKm;
        }
    }

    public float getSumPaceMinPerKm() {
        return mSumPaceMinPerKm;
    }
}
