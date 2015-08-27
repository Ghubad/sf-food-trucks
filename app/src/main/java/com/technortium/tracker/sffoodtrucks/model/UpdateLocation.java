package com.technortium.tracker.sffoodtrucks.model;

/**
 * Created by suhas on 27/08/15.
 */
public class UpdateLocation {
    private Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "UpdateLocation{" +
                "location=" + location +
                '}';
    }
}
