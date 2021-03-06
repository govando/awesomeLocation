package com.citiaps.locationservice;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import java.util.List;

/** Receptor para manejar updates de locations  **/

public class LocationUpdatesBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "LocUpdBroadcastReceiver";

    static final String ACTION_PROCESS_UPDATES =
            "com.citiaps.locationforegroundservice.action" +
                    ".PROCESS_UPDATES";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            Log.i(TAG, "---> Intent.getAction: "+action);
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    List<Location> locations = result.getLocations();
                    Utils.sendLocations(locations);
                }
            }
        }
    }

}
