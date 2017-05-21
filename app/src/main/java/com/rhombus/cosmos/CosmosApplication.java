package com.rhombus.cosmos;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.radiusnetworks.proximity.KitConfig;
import com.radiusnetworks.proximity.ProximityKitBeacon;
import com.radiusnetworks.proximity.ProximityKitBeaconRegion;
import com.radiusnetworks.proximity.ProximityKitManager;
import com.radiusnetworks.proximity.ProximityKitMonitorNotifier;
import com.radiusnetworks.proximity.ProximityKitRangeNotifier;
import com.radiusnetworks.proximity.ProximityKitSyncNotifier;
import com.radiusnetworks.proximity.RegionEvent;

import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.service.RunningAverageRssiFilter;

import java.util.Collection;
import java.util.Properties;

/**
 * Created by Zhoub on 5/21/2017.
 */

public class CosmosApplication extends Application implements ProximityKitRangeNotifier, ProximityKitSyncNotifier, ProximityKitMonitorNotifier {

    /**
     * Singleton storage for an instance of the manager
     */
    private static ProximityKitManager pkManager = null;

    private CalibrationActivity calibrationActivity;

    private static final String TAG = "CosmosApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        

        Properties settings = new Properties();
        settings.setProperty(
                KitConfig.CONFIG_API_URL,
                "https://proximitykit.radiusnetworks.com/api/kits/9186"
        );
        settings.setProperty(
                KitConfig.CONFIG_API_TOKEN,
                "e05c737eec186d4cd78c791a698374798b51f4860e5a5bff0ffd86975f26f144"
        );
        pkManager = ProximityKitManager.getInstance(
                getApplicationContext(),
                new KitConfig(settings)
        );

        pkManager.setProximityKitRangeNotifier(this);
        pkManager.setProximityKitMonitorNotifier(this);
        pkManager.setProximityKitSyncNotifier(this);

        pkManager.getBeaconManager().setRssiFilterImplClass(RunningAverageRssiFilter.class);
        RunningAverageRssiFilter.setSampleExpirationMilliseconds(1000l);

        pkManager.start();

    }

    public void setCalibrationActivity(CalibrationActivity calibrationActivity) {
        this.calibrationActivity = calibrationActivity;
    }

    @Override
    public void didFailSync(@NonNull Exception e) {

    }

    @Override
    public void didSync() {

    }

    @Override
    public void didDetermineStateForRegion(@RegionEvent.RegionState int i, @NonNull ProximityKitBeaconRegion proximityKitBeaconRegion) {

    }

    @Override
    public void didEnterRegion(@NonNull ProximityKitBeaconRegion proximityKitBeaconRegion) {

    }

    @Override
    public void didExitRegion(@NonNull ProximityKitBeaconRegion proximityKitBeaconRegion) {

    }

    @Override
    public void didRangeBeaconsInRegion(@NonNull Collection<ProximityKitBeacon> beacons,
                                        @NonNull ProximityKitBeaconRegion region) {
        Log.d(
                TAG,
                "didRangeBeaconsInRegion: beacons=" + beacons + " region=" + region
        );
        if (beacons.size() == 0) {
            return;
        }

        Log.d(TAG, "didRangeBeaconsInRegion: size=" + beacons.size() + " region=" + region);

        for (ProximityKitBeacon beacon : beacons) {
            Log.d(
                    TAG,
                    "I have a beacon with data: " + beacon + " attributes=" +
                            beacon.getAttributes()
            );

            // We've wrapped up further behavior in some internal helper methods
            // Check their docs for details on additional things which you can do we beacon data
            getDistance(beacon);
        }
    }

    private void getDistance(ProximityKitBeacon beacon) {
        if (calibrationActivity == null || beacon == null) {
            return;
        }

        Identifier id3 = beacon.getId3();

        calibrationActivity.updateDistance(id3.toInt(), beacon.getDistance());
    }
}
