package com.rhombus.cosmos;

/**
 * Created by Preetham on 5/21/2017.
 */
public class Location {
    private String location, action;
    private double[] position;

    public Location(String _location, double _p1, double _p2, double _p3, String _action) {
        location = _location;
        action = _action;
        position = new double[]{_p1, _p2, _p3};
    }

    public String getLocation() {
        return location;
    }
    public String getAction() {
        return action;
    }
    public double[] getPosition() {
        return position;
    }
}
