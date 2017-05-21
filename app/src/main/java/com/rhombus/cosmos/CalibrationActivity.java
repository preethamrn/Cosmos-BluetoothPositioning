package com.rhombus.cosmos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class CalibrationActivity extends AppCompatActivity {

    double[][] positions = new double[][]{{0,0}, {3,4}};
    double[] distances = new double[]{6, 1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);

        findViewById(R.id.calibrateButton1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Clicked calibrate button 1", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.calibrateButton2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Clicked calibrate button 2", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.calibrateButton3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Clicked calibrate button 3", Toast.LENGTH_SHORT).show();
            }
        });

        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
        LeastSquaresOptimizer.Optimum optimum = solver.solve();

        // the answer
        double[] centroid = optimum.getPoint().toArray();

        // error and geometry information; may throw SingularMatrixException depending the threshold argument provided
        RealVector standardDeviation = optimum.getSigma(0);
        RealMatrix covarianceMatrix = optimum.getCovariances(0);
        Log.d("Centroid", Double.toString(centroid[0]) + ", " + Double.toString(centroid[1]));
        Log.d("RealVector (std)", standardDeviation.toString());
        Log.d("RealMatrix (cov)", covarianceMatrix.toString());
    }
}
