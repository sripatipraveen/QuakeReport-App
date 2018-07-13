package com.example.android.quakereport;

/**
 * Created by praveen on 24/03/18.
 */

public class Word {

    private double mMagnitude;
    private String mPlace;
    private long mDate;
    private String mUrl;


    public Word(double mMagnitude, String mPlace, long mDate, String mUrl) {
        this.mMagnitude = mMagnitude;
        this.mPlace = mPlace;
        this.mDate = mDate;
        this.mUrl = mUrl;
    }

    public double getMagnitude(){
        return mMagnitude;
    }

    public String getPlace(){
        return mPlace;
    }

    public long getDate(){
        return mDate;
    }

    public String getUrl() {
        return mUrl;
    }

}
