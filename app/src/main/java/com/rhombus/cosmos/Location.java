package com.rhombus.cosmos;

/**
 * Created by Preetham on 5/21/2017.
 */
public class Location {
    private String location, action;
    private double[] position;

    public Location(String _location, double[] _p, String _action) {
        location = _location;
        action = _action;
        position = _p;
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
