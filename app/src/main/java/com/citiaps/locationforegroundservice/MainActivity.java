package com.citiaps.locationforegroundservice;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/** Se solicita acceso a ACCESS_FINE_LOCATION **/
public class MainActivity extends FragmentActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private static final long UPDATE_INTERVAL = 5000; // Cada 5 segundos.
    private static final long FASTEST_UPDATE_INTERVAL = 5000; // Cada 3 segundos.
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL*2 ; // Cada 10 segundos.

    /** Parametros del Request para la API de localización **/
    private LocationRequest mLocationRequest;
    /** Acceso a la API de localización */
    private FusedLocationProviderClient mFusedLocationClient;

    // Acceso a elementos del Layout.
    private Button mRequestUpdatesButton;
    private Button mRemoveUpdatesButton;
    private TextView mOnOff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRequestUpdatesButton = (Button) findViewById(R.id.mRequestUpdatesButton);
        mRemoveUpdatesButton = (Button) findViewById(R.id.mRemoveUpdatesButton);
        mOnOff = (TextView) findViewById(R.id.textView_onOff);

        // Check if the user revoked runtime permissions.
        if (!checkPermissions()) {
            requestPermissions();
        }

        //Acceso a la API que provee localización (Fused Location Provider)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        createLocationRequest();
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateButtonsState(Utils.getRequestingLocationUpdates(this));
        //mLocationUpdatesResultView.setText(Utils.getLocationUpdatesResult(this));
    }

    @Override
    protected void onStop() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }


    /**  Utils  **/
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.activity_main),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Pidiendo Permisos...");
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    /** Genera la estructura de los Requests **/
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setMaxWaitTime(MAX_WAIT_TIME);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private PendingIntent getPendingIntent() {
        // TODO(developer): uncomment to use PendingIntent.getService().
//        Intent intent = new Intent(this, LocationUpdatesIntentService.class);
//        intent.setAction(LocationUpdatesIntentService.ACTION_PROCESS_UPDATES);
//        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent = new Intent(this, LocationUpdatesBroadcastReceiver.class);
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(Utils.KEY_LOCATION_UPDATES_RESULT)) {
            // TODO aqui cambia el texto de Localización de la actividad
            //mLocationUpdatesResultView.setText(Utils.getLocationUpdatesResult(this));
        } else if (s.equals(Utils.KEY_LOCATION_UPDATES_REQUESTED)) {
            updateButtonsState(Utils.getRequestingLocationUpdates(this));
        }
    }

    /** Maneja los botones de Requests Handles the Request Updates button and requests start of location updates. */
    public void requestLocationUpdates(View view) {
        try {
            Log.i(TAG, "Starting location updates");
            Utils.setRequestingLocationUpdates(this, true);
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, getPendingIntent());
        } catch (SecurityException e) {
            Utils.setRequestingLocationUpdates(this, false);
            e.printStackTrace();
        }
    }

    /** Mantiene un solo botón disponible
     */
    private void updateButtonsState(boolean requestingLocationUpdates) {
        if (requestingLocationUpdates) {
            mRequestUpdatesButton.setEnabled(false);
            mRemoveUpdatesButton.setEnabled(true);
        } else {
            mRequestUpdatesButton.setEnabled(true);
            mRemoveUpdatesButton.setEnabled(false);
        }
    }
}
