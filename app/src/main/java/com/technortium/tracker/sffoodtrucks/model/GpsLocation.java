package com.technortium.tracker.sffoodtrucks.model;

/**
 * Created by suhas on 20/08/15.
 */
public class GpsLocation {

    private Location location;
    private float location_accuracy;
    private float speed;
    private float bearing;
    private double altitude;
    private String courier;
    private int id;
    private String trip_id;
    private String created_at;
    private String modified_at;
    private boolean fetch_next_points;

    public boolean isFetch_next_points() {
        return fetch_next_points;
    }

    public void setFetch_next_points(boolean fetch_next_points) {
        this.fetch_next_points = fetch_next_points;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getModified_at() {
        return modified_at;
    }

    public void setModified_at(String modified_at) {
        this.modified_at = modified_at;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public float getLocation_accuracy() {
        return location_accuracy;
    }

    public void setLocation_accuracy(float location_accuracy) {
        this.location_accuracy = location_accuracy;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String getCourier() {
        return courier;
    }

    public void setCourier(String courier) {
        this.courier = courier;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    @Override
    public String toString() {
        return "GpsLocation{" +
                "location=" + location +
                ", location_accuracy=" + location_accuracy +
                ", speed=" + speed +
                ", bearing=" + bearing +
                ", altitude=" + altitude +
                ", courier='" + courier + '\'' +
                ", id=" + id +
                ", trip_id='" + trip_id + '\'' +
                ", created_at='" + created_at + '\'' +
                ", modified_at='" + modified_at + '\'' +
                ", fetch_next_points=" + fetch_next_points +
                '}';
    }
}

/*
*  "location": {
                "type": "Point",
                "coordinates": [
                    77.1010206,
                    28.4195721
                ]
            },
            "location_accuracy": null,
            "speed": null,
            "bearing": null,
            "altitude": null,
            "courier": 1
*/
