package com.technortium.tracker.sffoodtrucks.model;

/**
 * Created by suhas on 26/08/15.
 */
public class Order {

    private int id;
    private Trip trip;
    private String estimated_delivery_time;
    private String status;
    private Destination destination;

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public String getEstimated_delivery_time() {
        return estimated_delivery_time;
    }

    public void setEstimated_delivery_time(String estimated_delivery_time) {
        this.estimated_delivery_time = estimated_delivery_time;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", trip=" + trip +
                ", estimated_delivery_time='" + estimated_delivery_time + '\'' +
                ", status='" + status + '\'' +
                ", destination=" + destination +
                '}';
    }
}
