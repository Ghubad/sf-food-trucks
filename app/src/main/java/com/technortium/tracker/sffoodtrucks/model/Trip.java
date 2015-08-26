package com.technortium.tracker.sffoodtrucks.model;

/**
 * Created by suhas on 26/08/15.
 */
public class Trip {
    private int id;
    private Courier courier;

    public Courier getCourier() {
        return courier;
    }

    public void setCourier(Courier courier) {
        this.courier = courier;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id=" + id +
                ", courier=" + courier +
                '}';
    }
}
