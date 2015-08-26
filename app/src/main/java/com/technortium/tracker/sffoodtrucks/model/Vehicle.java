package com.technortium.tracker.sffoodtrucks.model;

/**
 * Created by suhas on 26/08/15.
 */
public class Vehicle {

    private String description;
    private String vehicle_type;
    private String license_plate;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public String getLicense_plate() {
        return license_plate;
    }

    public void setLicense_plate(String license_plate) {
        this.license_plate = license_plate;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                ", description='" + description + '\'' +
                ", vehicle_type='" + vehicle_type + '\'' +
                ", license_plate='" + license_plate + '\'' +
                '}';
    }
}
