package com.rhombus.cosmos;

/**
 * Created by Preetham on 5/21/2017.
 */
public class Calibration {
    public double[][] calibration;

    public Calibration(double[] _c1, double[] _c2, double[] _c3) {
        calibration = new double[][]{_c1, _c2, _c3};
    }
}
