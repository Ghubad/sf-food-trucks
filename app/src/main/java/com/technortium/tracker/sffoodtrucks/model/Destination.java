package com.technortium.tracker.sffoodtrucks.model;

/**
 * Created by suhas on 27/08/15.
 */
public class Destination {

    private int id;
    private boolean is_location_accurate;
    private Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean is_location_accurate() {
        return is_location_accurate;
    }

    public void setIs_location_accurate(boolean is_location_accurate) {
        this.is_location_accurate = is_location_accurate;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "id=" + id +
                ", is_location_accurate=" + is_location_accurate +
                ", location=" + location +
                '}';
    }
}
