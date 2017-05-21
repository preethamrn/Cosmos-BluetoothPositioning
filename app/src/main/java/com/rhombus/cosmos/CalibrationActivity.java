package com.rhombus.cosmos;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class CalibrationActivity extends AppCompatActivity{

    private static final String TAG = "CalibrationActivity";
    private final int MY_PERMISSION_REQUEST_LOCATION = 1;

    double[][] positions = new double[][]{{0,0}, {2,2}, {3,3}};
    double[] distances = new double[]{4.2426, 1.4142, 0};

    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);

        findViewById(R.id.calibrateButton).setOnClickListener(new View.OnClickListener() {
            private int calibrateState = 0;
            @Override
            public void onClick(View view) {
                switch(calibrateState) {
                    case 0: positions[0] = new double[]{0,0}; Toast.makeText(getApplicationContext(), "Calibrating beacon 1", Toast.LENGTH_SHORT).show(); break;
                    case 1: positions[1] = new double[]{2,2}; Toast.makeText(getApplicationContext(), "Calibrating beacon 2", Toast.LENGTH_SHORT).show(); break;
                    case 2: positions[2] = new double[]{3,3}; Toast.makeText(getApplicationContext(), "Calibrating beacon 3", Toast.LENGTH_SHORT).show(); break;
                    default: Toast.makeText(getApplicationContext(), "Calibration finished.", Toast.LENGTH_SHORT).show(); break;
                }
                calibrateState++;
            }
        });

        findViewById(R.id.saveCalibration).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.addCalibration(new Calibration(positions[0], positions[1], positions[2]));
                Toast.makeText(getApplicationContext(), "Saving calibration", Toast.LENGTH_SHORT).show();
            }
        });

        db = new DBHelper(getApplicationContext());

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
        }

        ((CosmosApplication)getApplication()).setCalibrationActivity(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Yay

                } else {
                    // App doesn't run. Force quit?
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void updateDistance(int id, double distance) {
        Log.i(TAG, "ID: " + id + ", distance: " + distance);
}
}
