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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class CalibrationActivity extends AppCompatActivity{

    private static final String TAG = "CalibrationActivity";
    private final int MY_PERMISSION_REQUEST_LOCATION = 1;

    double[][] beaconPositions = new double[][]{{0,0,0}, {0,0,0}, {0,0,0}};
    double[] beaconDistances = new double[]{0, 0, 0};

    DBHelper db;

    TextView distanceView1, distanceView2, distanceView3;
    TextView currentLocation;
    EditText locationName;

    List<Location> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);

        findViewById(R.id.calibrateButton).setOnClickListener(new View.OnClickListener() {
            private int calibrateState = 0;
            @Override
            public void onClick(View view) {
                switch(calibrateState) {
                    case 0: beaconPositions[0] = new double[]{0,0,0}; Toast.makeText(getApplicationContext(), "Calibrating beacon 1", Toast.LENGTH_SHORT).show(); break;
                    case 1: beaconPositions[1] = new double[]{beaconDistances[0]/Math.sqrt(2),beaconDistances[0]/Math.sqrt(2),0}; Toast.makeText(getApplicationContext(), "Calibrating beacon 2", Toast.LENGTH_SHORT).show(); break;
                    case 2: beaconPositions[2] = triangulateLocation(new double[][]{beaconPositions[0], beaconPositions[1]}, new double[]{beaconDistances[0], beaconDistances[1]}); Toast.makeText(getApplicationContext(), "Calibrating beacon 3", Toast.LENGTH_SHORT).show(); break;
                    default: Toast.makeText(getApplicationContext(), "Calibration finished.", Toast.LENGTH_SHORT).show(); break;
                }
                calibrateState++;
            }
        });

        findViewById(R.id.saveCalibration).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Saved Calibration: " + "{" + Arrays.toString(beaconPositions[0]) + ", " + Arrays.toString(beaconPositions[1]) + ", " + Arrays.toString(beaconPositions[2]) + "}");
                db.addCalibration(new Calibration(beaconPositions[0], beaconPositions[1], beaconPositions[2]));
                Toast.makeText(getApplicationContext(), "Saving calibration", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.saveLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double[] locPosition = triangulateLocation(beaconPositions, beaconDistances);
                String name = locationName.getText().toString();
                db.addLocation(new Location(name, locPosition, "TOAST"));

                Log.i(TAG, "Saved Location (" + name + "): " + Arrays.toString(locPosition));
                Toast.makeText(getApplicationContext(), "Saving Location" + name, Toast.LENGTH_SHORT).show();
            }
        });

        db = new DBHelper(getApplicationContext());
        beaconPositions = db.getCalibration();
        locations = db.getLocations();
        Log.i(TAG, "Initial Positions: " + "{" + Arrays.toString(beaconPositions[0]) + ", " + Arrays.toString(beaconPositions[1]) + ", " + Arrays.toString(beaconPositions[2]) + "}");

        distanceView1 = (TextView) findViewById(R.id.distanceView1);
        distanceView2 = (TextView) findViewById(R.id.distanceView2);
        distanceView3 = (TextView) findViewById(R.id.distanceView3);
        currentLocation = (TextView) findViewById(R.id.currentLocation);
        locationName = (EditText) findViewById(R.id.locationName);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
        }

        ((CosmosApplication)getApplication()).setCalibrationActivity(this);

        Log.i("testing", Arrays.toString(triangulateLocation(new double[][]{beaconPositions[0], beaconPositions[1]}, new double[]{beaconDistances[0], beaconDistances[1]})));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Yay
                } else {
                    Toast.makeText(getApplicationContext(), "You need to give permission for this application to work!", Toast.LENGTH_LONG).show();
                    // App doesn't run. Force quit?
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void updateDistance(final int id, final double distance) {
        Log.i(TAG, "ID: " + id + ", distance: " + distance);
        beaconDistances[id-1] = distance;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch(id) {
                    case 1: distanceView1.setText("BEACON 1: " + Double.toString(distance)); break;
                    case 2: distanceView2.setText("BEACON 2: " + Double.toString(distance)); break;
                    case 3: distanceView3.setText("BEACON 3: " + Double.toString(distance)); break;
                }
            }
        });
    }


    private double[] triangulateLocation(double[][] ps, double[] ds) {
        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(ps, ds), new LevenbergMarquardtOptimizer());
        LeastSquaresOptimizer.Optimum optimum = solver.solve();

        // the answer
        double[] centroid = optimum.getPoint().toArray();

        /*
        // error and geometry information; may throw SingularMatrixException depending the threshold argument provided
        RealVector standardDeviation = optimum.getSigma(0);
        RealMatrix covarianceMatrix = optimum.getCovariances(0);
        Log.d("Centroid", Double.toString(centroid[0]) + ", " + Double.toString(centroid[1]));
        Log.d("RealVector (std)", standardDeviation.toString());
        Log.d("RealMatrix (cov)", covarianceMatrix.toString());
        */

        return centroid;
    }
}
