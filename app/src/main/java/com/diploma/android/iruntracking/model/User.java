package com.diploma.android.iruntracking.model;

public class User {
    private long mId;
    private String mEmail;
    private String mPassword;
    private String mFacebookId;
    private String mName;
    private float mWeight;

    public User() {
    }

    public User(String mEmail, String mPassword) {
        this.mEmail = mEmail;
        this.mPassword = mPassword;
    }

    public User(long id, String email, String password, String facebookId) {
        this.mId = id;
        this.mEmail = email;
        this.mPassword = password;
        this.mFacebookId = facebookId;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    public String getFacebookId() {
        return mFacebookId;
    }

    public void setFacebookId(String facebookId) {
        this.mFacebookId = facebookId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public float getWeight() {
        return mWeight;
    }

    public void setWeight(float weight) {
        this.mWeight = weight;
    }
}
