package com.rhombus.cosmos;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class CalibrationActivity extends AppCompatActivity{

    private static final String TAG = "CalibrationActivity";
    private final int MY_PERMISSION_REQUEST_LOCATION = 1;

    double[][] beaconPositions = new double[][]{{0,0,0}, {0,0,0}, {0,0,0}};
    double[] beaconDistances = new double[]{0, 0, 0};

    DBHelper db;

    TextView distanceView1, distanceView2, distanceView3;
    TextView currentLocation, currentPositionTV;

    List<Location> locations;
    List<String> last20Locations = new LinkedList<>();

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
                locations.clear();
                db.addCalibration(new Calibration(beaconPositions[0], beaconPositions[1], beaconPositions[2]));
                Toast.makeText(getApplicationContext(), "Saving calibration", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.saveLocation).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               LayoutInflater inflater = getLayoutInflater();
               View alertLayout = inflater.inflate(R.layout.save_location_popup, null);
               final EditText etName = (EditText) alertLayout.findViewById(R.id.et_name);
               final Spinner spAction = (Spinner) alertLayout.findViewById(R.id.spinner_action);
               final LinearLayout notificationLayout = (LinearLayout) alertLayout.findViewById(R.id.notification);
               final LinearLayout linkLayout = (LinearLayout) alertLayout.findViewById(R.id.link);
               final LinearLayout appLayout = (LinearLayout) alertLayout.findViewById(R.id.app);
               final EditText etNotificationText = (EditText) alertLayout.findViewById(R.id.et_notification_text);
               final EditText etLinkURL = (EditText) alertLayout.findViewById(R.id.et_link_url);
               final Spinner spOpenApp = (Spinner) alertLayout.findViewById(R.id.spinner_open_app);

               final StringBuilder s = new StringBuilder();

               final PackageManager pm = getPackageManager();
                //get a list of installed apps.
               List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
               ArrayList<String> appNames = new ArrayList<String>();
               final Map<String, String> packagesNames = new HashMap<String, String>();
               ApplicationInfo ai;
               for (ApplicationInfo packageInfo : packages) {
                   try{
                       ai = pm.getApplicationInfo( packageInfo.packageName, 0);
                   } catch (final PackageManager.NameNotFoundException e) {
                       ai = null;
                   }

                   if (ai != null) {
                       Intent i = pm.getLaunchIntentForPackage(packageInfo.packageName);
                       if (i != null) {
                           appNames.add((String) pm.getApplicationLabel(ai));
                           packagesNames.put((String) pm.getApplicationLabel(ai), packageInfo.packageName);
                       }
                   }
                   //packagesNames.add(packageInfo.packageName);

//                   Log.d(TAG, "Installed package :" + packageInfo.packageName);
//                   Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
//                   Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
               }
               Collections.sort(appNames);

               ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(CalibrationActivity.this, android.R.layout.simple_spinner_item, appNames);
               spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
               spOpenApp.setAdapter(spinnerArrayAdapter);
                // the getLaunchIntentForPackage returns an intent that you can use with startActivity()

               spAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                   @Override
                   public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                       notificationLayout.setVisibility(View.GONE);
                       linkLayout.setVisibility(View.GONE);
                       appLayout.setVisibility(View.GONE);
                       switch(position) {
                           case 1:
                               notificationLayout.setVisibility(View.VISIBLE);
                               break;
                           case 2:
                               linkLayout.setVisibility(View.VISIBLE);
                               break;
                           case 3:
                               appLayout.setVisibility(View.VISIBLE);
                               break;
                       }
                   }

                   @Override
                   public void onNothingSelected(AdapterView<?> parent) {

                   }
               });

               AlertDialog.Builder alert = new AlertDialog.Builder(CalibrationActivity.this);
               alert.setTitle("Location");
               alert.setView(alertLayout);
               alert.setCancelable(true);
               alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
                   }
               });

               alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                        switch(spAction.getSelectedItem().toString()) {
                            case "Show a Notification":
                                s.append("NOTIFICATION");
                                s.append("\n");
                                s.append(etNotificationText.getText().toString());
                                break;
                            case "Open a Link":
                                s.append("LINK");
                                s.append("\n");
                                s.append(etLinkURL.getText().toString());
                                break;
                            case "Open an App":
                                s.append("APP");
                                s.append("\n");
                                s.append(packagesNames.get(spOpenApp.getSelectedItem().toString()));
                                break;
                        }

                       double[] locPosition = triangulateLocation(beaconPositions, beaconDistances);
                       String name = etName.getText().toString();
                       Location loc = new Location(name, locPosition, s.toString());
                       db.addLocation(loc);
                       locations.add(loc);
                       Log.i(TAG, "Saved Location (" + name + "): " + Arrays.toString(locPosition));
                       Toast.makeText(getApplicationContext(), "Saving Location \"" + name + "\"", Toast.LENGTH_SHORT).show();
                   }
               });

               AlertDialog dialog = alert.create();
               dialog.show();
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
        currentPositionTV = (TextView) findViewById(R.id.currentPosition);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
        }

        ((CosmosApplication)getApplication()).setCalibrationActivity(this);

        Log.i("testing", Arrays.toString(triangulateLocation(new double[][]{beaconPositions[0], beaconPositions[1]}, new double[]{beaconDistances[0], beaconDistances[1]})));


        final Handler h = new Handler();
        final int delay = 1000;

        h.postDelayed(new Runnable(){
            public void run() {
                final double[] currentPosition = triangulateLocation(beaconPositions, beaconDistances);
                double minimum = Double.MAX_VALUE;
                Location minloc = null;
                for (Location location : locations) {
                    double distancesq = distanceSquared(currentPosition, location.getPosition());
                    if (distancesq < minimum) {
                        minloc = location;
                        minimum = distancesq;
                    }
                }
                double distanceLimit = 0.5;
                if (minloc != null) {
                    if (minimum < distanceLimit * distanceLimit) {
                        boolean alreadyActioned = false;
                        for (String locString : last20Locations) {
                            if (locString == minloc.getLocation()) {
                                alreadyActioned = true;
                                break;
                            }
                        }
                        if (!alreadyActioned) {
                            performAction(minloc);
                        }
                        last20Locations.add(minloc.getLocation());
                        if(last20Locations.size() > 10) {
                            last20Locations.remove(0);
                        }
                    } else {
                        last20Locations.add("NULL Location");
                        if(last20Locations.size() > 10) {
                            last20Locations.remove(0);
                        }
                    }
                }
                final String locationDisplayString;
                if (minloc != null && minimum < distanceLimit * distanceLimit) {
                    locationDisplayString = "Current Location: " + minloc.getLocation();
                } else {
                    locationDisplayString = "Current Location: null";
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentPositionTV.setText("Current Position: " + Arrays.toString(currentPosition));
                        currentLocation.setText(locationDisplayString);
                    }
                });
                h.postDelayed(this, delay);
            }
        }, delay);
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

    private void performAction(Location minloc) {
        String action = minloc.getAction();
        String parts[] = action.split("\n");
        if("NOTIFICATION".equals(parts[0].toUpperCase())) {
            NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.notification_icon)
                            .setContentTitle(minloc.getLocation())
                            .setContentText(parts[1]);
            int mNotificationId = 1;
            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        } else if("LINK".equals(parts[0].toUpperCase())) {
            String link = parts[1];
            if (!"http://".equals(link.substring(0, 7)) && !"https://".equals(link.substring(0, 8))) {
                link = "http://" + link;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(link));
            startActivity(intent);
        } else if("APP".equals(parts[0].toUpperCase())) {
            Intent intent = getPackageManager().getLaunchIntentForPackage(parts[1]);
            if (intent != null) {
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Null Intent: " + parts[1], Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Location: " + minloc.getLocation(), Toast.LENGTH_LONG).show();
        }
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

    private double distanceSquared(double[] l1, double[] l2) {
        double c1 = l1[0] - l2[0];
        double c2 = l1[1] - l2[1];
        double c3 = l1[2] - l2[2];
        return (c1 * c1) + (c2 * c2) + (c3 * c3);
    }
}
