package com.technortium.tracker.sffoodtrucks.model;

import java.util.Arrays;

public class Location {
    /*
    * "location": {
      "type": "Point",
      "coordinates": [
        -122.395804,
        37.792109
      ]
    },
    * */

    String type;
    double[] coordinates;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        return "Location{" +
                "type='" + type + '\'' +
                ", coordinates=" + Arrays.toString(coordinates) +
                '}';
    }
}
