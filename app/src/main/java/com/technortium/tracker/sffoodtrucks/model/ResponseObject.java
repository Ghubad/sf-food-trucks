package com.technortium.tracker.sffoodtrucks.model;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

/**
 * Created by suhas on 21/08/15.
 */
public class ResponseObject {

    private int count;
    private String previous;
    private String next;

    @SerializedName("results")
    private GpsLocation[] gpsLocations;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public GpsLocation[] getGpsLocations() {
        return gpsLocations;
    }

    public void setGpsLocations(GpsLocation[] gpsLocations) {
        this.gpsLocations = gpsLocations;
    }

    @Override
    public String toString() {
        return "ResponseObject{" +
                "count=" + count +
                ", previous='" + previous + '\'' +
                ", next='" + next + '\'' +
                ", gpsLocations=" + Arrays.toString(gpsLocations) +
                '}';
    }
}
